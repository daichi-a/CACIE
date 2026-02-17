package CACIE.genome.FunctionNodes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.StringTokenizer;

import CACIE.genome.Notes;
import CACIE.genome.OneNote;

public class Function_HARMONIZE extends Function_OneArgument {
	//小節ごとに和声付けをするクラス
	//その小節の中での音の頻度をもとに和声付けを行う
	
	public static Notes evl(Notes returnNotes, Notes first, ArrayList<String> configArray){
		//Coding中のテスト用
		returnNotes.addNotes(first);
		
		//configArrayからスケールに関する情報を持ってくる．
		String baseNote = extractBaseScaleNote(configArray);
		
		//どの音符の間に小節線があるか検出
		ArrayList<Integer> indexesOfBarPosition = detectBarPosition(first);
		int numOfBar = indexesOfBarPosition.size() -1;
		//System.err.println("NumOfBar is " + numOfBar);
		int counter = 0;
		Notes harmonyPart = new Notes();
		while(counter < numOfBar){
			ArrayList<Integer> popularNotes = 
				detectPopularNotesInABar(first, 
						indexesOfBarPosition.get(counter).intValue(), 
						indexesOfBarPosition.get(counter+1).intValue(),
						configArray);
			/*
			if(alreadyHarmonized(first, indexesOfBarPosition.get(counter), 
					indexesOfBarPosition.get(counter+1), 64 * counter)){
				//その小節の頭に４音以上なっていたら、その小節はパスする
				counter++;
				continue;
			}*/

				Notes tmpHarmonyPart = makeHarmony(popularNotes, baseNote);
				tmpHarmonyPart.setPosition(64*counter);
				harmonyPart.addNotes(tmpHarmonyPart);
				//returnNotes.addNotes(harmonyPart);
				counter++;

		}
		makeHarmonyRhythms(harmonyPart);
		returnNotes.addNotes(harmonyPart);
		return returnNotes;	
	}
	
	static void makeHarmonyRhythms(Notes harmonyPart){

	}
	
	static boolean alreadyHarmonized(Notes notes, int startIndex, int endIndex, int startPosition){
		//小節の中に冒頭で発音するノートが4つ以上あるとき、既に和声付けされていると考える。
		boolean harmonized = false;
		long positionArray[] = new long[endIndex-startIndex];
		int counter = 0;
		for(int i=startIndex; i<endIndex; i++){
			positionArray[counter] = notes.getNote(i).getPosition();
		}
		counter = 0;
		for(int i=0; i<endIndex-startIndex; i++)
			if(positionArray[i] == startPosition)
				counter++;
		if(counter >= 3)
			harmonized = true;
			
		return harmonized;
	}
	
