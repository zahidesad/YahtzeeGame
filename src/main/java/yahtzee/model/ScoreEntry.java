package yahtzee.model;

import yahtzee.model.Categories.Category;

/**
 * Wraps a scoring category with its selection state and assigned score.
 */
public class ScoreEntry {
    private Category category; // Associated scoring rule
    private boolean chosen;    // Whether this category has been scored
    private int score;         // Assigned score for this category

    /**
     * Initialize entry with category, unchosen and zero score.
     */
    public ScoreEntry(Category category) {
        this.category = category;
        chosen = false;
        score = 0;
    }

    /**
     * Get the scoring category logic.
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Check if this category is already scored.
     */
    public boolean isChosen() {
        return chosen;
    }

    /**
     * Mark this category as chosen or not.
     */
    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    /**
     * Get the assigned score for this category.
     */
    public int getScore() {
        return score;
    }

    /**
     * Assign a score to this category.
     */
    public void setScore(int score) {
        this.score = score;
    }
}