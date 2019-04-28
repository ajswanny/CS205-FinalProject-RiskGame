package risk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import risk.controller.*;
import risk.java.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Game extends Application {

    /* Class Fields */
    public static final int MAIN_MENU = 0;
    private static final int GAME = 1;
    public static final int ABOUT_GAME = 2;
    public static final int PAUSE_GAME_MENU = 3;
    public static final int GAME_SETUP = 4;
    private final int GAME_END = 5;
    public static final int HELP_GAME = 6;

    /** The color themes for each continent and those that are available to the user for selection. */
    public final String NORTH_AMERICA_HEX = "B78740";
    public final String SOUTH_AMERICA_HEX = "994734";
    public final String AFRICA_HEX = "876133";
    public final String ASIA_HEX = "5E693D";
    public final String AUSTRALIA_HEX = "8B626A";

    public enum PlayerColor {
        NA_YELLOW,
        SA_RED,
        EU_GRAY,
        AF_BROWN,
        AS_GREEN,
        AU_VIOLET
    }

    public enum TurnPhase {
        DRAFT,
        ATTACK,
        FORTIFY
    }

    public static final int ARMIES_TO_DRAFT= 5;

    public TurnPhase playerTurnPhase;

    /** Primary Stage of the Application */
    private Stage primaryStage, gamePauseMenuStage, gameEndStage;

    /** The Game's Scenes */
    private Scene mainMenuScene, gameScene, aboutGameScene, helpGameScene, gamePauseMenuScene, gameSetupScene, gameEndScene;

    /** Controller for the Game Scene */
    private GameSceneController gameSceneController;

    private GameEndSceneController gameEndSceneController;

    /** Collection of Territories referenced by their name. */
    public HashMap<String, Territory> territories;

    public Player player;

    public CPU cpu;

    private Territory cpuConqueredTerritory;

    private Dice playerDice, cpuDice;

    public GameState defaultLoadableGameState;

    private GameState gameState;

    private static Game instance;

    public Game() {
        instance = this;
    }

    /* Methods */
    /**
     * Starts the Game application.
     */
    @Override
    public void start(Stage primaryStage) {

        try {

            // Load data.
            defineTerritories();

            // Init dice
            playerDice = new Dice();
            cpuDice = new Dice();

            // Load and initialize all FXML.
            loadFxmlSources();

            // Initialize the alternate Stages.
            gamePauseMenuStage = new Stage(StageStyle.UNIFIED);
            gamePauseMenuStage.initModality(Modality.APPLICATION_MODAL);
            gamePauseMenuStage.setScene(gamePauseMenuScene);

            gameEndStage = new Stage(StageStyle.TRANSPARENT);
            gameEndStage.initModality(Modality.APPLICATION_MODAL);
            gameEndStage.setScene(gameEndScene);

            // Initialize the Application and its default Scene.
            primaryStage.setTitle("Risk");
            primaryStage.setScene(mainMenuScene);
            this.primaryStage = primaryStage;
            primaryStage.show();

        } catch (Exception e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
        }

        // Debugging access.
        debug();

    }

    @Override
    public void stop() {
        System.out.println("Shutting down Game instance: " + this + ".");
        System.exit(0);
    }

    /** Used for debugging. */
    private void debug() {

    }

    /**
     * Validates and completes a request to begin the game.
     * Game states:
     *  0 = there is a state to be loaded-in.
     *  1 = the player is creating a new game.
     */
    public void requestStartOfGame(boolean isNewGame, String playerSelectedColor) {
        if (isNewGame) {

            // Define new Game-state.
            player = new Player(PlayerColor.valueOf(playerSelectedColor));
            cpu = new CPU();

            // Randomly distribute controlled territories between 'player' and 'cpu'.
            Random random = new Random();
            int n;
            for (Territory territory : territories.values()) {
                n = random.nextInt(50);
                if (n % 2 == 0) {
                    player.addNewControlledTerritory(territory);
                    territory.setOwner(player);
                } else {
                    cpu.addNewControlledTerritory(territory);
                    territory.setOwner(cpu);
                }
                territory.setNumOfArmies(3 - n%2 + n%3);
            }
            this.gameState = new GameState(player, cpu);

            // Update the GUI with the newly defined GameState.
            gameSceneController.setGameState(gameState);
            game();

        } else {

            // Load saved game for continuation.
            defaultLoadableGameState = deserializeDefaultLoadableGameState();
            game();

        }
    }

    /** Controls the game. */
    private void game() {
        requestDisplayForScene(GAME);
        gameSceneController.setPlayerTurnIndicatorColor(player.getColor());
        playerTurn(TurnPhase.DRAFT);
    }

    /**
     * Three phases:
     *  Draft (1) - Place armies granted at the beginning of each turn;
     *  Attack (2) - Make attacks to enemy armies;
     *  Fortify (3) - Move armies to friendly territories.
     */
    private void playerTurn(TurnPhase phase) {
        switch (phase) {
            case DRAFT:
                playerTurnPhase = TurnPhase.DRAFT;
                gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.DRAFT);
                gameSceneController.setupBoardForNewPlayerTurn();
                break;
            case ATTACK:
                playerTurnPhase = TurnPhase.ATTACK;
                gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.ATTACK);
                break;
            case FORTIFY:
                playerTurnPhase = TurnPhase.FORTIFY;
                gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.FORTIFY);
                break;
        }
    }

    private void cpuTurn() {

        gameSceneController.setupBoardForNewCpuTurn();

        try {

            // Draft
            TimeUnit.SECONDS.sleep(1);
            gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.DRAFT);
            Territory territoryToDraftArmiesTo = cpu.draftArmies();
            territoryToDraftArmiesTo.addArmies(ARMIES_TO_DRAFT);
            gameSceneController.resetAmountOfArmiesForTerritory(territoryToDraftArmiesTo);

            // Attack
            TimeUnit.SECONDS.sleep(1);
            gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.ATTACK);
            // While CPU can continue to do battles
            int numOfAttacks = 0;
            playerDice.roll();
            cpuDice.roll();
            int NUM_OF_CPU_ATTACKS_ROOF = 20;
            while (cpuConqueredTerritory == null && numOfAttacks < NUM_OF_CPU_ATTACKS_ROOF) {
                cpuConqueredTerritory = cpu.CpuAttack(cpuDice.getTotal(), playerDice.getTotal());
                if (cpuConqueredTerritory != null) {
                    cpuConqueredTerritory.setOwner(cpu);
                    gameSceneController.updateTerritoryOwner(cpuConqueredTerritory.getName(), cpu);
                }
                gameSceneController.resetAmountOfArmiesForTerritories();
                numOfAttacks++;
            }
            checkForVictory();
            cpuConqueredTerritory = null;

            // Fortify
            TimeUnit.SECONDS.sleep(1);
            gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.FORTIFY);
            boolean cpuDidFortifyATerritory = cpu.fortifyTerritories();
            if (cpuDidFortifyATerritory) gameSceneController.resetAmountOfArmiesForTerritories();

            // End turn
            playerTurn(TurnPhase.DRAFT);

        } catch (InterruptedException e) {
            stop();
        }

    }

    /** Midpoint for all turn-phase transitions. */
    public void flagEndOfTurnPhase(Player player, TurnPhase turnPhase) {
        if (player == this.player) {
            switch (turnPhase) {
                case DRAFT:
                    playerTurn(TurnPhase.ATTACK);
                    break;
                case ATTACK:
                    playerTurn(TurnPhase.FORTIFY);
                    break;
                case FORTIFY:
                    cpuTurn();
                    break;
            }
        } else {
            playerTurn(TurnPhase.DRAFT);
        }
    }

    private void checkForVictory() {
        gameEndSceneController.setVictor(player);
        requestDisplayForScene(GAME_END);
//        if (player.getControlledTerritories().size() == 0) {
//            gameEndSceneController.setVictor(player);
//            requestDisplayForScene(GAME_END);
//        } else if (cpu.getControlledTerritories().size() == 0) {
//            gameEndSceneController.setVictor(cpu);
//            requestDisplayForScene(GAME_END);
//        }
    }

    /** Loads FXML data for access to FXMLControllers. */
    private void loadFxmlSources() throws Exception {

        // Loader for MenuSceneController
        MainMenuSceneController mainMenuSceneController = (MainMenuSceneController) loadFxmlController("fxml/MainMenuSceneController.fxml");
        mainMenuScene = mainMenuSceneController.getPrimaryScene();

        // Loader for GameSceneController
        gameSceneController = (GameSceneController) loadFxmlController("fxml/GameSceneController.fxml");
        gameScene = gameSceneController.getPrimaryScene();

        // Loader for AboutGameSceneController
        AboutGameSceneController aboutGameSceneController = (AboutGameSceneController) loadFxmlController("fxml/AboutGameSceneController.fxml");
        aboutGameScene = aboutGameSceneController.getPrimaryScene();

        // Loader for AboutGameSceneController
        HelpGameSceneController helpGameSceneController = (HelpGameSceneController) loadFxmlController("fxml/HelpGameSceneController.fxml");
        helpGameScene = helpGameSceneController.getPrimaryScene();

        // Loader for GamePauseMenuSceneController
        GamePauseMenuSceneController gamePauseMenuSceneController = (GamePauseMenuSceneController) loadFxmlController("fxml/GamePauseMenuScene.fxml");
        gamePauseMenuScene = gamePauseMenuSceneController.getPrimaryScene();

        // GameSetupSceneController
        GameSetupSceneController gameSetupSceneController = (GameSetupSceneController) loadFxmlController("fxml/GameSetupSceneController.fxml");
        gameSetupScene = gameSetupSceneController.getPrimaryScene();

        // GameEnd
        gameEndSceneController = (GameEndSceneController) loadFxmlController("fxml/GameEndSceneController.fxml");
        gameEndScene = gameEndSceneController.getPrimaryScene();

    }

    /** Shortcut for loading a FXML Controller class given its FXML file. */
    private RiskSceneController loadFxmlController(String controllerFxmlFilePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(controllerFxmlFilePath));
        fxmlLoader.load();
        return fxmlLoader.getController();
    }

    /** High-level method to organize creation of Territories and definition of their neighbors. */
    private void defineTerritories() {

        // Input Territory info and map these to objects.
        territories = new HashMap<>(42);
        String TERRITORY_NAMES_FP = "resources/territoriesInfo.txt";
        InputStream stream = getClass().getClassLoader().getResourceAsStream(TERRITORY_NAMES_FP);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {

                // Read name and continent-numerical-id for Territory.
                String[] val = line.split(",");
                territories.put(val[0], new Territory(val[0]));

            }
        } catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "File territoriesInfo.txt returned null", e);
        }

        defineTerritoryRelationships();

    }

    /** High-level method to organize definition of Territory neighbors. */
    private void defineTerritoryRelationships() {

        // North america
        setTerritoryNeighbors("alaska", "northwestTerritory", "alberta", "kamchatka");
        setTerritoryNeighbors("northwestTerritory", "alaska", "alberta", "ontario", "greenland");
        setTerritoryNeighbors("greenland", "northwestTerritory", "ontario", "quebec", "iceland");
        setTerritoryNeighbors("alberta", "alaska","northwestTerritory", "ontario", "westernUnitedStates");
        setTerritoryNeighbors("ontario", "alberta", "northwestTerritory", "greenland", "quebec", "westernUnitedStates", "easternUnitedStates");
        setTerritoryNeighbors("quebec", "ontario", "greenland", "easternUnitedStates");
        setTerritoryNeighbors("westernUnitedStates", "alberta", "ontario", "easternUnitedStates", "centralAmerica");
        setTerritoryNeighbors("easternUnitedStates", "westernUnitedStates", "ontario", "quebec", "centralAmerica");
        setTerritoryNeighbors("centralAmerica", "westernUnitedStates", "easternUnitedStates", "venezuela");

        // South america
        setTerritoryNeighbors("venezuela", "centralAmerica", "peru", "brazil");
        setTerritoryNeighbors("peru", "venezuela", "brazil", "argentina");
        setTerritoryNeighbors("brazil", "venezuela", "peru", "argentina", "northAfrica");
        setTerritoryNeighbors("argentina", "peru", "brazil");

        // Africa
        setTerritoryNeighbors("northAfrica", "westernEurope", "southernEurope", "egypt", "eastAfrica", "congo");
        setTerritoryNeighbors("egypt", "southernEurope", "middleEast", "eastAfrica", "northAfrica");
        setTerritoryNeighbors("eastAfrica", "northAfrica", "egypt", "congo", "southAfrica", "madagascar");
        setTerritoryNeighbors("congo", "northAfrica", "eastAfrica", "southAfrica");
        setTerritoryNeighbors("southAfrica", "congo", "eastAfrica", "madagascar");

        // Europe
        setTerritoryNeighbors("iceland", "greenland", "greatBritain", "scandinavia");
        setTerritoryNeighbors("scandinavia", "iceland", "greatBritain", "northernEurope", "ukraine");
        setTerritoryNeighbors("ukraine", "scandinavia", "northernEurope", "southernEurope", "middleEast", "afghanistan", "ural");
        setTerritoryNeighbors("greatBritain", "iceland", "scandinavia", "northernEurope", "westernEurope");
        setTerritoryNeighbors("northernEurope", "scandinavia", "greatBritain", "westernEurope", "southernEurope", "ukraine");
        setTerritoryNeighbors("westernEurope", "greatBritain", "northAfrica", "southernEurope", "northernEurope");
        setTerritoryNeighbors("southernEurope", "northernEurope", "westernEurope", "northAfrica", "egypt", "middleEast", "ukraine");

        // Asia
        setTerritoryNeighbors("ural", "ukraine", "afghanistan", "china", "siberia");
        setTerritoryNeighbors("siberia", "ural", "china", "mongolia", "irkutsk", "yakutsk");
        setTerritoryNeighbors("yakutsk", "siberia", "irkutsk", "kamchatka");
        setTerritoryNeighbors("kamchatka", "yakutsk", "irkutsk", "mongolia", "japan", "alaska");
        setTerritoryNeighbors("irkutsk", "yakutsk", "siberia", "mongolia", "kamchatka");
        setTerritoryNeighbors("mongolia", "irkutsk", "siberia", "china", "japan", "kamchatka");
        setTerritoryNeighbors("afghanistan", "ural", "ukraine", "middleEast", "india", "china");
        setTerritoryNeighbors("china", "mongolia", "siberia", "ural", "afghanistan", "india", "siam");
        setTerritoryNeighbors("middleEast", "ukraine", "southernEurope", "egypt", "india", "afghanistan");
        setTerritoryNeighbors("india", "china", "afghanistan", "middleEast", "siam");
        setTerritoryNeighbors("siam", "china", "india", "indonesia");

        // Australia
        setTerritoryNeighbors("indonesia", "siam", "westernAustralia");
        setTerritoryNeighbors("newGuinea", "indonesia", "westernAustralia", "easternAustralia");
        setTerritoryNeighbors("westernAustralia", "indonesia", "easternAustralia", "newGuinea");
        setTerritoryNeighbors("easternAustralia", "newGuinea", "westernAustralia");

    }

    /** Sets the neighbors of the Territory in the first parameter as those given in the following Strings. */
    private void setTerritoryNeighbors(String territoryName, String... neighborNames) {
        Territory territory = territories.get(territoryName);
        ArrayList<Territory> ts = new ArrayList<>();
        for (String neighbor: neighborNames) {
            ts.add(territories.get(neighbor));
        }
        territory.setNeighbors(ts);
    }

    /** Tells Game that it has been requested to change the Scene (or bring up a new Stage). */
    public void requestDisplayForScene(int scene) {
        switch (scene) {
            case GAME:
                primaryStage.setScene(gameScene);
                primaryStage.centerOnScreen();
                break;
            case ABOUT_GAME:
                primaryStage.setScene(aboutGameScene);
                break;
            case HELP_GAME:
                primaryStage.setScene(helpGameScene);
                break;
            case PAUSE_GAME_MENU:
                gamePauseMenuStage.show();
                break;
            case GAME_SETUP:
                primaryStage.setScene(gameSetupScene);
                break;
            case GAME_END:
                gameEndStage.show();
                break;
            default:
                primaryStage.setScene(mainMenuScene);
                primaryStage.centerOnScreen();
                break;
        }
    }

    /**
     * De-serializes an object at the specified file location.
     * @return The de-serialized object.
     */
    private GameState deserializeDefaultLoadableGameState() {
        try {
            // Create a file input object to open the file specified by 'file_path'.
            FileInputStream file_in_stream = new FileInputStream("resources/serializations/defaultLoadableGameState.ser");

            // Define the object deserializer.
            ObjectInputStream object_in_stream = new ObjectInputStream(file_in_stream);

            // Return the de-serialized object.
            return (GameState) (object_in_stream.readObject());

        } catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "defaultLoadableGameState resource file not found.", e);
            return null;
        } catch (ClassNotFoundException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public void performPlayerAttack(Territory attackOrigin, Territory attackTarget) {
        playerDice.roll();
        cpuDice.roll();
        boolean didConquerTarget = attackOrigin.attack(attackTarget, playerDice.getTotal(), cpuDice.getTotal());

        if (didConquerTarget) {
            attackTarget.setOwner(player);
            cpu.removeControlledTerritory(attackTarget);
            player.addNewControlledTerritory(attackTarget);
            gameSceneController.updateTerritoryOwner(attackTarget.getName(), player);
        }

        checkForVictory();
    }

    /* Getters */
    public static Game getInstance() {
        return instance;
    }

    public boolean playerControlsTerritory(Territory territory) {
        return player.getControlledTerritories().contains(territory);
    }

    public boolean cpuControlsTerritory(Territory territory) {
        return cpu.getControlledTerritories().contains(territory);
    }

    /** Used to request closing of a Stage and focus the primary Stage */
    public void closeGamePauseMenuStage() {
        gamePauseMenuStage.close();
    }

    public void closeGameVictoryDialogue() {
        gameEndStage.close();
    }

    public void setNumOfArmiesForTerritory(Territory territory, int numOfArmies) {
        territories.get(territory.getName()).setNumOfArmies(numOfArmies);
    }

    /* Main */
    public static void main(String[] args) {
        launch(args);
    }


}
