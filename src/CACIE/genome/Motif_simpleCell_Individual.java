package CACIE.genome;

import CACIE.RandomManager;
import CACIE.eventlist.CommonEventList;

public class Motif_simpleCell_Individual extends Abstract_Individual
{
	int[][] genomArray;
	int shibu, hachibu, futenshibu, juurokubu, nibu, futenhachibu, futennibu, zen;
	int[] lengthString;
	int kindOfNoteLength;
	int numOfNotes;
	
	public static final int NUMOF_PATTERNS = 5;
	public static final int NUMOF_PARAMS = 3;
	public static final int PITCH = 1;
	public static final int VELOCITY = 2;
	public static final int DULATION = 3;

	int[] patterns;

	public Motif_simpleCell_Individual(int IDNumber)
	{
		this.IDNumber = IDNumber;
		this.patterns = new int[this.NUMOF_PARAMS];
		this.genomArray = new int[this.numOfNotes][this.NUMOF_PARAMS];
		this.tableGen();
	}
	
	public Motif_simpleCell_Individual(int IDNumber, int[] patterns)
	{
		this(IDNumber);
		for (int i = 0; i < this.NUMOF_PARAMS; i++)
			this.patterns[i] = patterns[i];
	}

	private void tableGen()
	{
		this.kindOfNoteLength = 8;
		this.lengthString = new int[this.kindOfNoteLength];

		this.shibu = this.tickBase;
		this.lengthString[4] = this.shibu;

		this.hachibu = this.shibu / 2;
		this.lengthString[6] = this.hachibu;

		this.juurokubu = this.hachibu / 4;
		this.lengthString[7] = this.juurokubu;

		this.nibu = this.shibu * 2;
		this.lengthString[2] = this.nibu;

		this.zen = this.shibu * 4;
		this.lengthString[0] = this.zen;

		this.futenshibu = this.shibu + this.hachibu;
		this.lengthString[3] = this.futenshibu;

		this.futennibu = this.nibu + this.shibu;
		this.lengthString[1] = this.futennibu;

		this.futenhachibu = this.hachibu + this.juurokubu;
		this.lengthString[5] = this.futenhachibu;
	}

	public Abstract_Individual clone()
	{
		Motif_simpleCell_Individual returnInd = new Motif_simpleCell_Individual(this.IDNumber);
		returnInd.genomArray = new int[this.numOfNotes][this.NUMOF_PARAMS];
		for (int i = 0; i < this.numOfNotes; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				returnInd.genomArray[i][j] = this.genomArray[i][j];
			}
		}
		returnInd.lengthString = new int[this.lengthString.length];
		for (int i = 0; i < this.lengthString.length; i++)
		{
			returnInd.lengthString[i] = this.lengthString[i];
		}

		for (int i = 0; i < this.NUMOF_PARAMS; i++)
		{
			returnInd.patterns[i] = this.patterns[i];
		}

		returnInd.kindOfNoteLength = this.kindOfNoteLength;
		returnInd.numOfNotes = this.numOfNotes;

