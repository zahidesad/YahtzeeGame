package yahtzee.model;

import yahtzee.view.Resettable;

/**
 * Represents a player: name, scores, bonuses, and roll tracking.
 */
public class Player implements Resettable {
    private String name;        // Player's name
    private int rollCount;      // Rolls used this turn
    private int totalScore;     // Sum of lower-section scores
    private int upperScore;     // Sum of upper-section scores including bonus
    private int upperBonus;     // Upper section bonus points
    private int yahtzeeBonus;   // Cumulative Yahtzee bonuses
    private boolean haveYahtzee;// Indicates first Yahtzee achieved

    /**
     * Initialize player with default scores and flags.
     */
    public Player(String name) {
        this.name = name;
        rollCount = 0;
        totalScore = 0;
        upperScore = 0;
        upperBonus = 0;
        yahtzeeBonus = 0;
        haveYahtzee = false;
    }

    /**
     * Add score to upper or lower section.
     */
    public void addScore(int score, boolean isUpperCategory) {
        if (isUpperCategory)
            this.upperScore += score;
        else
            this.totalScore += score;
    }

    /**
     * Award and apply upper section bonus (35 points).
     */
    public void addUpperBonus() {
        upperBonus += 35;
        upperScore += 35;
    }

    /**
     * Add 100 points for additional Yahtzee bonus.
     */
    public void addYahtzeeBonus() {
        yahtzeeBonus += 100;
    }

    /**
     * Check if first Yahtzee has been scored.
     */
    public boolean getHaveYahtzee() {
        return haveYahtzee;
    }

    public String getName() {
        return name;
    }

    public int getRollCount() {
        return rollCount;
    }

    /**
     * Compute total score including bonuses.
     */
    public int getScore() {
        return totalScore + yahtzeeBonus + upperScore;
    }

    /**
     * Format score string with padding for display.
     */
    public String getStringScore() {
        if (totalScore < 10) {
            return totalScore + " ";
        } else {
            return Integer.toString(totalScore);
        }
    }

    public int getUpperBonus() {
        return upperBonus;
    }

    public int getUpperScore() {
        return this.upperScore;
    }

    public int getYahtzeeBonus() {
        return yahtzeeBonus;
    }

    /**
     * Increment roll count for each roll action.
     */
    public void incrementRollCount() {
        rollCount++;
    }

    /**
     * Reset player state for a new game.
     */
    @Override
    public void reset() {
        rollCount = 0;
        totalScore = 0;
        upperScore = 0;
        upperBonus = 0;
        yahtzeeBonus = 0;
        haveYahtzee = false;
    }

    /**
     * Reset only the roll count (new turn).
     */
    public void resetRollCount() {
        rollCount = 0;
    }

    /**
     * Mark that the player has achieved their first Yahtzee.
     */
    public void setHaveYahtzee() {
        haveYahtzee = true;
    }
}