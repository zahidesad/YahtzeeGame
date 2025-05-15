package yahtzee.model;

/**
 * Represents a single die with value and hold state.
 */
public class Dice {
    private int value;     // Current face value (1-6)
    private boolean held;   // Whether the die is held between rolls

    /**
     * Initialize die at default value 1, not held.
     */
    public Dice() {
        value = 1;
        held = false;
    }

    /**
     * Get the current face value of the die.
     */
    public int getValue() {
        return value;
    }

    /**
     * Set the face value of the die (used for remote updates).
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Check if the die is currently held.
     */
    public boolean isHeld() {
        return held;
    }

    /**
     * Mark the die as held or released.
     */
    public void setHeld(boolean held) {
        this.held = held;
    }

    /**
     * Roll the die if not held, assigning a random value 1-6.
     */
    public void roll() {
        if (!held) {
            value = (int) (Math.random() * 6 + 1);
        }
    }
}