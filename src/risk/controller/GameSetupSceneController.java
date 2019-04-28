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

public class GameSetupSceneController extends RiskSceneController {

    @FXML
    public Button backToMainMenu;

    @FXML
    public Button continueGame;

    @FXML
    public Button newGame;

    @FXML
    public HBox playerColorToggleButtons;

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
            continueGame.setDisable(true);
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

        // Continue Game Button
        continueGame.setOnAction(event -> {
            close();
            instance.requestStartOfGame(false, playerSelectedColor);
        });

        // New game
        newGame.setOnAction(event -> {
            close();
            instance.requestStartOfGame(true, playerSelectedColor);
        });

        // Disable Button until player makes a color selection.
        newGame.setDisable(true);

    }

    private void close() {
        for (Node node : playerColorToggleButtons.getChildren()) {
            node.setEffect(null);
        }
        newGame.setDisable(true);
    }

    public void showContinueGameButton() {
        showButton(continueGame);
    }

    public void hideContinueGameButton() {
        hideButton(continueGame);
    }

}
