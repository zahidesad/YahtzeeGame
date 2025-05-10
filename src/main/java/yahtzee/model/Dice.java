package yahtzee.model;

public class Dice {
    private int value;
    private boolean held;

    public Dice() {
        value = 1;
        held = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isHeld() {
        return held;
    }

    public void setHeld(boolean held) {
        this.held = held;
    }

    public void roll() {
        if (!held) {
            value = (int) (Math.random() * 6 + 1);
        }
    }
}