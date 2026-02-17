package CACIE.ui.ShoppingBasketGUI;

import CACIE.eventlist.CommonEventList;
import CACIE.ui.*;

import java.awt.BorderLayout;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;



public class GraphicalPopulationPresenterEvaluator_ShoppingBasket extends
		GraphicalPopulationPresenterEvaluator {
	
	int[] fitnessValues;
	CommonEventList[] eventLists, nextGenerationEventLists;
	ShoppingBasket_IEC shoppingBasket;
	
	public GraphicalPopulationPresenterEvaluator_ShoppingBasket
		(OperationWindows operationWindows){
		super(operationWindows);
		setupGUI();
		preInitialize();
		afterInitialize();
		fitnessValues = new int[opwin.populationSize];
		eventLists = new CommonEventList[opwin.populationSize];
		nextGenerationEventLists = new CommonEventList[opwin.populationSize];
		
	}
	
	public void setupGUI(){
		topFrame = new JFrame("Population Display");
		topFrame.setLayout(new BorderLayout());
		
		shoppingBasket = new ShoppingBasket_IEC();
		shoppingBasket.setGraphicalPopulationPresenterEvaluator(this);
		shoppingBasket.setNumOfIndividual(opwin.populationSize);

		topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		topFrame.setSize(800, 768);
		topFrame.add(controlButtons, BorderLayout.SOUTH);
		topFrame.add(shoppingBasket, BorderLayout.CENTER);

		
		topFrame.remove(shoppingBasket);
		topFrame.add(shoppingBasket, BorderLayout.CENTER);
		//topFrame.setVisible(true);
		shoppingBasket.init();
	}

	@Override
	public void afterInitialize() {
		// TODO Auto-generated method stub
		topFrame.setVisible(true);
		shoppingBasket.init();
	}

	@Override
	public void afterReproduce() {
		// TODO Auto-generated method stub
		//System.err.println("GraphicalPopulationPresenterEvaluator_SB: afterReproduce()");
		//shoppingBasket.init();
		topFrame.remove(shoppingBasket);
		shoppingBasket = new ShoppingBasket_IEC();
		shoppingBasket.setGraphicalPopulationPresenterEvaluator(this);
		shoppingBasket.setNumOfIndividual(opwin.populationSize);
		topFrame.add(shoppingBasket,BorderLayout.CENTER);
		topFrame.setVisible(true);
		shoppingBasket.init();
	}

	@Override
	public void closingProcessingAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFitnessValue(int index) {
		// TODO Auto-generated method stub
		return shoppingBasket.getFitnessValue(index);
	}

	
	@Override
	public void openingProcessingAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preInitialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preReproduce() {
		// TODO Auto-generated method stub

	}

	
	@Override
	public void setEventList(int index, CommonEventList eventList) {
		// TODO Auto-generated method stub
		eventLists[index] = eventList;
	}

	@Override
	public void setFitnessValue(int index, int position) {
		// TODO Auto-generated method stub

	}
	
	public void playAsMIDISequence(int index){
		try {
			eventLists[index].playAsMIDISequence(opwin.getTempo());
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopAll(){
		opwin.stopAll();
	}
	
	public boolean getPlayingState(int index){
		return eventLists[index].getPlayingState();
	}

	public double[][] getDistances(){
		return opwin.getDistances();
	}
}
