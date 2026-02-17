package CACIE.ui;

import java.util.ArrayList;

public class OperationWindows_GUI_OID5 extends OperationWindows_GUI_Traditional {

	public OperationWindows_GUI_OID5(int populationSize,
			ArrayList notes, ArrayList oprList, ArrayList confList) {
		super(populationSize, notes, oprList, confList);
	}
	
	public OperationWindows_GUI_OID5(){
		super();
	}
	
	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new GraphicalPopulationPresenterEvaluator_OID5(this);
	}

}
