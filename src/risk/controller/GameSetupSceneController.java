package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import risk.Game;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller for game-setup. Allows the user to choose a Player color and start a new game of load a saved game.
 *
 * @FXMLFile risk.fxml.GameSetupSceneController.fxml
 */
public class GameSetupSceneController extends RiskSceneController {

    /* Fields */
    /**
     * Control to return to the main menu.
     */
    @FXML
    private Button backToMainMenu;

    /**
     * Control to close this dialog and load a saved game-state.
     */
    @FXML
    private Button continueGame;

    /**
     * Control to start a new Game.
     */
    @FXML
    private Button newGame;

    /**
     * Container of all ToggleButtons to select a Player color.
     */
    @FXML
    private HBox playerColorToggleButtons;

    /**
     * Effect for the selected Player-color.
     */
    private final Glow SELECTED_COLOR_EFFECT = new Glow(0.5);
    private String playerSelectedColor;


    /* Constructors */
    public GameSetupSceneController() {
        if (verbose) System.out.println("Initialized Controller for Scene: GameSetup.");
    }


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        backToMainMenu.setOnAction(event -> {
            newGame.setDisable(true);
            for (Node node : playerColorToggleButtons.getChildren()) {
                ((ToggleButton) node).setSelected(false);
                node.setEffect(null);
            }
            instance.requestDisplayForScene(Game.MAIN_MENU);
        });

        // Define toggle buttons for choosing Player color.
        Circle toggleButtonShape = new Circle(34);
        for (int i = 0; i < 5; i++) {
            ToggleButton toggleButton = (ToggleButton) playerColorToggleButtons.getChildren().get(i);
            toggleButton.setShape(toggleButtonShape);
            toggleButton.setMinSize(68, 68);
            toggleButton.setMaxSize(68, 68);

            toggleButton.setOnAction(event -> {
                for (Node node : playerColorToggleButtons.getChildren()) {
                    node.setEffect(null);
                }
                toggleButton.setEffect(SELECTED_COLOR_EFFECT);
                playerSelectedColor = toggleButton.getId();
                newGame.setDisable(false);
            });
        }
        assert playerSelectedColor != null;

        // Define button actions.
        continueGame.setOnAction(event -> {
            close();
            instance.requestStartOfGame(false, playerSelectedColor);
        });
        newGame.setOnAction(event -> {
            close();
            instance.requestStartOfGame(true, playerSelectedColor);
        });

        // Disable Button until player makes a color selection.
        newGame.setDisable(true);

    }

    /**
     * Performs all cleanup for this Scene.
     */
    private void close() {
        for (Node node : playerColorToggleButtons.getChildren()) {
            node.setEffect(null);
        }
        newGame.setDisable(true);
    }

    /**
     * Game's access point to show 'continueGame'.
     */
    public void showContinueGameButton() {
        showNode(continueGame);
    }

    /**
     * Game's access point to hide 'continueGame'.
     */
    public void hideContinueGameButton() {
        hideNode(continueGame);
    }

}
