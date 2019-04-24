package risk.java;

import java.io.Serializable;

public class GameState implements Serializable {

    /* Fields */
    private Player player;
    private CPU cpu;

    /* Default Constructor */
    public GameState(Player player, CPU cpu) {
        this.player = player;
        this.cpu = cpu;
    }

    /* Getters */
    public Player getPlayer() {
        return player;
    }

}
