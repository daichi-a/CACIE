package CACIE.ui.GLSphereGUI;

import java.util.ArrayList;

import CACIE.ui.OperationWindows_GUI;

public class OperationWindows_GUI_GLSphere extends OperationWindows_GUI {

	public OperationWindows_GUI_GLSphere(int populationSize, ArrayList notes,
			ArrayList oprList, ArrayList confList) {
		super(populationSize, notes, oprList, confList);
		// TODO Auto-generated constructor stub
	}

	public OperationWindows_GUI_GLSphere() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new GraphicalPopulationPresenterEvaluator_GLSphere(this);
	}

}
