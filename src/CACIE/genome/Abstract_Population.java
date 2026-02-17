package CACIE.genome;

import CACIE.eventlist.CommonEventList;
import CACIE.eventlist.PopulationOfEventList;
import java.util.ArrayList;

public abstract class Abstract_Population {

	public final static int DEF_NUM_INDIVIDUALS = 36;

	//public final static int RANDOM_MODE = 1;

	//int geneticOperationMode = this.RANDOM_MODE;
	public static int geneticOperationMode = 1;
	
	abstract public Object getIndividual(int popID, int index);

	abstract public void initPopulation();

	abstract public void reproductPopulation();

	abstract public void replacePopulation();
	
	abstract public int evaluate(int fromIndex, int value);
	abstract public int reInject(CommonEventList eventList, int index);
	abstract public int getEvaluated(int fromIndex);

	abstract public PopulationOfEventList convertToPopulationEventList(int popID);

	abstract public void convertFromPopulationEventList(int popID,
			PopulationOfEventList eventList);

	abstract public CommonEventList convertToEventList(int popID, int index);

	abstract public void convertFromEventList(int popID, int index,
			CommonEventList eventList);

	abstract public void playAIndividual(int IDNumber, int generationID);

	abstract public void saveAIndividual(int IDNumber, int generationID,
			String fileName);

	abstract public String getGenomeString(int popID, int index);

	abstract public void refreshEvaluated();

	abstract public int sendToStore(int popID, int fromIndex);

	abstract public ArrayList<String> getCrossoveredIndividualLog();
	abstract public ArrayList<String> getMutatedIndividualLog();
	abstract public void setTempo(int newTempo);
}