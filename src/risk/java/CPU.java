package risk.java;

import risk.Game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The CPU class contains all data for the computer-player-unit from its resources to its strategies for the Game.
 */
public class CPU extends Player implements Serializable {

    /* Constructor */
    public CPU() {
        this.color = Game.PlayerColor.EU_GRAY;
    }

    /* Methods */
    /**
     * Implements the CPU's strategy for drafting armies: armies are added to the last controlled Territory identified
     * with the least amount of armies.
     * @return the Territory to draft armies to.
     */
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

    /**
     * Implements the CPU's strategy for attacking enemy Territories: an attack of 1-to-1 armies is made from the
     * Territories with the biggest difference of armies in favor of the CPU's attack origin.
     * @param cpuRoll the dice roll for the CPU.
     * @param enemyRoll the enemy dice roll.
     * @return attack data.
     */
    public CPUAttack attackTerritory(int cpuRoll, int enemyRoll) {

        // Identify the biggest advantage for an attack.
        int biggestAdvantage = -10000;
        Territory attackOrigin = null;
        Territory attackTarget = null;
        for (Territory currentFrom : controlledTerritories) {
            ArrayList<Territory> currentNeighbors = currentFrom.getNeighbors();
            for (Territory currentNeighbor : currentNeighbors) {
                if (currentFrom.owner != currentNeighbor.owner && currentFrom.getNumOfArmies() > 1) {
                    if ((currentFrom.numOfArmies - currentNeighbor.numOfArmies) > biggestAdvantage) {
                        biggestAdvantage = (currentFrom.numOfArmies - currentNeighbor.numOfArmies);
                        attackOrigin = currentFrom;
                        attackTarget = currentNeighbor;
                    }
                }
            }
        }

        // Test if CPU conquered a territory.
        if (attackOrigin != null) {
            return new CPUAttack(attackOrigin, attackTarget, attackOrigin.attack(attackTarget, cpuRoll, enemyRoll));
        } else {
            return null;
        }

    }

    /**
     * Implements the CPU's strategy for fortifying Territories: the CPU's Territory with the most armies is found and half of
     * these armies are moved to the Territory with the least amount of armies.
     * @return fortification data.
     */
    public CPUFortification fortifyTerritories() {
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
            return new CPUFortification(maxT, minT, maxT.getNumOfArmies()/2);
        } else {
            return null;
        }
    }

}
