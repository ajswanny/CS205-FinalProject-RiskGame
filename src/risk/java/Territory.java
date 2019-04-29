package risk.java;

import java.io.Serializable;
import java.util.ArrayList;

public class Territory implements Serializable {

    /* Fields */
    int numOfArmies;
    private ArrayList<Territory> neighbors;
    Player owner;
    private final String name;
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
    private void moveArmyTo(Territory territoryToTransferTo) {
        if (this.numOfArmies > 1) {
            territoryToTransferTo.numOfArmies += 1;
            this.numOfArmies -= 1;
        }
    }

    public void addArmies(int numToAdd){
        this.numOfArmies += numToAdd;
    }

    /**
     * Performs an attack from this Territory to the given target Territory.
     */
    public boolean attack(Territory toAttack, int selfRollValue, int enemyRollValue){
        if (this.numOfArmies > 1) {
            if (selfRollValue > enemyRollValue) {
                toAttack.numOfArmies -= 1;
            } else {
                this.numOfArmies -= 1;
            }

            if (toAttack.numOfArmies < 1) {
                moveArmyTo(toAttack);
                return true;
            }
        }
        return false;
    }

    public boolean isNeighborOf(Territory query) {
        for (Territory neighbor : neighbors) {
            if (neighbor == query) return true;
        }
        return false;
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

    ArrayList<Territory> getNeighbors() {
        return neighbors;
    }

    public Continent getContinent() {
        return continent;
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
