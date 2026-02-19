package CACIE;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.OneNote;
import CACIE.genome.Notes;
import CACIE.midi.MIDISequence;

public class CACIE_StringToMidi extends CACIE_O
{
  public static void main(String args[])
  {
    final StringBuilder configFileSelection = new StringBuilder("./ConfigData/BachCelloSonata1.config"); // default classic
    final StringBuilder dataFileSelection = new StringBuilder("./ConfigData/BachCelloSonata1.data"); // default classic
    
    configures(configFileSelection.toString(), dataFileSelection.toString());
    
    /* 
    CommonEventList eventList = convertStringToMidi(
        "(S (S (S (S (S (S 4 15) (S 0 1) ) (S 2 3) ) (S  (S (S 4 15) (S 0 15) ) (S 0 15) ) )"+
        "(S (S (S (S 5 15) (S 3 4) ) (S 5 6) ) (S  (S (S 7 15) (S 0 15) ) (S 0 15) ) ) ) (S (S (S  (S (S 3 15) (S 4 3) ) (S 2 1) ) (S  (S (S 2 15) (S 3 2) ) (S 1 0) ) )" +
        "(S (S (S (S 14 15) (S 0 1) ) (S 2 0) ) (S  (S (S 1 15) (S 15 15) ) (S 15 1) ) ) ) )"
        , notes, oprList, confList);
        */
    //saveMIDIFile("/home/tanji/Desktop/afo.mid", eventList);
  }
  
  public static CommonEventList convertStringToMidi(String str, ArrayList<Notes> notes, ArrayList<String> oprList, ArrayList<String> confList)
  {
    int mode = 0;
    Motif_simpleTree_Individual individual = new Motif_simpleTree_Individual(0, mode, notes, oprList, confList);
    individual.generateFromString(str);
    CommonEventList eventList = individual.getEventList();
    try {
		eventList.playAsMIDISequence(120);
	} catch (MidiUnavailableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    return eventList;
  }
  
  public static void saveMIDIFile(String filePath, CommonEventList eventList)
  {
    MIDISequence seq = new MIDISequence();
    //seq.setTempo(0, 120);
    
    for(int i=0; i<eventList.numberOfNote(); i++){
      OneNote tmpNote = (OneNote)eventList.get(i);
      seq.setNoteToTrack(0, 1, CommonEventList.convertChromaticToDiatonicInC(tmpNote.noteNumber()), tmpNote.noteVelocity(), tmpNote.positionInMotif(), tmpNote.noteLength());
    }
    
    try{
      seq.saveMIDISequenceToFile(filePath, 0);
    }catch (Exception e){
      System.err.println(e);
      System.exit(1);
    }
    try {
		seq.playMIDISequenceWithSequencer();
	} catch (MidiUnavailableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
