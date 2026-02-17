package CACIE;

import java.util.Random;

public final class RandomManager
{
	private static Random random;
	
	static {
		random = new Random();
	}
	
	private RandomManager() {}
	
	public static void setSeed(long seed)
	{
		random.setSeed( seed );
	}
	
	public static double getRandom()
	{
		return random.nextDouble();
	}
}
