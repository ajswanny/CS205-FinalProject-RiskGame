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
import risk.java.Dice;
import risk.java.GameState;
import risk.java.Territory;

import static risk.Game.PAUSE_GAME_MENU;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public class GameSceneController extends RiskSceneController {

    private final double TERRITORY_BUTTON_SHAPE_RAD = 12.0;

    private final double NEXT_PHASE_TURN_BUTTON_SHAPE_RAD = 20.0;

    private ArrayList<ToggleButton> territoryToggleButtons;

    private ArrayList<Line> legalAttackPathIndicators;

    private final Glow STANDARD_GLOW_EFFECT = new Glow(0.5);

    private final Glow TARGET_TERRITORY_EFFECT = new Glow(1);

    private ToggleButton selectedTerritoryToggleBtnForDraft;

    private ToggleButton attackOriginControl, attackTargetControl;

    private Dice playerDice, cpuDice;

    @FXML
    public Group boardNodes;

    @FXML
    public Button makeAttack;

    @FXML
    public Button nextPhaseOrTurn;

    @FXML
    public Button decreaseArmiesToDraftForSelectedTerritory;

    @FXML
    public Button increaseArmiesToDraftForSelectedTerritory;

    @FXML
    public Label armiesToDraftIndicator;

    @FXML
    public Label fortifyPhaseIndicator;

    @FXML
    public Label attackPhaseIndicator;

    @FXML
    public Label draftPhaseIndicator;

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

        armiesToDraftIndicator.setText(String.valueOf(instance.ARMIES_TO_DRAFT));

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

        // Button to go to next turn phase
        nextPhaseOrTurn.setOnAction(event -> indicateEndOfTurnPhase());
        nextPhaseOrTurn.setDisable(true);

        // Attack command btn
        disableButton(makeAttack);
        makeAttack.setOnAction(event -> handleAttackRequest());

        // Init dice
        playerDice = new Dice();
        cpuDice = new Dice();

    }

    /**
     * Validates and performs an attack. Currently we are implementing a process where a single attack attempts to
     * fully conquer a territory.
     */
    private void handleAttackRequest() {

        String originTerritoryName = attackOriginControl.getId();
        Territory originTerritory = instance.territories.get(originTerritoryName);
        String targetTerritoryName = attackTargetControl.getId();
        Territory targetTerritory = instance.territories.get(targetTerritoryName);

        if (originTerritory.getNumOfArmies() > targetTerritory.getNumOfArmies()) {

            // Attack with each territory until the enemy territory is conquered or player can not longer make an attack
            // from this origin
            for (int t = 0; t < originTerritory.getNumOfArmies(); t++) {

                playerDice.roll();
                cpuDice.roll();
                if (playerDice.getTotal() > cpuDice.getTotal()) {
                    decrementNumOfArmiesForTerritory(targetTerritory);
                } else {
                    decrementNumOfArmiesForTerritory(originTerritory);
                }

            }

        }

    }

    /**
     * Performs a decrement of a Territory's armies, updating data and GUI.
     */
    private void decrementNumOfArmiesForTerritory(Territory territory) {
        instance.decrementNumOfArmiesForTerritory(territory);
        for (ToggleButton toggleButton : territoryToggleButtons) {
            if (toggleButton.getId().equals(territory.getName())) {
                ((Label) toggleButton.getGraphic()).setText(String.valueOf(territory.getNumOfArmies()));
            }
        }
    }

    private void indicateEndOfTurnPhase() {
        switch (instance.playerTurnPhase) {
            case DRAFT:
                instance.flagEndOfPlayerDraftPhase();
                disableButton(decreaseArmiesToDraftForSelectedTerritory);
                disableButton(increaseArmiesToDraftForSelectedTerritory);
                armiesToDraftIndicator.setVisible(false);
                makeAttack.setVisible(true);
                makeAttack.setDisable(false);
                break;
            case ATTACK:
                instance.flagEndOfPlayerAttackPhase();
                break;
            case FORTIFY:
                instance.flagEndOFPlayerFortifyPhase();
                break;
        }
        resetBoard();
    }

    private void disableButton(Button button) {
        button.setVisible(false);
        button.setDisable(true);
    }

    /** Specifies the action of a TerritoryToggleButton with respect to the current Player-turn-phase. */
    private void territoryButtonAction(ToggleButton button) {
        switch (instance.playerTurnPhase) {
            case DRAFT:
                // Return if player selected a CPU-owned territory during DRAFT phase
                if (instance.territories.get(button.getId()).getOwner() == instance.cpu) {
                    return;
                }
                selectTerritoryToggleBtnForDraft(button);
                break;
            case ATTACK:
                selectTerritoryToggleBtnForAttack(button);
                break;
            case FORTIFY:
                break;
        }
    }

    private void selectTerritoryToggleBtnForDraft(ToggleButton button) {
        resetBoard();
        button.setEffect(STANDARD_GLOW_EFFECT);
        draftPhaseIndicator.setEffect(STANDARD_GLOW_EFFECT);

        decreaseArmiesToDraftForSelectedTerritory.setDisable(false);
        increaseArmiesToDraftForSelectedTerritory.setDisable(false);

        selectedTerritoryToggleBtnForDraft = button;
    }

    private void selectTerritoryToggleBtnForAttack(ToggleButton button) {

        resetBoard();

        if (instance.player.getControlledTerritories().contains(instance.territories.get(button.getId()))) {
            showAttackLinesForTerritory(button.getId());
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
        draftPhaseIndicator.setEffect(null);

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
    public void setHighlightForAttackPhaseIndicator(Game.TurnPhase which) {

        // Reset values.
        draftPhaseIndicator.setTextFill(Color.valueOf("#ffbf00"));
        attackPhaseIndicator.setTextFill(Color.valueOf("#ffbf00"));
        fortifyPhaseIndicator.setTextFill(Color.valueOf("#ffbf00"));

        // Set value
        switch (which) {
            case DRAFT:
                draftPhaseIndicator.setTextFill(Color.RED);
                break;
            case ATTACK:
                attackPhaseIndicator.setTextFill(Color.RED);
                break;
            case FORTIFY:
                fortifyPhaseIndicator.setTextFill(Color.RED);
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

    /**
     * Updates the GUI and data for a new amount of armies for a Territory.
     * Current implementation: only allow the user to draft armies in one territory. Could change later.
     */
    private void setNewAmountOfTerritoryArmies(ToggleButton territoryToggleBtn, int difference) {

        Territory territory = instance.territories.get(territoryToggleBtn.getId());

        int newArmyVal = territory.getNumOfArmies();
        // Player tries to undo a draft
        if (difference < 0 && Integer.valueOf(armiesToDraftIndicator.getText()) == 0) {
            newArmyVal -= instance.ARMIES_TO_DRAFT;
            armiesToDraftIndicator.setText("5");
            nextPhaseOrTurn.setDisable(true);

        //Player tries to draft armies
        } else if (difference > 0 && Integer.valueOf(armiesToDraftIndicator.getText()) == 5){
            newArmyVal += instance.ARMIES_TO_DRAFT;
            armiesToDraftIndicator.setText("0");
            nextPhaseOrTurn.setDisable(false);
        }

        instance.setNumOfArmiesForTerritory(territory, newArmyVal);
        ((Label) territoryToggleBtn.getGraphic()).setText(String.valueOf(territory.getNumOfArmies()));

    }

}
