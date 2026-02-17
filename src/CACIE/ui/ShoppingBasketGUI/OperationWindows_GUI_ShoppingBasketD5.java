package CACIE.ui.ShoppingBasketGUI;

import java.util.ArrayList;
import CACIE.genome.Notes;

public class OperationWindows_GUI_ShoppingBasketD5 extends
		OperationWindows_GUI_ShoppingBasket {
	
	public OperationWindows_GUI_ShoppingBasketD5(int populationSize,
			ArrayList<Notes> notes, ArrayList<String> oprList, ArrayList<String> confList) {
		super(populationSize, notes, oprList, confList);
	}

	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new GraphicalPopulationPresenterEvaluator_ShoppingBasketD5(this);
	}
	
}
