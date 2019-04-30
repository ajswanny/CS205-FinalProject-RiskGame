package risk.java;

import risk.Game;

import java.io.Serializable;

/**
 * Stores data about a game state: the Territories, their deployed armies, their owners, and the Player's active turn
 * phase.
 */
public class GameState implements Serializable {

    /* Fields */
    private final Player player;
    private final CPU cpu;
    private Game.TurnPhase playerTurnPhase;

    /* Constructor */
    public GameState(Player player, CPU cpu, Game.TurnPhase playerTurnPhase) {
        this.player = player;
        this.cpu = cpu;
        this.playerTurnPhase = playerTurnPhase;
    }

    /* Getters */
    public Player getPlayer() {
        return player;
    }

    public CPU getCpu() {
        return cpu;
    }

    public Game.TurnPhase getPlayerTurnPhase() {
        return playerTurnPhase;
    }

    public void setPlayerTurnPhase(Game.TurnPhase playerTurnPhase) {
        this.playerTurnPhase = playerTurnPhase;
    }
}