	static Notes makeHarmony(ArrayList<Integer> popularNoteArray, String baseNote){
		Notes harmonyNotes = new Notes();
		//ベースノートのチェック
		if(!baseNote.equals("C")){
			System.err.println("Function_HARMONIZE: this function currently implemented for only C Diatonic Scale. Exiting.");
			System.exit(1);
		}
		
		//３番目まで見て和音を構成
		if(popularNoteArray.size() < 3){
			//三つより小さかったら，
			if(popularNoteArray.size() == 1){
				//一つのとき
				//頻出ノートのドミナント(1オクターブと4度下)
				int noteNumberInDiatonic = popularNoteArray.get(0);
				OneNote harmonyNote = 
					new OneNote(noteNumberInDiatonic +21-3, 80, 0, 64);
				harmonyNotes.addNote(harmonyNote);
			}
			else{
				//二つのとき
				//頻出ノートとそのドミナントで二和音を構成
				int noteNumberInDiatonic = popularNoteArray.get(0);
				OneNote harmonyNote1 = 
					new OneNote(noteNumberInDiatonic+21, 70, 0, 64);
				OneNote harmonyNote2 = 
					new OneNote(noteNumberInDiatonic+21-3, 70, 0, 64);
				harmonyNotes.addNote(harmonyNote1);
				harmonyNotes.addNote(harmonyNote2);
			}
			
			
			
		}
		else{
			//音が３つより多いとき
			//頻出ノートとそのドミナントかサブドミナントのどちらが多いかで決める
			//ドミナントが多いとき，頻出ノートをルートにした３和音
			//サブドミナントが多いとき，サブドミナントを基準とした3和音
			//7の倍数がCなので，notenumber % 7で出せる
			//C=0, D=1, E=2, F=3, G=4, A=5, H=6
			int mostPopularNoteNumber = popularNoteArray.get(0).intValue();
			int numOfDominant = 0;
			int numOfSubDominant = 0;
			for(int i=1; i<popularNoteArray.size(); i++){
				if(popularNoteArray.get(i).intValue() == (mostPopularNoteNumber + 4) % 7)
					numOfDominant++;
				else if(popularNoteArray.get(i).intValue()  == (mostPopularNoteNumber + 3) % 7)
					numOfSubDominant++;
			}
			if(numOfDominant > numOfSubDominant){
				//再頻出を基準とした3和音  普通の左手の位置になるように+21する
				OneNote root = new OneNote(mostPopularNoteNumber+21, 60, 0, 64);
				OneNote third = new OneNote(mostPopularNoteNumber+21+2, 60, 0, 64);
				OneNote fifth = new OneNote(mostPopularNoteNumber+21+4, 60, 0, 64);
				harmonyNotes.addNote(root);
				harmonyNotes.addNote(third);
				harmonyNotes.addNote(fifth);
			}
			else if(numOfDominant < numOfSubDominant){
				//再頻出のサブドミナントを基準とした３和音 +21する．ただし転回
				OneNote root = new OneNote(mostPopularNoteNumber+21+3, 60, 0, 64);
				OneNote third = new OneNote(mostPopularNoteNumber+21+5, 60, 0, 64);
				OneNote fifth = new OneNote(mostPopularNoteNumber+21, 60, 0, 64);
				harmonyNotes.addNote(root);
				harmonyNotes.addNote(third);
				harmonyNotes.addNote(fifth);
			}
			else{
				//どっちも0だったりどっちも同じ数だったり……
				//再頻出とそのドミナントの２和音
				OneNote root = new OneNote(mostPopularNoteNumber+21, 70, 0, 64);
				OneNote fifth = new OneNote(mostPopularNoteNumber+21+5, 70, 0, 64);
				harmonyNotes.addNote(root);
				harmonyNotes.addNote(fifth);
			}
		}
		//System.err.println("Add " + harmonyNotes.getNumOfNotes() + "notes added as harmony.");
		return harmonyNotes;
	}
	
	static String extractBaseScaleNote(ArrayList<String> configArray){
		String baseNote = "NULL";
		int lineCounter = 0;
		String scaleName = "NULL";
		boolean scaleSwitch = false;
		while(lineCounter < configArray.size()){
			String configLine = configArray.get(lineCounter);
			StringTokenizer st = new StringTokenizer(configLine);
			String command = st.nextToken();
			if(command.equals("SCALE_FILTER")){
				if(st.nextToken().equals("ON")){
					scaleName = st.nextToken();
					baseNote = st.nextToken();
					scaleSwitch = true;
					//System.err.println(scaleName + baseNote);
					break;
				}
			}
			lineCounter++;
		}
		//エラー処理
		if(!scaleName.equals("DIATONIC") || !scaleSwitch){
			System.err.println("Function_HARMONIZE requires a config line about SCALE_FILTER.");
			System.err.println("e.g. CONFIG: SCALE_FILTER ON DIATONIC C \\e");
			System.exit(1);
		}
		
		return baseNote;
	}
	
