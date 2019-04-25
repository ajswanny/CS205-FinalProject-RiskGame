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

//public class AI {
//    Territory territory = new Territory();
//    Dice dice = new Dice();
//    private int terrSelect_prority;
//    private boolean occupy;
//
//    public AI(terrSelect_prority,occupy){
//
//        if(terrSelect_prority == 1 && !occupy) {
//            territory(Australia,6);
//        }
//    }
//    public void attack(){
//
//    }
//    public int defend(){
//        dice.roll();
//        return dice.getDie1(), dice.getDie2();
//    }

}