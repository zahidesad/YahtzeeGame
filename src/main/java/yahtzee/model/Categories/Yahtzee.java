package yahtzee.model.Categories;

public class Yahtzee extends Category {
    @Override
    public int getCategoryIndex() {
        return 13;
    }

    /**
     * Return 50 if all five die have the same value, otherwise 0
     */
    @Override
    public int getScore(int[] dice) {
        return getYahtzeeScore(dice);
    }

    @Override
    public int getYahtzeeBonusOverrideScore(int[] dice) {
        return 50;
    }

    @Override
    public String toString() {
        return "Yahtzee";
    }
}
