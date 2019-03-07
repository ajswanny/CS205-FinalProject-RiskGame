package risk;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game extends Application {

    /** Height and Width of window. */
    public static final int HEIGHT = 1000, WIDTH = 800;

    private Scene primaryScene;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryScene = new Scene(new Pane());

        primaryStage.setTitle("Risk");

        primaryStage.show();

    }

    public static void main(String[] args) {

        launch(args);

    }


}
