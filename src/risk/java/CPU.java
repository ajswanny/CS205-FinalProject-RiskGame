package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class CPU extends Player implements Serializable {

    private String about;
    private String aboutTheOneMove;

    public CPU() {
        this.color = Game.PlayerColor.EU_GRAY;
    }

    public void CpuPlaceArmies() {
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

    public Territory CpuAttack(int myRoll, int enemyRoll) {
        int biggestAdvantage = -10000;
        Territory from = null;
        Territory to = null;
        for (Territory currentFrom : controlledTerritories) {
            ArrayList<Territory> currentNeighbors = currentFrom.getNeighbors();
            for (Territory currentNeighbor : currentNeighbors) {
                if (currentFrom.owner != currentNeighbor.owner) {
                    if ((currentFrom.numOfArmies - currentNeighbor.numOfArmies) > biggestAdvantage) {
                        biggestAdvantage = (currentFrom.numOfArmies - currentNeighbor.numOfArmies);
                        from = currentFrom;
                        to = currentNeighbor;
                    }
                }
            }
        }

        // Test if CPU conquered a territory
        assert from != null;
        boolean didConquerTerritory = from.attack(to, myRoll, enemyRoll);
        if (didConquerTerritory) {
            return to;
        } else {
            return null;
        }
    }

    public Territory AI_attack() {
        Player p = new Player();
        ArrayList<Territory> playerControl = p.getControlledTerritories();
        Territory t = null;
        for (Territory territory : controlledTerritories) {
            if(t.){

            }
        }

    }

    public Territory draftArmies() {
        int min = 40;
        Territory t = null;
        for (Territory territory : controlledTerritories) {
            if (territory.getNumOfArmies() < min) {
                min = territory.getNumOfArmies();
                t = territory;
            }
        }
        return t;
    }

    public boolean fortifyTerritories() {
        int max = 2;
        int min = Integer.MAX_VALUE;
        Territory maxT = null;
        Territory minT = null;
        for (Territory territory : controlledTerritories) {
            if (territory.getNumOfArmies() > max) {
                max = territory.getNumOfArmies();
                maxT = territory;
            }
            if (territory.getNumOfArmies() < min) {
                min = territory.getNumOfArmies();
                minT = territory;
            }
        }
        if (maxT != null) {
            maxT.moveArmies(minT, maxT.getNumOfArmies()/2);
            return true;
        } else {
            return false;
        }
    }

    public void theOneMove(){
//        Dice roll goes here !!!!!!!!
        CpuPlaceArmies();
//        CpuAttack(dice roll1, dice roll2);
    }

}
