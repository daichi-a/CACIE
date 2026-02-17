package CACIE.genome;

import java.util.ArrayList;

public class Notes {

	ArrayList<OneNote> noteArray;

	long position;

	long duration;

	int numOfNotes;

	public Notes() {
		noteArray = new ArrayList<OneNote>();
		position = 0;
		duration = 0;
		numOfNotes = 0;
	}

	public Notes(OneNote note) {
		note.setPosition(0);
		this.noteArray = new ArrayList<OneNote>(1);
		this.noteArray.add(note);
		this.position = 0;
		this.fitPosition();
		this.duration = note.getDuration();
	}

	public Notes clone() {
		Notes notes = new Notes();
		notes.noteArray.ensureCapacity(this.noteArray.size());
		int until = this.noteArray.size();
		for (int i = 0; i < until; i++) {
			OneNote tmpNote = (OneNote) this.noteArray.get(i);
			notes.noteArray.add(tmpNote.clone());
		}
		notes.numOfNotes = this.numOfNotes;
		notes.position = this.position;
		notes.duration = this.duration;
		return notes;
	}

	public OneNote getNote(int index) {
		OneNote tmpNote = new OneNote();
		if (index < 0 && index > (this.noteArray.size() - 1)) {
			System.err.println("Notes:getNote:IndexError: Index:" + " Size:"
					+ this.noteArray.size());
		} else
			tmpNote = (OneNote) this.noteArray.get(index);
		return tmpNote;
	}

	public void addNote(OneNote note) {
		this.noteArray.ensureCapacity(this.noteArray.size() + 1);
		this.noteArray.add(note);
		this.duration = this.culcDuration();
	}

	public void addNote(int index, OneNote note) {
		this.noteArray.ensureCapacity(this.noteArray.size() + 1);
		this.noteArray.add(index, note);
		this.duration = this.culcDuration();
		this.numOfNotes = this.noteArray.size();
	}

	public void addNotes(Notes notes) {
		ArrayList<OneNote> tmpArray = notes.getNoteArray();
		int until = tmpArray.size();
		for (int i = 0; i < until; i++) {
			this.noteArray.ensureCapacity(this.noteArray.size() + 1);
			this.noteArray.add(tmpArray.get(i));
		}
		this.duration = this.culcDuration();
	}

	public long getPosition() {
		return this.position;
	}

	public void fitParameters() {
		this.sortNotes();
		this.fitPosition();
		this.numOfNotes = this.noteArray.size();
		this.duration = this.culcDuration();
		this.position = 0;
	}

	protected void sortNotes() {
		Notes newNotes = new Notes();
		Notes baseNotes = (Notes) this.clone();
		ArrayList<OneNote> baseArray = baseNotes.noteArray;
		ArrayList<OneNote> newArray = newNotes.noteArray;
		while (baseArray.size() > 0) {
			long minPos = Long.MAX_VALUE;
			int minPosIndex = -1;
			for (int i = 0; i < baseArray.size(); i++) {
				OneNote tmpNote = (OneNote) baseArray.get(i);
				if (tmpNote.getPosition() < minPos) {
					minPos = tmpNote.getPosition();
					minPosIndex = i;
				}
			}
			newArray.add(baseArray.get(minPosIndex));
			baseArray.remove(minPosIndex);
		}
		this.noteArray = newNotes.noteArray;
		this.fitPosition();
	}

	protected void fitPosition() {
		long minPos = Integer.MAX_VALUE;
		int until = this.noteArray.size();
		OneNote tmpNote;
		for (int i = 0; i < until; i++) {
			tmpNote = (OneNote) this.noteArray.get(i);
			// if(tmpNote.getPosition() < 0)
			// System.err.println("ERROR Position : " + tmpNote.getPosition());
			if (tmpNote.getPosition() < minPos)
				minPos = tmpNote.getPosition();
		}

		if (minPos > 0) {
			// System.out.println("minPos : " + minPos);
			for (int i = 0; i < until; i++) {
				tmpNote = (OneNote) this.noteArray.get(i);
				tmpNote.setPosition(tmpNote.getPosition() - minPos);
			}
		}
	}

	public ArrayList<OneNote> getNoteArray() {
		return noteArray;
	}

	public int getNumOfNotes() {
		this.numOfNotes = this.noteArray.size();
		return this.noteArray.size();
	}

	public void setNoteArray(ArrayList<OneNote> noteArray, int duration) {
		this.noteArray.clear();
		this.noteArray.ensureCapacity(noteArray.size());
		for(int i=0; i<noteArray.size(); i++)
			this.noteArray.add(noteArray.get(i).clone());		
		this.duration = duration;
		this.numOfNotes = this.noteArray.size();
	}

	public long getDuration() {
		this.fitPosition();
		this.duration = this.culcDuration();
		return this.duration;
	}

	public void removeNote(int index) {
		if (index >= 0 && index < this.noteArray.size())
			this.noteArray.remove(index);
		this.numOfNotes = this.noteArray.size();
	}

	public void removeAllNote(){
		for(int i=0; i<noteArray.size(); i++)
			removeNote(i);
			position = 0;
			duration = 0;
			numOfNotes = 0;
	}
	
	public int detectNoteInPositionDuration(long position, long duration, int startIndex){
		int returnIndex = -1;
		//与えられたPositionとDurationが合致する音でstartIndexから初めて一番先頭にある音のインデックスを返す
		//含まれてない場合-1を返す
		if(startIndex >= numOfNotes)
			return returnIndex;
		else{
			for(int i=0; i<numOfNotes; i++){
				if(noteArray.get(i).getDuration() == duration && 
						noteArray.get(i).getPosition() == position)
					returnIndex = i; break;
			}
		}
		return returnIndex;
	}
	
	public void setPosition(long increase) {
		/*
		int until = this.noteArray.size();
		if (until != 0) {
			for (int i = 0; i < until; i++) {
				OneNote tmpNote = (OneNote) this.noteArray.get(i);
				tmpNote.setPosition(tmpNote.getPosition() + increase);
			}
		}
		*/
		for(int i=0; i<noteArray.size(); i++)
			noteArray.get(i).setPosition(noteArray.get(i).getPosition()+increase);
	}
	
	public void setNotePosition(int index, long position){
		noteArray.get(index).setPosition(position);
	}

	public void resetAllNotePositionToZero(){
		for(int i=0; i< noteArray.size(); i++){
			noteArray.get(i).setPosition(0);
		}
	}
	
	private long culcDuration() {
		int until = this.noteArray.size();
		long maxValue = 0;
		for (int i = 0; i < until; i++) {
			OneNote tmpNote = (OneNote) this.noteArray.get(i);
			if (tmpNote.getPosition() < 0)
				System.out.println("culcDuration : " + i + " "
						+ tmpNote.getPosition());
			long tmpMax = tmpNote.getPosition() + tmpNote.getDuration();
			if (tmpMax > maxValue)
				maxValue = tmpMax;
		}
		return maxValue;
	}
}
