package CACIE.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import CACIE.eventlist.CommonEventList;
//import CACIE.eventlist.MusicLibraryHandler;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.TreeNodes;
import CACIE.genome.TreeOperators;

public class EvaluatingIndividual extends JPanel implements ActionListener {
	/**
	 * 
	 */

	int fitnessValue;

	CommonEventList eventList;

	JSlider slider;

	JButton play;

	JButton sendStore;

	JButton viewSingleTreeButton;

	JButton putTreeViewButton;

	JTextField genomStField;

	OperationWindows opWin;

	int inDex;

	EvaluatingIndividual() {
	}

	public EvaluatingIndividual(CommonEventList newEventList,
			OperationWindows newOpWin, int newInDex) {
		eventList = newEventList;
		opWin = newOpWin;
		inDex = newInDex;
		setupGUI();
	}

	public void setEventList(CommonEventList newEventList) {
		eventList.stopMIDISequence();
		eventList = newEventList;
		String tmpString = eventList.getGenomeString();
		genomStField.setText(tmpString);
		System.out.println(tmpString);
		fitnessValue = 50;
		setSliderOfFitness(fitnessValue);
		repaint();
	}

	public boolean hasGenomeString(){
		if(eventList.getGenomeString() == null)
			return false;
		else
			return true;
	}
	
	protected int getFitnessValue() {
		fitnessValue = slider.getValue();
		return fitnessValue;
	}

	public void stopPlaying() {
		eventList.stopMIDISequence();
	}

	public void startPlaying() throws MidiUnavailableException, InvalidMidiDataException {
		eventList.playAsMIDISequence(opWin.getTempo());
	}

	protected void setupGUI() {
		slider = new JSlider();
		play = new JButton("Play");
		play.setActionCommand("Play");
		play.addActionListener(this);
		sendStore = new JButton("Store");
		sendStore.setActionCommand("Store");
		sendStore.addActionListener(this);

		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new FlowLayout());
		middlePanel.add(play);
		middlePanel.add(sendStore);
		middlePanel.add(slider);

		// tanji's addition
		viewSingleTreeButton = new JButton("EditTree");
		viewSingleTreeButton.addActionListener(this);
		putTreeViewButton = new JButton("ViewTree");
		putTreeViewButton.addActionListener(this);
		middlePanel.add(viewSingleTreeButton);
		middlePanel.add(putTreeViewButton);
		//

		genomStField = new JTextField(new String());
		setLayout(new GridLayout(2, 1));
		add(this.genomStField);
		add(middlePanel);
	}

	public void setSliderOfFitness(int value) {
		slider.setValue(value);
	}

	public void actionPerformed(ActionEvent e) {
		String st = e.getActionCommand();
		if (st.equals("Play")) {
			opWin.stopAll();
			try {
				startPlaying();
			} catch (MidiUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//MusicLibraryHandler.showScore(MusicLibraryHandler
				//	.convertToTrackData(eventList));
		}
		if (st.equals("Store")) {
			opWin.sendStore(this.eventList);
		} else if (e.getSource() == viewSingleTreeButton) {
			if (opWin != null) {
				TreeEditor editor = new TreeEditor(opWin, inDex);
				JDialog dialog = new JDialog(opWin.topFrame);
				dialog.add(editor);
				dialog.setVisible(true);
				dialog.pack();
			}
		} else if (e.getSource() == putTreeViewButton) {
			if (opWin != null) {
				Motif_simpleTree_Individual individual = this.opWin.getIndividual(inDex);
				if (individual != null) {
					TreeModel treeModel = getTreeModel(individual);
					opWin.treeSelect(treeModel, inDex);
				}
			}
		}
	}

	public static DefaultTreeModel getTreeModel(
			Motif_simpleTree_Individual individual) {
		ArrayList<TreeNodes> genomSymbols = individual.getGenomeArray();

		//DefaultMutableTreeNode[] nodes = 
		//	new DefaultMutableTreeNode[genomSymbols.size()];
		Stack<DefaultMutableTreeNode> parentStack = 
			new Stack<DefaultMutableTreeNode>();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(genomSymbols
				.get(0));
		parentStack.add(root);

		for (int i = 1; i < genomSymbols.size(); i++) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					genomSymbols.get(i));
			DefaultMutableTreeNode parent = ((DefaultMutableTreeNode) parentStack
					.lastElement());
			parent.add(node);
			if (parent.getChildCount() == getStackCount(((TreeNodes) parent
					.getUserObject()).toString())
					* -1 + 1) {
				parentStack.pop();
			}
			if (individual.getStackCount(i) != 1) {
				parentStack.add(node);
			}
		}
		return new DefaultTreeModel(root);
	}

	private static String trimSymbolTail(String name) {
		String symbolName = name;
		if (symbolName.contains("_")) // nonterminal
		{
			symbolName = symbolName.substring(0, symbolName.indexOf("_"));
		}
		return symbolName;
	}

	private static int getStackCount(String symbol) {
		String symbolName = trimSymbolTail(symbol);
		// System.out.println("name " + symbolName);
		if (symbolName.matches("\\d+") || symbolName.equals("R")) // terminal
																	// if
		// symbol is
		// just a number
		{
			return 1;
		} else {
			return TreeOperators.getStackCount(TreeOperators
					.getOperatorFromString(symbolName));
		}
	}
	//
}
