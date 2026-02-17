package CACIE.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.*;

import CACIE.eventlist.*;

public abstract class GraphicalPopulationPresenterEvaluator implements
		ActionListener {

	public OperationWindows opwin;
	public JFrame topFrame;
	public JButton reproduceButton, initButton, stopButton;
	public JPanel controlButtons;
	public static int FITNESS_CENTER = 50;
	
	public ArrayList<EvaluatingIndividual> DistInds;

	public GraphicalPopulationPresenterEvaluator(OperationWindows operationWindows){
		opwin = operationWindows;
		initButton = new JButton("Init");
		initButton.setActionCommand("initButton");
		initButton.addActionListener(this);
		reproduceButton = new JButton("Reproduction");
		reproduceButton.setActionCommand("reproductionButton");
		reproduceButton.addActionListener(this);
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("stopButton");
		stopButton.addActionListener(this);
		
		controlButtons = new JPanel();
		controlButtons.setLayout(new FlowLayout());
		controlButtons.add(initButton);
		controlButtons.add(reproduceButton);
		controlButtons.add(stopButton);
		
		DistInds = new ArrayList<EvaluatingIndividual>(opwin.populationSize);
		for (int i = 0; i < opwin.populationSize; i++){
			EvaluatingIndividual tmpEvInd = new EvaluatingIndividual(new CommonEventList(0), opwin, i);
			DistInds.add(tmpEvInd);
		}
	}
	
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	    String aes = e.getActionCommand();
	    if (aes.equals("reproductionButton"))
	    {
	    	preReproduce(); //Processing and Animation
	    	opwin.reproduce_and_replace();
	    	afterReproduce(); //Processing and Animation
	    } else if (aes.equals("initButton"))
	    {
	    	preInitialize(); //Processing and Animation
	    	opwin.init();
	    	afterInitialize(); //Processing and Animation
	    } else if (aes.equals("stopButton"))
	    {
	      opwin.stopAll();
	    }
	    else if (aes.substring(0, 4).equals("Store"))
	    {
	      StringTokenizer tmpStkn = new StringTokenizer(aes, "_");
	      tmpStkn.nextToken();
	      System.out.println(tmpStkn.nextToken());
	      System.out.println(tmpStkn.nextToken());
	    }	
	}
	
	abstract public void openingProcessingAnimation();
	abstract public void closingProcessingAnimation();
	abstract public void preReproduce();
	abstract public void afterReproduce();
	abstract public void preInitialize();
	abstract public void afterInitialize();
	abstract public int getFitnessValue(int index);
	abstract public void setFitnessValue(int index, int position);
	abstract public void setEventList(int index, CommonEventList eventList);
	
}

