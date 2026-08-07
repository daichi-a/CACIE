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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/** Headless test of output-only SCALE/BARFIX decoration and MIDI export. */
public final class CACIE_PlaybackMidiCuiTest {
  private static void check(boolean ok,String message){if(!ok)throw new IllegalStateException(message);}
  private static List<String> notes(Sequence sequence){
    ArrayList<String> out=new ArrayList<String>();
    for(Track track:sequence.getTracks())for(int i=0;i<track.size();i++){
      MidiEvent event=track.get(i); if(event.getMessage() instanceof ShortMessage){ShortMessage m=(ShortMessage)event.getMessage();if(m.getCommand()==ShortMessage.NOTE_ON&&m.getData2()>0)out.add(event.getTick()+":"+m.getData1());}
    } return out;
  }
  public static void main(String[] args)throws Exception{
    System.setProperty("java.awt.headless","true");
    Notes phrase=new Notes();
    phrase.addNote(new OneNote(0,90,0,16)); phrase.addNote(new OneNote(1,90,16,16));
    phrase.addNote(new OneNote(2,90,32,16)); phrase.addNote(new OneNote(3,90,48,16)); phrase.fitParameters();
    ArrayList<Notes> terminals=new ArrayList<Notes>();terminals.add(phrase);
    ArrayList<String> operators=new ArrayList<String>();operators.add("S");
    Motif_simpleTree_Individual source=new Motif_simpleTree_Individual(0,TreeIndividuals.MONOPHONY_MODE,terminals,operators,new ArrayList<String>());
    source.generateFromString("0"); String raw=source.getGenomeString();
    Motif_simpleTree_Individual output=source.createPlaybackClone(new PlaybackSettings(2.0,2,ScaleType.NATURAL_MINOR));
    CommonEventList events=output.convertToEventList();
    check(raw.equals(source.getGenomeString()),"source genome was modified");
    check(events.getNumOfNotes()==2,"BARFIX did not limit output to two beats");
    Path midi=args.length==0?Files.createTempFile("cacie-playback-", ".mid"):Path.of(args[0]);
    MIDISequence memory=events.toMIDISequence(120); check(events.saveAsMIDISequence(midi.toString(),120),"MIDI export failed");
    List<String> expected=notes(memory.getSequence()), actual=notes(MidiSystem.getSequence(midi.toFile()));
    check(expected.equals(actual),"saved MIDI differs from playback sequence: "+expected+" != "+actual);
    check(actual.equals(java.util.Arrays.asList("0:2","16:4")),"unexpected scaled notes/ticks: "+actual);
    BufferedImage icon=TreeQuiltIcon.render(source,events);
    check(icon.getWidth()==96&&icon.getHeight()==96,"Tree Quilt icon is not 96 x 96");
    java.util.HashSet<Integer> colors=new java.util.HashSet<Integer>();for(int y=0;y<96;y++)for(int x=0;x<96;x++)colors.add(icon.getRGB(x,y));
    check(colors.size()>4,"Tree Quilt icon contains too few colors");
    Path png=midi.resolveSibling("tree-quilt.png");ImageIO.write(icon,"png",png.toFile());
    System.out.println("PASS playback/export notes and ticks: "+actual+" -> "+midi.toAbsolutePath());
    System.out.println("PASS 96 x 96 Tree Quilt rendering: "+png.toAbsolutePath());
  }
}
