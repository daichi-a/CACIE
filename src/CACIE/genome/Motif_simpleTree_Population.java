package CACIE.genome;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.RandomManager;
import CACIE.eventlist.CommonEventList;
import CACIE.eventlist.PopulationOfEventList;
import CACIE.eventlist.PlaybackSettings;

public class Motif_simpleTree_Population extends Abstract_Population {
	static int MAXEVLVALUE = 100;
	static int MINEVLVALUE = 0;
	static int ONEPOINT_CROSSOVER = 2048;
	static int PARTTREE_CROSSOVER = 2049;

	static int baseValueToMakeProportional = 10;

	protected ArrayList<Abstract_Individual> thisGeneration, workingGeneration,
			nextGeneration, genomeStocker;
	protected int populationSize;
	protected int mode = TreeIndividuals.MONOPHONY_MODE;
	protected ArrayList<Notes> notes;
	protected ArrayList<String> operatorArray;
	protected ArrayList<String> configArray;
	protected boolean operatorFrom = false;

	protected int[] tableOfIndexToApplyGO;
	protected int[] fitnessValueTable;

	private ArrayList<CommonEventList> evStockerThis;
	private ArrayList<CommonEventList> evStockerNext;
	private ArrayList<String> crossoveredIndividuals;
	//中身のStringは、(OffSpringIndex,firstParentIndex,secondParentIndex)
	private ArrayList<String> mutatedIndividuals;
	//中身のStringは、(Index,ChromosomeBeforeMutation,ChromosomeAfterMutation);
	
	boolean individualKeep = false;
	String individualKeepStyle = "NULL";
	String individualKeepDirectory = "NULL";
	int numOfGeneration = 0;
	int tempo = 120;

	public Motif_simpleTree_Population() {
	}

	public Motif_simpleTree_Population(int populationSize, int mode,
			ArrayList<Notes> notes) {
		System.out.println("Motif_simpleTree_Population:Gived " + notes.size()
				+ " terminal nodes");
		this.populationSize = populationSize;
		this.mode = mode;
		this.notes = notes;
		this.thisGeneration = new ArrayList<Abstract_Individual>(
				this.populationSize);
		this.thisGeneration.ensureCapacity(this.populationSize);
		this.workingGeneration = new ArrayList<Abstract_Individual>(
				this.populationSize);
		this.workingGeneration.ensureCapacity(this.populationSize);
		this.genomeStocker = new ArrayList<Abstract_Individual>();
		this.genomeStocker.ensureCapacity(this.genomeStocker.size());
		this.nextGeneration = new ArrayList<Abstract_Individual>(
				this.populationSize);
		this.nextGeneration.ensureCapacity(this.populationSize);
		this.fitnessValueTable = new int[this.populationSize];
		for (int i = 0; i < this.populationSize; i++) {
			this.fitnessValueTable[i] = 50;
		}
		this.evStockerThis = new ArrayList<CommonEventList>(this.populationSize);
		this.evStockerNext = new ArrayList<CommonEventList>(this.populationSize);
		
		crossoveredIndividuals = new ArrayList<String>(populationSize);
		mutatedIndividuals = new ArrayList<String>(populationSize);
	}

	public Motif_simpleTree_Population(int populationSize, int mode,
			ArrayList<Notes> notes, ArrayList<String> operators,
			ArrayList<String> configs) {
		this(populationSize, mode, notes);
		this.operatorFrom = true;
		this.operatorArray = operators;
		this.configArray = configs;
		extractReproductionConfig();
	}

	public void setTempo(int newTempo){
		tempo = newTempo;
	}
	
	protected void extractReproductionConfig(){
		int lineCounter = 0;
		while(lineCounter < configArray.size()){
			String configLine = configArray.get(lineCounter);
			StringTokenizer st = new StringTokenizer(configLine);
			String command = st.nextToken();
			boolean depth = false, maxLength = false, minLength = false;
			if(command.equals("KEEP_INDIVIDUAL")){
				if(st.nextToken().equals("ON")){
					individualKeep = true;
					individualKeepStyle = st.nextToken();
					individualKeepDirectory = st.nextToken();
				}
			}
			lineCounter++;
		}
	}
	
