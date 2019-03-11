package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GameSceneController implements Initializable {

    public Scene scene;

    @FXML
    public AnchorPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public Scene getScene() {
        return scene;
    }


}
