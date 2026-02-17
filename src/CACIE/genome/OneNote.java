package CACIE.genome;

public class OneNote
{

	int noteNumber;
	int noteLength;
	int noteVelocity;
	long positionInMotif;

	// Constructor

	public OneNote()
	{
		this.noteNumber = 0;
		this.noteLength = 0;
		this.noteVelocity = 0;
		this.positionInMotif = 0;

	}

	public OneNote(int noteNumber, int noteVelocity, long positionInMotif, int noteLength)
	{
		this.noteNumber = noteNumber;
		this.noteLength = noteLength;
		this.noteVelocity = noteVelocity;
		this.positionInMotif = positionInMotif;
	}

	protected OneNote clone()
	{
		OneNote returnNote = new OneNote(this.getNoteNumber(), this.getVelocity(), this.getPosition(), this.getDuration());
		return returnNote;
	}

	// Instance methods.
	// In-Out methods

	public int noteNumber()
	{
		return this.noteNumber;
	}

	public void noteNumber(int noteNumber)
	{
		this.noteNumber = noteNumber;
	}

	public int getNoteNumber()
	{
		return this.noteNumber;
	}

	public void setNoteNumber(int value)
	{
		this.noteNumber = value;
	}

	public int noteLength()
	{
		return this.noteLength;
	}

	public int getDuration()
	{
		return this.noteLength;
	}

	public void setDuration(int value)
	{
		this.noteLength = value;
	}

	public void setDuration(long value)
	{
		this.noteLength = (int) value;
	}

	public void noteLength(int noteLength)
	{
		this.noteLength = noteLength;
	}

	public int noteVelocity()
	{
		return this.noteVelocity;
	}

	public void noteVelocity(int noteVelocity)
	{
		this.noteVelocity = noteVelocity;
	}

	public int getVelocity()
	{
		return this.noteVelocity;
	}

	public void setVelocity(int value)
	{
		this.noteVelocity = value;
	}

	public long positionInMotif()
	{
		return this.positionInMotif;
	}

	public void positionInMotif(long positionInMotif)
	{
		this.positionInMotif = positionInMotif;
	}

	public long getPosition()
	{
		return this.positionInMotif;
	}

	public void setPosition(long value)
	{
		this.positionInMotif = value;
	}

	public Object getEventList()
	{
		String tmpEventList = new String(this.noteNumber() + "," + this.noteVelocity() + "," + this.positionInMotif() + ","
				+ this.noteLength());
		return tmpEventList;
	}

}
