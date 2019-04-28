package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * TODO:
 *  Create official background image for MenuScene.
 */
public class MainMenuSceneController extends RiskSceneController{

    @FXML
    public Button playGame;

    @FXML
    public Button quitGame;

    @FXML
    public Button aboutGame;

    public MainMenuSceneController() {
        if (verbose) System.out.println("Initialized Controller for Scene: MainMenu.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeButtonActions();
    }

    private void initializeButtonActions() {

        playGame.setOnAction(event -> {
            if (verbose) System.out.println("Displaying GameScene from 'playGame' Button used within MainMenuScene.");
            instance.requestDisplayForScene(Game.GAME_SETUP);
        });

        quitGame.setOnAction(event -> {
            if (verbose) System.out.println("Exiting Game from 'quitGame' Button used within MainMenuScene.");
            instance.stop();
        });

        aboutGame.setOnAction(event -> {
            if (verbose) System.out.println("Displaying AboutGameScene from 'aboutGame' Button used within MainMenuScene.");
            instance.requestDisplayForScene(Game.ABOUT_GAME);
        });

    }

}
