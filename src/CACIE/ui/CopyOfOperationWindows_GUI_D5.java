package CACIE.ui;

import java.util.ArrayList;


public class CopyOfOperationWindows_GUI_D5 extends OperationWindows_GUI {

	public CopyOfOperationWindows_GUI_D5(int populationSize,
			ArrayList notes, ArrayList oprList, ArrayList confList) {
		super(populationSize, notes, oprList, confList);
		/*
		RepresentationType = Configs.SIMPLETREE_MOTIF;
	    operationList = oprList;
	    notes = notes;

	    populationSize = populationSize;
	    thePopulation = new Population(populationSize, RepresentationType, 0, notes,
		oprList, confList);

	    setUpGUI();
	    generateNewPopulationDisplay();
	    
	    //Initialize Population and EventList
		thePopulation.initPopulation();
		cashEvThis = new ArrayList<CommonEventList>(populationSize);
		cashEvNext = new ArrayList<CommonEventList>(populationSize);
		for (int i = 0; i < populationSize; i++){
			cashEvThis.add(thePopulation.convertToEventList(0, i));
		}
		setListThisGeneration(cashEvThis);	  

	    generateGenomeStorageWindow();
		
	    // tree edit panel
	    //this.initTreeEditPanel();
	    topFrame.setVisible(true);
	    topFrame.pack();
	    generateTreeEditWindow();
	    */
	}

	public CopyOfOperationWindows_GUI_D5() {
		super();
	}

	
	protected void generateNewPopulationDisplay(){
		//Population Presentation Window
		populationDisplay = new GraphicalPopulationPresenterEvaluator_Traditional(this);
	}
}
