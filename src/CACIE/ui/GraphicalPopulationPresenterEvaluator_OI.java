package CACIE.ui;

import CACIE.eventlist.CommonEventList;
import javax.swing.JPanel;

public class GraphicalPopulationPresenterEvaluator_OI extends
		GraphicalPopulationPresenterEvaluator_Traditional {
	
	protected static int windowSizeX = 400, windowSizeY = 600;
	protected static int scrollPaneSizeX = 300, scrollPaneSizeY = 600;
	
	public GraphicalPopulationPresenterEvaluator_OI(OperationWindows operationWindows){
		super(operationWindows);
	}
	
	protected void generateEvaluatingIndividual(JPanel listPanel){
		for (int i = 0; i < opwin.populationSize; i++){
			EvaluatingIndividual tmpEvInd = new EvaluatingIndividual_OI(new CommonEventList(0), opwin, i);
			listPanel.add(tmpEvInd);
			DistInds.add(tmpEvInd);
		}
		System.err.println("Size of DistInds is:" + opwin.DistInds.size()+ ", PopulationSize is: " + opwin.populationSize);
	}


}
