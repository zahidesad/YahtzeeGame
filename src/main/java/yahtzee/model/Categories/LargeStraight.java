package yahtzee.model.Categories;

import java.util.Arrays;

public class LargeStraight extends Category {
	@Override
	public int getCategoryIndex()
	{
		return 11;
	}

	/**
	 * Return 40 if five of the die have consecutive values (e.g 1, 2, 3, 4, 5),
	 * otherwise 0
	 */
	@Override
	public int getScore(int[] dice)
	{
		int sum = 0;

		int[] theDice = new int[5];

		theDice[0] = dice[0];
		theDice[1] = dice[1];
		theDice[2] = dice[2];
		theDice[3] = dice[3];
		theDice[4] = dice[4];

		Arrays.sort(theDice);

		if (((theDice[0] == 1) && (theDice[1] == 2) && (theDice[2] == 3)
				&& (theDice[3] == 4) && (theDice[4] == 5))
				|| ((theDice[0] == 2) && (theDice[1] == 3) && (theDice[2] == 4)
						&& (theDice[3] == 5) && (theDice[4] == 6)))
		{
			sum = 40;
		}

		return sum;
	}

	@Override
	public int getYahtzeeBonusOverrideScore(int[] dice)
	{
		return 40;
	}

	@Override
	public String toString()
	{
		return "Large Straight";
	}
}
