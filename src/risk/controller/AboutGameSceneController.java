package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import risk.Game;

import static risk.Game.MAIN_MENU;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutGameSceneController implements Initializable {

    private Game instance;

    private Scene scene;

    @FXML
    public AnchorPane root;

    @FXML
    public Button goBackToMainMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        instance = Game.getInstance();

        scene = new Scene(root);

        initializeButtonActions();

    }

    private void initializeButtonActions() {

        goBackToMainMenu.setOnAction(event -> {
            System.out.println("Returning to MainMenu upon activation of 'goBackToMainMenu' button from the " +
                    "'AboutGameScene.");
            instance.requestDisplayForScene(MAIN_MENU);
        });

    }

    public Scene getScene() {
        return scene;
    }

}
