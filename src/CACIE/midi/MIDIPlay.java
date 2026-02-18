package CACIE.midi;

import java.io.File;
import javax.sound.midi.*;
import javax.sound.midi.MetaEventListener;

import CACIE.*;

public class MIDIPlay{
    
    static Sequencer sequencer = null;
    static Synthesizer synthesizer = null;
    static Sequence mySeq = null;
    static File myFile;
    
    
    //?��Ȃ�[?��?��?��Ȃ�?��D?��D?��D?��?��?��?��?��?��?��?��?��ꂾ?��?��?��Ȃ̂ɁC?��?��?��炢?��ʓ|?��?��?��?��?��?��  
    //?��Đ�?��̎�?��ɌĂяo?��?��?��?��?��\?��b?��h
    //?��?��?��?��±?��?��?��ł�BĂ�?��?��̂ŁC?��?��?��?��?��?��?��ɂƂ낢?��D
    //?��}?��?��?��`?��ł�?��Ƃ�?��ɂ͂¤?��?��?��?��Bƍl?��?��?��?��ƁD
    
    public static Sequencer initSequencer(){
	
    	try{
    		if(CACIE_O.indexOfDevice == -1)
    			sequencer = MidiSystem.getSequencer();
    		else{
    			CACIE_O.externalDevice.open();
    			sequencer = MidiSystem.getSequencer(false); //falseがダメになってる？
    			//sequencer = MidiSystem.getSequencer(); //これだといく2重再生の可能性あり
    			sequencer.getTransmitter().setReceiver(CACIE_O.externalDevice.getReceiver());
    			sequencer.open();
    		}
    	}catch(MidiUnavailableException e){
    		e.printStackTrace();
    		System.exit(1);
    	}
	
    	if(sequencer == null){
    		//Error sequencer device is note supported.
    		System.err.println("Error : cannot get sequencer");
    		System.exit(1);
    	}
	
    	sequencer.addMetaEventListener(new MetaEventListener(){
    		public void meta(MetaMessage event){
    			if(event.getType() == 47){
    				sequencer.close();
    				if(synthesizer != null){
    					synthesizer.close();
    				}
    			}
    		}
	    });
    	//?��?��?��?��?��܂�
	
	//	Acquire resources and make operational
    	try{        
    		sequencer.open();
    	}catch(MidiUnavailableException e){
    		e.printStackTrace();
    		System.exit(1);
    	}
    	//if(GUITest.indexOfDevice != -1)GUITest.externalDevice.close();
    	return sequencer;
    }
    
    public static void setSequence(Sequencer Sequencer, Sequence mySeqt) throws InvalidMidiDataException{
    	// Init the sequencer
    	Sequence mySeq = mySeqt;	
    	//Set the Sequence to default Sequencer
    	try{
    		sequencer.setSequence(mySeq);
    	}catch(InvalidMidiDataException e){
    		e.printStackTrace();
    		System.exit(1);
    	}
	
    	if(CACIE_O.indexOfDevice == -1){
    		//�Ѡ�V���Z�T�C�U��g���Ƃ�m    		
    		if (! (sequencer instanceof Synthesizer)){
    			try{
    				synthesizer = MidiSystem.getSynthesizer();
    				synthesizer.open();
    				Receiver	synthReceiver = synthesizer.getReceiver();
    				Transmitter	seqTransmitter = sequencer.getTransmitter();
    				seqTransmitter.setReceiver(synthReceiver);
    			}
    			catch (MidiUnavailableException e){
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    
    public static void openMIDIFile(String filename, Sequencer sequencer){
	
	// Reading the Midi file to a Sequence object
	
        
	File myFile = new File(filename);
	// Init the sequencer
	Sequence mySeq = null;
	try{
	    // Construct a Sequence Object and load it into my sequencer
	    mySeq = MidiSystem.getSequence(myFile);
	} catch (Exception e){
	    // Handle err and/or return
	    e.printStackTrace();
	    System.exit(1);
	}
	
	
	//Set the Sequence to default Sequencer
	try{
	    sequencer.setSequence(mySeq);
	}catch(InvalidMidiDataException e){
	    e.printStackTrace();
	    System.exit(1);
	}
	
	//?��ȉ�?��̂悤?��ȗ�?��R?��ɂ�?��C?��?��?��̕�?��?��?��̃R?��[?��h?��?��?��?��Ȃ�?��?��΂Ȃ�Ȃ�?��D
	/*	Now, we set up the destinations the Sequence should be
	 *	played on. Here, we try to use the default
	 *	synthesizer. With some Java Sound implementations
	 *	(Sun jdk1.3/1.4 and others derived from this codebase),
	 *	the default sequencer and the default synthesizer
	 *	are combined in one object. We test for this
	 *	condition, and if it's true, nothing more has to
	 *	be done. With other implementations (namely Tritonus),
	 *	sequencers and synthesizers are always seperate
	 *	objects. In this case, we have to set up a link
	 *	between the two objects manually.
	 *
	 *	By the way, you should never rely on sequencers
	 *	being synthesizers, too; this is a highly non-
	 *	portable programming style. You should be able to
	 *	rely on the other case working. Alas, it is only
	 *	partly true for the Sun jdk1.3/1.4.
	 */
	if (! (sequencer instanceof Synthesizer))
	    {
		/*
		 *	We try to get the default synthesizer, open()
		 *	it and chain it to the sequencer with a
		 *	Transmitter-Receiver pair.
		 */
		try
		    {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			Receiver	synthReceiver = synthesizer.getReceiver();
			Transmitter	seqTransmitter = sequencer.getTransmitter();
			seqTransmitter.setReceiver(synthReceiver);
		    }
		catch (MidiUnavailableException e)
		    {
			e.printStackTrace();
		    }
	    }
	//?��?��?��?��?��܂ŁD
	
    }
    
    /*
    public static void startMIDISequencer(Sequencer sequencer) throws MidiUnavailableException, InvalidMidiDataException{
	
	//?��ŁC?��?��BƃX?��^?��[?��g?��ł�?��?��D
    	if(CACIE_O.indexOfDevice != -1){
    		try{CACIE_O.externalDevice.open();}
    		catch(MidiUnavailableException e){
    			e.printStackTrace();
    		}
    	}
    	sequencer.start();    
    }
    */
    
    public static void pauseMIDISequencer(Sequencer sequencer){
		sequencer.stop();
    }
    
    
    public static void stopMIDISequencer(Sequencer sequencer){	
    	sequencer.stop();
    	//Tick?��?��ŏ�?��ɖ߂�
    	sequencer.setTickPosition(0L);
    	if(CACIE_O.indexOfDevice != -1){
    		CACIE_O.externalDevice.close();
    	}    	
    }
    
}

