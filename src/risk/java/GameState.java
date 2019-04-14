package risk.java;

import java.io.Serializable;

public class GameState implements Serializable {

    public Player player;
    public CPU cpu;

    public GameState(Player player, CPU cpu) {
        this.player = player;
        this.cpu = cpu;
    }
}
