package CACIE.genome.FunctionNodes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;

import CACIE.genome.Notes;
import CACIE.genome.OneNote;

public class Function_BOSSAHARMONIZE extends Function_HARMONIZE {
	//小節ごとに和声付けをするクラスの拡張で、Bossa Novaっぽいリズムにする
	//その小節の中での音の頻度をもとに和声付けを行う
	
	static void makeHarmonyRhythm(Notes harmonyPart){
		long position = harmonyPart.getPosition();
		long duration = harmonyPart.getDuration();
		Notes bossaHarmonyPart = new Notes();

		//小節ごとに分解
		ArrayList<Notes> oneBars = new ArrayList<Notes>((int)duration/64);
		int barCounter = 0;
		for(int i=0; i<oneBars.size(); i++){
			barCounter = i;
			int currentIndex = 0;
			Notes tmpHarmony = new Notes();
			//その小節のハーモニーパートとして与えられた音符を抽出
			int detectedIndex = 0;
			while(true){
				detectedIndex = 
					harmonyPart.detectNoteInPositionDuration(barCounter * 64, 64, currentIndex);
				if(detectedIndex < 0)
					break;		
				tmpHarmony.addNote(harmonyPart.getNote(detectedIndex));
				currentIndex = detectedIndex+1;
			}
			//ピッチ情報のみを使用
			//ピッチ情報を抽出
			OneNote[] sortedNotes = sortFromRoot(tmpHarmony);
			Notes bossaHarmonyInBar = new Notes();

			if(barCounter % 2 == 0){
				//奇数小節(0から数えると偶数)のとき
				//Rootノード
				int rootNotePitch = sortedNotes[0].getNoteNumber();
				OneNote rootFirst = new OneNote(rootNotePitch, 100, 0, 24);
				OneNote rootSecond = new OneNote(rootNotePitch, 100, 24, 40);
				bossaHarmonyInBar.addNote(rootFirst);
				bossaHarmonyInBar.addNote(rootSecond);
				
				//その他
				for(int j=1; j<sortedNotes.length; j++){
					int pitch = sortedNotes[j].getNoteNumber();
					OneNote first = new OneNote(pitch, 60, 16, 16);
					OneNote second = new OneNote(pitch, 80, 40, 16);
					OneNote third = new OneNote(pitch, 80, 56, 8);
					bossaHarmonyInBar.addNote(first);
					bossaHarmonyInBar.addNote(second);
					bossaHarmonyInBar.addNote(third);
				}
			}
			else{
				//偶数小節のとき
				//Root
				int rootNotePitch = sortedNotes[0].getNoteNumber();
				OneNote rootFirst = new OneNote(rootNotePitch, 100, 0, 32);
				OneNote rootSecond = new OneNote(rootNotePitch, 100, 32, 32);
				bossaHarmonyInBar.addNote(rootFirst);
				bossaHarmonyInBar.addNote(rootSecond);
				//その他
				for(int j=1; j<sortedNotes.length; j++){
					int pitch = sortedNotes[j].getNoteNumber();
					OneNote first = new OneNote(pitch, 80, 8, 16);
					OneNote second = new OneNote(pitch, 60, 24, 8);
					OneNote third = new OneNote(pitch, 80, 40, 24);
					bossaHarmonyInBar.addNote(first);
					bossaHarmonyInBar.addNote(second);
					bossaHarmonyInBar.addNote(third);
				}	                                
			}
			bossaHarmonyInBar.setPosition(64 * barCounter);
			ArrayList<OneNote> notes = bossaHarmonyInBar.getNoteArray();
			for(int f=0; f<bossaHarmonyInBar.getNumOfNotes(); f++){
				OneNote aNote = notes.get(f);
				System.err.println(aNote.getNoteNumber() + "," + aNote.getPosition() + "," + aNote.getDuration());
			}
			bossaHarmonyPart.addNotes(bossaHarmonyInBar);
		}
		
	}

	static OneNote[] sortFromRoot(Notes harmonyPart){
		ArrayList<OneNote> noteArray = harmonyPart.getNoteArray();
		//Object[] harmonyNotes = noteArray.toArray();
		OneNote[] harmonyNotes = new OneNote[noteArray.size()];
		for(int i=0; i<noteArray.size(); i++)
			harmonyNotes[i] = noteArray.get(i);
		java.util.Arrays.sort(harmonyNotes, new NoteNumberComparator());
		return harmonyNotes;
	}
}

class NoteNumberComparator implements java.util.Comparator<OneNote>{
	public int compare(OneNote note1, OneNote note2){
		//o1がピッチが低い時には	正の整数
		//o2が低い時には負の整数
		//同じ時は0を返す
		return note2.getNoteNumber() - note1.getNoteNumber();
	}
}	

