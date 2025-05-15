package yahtzee.model.Categories;

public class Chance extends Category {
    @Override
    public int getCategoryIndex() {
        return 12;
    }

    /**
     * Return the sum of the values of every die
     */
    @Override
    public int getScore(int[] dice) {
        int sum = 0;

        for (int i = 0; i < 5; i++) {
            sum += dice[i];
        }

        return sum;
    }

    @Override
    public int getYahtzeeBonusOverrideScore(int[] dice) {
        return getScore(dice);
    }

    @Override
    public String toString() {
        return "Chance";
    }
}
