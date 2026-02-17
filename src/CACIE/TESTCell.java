package CACIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.Motif_simpleCell_Individual;
import CACIE.genome.Motif_simpleCell_Population;
import CACIE.genome.Notes;
import CACIE.genome.OneNote;

public class TESTCell {

    public static void main(String[] Args){	
	Motif_simpleCell_Population population = new Motif_simpleCell_Population(16);
	population.initPopulation();
	outputData(population, 0, "./TESTDATA/CELL/0-init/data");

	BufferedReader in;
	in = new BufferedReader(new InputStreamReader(System.in));

	evaluateEachIndividual(population, in);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 1, "./TESTDATA/CELL/1-g/data");

	evaluateEachIndividual(population, in);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 2, "./TESTDATA/CELL/2-g/data");

	evaluateEachIndividual(population, in);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 3, "./TESTDATA/CELL/3-g/data");	

	try{
	    in.close();
	}
	catch(IOException e){
	    System.err.println(e);
	    System.exit(1);
	}

    }

    private static void outputData(Motif_simpleCell_Population population, int generation, String dirName){
	for(int i=0; i<population.getPopulationSize(); i++){
	    CommonEventList tmpEventList = population.convertToEventList(0, i);
	    tmpEventList.writeToFile(new String(dirName + generation + "_" + i + ".data"));
	    tmpEventList.saveAsMIDISequence(new String(dirName + generation + "_" + i + ".mid"));
	}
    }

    private static void evaluateEachIndividual(Motif_simpleCell_Population population, BufferedReader in){
	String tmpSt = new String("NULL");
	System.out.println("Evaluating");
	int until = population.getPopulationSize();
	
	
	    
	for(int i=0; i<until; i++){
	    Motif_simpleCell_Individual tmpInd = (Motif_simpleCell_Individual)population.getIndividual(0,i);
	    System.out.println(tmpInd.getGenomeString());
	    CommonEventList eventList = tmpInd.convertToEventList();
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
    }
    
    private static ArrayList getExNotes(){
	/*
	 0:(60, 100, 16), 1:(62, 110, 8), 2:(64, 80, 4), 3:(65, 75, 32),
	 4:(67, 90, 24), 5:(69, 65, 12), 6:(71, 70, 48), 7:(72, 60, 64)
	 8:(72, 0, 
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
	tmpNotes = new Notes(new OneNote(72, 0, 0, 16));
	initialNotes.add(tmpNotes);
	for(int i=0; i<initialNotes.size(); i++){
	    tmpNotes = (Notes)initialNotes.get(i);
	    OneNote tmpNote = tmpNotes.getNote(0);
	    System.err.print(i + ":(" + tmpNote.getNoteNumber() + "," + tmpNote.getVelocity() + "," + tmpNote.getDuration() + ") ");
	}
	System.out.println();
	return initialNotes;
    }
}