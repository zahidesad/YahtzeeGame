package yahtzee.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates game state: dice, scorecard, and roll logic.
 */
public class Game {
    private Player player;       // Current player data
    private Dice[] dice;         // Array of five dice
    private ScoreCard scoreCard; // Score entries for categories
    private int rollCount;       // Number of rolls this turn

    /**
     * Initialize a new game for a player.
     */
    public Game(String playerName) {
        player = new Player(playerName);
        dice = new Dice[5];
        for (int i = 0; i < 5; i++) {
            dice[i] = new Dice();
        }
        scoreCard = new ScoreCard();
        rollCount = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public Dice[] getDice() {
        return dice;
    }

    public ScoreCard getScoreCard() {
        return scoreCard;
    }

    public int getRollCount() {
        return rollCount;
    }

    public void setRollCount(int count) {
        rollCount = count;
    }

    /**
     * Roll all non-held dice, handle Yahtzee bonus, and increment roll count.
     */
    public void rollDice() {
        if (rollCount < 3) {
            if (rollCount == 0) {
                // Release all held dice on first roll
                for (Dice d : dice) {
                    d.setHeld(false);
                }
            }
            for (Dice d : dice) {
                d.roll();
            }
            rollCount++;
            // Award Yahtzee bonus if applicable
            if (isYahtzee() && player.getHaveYahtzee()) {
                player.addYahtzeeBonus();
            }
        }
    }

    /**
     * Categories available under normal rules.
     */
    public List<Integer> getNormalSelectableCategories() {
        List<Integer> normal = new ArrayList<>();
        // If Yahtzee bonus active, only matching upper category allowed
        if (isYahtzee() && scoreCard.getEntry(12).isChosen()) {
            int num = dice[0].getValue();
            int upperIndex = num - 1;
            if (!scoreCard.getEntry(upperIndex).isChosen()) {
                normal.add(upperIndex);
            }
        } else {
            // All unchosen categories available
            for (int i = 0; i < 13; i++) {
                if (!scoreCard.getEntry(i).isChosen()) {
                    normal.add(i);
                }
            }
        }
        return normal;
    }

    /**
     * Categories available under Yahtzee override rules.
     */
    public List<Integer> getOverrideSelectableCategories() {
        List<Integer> override = new ArrayList<>();
        if (isYahtzee() && scoreCard.getEntry(12).isChosen()) {
            int num = dice[0].getValue();
            int upperIndex = num - 1;
            if (scoreCard.getEntry(upperIndex).isChosen()) {
                // Lower section options first
                for (int i = 6; i < 13; i++) {
                    if (!scoreCard.getEntry(i).isChosen()) {
                        override.add(i);
                    }
                }
                // Otherwise any unchosen upper category
                if (override.isEmpty()) {
                    for (int i = 0; i < 6; i++) {
                        if (!scoreCard.getEntry(i).isChosen()) {
                            override.add(i);
                        }
                    }
                }
            }
        }
        return override;
    }

    /**
     * Compute score for a category, using override if requested.
     */
    public int getPossibleScore(int categoryIndex, boolean usingOverride) {
        int[] values = getDiceValues();
        if (usingOverride) {
            return scoreCard.getEntry(categoryIndex)
                    .getCategory().getYahtzeeBonusOverrideScore(values);
        } else {
            return scoreCard.getEntry(categoryIndex)
                    .getCategory().getScore(values);
        }
    }

    /**
     * Select and score a category, update player totals and bonuses.
     */
    public void selectCategory(int index, boolean usingOverride) {
        ScoreEntry entry = scoreCard.getEntry(index);
        if (!entry.isChosen()) {
            int[] values = getDiceValues();
            int score = usingOverride ?
                    entry.getCategory().getYahtzeeBonusOverrideScore(values) :
                    entry.getCategory().getScore(values);
            entry.setScore(score);
            entry.setChosen(true);
            boolean isUpper = index < 6;
            player.addScore(score, isUpper);
            if (index == 12 && score == 50) {
                player.setHaveYahtzee();
            }
            updateUpperBonus();
        }
    }

    /**
     * Award upper section bonus when threshold reached.
     */
    private void updateUpperBonus() {
        if (player.getUpperBonus() == 0 && player.getUpperScore() >= 63) {
            player.addUpperBonus();
        }
    }

    /**
     * Check if all categories have been chosen.
     */
    public boolean isGameOver() {
        return scoreCard.isAllChosen();
    }

    /**
     * Reset game state for a new round.
     */
    public void reset() {
        player.reset();
        for (Dice d : dice) {
            d.setValue(1);
            d.setHeld(false);
        }
        scoreCard = new ScoreCard();
        rollCount = 0;
    }

    /**
     * Determine if current dice values form a Yahtzee (all equal).
     */
    private boolean isYahtzee() {
        int first = dice[0].getValue();
        for (Dice d : dice) {
            if (d.getValue() != first) {
                return false;
            }
        }
        return true;
    }

    /**
     * Extract current dice face values into an array.
     */
    public int[] getDiceValues() {
        int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = dice[i].getValue();
        }
        return values;
    }
}
