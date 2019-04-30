package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller for the main-menu within the Game Scene. This dialog provides the user with the ability to quit the
 * game without saving its state, to quit the game and save its state, and to continue the game.
 *
 * @FXMLFile risk.fxml.GamePauseMenuSceneController.fxml
 */
public class GamePauseMenuSceneController extends RiskSceneController {

    /* Fields */
    /**
     * Quits the game without saving the game-state.
     */
    @FXML
    private Button quit;

    /**
     * Quits the game while saving the game-state.
     */
    @FXML
    private Button saveAndQuit;

    /**
     * Closes this dialog.
     */
    @FXML
    private Button continueGame;


    /* Constructor */
    public GamePauseMenuSceneController() {
        if (verbose) System.out.println("Initialized Controller for Scene: GamePauseMenu.");
    }


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        quit.setOnAction(event -> {
            if (verbose) System.out.println("Exiting GameScene from 'quit' Button used within 'GamePauseMenuScene.");
            instance.flagEndOfGame(false);
        });

        saveAndQuit.setOnAction(event -> {
            if (verbose) System.out.println("Exiting GameScene from 'saveAndQuit' Button used within 'GamePauseMenuScene.");
            instance.flagEndOfGame(true);
        });

        continueGame.setOnAction(event -> {
            instance.closeGamePauseMenuStage();
            instance.requestDisplayForScene(Game.GAME);
        });

    }

}
