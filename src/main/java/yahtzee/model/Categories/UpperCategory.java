package yahtzee.model.Categories;

public class UpperCategory extends Category {
    private int target;

    public UpperCategory(int target) {
        this.target = target;
    }

    @Override
    public int getCategoryIndex() {
        return target; // 1: Ones, 2: Twos, vb.
    }

    @Override
    public int getScore(int[] dice) {
        return addUpDice(target, dice);
    }

    @Override
    public int getYahtzeeBonusOverrideScore(int[] dice) {
        return 0;
    }

    @Override
    public String toString() {
        return switch (target) {
            case 1 -> "Ones";
            case 2 -> "Twos";
            case 3 -> "Threes";
            case 4 -> "Fours";
            case 5 -> "Fives";
            case 6 -> "Sixes";
            default -> "Unknown";
        };
    }
}