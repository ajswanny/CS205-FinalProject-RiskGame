package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable  {

    /* Fields */
    private risk.Game instance;

    Game.PlayerColor color;

    protected ArrayList<Territory> controlledTerritories;

    int numOfTotalArmies;

    public Player() {

    }

    public Player(Game.PlayerColor color, int numOfTotalArmies) {
        this.color = color;
        this.numOfTotalArmies = numOfTotalArmies;
    }

    /* Getters */
    public ArrayList<Territory> getControlledTerritories() {
        return controlledTerritories;
    }

    public Game.PlayerColor getColor() {
        return color;
    }
}
