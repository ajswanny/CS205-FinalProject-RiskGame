public class Dice {

    private int die1;   // Number showing on the die.
    private int die2;
    private int die3;

    public Dice() {
        // Constructor.  Rolls the dice, so that they initially
        // show some random values.
        roll();  // Call the roll() method to roll the dice.
    }

    public void roll() {
        // Roll the dice by setting each of the dice to be
        // a random number between 1 and 6.
        die1 = (int)(Math.random()*6) + 1;
        die2 = (int)(Math.random()*6) + 1;
        die3 = (int)(Math.random()*6) + 1;
    }

    public int getDie1() {
        // Return the number showing on the first die.
        return die1;
    }

    public int getDie2() {
        // Return the number showing on the second die.
        return die2;
    }

    public int getDie3() {
        // Return the number showing on the second die.
        return die3;
    }

    public int getTotal() {
        // Return the total showing on the two dice.
        return die1 + die2 + die3;
    }

}