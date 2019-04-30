package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

import static risk.Game.MAIN_MENU;

/**
 * FXML Controller for the Game's 'Help' scene. This Scene displays information about how to play the game.
 *
 * @FXMLFile risk.fxml.HelpGameSceneController.fxml
 */
public class HelpGameSceneController extends RiskSceneController {

    /* Fields */
    /**
     * Control to close this Scene.
     */
    @FXML
    private Button goBackToMainMenu;


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        goBackToMainMenu.setOnAction(event -> {
            System.out.println("Exiting AboutGameScene from 'goBackToMainMenu' Button used within AboutGameScene");
            instance.requestDisplayForScene(MAIN_MENU);
        });
    }

}
