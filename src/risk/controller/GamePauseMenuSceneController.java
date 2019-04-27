package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

public class GamePauseMenuSceneController extends RiskSceneController {

    @FXML
    private Button quit;

    @FXML
    private Button saveAndQuit;

    @FXML
    private Button continueGame;

    public GamePauseMenuSceneController() {
        System.out.println("Initialized Controller for Scene: GamePauseMenu.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeButtonActions();
    }

    private void initializeButtonActions() {

        quit.setOnAction(event -> {
            System.out.println("Exiting GameScene from 'quit' Button used within 'GamePauseMenuScene.");
            instance.flagEndOfGame();
        });

        saveAndQuit.setOnAction(event -> {
            System.out.println("Exiting GameScene from 'saveAndQuit' Button used within 'GamePauseMenuScene.");
            instance.flagEndOfGame();
            instance.serializeDefaultLoadableGameState();
        });

        continueGame.setOnAction(event -> {
            instance.closeGamePauseMenuStage();
            instance.requestDisplayForScene(Game.GAME);
        });

    }

}
