package CACIE.ui;

import java.util.Collections;
import java.util.List;
import CACIE.midi.MIDISequence;

/** Immutable absolute-tick plan used by both playback and export. */
public final class PlaybackSequencePlan {
  public static final class Segment { public final int slot;public final long startTick,endTick;Segment(int slot,long start,long end){this.slot=slot;startTick=start;endTick=end;}public double progress(long tick){return Math.max(0,Math.min(1,(tick-startTick)/(double)Math.max(1,endTick-startTick)));} }
  private final MIDISequence sequence;private final List<Segment> segments;private final long totalTicks;
  PlaybackSequencePlan(MIDISequence sequence,List<Segment> segments,long totalTicks){this.sequence=sequence;this.segments=Collections.unmodifiableList(segments);this.totalTicks=totalTicks;}
  public MIDISequence getMIDISequence(){return sequence;}public List<Segment> getSegments(){return segments;}public long getTotalTicks(){return totalTicks;}
  public Segment segmentAt(long tick){for(Segment s:segments)if(tick>=s.startTick&&tick<s.endTick)return s;return null;}
}
