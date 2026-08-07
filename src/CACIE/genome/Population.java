//Abstract_Populationの子クラスを扱うためのクラス
//これのコンストラクタでgenomeTypeを指定してやることで、そのクラスを使うことが出来る
package CACIE.genome;

import java.util.ArrayList;
import CACIE.eventlist.CommonEventList;
import CACIE.eventlist.PopulationOfEventList;
import CACIE.eventlist.PlaybackSettings;

public class Population extends Abstract_Population {
	String genomeType;

	Abstract_Population population;
	private PlaybackSettings playbackSettings=PlaybackSettings.DEFAULT;
		
	public Population(int populationSize, String gType, int treeMode,
			ArrayList<Notes> notes, ArrayList<String> oprList, ArrayList<String> configList) {
		this.genomeType = gType.toString();

		if (genomeType.equals("2DGA")){
			Motif_simpleCell_Population aPop = new Motif_simpleCell_Population(
					populationSize);
			population = aPop;
		} else if (genomeType.equals("TREE")) {
			Motif_simpleTree_Population aPop = new Motif_simpleTree_Population(populationSize, treeMode, notes, oprList, configList);
			population = aPop;
		} 
	}

	public Object getIndividual(int popID, int index) {
		Object returnObject = new Object();
		returnObject = population.getIndividual(popID, index);
		return returnObject;
	}

	public void initPopulation() {
		population.initPopulation();
	}

	public int reInject(CommonEventList eventList, int index) {
		return population.reInject(eventList, index);
	}

	public int sendToStore(int popID, int fromIndex) {
		return population.sendToStore(popID, fromIndex);
	}

	public int evaluate(int fromIndex, int value) {
		return population.evaluate(fromIndex, value);
	}

	public int getEvaluated(int fromIndex) {
		return population.getEvaluated(fromIndex);
	}

	public void refreshEvaluated() {
		population.refreshEvaluated();
	}

	public void reproductPopulation() {
		population.reproductPopulation();
	}

	public void replacePopulation() {
		population.replacePopulation();
	}

	public void convertFromEventList(int popID, int index, CommonEventList o) {
		population.convertFromEventList(popID, index, o);
	}

	public void convertFromPopulationEventList(int popID,
			PopulationOfEventList o) {
		population.convertFromPopulationEventList(popID, o);
	}

	public void playAIndividual(int IDNumber, int generationID) {
		population.playAIndividual(IDNumber, generationID);
	}

	public void saveAIndividual(int IDNumber, int generationID, String fileName) {
		population.saveAIndividual(IDNumber, generationID, fileName);
	}

	public PopulationOfEventList convertToPopulationEventList(int popID) {
		return population.convertToPopulationEventList(popID);
	}

	public CommonEventList convertToEventList(int popID, int index) {
		if(population instanceof Motif_simpleTree_Population)
			return ((Motif_simpleTree_Population)population).convertToPlaybackEventList(popID,index,playbackSettings);
		return population.convertToEventList(popID,index);
	}

	public void setPlaybackSettings(PlaybackSettings settings){this.playbackSettings=settings;}
	public PlaybackSettings getPlaybackSettings(){return playbackSettings;}

	public String getGenomeString(int popID, int index) {
		return population.getGenomeString(popID, index);
	}

	public ArrayList<String> getCrossoveredIndividualLog(){
		return population.getCrossoveredIndividualLog();
	}
	
	public ArrayList<String> getMutatedIndividualLog(){
		return population.getMutatedIndividualLog();
	}
	
	public void setTempo(int newTempo){
		population.setTempo(newTempo);
	}
	
}
