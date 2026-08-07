package CACIE.eventlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

import CACIE.genome.Notes;
import CACIE.genome.OneNote;
import CACIE.midi.MIDISequence;
import CACIE.midi.DrumPattern;

public class CommonEventList
{
	public enum PitchEncoding { DIATONIC_DEGREE, MIDI_NOTE_NUMBER }
	
	public static int DIATONIC_inC = 1;
	public static int CHROMATIC = 0;
	private static int scaleMode = DIATONIC_inC;

	public static int DT = 120; // Default Tempo
	
	private ArrayList<OneNote> eventList;
	private int numOfNotes;
	private int idNumber;
	private static String type = "Individual";
	private String genomeString;
	private int DefaultTempo;

	private Sequencer runningSeq;
	
	private boolean playingState = false;
	
	private int instrumentNumber;
	private PitchEncoding pitchEncoding = PitchEncoding.DIATONIC_DEGREE;
	public int DI = 0; // Default Instrument
	
	// Constructor

	public CommonEventList(int numOfNotes)
	{
		this.numOfNotes = numOfNotes;
		eventList = new ArrayList<OneNote>(this.numOfNotes);
		idNumber = 0;
		genomeString = new String("empty~~~~");
		DefaultTempo = DT;
		instrumentNumber = 0;
	}

	public CommonEventList(ArrayList<OneNote> eventList)
	{
		this.eventList = eventList;
		numOfNotes = this.eventList.size();
		idNumber = 0;
		genomeString = new String("empty~~~~");
		DefaultTempo = DT;
		instrumentNumber = 0;
	}

	// Instance method
	public int numberOfNote()
	{
		return numOfNotes;
	}

	public void numberOfNote(int numOfNotes)
	{
		this.numOfNotes = numOfNotes;
		eventList.ensureCapacity(this.numOfNotes);
	}

	public int idNumber()
	{
		return this.idNumber;
	}

	public void idNumber(int idNumber)
	{
		this.idNumber = idNumber;
	}

	public void setIDNumber(int idNumber)
	{
		this.idNumber = idNumber;
	}

	public void setList(ArrayList<OneNote> eventList)
	{
		this.eventList = eventList;
	}

	public void add(OneNote aNote)
	{
		this.eventList.ensureCapacity(this.eventList.size() + 1);
		this.eventList.add(aNote);
		this.numOfNotes = this.eventList.size();
	}

	public void add(Notes notes)
	{
		ArrayList<OneNote> tmpList = notes.getNoteArray();
		int until = tmpList.size();
		for (int i = 0; i < until; i++)
		{
			OneNote tmpNote = tmpList.get(i);
			this.add(tmpNote);
		}
		this.numOfNotes = this.eventList.size();
	}

	public Object get(int index)
	{
		if (index < eventList.size())
			return eventList.get(index);
		else return new OneNote();
	}

	public Notes getNotes()
	{
		Notes tmpNotes = new Notes();
		int until = this.eventList.size();
		for (int i = 0; i < until; i++)
		{
			OneNote tmpNote = (OneNote) this.eventList.get(i);
			tmpNotes.addNote(tmpNote);
		}
		if (tmpNotes.getNumOfNotes() == 0)
		{
			System.out.println("CommonEventList:getNotes: Has No Note!");
		}
		tmpNotes.fitParameters();
		return tmpNotes.clone();
	}

	public void fixNumOfNotes()
	{
		this.numOfNotes = this.eventList.size();
	}

	public int getNumOfNotes()
	{
		this.numOfNotes = this.eventList.size();
		return this.numOfNotes;
	}

	public void setNumOfNotes(int numOfNotes)
	{
		this.numOfNotes = numOfNotes;
	}
	
	public void setGenomeString(String st)
	{
		genomeString = st;
	}
	
	public String getGenomeString()
	{
		return genomeString;
	}
	
	// tanji
	public int getTempo()
	{
		return this.DefaultTempo;
	}
	
	public void setTempo(int newTempo)
	{
		this.DefaultTempo = newTempo;
	}
	//
	
	public int getInstrumentNumber(){
		return instrumentNumber;
	}
	
	public void setInstrumentNumber(int _instrumentNumber){
		instrumentNumber = _instrumentNumber;
	}

	public PitchEncoding getPitchEncoding(){
		return pitchEncoding;
	}

	public void setPitchEncoding(PitchEncoding pitchEncoding){
		this.pitchEncoding = pitchEncoding;
	}
	
