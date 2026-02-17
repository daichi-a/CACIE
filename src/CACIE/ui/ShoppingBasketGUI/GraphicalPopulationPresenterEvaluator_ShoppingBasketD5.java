package CACIE.ui.ShoppingBasketGUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import CACIE.ui.OperationWindows_GUI;

public class GraphicalPopulationPresenterEvaluator_ShoppingBasketD5 extends
		GraphicalPopulationPresenterEvaluator_ShoppingBasket {

	public GraphicalPopulationPresenterEvaluator_ShoppingBasketD5(
			OperationWindows_GUI operationWindows) {
		super(operationWindows);
		// TODO Auto-generated constructor stub
	}

	public void setupGUI(){
		topFrame = new JFrame("Population Display");
		topFrame.setLayout(new BorderLayout());
		
		shoppingBasket = new ShoppingBasketD5_IEC();
		shoppingBasket.setGraphicalPopulationPresenterEvaluator(this);
		shoppingBasket.setNumOfIndividual(opwin.populationSize);
		
				
		topFrame.add(shoppingBasket, BorderLayout.CENTER);
		topFrame.add(controlButtons, BorderLayout.SOUTH);
		topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		topFrame.setSize(800, 768);

		topFrame.setVisible(true);
	}
	
}
