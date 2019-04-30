package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Player object. This Class contains all data for a user of Game.
 */
public class Player implements Serializable  {

    /* Fields */
    /** Selectable color for this player. */
    Game.PlayerColor color;

    ArrayList<Territory> controlledTerritories;


    /* Constructors */
    Player() {
        controlledTerritories = new ArrayList<>();
    }

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
