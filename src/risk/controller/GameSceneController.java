package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Glow;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import risk.Game;

import static risk.Game.PAUSE_GAME_MENU;
import static risk.Game.PLAYER_SELECTED_TERRITORY_FOR_ATTACK;

import java.net.URL;
import java.util.ArrayList;
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

    private ArrayList<ToggleButton> territoryToggleButtons;

    private ArrayList<Line> legalAttackPathIndicators;

    private final Glow SELECTED_TERRITORY = new Glow(0.5);

    @FXML
    public Group boardNodes;

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


        // Load in references to board objects.
        territoryToggleButtons = new ArrayList<>(42);
        legalAttackPathIndicators = new ArrayList<>(84);
        for (Node node : boardNodes.getChildren()) {
            if (node instanceof ToggleButton) {
                territoryToggleButtons.add((ToggleButton) node);
            } else if (node instanceof  Line) {
                legalAttackPathIndicators.add((Line) node);
            }
        }

        // Initialize territory ToggleButtons.
        Circle circle = new Circle(TERRITORY_BUTTON_SHAPE_RAD);
        for (ToggleButton button : territoryToggleButtons) {
            button.setShape(circle);
            double size = 2*TERRITORY_BUTTON_SHAPE_RAD;
            button.setMinSize(size, size);
            button.setMaxSize(size, size);

            button.setOnAction(event -> selectTerritoryForAttack(button));
        }

        // Initialize legal-attack-path-indicators.
        for (Line line : legalAttackPathIndicators) {
            line.setVisible(false);
        }

        // Initialize button for controlling turn phases.
        circle = new Circle(NEXT_PHASE_TURN_BUTTON_SHAPE_RAD);
        nextPhaseOrTurn.setShape(circle);
        double size = 2*NEXT_PHASE_TURN_BUTTON_SHAPE_RAD;
        nextPhaseOrTurn.setMinSize(size, size);
        nextPhaseOrTurn.setMaxSize(size, size);

    }

    private void selectTerritoryForAttack(ToggleButton button) {

        if (!button.isSelected()) {

            // Highlight origin territory.
            button.setSelected(true);
            button.setEffect(SELECTED_TERRITORY);

            // Show lines to possible targets.
            //

            // Check if this is a territory to attack or an origin of attack.
            if (!Game.PLAYER_SELECTED_TERRITORY_FOR_ATTACK) {
                // This territory is an origin of attack.
                PLAYER_SELECTED_TERRITORY_FOR_ATTACK = true;
            } else {
                // This territory is a subject of attack.
                // PASS
            }

        }

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
