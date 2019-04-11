package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

import static risk.Game.PAUSE_GAME_MENU;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class GameSceneController extends RiskSceneController {

    /*
    TODO:
        Implement loading of previous Game-state.
     */

    private final double TERRITORY_BUTTON_SHAPE_RAD = 12.0;

    private final double NEXT_PHASE_TURN_BUTTON_SHAPE_RAD = 17.0;

    @FXML
    public Group territoryButtons;

    @FXML
    public Button nextPhaseOrTurn;

    @FXML
    public Label fortifyIndicator;

    @FXML
    public Label attackIndicator;

    @FXML
    public Label draftIndicator;

    @FXML
    public Circle playerTurnIndicator;

    @FXML
    public Circle cpuTurnIndicator;

    public GameSceneController() {
        System.out.println("Initialized Controller for Scene: Game.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeKeyboardListeners();

        Circle circle = new Circle(TERRITORY_BUTTON_SHAPE_RAD);
        for (Node node : territoryButtons.getChildren()) {
            ((Button) node).setShape(circle);
            ((Button) node).setMinSize(2* TERRITORY_BUTTON_SHAPE_RAD, 2* TERRITORY_BUTTON_SHAPE_RAD);
            ((Button) node).setMaxSize(2* TERRITORY_BUTTON_SHAPE_RAD, 2* TERRITORY_BUTTON_SHAPE_RAD);
        }

        circle = new Circle(NEXT_PHASE_TURN_BUTTON_SHAPE_RAD);
        nextPhaseOrTurn.setShape(circle);
        nextPhaseOrTurn.setMinSize(2*NEXT_PHASE_TURN_BUTTON_SHAPE_RAD, 2*NEXT_PHASE_TURN_BUTTON_SHAPE_RAD);
        nextPhaseOrTurn.setMaxSize(2*NEXT_PHASE_TURN_BUTTON_SHAPE_RAD, 2*NEXT_PHASE_TURN_BUTTON_SHAPE_RAD);

    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void initializeKeyboardListeners() {

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE: instance.requestDisplayForScene(PAUSE_GAME_MENU);
            }
        });

    }

    public Scene getScene() {
        return scene;
    }


}
