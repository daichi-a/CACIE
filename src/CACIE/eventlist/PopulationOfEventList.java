package CACIE.eventlist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
//import javax.sound.midi.Track;

import CACIE.genome.Notes;

public class PopulationOfEventList
{
	private ArrayList<CommonEventList> setOfCommonEventList;
	private int numOfIndividuals;
	//private static String type = "Population";

	// Constructor
	public PopulationOfEventList()
	{
		setOfCommonEventList = new ArrayList<CommonEventList>();
		numOfIndividuals = setOfCommonEventList.size();
	}

	public PopulationOfEventList(int numOfIndividual)
	{
		setOfCommonEventList = new ArrayList<CommonEventList>(numOfIndividual);
		this.numOfIndividuals = setOfCommonEventList.size();
	}

	public PopulationOfEventList(ArrayList<CommonEventList> setOfEventList)
	{
		this.setOfCommonEventList = setOfEventList;
		numOfIndividuals = this.setOfCommonEventList.size();
	}

	// instance methods

	public int numberOfIndividuals()
	{
		this.numOfIndividuals = this.setOfCommonEventList.size();
		return this.numOfIndividuals;
	}

	public int getNumOfIndividuals()
	{
		this.numOfIndividuals = this.setOfCommonEventList.size();
		return this.numOfIndividuals;
	}

	public void numberOfIndividuals(int numOfIndividuals)
	{
		this.numOfIndividuals = numOfIndividuals;
		this.setOfCommonEventList.ensureCapacity(this.numOfIndividuals);
	}

	public void setNumOfIndividuals(int numOfIndividuals)
	{
		this.numOfIndividuals = numOfIndividuals;
		this.setOfCommonEventList.ensureCapacity(this.numOfIndividuals);
	}

	public void add(CommonEventList eventlist)
	{
		this.setOfCommonEventList.add(eventlist);
		this.numOfIndividuals = this.setOfCommonEventList.size();
	}

	public void add(PopulationOfEventList eventlist)
	{
		int until = eventlist.getNumOfIndividuals();
		for (int i = 0; i < until; i++)
		{
			CommonEventList tmpEventList = (CommonEventList) eventlist.get(i);
			this.add(tmpEventList);
		}
	}

	public void remove(int index)
	{
		if (index >= 0 && index < this.numOfIndividuals)
		{
			this.setOfCommonEventList.remove(index);
			this.numOfIndividuals = this.setOfCommonEventList.size();
		}
	}

	public Object get(int index)
	{
		if (index < this.setOfCommonEventList.size())
			return (Object) this.setOfCommonEventList.get(index);
		else
		{
			CommonEventList tmpEventList = new CommonEventList(0);
			return (Object) tmpEventList;
		}
	}

	public boolean writeAIndividualToFile(int index, String fileName)
	{
		CommonEventList aIndividual = (CommonEventList) this.get(index);
		aIndividual.setIDNumber(index);
		return aIndividual.writeToFile(fileName);
	}

