package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

abstract public class RiskSceneController implements Initializable {

    Game instance;

    Scene scene;

    @FXML
    public AnchorPane root;

    void initializeCoreResources() {
        instance = Game.getInstance();
        scene = new Scene(root);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
