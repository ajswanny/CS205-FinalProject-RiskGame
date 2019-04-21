package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class CPU extends Player implements Serializable {

    private String about;
    private String aboutTheOneMove;

    public CPU(int numOfTotalArmies) {
        this.color = Game.PlayerColor.EUROPE;
        this.numOfTotalArmies = numOfTotalArmies;
    }

    public void CpuPlaceArmies(){
        ArrayList<Territory> tempArr = getControlledTerritories();
        int largestArmyIndex = 0;
        int largestArmy = 0;
        for (int i = 0; i < tempArr.size(); i++) {
            if (tempArr.get(i).numOfArmies > largestArmy){
                largestArmy = tempArr.get(i).numOfArmies;
                largestArmyIndex = i;
            }
        }

        int armiesToAdd = this.getNumOfNewArmies();

        this.deployArmies(getControlledTerritories().get(largestArmyIndex), armiesToAdd);
    }

    public void CpuAttack(int myRoll, int enemyRoll){
        ArrayList<Territory> tempArr = getControlledTerritories();
        int biggestAdvantage = -10000;
        Territory from = null;
        Territory to = null;
        for (int i = 0; i < tempArr.size(); i++) {
            Territory currentFrom = tempArr.get(i);
            ArrayList<Territory> currentNeighbors = currentFrom.getNeighbors();
            for (int j = 0; j < currentNeighbors.size(); j++) {
                if (currentFrom.owner != currentNeighbors.get(j).owner) {
                    if ((currentFrom.numOfArmies - currentNeighbors.get(j).numOfArmies) > biggestAdvantage) {
                        biggestAdvantage = (currentFrom.numOfArmies - currentNeighbors.get(j).numOfArmies);
                        from = currentFrom;
                        to = currentNeighbors.get(j);
                    }
                }
            }
        }
        from.attack(to, myRoll, enemyRoll);
    }

    public void theOneMove(){
//        Dice roll goes here !!!!!!!!
        CpuPlaceArmies();
//        CpuAttack(dice roll1, dice roll2);
    }

}
