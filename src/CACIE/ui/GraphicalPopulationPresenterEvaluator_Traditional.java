package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import java.util.ArrayList;

import CACIE.eventlist.CommonEventList;

public class GraphicalPopulationPresenterEvaluator_Traditional extends
		GraphicalPopulationPresenterEvaluator {

	JScrollPane ThisScrollPane;
	//ArrayList<EvaluatingIndividual> DistInds;
	
	protected static int windowSizeX = 800, windowSizeY = 600;
	protected static int scrollPaneSizeX = 500, scrollPaneSizeY = 250;
	
	public GraphicalPopulationPresenterEvaluator_Traditional(OperationWindows operationWindows){
		super(operationWindows);

		setupGUI();
		preInitialize();
		afterInitialize();
	}
	
	protected void generateEvaluatingIndividual(JPanel listPanel){
		for (int i = 0; i < opwin.populationSize; i++){
			EvaluatingIndividual tmpEvInd = new EvaluatingIndividual(new CommonEventList(0), opwin, i);
			listPanel.add(tmpEvInd);
			DistInds.add(tmpEvInd);
		}
		System.err.println("Size of DistInds is:" + opwin.DistInds.size()+ ", PopulationSize is: " + opwin.populationSize);
	}
	
	protected void setupGUI(){
		topFrame = new JFrame("Population Display");
		JPanel listThisGene = new JPanel();
		listThisGene.setLayout(new GridLayout(opwin.populationSize, 1));
		DistInds = new ArrayList<EvaluatingIndividual>(opwin.populationSize);
		generateEvaluatingIndividual(listThisGene);
		
		ThisScrollPane = new JScrollPane();
		ThisScrollPane.getViewport().setView(listThisGene);
		JPanel individualPanel = new JPanel();
		individualPanel.setLayout(new GridLayout(1, 2));
		individualPanel.add(this.ThisScrollPane);
		ThisScrollPane.setPreferredSize(new Dimension(scrollPaneSizeX, scrollPaneSizeY));

		// upperPanel
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(individualPanel);
		
		//�X�[�p�[�N���X�ŏ������R���g���[���{�^���̃p�l����z�u
		upperPanel.add(controlButtons, BorderLayout.SOUTH);
		topFrame.add(upperPanel);
		topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		System.out.println(upperPanel.getPreferredSize());

		topFrame.setSize(windowSizeX, windowSizeY);
		topFrame.setVisible(true);
		//topFrame.pack();
	}
		
	@Override
	public int getFitnessValue(int index){
		EvaluatingIndividual tmpInd = DistInds.get(index);
		return tmpInd.getFitnessValue();
	}

	@Override
	public void setFitnessValue(int index, int value){
		EvaluatingIndividual tmpInd = DistInds.get(index);
		tmpInd.setSliderOfFitness(value);
	}
	
	@Override
	public void setEventList(int index, CommonEventList eventList){
		EvaluatingIndividual tmpInd = DistInds.get(index);
		tmpInd.setEventList(eventList);
	}
	
	@Override
	public void afterInitialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterReproduce() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closingProcessingAnimation() {
		// TODO Auto-generated method stub

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

	
}
