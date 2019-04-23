package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable  {

    /* Fields */
    private risk.Game instance;
    Game.PlayerColor color;
    protected ArrayList<Territory> controlledTerritories;
    int numOfTotalArmies;
    private int numOfNewArmies;

    Player() {
        controlledTerritories = new ArrayList<>();
    }

    /* Constructors */
    public Player(Game.PlayerColor color, int numOfTotalArmies) {
        this();
        this.color = color;
        this.numOfTotalArmies = numOfTotalArmies;
    }

    /* Methods */
    public void addNewControlledTerritory(Territory territory) {
        controlledTerritories.add(territory);
    }

    public void removeControlledTerritory(Territory territory) {
        controlledTerritories.remove(territory);
    }

    public void deployArmies(Territory territory, int numArmiesToAdd){
        int armiesLeft = getNumOfNewArmies();
        if (numArmiesToAdd <= getNumOfNewArmies()){
            territory.numOfArmies += numArmiesToAdd;
            armiesLeft -= numArmiesToAdd;
            setNumOfNewArmies(armiesLeft);
        }
    }

    /* Getters */
    public ArrayList<Territory> getControlledTerritories() {
        return controlledTerritories;
    }

    public Game.PlayerColor getColor() {
        return color;
    }

    public void setNumOfNewArmies(int numOfNewArmies) {
        this.numOfNewArmies = numOfNewArmies;
    }

    public int getNumOfNewArmies(){
        return numOfNewArmies;
    }

}
