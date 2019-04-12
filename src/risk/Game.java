package risk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import risk.controller.AboutGameSceneController;
import risk.controller.GamePauseMenuSceneController;
import risk.controller.GameSceneController;
import risk.controller.MainMenuSceneController;
import risk.java.Territory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings({"FieldCanBeLocal"})
public class Game extends Application {

    /* Class Fields */
    /** Game-Scene enumerations */
    public static final int MAIN_MENU = 0, GAME = 1, ABOUT_GAME = 2, PAUSE_GAME_MENU = 3;

    /** Path to the text-file containing all Territory names. */
    private final String TERRITORY_NAMES_FP = "resources/territoriesInfo.txt";

    /** The color themes for each continent and those that are available to the user for selection. */
    private final String
        NORTH_AMERICA_HEX = "EAD3BA",
        SOUTH_AMERICA_HEX = "994734",
        EUROPE_HEX = "6A6F6B",
        AFRICA_HEX = "876133",
        ASIA_HEX = "5E693D",
        AUSTRALIA_HEX = "8B626A"
    ;

    /** Primary Stage of the Application */
    private Stage primaryStage, gamePauseMenuStage;

    /** The Game's Scenes */
    private Scene mainMenuScene, gameScene, aboutGameScene, gamePauseMenuScene;

    /** The Controller for the Menu Scene */
    private MainMenuSceneController mainMenuSceneController;

    /** The Controller for the Game Scene */
    private GameSceneController gameSceneController;

    /** The Controller for the AboutGame Scene */
    private AboutGameSceneController aboutGameSceneController;

    /** The Controller for the GamePauseMenu Scene */
    private GamePauseMenuSceneController gamePauseMenuSceneController;

    /** Collection of Territories referenced by their name. */
    private HashMap<String, Territory> territories;

    public String originTerritoryName;

    public String targetTerritorName;

    private static Game instance;

    public static boolean PLAYER_SELECTED_ORIGIN_TERRITORY = false;

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
        primaryStage.setScene(gameScene);
    }

    /** Loads FXML data for access to FXMLControllers. */
    private void loadFxmlSources() throws Exception {

        FXMLLoader fxmlLoader;

        // Loader for MenuSceneController
        fxmlLoader = new FXMLLoader(getClass().getResource("fxml/MainMenuSceneController.fxml"));
        fxmlLoader.load();
        mainMenuSceneController = fxmlLoader.getController();
        mainMenuScene = mainMenuSceneController.getScene();

        // Loader for GameSceneController
        fxmlLoader = new FXMLLoader(getClass().getResource("fxml/GameSceneController.fxml"));
        fxmlLoader.load();
        gameSceneController = fxmlLoader.getController();
        gameScene = gameSceneController.getScene();

        // Loader for AboutGameSceneController
        fxmlLoader = new FXMLLoader(getClass().getResource("fxml/AboutGameSceneController.fxml"));
        fxmlLoader.load();
        aboutGameSceneController = fxmlLoader.getController();
        aboutGameScene = aboutGameSceneController.getScene();

        // Loader for GamePauseMenuSceneController
        fxmlLoader = new FXMLLoader(getClass().getResource("fxml/GamePauseMenuScene.fxml"));
        fxmlLoader.load();
        gamePauseMenuSceneController = fxmlLoader.getController();
        gamePauseMenuScene = gamePauseMenuSceneController.getScene();

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

        setTerritoryNeighbors("alaska", "northwestTerritory", "alberta", "kamchatka");
        setTerritoryNeighbors("northwestTerritory", "alaska", "alberta", "ontario", "greenland");
        setTerritoryNeighbors("greenland", "northwestTerritory", "ontario", "quebec", "iceland");
        setTerritoryNeighbors("alberta", "alaska","northwestTerritory", "ontario", "westernUnitedStates");
        setTerritoryNeighbors("ontario", "alberta", "northwestTerritory", "greenland", "quebec", "westernUnitedStates", "easternUnitedStates");
        setTerritoryNeighbors("quebec", "ontario", "greenland", "easternUnitedStates");
        setTerritoryNeighbors("westernUnitedStates", "alberta", "ontario", "easternUnitedStates", "centralAmerica");
        setTerritoryNeighbors("easternUnitedStates", "westernUnitedStates", "ontario", "quebec", "centralAmerica");
        setTerritoryNeighbors("centralAmerica", "westernUnitedStates", "easternUnitedStates", "venezuela");

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

            default:
                // Switch to MainMenu.
                primaryStage.setScene(mainMenuScene);
                primaryStage.centerOnScreen();
                break;

        }

    }

    /* Getters */
    public static Game getInstance() {
        return instance;
    }

    /** Used to request closing of a Stage and focus the primary Stage */
    public void closeGamePauseMenuStage() {
        gamePauseMenuStage.close();
    }

    /* Main */
    public static void main(String[] args) {
        launch(args);
    }


}
