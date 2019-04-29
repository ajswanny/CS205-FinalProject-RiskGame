package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable  {

    Game.PlayerColor color;
    ArrayList<Territory> controlledTerritories;
    int draftBonus;


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

    public void getDraftBonus() {
        int numOfNorthAmericaTerritories = 0;
        int numOfSouthAmericaTerritories = 0;
        int numOfEuropeTerritories = 0;
        int numOfAfricaTerritories = 0;
        int numOfAsiaTerritories = 0;
        int numOfAustraliaTerritories = 0;
        for (Territory territory : controlledTerritories) {
            switch (territory.getContinent()) {
                case NORTH_AMERICA:
                    numOfNorthAmericaTerritories++;
                    break;
                case SOUTH_AMERICA:
                    numOfSouthAmericaTerritories++;
                    break;
                case EUROPE:
                    numOfEuropeTerritories++;
                    break;
                case AFRICA:
                    numOfAfricaTerritories++;
                    break;
                case ASIA:
                    numOfAsiaTerritories++;
                    break;
                case AUSTRALIA:
                    numOfAustraliaTerritories++;
                    break;
            }
        }
    }

}
