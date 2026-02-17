//?��ȒP?��Ƀv?��?��?��C?��\?��ȃV?��[?��N?��G?��?��?��X?��?��?��邽?��߂�??��?��C?��u?��?��?��?��
//position?��?��length?��͂�?��ׂ�Tick?��P?��?��

package CACIE.midi;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.*;

import CACIE.*;

public class MIDISequence{
    
    private Sequence sequence;
    
    private int ppq = -1;//example quarternote = 120
    
    //?��l?��齂�?��?��Ȃ�?���??��CPPQ=16?��ŃV?��[?��P?��?��?��X?��?��?��?��?��?��?��?��?��D?��?��?��?��\?��?��64?��?��?��?��?��?��?��܂ŁD
    public MIDISequence(){
	ppq = 16;
	try{
	    sequence = new Sequence(Sequence.PPQ, ppq);
	}
    catch(Exception ex){
	ex.printStackTrace();
	System.exit(1);
    }
	
	try{
	    sequence.createTrack();
	    //
	    Track track =  sequence.getTracks()[0];
		ShortMessage message = new ShortMessage();
		message.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 6, 0);
		track.add(new MidiEvent(message, 0));
		
	    
	}
	catch(Exception ex){
	    ex.printStackTrace();
	    System.exit(1);
	}
	
    }
    
    public MIDISequence(int ppq){
	ppq = this.ppq;
	try{
	    sequence = new Sequence(Sequence.PPQ, ppq);
	}
	catch(Exception ex){
	    ex.printStackTrace();
	    System.exit(1);
	}
	
	try{
	    sequence.createTrack();
	}
	catch(Exception ex){
	    ex.printStackTrace();
	    System.exit(1);
	}
	
    }
    
    
    
    public Sequence getSequence(){
	return sequence;
    }
    
    public void setSequence(Sequence sequence){
	this.sequence = sequence;
    }
    
    
    public void setTempo(int trackIndex, int tempo){
	
	Track track[] = sequence.getTracks();
	
	if(trackIndex > track.length){
	    System.out.println(track.length + " tracks are existing");
	    return;
	}
	
	MIDITrack.addMetaEventTo(track[trackIndex], 0x51, MIDITrack.createTempoMetaData(tempo), 0L);
    
    }
    
    public void setInstrument(int trackIndex, int channel, int instrument, long currentTick) throws MidiUnavailableException{
    	Track track[] = sequence.getTracks();
    	if(trackIndex >= track.length){
    		System.err.println(track.length + " tracks are existing");
    		return;
    	}
    	MIDITrack.addProgramChangeEventTo(track[trackIndex], trackIndex, channel, instrument, currentTick);
    }
    
	
	
    
    public void setNoteToTrack(int trackIndex, int channel, int noteNumber, int noteVelocity, long position, long length){
	
	Track track[] = sequence.getTracks();
	
	if(trackIndex > track.length){
	    System.out.println(track.length + "tracks are exitsting");
	    return;
	}
	
	
	//long tickPosition = Math.round(position * (double)ppq);
	//long tickLength = ppq * 4 / length;
	long tickPosition = position;
	long tickLength = length;
	MIDITrack.addNoteToTrack(track[trackIndex], channel, noteNumber, noteVelocity, tickPosition, tickLength);
    }
    
    
    public Sequencer playMIDISequenceWithSequencer() throws MidiUnavailableException, InvalidMidiDataException{
    	Sequencer sequencer;
    	Synthesizer synthesizer = null;
	
    	sequencer = MIDIPlay.initSequencer();
    	try{
    		
    		sequencer.setSequence(sequence);
    	}
    	catch(InvalidMidiDataException ex){
    		ex.printStackTrace();
    		System.exit(1);
    	}
    	if(CACIE_O.indexOfDevice == -1){
    		//�Ѡ�V���Z�T�C�U��g�����̏���
    		if (! (sequencer instanceof Synthesizer)){
    			/*
    			 *	We try to get the default synthesizer, open()
    			 *	it and chain it to the sequencer with a
    			 *	Transmitter-Receiver pair.
    			 */
    			try{
    				synthesizer = MidiSystem.getSynthesizer();
    				synthesizer.open();
    				Receiver synthReceiver = synthesizer.getReceiver();
    				Transmitter seqTransmitter = sequencer.getTransmitter();
    				seqTransmitter.setReceiver(synthReceiver);
    			}
    			catch (MidiUnavailableException e){
    				e.printStackTrace();
    			}
    		}
    	}
	
    	sequencer.start();
    	//MIDIPlay.startMIDISequencer(sequencer);
    	return sequencer;
    }
  
    
    //Sequence ?��?��MidiSystem?��̃t?��@?��?��?��N?��V?��?��?��?��?��?��g?��Băt?��@?��C?��?��?��ɏ�?��?��?��?��?��?��
    public void saveMIDISequenceToFile(String midiFileName, int fileType) throws Exception{
	
	int[] fileTypes = MidiSystem.getMidiFileTypes();
	
	if(fileTypes.length == 0){
	    //throw new Exception(jp.Resource.get("cannotGetMidiFileType"));
	}
	
	File file = new File(midiFileName);
	long lastModified;
	
	
	//?��?��?��?���̕�?��?�� ?��㏑�?��?��m?��F?��D
	//recommendation of overwrite
	
	if(file.exists()){
	    lastModified = file.lastModified();
	}
	else{
	    lastModified = -1;
	}
	{
	    int res = MidiSystem.write(this.sequence, fileType, file);
	    if(res == -1){
		throw new IOException("Problems writing to file");
	    }
	} 
	
	if(file.exists() && lastModified != file.lastModified()){
	    ;//?��?��?��?��
	}
	else{
	    //?��w?��?��̃t?��H?��[?��}?��b?��g?��Ńt?��@?��C?��?��?��?��ۑ�?��ł�?��Ȃ�?��B?��
	    int type = -1;
	    //?��?��?��?��?��?���?��?��?��?��?��?��ނ킯?��?��?��
	    for(int i = 0; i<fileTypes.length; i++){
		type = fileTypes[i];
		int res = MidiSystem.write(sequence, type, file);
		System.out.println("MidiSystem.write 90 res=" + res);
		
		if(file.exists() && lastModified != file.lastModified()){
		    System.out.println(midiFileName + "saved. filetype=" + type);
		    break;
		}
		
	    }
	    
	    //String errorMessage = "MidiFormat" + fileType + jp.Resource.get("cannotSaveMidiFileInThisType");
	    if(type >= 0){
		//erroMessage += "MidiFormat" + type + jp.Resource.get("cannotSaveMidiFileInThisType2")
		;
	    }
      //throw new Exception(errorMessage);
	}
	
    }
}
