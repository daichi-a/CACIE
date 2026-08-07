package CACIE.eventlist;

import CACIE.genome.Notes;
import CACIE.genome.OneNote;

/** Snaps chromatic MIDI pitches to the nearest note in a scale or chord. */
public final class ScaleFilter {
    private ScaleFilter() {}

    public static Notes apply(Notes source, ScaleType scale, int tonicPitchClass) {
        Notes snapped = new Notes();
        for (int i = 0; i < source.getNumOfNotes(); i++) {
            OneNote note = source.getNote(i);
            int pitch = snapToNearestPitch(note.getNoteNumber(), scale, tonicPitchClass);
            snapped.addNote(new OneNote(pitch, note.getVelocity(), note.getPosition(), note.getDuration()));
        }
        snapped.fitParameters();
        return DeterministicRegisterNormalizer.normalize(snapped);
    }

    public static int snapToNearestChordPitch(int midiPitch, ScaleType chord, int tonicPitchClass) {
        if (!chord.isChord()) throw new IllegalArgumentException("Not a chord: " + chord);
        return snapToNearestPitch(midiPitch, chord, tonicPitchClass);
    }

    public static int snapToNearestPitch(int midiPitch, ScaleType collection, int tonicPitchClass) {
        int best = 0, bestDistance = Integer.MAX_VALUE;
        for (int candidate = 0; candidate <= 127; candidate++) {
            int pc = Math.floorMod(candidate - tonicPitchClass, 12);
            boolean allowed = false;
            for (int interval : collection.getIntervals()) if (pc == interval) { allowed = true; break; }
            if (!allowed) continue;
            int distance = Math.abs(candidate - midiPitch);
            if (distance < bestDistance || (distance == bestDistance && candidate < best)) {
                best = candidate; bestDistance = distance;
            }
        }
        return best;
    }

    public static int convertScaleDegreeToMidi(int degree, ScaleType scale, int tonicPitchClass) {
        int[] intervals = scale.getIntervals();
        int octave = Math.floorDiv(degree, intervals.length);
        int scaleIndex = Math.floorMod(degree, intervals.length);
        int midiNote = octave * 12 + Math.floorMod(tonicPitchClass, 12) + intervals[scaleIndex];
        return Math.max(0, Math.min(127, midiNote));
    }

    /** Compatibility entry point for the old configuration-based caller. */
    public static int doFiltering(int inputNoteNumber, String scaleName, String key) {
        return snapToNearestPitch(inputNoteNumber, ScaleType.DIATONIC, tonicFromName(key));
    }

    public static int convertToDiatonicScale(int inputNoteNumber, String key) {
        return snapToNearestPitch(inputNoteNumber, ScaleType.DIATONIC, tonicFromName(key));
    }

    public static int tonicFromName(String tonic) {
        String normalized = tonic.trim().toUpperCase();
        String[] names = { "C", "C#", "D", "EB", "E", "F", "F#", "G", "AB", "A", "BB", "B" };
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(normalized)) return i;
        }
        if ("DB".equals(normalized)) return 1;
        if ("D#".equals(normalized)) return 3;
        if ("GB".equals(normalized)) return 6;
        if ("G#".equals(normalized)) return 8;
        if ("A#".equals(normalized)) return 10;
        throw new IllegalArgumentException("Unknown tonic: " + tonic);
    }

    public static String tonicName(int tonicPitchClass) {
        String[] names = { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B" };
        return names[Math.floorMod(tonicPitchClass, 12)];
    }
}
