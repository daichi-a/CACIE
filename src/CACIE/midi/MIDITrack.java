//Making midi Track
//?��C?��x?��?��?��g?��?��?��X?��g?��?��?��?��?��?��o?��?��?��?��?��Ƃ�l?��?��?��Ă�?��?��̂ŁCTick?��?��increase?��Ȃǂ͍s?��?��Ȃ�

package CACIE.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MIDITrack{
    
    //?��P?��?��?��?��Ԃ�?��?��?��ނ�?��߂�??��?��\?��b?��h
    //nextTick?���??��?��I?��ɂ͉�?��̒�?��?��?��?��Ӗ�?��?��?��?��D?��D?��D?��P?��?��?��?��?��?��?��̏ꍇ?��͂ˁD
    //trackIndex?��̓`?��?��?��?��?��l?��?��
    //?��C?��?��?��^?��t?��F?��C?��X?��?��
    //addNoteToTrack(Track, int trackIndex, noteNumber, int velocity, long currentTick, long tickLength);
    //Length?��͑S?��?��Tick?��P?��?��
    
    public static void addNoteToTrack(Track track, int channel, int noteNumber, int velocity, long currentTick, long tickLength){
	long nextTick = currentTick + tickLength;
	try{
	    {
	    	if(noteNumber > 127 || noteNumber < 0){
	    		System.out.println("noteNumber is " + noteNumber);
	    	}
	    ShortMessage message = new ShortMessage();
		message.setMessage(
				   ShortMessage.NOTE_ON,
				   channel,
				   noteNumber, //0x60, // middle c
				   velocity    //0x64
				   );
		//?��?��?��?��D?��D?��D?��A?��[?��M?��?��?��?��?��?��?��g?��?��?��l?���?BĂ�?��Ƃ́C2?��Ԗڂ̓`?��?��?��?��?��l?��?��?��C?��Ƃ�?��?��?��?��?��ƂɂȂ�D
		MidiEvent event = new MidiEvent(message, currentTick);
		track.add(event);
		// ? beginMessage.setMessage(NOTEON + channel, note, FULL_VOLUME );
	    }
	    {
		ShortMessage message = new ShortMessage();
		message.setMessage(
				   ShortMessage.NOTE_OFF,
				   channel,
				   noteNumber,
				   0x00
				   );
		//message.setMessage(ShortMessage.NOTE_ON, noteNumber, 0x00);
		MidiEvent event = new MidiEvent(message, nextTick);
		track.add(event);
	    }
    }
	catch (Exception e){
	    e.printStackTrace();
	}
    }
    
    public static void addChangeEventTo(Track track, int changeCommand, int trackIndex, int data1, int data2, long currentTick){
	try{
	    ShortMessage message = new ShortMessage();
	    message.setMessage(changeCommand, trackIndex, data1, data2);
	    MidiEvent event = new MidiEvent(message, currentTick);
	    track.add(event);
	}
	catch (Exception e){
	    e.printStackTrace();
	}
    }
    
    public static void addProgramChangeEventTo(Track track, int trackIndex, int channelNumber, int programNumber, long currentTick){
	try{
	    ShortMessage message = new ShortMessage();
	    message.setMessage(
			       ShortMessage.PROGRAM_CHANGE,
			       channelNumber, //0-F ...byte ?
			       programNumber, //00-7F
			       (int)currentTick);
	    MidiEvent event = new MidiEvent(message, currentTick);
	    track.add(event);
	}
	catch (Exception e){
	    e.printStackTrace();
	}
    }
    
    
    public static void addMetaEventTo(Track track, int type, byte[] data, long currentTick){
	try{
	    MetaMessage message = new MetaMessage();
	    message.setMessage(type, data, data.length);
	    //createTempoMetaData?��ō�B?��?��̂�Ԃ�?��?��?��ށD
	    //?��e?��?��?��|?��f?��[?��^?��?��type?��?�� 0x51?��炵?��?��?��D?��D?��D?��ǂ�?��?��?��D
	    MidiEvent event = new MidiEvent(message, currentTick);
	    track.add(event);
	}
	catch (Exception e){
	    e.printStackTrace();
	}
    }
    
    public static byte[] createTempoMetaData(double tempo){
  /*
     * Meta Event Set Tempo: FF 51 tttttt , in microseconds per MIDI quarter-note
     * ie: 07a120 (120 BPM). 60?��b / 120?��e?��?��?��| = 0.5  0.5 * 1000 * 1000 = 500,000 microseconds
     */
    int microsecondsPerMIDIquarterNote = (int)((60.0f / tempo) * 1000 * 1000);
    
    List<Byte> list = new ArrayList<Byte>();
    
    while (microsecondsPerMIDIquarterNote > 0){
      byte value = (byte)(microsecondsPerMIDIquarterNote & 0x000000FF);
      list.add(new Byte(value));
      
      microsecondsPerMIDIquarterNote = microsecondsPerMIDIquarterNote >> 8;
    }
    
    byte[] metaData = new byte[list.size()];
    int byteIndex = 0;
    
    for (int i = list.size() - 1; i >= 0; i--, byteIndex++){
      Byte value = (Byte)list.get(i);
      metaData[byteIndex] = value.byteValue();
    }
    
    return metaData;
  }

}
