package CACIE.ui;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import CACIE.eventlist.CommonEventList;

public class EvaluatingIndividual_OID5 extends EvaluatingIndividual_OI {
	//五段階のラジオボタンバージョン

	public EvaluatingIndividual_OID5() {
		// TODO Auto-generated constructor stub
	}

	public EvaluatingIndividual_OID5(CommonEventList eventList,
			OperationWindows opWin, int inDex) {
		super(eventList, opWin, inDex);
		// TODO Auto-generated constructor stub
	}
	
	protected void setupGUI() {
		slider = new JSlider(0, 4, 2);
		slider.setSnapToTicks(true);
		
		play = new JButton("Play");
		play.setActionCommand("Play");
		play.addActionListener(this);

		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new FlowLayout());
		middlePanel.add(this.play);
		middlePanel.add(this.slider);

		genomStField = new JTextField(new String());
		add(middlePanel);
	}
	
	
	//五段階にする
	public void setSliderOfFitness(int value) {
		slider.setValue(value/25);
		System.err.println("Fader setted " + value/25);
	}
	
	protected int getFitnessValue() {
		fitnessValue = slider.getValue() * 25;
		System.err.println("Fader give fitness: " + slider.getValue());
		return fitnessValue;
	}
}
