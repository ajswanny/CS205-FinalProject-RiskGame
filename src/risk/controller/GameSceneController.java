package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;

import static risk.Game.PAUSE_GAME_MENU;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class GameSceneController extends RiskSceneController {

    /*
    TODO:
        Implement loading of previous Game-state.
     */

    @FXML
    public Button alaska;

    public GameSceneController() {
        System.out.println("Initialized Controller for Scene: Game.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeKeyboardListeners();
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void initializeKeyboardListeners() {

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE: instance.requestDisplayForScene(PAUSE_GAME_MENU);
            }
        });

    }

    public Scene getScene() {
        return scene;
    }


}
