package CACIE.genome;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.RandomManager;
import CACIE.eventlist.CommonEventList;
import CACIE.eventlist.PopulationOfEventList;

public class Motif_simpleCell_Population extends Abstract_Population {
	static int MAXEVLVALUE = 100;

	static int MINEVLVALUE = 0;

	int[] priorityTable;

	int[] fitnessValueTable;

	int populationSize;

	ArrayList thisGeneration, workingGeneration, nextGeneration, genomStocker;

	public Motif_simpleCell_Population(int numOfIndividuals) {
		this.populationSize = numOfIndividuals;
		this.allocArrayLists();
	}

	public Motif_simpleCell_Population() {
		this.populationSize = this.DEF_NUM_INDIVIDUALS;
		this.allocArrayLists();
	}

	public ArrayList<String> getCrossoveredIndividualLog(){
		//dummy
		return new ArrayList<String>();
	}
	
	public ArrayList<String> getMutatedIndividualLog(){
		//dummy
		return new ArrayList<String>();
	}
	
	public void setTempo(int newTempo){
		//dummy
	}
	
	protected void allocArrayLists() {
		this.thisGeneration = new ArrayList(this.populationSize);
		this.thisGeneration.ensureCapacity(this.populationSize);
		this.nextGeneration = new ArrayList(this.populationSize);
		this.nextGeneration.ensureCapacity(this.populationSize);

		this.workingGeneration = new ArrayList(1);
		this.workingGeneration.ensureCapacity(1);
		this.genomStocker = new ArrayList(1);
		this.genomStocker.ensureCapacity(1);

		this.priorityTable = new int[this.populationSize];
		this.fitnessValueTable = new int[this.populationSize];
		for (int i = 0; i < this.populationSize; i++) {
			this.priorityTable[i] = 0;
			this.fitnessValueTable[0] = 50;
		}
	}

	public void initPopulation() {
		// Generating Pitch And Rhythm Pattern Array
		int[][] pattern = new int[this.populationSize][Motif_simpleCell_Individual.NUMOF_PARAMS];
		int pitchCounter = (int) Math.round(RandomManager.getRandom()
				* Motif_simpleCell_Individual.NUMOF_PATTERNS);
		int rhythmCounter = (int) Math.round(RandomManager.getRandom()
				* Motif_simpleCell_Individual.NUMOF_PATTERNS);
		int velocityCounter = (int) Math.round(Math.floor(RandomManager
				.getRandom()
				* Motif_simpleCell_Individual.NUMOF_PATTERNS));

		for (int i = 0; i < this.populationSize; i++) {
			if (pitchCounter >= Motif_simpleCell_Individual.NUMOF_PATTERNS)
				pitchCounter = 0;
			if (rhythmCounter >= Motif_simpleCell_Individual.NUMOF_PATTERNS)
				rhythmCounter = 0;
			if (velocityCounter >= Motif_simpleCell_Individual.NUMOF_PATTERNS)
				velocityCounter = 0;

			pattern[i][0] = pitchCounter;
			pattern[i][1] = velocityCounter;
			pattern[i][2] = rhythmCounter;
			pitchCounter++;
			velocityCounter++;
			rhythmCounter++;
		}

		this.thisGeneration = new ArrayList(this.populationSize);
		this.thisGeneration.ensureCapacity(this.populationSize);

		for (int i = 0; i < this.populationSize; i++) {
			int[] tmpPattern = new int[Motif_simpleCell_Individual.NUMOF_PARAMS];
			for (int j = 0; j < Motif_simpleCell_Individual.NUMOF_PARAMS; j++) {
				tmpPattern[j] = pattern[i][j];
			}
			Motif_simpleCell_Individual tmpInd = new Motif_simpleCell_Individual(
					i, tmpPattern);
			tmpInd.generate();
			this.thisGeneration.add(tmpInd);
		}

		/*
		 * for(int i=0; i<this.populationSize; i++){
		 * Motif_simpleCell_Individual tmp =
		 * (Motif_simpleCell_Individual)this.thisGeneration.get(i);
		 * CommonEventList eventList =
		 * (CommonEventList)tmp.convertToEventList(); }
		 */
	}

	public int getPopulationSize() {
		return this.populationSize;
	}

	public void refreshEvaluated() {
		for (int i = 0; i < this.populationSize; i++) {
			this.fitnessValueTable[i] = 50;
		}
	}

	public int getEvaluated(int index) {
		return this.fitnessValueTable[index];
	}

