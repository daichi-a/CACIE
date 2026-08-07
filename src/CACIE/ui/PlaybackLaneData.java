package CACIE.ui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import CACIE.eventlist.CommonEventList;

/** Immutable musical contents of one instrument lane. */
public final class PlaybackLaneData {
  private final List<CommonEventList> slots;private final int channel,program;
  public PlaybackLaneData(List<CommonEventList> slots,int channel,int program){this.slots=Collections.unmodifiableList(new ArrayList<CommonEventList>(slots));this.channel=channel;this.program=program;}
  public List<CommonEventList> getSlots(){return slots;}public int getChannel(){return channel;}public int getProgram(){return program;}
}
