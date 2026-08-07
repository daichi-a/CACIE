package CACIE.eventlist;

import CACIE.genome.Notes;
import CACIE.genome.OneNote;

/** Converts the scale-degree pitches used by GP into MIDI note numbers. */
public final class ScaleFilter {
    private ScaleFilter() {}

    public static Notes apply(Notes source, ScaleType scale, int tonicPitchClass) {
        Notes result = new Notes();
        for (int i = 0; i < source.getNumOfNotes(); i++) {
            OneNote note = source.getNote(i);
            int pitch = convertScaleDegreeToMidi(note.getNoteNumber(), scale, tonicPitchClass);
            result.addNote(new OneNote(pitch, note.getVelocity(), note.getPosition(), note.getDuration()));
        }
        result.fitParameters();
        return result;
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
        return convertScaleDegreeToMidi(inputNoteNumber, ScaleType.DIATONIC, tonicFromName(key));
    }

    public static int convertToDiatonicScale(int inputNoteNumber, String key) {
        return convertScaleDegreeToMidi(inputNoteNumber, ScaleType.DIATONIC, tonicFromName(key));
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