	public String getGenomeString(int popID, int index) {
		Motif_simpleTree_Individual tmpInd = new Motif_simpleTree_Individual();
		if (popID == 0)
			tmpInd = (Motif_simpleTree_Individual) this.thisGeneration
					.get(index);
		else if (popID == 1)
			tmpInd = (Motif_simpleTree_Individual) this.nextGeneration
					.get(index);
		else if (popID == 2)
			tmpInd = (Motif_simpleTree_Individual) this.genomeStocker
					.get(index);
		String returnString = tmpInd.getGenomeString();
		return returnString;
	}

	public Object getIndividual(int popID, int index) {
		Object returnObject = new Object();
		if (popID == 0)
			returnObject = this.thisGeneration.get(index);
		else if (popID == 1)
			returnObject = this.workingGeneration.get(index);
		else if (popID == 2)
			returnObject = this.genomeStocker.get(index);
		return returnObject;
	}

	public void initPopulation() {
		this.thisGeneration.clear();
		this.thisGeneration.ensureCapacity(this.populationSize);
		this.workingGeneration.clear();
		this.workingGeneration.ensureCapacity(this.populationSize);
		this.nextGeneration.clear();
		this.nextGeneration.ensureCapacity(this.populationSize);

		for (int i = 0; i < this.populationSize; i++) {
			Motif_simpleTree_Individual tmpInd = new Motif_simpleTree_Individual();

			if (this.operatorFrom)
				tmpInd = new Motif_simpleTree_Individual(i, this.mode,
						this.notes, this.operatorArray, this.configArray);
			else
				tmpInd = new Motif_simpleTree_Individual(i, this.mode,
						this.notes);

			tmpInd.generate();
			this.thisGeneration.add(tmpInd.clone());
		}
		System.err.println("Init : " + this.thisGeneration.size());

		this.generateEventList(0);
		numOfGeneration = 0;
		if(individualKeep)
			saveEvaluatedPopulation(evStockerThis, individualKeepDirectory, numOfGeneration, "presented");
	}
		
	protected void saveEvaluatedPopulation
		(ArrayList<CommonEventList> evaluatedEv, String directoryName, int currentNumOfGeneration, String state){
		PopulationOfEventList popEv = new PopulationOfEventList(evaluatedEv);
		popEv.writeToFile
			(directoryName + File.separator + currentNumOfGeneration + "_All_" + state +".dat");
		for(int i=0; i<evaluatedEv.size(); i++)
			evaluatedEv.get(i).saveAsMIDISequence
				(directoryName + File.separator + currentNumOfGeneration + "_" + i + "_" + state +".mid", tempo);
	}

	public void reproductPopulation() {
		numOfGeneration++;
		this.nextGeneration.clear();
		this.nextGeneration.ensureCapacity(this.populationSize);
		System.out.println("Reproduct : thisGeneSize : "
				+ this.thisGeneration.size());
		this.nextGeneration = this.crossOver(this.thisGeneration);
		System.out.println("Reproduct : nextGeneSize : "
				+ this.nextGeneration.size());
		if(individualKeep && individualKeepStyle.equals("ALL")){
			generateEventList(1);
			saveEvaluatedPopulation
				(evStockerNext, individualKeepDirectory, numOfGeneration, "BeforeMutation");
		}
		
		this.mutation(this.nextGeneration);
		this.generateEventList(1);
		//System.err.println("Motif_simpleTree_Population: nextGeneration.size() is " + nextGeneration.size());
	}
	
	public void replacePopulation() {
		if (nextGeneration.size() != 0) {
			//thisGeneration = (ArrayList)nextGeneration.clone();
			//evStockerThis = evStockerNext;
			//nextGeneration.clear();
			
			
			thisGeneration.clear();
			for(int i=0; i<nextGeneration.size(); i++)
				thisGeneration.add(nextGeneration.get(i));
				
			//System.out.println("Replace : PopulationSize : "
				//	+ this.thisGeneration.size());
			this.evStockerThis = this.evStockerNext;
			this.nextGeneration.clear();
			if(individualKeep)
				saveEvaluatedPopulation
					(evStockerThis, individualKeepDirectory, numOfGeneration, "presented");
			
		}
	}

