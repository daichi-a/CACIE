package CACIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.Motif_simpleTree_Population;
import CACIE.genome.Notes;
import CACIE.genome.OneNote;
import CACIE.genome.TreeIndividuals;
import CACIE.ui.ConfigFile;

public class TESTTreeWList {

    public static void main(String[] Args){
	ArrayList configList = ConfigFile.readParametersFromFile(Args[0]);
	ArrayList oprList = (ArrayList)configList.get(0);
	ArrayList confList = (ArrayList)configList.get(1);
	for(int i=0; i<oprList.size(); i++){
	    String tmpSt = (String)oprList.get(i);
	    System.out.print(tmpSt + " ");
	}
	System.out.println();
	ArrayList eventListsCash = new ArrayList(0);
	Motif_simpleTree_Population population = new Motif_simpleTree_Population(16, TreeIndividuals.POLYPHONY_MODE, getExNotes(), oprList, confList);
	population.initPopulation();
	outputData(population, 0, "./TESTDATA/TREE/0-init/data", eventListsCash);

	BufferedReader in;
	in = new BufferedReader(new InputStreamReader(System.in));

	CommonEventList tmpEventList = new CommonEventList(0);
	
	evaluateEachIndividual(population, in, eventListsCash, tmpEventList);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 1, "./TESTDATA/TREE/1-g/data", eventListsCash);

	evaluateEachIndividual(population, in, eventListsCash, tmpEventList);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 2, "./TESTDATA/TREE/2-g/data", eventListsCash);

	evaluateEachIndividual(population, in, eventListsCash, tmpEventList);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 3, "./TESTDATA/TREE/3-g/data", eventListsCash);	

	try{
	    in.close();
	}
	catch(IOException e){
	    System.err.println(e);
	    System.exit(1);
	}

    }

    private static void outputData(Motif_simpleTree_Population population, int generation, String dirName, ArrayList cash){
	int until = population.getPopulationSize();
	cash.clear();
	cash.ensureCapacity(until);
	for(int i=0; i<until; i++){
	    CommonEventList tmpEventList = population.convertToEventList(0, i);
	    cash.add(tmpEventList);
	    tmpEventList.writeToFile(new String(dirName + generation + "_" + i + ".data"));
	    tmpEventList.saveAsMIDISequence(new String(dirName + generation + "_" + i + ".mid"));
	}
    }

    private static void evaluateEachIndividual(Motif_simpleTree_Population population, BufferedReader in, ArrayList cash, CommonEventList eventList){
	String tmpSt = new String("NULL");
	System.out.println("Evaluating");
	int until = population.getPopulationSize();
	
	for(int i=0; i<until; i++){
	    eventList.stopMIDISequence();
	    eventList = (CommonEventList)cash.get(i);
	    System.out.println(eventList.getGenomeString());
	    try {
			eventList.playAsMIDISequence();
		} catch (MidiUnavailableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidMidiDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    System.out.print("Input fitness value (0-100) : ");
	    try{
		tmpSt = in.readLine();
	    }
	    catch(IOException e){
		System.err.println(e);
		System.exit(1);
	    }
	    System.out.println();
	    population.evaluate(i, Integer.parseInt(tmpSt));
	}
	eventList.stopMIDISequence();
    }
    
    private static ArrayList getExNotes(){
	/*
	  0:(60, 100, 16), 1:(62, 110, 8), 2:(64, 80, 4), 3:(65, 75, 32),
	  4:(67, 90, 24), 5:(69, 65, 12), 6:(71, 70, 48), 7:(72, 60, 64)
	  8:(72, 0, 4)
	*/

	ArrayList initialNotes = new ArrayList(9);
	initialNotes.ensureCapacity(8);
	Notes tmpNotes = new Notes(new OneNote(60, 100, 0, 16));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(62, 100, 0, 8));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(64, 80, 0, 4));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(65, 75, 0, 32));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(67, 90, 0, 24));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(69, 65, 0, 12));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(71, 70, 0, 48));
	initialNotes.add(tmpNotes);
	tmpNotes = new Notes(new OneNote(72, 60, 0, 64));
	initialNotes.add(tmpNotes);
	//tmpNotes = new Notes(new OneNote(72, 0, 0, 4));
	//initialNotes.add(tmpNotes);
	for(int i=0; i<initialNotes.size(); i++){
	    tmpNotes = (Notes)initialNotes.get(i);
	    OneNote tmpNote = tmpNotes.getNote(0);
	    System.err.print(i + ":(" + tmpNote.getNoteNumber() + "," + tmpNote.getVelocity() + "," + tmpNote.getDuration() + ") ");
	}
	System.out.println();
	return initialNotes;
    }
}