		return returnInd;
	}

	public String getGenomeString()
	{
		String returnString = new String("(");
		for (int i = 0; i < this.numOfNotes; i++)
		{
			returnString = returnString.concat("(");
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				returnString = returnString.concat(String.valueOf(this.genomArray[i][j]));
				if (j != this.NUMOF_PARAMS - 1)
					returnString = returnString.concat(",");
			}
			returnString = returnString.concat(")");
		}
		returnString = returnString.concat(")");
		return returnString;
	}

	public void makeEmptyGenome(int size)
	{
		this.numOfNotes = size;
		this.genomArray = new int[this.numOfNotes][this.NUMOF_PARAMS];
		for (int i = 0; i < this.numOfNotes; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				this.genomArray[i][j] = 0;
			}
		}
	}

	public int getNumOfParameters()
	{
		return this.NUMOF_PARAMS;
	}

	public int getGenomElements(int x, int y)
	{
		int returnValue = 0;
		if (x < this.numOfNotes && y < this.NUMOF_PARAMS)
			returnValue = this.genomArray[x][y];
		else
		{
			if (x >= (this.numOfNotes))
				returnValue = -1;
			else if (y >= this.NUMOF_PARAMS)
				returnValue = -1;
		}
		return returnValue;
	}

	public int mutation()
	{
		int returnValue = -1;
		// Mutation Pattern
		// 1:lost(??��?��??��?��»), 2:add(??��?��ղ??��?��), 3:swap(??��?��??��?��??��?��??��?��??��?��ؤ??��?��??��?��)
		int patternDef = (int) Math.round(Math.floor(RandomManager.getRandom() * 3)) + 1;

		if (patternDef == 1)
		{
			this.lostGenom();
		} else if (patternDef == 2)
		{
			this.addGenom();
		} else if (patternDef == 3)
		{
			this.swapGenom();
		}
		returnValue = 0;

		return returnValue;
	}

	private void checkValues(int[][] tmpValues)
	{

		for (int i = 0; i < tmpValues.length; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				if (tmpValues[i][j] == 0)
					System.out.println("Error!!! at " + i + " " + j);
			}
		}
	}

	private void lostGenom()
	{
		int numOfLostNotes = (int) Math.round(Math.floor(RandomManager.getRandom() * this.numOfNotes / 2));
		int lostPoint = (int) Math.round(Math.floor(RandomManager.getRandom() * this.numOfNotes));
		if (this.numOfNotes - lostPoint < numOfLostNotes)
			numOfLostNotes = this.numOfNotes - lostPoint;
		int newNumOfNotes = this.numOfNotes - numOfLostNotes;
		int[][] newGenomArray = new int[newNumOfNotes][this.NUMOF_PARAMS];

		for (int i = 0; i < lostPoint; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = this.genomArray[i][j];
			}
		}
		for (int i = lostPoint; i < newGenomArray.length; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = this.genomArray[i + numOfLostNotes][j];
			}
		}
		this.genomArray = newGenomArray;
		this.numOfNotes = newGenomArray.length;
	}

	private void addGenom()
	{
		int numOfAddNotes = (int) Math.round(Math.floor(RandomManager.getRandom() * this.numOfNotes));
		int insertPoint = (int) Math.round(Math.floor(RandomManager.getRandom() * this.numOfNotes));
		int takePoint = (int) Math.round(Math.floor(RandomManager.getRandom() * (this.numOfNotes - numOfAddNotes)));
		int newNumOfNotes = numOfAddNotes + this.numOfNotes;
		int newGenomArray[][] = new int[newNumOfNotes][this.NUMOF_PARAMS];

		int partGenom[][] = new int[numOfAddNotes][this.NUMOF_PARAMS];
		for (int i = 0; i < numOfAddNotes; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				partGenom[i][j] = this.genomArray[i + takePoint][j];
			}
		}

		for (int i = 0; i < insertPoint; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = this.genomArray[i][j];
			}
		}
		int counter = 0;
		for (int i = insertPoint; i < insertPoint + numOfAddNotes; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = partGenom[counter][j];
			}
			counter++;
		}
		for (int i = insertPoint + numOfAddNotes; i < newNumOfNotes; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = this.genomArray[i - numOfAddNotes][j];
			}
		}
		this.numOfNotes = newGenomArray.length;
		this.genomArray = newGenomArray;
	}

	private void swapGenom()
	{
		int swapPoint = (int) Math.round(Math.floor(RandomManager.getRandom() * this.numOfNotes));
		int newGenomArray[][] = new int[this.numOfNotes][this.NUMOF_PARAMS];
		int until = this.numOfNotes - swapPoint;
		for (int i = 0; i < until; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = this.genomArray[swapPoint + i][j];
			}
		}
		int counter = 0;
		for (int i = until; i < this.numOfNotes; i++)
		{
			for (int j = 0; j < this.NUMOF_PARAMS; j++)
			{
				newGenomArray[i][j] = this.genomArray[counter][j];
			}
			counter++;
		}
		this.genomArray = newGenomArray;
	}

	public void setGenomElements(int x, int y, int value)
	{
		if (x < this.numOfNotes && y < this.NUMOF_PARAMS)
			this.genomArray[x][y] = value;
	}

	public void generate()
	{
		int[] pattern;
		// Pitch
		pattern = this.getPattern(this.numOfNotes, this.patterns[0], this.PITCH);
		this.genomArray[0][0] = this.BASE_PITCH;
		for (int i = 1; i < this.numOfNotes; i++)
		{
			int value = this.genomArray[i - 1][0] + pattern[i];
			if (value < MIN_VALUE_PITCH)
				value = MIN_VALUE_PITCH;
			else if (value > MAX_VALUE_PITCH)
				value = MAX_VALUE_PITCH;
			this.genomArray[i][0] = value;
		}
		// Velocity
		pattern = this.getPattern(this.numOfNotes, this.patterns[1], this.VELOCITY);
		this.genomArray[0][1] = this.BASE_VELOCITY;
		for (int i = 1; i < this.numOfNotes; i++)
		{
			int value = this.genomArray[i - 1][1] + pattern[i];
			if (value < MIN_VALUE_VELOCITY)
				value = MIN_VALUE_VELOCITY;
			else if (value > MAX_VALUE_VELOCITY)
				value = MAX_VALUE_VELOCITY;
			this.genomArray[i][1] = value;
		}
		// Duration
		pattern = this.getPattern(this.numOfNotes, this.patterns[2], this.DULATION);
		int baseDuration = (int) Math.round(Math.floor(RandomManager.getRandom() * this.kindOfNoteLength));
		if (baseDuration == 0)
			baseDuration++;
		else if (baseDuration == kindOfNoteLength - 1)
			baseDuration--;
		int previousIndex = baseDuration;
		this.genomArray[0][2] = this.lengthString[baseDuration];
		for (int i = 1; i < this.numOfNotes; i++)
		{
			int index = previousIndex + pattern[i];
			if (index < 0)
				index = 0;
			else if (index >= this.kindOfNoteLength)
				index = this.kindOfNoteLength - 1;
			this.genomArray[i][2] = this.lengthString[index];
			previousIndex = index;
		}

		CommonEventList tmp = this.convertToEventList();
		// tmp.writeToFile("test.dat");

	}

	private static int[] getPattern(int numOfNotes, int patternID, int mode)
	{
		/*
		 * //mode : Motif_simpleCell_Individual.PITCH,
		 * Motif_simpleCell_Individual.VELOCITY,
		 * Motif_simpleCell_Individual.DULATION //patternID //0: - //1: / //2: \
		 * //3: / \ //4: \/
		 */

		int[] returnArray = new int[numOfNotes];
		int MaxWidthFromPreviousNote = 0;
		int MinWidthFromPreviousNote = 0;
		if (mode == Motif_simpleCell_Individual.PITCH)
		{
			MaxWidthFromPreviousNote = 4;
			MinWidthFromPreviousNote = 1;
		} else if (mode == Motif_simpleCell_Individual.VELOCITY)
		{
			MaxWidthFromPreviousNote = 40;
			MinWidthFromPreviousNote = 20;
		} else if (mode == Motif_simpleCell_Individual.DULATION)
		{
			MaxWidthFromPreviousNote = 3;
			MinWidthFromPreviousNote = 0;
		}

		// Generating Pattern
		if (patternID == 1)
		{
			for (int i = 0; i < numOfNotes; i++)
			{
				int tmpInt = (int) Math
						.round(Math.floor(RandomManager.getRandom() * (MaxWidthFromPreviousNote - MinWidthFromPreviousNote)));
				returnArray[i] = tmpInt + MinWidthFromPreviousNote;
			}
		} else if (patternID == 2)
		{
			for (int i = 0; i < numOfNotes; i++)
			{
				int tmpInt = (int) Math.round(Math.floor(RandomManager.getRandom() * MaxWidthFromPreviousNote - MinWidthFromPreviousNote));
				returnArray[i] = (tmpInt + MinWidthFromPreviousNote) * -1;
			}
		} else if (patternID == 3)
		{
			int center = numOfNotes / 2;
			for (int i = 0; i < center; i++)
			{
				returnArray[i] = ((int) Math.round(Math.floor(RandomManager.getRandom()
						* (MaxWidthFromPreviousNote - MinWidthFromPreviousNote))) + MinWidthFromPreviousNote)
						* -1;
			}
			for (int i = center; i < numOfNotes; i++)
			{
				returnArray[i] = (int) Math.round(Math.floor(RandomManager.getRandom() * MaxWidthFromPreviousNote
						- MinWidthFromPreviousNote))
						+ MinWidthFromPreviousNote;
			}
		} else if (patternID == 4)
		{
			int center = numOfNotes / 2;
			for (int i = 0; i < center; i++)
			{
				returnArray[i] = (int) Math.round(Math.floor(RandomManager.getRandom() * MaxWidthFromPreviousNote
						- MinWidthFromPreviousNote))
						+ MinWidthFromPreviousNote;
			}
			for (int i = center; i < numOfNotes; i++)
			{
				returnArray[i] = ((int) Math.round(Math.floor(RandomManager.getRandom() * MaxWidthFromPreviousNote
						- MinWidthFromPreviousNote)) + MinWidthFromPreviousNote)
						* -1;
			}
		} else
		{
			// included patternID == 0;
			for (int i = 0; i < numOfNotes; i++)
				returnArray[i] = 0;
		}

		return returnArray;
	}

	public void convertFromEventList(CommonEventList eventList)
	{
		int tmpNoteNum, tmpVelocity, tmpLength;
		OneNote tmpNote;
		this.numOfNotes = eventList.getNumOfNotes();
		this.genomArray = new int[this.numOfNotes][this.NUMOF_PARAMS];
		this.IDNumber = eventList.idNumber();
		for (int i = 0; i < this.numOfNotes; i++)
		{
			tmpNote = (OneNote) eventList.get(i);
			this.genomArray[i][0] = tmpNote.getNoteNumber();
			this.genomArray[i][1] = tmpNote.getVelocity();
			this.genomArray[i][2] = tmpNote.getDuration();
			// System.out.println(this.genomArray[i][0] + "," + this.genomArray[i][1]
			// + "," + this.genomArray[i][2]);
		}
	}

	public CommonEventList convertToEventList()
	{
		CommonEventList tmpString = new CommonEventList(this.numOfNotes);

		int tickCounter = 0;
		for (int i = 0; i < this.numOfNotes; i++)
		{
			OneNote tmpNote = new OneNote(this.genomArray[i][0], this.genomArray[i][1], tickCounter, this.genomArray[i][2]);
			tmpString.add(tmpNote);
			tickCounter += this.genomArray[i][2];
		}
		tmpString.idNumber(this.IDNumber);

		return tmpString;
	}

	protected void fixParameters()
	{
		this.numOfNotes = this.genomArray.length;
	}

	public int getNumOfNotes()
	{
		this.fixParameters();
		return this.numOfNotes;
	}

}
