package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import static risk.Game.MAIN_MENU;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutGameSceneController extends RiskSceneController {

    @FXML
    public Button goBackToMainMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeButtonActions();
    }

    private void initializeButtonActions() {

        goBackToMainMenu.setOnAction(event -> {
            System.out.println("Exiting AboutGameScene from 'goBackToMainMenu' Button used within AboutGameScene");
            instance.requestDisplayForScene(MAIN_MENU);
        });

    }

    public Scene getScene() {
        return scene;
    }

}
