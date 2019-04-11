package risk.java;

import risk.Continent;

import java.util.ArrayList;
import java.util.Arrays;

public class Territory {

    /* Fields */
    int numOfArmies;
    ArrayList<Territory> neighbors;
    Player owner;
    String name;
    Continent continent;

    /* Constructors */
    /** Default constructor. */
    public Territory(String name, int continentNumId) {
        this.name = name;
        neighbors = new ArrayList<>();

        switch (continentNumId) {
            case 1:
                continent = Continent.NORTH_AMERICA;
                break;
            case 2:
                continent = Continent.SOUTH_AMERICA;
                break;
            case 3:
                continent = Continent.EUROPE;
                break;
            case 4:
                continent = Continent.AFRICA;
                break;
            case 5:
                continent = Continent.ASIA;
                break;
            case 6:
                continent = Continent.AUSTRALIA;
                break;
        }
    }

    /* Setters */
    /** Sets this territories neighbors to the ones given in the ArrayList parameter. */
    public void setNeighbors(ArrayList<Territory> neighbors) {
        if (this.neighbors.isEmpty()) {
            this.neighbors = new ArrayList<>(neighbors.size());
        }
        this.neighbors.addAll(neighbors);
    }

}