	public void playAIndividual(int index)
	{
		CommonEventList aIndividual = (CommonEventList) this.get(index);
		try {
			aIndividual.playAsMIDISequence();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Fixed method to properly play MIDI sequence
	public void playMIDISequence() {
		try {
			// Create a new sequence
			Sequence sequence = new Sequence(Sequence.PPQ, 24);
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			
			// Add tracks to the sequence
			//Track track = sequence.createTrack();
			
			// This would need to be implemented based on the actual note data
			// For now, just opening the sequencer
			sequencer.start();
			
			// Wait for playback to complete
			while (sequencer.isRunning()) {
				Thread.sleep(100);
			}
			
			sequencer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFileHeaderPart(BufferedWriter out)
	{
		try
		{
			out.write("Population");
			out.newLine();
			out.write("NumberOfIndividuals," + String.valueOf(this.numOfIndividuals));
			out.newLine();
		} catch (IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}

	private void writeToFileFooterPart(BufferedWriter out)
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

	private void writeToFileIndividualPart(BufferedWriter out)
	{
		CommonEventList aIndividual;

		for (int i = 0; i < this.numOfIndividuals; i++)
		{
			aIndividual = (CommonEventList) this.setOfCommonEventList.get(i);
			aIndividual.setIDNumber(i);
			aIndividual.writeToFileInPopulation(out);
		}
	}

	public boolean writeToFile(String fileName)
	{
		//CommonEventList aIndividual;
		boolean returnValue = false;
		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new FileWriter(fileName));

			this.writeToFileHeaderPart(out);
			this.writeToFileIndividualPart(out);
			this.writeToFileFooterPart(out);

			out.close();
			returnValue = true;
		} catch (IOException e)
		{
			System.err.println(e);
		}
		return returnValue;
	}

	public static boolean isPopulation(String fileName)
	{
		BufferedReader in;
		boolean returnValue = false;
		try
		{
			in = new BufferedReader(new FileReader(fileName));
			String s = in.readLine();
			if (s.equals("Population"))
				returnValue = true;
			in.close();
		} catch (IOException e)
		{
			System.err.println(e);
		}
		return returnValue;
	}
	
    public boolean readFromFile(InputStream input)
    {
        BufferedReader in;
        boolean returnValue = false;
        try
        {
            in = new BufferedReader(new InputStreamReader(input));
            if (this.readFromFileHeaderPart(in))
                if (this.readFromFileIndividualPart(in))
                    if (this.readFromFileFooterPart(in))
                        returnValue = true;
                    else
                        System.err.println("PopulationOfEventList:readFromFile:reaFromFileFooterPart returns false");
                else
                    System.err.println("PopulationOfEventList:readFromFile:reaFromFileIndividualrPart returns false");
            else
                System.err.println("PopulationOfEventList:readFromFile:reaFromFileHeaderPart returns false");
            in.close();
        } catch (IOException e)
        {
            System.err.println(e);
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
				if (this.readFromFileIndividualPart(in))
					if (this.readFromFileFooterPart(in))
						returnValue = true;
					else
						System.err.println("PopulationOfEventList:readFromFile:reaFromFileFooterPart returns false");
				else
					System.err.println("PopulationOfEventList:readFromFile:reaFromFileIndividualrPart returns false");
			else
				System.err.println("PopulationOfEventList:readFromFile:reaFromFileHeaderPart returns false");
			in.close();
		} catch (IOException e)
		{
			System.err.println(e);
		}
		return returnValue;
	}

	private boolean readFromFileHeaderPart(BufferedReader in)
	{
		String s, tmpString, subtractedString;
		boolean returnValue = false;
		try
		{
			s = in.readLine();
			if (s.equals("Population"))
			{
				tmpString = in.readLine();

				subtractedString = tmpString.substring(0, 20);

				if (subtractedString.equals("NumberOfIndividuals,"))
				{
					this.numOfIndividuals = Integer.parseInt(tmpString.substring(20));
					//System.err.println("Number of terminal nodes in file is " + numOfIndividuals);
					returnValue = true;
				}
			}
		} catch (IOException e)
		{
			System.err.println(e);
		}
		return returnValue;
	}

	private boolean readFromFileFooterPart(BufferedReader in)
	{
		String s;
		boolean returnValue = false;
		try
		{
			s = in.readLine();
			if (s.equals("\\e"))
				returnValue = true;
		} catch (IOException e)
		{
			System.err.println(e);
		}
		return returnValue;
	}

	private boolean readFromFileIndividualPart(BufferedReader in)
	{
		boolean returnValue = false;
		CommonEventList tmpEventList;

		setOfCommonEventList.clear();
		setOfCommonEventList.ensureCapacity(numOfIndividuals);
		for (int i = 0; i < numOfIndividuals; i++)
		{
			tmpEventList = new CommonEventList(i);
			if (tmpEventList.readFromFileInPopulation(in)){
				setOfCommonEventList.add(tmpEventList);
			}
			else{
				System.err.println
				("PopulationOfEventList: readFromFileIndividualPart: commonEventlist.readFromFileInPopulation returns false. index is:" + i);
				while(true){
					try{
						if(in.readLine().equals("\\e"))
							break;
					}
					catch(IOException e){
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		}
		
		if (setOfCommonEventList.size() == this.numOfIndividuals)
			returnValue = true;
		else{
			System.err.println("Number of terminal nodes in file is " + numOfIndividuals + " in file header.");
			System.err.println("Number of terminal nodes in file is " + setOfCommonEventList.size() + " of setOfEventList");
			numOfIndividuals = setOfCommonEventList.size();
		}
		return returnValue;
	}

	public ArrayList<Notes> getNotesFromPopulation()
	{
		ArrayList<Notes> returnArray = new ArrayList<Notes>(this.numOfIndividuals);
		returnArray.ensureCapacity(this.numOfIndividuals);
		for (int i = 0; i < this.numOfIndividuals; i++)
		{
			CommonEventList tmpEventList = (CommonEventList) this.setOfCommonEventList.get(i);
			Notes tmpNotes = (Notes) tmpEventList.getNotes();
			tmpNotes.fitParameters();
			returnArray.add(tmpNotes);
		}
		return returnArray;
	}

	/*
	 * public boolean readFromFile(String fileName){ String s; OneNote tmpNote;
	 * int index1, index2, index3, tmpIdNumber, tmpNumOfNote; Integer tmpInt1,
	 * tmpInt2, tmpInt3, tmpInt4; CommonEventList tmpEventList;
	 * 
	 * this.setOfCommonEventList.clear(); try{ BufferedReader in = new
	 * BufferedReader(new FileReader(fileName)); s = in.readLine();
	 * if(this.type.equals(s) == false){ return false; } else{ //Number Of
	 * Individual tmpInt1 = new Integer(in.readLine()); this.numOfIndividuals =
	 * tmpInt1.intValue();
	 * 
	 * for(int i=0; i<this.numOfIndividuals; i++){ //Id Number for each
	 * individual tmpInt1 = new Integer(in.readLine()); tmpIdNumber =
	 * tmpInt1.intValue(); //Number of Note for each individual tmpInt1 = new
	 * Integer(in.readLine()); tmpNumOfNote = tmpInt1.intValue(); tmpEventList =
	 * new CommonEventList(tmpNumOfNote); tmpEventList.idNumber(tmpIdNumber);
	 * for(int j=0; j<tmpNumOfNote; j++){ if((s = in.readLine()) == null){ return
	 * false; } else{ index1 = s.indexOf(","); index2 = s.indexOf(",", index1 +1);
	 * index3 = s.indexOf(",", index2 +1); tmpInt1 = new Integer(s.substring(0,
	 * index1)); tmpInt2 = new Integer(s.substring(index1 +1, index2)); tmpInt3 =
	 * new Integer(s.substring(index2 +1, index3)); tmpInt4 = new
	 * Integer(s.substring(index3 +1, s.length())); tmpNote = new
	 * OneNote(tmpInt1.intValue(), tmpInt2.intValue(), tmpInt3.intValue(),
	 * tmpInt4.intValue()); tmpEventList.add(tmpNote); } }
	 * this.setOfCommonEventList.add((Object)tmpEventList); } in.close(); } }
	 * catch (IOException e){ System.err.println(e); System.exit(1); } return
	 * true; }
	 */
}
