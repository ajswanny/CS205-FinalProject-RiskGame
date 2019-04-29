package risk.java;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representation of a Territory on the game-board-map. Contains data about the amount of armies deployed in this
 * Territory, the owner of the Territory, and this Territory's neighboring Territories.
 */
public class Territory implements Serializable {

    /* Fields */
    int numOfArmies;
    private ArrayList<Territory> neighboringTerritories;
    Player owner;
    private final String name;

    /* Constructors */
    public Territory(String name) {
        this.name = name;
        neighboringTerritories = new ArrayList<>();
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
    public boolean attack(Territory attackTarget, int selfRollValue, int enemyRollValue){
        if (this.numOfArmies > 1) {
            if (selfRollValue > enemyRollValue) {
                attackTarget.numOfArmies -= 1;
            } else {
                this.numOfArmies -= 1;
            }

            if (attackTarget.numOfArmies < 1) {
                moveArmyTo(attackTarget);
                return true;
            }
        }
        return false;
    }

    public boolean isNeighborOf(Territory query) {
        for (Territory neighbor : neighboringTerritories) {
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

    ArrayList<Territory> getNeighboringTerritories() {
        return neighboringTerritories;
    }

    /* Setters */
    /**
     * Sets all neighboring Territories to the ones given in the ArrayList parameter.
     */
    public void setNeighboringTerritories(ArrayList<Territory> neighboringTerritories) {
        if (this.neighboringTerritories.isEmpty()) {
            this.neighboringTerritories = new ArrayList<>(neighboringTerritories.size());
        }
        this.neighboringTerritories.addAll(neighboringTerritories);
    }

    public void setNumOfArmies(int numOfArmies) {
        this.numOfArmies = numOfArmies;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

}