	public ArrayList<String> getCrossoveredIndividualLog(){
		return crossoveredIndividuals;
	}
	
	public ArrayList<String> getMutatedIndividualLog(){
		return mutatedIndividuals;
	}
	
	public int sendToStore(int popID, int index) {
		this.genomeStocker.ensureCapacity(this.genomeStocker.size() + 1);
		if (popID == 0)
			this.genomeStocker.add(this.thisGeneration.get(index));
		else if (popID == 1)
			this.genomeStocker.add(this.nextGeneration.get(index));
		return 0;
	}

	public int evaluate(int index, int value) {
		if (value > MAXEVLVALUE)
			value = MAXEVLVALUE;
		else if (value < MINEVLVALUE)
			value = MINEVLVALUE;
		this.fitnessValueTable[index] = value;
		return value;
	}

	public int getEvaluated(int index) {
		return this.fitnessValueTable[index];
	}

	public void refreshEvaluated() {
		for (int i = 0; i < this.fitnessValueTable.length; i++) {
			this.fitnessValueTable[i] = 0;
		}
	}

	public int getPopulationSize() {
		return this.populationSize;
	}

	public void mutation(ArrayList<Abstract_Individual> nextGene) {
		mutatedIndividuals.clear();
		mutatedIndividuals.ensureCapacity(populationSize);
		for (int i = 0; i < this.populationSize; i++) {
			String chromosomes = "(";
			Motif_simpleTree_Individual tmpInd = (Motif_simpleTree_Individual) nextGene
					.get(i);
			System.out.println("Before Mutation : " + tmpInd.getGenomeString());
			chromosomes = chromosomes.toString() + tmpInd.getGenomeString() + ",";
			tmpInd.mutate();
			System.out.println("After  Mutation : " + tmpInd.getGenomeString());
			chromosomes = chromosomes.toString() + tmpInd.getGenomeString() + ")";
			mutatedIndividuals.add(chromosomes);
		}
	}

	private int rouletteTable(int sumOfFV, int[] FVTable) {
		int indexCounter = 0;
		int FVCounter = 0;
		int position = (int) Math.round(Math.floor(RandomManager.getRandom()
				* sumOfFV));
		while (indexCounter < this.populationSize) {
			FVCounter += FVTable[indexCounter];
			if (FVCounter >= position) {
				break;
			} else {
				indexCounter++;
			}
		}
		return indexCounter;
	}

