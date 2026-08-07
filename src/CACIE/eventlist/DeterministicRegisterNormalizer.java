package CACIE.eventlist;

import java.util.ArrayList;
import java.util.Collections;
import CACIE.genome.Notes;
import CACIE.genome.OneNote;

/** Selects one reproducible octave register without consulting playback history. */
public final class DeterministicRegisterNormalizer {
    public static final int COMFORT_LOW=48,COMFORT_HIGH=72,TARGET_CENTER=60;
    private DeterministicRegisterNormalizer() {}

    public static Notes normalize(Notes source) {
        int shift=calculateOctaveShift(source);Notes result=new Notes();
        for(int i=0;i<source.getNumOfNotes();i++){OneNote n=source.getNote(i);result.addNote(new OneNote(n.getNoteNumber()+shift,n.getVelocity(),n.getPosition(),n.getDuration()));}
        result.fitParameters();return result;
    }

    public static int calculateOctaveShift(Notes source) {
        ArrayList<Integer> pitches=new ArrayList<Integer>();int min=127,max=0;
        for(int i=0;i<source.getNumOfNotes();i++){OneNote n=source.getNote(i);if(n.getVelocity()<=0)continue;int pitch=n.getNoteNumber();pitches.add(pitch);min=Math.min(min,pitch);max=Math.max(max,pitch);}
        if(pitches.isEmpty())return 0;Collections.sort(pitches);double median=pitches.size()%2==1?pitches.get(pitches.size()/2):(pitches.get(pitches.size()/2-1)+pitches.get(pitches.size()/2))/2.0;
        int bestShift=0;long bestOverflow=Long.MAX_VALUE;double bestCenter=Double.MAX_VALUE;int bestMovement=Integer.MAX_VALUE;
        for(int oct=-10;oct<=10;oct++){int shift=oct*12;if(min+shift<0||max+shift>127)continue;long overflow=0;for(int pitch:pitches){int moved=pitch+shift;if(moved<COMFORT_LOW)overflow+=COMFORT_LOW-moved;else if(moved>COMFORT_HIGH)overflow+=moved-COMFORT_HIGH;}double center=Math.abs(median+shift-TARGET_CENTER);int movement=Math.abs(shift);
            if(overflow<bestOverflow||(overflow==bestOverflow&&center<bestCenter)||(overflow==bestOverflow&&center==bestCenter&&movement<bestMovement)||(overflow==bestOverflow&&center==bestCenter&&movement==bestMovement&&shift<bestShift)){bestShift=shift;bestOverflow=overflow;bestCenter=center;bestMovement=movement;}
        }
        return bestShift;
    }
}
