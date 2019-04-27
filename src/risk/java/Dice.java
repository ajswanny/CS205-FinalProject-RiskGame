package risk.java;

public class Dice {

    private int die1;   // Number showing on the die.
    private int die2;
    private int die3;

    public Dice() {
        // Constructor.  Rolls the dice, so that they initially
        // show some random values.
//        roll();  // Call the roll() method to roll the dice.

        die1 = 0;
        die2 = 0;
        die3 = 0;

    }

    public void roll() {
        // Roll the dice by setting each of the dice to be
        // a random number between 1 and 6.
        die1 = (int)(Math.random()*6) + 1;
        die2 = (int)(Math.random()*6) + 1;
        die3 = (int)(Math.random()*6) + 1;
    }

    public int getDie1() {
        return die1;
    }

    public int getDie2() {
        return die2;
    }

    public int getDie3() {
        return die3;
    }

    public int getLargestOfTwo() {
        if(die1 >= die2){
            return die1;
        }else{
            return die2;
        }
    }

    public int getLargestOfThree(){
        int largest = 0;
        if(die1 > die2 && die1 > die3){
            largest = die1;
        }else if(die1 > die2 && die1 < die3){
            largest = die3;
        }else if(die1 < die2 && die1 > die3){
            largest = die2;
        }
        return largest;
    }

}