	private ArrayList<Abstract_Individual> crossOver(ArrayList<Abstract_Individual> thisGene) {
		ArrayList<Abstract_Individual> returnGene = 
			new ArrayList<Abstract_Individual>(thisGene.size());
		returnGene.ensureCapacity(thisGene.size());
		
		crossoveredIndividuals.clear();
		crossoveredIndividuals.ensureCapacity(populationSize);

		int sumOfFitnessValue = 0;
		for (int i = 0; i < this.populationSize; i++) {
			sumOfFitnessValue += this.fitnessValueTable[i];
		}
		if (sumOfFitnessValue == 0) {
			for (int i = 0; i < this.populationSize; i++) {
				this.fitnessValueTable[i] = 50;
				sumOfFitnessValue += this.fitnessValueTable[i];
			}
		}

		if (thisGene.size() == this.populationSize) {
			int counter = 0;
			Motif_simpleTree_Individual tmpInd = new Motif_simpleTree_Individual(
					0, this.mode, this.notes);

			while (counter < populationSize) {
				boolean doppele = true;
				int turnCount = 0;
				int beforeIndex = 0;
				do {
					int crossFromIndex = this.rouletteTable(sumOfFitnessValue,
							this.fitnessValueTable);
					int crossToIndex = this.rouletteTable(sumOfFitnessValue,
							this.fitnessValueTable);
					System.out.print("(" + crossFromIndex + "," + crossToIndex
							+ ")" + " ");
					//Log
					String crossoverRecord = 
						"(" + counter + "," + crossFromIndex + "," + crossToIndex +")";
					crossoveredIndividuals.add(crossoverRecord);
					
					Motif_simpleTree_Individual firstInd = (Motif_simpleTree_Individual) thisGene
							.get(crossFromIndex);
					Motif_simpleTree_Individual secondInd = (Motif_simpleTree_Individual) thisGene
							.get(crossToIndex);
					int crossOverMode = (int) Math.round(Math
							.floor(RandomManager.getRandom() * 2));
					if (crossOverMode == 0)
						tmpInd = (Motif_simpleTree_Individual) this
								.crossOverIndividuals(ONEPOINT_CROSSOVER,
										counter, (Object) firstInd.clone(),
										(Object) secondInd.clone());
					else
						tmpInd = (Motif_simpleTree_Individual) this
								.crossOverIndividuals(PARTTREE_CROSSOVER,
										counter, (Object) firstInd.clone(),
										(Object) secondInd.clone());
					boolean inDoppele = true;
					for (int i = 0; i < returnGene.size(); i++) {
						Motif_simpleTree_Individual tmpInd2 = (Motif_simpleTree_Individual) returnGene
								.get(i);
						String tmpSt = tmpInd.getGenomeString();
						if (tmpSt.equals(tmpInd2.getGenomeString())) {
							inDoppele = false;
							break;
						}
					}

					if (inDoppele || turnCount >= populationSize)
						doppele = false;
					turnCount++;
					beforeIndex = crossFromIndex + 1;
					if (beforeIndex >= this.populationSize)
						beforeIndex = 0;
				} while (doppele);
				System.out.println(" ");
				returnGene.add(tmpInd.clone());
				counter++;
			}
		}
		return returnGene;
	}

	private Object crossOverIndividuals(int coMode, int index, Object first,
			Object second) {
		Object returnInd = new Object();
		//int chMode = (int) Math
		//		.round(Math.floor(RandomManager.getRandom() * 2))
		//		+ ONEPOINT_CROSSOVER;

		if (coMode == ONEPOINT_CROSSOVER)
			returnInd = this.onePointCrossOver(index, first, second);
		else if (coMode == PARTTREE_CROSSOVER)
			returnInd = this.coOptCrossOver(index, first, second);
		returnInd = this.onePointCrossOver(index, first, second);
		return returnInd;
	}

	private Object coOptCrossOver(int index, Object first, Object second) {
		int mainSel = (int) Math.round(Math
				.floor(RandomManager.getRandom() * 2));
		if (mainSel == 0) {
			Object tmp = second;
			second = first;
			first = tmp;
		}
		Motif_simpleTree_Individual returnInd;
		Motif_simpleTree_Individual firstInd = (Motif_simpleTree_Individual) first;
		Motif_simpleTree_Individual secondInd = (Motif_simpleTree_Individual) second;
		ArrayList<TreeNodes> firstGenom = firstInd.getGenomeArray();
		ArrayList<TreeNodes> secondGenom = secondInd.getGenomeArray();

		int frontOffset = 0;
		int firstCrossPoint = (int) Math.round(Math.floor(RandomManager
				.getRandom()
				* (firstInd.getNumOfNodes()-frontOffset))) + frontOffset;
		while (firstInd.getNodeMode(firstCrossPoint) == TreeNodes.NONTERMINAL) {
			firstCrossPoint++;
		}
		int secondCrossPoint = (int) Math.round(Math.floor(RandomManager
				.getRandom()
				* (secondInd.getNumOfNodes()-frontOffset))) + frontOffset;
		while (secondInd.getNodeMode(secondCrossPoint) == TreeNodes.NONTERMINAL) {
			secondCrossPoint++;
		}

		ArrayList<Integer> firstIndex = TreeIndividuals.getPartTreeIndex(firstGenom,
				firstCrossPoint);
		ArrayList<Integer> secondIndex = TreeIndividuals.getPartTreeIndex(secondGenom,
				secondCrossPoint);
		Integer tmpInt;
		tmpInt = (Integer) firstIndex.get(0);
		int firstFromIndex = tmpInt.intValue();
		tmpInt = (Integer) firstIndex.get(1);
		//int firstToIndex = tmpInt.intValue();
		tmpInt = (Integer) secondIndex.get(0);
		int secondFromIndex = tmpInt.intValue();
		tmpInt = (Integer) secondIndex.get(1);
		int secondToIndex = tmpInt.intValue();
		// System.out.println("first : " + firstGenom.size() + " " +
		// firstFromIndex
		// + " " + firstToIndex + " second : " + secondGenom.size() + " " +
		// secondFromIndex + " " + secondToIndex);
		if(firstFromIndex < 0 || 
				secondFromIndex < 0 || secondFromIndex > secondToIndex){
			System.out.println("Motif_simpleTree_Popularion: coOptCrossover: indexes are miss generated.");
			returnInd = (Motif_simpleTree_Individual)firstInd.clone();
		}
		else{
		returnInd = (Motif_simpleTree_Individual) firstInd.clone();
		returnInd.IDNumber = index;
		ArrayList<TreeNodes> newGenom = returnInd.getGenomeArray();
		TreeIndividuals.replaceNodes(newGenom,
				TreeIndividuals.extractGenomeArray(secondGenom,
						secondFromIndex, secondToIndex), firstFromIndex,
				firstFromIndex);
		}
		return (Object) returnInd;
	}

