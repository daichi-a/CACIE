package CACIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.Motif_simpleTree_Population;
import CACIE.genome.TreeIndividuals;
import CACIE.ui.ConfigFile;

public class TESTPieceWList {

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

	BufferedReader in;
	in = new BufferedReader(new InputStreamReader(System.in));	
	ArrayList givedNotes = getExNotes("TESTDATA/PIECE/base/base", 7, in);
	Motif_simpleTree_Population population = new Motif_simpleTree_Population(16, TreeIndividuals.POLYPHONY_MODE,givedNotes, oprList, confList);
	population.initPopulation();

	outputData(population, 0, "./TESTDATA/PIECE/0-init/data", eventListsCash);

	CommonEventList tmpEventList = new CommonEventList(0);
	evaluateEachIndividual(population, in, eventListsCash, tmpEventList);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 1, "./TESTDATA/PIECE/1-g/data", eventListsCash);

	evaluateEachIndividual(population, in, eventListsCash, tmpEventList);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 2, "./TESTDATA/PIECE/2-g/data", eventListsCash);

	evaluateEachIndividual(population, in, eventListsCash, tmpEventList);
	population.reproductPopulation();
	population.replacePopulation();
	outputData(population, 3, "./TESTDATA/PIECE/3-g/data" ,eventListsCash);	

	try{
	    in.close();
	}
	catch(IOException e){
	    System.err.println(e);
	    System.exit(1);
	}

    }

    private static void outputData(Motif_simpleTree_Population population, int generation, String dirName, ArrayList eventLists){
	int until = population.getPopulationSize();
	eventLists.clear();
	eventLists.ensureCapacity(until);
	for(int i=0; i<until; i++){
	    CommonEventList tmpEventList = population.convertToEventList(0, i);
	    eventLists.add(tmpEventList);
	    tmpEventList.writeToFile(new String(dirName + generation + "_" + i + ".data"));
	    tmpEventList.saveAsMIDISequence(new String(dirName + generation + "_" + i + ".mid"));
	}
    }

    private static void evaluateEachIndividual(Motif_simpleTree_Population population, BufferedReader in, ArrayList eventLists, CommonEventList alist){
	String tmpSt = new String("NULL");
	System.out.println("Evaluating");
	int until = population.getPopulationSize();
		    
	for(int i=0; i<until; i++){
	    alist.stopMIDISequence();
	    alist = (CommonEventList)eventLists.get(i);;
	    System.out.println(alist.getGenomeString());
	    try {
			alist.playAsMIDISequence();
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
	alist.stopMIDISequence();
    }
    
    private static ArrayList getExNotes(String arg, int base, BufferedReader in){
	int until = base;
	ArrayList includeMotif = new ArrayList(base);
	includeMotif.ensureCapacity(until);
	for(int i=0; i<until; i++){
	    CommonEventList tmpEventList = new CommonEventList(i);
	    tmpEventList.readFromFile(arg + i + ".data");
	    //recommandMotif(tmpEventList, in);
	    includeMotif.add(tmpEventList.getNotes());
	}
	return includeMotif;
    }
    
    private static void recommandMotif(CommonEventList tmpEventList, BufferedReader in){
	String tmpSt;
	try {
		tmpEventList.playAsMIDISequence();
	} catch (MidiUnavailableException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (InvalidMidiDataException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try{
	    tmpSt = in.readLine();
	}
	catch(IOException e){
	    System.err.println(e);
	}

    }	

}
