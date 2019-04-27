package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A template-Class for Controllers of Risk's Scenes.
 */
abstract public class RiskSceneController implements Initializable {

    Game instance;

    Scene primaryScene;

    @FXML
    public AnchorPane root;

    private void initializeCoreResources() {
        instance = Game.getInstance();
        primaryScene = new Scene(root);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCoreResources();
    }

    void hideButton(Button button) {
        button.setVisible(false);
    }

    void showButton(Button button) {
        button.setVisible(true);
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }

}
