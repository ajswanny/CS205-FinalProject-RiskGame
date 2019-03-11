package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TODO:
 *  Create background image for MenuScene.
 */
public class MenuSceneController implements Initializable {

    private Scene scene;

    @FXML
    public AnchorPane root;

    @FXML
    public Label projectCredits;

    @FXML
    public Button playGame;

    @FXML
    public Button quitGame;

    public MenuSceneController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scene = new Scene(root);
    }

    public Scene getScene() {
        return scene;
    }

}