	private Object onePointCrossOver(int index, Object first, Object second) {
		int mainSel = (int) Math.round(Math
				.floor(RandomManager.getRandom() * 2));
		if (mainSel == 0) {
			Object tmp = second;
			second = first;
			first = tmp;
		}
		Motif_simpleTree_Individual returnInd = new Motif_simpleTree_Individual(
				index, this.mode, this.notes, this.operatorArray,
				this.configArray);
		returnInd.extractConfigsForInitialize();
		Motif_simpleTree_Individual firstInd = (Motif_simpleTree_Individual) first;
		Motif_simpleTree_Individual secondInd = (Motif_simpleTree_Individual) second;
		int SCTable1[] = new int[firstInd.getNumOfNodes()];
		int SCTable2[] = new int[secondInd.getNumOfNodes()];

		SCTable1[0] = firstInd.getStackCount(0);
		int maxDepth1 = 0;
		for (int i = 1; i < SCTable1.length; i++) {
			SCTable1[i] = SCTable1[i - 1] + firstInd.getStackCount(i);
			if (SCTable1[i] < maxDepth1)
				maxDepth1 = SCTable1[i];
		}
		//if(maxDepth1 == 0){
		//	System.out.println("maxDepth1 is: " + maxDepth1);
		//	for(int i=0; i<SCTable1.length; i++)
		//		System.out.print(SCTable1[i] + ",");
		//}
		//System.out.println();
		
		SCTable2[0] = secondInd.getStackCount(0);
		int maxDepth2 = 0;
		for (int i = 1; i < SCTable2.length; i++) {
			SCTable2[i] = SCTable2[i - 1] + secondInd.getStackCount(i);
			if (SCTable2[i] < maxDepth2)
				maxDepth2 = SCTable2[i];
		}
		int commonDepth = 0;
		if (maxDepth1 < maxDepth2)
			commonDepth = maxDepth2;
		else
			commonDepth = maxDepth1;
		//System.out.println();
		//System.out.println("maxDepth1: " + maxDepth1 + " maxDepth2: " + maxDepth2 + " Common Depth : " + commonDepth);

		int crossSC = 0;
		if(commonDepth == 0)
			crossSC = 0;
		else{
			//commonDepth = commonDepth * -1;
			crossSC = (int) Math.round(RandomManager.getRandom()
					* (commonDepth+1)) -1;
			//crossSC = (crossSC + 1) * -1;
			//if (crossSC != -1)
				//crossSC++;
		}
		//System.out.println("crossSC : " + crossSC);

		//特殊なノードがトップに来ている時の余白の設定
		int frontOffset = 0;
		
		int counter = 0;
		for (int i = frontOffset; i < SCTable1.length; i++) {
			if (SCTable1[i] == crossSC)
					counter++;
		}
		if(counter==0)
			System.out.println("There is no cross point in first parent. corssSC is:" + crossSC);
		int fcsp = (int) Math.round(Math.floor(RandomManager.getRandom()
				* counter));
		// System.out.println("firstGenom has " + counter + " nodes keep
		// crossSC.
		// and crosspoint is " + fcsp);
		counter = 0;
		int point = 0;
		for (int i = frontOffset; i < SCTable1.length; i++) {
			if (SCTable1[i] == crossSC) {
				//System.out.print("(" + i + ":" + SCTable1[i] + ":" + counter + "),");
				if (counter == fcsp){
					point = i + 1;
					break;
				}
				else
					counter++;
			}
		}
		int firstCrossPoint = point;
		if(firstCrossPoint < frontOffset){
			System.out.println("firstCrossPoint less than frontOffset:(" + firstCrossPoint + "," + frontOffset + ")");
		}
		
		counter = 0;
		for (int i = frontOffset; i < SCTable2.length; i++) {
			if (SCTable2[i] == crossSC)
				counter++;
		}
		if(counter==0)
			System.out.println("There is no cross point in second parent. corssSC is:" + crossSC);

		int scsp = (int) Math.round(Math.floor(RandomManager.getRandom() * counter));
		// System.out.println("secondGenom has " + counter + " nodes keep
		// crossSC.
		// and crosspoint is " + scsp);
		counter = 0;
		point = 0;
		for (int i = frontOffset; i < SCTable2.length; i++) {
			if (SCTable2[i] == crossSC) {
				//System.out.print("(" + i + ":" + SCTable2[i] + ":" + counter + "),");
				if (counter == scsp){
					point = i + 1;
					break;
				}
				else
					counter++;
			}
		}
		System.err.println("");
		int secondCrossPoint = point;
		if(secondCrossPoint < frontOffset){
			System.out.println("seondCrossPoint less than frontOffset:(" + secondCrossPoint + "," + frontOffset + ")");
		}
		
		//System.out.println("crossPoint of first Genome : " + firstCrossPoint +
		// " second Genome : " + secondCrossPoint +", fscp is:" + fcsp + " scsp is:" + scsp +
		// ", frontOffset is:" + frontOffset);

		int scadd1 = 0;
		for (int i = 0; i < firstCrossPoint; i++) {
			TreeNodes tmpNode = (TreeNodes) firstInd.getNode(i);
			scadd1 += tmpNode.getStackCount();
		}
		int scadd2 = 0;
		for (int i = secondCrossPoint; i < SCTable2.length; i++) {
			TreeNodes tmpNode = (TreeNodes) secondInd.getNode(i);
			scadd2 += tmpNode.getStackCount();
		}
		// System.out.println("Front genom SC : " + scadd1 + " Second genomSC :
		// " +
		// scadd2);

		int newSize = firstCrossPoint + (SCTable2.length - secondCrossPoint);
		ArrayList<TreeNodes> newGenom = new ArrayList<TreeNodes>(newSize);
		for (int i = 0; i < firstCrossPoint; i++) {
			TreeNodes tmpNode = (TreeNodes) firstInd.getNode(i);
			newGenom.add(tmpNode.clone());
		}
		for (int i = secondCrossPoint; i < SCTable2.length; i++) {
			TreeNodes tmpNode = (TreeNodes) secondInd.getNode(i);
			newGenom.add(tmpNode.clone());
		}
		int scCounter = 0;
		TreeNodes tmpNode = (TreeNodes) newGenom.get(0);
		scCounter = tmpNode.getStackCount();
		for (int i = 1; i < newGenom.size(); i++) {
			tmpNode = (TreeNodes) newGenom.get(i);
			scCounter += tmpNode.getStackCount();
		}
		if (scCounter != 1){
			System.out
					.println("Faild to OnePointCrossOver. new genome's SC sum is "
							+ scCounter);
			returnInd.setGenomArray(firstInd.getGenomeArray());
		}
		else{
			returnInd.setGenomArray(newGenom);
		}
		return (Object) returnInd;

	}

