package CACIE.midi;

/** Fixed 4/4 eight-beat pattern, repeated and cut without time scaling. */
public final class EightBeatDrumPattern implements DrumPattern {
  public static final int DRUM_CHANNEL=9;
  private static final long BAR_TICKS=64;
  public void addTo(MIDISequence sequence,long startTick,long durationTicks){
    if(durationTicks<=0)return;
    for(long bar=0;bar<durationTicks;bar+=BAR_TICKS){
      for(int tick=0;tick<64;tick+=8)add(sequence,42,tick%32==0?88:72,startTick+bar+tick,2,startTick+durationTicks);
      add(sequence,36,105,startTick+bar,3,startTick+durationTicks);add(sequence,36,100,startTick+bar+32,3,startTick+durationTicks);
      add(sequence,38,108,startTick+bar+16,3,startTick+durationTicks);add(sequence,38,108,startTick+bar+48,3,startTick+durationTicks);
    }
  }
  private static void add(MIDISequence sequence,int note,int velocity,long tick,long length,long limit){if(tick>=limit)return;long clipped=Math.min(length,limit-tick);if(clipped>0)sequence.setNoteToTrack(0,DRUM_CHANNEL,note,velocity,tick,clipped);}
}
