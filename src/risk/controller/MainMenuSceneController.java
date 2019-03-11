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

import static risk.Game.GAME;
import static risk.Game.ABOUT_GAME;

/**
 * TODO:
 *  Create background image for MenuScene.
 */
public class MainMenuSceneController implements Initializable {

    private Game instance;

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

        instance = Game.getInstance();

        scene = new Scene(root);

        initializeButtonActions();

    }

    private void initializeButtonActions() {

        playGame.setOnAction(event -> {
            System.out.println("Beginning Game from 'playGame' Button used within the MainMenuScene.");
            instance.requestDisplayForScene(GAME);
        });

        quitGame.setOnAction(event -> {
            System.out.println("Exiting Game from 'quitGame' Button used within the MainMenuScene.");
            Game.getInstance().stop();
        });

        aboutGame.setOnAction(event -> {
            System.out.println("Opening 'About' page from 'aboutGame' Button used within the MainMenuScene.");
            instance.requestDisplayForScene(ABOUT_GAME);
        });

    }

    public Scene getScene() {
        return scene;
    }

}