	public PopulationOfEventList convertToPopulationEventList(int popID) {
		return new PopulationOfEventList(this.populationSize);
	}

	public void convertFromPopulationEventList(int popID,
			PopulationOfEventList eventList) {

	}

	private void generateEventList(int popID) {
		ArrayList<Abstract_Individual> toProcessGenome = new ArrayList<Abstract_Individual>();
		ArrayList<CommonEventList> toProcessEv = new ArrayList<CommonEventList>();
		if (popID == 0) {
			toProcessGenome = this.thisGeneration;
			toProcessEv = this.evStockerThis;
		} else if (popID == 1) {
			toProcessGenome = this.nextGeneration;
			toProcessEv = this.evStockerNext;
		} else {
			System.out.println("Motif_simpleTree_Population : No popID");
		}

		EventListGenerator evG = new EventListGenerator(toProcessGenome);
		evG.start();
		try {
			evG.join();
		} catch (InterruptedException e) {
			System.err.println(e);
			System.exit(1);
		}
		//チェック用
		for(int i=0; i<toProcessGenome.size(); i++){
			Motif_simpleTree_Individual individual = (Motif_simpleTree_Individual)toProcessGenome.get(i);
			if(individual.getGeneratedEventList() == null){
				System.err.println("Motif_simpleTree_Populaton:generateEventList1: generated eventlist is null. index is " + i);
				System.exit(1);
			}
		}
		
		
		toProcessEv.clear();
		int until = toProcessGenome.size();
		for (int i = 0; i < until; i++) {
			Motif_simpleTree_Individual tmpInd = (Motif_simpleTree_Individual) toProcessGenome
					.get(i);
			toProcessEv.ensureCapacity(toProcessEv.size() + 1);
			toProcessEv.add(tmpInd.getGeneratedEventList());
		}

		//チェック用
		for(int i=0; i<toProcessEv.size(); i++){
			if(toProcessEv.get(i) == null){
				System.err.println("Motif_simpleTree_Populaton:generateEventList2: generated eventlist is null. index is " + i);
				System.exit(1);
			}
		}

		System.out.println();
		System.out.println("Finish");
	}

