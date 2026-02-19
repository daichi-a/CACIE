package CACIE.ui;

import java.awt.FlowLayout; //import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
//import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame; //import javax.swing.JLabel;
import javax.swing.JPanel; //import javax.swing.JScrollPane;
import javax.swing.JSlider; //import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeModel;

import java.io.*;


import CACIE.eventlist.CommonEventList; //import CACIE.genome.Individual;
import CACIE.eventlist.PopulationOfEventList;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.Population;
//import CACIE.ui.sphereGUI.SphereGUI;

public class OperationWindows_GUI extends OperationWindows implements
		ActionListener, ChangeListener {

	protected static int REPRODUCE = 1;
	protected static int REPLACE = 2;
	protected static int INITIALIZE = 3;
	protected static int REINJECT = 4;

	protected TreeEditFrame treeEditFrame;
	protected GraphicalPopulationPresenterEvaluator populationDisplay;
	
	boolean logFile, writeFirst;
	String logFileName;
	protected long startTime, finishTime; 


	public OperationWindows_GUI(int _populationSize, ArrayList _notes,
			ArrayList _oprList, ArrayList confList) {
		genomeType = "TREE";
		operationList = _oprList;
		notes = _notes;

		populationSize = _populationSize;
		thePopulation = 
			new Population(populationSize, genomeType, 0, notes, _oprList, confList);
		
		//make LogFileName
		logFile = false;
		writeFirst = true;
		int counter = 0;
		while(counter<confList.size()){
			String currentConfigLine = (String)confList.get(counter);
			if(currentConfigLine.matches("^ LOG_FILE_NAME.*")){
				StringTokenizer st = new StringTokenizer(currentConfigLine, " ");
				if(st.countTokens()<2){
					System.err.println("OperationWindows_GUI: No log file name given.");
					break;
				}
				logFile = true;
				st.nextToken();
				logFileName = st.nextToken();
				System.err.println("OperationWndows_GUI: Log file name: " + logFileName);
				break;
			}
			counter++;
		}
		

		// Initialize Population and EventList
		thePopulation.initPopulation();
		cashEvThis = new ArrayList<CommonEventList>(populationSize);
		cashEvNext = new ArrayList<CommonEventList>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			cashEvThis.add(thePopulation.convertToEventList(0, i));
		}

		setUpGUI();
		generateNewPopulationDisplay();
		generateGenomeStorageWindow();
		setListThisGeneration(cashEvThis);
		
		// tree edit panel
		// this.initTreeEditPanel();
		topFrame.setVisible(true);
		topFrame.pack();
		generateTreeEditWindow();

		// generateSphereGUIWindow();
		
		startTime = System.currentTimeMillis();
		instrumentNumber = 0;
	}
 
