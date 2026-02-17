package CACIE.ui;

import java.util.ArrayList;

import CACIE.ui.sphereGUI.*;

public class OperationWindows_GUI_DD5 extends OperationWindows_GUI {

	public OperationWindows_GUI_DD5(int populationSize,
			ArrayList notes, ArrayList oprList, ArrayList confList) {
		super(populationSize, notes, oprList, confList);
	}

	public OperationWindows_GUI_DD5() {
		super();
	}

	
	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new SphereGUI(this);
	}
}
