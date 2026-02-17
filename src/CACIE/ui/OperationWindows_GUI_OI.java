package CACIE.ui;

import java.util.ArrayList;

public class OperationWindows_GUI_OI extends OperationWindows_GUI_Traditional {

	public OperationWindows_GUI_OI(int populationSize,
			ArrayList notes, ArrayList oprList, ArrayList confList) {
		super(populationSize, notes, oprList, confList);
	}
	
	public OperationWindows_GUI_OI(){
		super();
	}
	
	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new GraphicalPopulationPresenterEvaluator_OI(this);
	}

}
