package CACIE.midi;

/** Adds a percussion pattern to an existing MIDI sequence. */
public interface DrumPattern {
  void addTo(MIDISequence sequence,long startTick,long durationTicks);
  default void addTo(MIDISequence sequence,long durationTicks){addTo(sequence,0,durationTicks);}
}
