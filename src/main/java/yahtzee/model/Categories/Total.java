package yahtzee.model.Categories;


class Total extends Category {
    @Override
    public int getCategoryIndex() {
        return 14;
    }

    /**
     * A total category cannot generate a score, but is given one instead.
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
        return "Total";
    }
}
