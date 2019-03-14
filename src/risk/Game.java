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

import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal", "JavaDoc"})
public class Game extends Application {

    /* Class Fields */
    /** Game-Scene enumerations */
    public static final int MAIN_MENU = 0, GAME = 1, ABOUT_GAME = 2, PAUSE_GAME_MENU = 3;

    private static final int GAME_SCENE_WIDTH = 1000, GAME_SCENE_HEIGHT = 700;
    private static final int MENU_SCENE_WIDTH = 800, MENU_SCENE_HEIGHT = 500;

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

    private static Game instance;

    public Game() {
        instance = this;
    }

    /* Methods */
    /**
     * Starts the Game.
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {

        try {

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

    }

    @Override
    public void stop() {
        System.out.println("Shutting down Game instance " + this + ".");
        System.exit(0);
    }

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

    public void requestDisplayForScene(int scene) {

        switch (scene) {

            case MAIN_MENU:
                setDisplayToMainMenuScene();
                break;

            case GAME:
                setDisplayToGameScene();
                break;

            case ABOUT_GAME:
                setDisplayToAboutGameScene();
                break;

            case PAUSE_GAME_MENU:
                displayGamePauseMenu();
                break;

            default:
                setDisplayToMainMenuScene();
                break;

        }

    }

    public void displayGamePauseMenu() {
        gamePauseMenuStage.show();
    }

    private void setDisplayToMainMenuScene() {
        primaryStage.setScene(mainMenuScene);
    }

    private void setDisplayToGameScene() {
        primaryStage.setScene(gameScene);
    }

    private void setDisplayToAboutGameScene() {
        primaryStage.setScene(aboutGameScene);
    }

    /* Getters */
    public static Game getInstance() {
        return instance;
    }

    public void closeGamePauseMenuStage() {
        gamePauseMenuStage.close();
    }

    /* Main */
    public static void main(String[] args) {
        launch(args);
    }


}
