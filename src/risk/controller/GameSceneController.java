package risk.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import risk.Game;
import risk.java.CPU;
import risk.java.GameState;
import risk.java.Player;
import risk.java.Territory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static risk.Game.PAUSE_GAME_MENU;

/**
 *
 */
public class GameSceneController extends RiskSceneController {

    // Collection of all ToggleButtons for Territories
    private ArrayList<ToggleButton> territoryToggleButtons;

    // Collection of Lines that show the Player which Territories they can attack from any given origin
    private ArrayList<Line> legalPathIndicators;

    // GUI Glow effects for Player selections
    public final DropShadow STANDARD_ATTACK_EFFECT = new DropShadow(BlurType.GAUSSIAN, Color.valueOf("#A80B0A"), 30, 0.5, 0, 0);
    public final DropShadow STANDARD_DRAFT_EFFECT = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 30, 0.5, 0, 0);
    private final Glow TARGET_PATH_EFFECT = new Glow(0.5);
    private final Bloom STANDARD_BLOOM_EFFECT = new Bloom(0.3);
    private Lighting ROOT_SHADOW;

    private final DropShadow CURRENT_TURN_OWNER = new DropShadow();

    // Flags for user selections
    private ToggleButton draftTerritoryControl, attackOriginControl, attackTargetControl, fortifyTerritoryControl;

    private String styleForPlayerColor, styleForCpuColor;

    private int armiesToMoveForFortification = 0;

    @FXML
    private Group boardNodes;

    @FXML
    private Group armyMovementControls;

    @FXML
    private Button makeAttack;

    @FXML
    private Button nextPhaseOrTurnButton;

    @FXML
    private Button decreaseArmiesToDraftOrFortify;

    @FXML
    private Button increaseArmiesToDraftOrFortify;

    @FXML
    private Label armiesToMoveIndicator;

    @FXML
    private Label fortifyPhaseIndicator;

    @FXML
    private Label attackPhaseIndicator;

    @FXML
    private Label draftPhaseIndicator;

    @FXML
    private Circle playerTurnIndicator;

    @FXML
    private Circle cpuTurnIndicator;

    @FXML
    private Circle armiesToMoveIndicatorGraphic;

    public GameSceneController() {
        if (verbose) System.out.println("Initialized Controller for Scene: Game.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);
        initializeKeyboardListeners();

        // Shadow for root when other menus are displayed on screen.
        ROOT_SHADOW = new Lighting(new Light.Distant());
        ROOT_SHADOW.setBumpInput(new Shadow());

        armiesToMoveIndicator.setText(String.valueOf(Game.ARMIES_TO_DRAFT));

        // Buttons for increasing and increasing armies in a draft
        decreaseArmiesToDraftOrFortify.setOnAction(event -> setAmountOfArmiesForTerritory(-1));
        increaseArmiesToDraftOrFortify.setOnAction(event -> setAmountOfArmiesForTerritory(1));

        // Load in references to board objects.
        territoryToggleButtons = new ArrayList<>(42);
        legalPathIndicators = new ArrayList<>(84);
        for (Node node : boardNodes.getChildren()) {
            if (node instanceof ToggleButton) {
                territoryToggleButtons.add((ToggleButton) node);
            } else if (node instanceof  Line) {
                legalPathIndicators.add((Line) node);
            }
        }

        // Initialize territory ToggleButtons.
        for (ToggleButton button : territoryToggleButtons) {
            button.setOnAction(event -> territoryButtonAction(button));
            button.setBorder(null);
        }

        // Initialize legal-attack-path-indicators.
        for (Line line : legalPathIndicators) {
            line.setVisible(false);
        }

        // Button to go to next turn phase
        nextPhaseOrTurnButton.setOnAction(event -> endTurnPhase());
        nextPhaseOrTurnButton.setDisable(true);

        // Attack command btn
        hideNode(makeAttack);
        makeAttack.setOnAction(event -> handleAttackRequest());

    }

    /**
     * Validates and performs an attack.
     */
    private void handleAttackRequest() {

        try {
            String originTerritoryName = attackOriginControl.getId();
            Territory originTerritory = instance.territories.get(originTerritoryName);
            String targetTerritoryName = attackTargetControl.getId();
            Territory targetTerritory = instance.territories.get(targetTerritoryName);
            instance.performPlayerAttack(originTerritory, targetTerritory);

            // Update GUI for new Territory armies values
            for (ToggleButton toggleButton : territoryToggleButtons) {
                if (toggleButton.getId().equals(targetTerritoryName)) {
//                    ((Label) toggleButton.getGraphic()).setText(String.valueOf(targetTerritory.getNumOfArmies()));
                    toggleButton.setText(String.valueOf(targetTerritory.getNumOfArmies()));
                }
                if (toggleButton.getId().equals(originTerritoryName)) {
//                    ((Label) toggleButton.getGraphic()).setText(String.valueOf(originTerritory.getNumOfArmies()));
                    toggleButton.setText(String.valueOf(originTerritory.getNumOfArmies()));
                }
            }
        } catch (NullPointerException ignored) {
        }

    }

    public void updateTerritoryOwner(String territoryName, Player newOwner) {
        for (ToggleButton toggleButton : territoryToggleButtons) {
            if (toggleButton.getId().equals(territoryName)) {
                if (newOwner instanceof CPU) {
                    toggleButton.setStyle(styleForCpuColor);
                } else {
                    toggleButton.setStyle(styleForPlayerColor);
                }
            }
        }
        resetBoard(true, true);
        attackOriginControl = null;
        attackTargetControl = null;
    }

    /** Synchronizes end of turn-phases with 'Game'. */
    private void endTurnPhase() {
        switch (instance.playerTurnPhase) {
            case DRAFT:

                setupBoardForPlayerTurnPhase(Game.TurnPhase.ATTACK);
                instance.flagEndOfTurnPhase(instance.player, Game.TurnPhase.DRAFT);
                break;

            case ATTACK:

                setupBoardForPlayerTurnPhase(Game.TurnPhase.FORTIFY);
                instance.flagEndOfTurnPhase(instance.player, Game.TurnPhase.ATTACK);
                break;

            case FORTIFY:

                setupBoardForPlayerTurnPhase(Game.TurnPhase.END);
                instance.flagEndOfTurnPhase(instance.player, Game.TurnPhase.FORTIFY);
                break;
        }
        resetBoard(true, true);
    }

    private void setupBoardForPlayerTurnPhase(Game.TurnPhase turnPhase) {

        switch (turnPhase) {

            case DRAFT:

                // Show the amount of armies available for drafting.
                armiesToMoveIndicator.setText(String.valueOf(Game.ARMIES_TO_DRAFT));

                // Enable controls.
                showNode(armyMovementControls);
                showNode(nextPhaseOrTurnButton);
                hideNode(makeAttack);
                break;

            case ATTACK:

                // Prepare GUI controls for ATTACK phase
                hideNode(armyMovementControls);
                showNode(makeAttack);
                break;

            case FORTIFY:

                // Prepare GUI controls for FORTIFY phase
                hideNode(makeAttack);
                showNode(armyMovementControls);
                break;

            case END:

                // Prepare GUI controls for next CPU turn-phase
                hideNode(armyMovementControls);
                hideNode(nextPhaseOrTurnButton);
                playerTurnIndicator.setEffect(null);
                cpuTurnIndicator.setEffect(CURRENT_TURN_OWNER);
                break;

        }

    }

    /** Specifies the action of a TerritoryToggleButton with respect to the current Player-turn-phase. */
    private void territoryButtonAction(ToggleButton button) {
        switch (instance.playerTurnPhase) {
            case DRAFT:
                // Proceed if player selected a Player-owned territory
                if (instance.playerControlsTerritory(instance.territories.get(button.getId()))) {
                    selectTerritoryToggleBtnForDraft(button);
                }
                else {
                    button.setSelected(false);
                }
                break;
            case ATTACK:
                selectTerritoryToggleBtnForAttack(button);
                break;
            case FORTIFY:
                // Proceed if player selected a Player-owned territory
                if (instance.playerControlsTerritory(instance.territories.get(button.getId()))) {
                    selectTerritoryToggleBtnForFortify(button);
                }
                else {
                    button.setSelected(false);
                }
                break;
        }
    }

    public void setEffectForTerritoryToggleButton(Territory territory, Effect effect) {
        for (ToggleButton toggleButton : territoryToggleButtons) {
            if (toggleButton.getId().equals(territory.getName())) {
                toggleButton.setEffect(effect);
            }
        }
    }

    /** Action for when a Player selects a Territory ToggleButton during the DRAFT turn-phase. */
    private void selectTerritoryToggleBtnForDraft(ToggleButton button) {
        resetBoard(false, true);
        button.setEffect(STANDARD_DRAFT_EFFECT);
        draftTerritoryControl = button;
    }

    /** Action for when a Player selects a Territory ToggleButton during the ATTACK turn-phase. */
    private void selectTerritoryToggleBtnForAttack(ToggleButton button) {

        Territory selectedTerritory = instance.territories.get(button.getId());

        // If territory belongs to Player update GUI and flag the territory
        if (instance.playerControlsTerritory(selectedTerritory)) {
            resetBoard(true, true);
            button.setEffect(STANDARD_ATTACK_EFFECT);
            showLegalAttackLinesForTerritory(button.getId());
            attackOriginControl = button;

        // If territory belongs to CPU and an origin of attack has been selected and the territory is a neighbor of the origin of attack...
        } else if (instance.cpuControlsTerritory(selectedTerritory) && attackOriginControl != null && selectedTerritory.isNeighborOf(instance.territories.get(attackOriginControl.getId()))) {

            // Highlight the attack path
            if (attackTargetControl != null) attackTargetControl.setEffect(null);
            showLegalAttackPathFor(attackOriginControl.getId(), button.getId(), true);
            button.setEffect(STANDARD_ATTACK_EFFECT);
            attackTargetControl = button;

        } else {
            resetBoard(true, true);
            attackOriginControl = null;
            attackTargetControl = null;
        }

    }

    public void showLegalAttackPathFor(String attackOriginName, String attackTargetName, boolean resetOtherPathEffects) {
        for (Line line : legalPathIndicators) {
            if (resetOtherPathEffects) {
                line.setEffect(null);
            }
            String lineID = line.getId();
            if (lineID.contains(attackOriginName) && lineID.contains(attackTargetName)) {
                line.setEffect(TARGET_PATH_EFFECT);
            }
        }
    }

    private void selectTerritoryToggleBtnForFortify(ToggleButton button) {

        // If territory belongs to Player update GUI and flag the territory
        resetBoard(false, true);
        button.setEffect(STANDARD_DRAFT_EFFECT);
        fortifyTerritoryControl = button;

    }

    /** Removes all Player customizations from the Game-board. */
    public void resetBoard(boolean lines, boolean toggleButtons) {

        // Hide all other attack-paths
        if (lines) {
            for (Line line : legalPathIndicators) {
                line.setVisible(false);
                line.setEffect(null);
            }
        }

        // Hide all other Glows
        if (toggleButtons) {
            for (ToggleButton toggleButton : territoryToggleButtons) {
                toggleButton.setEffect(null);
            }
        }

    }

    /** Shows all paths that a user can take from a controlled Territory for an attack. */
    private void showLegalAttackLinesForTerritory(String territoryName) {
        for (Line line : legalPathIndicators) {
            if (line.getId().contains(territoryName)) {
                for (Territory territory : instance.cpu.getControlledTerritories()) {
                    // Show path if it links to an enemy-controlled territory
                    if (line.getId().contains(territory.getName())) {
                        line.setVisible(true);
                    }
                }
            }
        }
    }

    public void showLegalCpuAttackLinesForTerritory(String territoryName) {
        for (Line line : legalPathIndicators) {
            if (line.getId().contains(territoryName)) {
                for (Territory territory : instance.player.getControlledTerritories()) {
                    // Show path if it links to an enemy-controlled territory
                    if (line.getId().contains(territory.getName())) {
                        line.setVisible(true);
                    }
                }
            }
        }
    }


    private void initializeKeyboardListeners() {
        primaryScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                instance.requestDisplayForScene(PAUSE_GAME_MENU);
            }
        });
    }

    /**
     * Highlights Labels that tell the Player turnPhase turn phase they are in. Draft: 1; Attack: 2; Fortify: 3.
     */
    public void setHighlightForAttackPhaseIndicator(Game.TurnPhase turnPhase) {

        // Set value
        switch (turnPhase) {
            case DRAFT:
                fortifyPhaseIndicator.setEffect(null);
                draftPhaseIndicator.setEffect(STANDARD_BLOOM_EFFECT);
                break;
            case ATTACK:
                draftPhaseIndicator.setEffect(null);
                attackPhaseIndicator.setEffect(STANDARD_BLOOM_EFFECT);
                break;
            case FORTIFY:
                attackPhaseIndicator.setEffect(null);
                fortifyPhaseIndicator.setEffect(STANDARD_BLOOM_EFFECT);
                break;
        }

    }

    /** Highlights the GUI Shape indicating whose turn it is (Player or CPU). */
    private void setPlayerTurnIndicatorColor(Game.PlayerColor playerColor) {
        playerTurnIndicator.setFill(Color.valueOf(getColorHexForPlayerColor(playerColor)));
    }

    /** Returns a HEX String for the PlayerColor parameter. */
    private String getColorHexForPlayerColor(Game.PlayerColor playerColor) {
        switch (playerColor) {
            case NA_YELLOW:
                return instance.NORTH_AMERICA_HEX;
            case SA_RED:
                return instance.SOUTH_AMERICA_HEX;
            case AF_BROWN:
                return instance.AFRICA_HEX;
            case AS_GREEN:
                return instance.ASIA_HEX;
            case AU_VIOLET:
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

        styleForPlayerColor = "-fx-background-color: #" + getColorHexForPlayerColor(gameState.getPlayer().getColor());
        styleForCpuColor = "-fx-background-color: #6a6f6b";
        Territory territory;

        for (ToggleButton territoryButton : territoryToggleButtons) {
            territory = instance.territories.get(territoryButton.getId());
            if (territory.getOwner() == gameState.getPlayer()) {
                territoryButton.setStyle(styleForPlayerColor);
            } else {
                territoryButton.setStyle(styleForCpuColor);
            }
        }

        for (ToggleButton territoryButton : territoryToggleButtons) {
            territoryButton.setText(String.valueOf(instance.territories.get(territoryButton.getId()).getNumOfArmies()));
        }

    }

    /**
     * Updates the GUI and data for a new amount of armies for a Territory, whether this is done in draft or fortify phase
     */
    private void setAmountOfArmiesForTerritory(int difference) {
        Territory territory;
        int newArmyVal;
        switch (instance.playerTurnPhase) {
            case DRAFT:
                // Fetch territory button to draft for
                territory = instance.territories.get(draftTerritoryControl.getId());
                newArmyVal = territory.getNumOfArmies();

                // Player tries to undo a draft
                if (difference < 0 && Integer.valueOf(armiesToMoveIndicator.getText()) == 0) {
                    newArmyVal -= Game.ARMIES_TO_DRAFT;
                    armiesToMoveIndicator.setText("5");
                    nextPhaseOrTurnButton.setDisable(true);

                //Player tries to draft armies
                } else if (difference > 0 && Integer.valueOf(armiesToMoveIndicator.getText()) == 5) {
                    newArmyVal += Game.ARMIES_TO_DRAFT;
                    armiesToMoveIndicator.setText("0");
                    nextPhaseOrTurnButton.setDisable(false);
                }

                // Update Territory GUI and data
                instance.setNumOfArmiesForTerritory(territory, newArmyVal);
                draftTerritoryControl.setText(String.valueOf(territory.getNumOfArmies()));
                break;

            case FORTIFY:
                // Fetch territory button to fortify for
                territory = instance.territories.get(fortifyTerritoryControl.getId());
                newArmyVal = territory.getNumOfArmies();

                // Player tries to remove an army from a territory
                if (difference < 0 && territory.getNumOfArmies() > 1) {
                    newArmyVal -= 1;
                    armiesToMoveForFortification += 1;   // Var indicating how many armies are being moved from a territory
                    armiesToMoveIndicator.setText(String.valueOf(armiesToMoveForFortification));    // Update GUI indicator

                // Player tries to add an army to a territory
                } else if (difference > 0 && armiesToMoveForFortification > 0){
                    newArmyVal += 1;
                    armiesToMoveForFortification -= 1;
                    armiesToMoveIndicator.setText(String.valueOf(armiesToMoveForFortification));
                }

                // Prevent player from ending turn if they have pending armies to move
                if (armiesToMoveForFortification == 0) {
                    nextPhaseOrTurnButton.setDisable(false);
                } else {
                    nextPhaseOrTurnButton.setDisable(true);
                }

                // Update Territory GUI and data
                instance.setNumOfArmiesForTerritory(territory, newArmyVal);
                fortifyTerritoryControl.setText(String.valueOf(territory.getNumOfArmies()));
        }
    }

    /**
     * Updates the given Territory's ToggleButton to show its correct number of armies.
     */
    public void resetAmountOfArmiesForTerritory(Territory territory) {
        for (ToggleButton territoryToggleButton : territoryToggleButtons) {
            if (territoryToggleButton.getId().equals(territory.getName())) {
                territoryToggleButton.setText(String.valueOf(territory.getNumOfArmies()));
            }
        }
    }

    /**
     * Updates all Territory-ToggleButtons to show the correct number of armies contained in each Territory.
     */
    public void resetAmountOfArmiesForTerritories() {
        for (ToggleButton territoryToggleButton : territoryToggleButtons) {
            territoryToggleButton.setText(String.valueOf(instance.territories.get(territoryToggleButton.getId()).getNumOfArmies()));
        }
    }

    /** Sets board for new Player turn (all GUI stuff begins here) */
    public void setupBoardForNewPlayerTurn() {

        // Highlight graphics that indicate the Player's color choice
        setPlayerTurnIndicatorColor(instance.player.getColor());
        armiesToMoveIndicatorGraphic.setFill(Color.valueOf(getColorHexForPlayerColor(instance.player.getColor())));

        setupBoardForPlayerTurnPhase(instance.playerTurnPhase);

        // Highlight the turn-indicator.
        playerTurnIndicator.setEffect(CURRENT_TURN_OWNER);

    }

    public void setupBoardForNewCpuTurn() {

        // Prepare board.
        setupBoardForPlayerTurnPhase(Game.TurnPhase.END);

    }

    public void enableRootShadow() {
        root.setEffect(ROOT_SHADOW);
    }

    public void disableRootShadow() {
        root.setEffect(null);
    }

}
