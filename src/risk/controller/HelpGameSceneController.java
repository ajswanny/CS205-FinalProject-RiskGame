package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

import static risk.Game.MAIN_MENU;

public class HelpGameSceneController extends RiskSceneController {

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
}
