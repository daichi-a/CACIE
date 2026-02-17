package CACIE.genome.FunctionNodes;

import CACIE.genome.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Function_CHORDFIX extends Function_OneArgument {

	public static Notes evl(Notes returnNotes, Notes first, ArrayList<String> configArray){
		ArrayList<String> codeProgress = extractChordFilterProgress(configArray);
		if(codeProgress.get(0).equals("NULL")){
			//スイッチがオフの時
			//そのまま通過させる
			returnNotes.addNotes(first);
			return returnNotes;
		}

		
		
		
		
		return returnNotes;
	}
	
	protected static ArrayList<String> extractChordFilterProgress(ArrayList<String >configArray){
		//Extract configs for chromosome initialize
		ArrayList<String> codeProgress = new ArrayList<String>();
		codeProgress.add("NULL");
		int lineCounter = 0;
		boolean chordFilter = false;
		while(lineCounter < configArray.size()){
			String configLine = configArray.get(lineCounter);
			StringTokenizer st = new StringTokenizer(configLine);
			String command = st.nextToken();
			if(command.equals("CODE_FILTER")){
				String codeFilterSwitch = st.nextToken();
				if(codeFilterSwitch.equals("ON")){
					chordFilter = true;
					
					
					
				}
				else
					break;
			}
			lineCounter++;
		}
		
		return codeProgress;
	}
}
