package CACIE.ui.GLSphereGUI;

import java.awt.BorderLayout;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;

import CACIE.eventlist.CommonEventList;
import CACIE.ui.GraphicalPopulationPresenterEvaluator;
import CACIE.ui.OperationWindows;
import CACIE.ui.ShoppingBasketGUI.*;
import CACIE.ui.GLSphereGUI.*;

import javax.media.opengl.*;

public class GraphicalPopulationPresenterEvaluator_GLSphere extends
		GraphicalPopulationPresenterEvaluator_ShoppingBasket {

	GLSphere glSphere;
	int[] fitnessValues;
	CommonEventList[] eventLists, nextGenerationEventLists;
	
	public GraphicalPopulationPresenterEvaluator_GLSphere(
			OperationWindows operationWindows) {
		super(operationWindows);
		fitnessValues = new int[opwin.populationSize];
		eventLists = new CommonEventList[opwin.populationSize];
		nextGenerationEventLists = new CommonEventList[opwin.populationSize];
	}

	public void setupGUI(){
		topFrame = new JFrame("Population Display");
		topFrame.setLayout(new BorderLayout());
		
		//GLCapabilities glCapabilities = new GLCapabilities();
		
		//glSphere = new GLSphere(glCapabilities);
		glSphere = new GLSphere();
		
		glSphere.setGraphicalPopulationPresenterEvaluator(this);
		glSphere.setNumOfIndividual(opwin.populationSize);
				
		topFrame.add(glSphere, BorderLayout.CENTER);
		topFrame.add(controlButtons, BorderLayout.SOUTH);
		topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		topFrame.setSize(800, 768);

		topFrame.setVisible(true);
		
		//JFrame frame = new JFrame("GLSphere test Display");
		//frame.add(glSphere);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    //frame.setVisible(true);
	    //frame.setSize(800, 600);	
	}
	
	@Override
	public void afterInitialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterReproduce() {
		topFrame.remove(glSphere);
		glSphere = new GLSphere();
		glSphere.setGraphicalPopulationPresenterEvaluator(this);
		glSphere.setNumOfIndividual(opwin.populationSize);
		topFrame.add(glSphere,BorderLayout.CENTER);
		topFrame.setVisible(true);
	}

	@Override
	public void closingProcessingAnimation() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFitnessValue(int index) {
		return glSphere.getFitnessValue(index);
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
		eventLists[index] = eventList;
	}

	@Override
	public void setFitnessValue(int index, int position) {
		// TODO Auto-generated method stub

	}

	public void playAsMIDISequence(int index){
		System.err.println("playing index is " +index);
		if(index >= 0 && index < opwin.populationSize)
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
