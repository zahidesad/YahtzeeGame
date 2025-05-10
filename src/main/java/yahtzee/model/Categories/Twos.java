package yahtzee.model.Categories;


public class Twos extends Category {
    @Override
    public int getCategoryIndex() {
        return 2;
    }

    /**
     * Sum the values of a set of dice where the value of the dice is 2
     */
    @Override
    public int getScore(int[] dice) {
        return addUpDice(2, dice);
    }

    @Override
    public int getYahtzeeBonusOverrideScore(int[] dice) {
        return 0;
    }

    @Override
    public String toString() {
        return "Twos";
    }
}
