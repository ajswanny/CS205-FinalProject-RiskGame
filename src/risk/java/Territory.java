package risk.java;

import risk.Continent;

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

    /* Getters */
    public String getName() {
        return name;
    }
    /* Setters */
    /** Sets this territories neighbors to the ones given in the ArrayList parameter. */
    public void setNeighbors(ArrayList<Territory> neighbors) {
        if (this.neighbors.isEmpty()) {
            this.neighbors = new ArrayList<>(neighbors.size());
        }
        this.neighbors.addAll(neighbors);
    }

    public void moveArmies(Territory territoryToTranferTo, int numToTransfer){
        if (this.numOfArmies > numToTransfer) {
            territoryToTranferTo.numOfArmies += numToTransfer;
            this.numOfArmies -= numToTransfer;
        }
    }

    public void addArmies(int numToAdd){
        this.numOfArmies += numToAdd;
    }

    public void removeArmies(int numToRemove){
        this.numOfArmies -= numToRemove;
    }

    public void attack(Territory toAttack, int selfRollValue, int enemyRollValue){
        if (this.numOfArmies > 1){
          if (selfRollValue > enemyRollValue){
              toAttack.numOfArmies -= 1;
          } else {
              this.numOfArmies -= 1;
          }

          if (toAttack.numOfArmies < 1){
              moveArmies(toAttack,1);
          }
        }
    }
}
