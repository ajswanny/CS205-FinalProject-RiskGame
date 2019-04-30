package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

import static risk.Game.MAIN_MENU;

/**
 * FXML Controller for the Scene which presents the user with information about the Game's creation.
 *
 * @FXMLFile risk.fxml.AboutGameSceneController.fxml
 */
public class AboutGameSceneController extends RiskSceneController {

    /* Fields */
    /**
     * Control to return to the main menu.
     */
    @FXML
    private Button goBackToMainMenu;


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        goBackToMainMenu.setOnAction(event -> {
            if (verbose) System.out.println(
                    "Exiting AboutGameScene from 'goBackToMainMenu' Button used within AboutGameScene"
            );
            instance.requestDisplayForScene(MAIN_MENU);
        });
    }

}
