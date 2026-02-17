package CACIE.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import CACIE.eventlist.CommonEventList;

public class EvaluatingIndividual_OI extends EvaluatingIndividual {

	public EvaluatingIndividual_OI() {
		super();
	}

	public EvaluatingIndividual_OI(CommonEventList eventList,
			OperationWindows opWin, int inDex) {
		super(eventList, opWin, inDex);
	}

	protected void setupGUI() {
		slider = new JSlider();
		play = new JButton("Play");
		play.setActionCommand("Play");
		play.addActionListener(this);
		//sendStore = new JButton("Store");
		//sendStore.setActionCommand("Store");
		//sendStore.addActionListener(this);

		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new FlowLayout());
		middlePanel.add(this.play);
		//middlePanel.add(this.sendStore);
		middlePanel.add(this.slider);

		// tanji's addition
		/*
		viewSingleTreeButton = new JButton("EditTree");
		viewSingleTreeButton.addActionListener(this);
		putTreeViewButton = new JButton("ViewTree");
		putTreeViewButton.addActionListener(this);
		middlePanel.add(this.viewSingleTreeButton);
		middlePanel.add(this.putTreeViewButton);
		*/
		//

		genomStField = new JTextField(new String());
		//this.setLayout(new GridLayout(2, 1));
		//this.add(this.genomStField);
		add(middlePanel);
	}
}
