package risk;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import risk.controller.GameSceneController;
import risk.controller.MenuSceneController;

public class Game extends Application {

    /** Height and Width of window. */
    public static final int HEIGHT = 1000, WIDTH = 800;

    /** The Game-App's primary Scene. */
    private Scene primaryScene;

    /** The Controller object for the Menu Scene. */
    private MenuSceneController menuSceneController;

    /** The root View of the Menu Scene.*/
    private AnchorPane menuSceneRootView;

    /** The Controller object for the Game Scene. */
    private GameSceneController gameSceneController;

    /** The root View of the Menu Scene. */
    private AnchorPane gameSceneRootView;

    /**
     * Starts the Game GUI.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        generateFxmlLoaders();

        primaryScene = new Scene(new Pane());
        primaryScene.setRoot(menuSceneRootView);

        primaryStage.setTitle("Risk");
        primaryStage.setScene(primaryScene);

        primaryStage.show();

    }

    @Override
    public void stop() {
        System.out.println("Thanks for playing Risk!");
    }

    private void generateFxmlLoaders() throws Exception {

        FXMLLoader fxmlLoader;

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("fxml/MenuSceneController.fxml"));
        menuSceneController = fxmlLoader.getController();
        menuSceneRootView = fxmlLoader.load();

//        fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("fxml/GameSceneController"));
//        gameSceneController = fxmlLoader.getController();
//        gameSceneRootView = fxmlLoader.load();

    }

    public static void main(String[] args) {

        launch(args);

    }


}
