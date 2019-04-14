package risk.java;

import risk.Game;

import java.io.Serializable;

public class CPU extends Player implements Serializable {

    private String about;
    private String aboutTheOneMove;

    public CPU(int numOfTotalArmies) {
        this.color = Game.PlayerColor.EUROPE;
        this.numOfTotalArmies = numOfTotalArmies;
    }

}
