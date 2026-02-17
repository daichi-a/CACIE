package CACIE.genome;

import CACIE.eventlist.CommonEventList;

public abstract class Abstract_Individual {

	public static int MAXNOTE = 12;

	public static int MINNOTE = 3;

	public static int tickBase = 16;

	public static int PROB_MUT_STR = 30;

	public static int MAX_VALUE_PITCH = 127;

	public static int MIN_VALUE_PITCH = 0;

	public static int MAX_VALUE_VELOCITY = 127;

	public static int MIN_VALUE_VELOCITY = 40;

	public static int MAX_DULATION = tickBase * 4;

	public static int MIN_DULLATION = tickBase / 4;

	public static int BASE_PITCH = 60;

	public static int BASE_VELOCITY = 80;

	int IDNumber;

	abstract public void generate();

	abstract public void makeEmptyGenome(int size);

	abstract public CommonEventList convertToEventList();

	abstract public void convertFromEventList(CommonEventList eventList);

	abstract public String getGenomeString();

	abstract public int getNumOfNotes();

	public CommonEventList getEventList() {
		CommonEventList tmpEventList = convertToEventList();
		return tmpEventList;
	}

	public void setEventList(CommonEventList eventList) {
		this.convertFromEventList(eventList);
	}

	abstract public Abstract_Individual clone();
	
}