	public int evaluate(int index, int value) {
		if (value > MAXEVLVALUE)
			value = MAXEVLVALUE;
		else if (value < MINEVLVALUE)
			value = MINEVLVALUE;
		this.fitnessValueTable[index] = value;
		return value;
	}

	public void setIndividualToGenomStocker(int index) {
		int containerSize = this.genomStocker.size();
		containerSize++;
		this.genomStocker.ensureCapacity(containerSize);
		this.genomStocker.add(this.thisGeneration.get(index));
	}

	public int sendToStore(int popID, int fromIndex) {
		int returnValue = -1;
		int tmpSize = -1;
		if (popID == 0) {
			if (this.genomStocker != null) {
				tmpSize = this.genomStocker.size();
				this.genomStocker.ensureCapacity(tmpSize++);
				if (fromIndex < this.thisGeneration.size() && fromIndex >= 0) {
					this.genomStocker.add(this.thisGeneration.get(fromIndex));
					returnValue = 0;
				}
			}
		} else if (popID == 1) {
			if (this.genomStocker != null) {
				tmpSize = this.genomStocker.size();
				this.genomStocker.ensureCapacity(tmpSize++);
				if (fromIndex < this.nextGeneration.size() && fromIndex >= 0) {
					this.genomStocker.add(this.nextGeneration.get(fromIndex));
					returnValue = 0;
				}
			}
		}
		return returnValue;
	}

	public Object getIndividual(int popID, int index) {
		Object returnObject = new Object();
		// popID 0:thisGeneration 1:genomStocker
		if (popID == 0)
			returnObject = this.thisGeneration.get(index);
		else if (popID == 1)
			returnObject = this.genomStocker.get(index);

		return returnObject;
	}

	public void convertFromPopulationEventList(int popID,
			PopulationOfEventList tmpPopEventList) {
		int tmpNum = tmpPopEventList.getNumOfIndividuals();

		CommonEventList tmpEventList;
		if (popID == 0) {
			this.populationSize = tmpNum;
			this.thisGeneration = new ArrayList(this.populationSize);
		} else if (popID == 1) {
			this.genomStocker = new ArrayList(tmpNum);
		}

		for (int i = 0; i < tmpNum; i++) {
			tmpEventList = (CommonEventList) tmpPopEventList.get(i);
			this.convertFromEventList(popID, i, tmpEventList);
		}
	}

	public void convertFromEventList(int popID, int index,
			CommonEventList eventList) {
		Motif_simpleCell_Individual aInd = new Motif_simpleCell_Individual(
				index);
		aInd.convertFromEventList(eventList);

		if (popID == 0) {
			this.thisGeneration.ensureCapacity(index);
			this.thisGeneration.add(aInd);
		} else if (popID == 1) {
			this.genomStocker.ensureCapacity(index);
			this.genomStocker.add(aInd);
		}
	}

	public PopulationOfEventList convertToPopulationEventList(int popID) {
		CommonEventList tmpEventList;
		PopulationOfEventList tmpPopEventList = new PopulationOfEventList(1);
		Motif_simpleCell_Individual aInd = new Motif_simpleCell_Individual(1);
		if (popID == 0) {
			int until = this.thisGeneration.size();
			tmpPopEventList = new PopulationOfEventList(until);
			for (int i = 0; i < until; i++) {
				aInd = (Motif_simpleCell_Individual) this.thisGeneration.get(i);
				tmpPopEventList.add(aInd.convertToEventList());
			}
		} else if (popID == 1) {
			int until = this.genomStocker.size();
			tmpPopEventList = new PopulationOfEventList(until);
			for (int i = 0; i < until; i++) {
				aInd = (Motif_simpleCell_Individual) this.genomStocker.get(i);
				tmpPopEventList.add(aInd.convertToEventList());
			}
		}
		return tmpPopEventList;
	}

	public CommonEventList convertToEventList(int popID, int index) {
		CommonEventList eventList = new CommonEventList(index);
		Motif_simpleCell_Individual aInd = new Motif_simpleCell_Individual(1);
		if (popID == 0) {
			aInd = (Motif_simpleCell_Individual) this.thisGeneration.get(index);
		} else if (popID == 1) {
			aInd = (Motif_simpleCell_Individual) this.genomStocker.get(index);
		}
		eventList = aInd.convertToEventList();
		return eventList;
	}

	public String getGenomeString(int popID, int index) {
		String returnString = new String();
		Motif_simpleCell_Individual aInd = new Motif_simpleCell_Individual(1);
		if (popID == 0) {
			aInd = (Motif_simpleCell_Individual) this.thisGeneration.get(index);
		} else if (popID == 1) {
			aInd = (Motif_simpleCell_Individual) this.genomStocker.get(index);
		}

		returnString = aInd.getGenomeString();
		return returnString;
	}

