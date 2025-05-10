package yahtzee.model.Categories;

public class Fives extends Category {
	@Override
	public int getCategoryIndex()
	{
		return 5;
	}

	/**
	 * Sum the values of a set of dice where the value of the dice is 5
	 */
	@Override
	public int getScore(int[] dice)
	{
		return addUpDice(5, dice);
	}

	@Override
	public int getYahtzeeBonusOverrideScore(int[] dice)
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "Fives";
	}
}
