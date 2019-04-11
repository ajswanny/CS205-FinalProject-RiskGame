package risk.java;

import java.util.ArrayList;
import java.util.Arrays;

public class Territory {

    /* Fields */
    int numOfArmies;
    ArrayList<Territory> neighbors;
    Player owner;
    String name;
    String continent;

    /* Constructors */
    /** Default constructor. */
    public Territory(String name) {
        this.name = name;
    }

    /* Methods */
    /** Returns a new Territory with the specified name. */
    public static Territory withName(String name) {
        return new Territory(name);
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
