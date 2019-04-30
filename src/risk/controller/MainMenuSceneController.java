package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller for the Game's main menu. This is the access point to all other Scenes.
 */
public class MainMenuSceneController extends RiskSceneController{

    /* Fields */
    /**
     * Access to the Game setup-Scene.
     */
    @FXML
    private Button playGame;

    /**
     * Control to close this Application.
     */
    @FXML
    private Button quitGame;

    /**
     * Control to access 'AboutGameScene'.
     */
    @FXML
    private Button aboutGame;

    /**
     * Control to access 'HelpGameScene'.
     */
    @FXML
    private Button helpGame;


    /* Constructor */
    public MainMenuSceneController() {
        if (verbose) System.out.println("Initialized Controller for Scene: MainMenu.");
    }


    /* Methods */
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

        helpGame.setOnAction(event -> {
            System.out.println("Displaying HelpGameScene from 'helpGame' Button used within MainMenuScene.");
            instance.requestDisplayForScene(Game.HELP_GAME);
        });

    }

}
