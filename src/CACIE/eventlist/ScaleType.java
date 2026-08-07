package CACIE.eventlist;

/** Scales available to the Breeding playback decorator. */
public enum ScaleType {
    DIATONIC("Scale - Diatonic", false, new int[] { 0, 2, 4, 5, 7, 9, 11 }),
    MAJOR("Scale - Major", false, new int[] { 0, 2, 4, 5, 7, 9, 11 }),
    NATURAL_MINOR("Scale - Natural minor", false, new int[] { 0, 2, 3, 5, 7, 8, 10 }),
    HARMONIC_MINOR("Scale - Harmonic minor", false, new int[] { 0, 2, 3, 5, 7, 8, 11 }),
    MELODIC_MINOR("Scale - Melodic minor", false, new int[] { 0, 2, 3, 5, 7, 9, 11 }),
    PENTATONIC("Scale - Pentatonic", false, new int[] { 0, 2, 4, 7, 9 }),
    CHROMATIC("Scale - Chromatic", false, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 }),
    ALTERED("Scale - Altered", false, new int[] { 0, 1, 3, 4, 6, 8, 10 }),
    COMBINATION_DIMINISHED("Scale - Combination of diminished", false, new int[] { 0, 1, 3, 4, 6, 7, 9, 10 }),
    WHOLE_TONE("Scale - Whole tone", false, new int[] { 0, 2, 4, 6, 8, 10 }),
    MAJOR_TRIAD("Chord - Major triad", true, new int[] { 0, 4, 7 }),
    MINOR_TRIAD("Chord - Minor triad", true, new int[] { 0, 3, 7 }),
    DIMINISHED_TRIAD("Chord - Diminished triad", true, new int[] { 0, 3, 6 }),
    MAJOR_SEVENTH("Chord - Major 7th", true, new int[] { 0, 4, 7, 11 }),
    DOMINANT_SEVENTH("Chord - 7th", true, new int[] { 0, 4, 7, 10 }),
    MINOR_SEVENTH("Chord - Minor 7th", true, new int[] { 0, 3, 7, 10 }),
    DIMINISHED_SEVENTH("Chord - Diminished 7th", true, new int[] { 0, 3, 6, 9 });

    private final String label;
    private final int[] intervals;
    private final boolean chord;

    ScaleType(String label, boolean chord, int[] intervals) {
        this.label = label;
        this.chord = chord;
        this.intervals = intervals;
    }

    public boolean isChord() { return chord; }

    public int[] getIntervals() {
        return intervals.clone();
    }

    @Override
    public String toString() {
        return label;
    }
}
