package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import risk.java.CPU;
import risk.java.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class GameEndSceneController extends RiskSceneController {

    @FXML
    private Label result;

    @FXML
    private Button backToMainMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        backToMainMenu.setOnAction(event -> instance.flagEndOfGame(false));
    }

    /* Setters */
    public void setVictor(Player victor) {
        if (victor instanceof CPU) {
            result.setText("Defeat...");
        } else {
            result.setText("Victory!");
        }
    }

}
