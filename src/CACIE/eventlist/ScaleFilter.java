package CACIE.eventlist;

public class ScaleFilter {

	public static int doFiltering(int inputNoteNumber, String scaleName, String key){
		int returnNoteNumber = 0;
		if(scaleName.equals("DIATONIC"))
			returnNoteNumber = convertToDiatonicScale(inputNoteNumber, key);
		return returnNoteNumber;
		
	}
	
	public static int convertToDiatonicScale(int inputNoteNumber, String key){
	
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
			//Add key offset
			if(key.equals("C+") || key.equals("D-"))
				returnNumber += 1;
			else if(key.equals("D"))
				returnNumber += 2;
			else if(key.equals("D+") || key.equals("E-"))
				returnNumber += 3;
		}
		return returnNumber;
		
	}
}
