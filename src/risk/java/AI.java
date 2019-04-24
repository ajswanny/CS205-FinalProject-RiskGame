package risk.java;

import risk.Game;
/*
    bool occupy; whether the territory has been occupied
    int terrSelect_prority; set southAmerica and Australia as 1, other continents as 0

    Strategy:
        1. AI will try to occupy southAmerica or Australia first, and place armies on the entry of
           the continents.
        2. AI will not attack players until they accumulate a specific number of armies. When defend, AI will
           always choose to roll two dices.
*/

public class AI {
    Territory territory;
    private int terrSelect_prority = 1;
    private boolean occupy = false;

    public AI(){
        if(terrSelect_prority == 1 && !occupy) {
            territory(Australia,6);
        }

    }
    public void attack(){

    }
    public void defend(){

    }

}