	public void replacePopulation() {
		this.thisGeneration = this.nextGeneration;
	}

	protected void copyPopulation() {
		this.workingGeneration.clear();
		this.workingGeneration.ensureCapacity(this.thisGeneration.size());
		for (int i = 0; i < this.thisGeneration.size(); i++) {
			Motif_simpleCell_Individual tmpInd = (Motif_simpleCell_Individual) this.thisGeneration
					.get(i);
			this.workingGeneration.add((Object) tmpInd.clone());
		}
	}

	public void reproductPopulation() {
		this.copyPopulation();
		this.nextGeneration = (ArrayList) randomCrossOver(this.thisGeneration,
				this.workingGeneration);
		this.randomMutation(this.nextGeneration);
		this.thisGeneration = this.nextGeneration;
	}

	public void mutation() {
		this.randomMutation(this.nextGeneration);
	}

	private void randomMutation(ArrayList aGeneration) {
		Motif_simpleCell_Individual tmpInd;
		for (int i = 0; i < this.populationSize; i++) {
			tmpInd = (Motif_simpleCell_Individual) aGeneration.get(i);
			tmpInd.mutation();
		}
	}

	private Object randomCrossOver(ArrayList thisGene, ArrayList workGene) {
		ArrayList returnGene = new ArrayList(thisGene.size());
		returnGene.ensureCapacity(thisGene.size());
		int priorityIndex[] = new int[this.populationSize];
		if (thisGene.size() == workGene.size()) {
			int maxCount = 0;
			for (int i = 0; i < this.populationSize; i++) {
				maxCount += this.fitnessValueTable[i];
			}
			for (int i = 0; i < this.populationSize; i++) {
				double ratio = (double) maxCount
						/ (double) fitnessValueTable[i];
				this.priorityTable[i] = (int) Math.round(Math
						.floor(this.populationSize / ratio));
			}
			int counter = 0;
			for (int i = 0; i < this.populationSize; i++) {
				counter += this.priorityTable[i];
			}
			boolean canContinue = false;
			int top = 0;
			int topIndividualIndex = -1;
			for (int i = 0; i < this.populationSize; i++) {
				if (top < this.priorityTable[i]) {
					top = this.priorityTable[i];
					topIndividualIndex = i;
				}
			}

			while (canContinue == false) {
				if (counter == this.populationSize)
					canContinue = true;
				else if (counter < this.populationSize) {
					this.priorityTable[topIndividualIndex]++;
					counter++;
				} else if (counter > this.populationSize) {
					this.priorityTable[topIndividualIndex]--;
					counter--;
				}
			}
			counter = 0;
			int[] tableOfIndexToCross = new int[this.populationSize];
			int indIndex = 0;
			for (int i = 0; i < this.populationSize; i++) {
				int indCounter = 0;
				while (indCounter < priorityTable[indIndex]) {
					tableOfIndexToCross[i] = indIndex;
					indCounter++;
				}
				indIndex++;
			}
			System.out.println("TEST");
			counter = 0;
			Motif_simpleCell_Individual tmpInd = new Motif_simpleCell_Individual(
					0);
			while (counter < this.populationSize) {
				boolean doppele = true;
				int turnCount = 0;
				int beforeIndex = 0;
				do {
					int crossFromIndex = 0;
					if (turnCount == 0)
						crossFromIndex = (int) Math.round(Math
								.floor(RandomManager.getRandom()
										* this.populationSize));
					else
						crossFromIndex = beforeIndex;
					Motif_simpleCell_Individual firstInd = (Motif_simpleCell_Individual) thisGene
							.get(tableOfIndexToCross[counter]);
					Motif_simpleCell_Individual secondInd = (Motif_simpleCell_Individual) workGene
							.get(tableOfIndexToCross[crossFromIndex]);

					tmpInd = (Motif_simpleCell_Individual) this
							.crossOverIndividuals((Object) firstInd.clone(),
									(Object) secondInd.clone(), counter);
					boolean inDoppele = true;
					for (int i = 0; i < returnGene.size(); i++) {
						Motif_simpleCell_Individual tmpInd2 = (Motif_simpleCell_Individual) returnGene
								.get(i);
						String tmpSt = tmpInd2.getGenomeString();
						if (tmpSt.equals(tmpInd.getGenomeString())) {
							inDoppele = false;
							break;
						}
					}
					if (inDoppele || turnCount >= this.populationSize)
						doppele = false;
					turnCount++;
					beforeIndex = crossFromIndex + 1;
					if (beforeIndex >= this.populationSize)
						beforeIndex = 0;
				} while (doppele);
				System.out.println(tmpInd.getGenomeString());
				returnGene.add(tmpInd.clone());
				counter++;
			}
		}
		return returnGene;
	}

