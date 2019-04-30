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
 * A template for Controllers of Risk's Scenes.
 */
abstract public class RiskSceneController implements Initializable {

    /* Fields */
    /**
     * Application instance.
     */
    Game instance;

    /**
     * Primary Scene for this Controller.
     */
    Scene primaryScene;

    /**
     * If this Scene should execute with console output.
     */
    boolean verbose;

    /**
     * The root JavaFX Node.
     */
    @FXML
    AnchorPane root;


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCoreResources();
    }

    /**
     * Defines access to the Game instance and initializes the primary Scene.
     */
    private void initializeCoreResources() {
        instance = Game.getInstance();
        verbose = instance.verbose;
        primaryScene = new Scene(root);
    }

    /**
     * Hides a given JavaFX node.
     */
    void hideNode(Node node) {
        node.setVisible(false);
    }

    /**
     * Shows a given JavaFX node.
     */
    void showNode(Node node) {
        node.setVisible(true);
    }

    public Scene getPrimaryScene() {
        return primaryScene;
    }

}
