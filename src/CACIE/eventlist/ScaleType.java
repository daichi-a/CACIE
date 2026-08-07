package CACIE.eventlist;

/** Scales available to the Breeding playback decorator. */
public enum ScaleType {
    DIATONIC("Diatonic", new int[] { 0, 2, 4, 5, 7, 9, 11 }),
    MAJOR("Major", new int[] { 0, 2, 4, 5, 7, 9, 11 }),
    NATURAL_MINOR("Natural minor", new int[] { 0, 2, 3, 5, 7, 8, 10 }),
    HARMONIC_MINOR("Harmonic minor", new int[] { 0, 2, 3, 5, 7, 8, 11 }),
    MELODIC_MINOR("Melodic minor", new int[] { 0, 2, 3, 5, 7, 9, 11 }),
    PENTATONIC("Pentatonic", new int[] { 0, 2, 4, 7, 9 }),
    CHROMATIC("Chromatic", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });

    private final String label;
    private final int[] intervals;

    ScaleType(String label, int[] intervals) {
        this.label = label;
        this.intervals = intervals;
    }

    public int[] getIntervals() {
        return intervals.clone();
    }

    @Override
    public String toString() {
        return label;
    }
}
