package risk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import risk.controller.GameSceneController;
import risk.controller.MenuSceneController;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends Application {

    public static final int MENU = 0, GAME = 1;

    private Stage primaryStage;

    private Scene menuScene, gameScene;

    /** The Controller object for the Menu Scene. */
    private MenuSceneController menuSceneController;

    /** The Controller object for the Game Scene. */
    private GameSceneController gameSceneController;

    private static Game instance;

    public Game() {
        instance = this;
    }

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
            primaryStage.setScene(menuScene);
            primaryStage.show();

        } catch (Exception e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @Override
    public void stop() {
        System.out.println("Thanks for playing Risk!");
    }

    private void loadFxmlSources() throws Exception {

        FXMLLoader fxmlLoader;

        // Loader for MenuSceneController
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("fxml/MenuSceneController.fxml"));
        fxmlLoader.load();
        menuSceneController = fxmlLoader.getController();
        menuScene = menuSceneController.getScene();

        // Loader for GameSceneController
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("fxml/GameSceneController.fxml"));
        fxmlLoader.load();
        gameSceneController = fxmlLoader.getController();
        gameScene = gameSceneController.getScene();

    }

    /* Getters */
    public static Game getInstance() {
        return instance;
    }

    public static void main(String[] args) {

        launch(args);

    }


}
