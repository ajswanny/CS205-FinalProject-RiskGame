package risk.java;

import java.util.ArrayList;

public class Player {
    private risk.Game instance;
    private ArrayList<Territory> territories;
    private int turnValue;
    private int numOfArmies;
    private int numOfNewArmies;
    private boolean endOfTurn;

    public void setNumOfNewArmies(int numOfNewArmies) {
        this.numOfNewArmies = numOfNewArmies;
    }

    public int getNumOfNewArmies(){
        return this.numOfNewArmies;
    }

    public void setEndOfTurn(boolean end){
        this.endOfTurn = end;
    }

    public boolean getEndOfTurn(){
        return this.endOfTurn;
    }

    public void deployArmies(Territory territory, int numArmiesToAdd){
        int armiesLeft = getNumOfNewArmies();
        if (numArmiesToAdd <= getNumOfNewArmies()){
            territory.numOfArmies += numArmiesToAdd;
            armiesLeft -= numArmiesToAdd;
            setNumOfNewArmies(armiesLeft);
        }
    }
}
