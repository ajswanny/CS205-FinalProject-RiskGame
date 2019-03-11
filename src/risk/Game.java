package risk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import risk.controller.AboutGameSceneController;
import risk.controller.GameSceneController;
import risk.controller.MainMenuSceneController;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends Application {

    /* Class Fields */
    public static final int MAIN_MENU = 0, GAME = 1, ABOUT_GAME = 2;

    private Stage primaryStage;

    private Scene mainMenuScene, gameScene, aboutGameScene;

    /** The Controller object for the Menu Scene. */
    private MainMenuSceneController mainMenuSceneController;

    /** The Controller object for the Game Scene. */
    private GameSceneController gameSceneController;

    /** The Controller object for the AboutGame Scene. */
    private AboutGameSceneController aboutGameSceneController;

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
        System.out.println("Shutting down Game instance" + this + ".");
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

            default:
                setDisplayToMainMenuScene();
                break;

        }

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

    /* Main */
    public static void main(String[] args) {
        launch(args);
    }


}
