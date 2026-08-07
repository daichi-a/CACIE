package CACIE.eventlist;

import java.util.ArrayList;
import java.util.Collections;
import CACIE.genome.OneNote;

/** Applies output-only octave shifts so adjacent playback slots connect naturally. */
public final class PlaybackSequenceContinuity {
  private Integer previousLastPitch;
  public void reset(){previousLastPitch=null;}

  public CommonEventList connect(CommonEventList source){
    if(source==null)return null;
    int first=anchor(source,true), shift=0;
    if(first>=0&&previousLastPitch!=null)shift=bestShift(source,first,previousLastPitch.intValue());
    CommonEventList result=transposeCopy(source,shift);
    int last=anchor(result,false);if(last>=0)previousLastPitch=last;
    return result;
  }

  private static int bestShift(CommonEventList list,int first,int previous){
    int min=127,max=0;boolean found=false;for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);if(n.getVelocity()<=0)continue;min=Math.min(min,n.getNoteNumber());max=Math.max(max,n.getNoteNumber());found=true;}if(!found)return 0;
    int best=0,bestDistance=Integer.MAX_VALUE,bestOctaves=Integer.MAX_VALUE;
    for(int oct=-10;oct<=10;oct++){int shift=oct*12;if(min+shift<0||max+shift>127)continue;int distance=Math.abs(first+shift-previous),octaves=Math.abs(oct);if(distance<bestDistance||(distance==bestDistance&&octaves<bestOctaves)||(distance==bestDistance&&octaves==bestOctaves&&shift<best)){best=shift;bestDistance=distance;bestOctaves=octaves;}}
    return best;
  }

  private static int anchor(CommonEventList list,boolean first){
    long selected=first?Long.MAX_VALUE:Long.MIN_VALUE;for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);if(n.getVelocity()<=0)continue;selected=first?Math.min(selected,n.getPosition()):Math.max(selected,n.getPosition());}
    if(selected==Long.MAX_VALUE||selected==Long.MIN_VALUE)return -1;ArrayList<Integer> pitches=new ArrayList<Integer>();for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);if(n.getVelocity()>0&&n.getPosition()==selected)pitches.add(n.getNoteNumber());}Collections.sort(pitches);int count=pitches.size();return count%2==1?pitches.get(count/2):(pitches.get(count/2-1)+pitches.get(count/2))/2;
  }

  private static CommonEventList transposeCopy(CommonEventList source,int shift){
    ArrayList<OneNote> notes=new ArrayList<OneNote>();for(int i=0;i<source.getNumOfNotes();i++){OneNote n=(OneNote)source.get(i);notes.add(new OneNote(Math.max(0,Math.min(127,n.getNoteNumber()+shift)),n.getVelocity(),n.getPosition(),n.getDuration()));}
    CommonEventList result=new CommonEventList(notes);result.setPitchEncoding(source.getPitchEncoding());result.setInstrumentNumber(source.getInstrumentNumber());result.setTempo(source.getTempo());result.setGenomeString(source.getGenomeString());return result;
  }
}
