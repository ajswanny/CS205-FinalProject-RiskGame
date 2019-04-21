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
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings({"FieldCanBeLocal"})
public class Game extends Application {

    /**
     * TODO:
     *      Fix 'setNewAmountOfTerritoryArmies' within GameSceneController
     */

    /* Class Fields */
    public static final int MAIN_MENU = 0;
    private static final int GAME = 1;
    public static final int ABOUT_GAME = 2;
    public static final int PAUSE_GAME_MENU = 3;
    public static final int GAME_SETUP = 4;

    /** Path to the text-file containing all Territory names. */
    private final String TERRITORY_NAMES_FP = "resources/territoriesInfo.txt";

    /** The color themes for each continent and those that are available to the user for selection. */
    @SuppressWarnings("unused")
    public final String
        NORTH_AMERICA_HEX = "B78740",
        SOUTH_AMERICA_HEX = "994734",
        EUROPE_HEX = "6A6F6B",
        AFRICA_HEX = "876133",
        ASIA_HEX = "5E693D",
        AUSTRALIA_HEX = "8B626A"
    ;

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
        FORTIFY,
        CPU
    }

    private final int STARTING_NUM_OF_ARMIES = 40;

    public static final int ARMIES_TO_DRAFT= 5;

    public TurnPhase playerTurnPhase;

    /** Primary Stage of the Application */
    private Stage primaryStage, gamePauseMenuStage;

    /** The Game's Scenes */
    private Scene mainMenuScene, gameScene, aboutGameScene, gamePauseMenuScene, gameSetupScene;

    /** Controller for the Menu Scene */
    private MainMenuSceneController mainMenuSceneController;

    /** Controller for the Game Scene */
    private GameSceneController gameSceneController;

    /** Controller for the AboutGame Scene */
    private AboutGameSceneController aboutGameSceneController;

    /** Controller for the GamePauseMenu Scene */
    private GamePauseMenuSceneController gamePauseMenuSceneController;

    /** Controller for the GameSetup Scene */
    private GameSetupSceneController gameSetupSceneController;

    /** Collection of Territories referenced by their name. */
    public HashMap<String, Territory> territories;

    public String originTerritoryName;

    public String targetTerritoryName;

    public Player player;

    public CPU cpu;

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

            // Initialize the alternate Stage.
            gamePauseMenuStage = new Stage(StageStyle.UNIFIED);
            gamePauseMenuStage.initModality(Modality.APPLICATION_MODAL);
            gamePauseMenuStage.setScene(gamePauseMenuScene);

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

        requestStartOfGame(true, "SA_RED");


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
            player = new Player(PlayerColor.valueOf(playerSelectedColor), STARTING_NUM_OF_ARMIES);
            cpu = new CPU(STARTING_NUM_OF_ARMIES);

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
                territory.setNumOfArmies(4-n%2);
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

        gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.CPU);

    }

    public void flagEndOfPlayerDraftPhase() {
        playerTurn(TurnPhase.ATTACK);
    }

    public void flagEndOfPlayerAttackPhase() {
        playerTurn(TurnPhase.FORTIFY);
    }

    public void flagEndOfPlayerFortifyPhase() {
        cpuTurn();
    }

    /** Loads FXML data for access to FXMLControllers. */
    private void loadFxmlSources() throws Exception {

        // Loader for MenuSceneController
        mainMenuSceneController = (MainMenuSceneController) loadFxmlController("fxml/MainMenuSceneController.fxml");
        mainMenuScene = mainMenuSceneController.getPrimaryScene();

        // Loader for GameSceneController
        gameSceneController = (GameSceneController) loadFxmlController("fxml/GameSceneController.fxml");
        gameScene = gameSceneController.getPrimaryScene();

        // Loader for AboutGameSceneController
        aboutGameSceneController = (AboutGameSceneController) loadFxmlController("fxml/AboutGameSceneController.fxml");
        aboutGameScene = aboutGameSceneController.getPrimaryScene();

        // Loader for GamePauseMenuSceneController
        gamePauseMenuSceneController = (GamePauseMenuSceneController) loadFxmlController("fxml/GamePauseMenuScene.fxml");
        gamePauseMenuScene = gamePauseMenuSceneController.getPrimaryScene();

        // GameSetupSceneController
        gameSetupSceneController = (GameSetupSceneController) loadFxmlController("fxml/GameSetupSceneController.fxml");
        gameSetupScene = gameSetupSceneController.getPrimaryScene();

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
        InputStream stream = getClass().getClassLoader().getResourceAsStream(TERRITORY_NAMES_FP);
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {

                // Read name and continent-numerical-id for Territory.
                String[] vals = line.split(",");
                territories.put(vals[0], new Territory(vals[0], Integer.valueOf(vals[1])));

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

            case PAUSE_GAME_MENU:
                gamePauseMenuStage.show();
                break;

            case GAME_SETUP:
                primaryStage.setScene(gameSetupScene);
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


    /* Setters */
    public void setGameIsRunning(boolean gameIsRunning) {
    }

    public void setNumOfArmiesForTerritory(Territory territory, int numOfArmies) {
        territories.get(territory.getName()).setNumOfArmies(numOfArmies);
    }

    public void decrementNumOfArmiesForTerritory(Territory territory) {
        territory.setNumOfArmies(territory.getNumOfArmies() - 1);
    }

    /* Main */
    public static void main(String[] args) {
        launch(args);
    }


}
