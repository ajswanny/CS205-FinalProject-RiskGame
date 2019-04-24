package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable  {

    Game.PlayerColor color;
    ArrayList<Territory> controlledTerritories;

    Player() {
        controlledTerritories = new ArrayList<>();
    }

    /* Constructors */
    public Player(Game.PlayerColor color) {
        this();
        this.color = color;
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
