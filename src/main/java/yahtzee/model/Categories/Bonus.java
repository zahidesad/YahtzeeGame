package yahtzee.model.Categories;

public class Bonus extends Category {
    @Override
    public int getCategoryIndex() {
        return 15;
    }

    /**
     * A bonus category cannot generate a score, but is given one instead.
     */
    @Override
    public int getScore(int[] dice) {
        return 0;
    }

    @Override
    public int getYahtzeeBonusOverrideScore(int[] dice) {
        return 0;
    }

    @Override
    public String toString() {
        return "Bonus";
    }
}
