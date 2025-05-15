package yahtzee.model.Categories;

/**
 * Base class for all scoring categories and factory for category instances.
 */
public abstract class Category {

    /**
     * Factory method to create a Category based on its index.
     */
    public static Category getCategory(int index) {
        // Upper section categories 1-6
        if (index >= 1 && index <= 6) {
            return new UpperCategory(index);
        }
        // Lower section and special categories
        switch (index) {
            case 7:
                return new ThreeOfAKind();
            case 8:
                return new FourOfAKind();
            case 9:
                return new FullHouse();
            case 10:
                return new SmallStraight();
            case 11:
                return new LargeStraight();
            case 12:
                return new Chance();
            case 13:
                return new Yahtzee();
            case 14:
                return new Total();
            case 15:
                return new Bonus();
            default:
                throw new IllegalArgumentException("Invalid category index: " + index);
        }
    }

    /**
     * Compute Yahtzee score: 50 if all dice match, otherwise 0.
     */
    public static int getYahtzeeScore(int[] diceValues) {
        if (diceValues.length == 0) return 0;
        int first = diceValues[0];
        for (int v : diceValues) {
            if (v != first) return 0;
        }
        return 50;
    }

    /**
     * Sum all dice equal to the given face value.
     */
    protected int addUpDice(int value, int[] diceValues) {
        int sum = 0;
        for (int v : diceValues) {
            if (v == value) {
                sum += v;
            }
        }
        return sum;
    }

    /**
     * Return the index that identifies this category.
     */
    public abstract int getCategoryIndex();

    /**
     * Calculate the score for this category given dice values.
     */
    public abstract int getScore(int[] diceValues);

    /**
     * Calculate the override score for Yahtzee bonus rules.
     */
    public abstract int getYahtzeeBonusOverrideScore(int[] diceValues);

    /**
     * Provide a string representation of the category name.
     */
    @Override
    public abstract String toString();
}
