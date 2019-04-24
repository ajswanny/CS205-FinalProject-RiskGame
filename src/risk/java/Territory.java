package risk.java;

import java.util.ArrayList;

public class Territory {

    /* Fields */
    int numOfArmies;
    private ArrayList<Territory> neighbors;
    Player owner;
    private String name;

    /* Constructors */
    /** Default constructor. */
    public Territory(String name) {
        this.name = name;
        neighbors = new ArrayList<>();
    }

    /* Methods */
    void moveArmies(Territory territoryToTransferTo, int numToTransfer) {
        if (this.numOfArmies > numToTransfer) {
            territoryToTransferTo.numOfArmies += numToTransfer;
            this.numOfArmies -= numToTransfer;
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

    ArrayList<Territory> getNeighbors() {
        return neighbors;
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
