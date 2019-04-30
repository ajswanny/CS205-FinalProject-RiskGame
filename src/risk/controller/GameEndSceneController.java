package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import risk.java.CPU;
import risk.java.Player;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller for the dialog which tells the user the game has ended either by a Player victory or a CPU victory.
 *
 * @FXMLFile risk.fxml.GameEndSceneController.fxml
 */
public class GameEndSceneController extends RiskSceneController {

    /* Fields */
    /**
     * Indicates whether the user has won or lost the game.
     */
    @FXML
    private Label result;

    /**
     * Main-menu access control. This control tells the Game instance that it should execute the end-of-game procedure.
     */
    @FXML
    private Button backToMainMenu;


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        backToMainMenu.setOnAction(event -> instance.flagEndOfGame(false));
    }


    /* Setters */
    /**
     * Updates the GUI to indicate which player won the game.
     * @param victor the player that was victorious.
     */
    public void setVictor(Player victor) {
        if (victor instanceof CPU) {
            result.setText("Defeat...");
        } else {
            result.setText("Victory!");
        }
    }

}
