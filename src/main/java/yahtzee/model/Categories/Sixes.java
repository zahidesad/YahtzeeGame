package yahtzee.model.Categories;


public class Sixes extends Category {
	@Override
	public int getCategoryIndex()
	{
		return 6;
	}

	/**
	 * Sum the values of a set of dice where the value of the dice is 6
	 */
	@Override
	public int getScore(int[] dice)
	{
		return addUpDice(6, dice);
	}

	@Override
	public int getYahtzeeBonusOverrideScore(int[] dice)
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "Sixes";
	}
}
