package CACIE.genome;

import CACIE.eventlist.CommonEventList;

public class Individual extends Abstract_Individual {

	//ArrayList<Abstract_Individual> INDS;
	Abstract_Individual individual;
	
	String genomeType;

	public Individual(String gType) {
		genomeType = gType;
		if (genomeType.equals("2DGA")) {
			individual = new Motif_simpleCell_Individual(0);
		}
	}

	public Abstract_Individual clone(){
		Individual returnIndividual = new Individual(genomeType);
		returnIndividual.set(individual);
		return returnIndividual;
	}
	
	public void set(Abstract_Individual o) {
		individual = o;
	}

	public void generate() {
		individual.generate();
	}

	// abstract public Object clone();
	
	public void makeEmptyGenome(int size) {
		individual.makeEmptyGenome(size);
	}

	// abstract public void mutation();

	public CommonEventList convertToEventList() {
		return individual.convertToEventList();
	}

	public void convertFromEventList(CommonEventList eventList) {
		individual.convertFromEventList(eventList);
	}

	public int getNumOfNotes() {
		return individual.getNumOfNotes();
	}

	public String getGenomeString() {
		return individual.getGenomeString();
	}

}