	public CommonEventList convertToEventList(int popID, int index) {
		CommonEventList eventList = new CommonEventList(index);
		if (popID == 0)
			eventList = (CommonEventList) this.evStockerThis.get(index);
		else if (popID == 1)
			eventList = (CommonEventList) this.evStockerNext.get(index);
		else
			System.out
					.println("Motif_simpleTree_Population : No population ID "
							+ popID);
		if(eventList == null){
			System.err.println("Motif_simpleTree_Population:convertToEventList: eventList is null. popID is " 
					+ popID + " index is " + index);
			System.exit(1);
		}
		return eventList;

	}

	/** Converts the selected source individual through output-only SCALE/BARFIX settings. */
	public CommonEventList convertToPlaybackEventList(int popID,int index,PlaybackSettings settings){
		Motif_simpleTree_Individual individual;
		if(popID==0) individual=(Motif_simpleTree_Individual)thisGeneration.get(index);
		else if(popID==1) individual=(Motif_simpleTree_Individual)nextGeneration.get(index);
		else if(popID==2) individual=(Motif_simpleTree_Individual)genomeStocker.get(index);
		else throw new IllegalArgumentException("Unknown population ID: "+popID);
		return individual.createPlaybackClone(settings).convertToEventList();
	}

	public int reInject(CommonEventList eventList, int index) {
		//indexが-1の時は一番fitnessが低いところに入れる
		int indexToApply = index;
		if(indexToApply == -1){
		int minFitness = 101;
		for (int i = populationSize - 1; i >= 0; i--) {
			if (minFitness > this.fitnessValueTable[i]) {
				indexToApply = i;
				minFitness = this.fitnessValueTable[i];
			}
		}
			
		}
			reInject(indexToApply, eventList);

		return indexToApply;
	}

