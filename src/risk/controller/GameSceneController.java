package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import risk.Game;
import risk.java.GameState;
import risk.java.Player;
import risk.java.Territory;

import static risk.Game.PAUSE_GAME_MENU;
import static risk.Game.PLAYER_SELECTED_ORIGIN_TERRITORY;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public class GameSceneController extends RiskSceneController {

    /*
    TODO:
        Implement loading of previous Game-state.
     */

    private final double TERRITORY_BUTTON_SHAPE_RAD = 12.0;

    private final double NEXT_PHASE_TURN_BUTTON_SHAPE_RAD = 17.0;

    private ArrayList<ToggleButton> territoryToggleButtons;

    private ArrayList<Line> legalAttackPathIndicators;

    private final Glow STANDARD_GLOW_EFFECT = new Glow(0.5);

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

        resetBoard();

        // Highlight origin territory.
        button.setEffect(STANDARD_GLOW_EFFECT);

        // Check if this is a territory to attack or an origin of attack.
        if (!Game.PLAYER_SELECTED_ORIGIN_TERRITORY) {

            // This territory is an origin of attack.
            PLAYER_SELECTED_ORIGIN_TERRITORY = true;

            // Check if territory belongs to Player.

            showAttackLinesForTerritory(button.getId());

        } else {

            // This territory is a subject of attack.
            instance.targetTerritoryName = button.getId();

            // Tell Game that the Player has not selected a territory for the origin of an attack.
            PLAYER_SELECTED_ORIGIN_TERRITORY = false;

        }

    }

    /** Removes all Player customizations from the Game-board. */
    private void resetBoard() {

        // Hide all other attack-paths
        for (Line line : legalAttackPathIndicators) {
            line.setVisible(false);
        }

        for (ToggleButton toggleButton : territoryToggleButtons) {
            toggleButton.setEffect(null);
        }

    }

    private void showAttackLinesForTerritory(String territoryName) {
        for (Line line : legalAttackPathIndicators) {
            if (line.getId().contains(territoryName)) {
                line.setVisible(true);
            }
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private void initializeKeyboardListeners() {
        primaryScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE: instance.requestDisplayForScene(PAUSE_GAME_MENU);
            }
        });
    }

    /**
     * Draft: 1;
     * Attack: 2;
     * Fortify: 3.
     */
    public void setHighlightForAttackPhaseIndicator(int which) {

        // Reset values.
        draftIndicator.setTextFill(Color.valueOf("#ffbf00"));
        attackIndicator.setTextFill(Color.valueOf("#ffbf00"));
        fortifyIndicator.setTextFill(Color.valueOf("#ffbf00"));

        // Set value
        switch (which) {
            case 1:
                draftIndicator.setTextFill(Color.RED);
                break;
            case 2:
                attackIndicator.setTextFill(Color.RED);
                break;
            case 3:
                fortifyIndicator.setTextFill(Color.RED);
                break;
        }

    }

    public void setPlayerTurnIndicatorColor(Game.PlayerColor playerColor) {
        playerTurnIndicator.setFill(Color.valueOf(getColorHexForPlayerColor(playerColor)));
    }

    /** Returns a HEX String for the PlayerColor parameter. */
    private String getColorHexForPlayerColor(Game.PlayerColor playerColor) {
        switch (playerColor) {
            case NORTH_AMERICA:
                return instance.NORTH_AMERICA_HEX;
            case SOUTH_AMERICA:
                return instance.SOUTH_AMERICA_HEX;
            case AFRICA:
                return instance.AFRICA_HEX;
            case ASIA:
                return instance.ASIA_HEX;
            case AUSTRALIA:
                return instance.AUSTRALIA_HEX;
            default:
                return "#FFFFFF";
        }
    }

    /* Setters */
    public void setGameState(GameState gameState) {

        setArmiesLabelsForTerritories(gameState.player.getControlledTerritories());
        setArmiesLabelsForTerritories(gameState.cpu.getControlledTerritories());

    }

    private void setArmiesLabelsForTerritories(ArrayList<Territory> territories) {

    }

}
