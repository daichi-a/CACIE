package CACIE.eventlist;

/** Output-only settings; never part of evolution or the stored source genome. */
public final class PlaybackSettings {
  public static final PlaybackSettings DEFAULT = new PlaybackSettings(4.0, 0, ScaleType.DIATONIC);
  private final double beats; private final int tonic; private final ScaleType scale;
  public PlaybackSettings(double beats,int tonic,ScaleType scale){if(beats<=0&&!Double.isInfinite(beats))throw new IllegalArgumentException("beats must be positive or infinity");this.beats=beats;this.tonic=Math.floorMod(tonic,12);this.scale=scale;}
  public double getBeats(){return beats;} public int getTonic(){return tonic;} public ScaleType getScale(){return scale;}
}
