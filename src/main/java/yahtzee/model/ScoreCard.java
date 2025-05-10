package yahtzee.model;

import yahtzee.model.Categories.Category;

public class ScoreCard {
    private ScoreEntry[] entries;

    public ScoreCard() {
        entries = new ScoreEntry[13];
        for (int i = 0; i < 13; i++) {
            entries[i] = new ScoreEntry(Category.getCategory(i + 1));
        }
    }

    public ScoreEntry getEntry(int index) {
        return entries[index];
    }

    public boolean isAllChosen() {
        for (ScoreEntry entry : entries) {
            if (!entry.isChosen()) {
                return false;
            }
        }
        return true;
    }
}
