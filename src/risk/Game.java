package risk;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import risk.controller.*;
import risk.java.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Game extends Application {

    /**
     * TODO: Player attack bug
     */

    /* Class Fields */
    public static final int MAIN_MENU = 0;
    public static final int GAME = 1;
    public static final int ABOUT_GAME = 2;
    public static final int PAUSE_GAME_MENU = 3;
    public static final int GAME_SETUP = 4;
    private final int GAME_END = 5;

    /** The color themes for each continent and those that are available to the user for selection. */
    public final String NORTH_AMERICA_HEX = "B78740";
    public final String SOUTH_AMERICA_HEX = "994734";
    public final String AFRICA_HEX = "876133";
    public final String ASIA_HEX = "5E693D";
    public final String AUSTRALIA_HEX = "8B626A";

    private final String SAVED_GAME_STATE_FP = "src/resources/serializations/savedGameState.ser";

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
    private Scene mainMenuScene, gameScene, aboutGameScene, gamePauseMenuScene, gameSetupScene, gameEndScene;

    /** Controller for the Game Scene */
    private GameSceneController gameSceneController;

    private GameSetupSceneController gameSetupSceneController;

    private GameEndSceneController gameEndSceneController;

    /** Collection of Territories referenced by their name. */
    public HashMap<String, Territory> territories;

    public Player player;

    public CPU cpu;

    private Territory cpuConqueredTerritory;

    private Dice playerDice, cpuDice;

    private Thread gameloop;

    public GameState savedGameState;

    private GameState gameState;

    private final Object turnLock = new Object();

    private boolean gameIsRunning;

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
            savedGameState = deserializeSavedGameState();

            // Init dice
            playerDice = new Dice();
            cpuDice = new Dice();

            // Load and initialize all FXML.
            loadFxmlSources();

            // Initialize the alternate Stages.
            gamePauseMenuStage = new Stage(StageStyle.TRANSPARENT);
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

            // Debugging access.
            debug();

        } catch (Exception e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @Override
    public void stop() {
        System.out.println("Shutting down Game instance: " + this + ".");
        serializeSavedGameState();
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
            initializeTerritories();

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

        } else {

            loadSavedGame();

        }

        // Update the GUI with the newly defined GameState and start the game-loop.
        gameSceneController.setGameState(gameState);
        game();

    }

    /** Controls the game. */
    private synchronized void game() {

        // Prepare GUI for game-loop.
        requestDisplayForScene(GAME);
        gameSceneController.setPlayerTurnIndicatorColor(player.getColor());

        // Define and run the game-loop Thread.
        gameIsRunning = true;
        gameloop = new Thread(() -> {
            synchronized (turnLock) {
                try {
                    while (gameIsRunning) {

                        // Start player turn and wait for notification of its termination
                        playerTurn(TurnPhase.DRAFT);
                        turnLock.wait();

                        // Start CPU turn and wait for notification of its termination
                        cpuTurn();
                        turnLock.wait();

                    }
                } catch (InterruptedException e) {
                    System.out.println("Game-loop interrupted.");
                } finally {
                    System.out.println("Game-loop complete.");
                }
            }
        });
        gameloop.setDaemon(true);
        gameloop.start();

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
                Platform.runLater(() -> gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.DRAFT));
                Platform.runLater(() -> gameSceneController.setupBoardForNewPlayerTurn());
                break;
            case ATTACK:
                playerTurnPhase = TurnPhase.ATTACK;
                Platform.runLater(() -> gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.ATTACK));
                break;
            case FORTIFY:
                playerTurnPhase = TurnPhase.FORTIFY;
                Platform.runLater(() -> gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.FORTIFY));
                break;
        }
    }

    /**
     * Creates a new Thread for all CPU-turn actions. Once the Thread finishes execution, notifies 'turnLock' to move
     * onto the next Player turn.
     */
    private void cpuTurn() {

        Task<Void> cpuTurn = new Task<Void>() {
            @Override
            protected Void call() {

                try {

                    Platform.runLater(() -> gameSceneController.setupBoardForNewCpuTurn());

                    // Draft
                    Platform.runLater(() -> gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.DRAFT));
                    Thread.sleep(5000);
                    Territory territoryToDraftArmiesTo = cpu.draftArmies();
                    territoryToDraftArmiesTo.addArmies(ARMIES_TO_DRAFT);
                    Platform.runLater(() -> gameSceneController.resetAmountOfArmiesForTerritory(territoryToDraftArmiesTo));

                    // Attack
                    Platform.runLater(() -> gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.ATTACK));
                    Thread.sleep(5000);
                    int numOfAttacks = 0;
                    playerDice.roll();
                    cpuDice.roll();
                    int NUM_OF_CPU_ATTACKS_ROOF = 20;
                    while (numOfAttacks < NUM_OF_CPU_ATTACKS_ROOF) {

                        // Perform attack and record conquered Territory if the event ocurred
                        cpuConqueredTerritory = cpu.CpuAttack(cpuDice.getTotal(), playerDice.getTotal());
                        if (cpuConqueredTerritory != null) {

                            // Set the Territory's new owner
                            transferTerritoryOwnership(cpu, player, cpuConqueredTerritory);
                            Platform.runLater(() -> gameSceneController.updateTerritoryOwner(cpuConqueredTerritory.getName(), cpu));

                        }

                        Platform.runLater(() -> gameSceneController.resetAmountOfArmiesForTerritories());
                        Thread.sleep(1000);
                        numOfAttacks++;

                    }
                    checkForVictory();
                    cpuConqueredTerritory = null;

                    // Fortify
                    Platform.runLater(() -> gameSceneController.setHighlightForAttackPhaseIndicator(TurnPhase.FORTIFY));
                    Thread.sleep(5000);
                    Platform.runLater(() -> gameSceneController.resetAmountOfArmiesForTerritories());
                    CPUFortification cpuFortifyResults = cpu.fortifyTerritories();
                    if (cpuFortifyResults != null) {

                        for (int i = 0; i < cpuFortifyResults.delta; i++) {
                            cpuFortifyResults.unfortified.setNumOfArmies(cpuFortifyResults.unfortified.getNumOfArmies() - i);
                            Platform.runLater(() -> gameSceneController.resetAmountOfArmiesForTerritory(cpuFortifyResults.unfortified));
                            Thread.sleep(1000);
                        }

                        for (int i = 0; i < cpuFortifyResults.delta; i++) {
                            cpuFortifyResults.fortified.setNumOfArmies(cpuFortifyResults.fortified.getNumOfArmies() + i);
                            Platform.runLater(() -> gameSceneController.resetAmountOfArmiesForTerritory(cpuFortifyResults.fortified));
                            Thread.sleep(1000);
                        }

                    }

                    Platform.runLater(() -> gameSceneController.resetAmountOfArmiesForTerritories());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("CPU-Turn Thread interrupted.");
                }

                // Notify threads waiting on 'turnLock' to continue work.
                synchronized (turnLock) {
                    turnLock.notify();
                }

                return null;
            }

        };

        Thread th = new Thread(cpuTurn);
        th.setDaemon(true);
        th.start();

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
                    synchronized (turnLock) {
                        turnLock.notify();
                    }
                    break;
            }
        } else {
            playerTurn(TurnPhase.DRAFT);
        }
    }

    private void checkForVictory() {
        if (player.getControlledTerritories().size() == 0) {
            gameEndSceneController.setVictor(player);
            requestDisplayForScene(GAME_END);
        } else if (cpu.getControlledTerritories().size() == 0) {
            gameEndSceneController.setVictor(cpu);
            requestDisplayForScene(GAME_END);
        }
    }

    public void flagEndOfGame(boolean save) {

        // End Threads
        gameIsRunning = false;
        gameloop.interrupt();

        if (gamePauseMenuStage.isShowing()) {
            gamePauseMenuStage.close();
        }

        if (gameEndStage.isShowing()) {
            gameEndStage.close();
        }

        if (save) {

            // Save the game state.
            savedGameState = gameState;

            // Reset the game state.
            gameState = null;

        }

        requestDisplayForScene(MAIN_MENU);

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

        // Loader for GamePauseMenuSceneController
        GamePauseMenuSceneController gamePauseMenuSceneController = (GamePauseMenuSceneController) loadFxmlController("fxml/GamePauseMenuScene.fxml");
        gamePauseMenuScene = gamePauseMenuSceneController.getPrimaryScene();

        // GameSetupSceneController
        gameSetupSceneController = (GameSetupSceneController) loadFxmlController("fxml/GameSetupSceneController.fxml");
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

    /**
     * Parses 'savedGameState' into the necessary application objects and data structures.
     */
    private void loadSavedGame() {

        // Load saved game for continuation.
        if (savedGameState == null) {
            throw new NullPointerException("Loaded game-state serialization but stored object was null.");
        } else {
            gameState = savedGameState;
        }

        // Define saved Player and CPU data
        player = savedGameState.getPlayer();
        cpu = savedGameState.getCpu();

        // Define saved Territory data
        territories = new HashMap<>();
        for (Territory territory : player.getControlledTerritories()) {
            territories.put(territory.getName(), territory);
        }
        for (Territory territory : cpu.getControlledTerritories()) {
            territories.put(territory.getName(), territory);
        }

    }

    /** High-level method to organize creation of Territories and definition of their neighbors. */
    private void initializeTerritories() {

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

    /**
     * High-level method to organize definition of Territory neighbors.
     */
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
        setTerritoryNeighbors("japan", "kamchatka", "mongolia");
        setTerritoryNeighbors("afghanistan", "ural", "ukraine", "middleEast", "india", "china");
        setTerritoryNeighbors("china", "mongolia", "siberia", "ural", "afghanistan", "india", "siam");
        setTerritoryNeighbors("middleEast", "ukraine", "southernEurope", "egypt", "india", "afghanistan");
        setTerritoryNeighbors("india", "china", "afghanistan", "middleEast", "siam");
        setTerritoryNeighbors("siam", "china", "india", "indonesia");

        // Australia
        setTerritoryNeighbors("indonesia", "siam", "newGuinea", "westernAustralia");
        setTerritoryNeighbors("newGuinea", "indonesia", "westernAustralia", "easternAustralia");
        setTerritoryNeighbors("westernAustralia", "indonesia", "easternAustralia", "newGuinea");
        setTerritoryNeighbors("easternAustralia", "newGuinea", "westernAustralia");

    }

    /**
     * Sets the neighbors of the Territory in the first parameter as those given in the following Strings.
     */
    private void setTerritoryNeighbors(String territoryName, String... neighborNames) {
        Territory territory = territories.get(territoryName);
        ArrayList<Territory> ts = new ArrayList<>();
        for (String neighbor: neighborNames) {
            ts.add(territories.get(neighbor));
        }
        territory.setNeighbors(ts);
    }

    /**
     * Tells Game that it has been requested to change the Scene (or bring up a new Stage).
     */
    public void requestDisplayForScene(int scene) {
        switch (scene) {
            case GAME:
                gameSceneController.disableRootShadow();
                primaryStage.setScene(gameScene);
                primaryStage.centerOnScreen();
                break;
            case ABOUT_GAME:
                primaryStage.setScene(aboutGameScene);
                break;
            case PAUSE_GAME_MENU:
                gameSceneController.enableRootShadow();
                gamePauseMenuStage.show();
                break;
            case GAME_SETUP:

                // Initialize option to load a saved game if one is available.
                if (savedGameState == null) {
                    gameSetupSceneController.hideContinueGameButton();
                } else {
                    gameSetupSceneController.showContinueGameButton();
                }

                primaryStage.setScene(gameSetupScene);
                break;

            case GAME_END:
                gameSceneController.enableRootShadow();
                gameEndStage.show();
                break;
            default:
                primaryStage.setScene(mainMenuScene);
                primaryStage.centerOnScreen();
                break;
        }
    }

    /**
     * De-serializes the saved game-state at the specified file location.
     * @return The de-serialized object.
     */
    private GameState deserializeSavedGameState() {
        try {

            // Deserialize the object.
            FileInputStream file_in_stream = new FileInputStream(SAVED_GAME_STATE_FP);
            ObjectInputStream object_in_stream = new ObjectInputStream(file_in_stream);

            // Return the de-serialized object.
            return (GameState) (object_in_stream.readObject());

        } catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, "savedGameState resource file not found.", e);
            return null;
        } catch (ClassNotFoundException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     * Creates a serialization of 'savedGameState' for later use.
     */
    private void serializeSavedGameState() {
        try {

            // Serialize the object.
            FileOutputStream file_out_stream = new FileOutputStream(SAVED_GAME_STATE_FP);
            ObjectOutputStream object_out_stream = new ObjectOutputStream(file_out_stream);
            object_out_stream.writeObject(savedGameState);

            // Close the streams.
            file_out_stream.close();
            object_out_stream.close();

        } catch (IOException e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void performPlayerAttack(Territory attackOrigin, Territory attackTarget) {
        playerDice.roll();
        cpuDice.roll();
        boolean didConquerTarget = attackOrigin.attack(attackTarget, playerDice.getTotal(), cpuDice.getTotal());

        if (didConquerTarget) {
            transferTerritoryOwnership(player, cpu, attackTarget);
            gameSceneController.updateTerritoryOwner(attackTarget.getName(), player);
        }

        checkForVictory();
    }

    public void closeGamePauseMenuStage() {
        gamePauseMenuStage.close();
    }

    private void transferTerritoryOwnership(Player newOwner, Player previousOwner, Territory territory) {
        territory.setOwner(newOwner);

        previousOwner.removeControlledTerritory(territory);
        newOwner.addNewControlledTerritory(territory);
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

    public void setNumOfArmiesForTerritory(Territory territory, int numOfArmies) {
        territories.get(territory.getName()).setNumOfArmies(numOfArmies);
    }

    /* Main */
    public static void main(String[] args) {
        launch(args);
    }


}
