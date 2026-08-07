package CACIE.ui;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.*;
import CACIE.eventlist.CommonEventList;
import CACIE.genome.OneNote;
import CACIE.midi.DrumPattern;
import CACIE.midi.EightBeatDrumPattern;
import CACIE.midi.MIDISequence;

/** Prebuilds every playback slot into one absolute-tick MIDI sequence. */
public final class PlaybackSequenceBuilder {
  public static final int MARKER_CHANNEL=15,MARKER_CONTROLLER=119;
  private PlaybackSequenceBuilder(){}
  public static PlaybackSequencePlan build(List<CommonEventList> lists,List<Long> lengths,int tempo,boolean withDrums)throws Exception{
    return buildLanes(java.util.Arrays.asList(new PlaybackLaneData(lists,0,0)),lengths,tempo,withDrums);
  }
  public static PlaybackSequencePlan buildLanes(List<PlaybackLaneData> lanes,List<Long> lengths,int tempo,boolean withDrums)throws Exception{
    MIDISequence midi=new MIDISequence();midi.setTempo(0,tempo);for(PlaybackLaneData lane:lanes)midi.setInstrument(0,lane.getChannel(),lane.getProgram(),0);DrumPattern drums=new EightBeatDrumPattern();ArrayList<PlaybackSequencePlan.Segment> segments=new ArrayList<PlaybackSequencePlan.Segment>();long cursor=0;int slotCount=lengths.size();
    for(int slot=0;slot<slotCount;slot++){boolean occupied=false;long natural=1;for(PlaybackLaneData lane:lanes)if(slot<lane.getSlots().size()&&lane.getSlots().get(slot)!=null){occupied=true;natural=Math.max(natural,duration(lane.getSlots().get(slot)));}if(!occupied)continue;long selected=lengths.get(slot);long duration=Math.max(1,selected==Long.MAX_VALUE?natural:selected);long end=cursor+duration;segments.add(new PlaybackSequencePlan.Segment(slot,cursor,end));addMarker(midi,cursor,slot+1);
      for(PlaybackLaneData lane:lanes){if(slot>=lane.getSlots().size())continue;CommonEventList events=lane.getSlots().get(slot);if(events==null)continue;for(int i=0;i<events.getNumOfNotes();i++){OneNote n=(OneNote)events.get(i);if(n.getVelocity()<=0||n.getPosition()<0||n.getPosition()>=duration)continue;long length=Math.min(n.getDuration(),duration-n.getPosition());if(length>0)midi.setNoteToTrack(0,lane.getChannel(),n.getNoteNumber(),n.getVelocity(),cursor+n.getPosition(),length);}}
      if(withDrums)drums.addTo(midi,cursor,duration);cursor=end;
    }addMarker(midi,cursor,0);return new PlaybackSequencePlan(midi,segments,cursor);
  }
  private static long duration(CommonEventList list){long end=1;for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);if(n.getVelocity()>0)end=Math.max(end,n.getPosition()+n.getDuration());}return end;}
  private static void addMarker(MIDISequence midi,long tick,int value)throws InvalidMidiDataException{ShortMessage message=new ShortMessage();message.setMessage(ShortMessage.CONTROL_CHANGE,MARKER_CHANNEL,MARKER_CONTROLLER,value);midi.getSequence().getTracks()[0].add(new MidiEvent(message,tick));}
}