	protected void writeEventListPart(BufferedWriter out)
	{
		OneNote tmpNote;
		try
		{
			out.write("Event");
			out.newLine();
			if (this.numOfNotes != 0)
			{
				for (int i = 0; i < numOfNotes; i++)
				{
					tmpNote = (OneNote) eventList.get(i);
					out.write((String) tmpNote.getEventList());
					out.newLine();
				}

			}
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}

	protected void writeGenomPart(BufferedWriter out)
	{
		try
		{
			out.write("Genom");
			out.newLine();
			out.write((String) this.genomeString);
			out.newLine();
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}

	protected void writeHeaderPart(BufferedWriter out)
	{
		String s;
		try
		{
			out.write(type);
			out.newLine();
			s = new String("IDNumber," + String.valueOf(idNumber));
			out.write(s);
			out.newLine();
			s = new String("NumberOfNotes," + String.valueOf(numOfNotes));
			out.write(s);
			out.newLine();
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}

	protected void writeFooterPart(BufferedWriter out)
	{
		try
		{
			out.write("\\e");
			out.newLine();
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}

	public void writeToFileInPopulation(BufferedWriter out)
	{
		this.writeHeaderPart(out);
		this.writeEventListPart(out);
		this.writeGenomPart(out);
		this.writeFooterPart(out);
	}

	public boolean writeToFile(String fileName)
	{
		boolean returnValue = false;
		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new FileWriter(fileName));

			this.writeHeaderPart(out);
			this.writeEventListPart(out);
			this.writeGenomPart(out);
			this.writeFooterPart(out);

			out.close();
			returnValue = true;
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
		return returnValue;
	}

	public void stopMIDISequence()
	{
		if (this.playingState)
		{
			if (this.runningSeq.isRunning())
			{
				this.runningSeq.stop();
			}
			this.runningSeq.close();
			playingState = false;
		}
	}
	
	public void playAsMIDISequence() throws MidiUnavailableException, InvalidMidiDataException
	{
		playAsMIDISequence(DefaultTempo);
	}
	
	public void playAsMIDISequence(int tempo) throws MidiUnavailableException, InvalidMidiDataException
	{
		MIDISequence mySeq = toMIDISequence(tempo);
		this.runningSeq = mySeq.playMIDISequenceWithSequencer();
		this.playingState = true;
	}

	/** The one MIDI conversion path shared by playback, export and headless tests. */
	public MIDISequence toMIDISequence(int tempo) throws MidiUnavailableException {
		MIDISequence sequence = new MIDISequence();
		sequence.setTempo(0, tempo);
		sequence.setInstrument(0, 0, instrumentNumber, 0);
		for (int i=0;i<numOfNotes;i++) {
			OneNote note=fixParameter(eventList.get(i));
			if(note.getVelocity()>0 && note.getPosition()>=0) {
				int pitch=pitchEncoding==PitchEncoding.MIDI_NOTE_NUMBER ? note.noteNumber() : convertChromaticToDiatonicInC(note.noteNumber());
				sequence.setNoteToTrack(0,0,fitMidiNoteNumber(pitch),note.noteVelocity(),note.positionInMotif(),note.noteLength());
			}
		}
		return sequence;
	}

	public MIDISequence toMIDISequenceWithDrums(int tempo,DrumPattern pattern,long durationTicks) throws MidiUnavailableException {
		MIDISequence sequence=toMIDISequence(tempo);pattern.addTo(sequence,durationTicks);return sequence;
	}

	public void playAsMIDISequenceWithDrums(int tempo,DrumPattern pattern,long durationTicks) throws MidiUnavailableException,InvalidMidiDataException {
		MIDISequence sequence=toMIDISequenceWithDrums(tempo,pattern,durationTicks);runningSeq=sequence.playMIDISequenceWithSequencer();playingState=true;
	}
	
	public static int convertChromaticToDiatonicInC(int inputNoteNumber){
		int returnNumber = 0;
		if(inputNoteNumber != 0){
			int degree = inputNoteNumber % 7;
			int octave = 0;
			if(inputNoteNumber > 6)
				octave = inputNoteNumber / 7;
			int keyNoteNumber = 0; //Do
			switch(degree){
				case(1): keyNoteNumber = 2; //Re
				break;
				case(2): keyNoteNumber = 4; //Mi
				break;
				case(3): keyNoteNumber = 5; //Fa
				break;
				case(4): keyNoteNumber = 7; //So
				break;
				case(5): keyNoteNumber = 9; //La
				break;
				case(6): keyNoteNumber = 11; //Si
				break;
			}
			returnNumber = octave * 12 + keyNoteNumber; 
		}
		return returnNumber;
	}
	
	public boolean saveAsMIDISequence(String fileName)
	{
		return saveAsMIDISequence(fileName, DefaultTempo);
	}

	public boolean saveAsMIDISequence(String fileName, int tempo)
	{
		boolean returnValue = false;
		try {
			MIDISequence mySeq=toMIDISequence(tempo);
			mySeq.saveMIDISequenceToFile(fileName, 0);
			returnValue = true;
		} catch (Exception e)
		{
			System.err.println(e);
			System.exit(1);
		}
		return returnValue;
	}

	public boolean readFromFileInPopulation(BufferedReader in)
	{
		boolean returnValue = false;
		if (this.readFromFileHeaderPart(in)){
			if (this.readFromFileEventListPart(in)){
				if (this.readFromFileGenomStringPart(in)){
					if (this.readFromFileFooterPart(in))
						returnValue = true;
					else
						System.err.println("CommonEventList:readFromFileInPopulation:reaFromFileFooterPart returns false");
				}
				else{
					System.err.println("CommonEventList:readFromFileInPopulation:reaFromFileGenomeStringPart returns false");
				}
			}
			else{
				System.err.println("CommonEventList:readFromFileInPopulation:reaFromFileEventListPart returns false");				
			}
		}
		else{
			System.err.println("CommonEventList:readFromFileInPopulation:reaFromFileHeaderPart returns false");
		}
		
		return returnValue;
	}

	public boolean readFromFile(String fileName)
	{
		BufferedReader in;
		boolean returnValue = false;
		try
		{
			in = new BufferedReader(new FileReader(fileName));

			if (this.readFromFileHeaderPart(in))
				if (this.readFromFileEventListPart(in))
					if (this.readFromFileGenomStringPart(in))
						if (this.readFromFileFooterPart(in))
							returnValue = true;
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
		return returnValue;
	}

	protected boolean readFromFileEventListPart(BufferedReader in)
	{
		boolean returnValue = false;
		String s;
		int index1, index2, index3; 

		// Integer tmpInt1, tmpInt2, tmpInt3, tmpInt4;
		int tmpNoteNum, tmpVelocity, tmpPosition, tmpDuration;
		OneNote tmpNote;
		try
		{
			s = in.readLine();
			if (s.equals("Event"))
			{
				for (int i = 0; i < this.numOfNotes; i++)
				{
					s = in.readLine();
					index1 = s.indexOf(",");
					index2 = s.indexOf(",", index1 + 1);
					index3 = s.indexOf(",", index2 + 1);
					tmpNoteNum = Integer.parseInt(s.substring(0, index1));
					tmpVelocity = Integer.parseInt(s.substring(index1 + 1, index2));
					tmpPosition = Integer.parseInt(s.substring(index2 + 1, index3));
					tmpDuration = Integer.parseInt(s.substring((index3 + 1)));
					tmpNote = new OneNote(tmpNoteNum, tmpVelocity, tmpPosition, tmpDuration);

					int tmpCapa = this.eventList.size();
					this.eventList.ensureCapacity(++tmpCapa);
					this.eventList.add(tmpNote);
					// System.out.println(tmpNoteNum + "," + tmpVelocity + "," +
					// tmpPosition + "," + tmpDuration);
				}
				returnValue = true;
			}
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
		return returnValue;
	}

	protected boolean readFromFileGenomStringPart(BufferedReader in)
	{
		boolean returnValue = false;
		try
		{
			String tmpString = in.readLine();
			if (tmpString.equals("Genom"))
			{
				this.genomeString = in.readLine();
				returnValue = true;
			}
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
		return returnValue;
	}

	protected boolean readFromFileHeaderPart(BufferedReader in)
	{
		String s, tmpString;
		boolean returnValue = false;
		try
		{
			s = in.readLine();
			if (s.equals("Individual"))
			{
				s = in.readLine();
				tmpString = s.substring(0, 9);
				if (tmpString.equals("IDNumber,"))
				{
					this.idNumber(Integer.parseInt(s.substring(9)));
					s = in.readLine();
					tmpString = s.substring(0, 14);
					if (tmpString.equals("NumberOfNotes,"))
					{
						this.setNumOfNotes(Integer.parseInt(s.substring(14)));
						returnValue = true;
					}
				}
			}
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}

		return returnValue;
	}

	protected boolean readFromFileFooterPart(BufferedReader in)
	{
		boolean returnValue = false;
		String s;
		try
		{
			s = in.readLine();
			if (s.equals("\\e"))
				returnValue = true;
		} catch (IOException e)
		{
			System.err.println("e");
			System.exit(1);
		}

		return returnValue;
	}

	private OneNote fixParameter(OneNote tmpNote)
	{
		int tmp;
		if ((tmp = tmpNote.getNoteNumber()) > 127)
			tmpNote.setNoteNumber(127);
		if (tmp < 0)
			tmpNote.setVelocity(0);
		if ((tmp = tmpNote.getVelocity()) > 127)
			tmpNote.setVelocity(127);
		if (tmp < 0)
			tmpNote.setVelocity(0);

		return tmpNote;
	}

	/** Keep a converted pitch inside the range accepted by a MIDI note event. */
	private static int fitMidiNoteNumber(int noteNumber)
	{
		return Math.max(0, Math.min(127, noteNumber));
	}

	public boolean getPlayingState(){
		return playingState;
	}
	
	public static void main(String args[])
	{
	  for( int i = 0; i < 30; i++ )
	  {
//	    /System.out.println( i + " D -> C: " + convertDiatonicToChromaticInC(i) );
	    System.out.println( i + " C -> D: " + convertChromaticToDiatonicInC(i) );
	  }
	}
}
