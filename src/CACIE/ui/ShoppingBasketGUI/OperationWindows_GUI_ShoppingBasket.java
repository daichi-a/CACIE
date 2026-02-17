package CACIE.ui.ShoppingBasketGUI;

import java.util.ArrayList;

//import CACIE.ui.GraphicalPopulationPresenterEvaluator;
import CACIE.ui.OperationWindows_GUI;
import CACIE.eventlist.CommonEventList;
import CACIE.genome.Notes;

public class OperationWindows_GUI_ShoppingBasket extends OperationWindows_GUI {

	public OperationWindows_GUI_ShoppingBasket(int populationSize,
			ArrayList<Notes> notes, ArrayList<String> oprList, ArrayList<String> confList) {
		super(populationSize, notes, oprList, confList);
	}
	
	public OperationWindows_GUI_ShoppingBasket(){
		super();
	}
	
	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new GraphicalPopulationPresenterEvaluator_ShoppingBasket(this);
	}
	
	public void setListThisGeneration(ArrayList eventlistSet){
		for(int i=0; i<populationSize; i++)
			populationDisplay.setEventList(i,(CommonEventList)eventlistSet.get(i));
		
	}
	
	public void setListNextGeneration(ArrayList eventlistSet){
		for(int i=0;i<populationSize; i++){
			CommonEventList eventlist = (CommonEventList)eventlistSet.get(i);
			populationDisplay.setEventList(i,eventlist);
		}
	}	
}
