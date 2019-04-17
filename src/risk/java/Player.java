package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable  {

    /* Fields */
    private risk.Game instance;

    Game.PlayerColor color;

    private ArrayList<Territory> controlledTerritories;

    int numOfTotalArmies;

    Player() {
        controlledTerritories = new ArrayList<>();
    }

    /* Constructors */
    public Player(Game.PlayerColor color, int numOfTotalArmies) {
        this();
        this.color = color;
        this.numOfTotalArmies = numOfTotalArmies;
    }

    /* Methods */
    public void addNewControlledTerritory(Territory territory) {
        controlledTerritories.add(territory);
    }

    public void removeControlledTerritory(Territory territory) {
        controlledTerritories.remove(territory);
    }

    /* Getters */
    public ArrayList<Territory> getControlledTerritories() {
        return controlledTerritories;
    }

    public Game.PlayerColor getColor() {
        return color;
    }

}
