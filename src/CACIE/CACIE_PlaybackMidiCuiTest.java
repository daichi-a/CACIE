package CACIE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.*;
import CACIE.eventlist.*;
import CACIE.genome.*;
import CACIE.midi.MIDISequence;
import CACIE.ui.TreeQuiltIcon;
import CACIE.ui.PlaybackSequenceBuilder;
import CACIE.ui.PlaybackSequencePlan;
import CACIE.ui.RealtimeLoopSequence;
import CACIE.ui.PlaybackLaneData;
import CACIE.midi.EightBeatDrumPattern;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/** Headless test of output-only SCALE/BARFIX decoration and MIDI export. */
public final class CACIE_PlaybackMidiCuiTest {
  private static void check(boolean ok,String message){if(!ok)throw new IllegalStateException(message);}
  private static List<String> notes(Sequence sequence){
    return notes(sequence,-1);
  }
  private static List<String> notes(Sequence sequence,int channel){
    ArrayList<String> out=new ArrayList<String>();
    for(Track track:sequence.getTracks())for(int i=0;i<track.size();i++){
      MidiEvent event=track.get(i); if(event.getMessage() instanceof ShortMessage){ShortMessage m=(ShortMessage)event.getMessage();if(m.getCommand()==ShortMessage.NOTE_ON&&m.getData2()>0&&(channel<0||m.getChannel()==channel))out.add(event.getTick()+":"+m.getData1());}
    } return out;
  }
  public static void main(String[] args)throws Exception{
    System.setProperty("java.awt.headless","true");
    Notes phrase=new Notes();
    phrase.addNote(new OneNote(60,90,0,16)); phrase.addNote(new OneNote(61,90,16,16));
    phrase.addNote(new OneNote(62,90,32,16)); phrase.addNote(new OneNote(63,90,48,16)); phrase.fitParameters();
    ArrayList<Notes> terminals=new ArrayList<Notes>();terminals.add(phrase);
    ArrayList<String> operators=new ArrayList<String>();operators.add("S");
    Motif_simpleTree_Individual source=new Motif_simpleTree_Individual(0,TreeIndividuals.MONOPHONY_MODE,terminals,operators,new ArrayList<String>());
    source.generateFromString("0"); String raw=source.getGenomeString();
    Motif_simpleTree_Individual output=source.createPlaybackClone(new PlaybackSettings(2.0,2,ScaleType.NATURAL_MINOR));
    CommonEventList events=output.convertToEventList();
    check(raw.equals(source.getGenomeString()),"source genome was modified");
    check(events.getNumOfNotes()==2,"BARFIX did not limit output to two beats");
    CommonEventList repeated=source.createPlaybackClone(new PlaybackSettings(2.0,2,ScaleType.NATURAL_MINOR)).convertToEventList();
    CommonEventList shorter=source.createPlaybackClone(new PlaybackSettings(1.0,2,ScaleType.NATURAL_MINOR)).convertToEventList();
    check(eventPitchTicks(events).equals(eventPitchTicks(repeated)),"same individual/settings produced different pitches");
    check(!eventPitchTicks(shorter).isEmpty()&&eventPitchTicks(shorter).get(0).equals(eventPitchTicks(events).get(0)),"BARFIX length changed the normalized register");
    Path midi=args.length==0?Files.createTempFile("cacie-playback-", ".mid"):Path.of(args[0]);
    MIDISequence memory=events.toMIDISequence(120); check(events.saveAsMIDISequence(midi.toString(),120),"MIDI export failed");
    List<String> expected=notes(memory.getSequence()), actual=notes(MidiSystem.getSequence(midi.toFile()));
    check(expected.equals(actual),"saved MIDI differs from playback sequence: "+expected+" != "+actual);
    check(actual.equals(java.util.Arrays.asList("0:60","16:60")),"unexpected scaled notes/ticks: "+actual);
    check(ScaleFilter.snapToNearestPitch(66,ScaleType.MAJOR,0)==65,"C major scale did not snap F# to F");
    check(ScaleFilter.snapToNearestPitch(60,ScaleType.NATURAL_MINOR,2)==60,"D natural minor changed an existing C");
    check(ScaleFilter.snapToNearestChordPitch(66,ScaleType.MAJOR_TRIAD,0)==67,"C major triad did not snap F# to G");
    check(ScaleFilter.snapToNearestChordPitch(62,ScaleType.MAJOR_TRIAD,0)==60,"equal chord distance must prefer the lower note");
    check(ScaleType.ALTERED.getIntervals().length==7&&ScaleType.COMBINATION_DIMINISHED.getIntervals().length==8&&ScaleType.WHOLE_TONE.getIntervals().length==6,"new scale definitions are invalid");
    CommonEventList high=new CommonEventList(new ArrayList<OneNote>(java.util.Arrays.asList(new OneNote(72,90,0,16))));high.setPitchEncoding(CommonEventList.PitchEncoding.MIDI_NOTE_NUMBER);
    CommonEventList low=new CommonEventList(new ArrayList<OneNote>(java.util.Arrays.asList(new OneNote(48,90,0,16),new OneNote(52,90,16,16))));low.setPitchEncoding(CommonEventList.PitchEncoding.MIDI_NOTE_NUMBER);
    Notes extremeHigh=new Notes();extremeHigh.addNote(new OneNote(108,90,0,16));extremeHigh.addNote(new OneNote(112,90,16,16));extremeHigh.fitParameters();Notes normalizedHigh=ScaleFilter.apply(extremeHigh,ScaleType.CHROMATIC,0);
    check(normalizedHigh.getNote(0).getNoteNumber()==60&&normalizedHigh.getNote(1).getNoteNumber()==64,"high register was not normalized deterministically");
    Notes extremeLow=new Notes();extremeLow.addNote(new OneNote(0,90,0,16));extremeLow.addNote(new OneNote(4,90,16,16));extremeLow.fitParameters();Notes normalizedLow=ScaleFilter.apply(extremeLow,ScaleType.CHROMATIC,0);
    check(normalizedLow.getNote(0).getNoteNumber()==60&&normalizedLow.getNote(1).getNoteNumber()==64,"low register was not normalized deterministically");
    check(DeterministicRegisterNormalizer.calculateOctaveShift(extremeHigh)==-48,"deterministic high-register shift changed");
    EightBeatDrumPattern drums=new EightBeatDrumPattern();
    List<String> twoBeat=notes(events.toMIDISequenceWithDrums(120,drums,32).getSequence(),9);
    check(twoBeat.equals(java.util.Arrays.asList("0:42","0:36","8:42","16:42","16:38","24:42")),"two-beat drum cut is wrong: "+twoBeat);
    List<String> fourBeat=notes(events.toMIDISequenceWithDrums(120,drums,64).getSequence(),9);
    check(fourBeat.size()==12&&!containsTickAtOrAfter(fourBeat,64),"four-beat drum pattern is wrong: "+fourBeat);
    List<String> eightBeat=notes(events.toMIDISequenceWithDrums(120,drums,128).getSequence(),9);
    check(eightBeat.size()==24&&eightBeat.contains("64:36")&&eightBeat.contains("120:42")&&!containsTickAtOrAfter(eightBeat,128),"eight-beat drum repeat is wrong: "+eightBeat);
    MIDISequence combined=events.toMIDISequenceWithDrums(120,drums,64);Path drumMidi=midi.resolveSibling("playback-with-drums.mid");combined.saveMIDISequenceToFile(drumMidi.toString(),0);
    check(notes(combined.getSequence(),9).equals(notes(MidiSystem.getSequence(drumMidi.toFile()),9)),"saved drum MIDI differs from memory sequence");
    check(notes(memory.getSequence(),0).equals(notes(combined.getSequence(),0)),"adding drums changed melody events");
    ArrayList<CommonEventList> planLists=new ArrayList<CommonEventList>();planLists.add(high);planLists.add(low);ArrayList<Long> planLengths=new ArrayList<Long>();planLengths.add(32L);planLengths.add(64L);
    PlaybackSequencePlan plan=PlaybackSequenceBuilder.build(planLists,planLengths,120,true);
    check(plan.getSegments().size()==2&&plan.getSegments().get(0).startTick==0&&plan.getSegments().get(0).endTick==32&&plan.getSegments().get(1).startTick==32&&plan.getSegments().get(1).endTick==96,"absolute playback segments are wrong");
    check(notes(plan.getMIDISequence().getSequence(),0).equals(java.util.Arrays.asList("0:72","32:48","48:52")),"absolute melody ticks are wrong or sequence order changed pitch: "+notes(plan.getMIDISequence().getSequence(),0));
    check(controllerEvents(plan.getMIDISequence().getSequence()).equals(java.util.Arrays.asList("0:1","32:2","96:0")),"slot marker events are wrong: "+controllerEvents(plan.getMIDISequence().getSequence()));
    check(notes(plan.getMIDISequence().getSequence(),9).contains("32:36"),"second drum segment did not restart at absolute tick 32");
    ArrayList<PlaybackLaneData> fourLanes=new ArrayList<PlaybackLaneData>();fourLanes.add(new PlaybackLaneData(java.util.Arrays.asList(high,low),0,0));fourLanes.add(new PlaybackLaneData(java.util.Arrays.asList(low,high),1,40));fourLanes.add(new PlaybackLaneData(java.util.Arrays.<CommonEventList>asList(null,null),2,48));fourLanes.add(new PlaybackLaneData(java.util.Arrays.<CommonEventList>asList(null,null),3,56));
    PlaybackSequencePlan multi=PlaybackSequenceBuilder.buildLanes(fourLanes,java.util.Arrays.asList(32L,64L),120,true);
    check(multi.getSegments().size()==2&&multi.getSegments().get(1).startTick==32,"four lanes do not share one Length timeline");
    check(notes(multi.getMIDISequence().getSequence(),0).equals(java.util.Arrays.asList("0:72","32:48","48:52")),"lane 1 channel/ticks are wrong");
    check(notes(multi.getMIDISequence().getSequence(),1).equals(java.util.Arrays.asList("0:48","16:52","32:72")),"lane 2 channel/ticks are wrong");
    check(programEvents(multi.getMIDISequence().getSequence()).containsAll(java.util.Arrays.asList("0:0:0","0:1:40","0:2:48","0:3:56")),"GM program changes are missing: "+programEvents(multi.getMIDISequence().getSequence()));
    check(notes(multi.getMIDISequence().getSequence(),9).size()>0,"shared drum track is missing from multi-lane plan");
    Path planMidi=midi.resolveSibling("absolute-playback-plan.mid");plan.getMIDISequence().saveMIDISequenceToFile(planMidi.toString(),0);
    check(notes(plan.getMIDISequence().getSequence()).equals(notes(MidiSystem.getSequence(planMidi.toFile()))),"saved absolute playback MIDI differs from memory sequence");
    RealtimeLoopSequence realtime=new RealtimeLoopSequence(120);RealtimeLoopSequence.Block loop0=realtime.append(0,plan);RealtimeLoopSequence.Block loop1=realtime.append(1,plan);realtime.append(2,plan);
    check(loop0.startTick==0&&loop1.startTick==96,"initial loop blocks are not contiguous");
    ArrayList<CommonEventList> changedLists=new ArrayList<CommonEventList>();changedLists.add(low);ArrayList<Long> changedLengths=new ArrayList<Long>();changedLengths.add(16L);PlaybackSequencePlan changedPlan=PlaybackSequenceBuilder.build(changedLists,changedLengths,120,false);
    List<String> currentBefore=notesInRange(realtime.getMIDISequence().getSequence(),0,96,0);RealtimeLoopSequence.Block replaced=realtime.replaceFuture(1,changedPlan,64);
    check(currentBefore.equals(notesInRange(realtime.getMIDISequence().getSequence(),0,96,0)),"replacing next loop changed the current loop");
    check(replaced.startTick==96&&replaced.endTick==112,"replacement loop has wrong absolute ticks");
    check(notesInRange(realtime.getMIDISequence().getSequence(),96,112,0).equals(java.util.Arrays.asList("96:48")),"next-loop notes were not replaced: "+notesInRange(realtime.getMIDISequence().getSequence(),96,112,0));
    check(realtime.getBlocks().get(2).startTick==112,"guard loop was not shifted after variable-length replacement");
    PlaybackSequencePlan lengthened=PlaybackSequenceBuilder.build(planLists,java.util.Arrays.asList(64L,64L),120,true);RealtimeLoopSequence.Block lengthenedLoop=realtime.replaceFuture(1,lengthened,80);
    check(lengthenedLoop.startTick==96&&lengthenedLoop.endTick==224,"Length extension did not resize the next loop");
    check(lengthenedLoop.plan.getSegments().get(1).startTick==64,"Length extension did not move the following slot");
    check(realtime.getBlocks().get(2).startTick==224,"Length extension did not move the guard loop");
    check(notesInRange(realtime.getMIDISequence().getSequence(),0,96,0).equals(currentBefore),"Length menu update changed the current loop");
    boolean rejected=false;try{realtime.replaceFuture(1,plan,96);}catch(IllegalStateException expectedException){rejected=true;}check(rejected,"a started block was replaceable");
    // Exercise the JDK's live Sequencer implementation: mutate a future Track block
    // while tick position advances, then verify it crosses the new boundary without stop/restart.
    PlaybackSequencePlan tiny=PlaybackSequenceBuilder.build(java.util.Arrays.asList(low),java.util.Arrays.asList(8L),600,false);
    PlaybackSequencePlan tinyChanged=PlaybackSequenceBuilder.build(java.util.Arrays.asList(high),java.util.Arrays.asList(4L),600,false);
    RealtimeLoopSequence live=new RealtimeLoopSequence(600);RealtimeLoopSequence.Block live0=live.append(0,tiny);live.append(1,tiny);live.append(2,tiny);
    Sequencer silent=MidiSystem.getSequencer(false);silent.open();silent.setSequence(live.getMIDISequence().getSequence());silent.start();
    waitForTick(silent,Math.max(1,live0.endTick/2),2000);long beforeMutation=silent.getTickPosition();live.replaceFuture(1,tinyChanged,beforeMutation);waitForTick(silent,live0.endTick+tinyChanged.getTotalTicks(),2000);long afterMutation=silent.getTickPosition();check(afterMutation>=live0.endTick+tinyChanged.getTotalTicks(),"live Sequencer did not cross the replaced loop boundary");silent.stop();silent.close();
    BufferedImage icon=TreeQuiltIcon.render(source,events);
    check(icon.getWidth()==96&&icon.getHeight()==96,"Tree Quilt icon is not 96 x 96");
    java.util.HashSet<Integer> colors=new java.util.HashSet<Integer>();for(int y=0;y<96;y++)for(int x=0;x<96;x++)colors.add(icon.getRGB(x,y));
    check(colors.size()>4,"Tree Quilt icon contains too few colors");
    Path png=midi.resolveSibling("tree-quilt.png");ImageIO.write(icon,"png",png.toFile());
    System.out.println("PASS playback/export notes and ticks: "+actual+" -> "+midi.toAbsolutePath());
    System.out.println("PASS 96 x 96 Tree Quilt rendering: "+png.toAbsolutePath());
    System.out.println("PASS Scale/Chord mapping and deterministic register normalization");
    System.out.println("PASS fixed eight-beat drum pattern, cuts and MIDI reload");
    System.out.println("PASS single absolute-tick playback plan and slot markers");
    System.out.println("PASS uninterrupted future-loop replacement and variable loop lengths");
    System.out.println("PASS four GM instrument lanes on one shared Sequence timeline");
  }
  private static boolean containsTickAtOrAfter(List<String> events,long limit){for(String event:events)if(Long.parseLong(event.substring(0,event.indexOf(':')))>=limit)return true;return false;}
  private static List<String> controllerEvents(Sequence sequence){ArrayList<String> out=new ArrayList<String>();for(Track track:sequence.getTracks())for(int i=0;i<track.size();i++){MidiEvent event=track.get(i);if(event.getMessage() instanceof ShortMessage){ShortMessage m=(ShortMessage)event.getMessage();if(m.getCommand()==ShortMessage.CONTROL_CHANGE&&m.getChannel()==PlaybackSequenceBuilder.MARKER_CHANNEL&&m.getData1()==PlaybackSequenceBuilder.MARKER_CONTROLLER)out.add(event.getTick()+":"+m.getData2());}}return out;}
  private static List<String> notesInRange(Sequence sequence,long start,long end,int channel){ArrayList<String> out=new ArrayList<String>();for(String event:notes(sequence,channel)){long tick=Long.parseLong(event.substring(0,event.indexOf(':')));if(tick>=start&&tick<end)out.add(event);}return out;}
  private static void waitForTick(Sequencer sequencer,long tick,long timeout)throws Exception{long deadline=System.currentTimeMillis()+timeout;while(sequencer.getTickPosition()<tick&&System.currentTimeMillis()<deadline)Thread.sleep(2);check(sequencer.getTickPosition()>=tick,"Sequencer timeout at tick "+sequencer.getTickPosition()+" waiting for "+tick);}
  private static List<String> eventPitchTicks(CommonEventList list){ArrayList<String> out=new ArrayList<String>();for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);if(n.getVelocity()>0)out.add(n.getPosition()+":"+n.getNoteNumber());}return out;}
  private static List<String> programEvents(Sequence sequence){ArrayList<String> out=new ArrayList<String>();for(Track track:sequence.getTracks())for(int i=0;i<track.size();i++){MidiEvent e=track.get(i);if(e.getMessage() instanceof ShortMessage){ShortMessage m=(ShortMessage)e.getMessage();if(m.getCommand()==ShortMessage.PROGRAM_CHANGE)out.add(e.getTick()+":"+m.getChannel()+":"+m.getData1());}}return out;}
}