	private Object crossOverIndividuals(Object first, Object second, int index) {
		Motif_simpleCell_Individual returnInd, firstInd, secondInd;
		Motif_simpleCell_Individual firstArg = (Motif_simpleCell_Individual) first;
		Motif_simpleCell_Individual secondArg = (Motif_simpleCell_Individual) second;
		firstInd = (Motif_simpleCell_Individual) firstArg.clone();
		secondInd = (Motif_simpleCell_Individual) secondArg.clone();

		returnInd = new Motif_simpleCell_Individual(index);
		int np = firstInd.getNumOfParameters();
		int mode = (int) Math.round(Math.floor(RandomManager.getRandom()
				* (np + 1)));

		int firstNotes = firstInd.getNumOfNotes();
		int secondNotes = secondInd.getNumOfNotes();
		int finalNoteNum = 0;

		int firstCrossPoint = (int) Math.round(Math.floor(RandomManager
				.getRandom()
				* firstNotes)) + 1;
		int secondCrossPoint = (int) Math.round(Math.floor(RandomManager
				.getRandom()
				* secondNotes)) + 1;
		finalNoteNum = firstCrossPoint + (secondNotes - secondCrossPoint);
		returnInd.makeEmptyGenome(finalNoteNum);

		int space = firstNotes - firstCrossPoint;
		for (int counter = 0; counter < np; counter++) {
			int crossPoint = (int) Math.round(Math.floor(RandomManager
					.getRandom()
					* space))
					+ firstCrossPoint;
			if (crossPoint == 0)
				crossPoint++;

			for (int i = 0; i < crossPoint; i++) {
				returnInd.setGenomElements(i, counter, firstInd
						.getGenomElements(i, counter));
			}
			int secondCount = secondNotes - 1;
			for (int i = finalNoteNum - 1; i >= crossPoint; i--) {
				returnInd.setGenomElements(i, counter, secondInd
						.getGenomElements(secondCount, counter));
				secondCount--;
			}
		}
		returnInd.fixParameters();
		return (Object) returnInd;

	}

	public void playAIndividual(int index) {

	}

	public void saveEventListToFile(String fileName) {
		PopulationOfEventList pEventList;
	}

	public void readEventListFromFile(String fileName) {
		PopulationOfEventList pEventList;
	}

	public void playAIndividual(int IDNumber, int generationID) {
		Motif_simpleCell_Individual tmpInd = (Motif_simpleCell_Individual) this.genomStocker
				.get(IDNumber);
		if (generationID == 0)
			tmpInd = (Motif_simpleCell_Individual) this.genomStocker
					.get(IDNumber);
		else if (generationID == 1)
			tmpInd = (Motif_simpleCell_Individual) this.thisGeneration
					.get(IDNumber);
		else if (generationID == 2)
			tmpInd = (Motif_simpleCell_Individual) this.nextGeneration
					.get(IDNumber);
		else if (generationID == 3)
			tmpInd = (Motif_simpleCell_Individual) this.workingGeneration
					.get(IDNumber);

		CommonEventList tmpEventList = tmpInd.convertToEventList();
		try {
			tmpEventList.playAsMIDISequence();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveAIndividual(int IDNumber, int generationID, String fileName) {
		Motif_simpleCell_Individual tmpInd = (Motif_simpleCell_Individual) this.genomStocker
				.get(IDNumber);
		if (generationID == 0)
			tmpInd = (Motif_simpleCell_Individual) this.genomStocker
					.get(IDNumber);
		else if (generationID == 1)
			tmpInd = (Motif_simpleCell_Individual) this.thisGeneration
					.get(IDNumber);
		else if (generationID == 2)
			tmpInd = (Motif_simpleCell_Individual) this.nextGeneration
					.get(IDNumber);
		else if (generationID == 3)
			tmpInd = (Motif_simpleCell_Individual) this.workingGeneration
					.get(IDNumber);

		CommonEventList tmpEventList = tmpInd.convertToEventList();
		tmpEventList.saveAsMIDISequence(fileName);
	}
	
	public int reInject(CommonEventList eventList, int index) {
		return index;
	}
}