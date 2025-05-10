package yahtzee.model.Categories;

public class Ones extends Category {

	public int getCategoryIndex() {
		return 1;
	}

	@Override
	public int getScore(int[] diceValues) {
		return addUpDice(1, diceValues);
	}

	@Override
	public int getYahtzeeBonusOverrideScore(int[] diceValues) {
		return 0;
	}

	@Override
	public String toString() {
		return "Ones";
	}

	protected int addUpDice(int value, int[] diceValues) {
		int sum = 0;
		for (int v : diceValues) {
			if (v == value) {
				sum += v;
			}
		}
		return sum;
	}
}
