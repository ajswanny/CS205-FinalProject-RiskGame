package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

import static risk.Game.GAME;
import static risk.Game.ABOUT_GAME;

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
        System.out.println("Initialized Controller for Scene: MainMenu.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeCoreResources();
        initializeButtonActions();

    }

    private void initializeButtonActions() {

        playGame.setOnAction(event -> {
            System.out.println("Displaying GameScene from 'playGame' Button used within MainMenuScene.");
            instance.requestDisplayForScene(GAME);
        });

        quitGame.setOnAction(event -> {
            System.out.println("Exiting Game from 'quitGame' Button used within MainMenuScene.");
            instance.stop();
        });

        aboutGame.setOnAction(event -> {
            System.out.println("Displaying AboutGameScene from 'aboutGame' Button used within MainMenuScene.");
            instance.requestDisplayForScene(ABOUT_GAME);
        });

    }

    public Scene getScene() {
        return scene;
    }

}
