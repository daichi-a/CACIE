package CACIE.genome.FunctionNodes;
import CACIE.genome.*;
import java.util.ArrayList;

public class Function_BARFIX34 extends Function_OneArgument{
	public static Notes evl(Notes returnNotes, Notes first, ArrayList<String> configArray){
		  //与えられた音列を無理矢理4/4に落とし込む関数
		  
		  //Duration
		  //sixteenth = 4, eight = 8, quater = 16, half = 32, whole = 64

		  long currentTime = 0;
		  int currentNote = 0;

		  //全体の長さを見る
		  long wholeDurationBeforeFix = first.getDuration();
		  if((wholeDurationBeforeFix % 64) == 0){
			  //既に小節に沿っているので処理しない
			  //そのままreturnNotesにつっこむ
			  returnNotes.addNotes(first);
		  }
		  else{
			  //if(true){
			  ArrayList<Notes> samePosition = TreeOperators.detectNoteOfSamePositions(first);
			  //頭のノートから逐次処理
			  //次の音符を見て、拍子に沿うように変化させる、
			  //自分が拍子の頭にある時、
			  //自分が十六分音符で次が四分以上なら相手を、
			  //自分が八分以上なら自分を変化させる
			  boolean lastNoteProcessed = false;
			  
			  while(currentNote < samePosition.size()){
				  //System.err.println("BARFIX44: now processing. " +
				  //		"numOfNote is " + samePosition.size() + ". " + currentNote + "th note." + 
				  //		"Duration is " + samePosition.get(currentNote).getDuration() + ". " + 
				  //		"CurrentTime is:" + currentTime);
				  if(currentNote == samePosition.size()-1){
					  //最後のノートの場合、小節いっぱいまで伸ばす
					  Notes lastNotes = samePosition.get(currentNote);
					  long emptyDuration = 64 - (currentTime % 64); 
					  for(int i=0; i<lastNotes.getNumOfNotes(); i++)
						  lastNotes.getNote(i).setDuration(emptyDuration);
					  currentTime += emptyDuration;
					  currentNote++;
					  lastNoteProcessed = true;
					  //break;
				  }
				  else if(currentTime % 16 == 0 || currentNote == 0){
					  //４分音符ビートにのってる場合
					  //自分の長さを取ってくる
					  long currentDuration = samePosition.get(currentNote).getDuration();
					  if(currentDuration <= 4){
						  //十六分以下の時
						  
						  //次のノートが四分以上であれば、次のノートの長さを変更して、
						  //currentNoteを装飾音符にする
						  long nextDuration = samePosition.get(currentNote+1).getDuration();
						  if(nextDuration >= 16){
							  long nextDurationFixed = nextDuration - currentDuration;
							  for(int i=0; i<samePosition.get(currentNote+1).getNumOfNotes(); i++)
								  samePosition.get(currentNote+1).getNote(i).setDuration(nextDurationFixed);						  
							  
							  currentNote += 2;
							  currentTime += nextDuration;
						  }
						  else{
							  //次のノートが四分以下だった場合、
							  if(nextDuration == 12){
								  //符点八分の時はそのまま
								  currentNote += 2;
								  currentTime += currentDuration + nextDuration;
							  }
							  else if(nextDuration == 8){
								  //次のノートが八分だった場合、
								  //if(currentNote <= samePosition.size() -2){
								  long nextNextDuration = -1;
								  if(currentNote <samePosition.size() -2)
									  nextNextDuration = samePosition.get(currentNote+2).getDuration();
								  else
									  nextNextDuration = 16;
								  if(nextNextDuration == 4){
									  //もう一つ先(次の次)の音が16だったらそのまま	
									  //(シンコペーション)
									  currentNote += 3;
									  currentTime += currentDuration + nextDuration + nextNextDuration;
								  }
								  else{
									  //それ以外は次の八分を符点八分に変える
									  long nextDurationFixed = 12;
									  for(int i=0; i<samePosition.get(currentNote+1).getNumOfNotes(); i++)
										  samePosition.get(currentNote+1).getNote(i).setDuration(nextDurationFixed);	 
									  currentNote += 2;
									  currentTime += currentDuration + nextDurationFixed;
								  }
							  }
							  else{
								  //次の音も16分だった場合
								  //そのまま次へ(16分連打で八分化)
								  currentNote += 2;
								  currentTime += currentDuration + nextDuration;
							  }  
						  }
					  }		
					  else{
						  //16分より大きかった場合
						  currentNote++;
						  currentTime += currentDuration;
					  }
				  }
				  else if (currentTime % 8 == 0){
					  //４分音符拍子ポジションにない時
					  //8分の上にのってる時
					  if(samePosition.get(currentNote).getDuration() == 4){
						  //現在が16だった場合
						  //次が16でなかったら、自分を8に伸ばす
						  if(!(samePosition.get(currentNote+1).getDuration() == 4)){
							  for(int i=0; i<samePosition.get(currentNote).getNumOfNotes(); i++)
								  samePosition.get(currentNote).getNote(i).setDuration(8);
							  currentNote++;
							  currentTime += 8;
						  }
						  else{
							  currentTime += samePosition.get(currentNote).getDuration();
							  currentNote++;
						  }
					  }
					  else if(samePosition.get(currentNote).getDuration() > 16){
						  //４分より大きい場合
						  //この八分音符の分だけ長さを増やして、次の音が拍子に入るようにする
						  long currentDuration = samePosition.get(currentNote).getDuration();
						  for(int i=0; i<samePosition.get(currentNote).getNumOfNotes(); i++)
							  samePosition.get(currentNote).getNote(i).setDuration(8+currentDuration);
						  currentNote++;
						  currentTime += (8+currentDuration);
					  }
					  else{
						  currentTime+=samePosition.get(currentNote).getDuration();
						  currentNote++;
					  }
				  }
				  else if (currentTime % 4 == 0){
					  //16分の上にのっている時
					  if(samePosition.get(currentNote).getDuration() > 16){
						//この音が４分より大きい場合，この１６分の分だけ長さを増やして次の音が拍子に入るようにする
						long currentDuration = samePosition.get(currentNote).getDuration();
						for(int i=0; i<samePosition.get(currentNote).getNumOfNotes(); i++)
							samePosition.get(currentNote).getNote(i).setDuration(4+currentDuration);
						currentNote++;
						currentTime += (4+currentDuration);
					  }
					  else{
						  currentTime+=samePosition.get(currentNote).getDuration();
						  currentNote++;
					  }
				  }
				  else{
					  //そのどれでもないとき
					  currentNote++;
					  currentTime += samePosition.get(currentNote).getDuration();
				  }
				  
				  //現在プロセスした最後の音が小節をまたぐようなら，音を短くする
				  //つまり次の音を必ず小節の頭に持ってくる
				  long tmpLastNoteDuration = samePosition.get(currentNote-1).getDuration();
				  long lastNoteOnsetTime = currentTime - tmpLastNoteDuration;
				  long lastNoteOnsetTimeInTheBar = lastNoteOnsetTime % 64;
				  
				  if(lastNoteOnsetTimeInTheBar+tmpLastNoteDuration > 64){
					  long surplusTime = lastNoteOnsetTimeInTheBar+tmpLastNoteDuration - 64;
					  long correctedLastNoteDuration = tmpLastNoteDuration - surplusTime;
					  for(int i=0; i<samePosition.get(currentNote-1).getNumOfNotes(); i++)
							samePosition.get(currentNote-1).getNote(i).setDuration(correctedLastNoteDuration);
					  currentTime -= surplusTime;
				  }
				  
				  
				  
				  
			  }//End of While
			  
			  //Whileループが終った後の最後の音の処理．最後の音が伸ばされていない時は小節いっぱいまで伸ばす
			  if(!lastNoteProcessed){
				  if((currentTime % 64) != 0){
				  Notes lastNotes = samePosition.get(samePosition.size()-1);
				  long tmpLastNoteDuration = lastNotes.getNote(0).getDuration();
				  long tmpBeforeLastNoteTime = currentTime - tmpLastNoteDuration;
				  long emptyDuration = 64 - (tmpBeforeLastNoteTime % 64);
				  
				  //long emptyDuration = 64 - ((currentTime - tmpLastNoteDuration) % 64);
				  //long emptyDuration = 64 - (currentTime % 64);
				  for(int i=0; i<lastNotes.getNumOfNotes(); i++)
					  lastNotes.getNote(i).setDuration(emptyDuration);
				  currentTime = (currentTime - tmpLastNoteDuration + emptyDuration);	
				  }
			  }
			  //チェック currentTimeがちゃんと小節を満たしているか
			  if(currentTime % 64 != 0){
				  System.err.println("TreeOperators:BarFix:currentTime of end note is not bar's end: " 
						  + currentTime + "," + lastNoteProcessed + "," + 
						  samePosition.get(samePosition.size()-1).getDuration() +","+
						  samePosition.get(samePosition.size()-2).getDuration());
				  System.exit(1);
			  }
				  
			  //returnNotesにつっこんでいく
			  currentTime = 0;
			  for(int i=0;i<samePosition.size(); i++){
				  //System.err.println("CurrentTime is " + currentTime);
				  Notes currentNotes = samePosition.get(i);
				  //currentNotes.resetAllNotePositionToZero();
				  //currentNotes.setPosition(currentTime);
				  //returnNotes.addNotes(currentNotes);
				  for(int j=0; j<currentNotes.getNumOfNotes(); j++){
					  OneNote note = currentNotes.getNote(j);
					  OneNote newNote = new OneNote(note.getNoteNumber(), note.getVelocity(), currentTime, (int)currentNotes.getDuration());
					  returnNotes.addNote(newNote);
					  //System.err.print("(P:"+newNote.getPosition()+",D:"+newNote.getDuration()+")");
				  }
				  currentTime += currentNotes.getDuration();
			  }
			  //System.err.println();
			  returnNotes.fitParameters();
		  }
		  return returnNotes;
	}
}
