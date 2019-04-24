package risk.java;

import risk.Game;

import java.util.ArrayList;

public class Territory {

    /* Fields */
    int numOfArmies;
    private ArrayList<Territory> neighbors;
    Player owner;
    private String name;
    private Continent continent;
    private int continentNumId;

    /* Constructors */
    /** Default constructor. */
    public Territory(String name, int continentNumId) {
        this.name = name;
        neighbors = new ArrayList<>();

        switch (continentNumId) {
            case 4:
                continent = Continent.NORTH_AMERICA;
                break;
            case 5:
                continent = Continent.SOUTH_AMERICA;
                break;
            case 2:
                continent = Continent.EUROPE;
                break;
            case 3:
                continent = Continent.AFRICA;
                break;
            case 1:
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

    void moveArmies(Territory territoryToTransferTo, int numToTransfer) {
        if (this.numOfArmies > numToTransfer) {
            territoryToTransferTo.numOfArmies += numToTransfer;
            this.numOfArmies -= numToTransfer;
        }
    }

    public void addArmies(int numToAdd){
        this.numOfArmies += numToAdd;
    }

    public void removeArmies(int numToRemove){
        this.numOfArmies -= numToRemove;
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
                moveArmies(toAttack, 1);
                return true;
            }
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

    public ArrayList<Territory> getNeighbors() {
        return neighbors;
    }

    public int getContinentID() {
        return continentNumId;
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
