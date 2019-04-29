package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
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

    boolean verbose;

    @FXML
    AnchorPane root;

    private void initializeCoreResources() {
        instance = Game.getInstance();
        verbose = instance.verbose;
        primaryScene = new Scene(root);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCoreResources();
    }

    void hideNode(Node node) {
        node.setVisible(false);
    }

    void showNode(Node node) {
        node.setVisible(true);
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }

}
