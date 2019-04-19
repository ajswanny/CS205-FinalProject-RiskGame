package risk.java;

import java.util.ArrayList;

public class Territory {

    /* Fields */
    int numOfArmies;
    private ArrayList<Territory> neighbors;
    Player owner;
    private String name;
    private Continent continent;

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

    /* Methods */
    public boolean hasNeighbor(Territory territory) {
        return neighbors.contains(territory);
    }

    /* Getters */
    public String getName() {
        return name;
    }

    public int getNumOfArmies() {
        return numOfArmies;
    }

    public Player getOwner() {
        return owner;
    }

    /* Setters */
    /** Sets this territories neighbors to the ones given in the ArrayList parameter. */
    public void setNeighbors(ArrayList<Territory> neighbors) {
        if (this.neighbors.isEmpty()) {
            this.neighbors = new ArrayList<>(neighbors.size());
        }
        this.neighbors.addAll(neighbors);
    }

    public void setNumOfArmies(int numOfArmies) {
        this.numOfArmies = numOfArmies;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

}
