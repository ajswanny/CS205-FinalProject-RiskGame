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
 * FXML Controller for the Game Scene. This controller implements all GUI elements of the Game.
 *
 * @FXMLFile risk.fxml.GameSceneController.fxml
 */
public class GameSceneController extends RiskSceneController {

    /* Fields */
    /**
     * Collection of all ToggleButtons for Territories. These ToggleButtons implement Drafting, Attacking, and
     * Fortifying of any Territories.
     */
    private ArrayList<ToggleButton> territoryToggleButtons;

    /**
     * Collection of Lines that show the Player which Territories they can attack from any given origin.
     */
    private ArrayList<Line> legalPathIndicators;

    /**
     * Effects for Player and CPU Territory selections.
     */
    public final DropShadow STANDARD_ATTACK_EFFECT = new DropShadow(BlurType.GAUSSIAN, Color.valueOf("#A80B0A"), 30, 0.5, 0, 0);
    public final DropShadow STANDARD_DRAFT_EFFECT = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 30, 0.5, 0, 0);
    private final DropShadow CURRENT_TURN_OWNER = new DropShadow();
    private final Glow TARGET_PATH_EFFECT = new Glow(0.5);
    private final Bloom STANDARD_BLOOM_EFFECT = new Bloom(0.3);

    /**
     * Effect for the Game Scene to be used when the Game-Pause dialog is presented.
     */
    private Lighting ROOT_SHADOW;

    /**
     * Flags for user Territory selections.
     */
    private ToggleButton draftTerritoryControl, attackOriginControl, attackTargetControl, fortifyTerritoryControl;

    /**
     * CSS style for the Player-selected color and the CPU's color.
     */
    private String styleForPlayerColor, styleForCpuColor;

    private int armiesToMoveForFortification = 0;

    /**
     * All Territory-representation-related Nodes.
     */
    @FXML
    private Group boardNodes;

    /**
     * Controls for Drafting, Attacking, and Fortifying for the Player.
     */
    @FXML
    private Group armyMovementControls;

    /**
     * Control for making Attacks on enemy Territories.
     */
    @FXML
    private Button makeAttack;

    /**
     * Control for ending the Player's current turn-phase.
     */
    @FXML
    private Button nextPhaseOrTurn;

    /**
     * Control for decreasing armies fortified.
     */
    @FXML
    private Button decreaseArmiesToDraftOrFortify;

    /**
     * Control for increasing armies fortified.
     */
    @FXML
    private Button increaseArmiesToDraftOrFortify;

    /**
     * Label indicating all Territories in movement during the Player's Fortify phase.
     */
    @FXML
    private Label armiesToMoveIndicator;

    /**
     * Labels indicating the Player or CPU's current turn-phase.
     */
    @FXML
    private Label fortifyPhaseIndicator;
    @FXML
    private Label attackPhaseIndicator;
    @FXML
    private Label draftPhaseIndicator;

    /**
     * Graphic showing the Player's selected color and whether or not it is their turn.
     */
    @FXML
    private Circle playerTurnIndicator;

    /**
     * Graphic showing the CPU's color and whether or not it is their turn.
     */
    @FXML
    private Circle cpuTurnIndicator;

    /**
     * Graphic for 'armiesToMoveIndicator'.
     */
    @FXML
    private Circle armiesToMoveIndicatorGraphic;


    /* Constructor */
    public GameSceneController() {
        if (verbose) System.out.println("Initialized Controller for Scene: Game.");
    }


    /* Methods */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        super.initialize(location, resources);

        // Initialize ESCAPE keyboard listener.
        primaryScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                instance.requestDisplayForScene(PAUSE_GAME_MENU);
            }
        });

        // Shadow for root when other menus are displayed on screen.
        ROOT_SHADOW = new Lighting(new Light.Distant());
        ROOT_SHADOW.setBumpInput(new Shadow());

        // Buttons for increasing and decreasing armies in a draft.
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

        // Initialize Territory representations as ToggleButtons.
        for (ToggleButton button : territoryToggleButtons) {
            button.setOnAction(event -> territoryButtonAction(button));
            button.setBorder(null);
        }

        // Initialize legal attack-path-indicators.
        for (Line line : legalPathIndicators) {
            line.setVisible(false);
        }

        // Button to go to next turn phase.
        nextPhaseOrTurn.setOnAction(event -> endTurnPhase());

        // Attack command Button.
        hideNode(makeAttack);
        makeAttack.setOnAction(event -> handleAttackRequest());

    }

    /**
     * Validates and performs an attack.
     */
    private void handleAttackRequest() {

        try {

            // Perform the attack.
            String originTerritoryName = attackOriginControl.getId();
            Territory originTerritory = instance.territories.get(originTerritoryName);
            String targetTerritoryName = attackTargetControl.getId();
            Territory targetTerritory = instance.territories.get(targetTerritoryName);
            instance.performPlayerAttack(originTerritory, targetTerritory);

            // Update GUI for new Territory armies values and activate sound effects.
            instance.playAttackSfx();
            for (ToggleButton toggleButton : territoryToggleButtons) {
                if (toggleButton.getId().equals(targetTerritoryName)) {
                    toggleButton.setText(String.valueOf(targetTerritory.getNumOfArmies()));
                }
                if (toggleButton.getId().equals(originTerritoryName)) {
                    toggleButton.setText(String.valueOf(originTerritory.getNumOfArmies()));
                }
            }

        } catch (NullPointerException e) {
            if (verbose) System.out.println("Player attempted illegal attack move.");
        }

    }

    /**
     * Synchronizes end of turn-phases with 'Game'.
     */
    private void endTurnPhase() {

        switch (instance.playerTurnPhase) {
            case DRAFT:
                setupBoardForPlayerTurnPhase(Game.TurnPhase.ATTACK);
                instance.flagEndOfTurnPhase(Game.TurnPhase.DRAFT);
                break;

            case ATTACK:
                setupBoardForPlayerTurnPhase(Game.TurnPhase.FORTIFY);
                instance.flagEndOfTurnPhase(Game.TurnPhase.ATTACK);
                break;

            case FORTIFY:
                setupBoardForPlayerTurnPhase(Game.TurnPhase.END);
                instance.flagEndOfTurnPhase(Game.TurnPhase.FORTIFY);
                break;
        }
        resetBoard(true, true);

    }

    /**
     * Updates GUI for new turn-phase.
     * @param turnPhase the upcoming turn-phase.
     */
    private void setupBoardForPlayerTurnPhase(Game.TurnPhase turnPhase) {

        switch (turnPhase) {

            case DRAFT:
                // Show the amount of armies available for drafting.
                armiesToMoveIndicator.setText(String.valueOf(Game.ARMIES_TO_DRAFT));

                // Update necessary controls.
                cpuTurnIndicator.setEffect(null);
                showNode(armyMovementControls);
                showNode(nextPhaseOrTurn);
                hideNode(makeAttack);
                break;

            case ATTACK:
                // Update necessary controls.
                hideNode(armyMovementControls);
                showNode(makeAttack);
                showNode(nextPhaseOrTurn);
                break;

            case FORTIFY:
                // Update necessary controls.
                armiesToMoveIndicator.setText("0");
                hideNode(makeAttack);
                showNode(armyMovementControls);
                showNode(nextPhaseOrTurn);
                break;

            case END:
                // Prepare GUI controls for next CPU turn-phase.
                hideNode(armyMovementControls);
                hideNode(nextPhaseOrTurn);
                playerTurnIndicator.setEffect(null);
                cpuTurnIndicator.setEffect(CURRENT_TURN_OWNER);
                break;

        }

    }

    /**
     * Specifies the action of a Territory-ToggleButton with respect to the current Player-turn-phase.
     */
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

    /**
     * Action for when a Player selects a Territory-ToggleButton during the Draft turn-phase.
     */
    private void selectTerritoryToggleBtnForDraft(ToggleButton button) {
        resetBoard(false, true);
        button.setEffect(STANDARD_DRAFT_EFFECT);
        draftTerritoryControl = button;
    }

    /**
     * Action for when a Player selects a Territory ToggleButton during the Attack turn-phase.
     */
    private void selectTerritoryToggleBtnForAttack(ToggleButton button) {

        Territory selectedTerritory = instance.territories.get(button.getId());

        // If territory belongs to Player update GUI and flag the territory
        if (instance.playerControlsTerritory(selectedTerritory)) {
            resetBoard(true, true);
            button.setEffect(STANDARD_ATTACK_EFFECT);
            showLegalAttackLinesForTerritory(button.getId());
            attackOriginControl = button;

        // If territory belongs to CPU and an origin of attack has been selected and the territory is a neighbor of
        // the origin of attack...
        } else if (
                instance.cpuControlsTerritory(selectedTerritory) &&
                attackOriginControl != null                      &&
                selectedTerritory.isNeighborOf(instance.territories.get(attackOriginControl.getId()))
        ) {

            // Highlight the attack path.
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

    /**
     * Action for when a Player selects a Territory ToggleButton during the Fortify turn-phase.
     */
    private void selectTerritoryToggleBtnForFortify(ToggleButton button) {

        // If territory belongs to Player update GUI and flag the territory for modification.
        resetBoard(false, true);
        button.setEffect(STANDARD_DRAFT_EFFECT);
        fortifyTerritoryControl = button;

    }

    /**
     * Shows an attack path for the specified Territories.
     * @param attackOriginName origin of attack.
     * @param attackTargetName target of attack.
     * @param resetOtherPathEffects whether this method should remove other attack-path highlights.
     */
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

    /**
     * Removes all desired Player customizations from the Game-board.
     * @param lines if attack-path-lines should be hidden.
     * @param toggleButtons if ToggleButton highlights should be removed.
     */
    public void resetBoard(boolean lines, boolean toggleButtons) {

        // Hide all other attack-paths.
        if (lines) {
            for (Line line : legalPathIndicators) {
                line.setVisible(false);
                line.setEffect(null);
            }
        }

        // Hide all other Territory-ToggleButton Effects.
        if (toggleButtons) {
            for (ToggleButton toggleButton : territoryToggleButtons) {
                toggleButton.setEffect(null);
            }
        }

    }

    /**
     * Shows all paths that a Player can use from a controlled Territory for an attack.
     */
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

    /**
     * Shows all paths that the CPU can use from a controlled Territory for an attack.
     */
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

    /**
     * Highlights Labels that indicate which turn-phase is active for the Player or the CPU.
     */
    public void setHighlightForAttackPhaseIndicator(Game.TurnPhase turnPhase) {

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

    /**
     * Returns a HEX String for the PlayerColor parameter.
     */
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
                // Return white.
                return "#FFFFFF";
        }

    }


    /* Setters */
    /**
     * Gives an Effect to the specified Territory's ToggleButton.
     * @param territory the Territory to highlight on the GUI.
     * @param effect the desired Effect.
     */
    public void setEffectForTerritoryToggleButton(Territory territory, Effect effect) {
        for (ToggleButton toggleButton : territoryToggleButtons) {
            if (toggleButton.getId().equals(territory.getName())) {
                toggleButton.setEffect(effect);
            }
        }
    }

    /**
     * Highlights the GUI Shape indicating whose turn it is (Player or CPU).
     */
    private void setPlayerTurnIndicatorColor(Game.PlayerColor playerColor) {
        playerTurnIndicator.setFill(Color.valueOf(getColorHexForPlayerColor(playerColor)));
    }


    /**
     * Updates the GUI representation of a Territory's owner.
     * @param territoryName the Territory to update.
     * @param newOwner the new owner of the Territory.
     */
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

    /**
     * Sets the game state: defines all Territory-ToggleButtons, setting their color with respect to the Player that
     * controls that territory and setting their text to display the amount of armies present in Territories.
     */
    public void setGameState(GameState gameState) {

        // Fetch the color of the Player and the CPU.
        styleForPlayerColor = "-fx-background-color: #" + getColorHexForPlayerColor(gameState.getPlayer().getColor());
        styleForCpuColor = "-fx-background-color: #6a6f6b";
        Territory territory;

        // Define Territory army values and owners.
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
     * Updates the GUI and data for a new amount of armies for a Territory, whether this is done in the Draft or
     * Fortify phase.
     */
    private void setAmountOfArmiesForTerritory(int difference) {

        Territory territory;
        int newArmyVal;

        switch (instance.playerTurnPhase) {

            case DRAFT:
                // Fetch territory button to draft for.
                territory = instance.territories.get(draftTerritoryControl.getId());
                newArmyVal = territory.getNumOfArmies();

                // Player tries to undo a draft.
                if (difference < 0 && Integer.valueOf(armiesToMoveIndicator.getText()) == 0) {
                    newArmyVal -= Game.ARMIES_TO_DRAFT;
                    armiesToMoveIndicator.setText("5");
                    nextPhaseOrTurn.setDisable(true);

                //Player tries to draft armies.
                } else if (difference > 0 && Integer.valueOf(armiesToMoveIndicator.getText()) == 5) {
                    newArmyVal += Game.ARMIES_TO_DRAFT;
                    armiesToMoveIndicator.setText("0");
                    nextPhaseOrTurn.setDisable(false);
                }

                // Update Territory GUI and data.
                instance.setNumOfArmiesForTerritory(territory, newArmyVal);
                draftTerritoryControl.setText(String.valueOf(territory.getNumOfArmies()));
                break;

            case FORTIFY:
                // Fetch Territory ToggleButton for fortification.
                territory = instance.territories.get(fortifyTerritoryControl.getId());
                newArmyVal = territory.getNumOfArmies();

                // Player tries to remove an army from a Territory.
                if (difference < 0 && territory.getNumOfArmies() > 1) {
                    newArmyVal -= 1;

                    // Var indicating how many armies are being moved from a Territory.
                    armiesToMoveForFortification += 1;

                    // Update GUI indicator.
                    armiesToMoveIndicator.setText(String.valueOf(armiesToMoveForFortification));

                // Player tries to add an army to a Territory.
                } else if (difference > 0 && armiesToMoveForFortification > 0){
                    newArmyVal += 1;
                    armiesToMoveForFortification -= 1;
                    armiesToMoveIndicator.setText(String.valueOf(armiesToMoveForFortification));
                }

                // Prevent Player from ending turn if they have pending armies to move.
                if (armiesToMoveForFortification == 0) {
                    nextPhaseOrTurn.setDisable(false);
                } else {
                    nextPhaseOrTurn.setDisable(true);
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

    /**
     * Sets board for new Player turn. This is Game's main access point for a new Player turn.
     */
    public void setupBoardForNewPlayerTurn() {

        // Highlight graphics that indicate the Player's color choice.
        setPlayerTurnIndicatorColor(instance.player.getColor());
        armiesToMoveIndicatorGraphic.setFill(Color.valueOf(getColorHexForPlayerColor(instance.player.getColor())));

        // Setup controls with the Player's turn-phase.
        setupBoardForPlayerTurnPhase(instance.playerTurnPhase);

        // Highlight the turn-indicator.
        playerTurnIndicator.setEffect(CURRENT_TURN_OWNER);

    }

    /**
     * Sets-up the board GUI for a new CPU turn. This is Game's main access point for a new CPU turn.
     */
    public void setupBoardForNewCpuTurn() {

        // Prepare board controls.
        setupBoardForPlayerTurnPhase(Game.TurnPhase.END);

    }

    /**
     * Game's main access point to turn on the GameScene's root-node Effect.
     */
    public void enableRootShadow() {
        root.setEffect(ROOT_SHADOW);
    }

    /**
     * Game's main access point to turn off the GameScene's root-node Effect.
     */
    public void disableRootShadow() {
        root.setEffect(null);
    }

}
