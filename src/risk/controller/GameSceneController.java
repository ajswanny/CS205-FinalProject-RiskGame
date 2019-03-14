package risk.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

public class GameSceneController implements Initializable {

    private Game instance;

    public Scene scene;

    @FXML
    public AnchorPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = Game.getInstance();

        scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE: instance.stop();
            }
        });
    }

    public Scene getScene() {
        return scene;
    }


}
