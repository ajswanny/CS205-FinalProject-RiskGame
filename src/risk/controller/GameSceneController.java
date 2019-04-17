package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import risk.Game;
import risk.java.GameState;
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

    private ToggleButton selectedTerritoryToggleBtnForDraft;

    @FXML
    public Group boardNodes;

    @FXML
    public Button nextPhaseOrTurn;

    @FXML
    public Button decreaseArmiesToDraftForSelectedTerritory;

    @FXML
    public Button increaseArmiesToDraftForSelectedTerritory;

    @FXML
    public Label armiesToDraftIndicator;

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

        // Disable buttons that require action
        decreaseArmiesToDraftForSelectedTerritory.setDisable(true);
        increaseArmiesToDraftForSelectedTerritory.setDisable(true);

        // Buttons for increasing and increasing armies in a draft
        decreaseArmiesToDraftForSelectedTerritory.setOnAction(event -> setNewAmountOfTerritoryArmies(selectedTerritoryToggleBtnForDraft, -1));
        increaseArmiesToDraftForSelectedTerritory.setOnAction(event -> setNewAmountOfTerritoryArmies(selectedTerritoryToggleBtnForDraft, 1));

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

            button.setOnAction(event -> territoryButtonAction(button));
        }
        selectedTerritoryToggleBtnForDraft = null;

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

    /** Specifies the action of a TerritoryToggleButton with respect to the current Player-turn-phase. */
    private void territoryButtonAction(ToggleButton button) {
        switch (instance.playerTurnPhase) {
            case 1:
                selectTerritoryToggleBtnForDraft(button);
                break;
            case 2:
                selectTerritoryToggleBtnForAttack(button);
                break;
            case 3:
                break;
        }
    }

    private void selectTerritoryToggleBtnForDraft(ToggleButton button) {
        resetBoard();
        button.setEffect(STANDARD_GLOW_EFFECT);
        draftIndicator.setEffect(STANDARD_GLOW_EFFECT);

        decreaseArmiesToDraftForSelectedTerritory.setDisable(false);
        increaseArmiesToDraftForSelectedTerritory.setDisable(false);

        selectedTerritoryToggleBtnForDraft = button;
    }

    private void selectTerritoryToggleBtnForAttack(ToggleButton button) {
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

    private void selectTerritoryToggleBtnForFortify(ToggleButton button) {

    }

    /** Removes all Player customizations from the Game-board. */
    private void resetBoard() {

        // Hide all other attack-paths
        for (Line line : legalAttackPathIndicators) {
            line.setVisible(false);
        }

        // Hide all other Glows
        for (ToggleButton toggleButton : territoryToggleButtons) {
            toggleButton.setEffect(null);
        }
        draftIndicator.setEffect(null);

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
    /**
     * Sets the game state: defines all Territory-ToggleButtons, setting their color with respect to the Player that
     * controls that territory and setting their text to display the amount of armies present in Territories.
     */
    public void setGameState(GameState gameState) {
        String styleForPlayerColor = "-fx-background-color: #" + getColorHexForPlayerColor(gameState.player.getColor());
        String styleForCpuColor = "-fx-background-color: #6a6f6b";
        Territory territory;
        for (ToggleButton territoryButton : territoryToggleButtons) {
            territory = instance.territories.get(territoryButton.getId());
            if (territory.getOwner() == gameState.player) {
                territoryButton.setStyle(styleForPlayerColor);
            } else {
                territoryButton.setStyle(styleForCpuColor);
            }
        }

        for (ToggleButton button : territoryToggleButtons) {
            button.setText(String.valueOf(instance.territories.get(button.getId()).getNumOfArmies()));
            button.setGraphic(new Label(button.getText()));
        }
    }

    /** Sets the Label that tells the user how many armies they may place at the beginning of their turn. */
    public void setArmiesToDraftIndicator(int availableArmiesToDraft) {
        armiesToDraftIndicator.setText(String.valueOf(availableArmiesToDraft));
    }

    private void setNewAmountOfTerritoryArmies(ToggleButton territoryToggleBtn, int difference) {

        // Validate request
        if (difference < 0) {
            // User is attempting to remove armies from a territory, but this is only possible if armies have been drafted
            // to this territory in this turn
            if (Integer.valueOf(armiesToDraftIndicator.getText()) > 5) {
                armiesToDraftIndicator.setText(String.valueOf(Integer.valueOf(armiesToDraftIndicator.getText()) + 1));
            } else {
                return;
            }
        } else if (difference > 0) {
            // User is attempting to add an army to a territory: this is only possible if they have a valid amount of
            // armies remaining for drafting
            int val = Integer.valueOf(armiesToDraftIndicator.getText());
            if (val > 0 && val <= 5) {
                armiesToDraftIndicator.setText(String.valueOf(Integer.valueOf(armiesToDraftIndicator.getText()) - 1));
            } else {
                return;
            }
        }

        // Update remaining data and gui elements
        Territory territory = instance.territories.get(territoryToggleBtn.getId());
        instance.setNumOfArmiesForTerritory(territory, territory.getNumOfArmies() + difference);
        ((Label) territoryToggleBtn.getGraphic()).setText(String.valueOf(territory.getNumOfArmies()));

    }

}
