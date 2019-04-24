package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

public class GamePauseMenuSceneController extends RiskSceneController {

    @FXML
    public Button exitToMainMenu;

    public GamePauseMenuSceneController() {
        System.out.println("Initialized Controller for Scene: GamePauseMenu.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeButtonActions();
    }

    private void initializeButtonActions() {

        exitToMainMenu.setOnAction(event -> {
            System.out.println("Exiting GameScene from 'exitToMainMenu' Button used within 'GamePauseMenuScene.");
            instance.closeGamePauseMenuStage();
            instance.requestDisplayForScene(Game.MAIN_MENU);
        });

    }

}
