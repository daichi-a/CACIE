package CACIE.ui;

import javax.swing.JPanel;

import CACIE.eventlist.CommonEventList;

public class GraphicalPopulationPresenterEvaluator_OID5 extends
		GraphicalPopulationPresenterEvaluator_OI {

	public GraphicalPopulationPresenterEvaluator_OID5(
			OperationWindows operationWindows) {
		super(operationWindows);
		// TODO Auto-generated constructor stub
	}
	
	protected void generateEvaluatingIndividual(JPanel listPanel){
		for (int i = 0; i < opwin.populationSize; i++){
			EvaluatingIndividual tmpEvInd = new EvaluatingIndividual_OID5(new CommonEventList(0), opwin, i);
			listPanel.add(tmpEvInd);
			DistInds.add(tmpEvInd);
		}
		System.err.println("Size of DistInds is:" + opwin.DistInds.size()+ ", PopulationSize is: " + opwin.populationSize);
	}
}
