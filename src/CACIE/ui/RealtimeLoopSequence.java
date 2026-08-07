package CACIE.ui;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.*;
import CACIE.midi.MIDISequence;

/** A single, continuously growing MIDI Sequence whose future loop blocks may be replaced. */
public final class RealtimeLoopSequence {
  public static final int LOOP_MARKER_CONTROLLER=118;
  public static final class Block {
    public final long loopNumber,startTick,endTick;
    public final PlaybackSequencePlan plan;
    private final List<MidiEvent> events;
    private Block(long number,long start,long end,PlaybackSequencePlan plan,List<MidiEvent> events){loopNumber=number;startTick=start;endTick=end;this.plan=plan;this.events=events;}
  }
  private final MIDISequence midi=new MIDISequence();
  private final Track track=midi.getSequence().getTracks()[0];
  private final ArrayList<Block> blocks=new ArrayList<Block>();

  public RealtimeLoopSequence(int tempo)throws Exception{midi.setTempo(0,tempo);midi.setInstrument(0,0,0,0);}
  public MIDISequence getMIDISequence(){return midi;}
  public List<Block> getBlocks(){return java.util.Collections.unmodifiableList(blocks);}
  public synchronized Block append(long loopNumber,PlaybackSequencePlan source)throws Exception{
    long start=blocks.isEmpty()?0:blocks.get(blocks.size()-1).endTick;
    Block block=copyBlock(loopNumber,start,source);blocks.add(block);return block;
  }
  /** Replaces only a not-yet-started block, retaining its absolute start tick. */
  public synchronized Block replaceFuture(long loopNumber,PlaybackSequencePlan source,long currentTick)throws Exception{
    for(int i=0;i<blocks.size();i++)if(blocks.get(i).loopNumber==loopNumber){Block old=blocks.get(i);if(currentTick>=old.startTick)throw new IllegalStateException("Cannot replace a started loop block");for(MidiEvent event:old.events)track.remove(event);Block replacement=copyBlock(loopNumber,old.startTick,source);blocks.set(i,replacement);shiftFollowingBlocks(i+1,replacement.endTick);return replacement;}throw new IllegalArgumentException("Unknown loop "+loopNumber);
  }
  private void shiftFollowingBlocks(int index,long cursor)throws Exception{
    for(int i=index;i<blocks.size();i++){Block old=blocks.get(i);for(MidiEvent event:old.events)track.remove(event);Block moved=copyBlock(old.loopNumber,cursor,old.plan);blocks.set(i,moved);cursor=moved.endTick;}
  }
  private Block copyBlock(long number,long start,PlaybackSequencePlan source)throws Exception{
    ArrayList<MidiEvent> added=new ArrayList<MidiEvent>();
    for(Track sourceTrack:source.getMIDISequence().getSequence().getTracks())for(int i=0;i<sourceTrack.size();i++){
      MidiEvent event=sourceTrack.get(i);if(event.getMessage() instanceof MetaMessage&&((MetaMessage)event.getMessage()).getType()==0x2f)continue;
      // Tempo and program setup belong to the shared sequence, not every loop block.
      if(event.getTick()==0&&event.getMessage() instanceof MetaMessage&&((MetaMessage)event.getMessage()).getType()==0x51)continue;
      MidiEvent copy=new MidiEvent((MidiMessage)event.getMessage().clone(),start+event.getTick());track.add(copy);added.add(copy);
    }
    addControl(added,start,LOOP_MARKER_CONTROLLER,(int)(number%128));
    long end=start+Math.max(1,source.getTotalTicks());
    return new Block(number,start,end,source,added);
  }
  private void addControl(List<MidiEvent> added,long tick,int controller,int value)throws Exception{ShortMessage m=new ShortMessage();m.setMessage(ShortMessage.CONTROL_CHANGE,PlaybackSequenceBuilder.MARKER_CHANNEL,controller,value);MidiEvent e=new MidiEvent(m,tick);track.add(e);added.add(e);}
}
