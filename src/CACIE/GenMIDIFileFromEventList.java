//Test Program for eventlist

package CACIE;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.OneNote;
import CACIE.midi.MIDISequence;

class GenMIDIFileFromEventList{

  public static void main(String args[]){

    CommonEventList eventList = new CommonEventList(7);
    eventList.readFromFile(args[0]);
    //eventList.writeToFile(args[0] + "d");

    MIDISequence mySeq = new MIDISequence();
    mySeq.setTempo(0, 120);

    for(int i=0; i<eventList.numberOfNote(); i++){
      OneNote tmpNote = (OneNote)eventList.get(i);
      mySeq.setNoteToTrack(0, 1, tmpNote.noteNumber(), tmpNote.noteVelocity(), tmpNote.positionInMotif(), tmpNote.noteLength());
      
    }
    try{
      mySeq.saveMIDISequenceToFile(args[1], 0);
    }catch (Exception e){
      System.err.println(e);
      System.exit(1);
    }
    try {
		mySeq.playMIDISequenceWithSequencer();
	} catch (MidiUnavailableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
  }


}