package risk.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TODO:
 *  Create background image for MenuScene.
 */
public class MainMenuSceneController implements Initializable {

    private Scene scene;

    @FXML
    public AnchorPane root;

    @FXML
    public Label projectCredits;

    @FXML
    public Button playGame;

    @FXML
    public Button quitGame;

    @FXML
    public Button aboutGame;

    public MainMenuSceneController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scene = new Scene(root);

        initializeButtonActions();
    }

    private void initializeButtonActions() {

        playGame.setOnAction(event -> {
            System.out.println("Beginning Game from 'playGame' Button used within the MainMenuScene.");
        });

        quitGame.setOnAction(event -> {
            System.out.println("Exiting Game from 'quitGame' Button used within the MainMenuScene.");
            Game.getInstance().stop();
        });

        aboutGame.setOnAction(event -> {
            System.out.println("Opening 'About' page from 'aboutGame' Button used within the MainMenuScene.");
        });

    }

    public Scene getScene() {
        return scene;
    }

}
