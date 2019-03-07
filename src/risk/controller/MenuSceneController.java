package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuSceneController implements Initializable {

    @FXML
    public AnchorPane root;

    @FXML
    public Label testLabel;

    public MenuSceneController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        testLabel.setText("Hello");

    }

}