	static ArrayList<Integer> detectPopularNotesInABar(Notes notes, int barStartIndex, int barEndIndex,
			ArrayList<String> configArray){
		//その小節に登場する音を頻出順に格納したもの
		ArrayList<Integer> popularNotes = new ArrayList<Integer>();
		
		Hashtable<String,Integer> countTable = new Hashtable<String, Integer>();
		//いくつ含まれるかを数えていく
		//スケールに沿うように%7する
		for(int i=barStartIndex; i<barEndIndex; i++){
			String noteNumberInString = Integer.toString(notes.getNote(i).getNoteNumber()%7);
			if(!countTable.contains(noteNumberInString))
				countTable.put(noteNumberInString, new Integer(1));
			else{
				int currentCount = countTable.get(noteNumberInString).intValue();
				countTable.remove(noteNumberInString);
				countTable.put(noteNumberInString, new Integer(currentCount+1));
			}	
		}
		
		//並び替えてArrayListに出力
		for(Enumeration<String> e = countTable.keys(); e.hasMoreElements();){
			String currentNoteNumber = e.nextElement();
			int currentCountValue = countTable.get(currentNoteNumber);
			if(popularNotes.size() == 0) //最初のとき
				popularNotes.add(new Integer(Integer.parseInt(currentNoteNumber)));
			else{
				//次からは比較して突っ込む
				int counter = 0;
				while(counter < popularNotes.size()){
					if(counter >= popularNotes.size() -1){ //一番値が小さい．最後に突っ込む
						popularNotes.add(new Integer(Integer.parseInt(currentNoteNumber)));
						break;
					}
					else{
						//ソートしながら突っ込む
						Integer targetNoteNumber = popularNotes.get(counter);
						int targetCountValue = 
							countTable.get(targetNoteNumber.toString()).intValue(); 
						if(currentCountValue > targetCountValue){
							popularNotes.add(counter, new Integer(Integer.parseInt(currentNoteNumber)));
							break;
						}
					}
					counter++;
				}
			}
		}
		
		//チェック
		if(popularNotes.size() != countTable.size()){
			System.err.println("Function_HARMONIZE:detectPopularNotesInABar: faild to make popular note number talbe");
			System.exit(1);
		}
		
		return popularNotes;
	}
	
	static ArrayList<Integer> detectBarPosition(Notes notes){
		//どの音とどの音の間に小節線があるか
		ArrayList<Integer> indexesOfBarPosition = new ArrayList<Integer>();
		int counter = 0;
		while(counter < notes.getNumOfNotes()){
			OneNote currentNote = notes.getNote(counter);
			//System.err.print(currentNote.getPosition() + " ");
			if(currentNote.getPosition() % 64 == 0){
				//丁度OnsetTimeが小節頭の時
				indexesOfBarPosition.add(new Integer(counter));
				counter++;
			}
			else if(currentNote.getPosition() % 64 < 64 && 
					currentNote.getPosition() % 64 + currentNote.getDuration() > 64){
				//音が小節線をまたいだとき //BARFIXの規定ではないはずなのだが……
				indexesOfBarPosition.add(new Integer(counter));
				counter++;
			}
			else if(currentNote.getPosition() % 64 < 64)
				counter++;
			
			else
				counter++;
		}
		//仮想の最後の音を仮定して，そのインデックスを最後につける
		indexesOfBarPosition.add(new Integer(counter));
		//System.err.println("indexesOfBarPosition.size is " + indexesOfBarPosition.size());
		//かぶっている部分がないか処理
		//かぶっていたら後方(つまりOnsetTimeがジャストの方を優先)
		counter = 1;
		int currentBarPositionIndex = 0;
		while(counter < indexesOfBarPosition.size()){
			if(indexesOfBarPosition.get(counter).intValue() == currentBarPositionIndex &&
					counter != 1){
				indexesOfBarPosition.remove(counter-1);
			}
			else
				counter++;
			currentBarPositionIndex = indexesOfBarPosition.get(counter-1).intValue();
		}
		
		return indexesOfBarPosition;
	}
}