/* 	protected void generateSphereGUIWindow() {
		SphereGUI sphereGUI = new SphereGUI(this);
		sphereGUI.init();
		JFrame frame = new JFrame();
		frame.add(sphereGUI.panel);
		frame.setSize(800, 600);
		frame.setVisible(true);
		System.out.println("SphereGUI");
	}
*/
	public OperationWindows_GUI() {
		this.populationSize = 16;
		this.setUpGUI();
	}
 
	protected void generateNewPopulationDisplay() {
		// Population Presentation Window
		//populationDisplay = new SphereGUI(this);
	}
	

	protected void generateGenomeStorageWindow() {
		// Realizing Genome Storage
		genomStocker = new ManageTerminalNodes(
				ManageTerminalNodes.WITH_GUI);
		genomStocker.setPopulationWindow(this);
		Point tmpPoint = topFrame.getLocation();
		int width = topFrame.getWidth();
		int height = topFrame.getHeight();
		genomStocker.setPosition(tmpPoint.x, tmpPoint.y + height);
	}

	protected void generateTreeEditWindow() {
		// Realizing Tree Edit Frame
		treeEditFrame = new TreeEditFrame(this);
		Point tmpPoint = topFrame.getLocation();
		int width = topFrame.getWidth();
		int height = topFrame.getHeight();
		treeEditFrame.setPosition(tmpPoint.x + width, tmpPoint.y);
	}

	protected void setUpGUI() {
		setUpControllWindowGUI();
	}

	protected void setUpControllWindowGUI() {
		this.topFrame = new JFrame("CACIE Control Panel");

		JPanel controllers = new JPanel();
		// JButton initBut = new JButton("Init");
		// initBut.setActionCommand("initBut");
		// initBut.addActionListener(this);
		// JButton reproduceBut = new JButton("Reproduce");
		// reproduceBut.setActionCommand("reproduceBut");
		// reproduceBut.addActionListener(this);
		// JButton replaceBut = new JButton("Replace");
		// replaceBut.setActionCommand("replaceBut");
		// replaceBut.addActionListener(this);
		JButton stopBut = new JButton("Stop");
		stopBut.setActionCommand("stopBut");
		
		stopBut.addActionListener(this);
		controllers.setLayout(new FlowLayout());
		// controllers.add(initBut);
		// controllers.add(reproduceBut);
		// controllers.add(replaceBut);
		controllers.add(stopBut);
		
		JButton readBut = new JButton("Read");
		readBut.setActionCommand("readBut");
		readBut.addActionListener(this);
		controllers.add(readBut);
		
		// Tempo
		tempoBar = new JSlider(10, 200);
		tempoBar.setValue(CommonEventList.DT);
		tempoBar.addChangeListener(this);
		tempoText = new JTextField("Tempo:" + CommonEventList.DT + "  ");
		tempoText.setEditable(false);
		controllers.add(tempoBar);
		controllers.add(tempoText);

		// Instrument
		Vector<String> instrumentName = new Vector<String>();
		instrumentName.add("Grand Piano");
		instrumentName.add("Music Box");
		instrumentName.add("Marinba");
		instrumentName.add("Percussive Organ");
		instrumentName.add("Clean Guitar");
		instrumentName.add("Overdrive Guitar");
		instrumentName.add("Strings");
		instrumentName.add("Trumpet");
		instrumentName.add("Saxophone");
		instrumentName.add("Clarinet");
		instrumentName.add("Flute");
		instrumentName.add("Orchestra");
		instrumentBox = new JComboBox(instrumentName);
		instrumentBox.setActionCommand("instrumentBox");
		instrumentBox.addActionListener(this);
		controllers.add(instrumentBox);
		
		topFrame.add(controllers);
		topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// this.topFrame.setVisible(true);
		// this.topFrame.pack();
		// this.topFrame.setSize(500, 600);

	}

	public void actionPerformed(ActionEvent e) {
		String aes = e.getActionCommand();
		//System.err.println(aes);
		if (aes.equals("reproduceBut")) {
			this.reproduce();
		} else if (aes.equals("replaceBut")) {
			this.replace();
		} else if (aes.equals("initBut")) {
			this.init();
		} else if (aes.equals("stopBut")) {
			//System.err.println("Stop All Playing.");
			this.stopAll();
		} else if (aes.equals("readBut")) {
			readPopulationFromFile();
		} else if (aes.equals("instrumentBox")){
			int selectedIndex = instrumentBox.getSelectedIndex();
			if(selectedIndex != -1){
				if(selectedIndex == 0)
					instrumentNumber = 0;
				else if(selectedIndex == 1)
					instrumentNumber = 10;
				else if(selectedIndex == 2)
					instrumentNumber = 12;
				else if(selectedIndex == 3)
					instrumentNumber = 17;
				else if(selectedIndex == 4)
					instrumentNumber = 27;
				else if(selectedIndex == 5)
					instrumentNumber = 29;
				else if(selectedIndex == 6)
					instrumentNumber = 48;
				else if(selectedIndex == 7)
					instrumentNumber = 56;
				else if(selectedIndex == 8)
					instrumentNumber = 66;
				else if(selectedIndex == 9)
					instrumentNumber = 71;
				else if(selectedIndex == 10)
					instrumentNumber = 73;
				else if(selectedIndex == 11)
					instrumentNumber = 112;
			}
			else{
				instrumentNumber = 0;
			}
			for(int i=0; i<cashEvThis.size(); i++)
				cashEvThis.get(i).setInstrumentNumber(instrumentNumber);
			for(int i=0; i<cashEvNext.size(); i++)
				cashEvNext.get(i).setInstrumentNumber(instrumentNumber);
			
		}
		else if (aes.substring(0, 4).equals("Store")) {
			StringTokenizer tmpStkn = new StringTokenizer(aes, "_");
			tmpStkn.nextToken();
			System.out.println(tmpStkn.nextToken());
			System.out.println(tmpStkn.nextToken());
		}
	}

	public void init() {
		thePopulation.initPopulation();
		cashEvThis.clear();
		cashEvThis.ensureCapacity(this.populationSize);
		for (int i = 0; i < this.populationSize; i++) {
			cashEvThis.add(thePopulation.convertToEventList(0, i));
		}
		makeIndividualList(INITIALIZE);
	}

	public void reproduce_and_replace() {
		finishTime = System.currentTimeMillis();
		if(logFile)
			writeLog_TimeFitnessDistance();
		reproduce();
		replace();
		startTime = System.currentTimeMillis();
		if(logFile)
			writeLog_CrossoverAndMutation();
	}

	public void reproduce() {
		
		thePopulation.refreshEvaluated();
		for (int i = 0; i < this.populationSize; i++) {
			thePopulation.evaluate(i, populationDisplay.getFitnessValue(i));
			System.out.println("Fitness[" + i + "]: "
					+ populationDisplay.getFitnessValue(i));
		}
		thePopulation.setTempo(getTempo());
		thePopulation.reproductPopulation();

		cashEvNext.clear();
		cashEvNext.ensureCapacity(this.populationSize);
		for (int i = 0; i < this.populationSize; i++) {
			cashEvNext.add(thePopulation.convertToEventList(1, i));
		}
		makeIndividualList(REPRODUCE);
	}

	public void replace() {
		
		stopAll();
		thePopulation.replacePopulation();
		this.cashEvNext.clear();
		cashEvThis.clear();
		cashEvThis.ensureCapacity(this.populationSize);
		
		for (int i = 0; i < this.populationSize; i++) {
			cashEvThis.add(thePopulation.convertToEventList(0, i));
			if(cashEvThis.get(i)==null){
				System.err.println("OperationWindows_GUI:replace: convertToEventList has null:" + i);
				System.exit(1);
			}
		}
		
		makeIndividualList(REPLACE);
	}

	protected void makeIndividualList(int mode) {
		if (mode == REPRODUCE) {
			// this.NextScrollPane.getViewport().setView(this.listNextGene);
			setListNextGeneration(cashEvNext);
		} else if (mode == REPLACE) {
			setListThisGeneration(cashEvThis);
			this.NextScrollPane.getViewport().setView(new JPanel());
		} else if (mode == INITIALIZE) {
			setListThisGeneration(this.cashEvThis);
			this.NextScrollPane.getViewport().setView(new JPanel());
		} else if (mode == REINJECT) {
			setListThisGeneration(this.cashEvThis);
		}
		this.topFrame.repaint();
	}

	public void reInject(CommonEventList eventList, int index) {
		thePopulation.refreshEvaluated();
		int temporaryFitness[] = new int[this.populationSize];
		if(index == -1){
		for (int i = 0; i < this.populationSize; i++) {
			int tmpFitness = populationDisplay.getFitnessValue(i);
			thePopulation.evaluate(i, tmpFitness);
			temporaryFitness[i] = tmpFitness;
		}
		}
		int indexToApply = thePopulation.reInject(eventList, index);
		CommonEventList tmpEv = thePopulation.convertToEventList(0,
				indexToApply);
		this.cashEvThis.remove(indexToApply);
		this.cashEvThis.add(indexToApply, tmpEv);
		makeIndividualList(REINJECT);
		if(index == -1){
		for (int i = 0; i < this.populationSize; i++) {
			if (i != indexToApply)
				populationDisplay.setFitnessValue(i, temporaryFitness[i]);
			else
				populationDisplay.setFitnessValue(i,
						GraphicalPopulationPresenterEvaluator.FITNESS_CENTER);
		}
		System.out.println("OperationWindows : finish Re-Inject : Replace to "
				+ indexToApply);
		}
	}

	public void sendStore(CommonEventList evt) {
		this.genomStocker.addIndividual(evt);
	}

	public void setListThisGeneration(ArrayList eventListSet) {
		for (int i = 0; i < this.populationSize; i++) {
			CommonEventList tmpEventList = (CommonEventList) eventListSet
					.get(i);
			if (tmpEventList == null){
				System.out
						.println("OperationWindows:setListThisGeneration: Cannot find "
								+ i + "'th eventlist");
				System.exit(1);
			}
			populationDisplay.setEventList(i, (CommonEventList) eventListSet
					.get(i));
		}
	}

	public void setListNextGeneration(ArrayList eventlistSet) {
		int until = this.ListInds.size();
		for (int i = 0; i < until; i++) {
			ListeningIndividual lstInd = (ListeningIndividual) this.ListInds
					.get(i);
			lstInd.setEventList((CommonEventList) eventlistSet.get(i));
		}
	}

	public void stopAll() {
		int until = cashEvThis.size();
		for (int i = 0; i < until; i++) {
			CommonEventList tmpEventList = (CommonEventList) cashEvThis.get(i);
			if(tmpEventList!=null)
				tmpEventList.stopMIDISequence();
		}
		until = cashEvNext.size();
		for (int i = 0; i < until; i++) {
			CommonEventList tmpEventList = (CommonEventList) cashEvNext.get(i);
			if(tmpEventList != null)
				tmpEventList.stopMIDISequence();
		}
	}

	public void readPopulationFromFile() {
		String fileName = getPopulationFileName();
		if(fileName.equals("CANCELLED")){
			//キャンセルされた場合はなにもせずに返す
			return;
		}
		
		if(!PopulationOfEventList.isPopulation(fileName)){
			System.err.println("OperationWindows_GUI: readPopulationFromFile: The file is not population.");
			return;
		}
		
		PopulationOfEventList popEv = new PopulationOfEventList();
		popEv.readFromFile(fileName);
		for(int i=0; i<popEv.getNumOfIndividuals(); i++)
			reInject((CommonEventList)popEv.get(i), i);

		makeIndividualList(REINJECT);		
	}
	
	protected String getPopulationFileName(){
		String filePath = "CANCELLED";
		JFileChooser fc = new JFileChooser(".");
		int result = fc.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION)
			filePath = fc.getSelectedFile().getPath();
		/*
		else if(result == JFileChooser.CANCEL_OPTION){
			System.err.println("Canceled.");
			System.exit(0);
		}
		else{
			System.err.println("Error. Exiting.");
			System.exit(1);
		}*/
		return filePath;
	}
	
	public Motif_simpleTree_Individual getIndividual(int index) {
		return (Motif_simpleTree_Individual) this.thePopulation.getIndividual(
				0, index);
	}

	public Population getPopulation() {
		return this.thePopulation;
	}

	public ArrayList getOperationList() {
		return operationList;
	}

	public ArrayList getNotes() {
		return notes;
	}

	public int getTempo() {
		return tempoBar.getValue();
	}

	public int getInstrumentNumber(){
		return instrumentNumber;
	}
	
	public void treeSelect(TreeModel tree, int index) {
		TreeEditPanel _treeEditPanel = treeEditFrame.getTreeEditPanel();
		_treeEditPanel.selectTree(tree, index);
	}

	// implementation from changeListener method
	public void stateChanged(ChangeEvent ce) {
		tempoText.setText("Tempo:" + String.valueOf(tempoBar.getValue()));
		this.topFrame.repaint();
	}
	
	public void writeLog_TimeFitnessDistance(){
		long requiredTimeToEvaluate = finishTime - startTime;
		System.err.println("Evaluating time is: " + requiredTimeToEvaluate);
		String log = new String(String.valueOf(requiredTimeToEvaluate));
		for(int i=0; i<populationSize; i++)
			log = log + "," +  String.valueOf(populationDisplay.getFitnessValue(i));
		
		//追記していく
		BufferedWriter writer = null;
		try{
			if(writeFirst){
				writer = new BufferedWriter(new FileWriter(logFileName,false));
				writeFirst = false;
			}
			else
				writer = new BufferedWriter(new FileWriter(logFileName,true));
			writer.write(log);
			writer.newLine();
			double[][] distances = getDistances();
			String distanceLogLine = "";
			for(int i=0; i<populationSize; i++){
				for(int j=0; j<populationSize; j++){
					if(j==0)
						distanceLogLine = Double.toString(distances[i][j]);
					else
						distanceLogLine = distanceLogLine.toString() + "," + Double.toString(distances[i][j]);
				}
				writer.write(distanceLogLine);
				writer.newLine();
			}			
			writer.newLine();
			writer.flush();
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeLog_CrossoverAndMutation(){
		BufferedWriter writer = null;
		try{
			if(writeFirst){
				writer = new BufferedWriter(new FileWriter(logFileName,false));
				writeFirst = false;
			}
			else
				writer = new BufferedWriter(new FileWriter(logFileName,true));

			//Crossoverのログ
			String crossoverLog = "Crossover:";
			ArrayList<String> crossoveredRecord = 
				thePopulation.getCrossoveredIndividualLog();
			for(int i=0; i<populationSize; i++){
				crossoverLog = 
					crossoverLog.toString() + "," + crossoveredRecord.get(i).toString();
			}
			writer.write(crossoverLog);
			writer.newLine();
			
			//Mutationのログ
			//String mutationLog = "Mutation:";
			writer.write("Mutation:"); writer.newLine();
			ArrayList<String> mutatedRecord = 
				thePopulation.getMutatedIndividualLog();
			for(int i=0; i<populationSize; i++){
				writer.write(mutatedRecord.get(i).toString());
				writer.newLine();
			}
			//writer.write(mutationLog);
			//writer.newLine();
			
			writer.newLine();
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	
	/*
	 * public EvaluatingIndividual reflesh(int index) {
	 * Motif_simpleTree_Individual individual = ((Motif_simpleTree_Individual)
	 * thePopulation .getIndividual(0, index)); individual.run();
	 * cashEvThis.set(index, individual.getEventList()); CommonEventList
	 * eventList = (CommonEventList) this.cashEvThis.get(index);
	 * ((EvaluatingIndividual)
	 * this.DistInds.get(index)).setEventList(eventList); // this.treeSelect( //
	 * ((EvaluatingIndividual)DistInds.get(index)).getTreeModel( individual // ),
	 * index ); return ((EvaluatingIndividual) this.DistInds.get(index)); }
	 */
}