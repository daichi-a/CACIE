package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.OneNote;

public class DisplayIndividual implements ActionListener
{
  JFrame topFrame;
  JPanel topPanel, buttonPanel;
  JList displayEventList;
  JButton displayPianoRoll, playMIDI, saveMIDI, saveEventList;
  JScrollPane scrollPane;
  String[] displiedEventList;
  
  JFileChooser fileChooser;

  CommonEventList eventList;

  public DisplayIndividual(int x, int y)
  {
    this.setDisplayEventList();
    this.setUpGUI(x, y);
  }

  public DisplayIndividual(Object o, int x, int y)
  {
    this.setEventList(o);
    this.setDisplayEventList();
    this.setUpGUI(x, y);

  }

  private void setDisplayEventList()
  {
    String[] lists;

    OneNote tmpNote;
    if (this.eventList == null)
    {
      String[] dammy = { "dammy", "1,34,56,34", "3,56,6,34,", "6,4,3,7", "2,5,7,5" };
      lists = dammy;
    } else
    {
      int pNumOfNotes = this.eventList.getNumOfNotes();
      lists = new String[pNumOfNotes + 1];
      lists[0] = new String("NoteNum,Velocity,OnTime,Duration");
      for (int i = 0; i < pNumOfNotes; i++)
      {
	tmpNote = (OneNote) this.eventList.get(i);
	lists[i + 1] = (String) tmpNote.getEventList();
      }
    }

    this.displiedEventList = lists;
  }

  private void setEventList(Object eventlist)
  {
    this.eventList = (CommonEventList) eventlist;
  }

  public void setUpGUI(int x, int y)
  {
    this.topFrame = new JFrame();
    this.topPanel = new JPanel();
    this.displayEventList = new JList(this.displiedEventList);
    this.scrollPane = new JScrollPane();
    this.scrollPane.getViewport().setView(this.displayEventList);

    this.displayPianoRoll = new JButton("Piano Roll");
    this.displayPianoRoll.setActionCommand("displayPianoRoll");
    this.displayPianoRoll.addActionListener(this);

    this.playMIDI = new JButton("Play MIDI");
    this.playMIDI.setActionCommand("playMIDI");
    this.playMIDI.addActionListener(this);

    this.saveMIDI = new JButton("Save MIDI File");
    this.saveMIDI.setActionCommand("saveMIDI");
    this.saveMIDI.addActionListener(this);

    this.saveEventList = new JButton("Save EventList");
    this.saveEventList.setActionCommand("saveEventList");
    this.saveEventList.addActionListener(this);

    this.buttonPanel = new JPanel();
    this.buttonPanel.add(this.displayPianoRoll);
    this.buttonPanel.add(this.playMIDI);
    this.buttonPanel.add(this.saveMIDI);
    this.buttonPanel.add(this.saveEventList);

    this.topPanel.setLayout(new BorderLayout());
    this.topPanel.add(BorderLayout.CENTER, this.scrollPane);
    this.topPanel.add(BorderLayout.SOUTH, this.buttonPanel);

    this.topFrame.getContentPane().setLayout(new BorderLayout());
    this.topFrame.getContentPane().add(BorderLayout.CENTER, this.topPanel);

    this.topFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.topFrame.pack();
    this.topFrame.setLocation(x, y);
    topFrame.setVisible(true);
  }

  public void actionPerformed(ActionEvent e)
  {
    String eventCommand = e.getActionCommand();
    if (eventCommand.equals("playMIDI"))
      this.DIPlayMIDI();
    else if (eventCommand.equals("saveMIDI"))
      this.DISaveMIDI();
    else if (eventCommand.equals("saveEventList"))
      this.DISaveEventList();
    else if (eventCommand.equals("displayPianoRoll"))
      this.DIDisplayPianoRoll();

  }

  private void DIPlayMIDI()
  {
    try {
		this.eventList.playAsMIDISequence(CommonEventList.DT);
	} catch (MidiUnavailableException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidMidiDataException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  private void DISaveMIDI()
  {
    fileChooser = new JFileChooser();
    if (fileChooser.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION)
    {
      File tmpFile = fileChooser.getSelectedFile();
      String filename = tmpFile.getPath();
      this.eventList.saveAsMIDISequence(filename);

    }
  }

  private void DISaveEventList()
  {
    fileChooser = new JFileChooser();
    if (fileChooser.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION)
    {
      File tmpFile = fileChooser.getSelectedFile();
      String filename = tmpFile.getPath();
      this.eventList.writeToFile(filename);
    }

  }

  private void DIDisplayPianoRoll()
  {

  }

}