package CACIE.midi;

import javax.sound.midi.*;
import javax.sound.midi.MetaMessage;

public class MIDITrack{
    
    // ?��P?��?��?��?��Ԃ�?��?��?��ނ�?��߂�??��?��\?��b?��h
    // nextTick?���??��?��I?��ɂ͉�?��̒�?��?��?��?��Ӗ�?��?��?��?��D?��D?��D?��P?��?��
    // trackIndex?��̓`?��?��?��?��?��l?��?��
    // ?��C?��?��?��^?��t?��F?��C?��X?��?��
    // addNoteToTrack(Track, int trackIndex, noteNumber, int velocity, long currentTick, long tickLength);
    // Length?��͑S?��?��Tick?��P?��?��
    
    public static void addNoteToTrack(Track track, int channel, int noteNumber, int noteVelocity, long position, long length){
        try {
            // Create note on event
            MidiEvent noteOnEvent = new MidiEvent(
                new ShortMessage(ShortMessage.NOTE_ON, channel, noteNumber, noteVelocity),
                position
            );
            track.add(noteOnEvent);

            // Create note off event
            MidiEvent noteOffEvent = new MidiEvent(
                new ShortMessage(ShortMessage.NOTE_OFF, channel, noteNumber, noteVelocity),
                position + length
            );
            track.add(noteOffEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void addProgramChangeEventTo(Track track, int trackIndex, int channel, int instrument, long position){
        try {
            MidiEvent programChangeEvent = new MidiEvent(
                new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, instrument, 0),
                position
            );
            track.add(programChangeEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void addMetaEventTo(Track track, int type, byte[] data, long position){
        try {
            MetaMessage metaMessage = new MetaMessage();
            metaMessage.setMessage(type, data, data.length);
            MidiEvent metaEvent = new MidiEvent(metaMessage, position);
            track.add(metaEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] createTempoMetaData(int tempo){
        // Convert tempo (in BPM) to microseconds per quarter note
        int microsecondsPerQuarterNote = (int)(60000000.0 / tempo);
    
        // Create tempo meta message data
        byte[] data = new byte[3];
        data[0] = (byte)((microsecondsPerQuarterNote >> 16) & 0xFF);
        data[1] = (byte)((microsecondsPerQuarterNote >> 8) & 0xFF);
        data[2] = (byte)(microsecondsPerQuarterNote & 0xFF);

        return data;
    }
}
