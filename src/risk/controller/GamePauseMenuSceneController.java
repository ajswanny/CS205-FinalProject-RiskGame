package risk.controller;

import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;

public class GamePauseMenuSceneController extends RiskSceneController {

    public GamePauseMenuSceneController() {
        System.out.println("Initialized Controller for Scene: GamePauseMenu.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCoreResources();
    }

    public Scene getScene() {
        return scene;
    }

}
