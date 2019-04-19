
import risk.java.Dice;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Dice_test {

    public static void main(String[] args) {

        Dice player;          // A variable that will refer to the dice.
        Dice cpu;

        player = new Dice();  // Create the PairOfDice object.
        cpu = new Dice();

        if (player.getDie1() >= cpu.getDie1()){
            System.out.println("Player wins, pick an area first");
        }else{
            System.out.println("AI wins, pick an area first");
        }

        // Simulate a combat assume player attack, computer defend
        int numOfAttackerArmies = 3; // declare number of army
        int numOfDefenderArmies = 2;
        List<Integer> AttackerArmylist = new LinkedList<Integer>();     //create army list
        List<Integer> DefenderArmylist = new LinkedList<Integer>();
        if(numOfAttackerArmies == 1){
            AttackerArmylist.add(player.getDie1());
        }else if(numOfAttackerArmies == 2){
            AttackerArmylist.add(player.getDie1());
            AttackerArmylist.add(player.getDie2());
        }else{
            AttackerArmylist.add(player.getDie1());
            AttackerArmylist.add(player.getDie2());
            AttackerArmylist.add(player.getDie3());
        }

        if(numOfDefenderArmies == 1){
            DefenderArmylist.add(cpu.getDie1());
        }else if(numOfDefenderArmies == 2){
            DefenderArmylist.add(cpu.getDie1());
            DefenderArmylist.add(cpu.getDie2());
        }

        System.out.println("Attacker roll result:");
        for(int i = 0; i < AttackerArmylist.size(); i++) {
            System.out.print(AttackerArmylist.get(i));
        }
        System.out.println("\nDefender roll result:");
        for(int i = 0; i < DefenderArmylist.size(); i++) {
            System.out.print(DefenderArmylist.get(i));
        }

        if(Collections.max(AttackerArmylist) >= Collections.max(DefenderArmylist)){
            System.out.println("\nAttacker wins");
        }else{
            System.out.println("\nDefender wins");
        }
    }

}