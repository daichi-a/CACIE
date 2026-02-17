package CACIE.genome;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

public class DistanceCalculator
{
	/** main method for test */
	public static void main(String args[])
	{
		// Integer
		Integer[] integerDataA = new Integer[]{1, 2, 4, 5, 6};
		Integer[] integerDataB = new Integer[]{1, 2, 3, 4, 5, 6};
		System.out.println( getLevenshteinDistance( integerDataA, integerDataB) );
		
		// String
		String[] stringDataA = new String[]{"this", "is", "a", "pen", "."};
		String[] stringDataB = new String[]{"this", "dog", "is", "very", "hungry", "."};
		System.out.println( getLevenshteinDistance(stringDataA, stringDataB) );
	}
	
	/** calculates positions of indivuduals. All the distance is normalized to 0.0 to 1.0.
	 * @param population and size of population.
	 * @return position array.
	 */
	public static double[][] getDistanceMatrix(Population population, int size)
	{
		if( population == null || size == 0)
		{
			return null;
		}
		List<String[]> targetGenemeList = new ArrayList<String[]>(size);
		
		for( int i = 0; i < size; i++ )
		{		
			List genomeList = ((Motif_simpleTree_Individual)population.getIndividual(0, i)).getGenomeArray();
			//List<String> genomeStrings = new ArrayList<String>(genomeList.size());
			String[] genomeStrings = new String[genomeList.size()];
			for( int j = 0; j < genomeList.size(); j++ )
			{
				genomeStrings[j] = genomeList.get(j).toString();
			}
			targetGenemeList.add(genomeStrings);
//			System.out.println( i + " : " + Arrays.deepToString(targetGenemeList.get(i)) );
		}
		
		double[][] distanceMatrix = new double[size][size];
		double maxValue = 1;
		
		for( int i = 0; i < size; i++ )
		{
			for( int j = i + 1; j < size; j++ )
			{
				double distance = getLevenshteinDistance(targetGenemeList.get(i), targetGenemeList.get(j) );
				distanceMatrix[i][j] = distance;
				if( maxValue < distance )
				{
					maxValue = distance;
				}
			}
		}
//		for( int i = 0; i < size; i++ )
//		{
//			distanceMatrix[i][i] = 0;
//			for( int j = i + 1; j < size; j++ )
//			{
//				distanceMatrix[i][j] = distanceMatrix[i][j] / maxValue;
//				distanceMatrix[j][i] = distanceMatrix[i][j];
//			}
//		}
		
//		for( int i = 0; i < size; i++ )
//		{
//			for( int j = 0; j < size; j++ )
//			{
//				System.out.print( distanceMatrix[i][j] + " ");
//			}
//			System.out.println();
//		}
		
		return distanceMatrix;
	}
	
	/**
	 * Returns the Levenshtein distance between two data, targetA and targetB.
	 * @param <T> parameter type
	 * @param targetA
	 * @param targetB
	 * @return the levenshtein distance.
	 */
	public static <T> double getLevenshteinDistance(List<T> targetA, List<T> targetB)
	{
		return getLevenshteinDistance(targetA.toArray(), targetB.toArray());
	}
	
	/**
	 * Returns the Levenshtein distance between two data, targetA and targetB.
	 * @param <T> parameter type
	 * @param targetA
	 * @param targetB
	 * @return the levenshtein distance.
	 */
	public static <T> double getLevenshteinDistance(T[] targetA, T[] targetB)
	{
//		System.out.println("Comparing: ");
//		System.out.println( Arrays.deepToString( targetA) + "\n" + Arrays.deepToString(targetB) );
		
		int d[][]; // matrix
		int i; // iterates through s
  	int j; // iterates through t
  	T s_i; // ith character of s
  	T t_j; // jth character of t
  	int cost; // cost
  	
    // Step 1
  	
  	if (targetA.length == 0) return targetB.length;
    if (targetB.length == 0) return targetA.length;
    d = new int[targetA.length+1][targetB.length+1];
    
    // Step 2
    for (i = 0; i <= targetA.length; i++) d[i][0] = i;
    for (j = 0; j <= targetB.length; j++) d[0][j] = j;
    
    // Step 3
    for (i = 1; i <= targetA.length; i++)
    {
      s_i = targetA[i-1];
      
      // Step 4
      for (j = 1; j <= targetB.length; j++)
      {
        t_j = targetB[j-1];

        // Step 5
        if (s_i.equals(t_j)) cost = 0;
        else cost = 1;
        
        // Step 6
        d[i][j] = Minimum (d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);
      }
    }
    
//    System.out.println( d[targetA.length][targetB.length] );
    // Step 7
    return d[targetA.length][targetB.length];
	}
	
  /** returns mininum value of three values */
	private static <T> int Minimum (int a, int b, int c)
	{
		int mi;
    mi = a;
    if (b < mi) mi = b;
    if (c < mi) mi = c;
    return mi;
  }
}
