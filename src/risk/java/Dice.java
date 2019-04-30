package risk.java;

/**
 * Implementation of a set of dice used to roll and compare when Territories are attacked.
 */
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

    public int getTotal() {
        // Return the total showing on the two dice.
        return die1 + die2 + die3;
    }

}