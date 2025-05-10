package yahtzee.model.Categories;


/**
 * Threes category
 *
 * @author Ryan Harrison
 */
public class Threes extends Category {
    @Override
    public int getCategoryIndex() {
        return 3;
    }

    /**
     * Sum the values of a set of dice where the value of the dice is 3
     */
    @Override
    public int getScore(int[] dice) {
        return addUpDice(3, dice);
    }

    @Override
    public int getYahtzeeBonusOverrideScore(int[] dice) {
        return 0;
    }

    @Override
    public String toString() {
        return "Threes";
    }
}
