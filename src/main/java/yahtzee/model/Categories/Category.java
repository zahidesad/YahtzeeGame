package yahtzee.model.Categories;

public abstract class Category {
	public static Category getCategory(int index) {
		if (index >= 1 && index <= 6) {
			return new UpperCategory(index);
		}
		switch (index) {
			case 7: return new ThreeOfAKind();
			case 8: return new FourOfAKind();
			case 9: return new FullHouse();
			case 10: return new SmallStraight();
			case 11: return new LargeStraight();
			case 12: return new Chance();
			case 13: return new Yahtzee();
			case 14: return new Total();
			case 15: return new Bonus();
			default: throw new IllegalArgumentException("Invalid category index: " + index);
		}
	}

	public static int getYahtzeeScore(int[] diceValues) {
		if (diceValues.length == 0) return 0;
		int first = diceValues[0];
		for (int v : diceValues) {
			if (v != first) return 0;
		}
		return 50;
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

	public abstract int getCategoryIndex();
	public abstract int getScore(int[] diceValues);
	public abstract int getYahtzeeBonusOverrideScore(int[] diceValues);
	@Override
	public abstract String toString();
}