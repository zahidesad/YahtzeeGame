package yahtzee.model.Categories;


public class Fours extends Category {
	@Override
	public int getCategoryIndex()
	{
		return 4;
	}

	/**
	 * Sum the values of a set of dice where the value of the dice is 4
	 */
	@Override
	public int getScore(int[] dice)
	{
		return addUpDice(4, dice);
	}

	@Override
	public int getYahtzeeBonusOverrideScore(int[] dice)
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "Fours";
	}
}
