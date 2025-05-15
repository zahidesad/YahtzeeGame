package yahtzee.model;

import yahtzee.model.Categories.Category;

/**
 * Holds score entries for each category in the game.
 */
public class ScoreCard {
    private ScoreEntry[] entries;  // Array of 13 score slots

    /**
     * Initialize score entries for all categories.
     */
    public ScoreCard() {
        entries = new ScoreEntry[13];
        for (int i = 0; i < 13; i++) {
            entries[i] = new ScoreEntry(Category.getCategory(i + 1));
        }
    }

    /**
     * Retrieve the ScoreEntry at the specified index.
     */
    public ScoreEntry getEntry(int index) {
        return entries[index];
    }

    /**
     * Check if all categories have been scored.
     */
    public boolean isAllChosen() {
        for (ScoreEntry entry : entries) {
            if (!entry.isChosen()) {
                return false;
            }
        }
        return true;
    }
}
