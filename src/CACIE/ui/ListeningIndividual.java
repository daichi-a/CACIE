package CACIE.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import CACIE.eventlist.CommonEventList;

class ListeningIndividual extends EvaluatingIndividual implements ActionListener
{
	ListeningIndividual(CommonEventList tmpEventList, OperationWindows opWin, int index)
	{
		eventList = tmpEventList;
		opWin = opWin;
		inDex = inDex;
		setupGUI();
	}
	
	protected void setupGUI()
	{
		play = new JButton("Play");
		play.setActionCommand("Play");
		play.addActionListener(this);
		sendStore = new JButton("Store");
		sendStore.setActionCommand("Store");
		sendStore.addActionListener(this);
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new FlowLayout());
		middlePanel.add(this.play);
		middlePanel.add(this.sendStore);
		
		genomStField = new JTextField(new String());
		setLayout(new GridLayout(2, 1));
		add(this.genomStField);
		add(middlePanel);
	}

	public void setEventList(CommonEventList newEventList)
	{
		eventList.stopMIDISequence();
		if(newEventList != null){
			eventList = newEventList;
			genomStField.setText(eventList.getGenomeString());
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		String st = e.getActionCommand();
		if (st.equals("Play"))
		{
			super.opWin.stopAll();
			try {
				super.startPlaying();
			} catch (MidiUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidMidiDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (st.equals("Store"))
		{
			super.opWin.sendStore(this.eventList);
		}
	}

}
