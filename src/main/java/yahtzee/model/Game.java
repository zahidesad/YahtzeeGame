package yahtzee.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Player player;
    private Dice[] dice;
    private ScoreCard scoreCard;
    private int rollCount;

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

    public void rollDice() {
        if (rollCount < 3) {
            if (rollCount == 0) {
                for (Dice d : dice) {
                    d.setHeld(false);
                }
            }
            for (Dice d : dice) {
                d.roll();
            }
            rollCount++;
            if (isYahtzee() && player.getHaveYahtzee()) {
                player.addYahtzeeBonus();
            }
        }
    }

    public List<Integer> getNormalSelectableCategories() {
        List<Integer> normal = new ArrayList<>();
        if (isYahtzee() && scoreCard.getEntry(12).isChosen()) {
            int num = dice[0].getValue();
            int upperIndex = num - 1;
            if (!scoreCard.getEntry(upperIndex).isChosen()) {
                normal.add(upperIndex);
            }
        } else {
            for (int i = 0; i < 13; i++) {
                if (!scoreCard.getEntry(i).isChosen()) {
                    normal.add(i);
                }
            }
        }
        return normal;
    }

    public List<Integer> getOverrideSelectableCategories() {
        List<Integer> override = new ArrayList<>();
        if (isYahtzee() && scoreCard.getEntry(12).isChosen()) {
            int num = dice[0].getValue();
            int upperIndex = num - 1;
            if (scoreCard.getEntry(upperIndex).isChosen()) {
                for (int i = 6; i < 13; i++) {
                    if (!scoreCard.getEntry(i).isChosen()) {
                        override.add(i);
                    }
                }
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

    public int getPossibleScore(int categoryIndex, boolean usingOverride) {
        int[] values = getDiceValues();
        if (usingOverride) {
            return scoreCard.getEntry(categoryIndex).getCategory().getYahtzeeBonusOverrideScore(values);
        } else {
            return scoreCard.getEntry(categoryIndex).getCategory().getScore(values);
        }
    }

    public void selectCategory(int index, boolean usingOverride) {
        ScoreEntry entry = scoreCard.getEntry(index);
        if (!entry.isChosen()) {
            int[] values = getDiceValues();
            int score = usingOverride ? entry.getCategory().getYahtzeeBonusOverrideScore(values) : entry.getCategory().getScore(values);
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

    private void updateUpperBonus() {
        if (player.getUpperBonus() == 0 && player.getUpperScore() >= 63) {
            player.addUpperBonus();
        }
    }

    public boolean isGameOver() {
        return scoreCard.isAllChosen();
    }

    public void reset() {
        player.reset();
        for (Dice d : dice) {
            d.setValue(1);
            d.setHeld(false);
        }
        scoreCard = new ScoreCard();
        rollCount = 0;
    }

    private boolean isYahtzee() {
        int first = dice[0].getValue();
        for (Dice d : dice) {
            if (d.getValue() != first) {
                return false;
            }
        }
        return true;
    }

    private int[] getDiceValues() {
        int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = dice[i].getValue();
        }
        return values;
    }
}