package yahtzee.model;

import yahtzee.model.Categories.Category;

public class ScoreEntry {
    private Category category;
    private boolean chosen;
    private int score;

    public ScoreEntry(Category category) {
        this.category = category;
        chosen = false;
        score = 0;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