	public void reInject(int index, CommonEventList eventList) {
		Motif_simpleTree_Individual tmpInd = (Motif_simpleTree_Individual) this.thisGeneration
				.get(index);
		tmpInd.setConfigArray(configArray);
		tmpInd.convertFromEventList(eventList);
		Thread th = new Thread(tmpInd);
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		this.evStockerThis.remove(index);
		this.evStockerThis.add(index, tmpInd.getGeneratedEventList());
	}

	public void convertFromEventList(int popID, int index,
			CommonEventList eventList) {
		if (popID == 0)
			reInject(index, eventList);
	}

	public void playAIndividual(int IDNumber, int generationID) {
		Motif_simpleTree_Individual tmpInd = new Motif_simpleTree_Individual();
		if (generationID == 0)
			tmpInd = (Motif_simpleTree_Individual) this.thisGeneration
					.get(IDNumber);
		else if (generationID == 1)
			tmpInd = (Motif_simpleTree_Individual) this.nextGeneration
					.get(IDNumber);
		else if (generationID == 2)
			tmpInd = (Motif_simpleTree_Individual) this.genomeStocker
					.get(IDNumber);
		else if (generationID == 3)
			tmpInd = (Motif_simpleTree_Individual) this.genomeStocker
					.get(IDNumber);

		CommonEventList tmpEventList = tmpInd.createPlaybackClone(PlaybackSettings.DEFAULT).convertToEventList();
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
		Motif_simpleTree_Individual tmpInd = new Motif_simpleTree_Individual();
		if (generationID == 0)
			tmpInd = (Motif_simpleTree_Individual) this.thisGeneration
					.get(IDNumber);
		else if (generationID == 1)
			tmpInd = (Motif_simpleTree_Individual) this.nextGeneration
					.get(IDNumber);
		else if (generationID == 2)
			tmpInd = (Motif_simpleTree_Individual) this.genomeStocker
					.get(IDNumber);
		CommonEventList tmpEventList = tmpInd.createPlaybackClone(PlaybackSettings.DEFAULT).convertToEventList();
		tmpEventList.writeToFile(fileName);
		tmpEventList.saveAsMIDISequence(fileName + ".mid");
	}

	public String getGenomString(int popID, int index) {
		Motif_simpleTree_Individual tmpInd = new Motif_simpleTree_Individual();
		if (popID == 0)
			tmpInd = (Motif_simpleTree_Individual) this.thisGeneration
					.get(index);
		else if (popID == 1)
			tmpInd = (Motif_simpleTree_Individual) this.nextGeneration
					.get(index);
		else if (popID == 2)
			tmpInd = (Motif_simpleTree_Individual) this.genomeStocker
					.get(index);
		String returnString = tmpInd.getGenomeString();
		return returnString;
	}

}

class EventListGenerator extends Thread {
	ArrayList<Abstract_Individual> population;
	ArrayList<Thread> ThreadStocker;

	EventListGenerator(ArrayList<Abstract_Individual> _population) {
		population = _population;
		ThreadStocker = new ArrayList<Thread>(population.size());

		int until = population.size();
		int counter = 0;
		while (counter < until) {
			Motif_simpleTree_Individual tmpInd = (Motif_simpleTree_Individual) population
					.get(counter);
			Thread th = new Thread(tmpInd);
			ThreadStocker.add(th);
			counter++;
		}
	}

	public void run() {
		int until = this.population.size();
		int counter = 0;
		while (counter < until) {
			Thread th = (Thread) this.ThreadStocker.get(counter);
			th.start();
			counter++;
		}
		counter = 0;
		while (counter < until) {
			Thread th = (Thread) this.ThreadStocker.get(counter);
			try {
				th.join();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			counter++;
		}
		
		//ちゃんと生成されているかのチェック
		for(int i=0;i<population.size(); i++){
			Motif_simpleTree_Individual individual = 
				(Motif_simpleTree_Individual)population.get(i);
			if(individual.getGeneratedEventList() == null){
				System.err.println("EventListGenerator:run: generated eventlist is null:" + i);
				System.exit(1);
			}
		}
	}
}
