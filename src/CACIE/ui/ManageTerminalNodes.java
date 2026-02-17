package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

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
import CACIE.eventlist.PopulationOfEventList;
import CACIE.genome.Notes;

public class ManageTerminalNodes implements ActionListener
{
	PopulationOfEventList eventLists;
	JFrame topFrame;
	JFileChooser fileChooser;
	JList listOfIndividuals;
	JScrollPane scrollPane;
	String[] displayEventList;
	int uiMode;
	CommonEventList CurrentEv;
	JPanel middlePanel, controllers;
	boolean relationPopWindow = false;
	OperationWindows opwin;

	JButton addBut, removeBut, saveBut, saveAllBut, playBut, reInjectBut, stopBut;

	int x = 0, y = 0;

	// mode
	public static int WITH_GUI = 1024;
	public static int WITHOUT_GUI = 1025;

	public ManageTerminalNodes(String fileName, int mode)
	{
		this();
		this.eventLists.readFromFile(fileName);
		this.uiMode = mode;

		//int numOfTerminalNodes = this.eventLists.numberOfIndividuals();

		if (this.uiMode == ManageTerminalNodes.WITH_GUI)
		{
			this.makeDisplayEventList();
			this.setupGUI();
		}
	}

	public ManageTerminalNodes(PopulationOfEventList tmpEventList, int mode)
	{
		this.eventLists = tmpEventList;
		this.uiMode = mode;
		if (this.uiMode == ManageTerminalNodes.WITH_GUI)
		{
			this.makeDisplayEventList();
			this.setupGUI();
		}
	}

	public void setPosition(int x, int y)
	{
		this.topFrame.setLocation(x, y);
		this.topFrame.repaint();
	}

	public ManageTerminalNodes(int mode)
	{
		this.eventLists = new PopulationOfEventList();
		this.uiMode = mode;
		if (this.uiMode == ManageTerminalNodes.WITH_GUI)
		{
			this.makeDisplayEventList();
			this.setupGUI();
		}
	}

	public ManageTerminalNodes(String fileName)
	{
		this();
		if(!eventLists.readFromFile(fileName)){
			System.err.println("ManageTerinalNodes: given terminal node file has irregular format. Exiting.");
			//System.exit(1);
		}
		//System.err.println
		//("Number of terminal nodes in file is " + eventLists.getNumOfIndividuals() + " in ManageTerminalNodes");
	}
	
    public ManageTerminalNodes(InputStream input)
    {
        this();
        if(!eventLists.readFromFile(input)){
            System.err.println("ManageTerinalNodes: given terminal node file has irregular format. Exiting.");
            //System.exit(1);
        }
        //System.err.println
        //("Number of terminal nodes in file is " + eventLists.getNumOfIndividuals() + " in ManageTerminalNodes");
    }
    
	public ManageTerminalNodes()
	{
		this.eventLists = new PopulationOfEventList();
		this.uiMode = WITHOUT_GUI;
	}

	private void makeDisplayEventList()
	{
		this.displayEventList = new String[this.eventLists.getNumOfIndividuals()];
		int until = this.eventLists.getNumOfIndividuals();
		for (int i = 0; i < until; i++)
		{
			this.displayEventList[i] = new String("Individual " + i);
		}
	}

	private void setupGUI()
	{
		this.topFrame = new JFrame("CACIE Genome Storage");
		
		this.scrollPane = new JScrollPane();
		this.makeDisplayEventList();
		this.listOfIndividuals = new JList(this.displayEventList);
		this.scrollPane.getViewport().setView(this.listOfIndividuals);

		this.topFrame.setLayout(new BorderLayout());
		this.middlePanel = new JPanel();
		this.middlePanel.setLayout(new GridLayout(1, 1));
		this.middlePanel.add(this.scrollPane);
		this.topFrame.add(this.middlePanel, BorderLayout.CENTER);

		this.controllers = new JPanel();
		this.addBut = new JButton("Add");
		this.addBut.setActionCommand("addBut");
		this.addBut.addActionListener(this);
		this.saveBut = new JButton("Save This");
		this.saveBut.setActionCommand("saveBut");
		this.saveBut.addActionListener(this);
		this.saveAllBut = new JButton("Save All");
		this.saveAllBut.setActionCommand("saveAllBut");
		this.saveAllBut.addActionListener(this);
		this.playBut = new JButton("Play");
		this.playBut.setActionCommand("playBut");
		this.playBut.addActionListener(this);
		this.stopBut = new JButton("Stop");
		this.stopBut.setActionCommand("stopBut");
		this.stopBut.addActionListener(this);
		this.removeBut = new JButton("Remove");
		this.removeBut.setActionCommand("removeBut");
		this.removeBut.addActionListener(this);
		this.reInjectBut = new JButton("Re-Inject");
		this.reInjectBut.setActionCommand("reInjectBut");
		this.reInjectBut.addActionListener(this);

		this.controllers.setLayout(new GridLayout(1, 5));
		this.controllers.add(this.addBut);
		this.controllers.add(this.playBut);
		this.controllers.add(this.stopBut);
		this.controllers.add(this.removeBut);
		this.controllers.add(this.saveBut);
		this.controllers.add(this.saveAllBut);
		this.controllers.add(this.reInjectBut);

		this.topFrame.getContentPane().add(this.controllers, BorderLayout.SOUTH);

		this.topFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.topFrame.pack();
		this.topFrame.setLocation(this.x, this.y);
		this.topFrame.setVisible(true);

		
		//this.topFrame.add(this.controllers, BorderLayout.SOUTH);
		// size
		//this.topFrame.setMinimumSize(new Dimension(50, 50) );
	}
	
	public JFrame getTopFrame()
	{
		return topFrame;
	}
	
	public JPanel getTopPanel(){
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(new BorderLayout());
		returnPanel.add(middlePanel, BorderLayout.CENTER);
		returnPanel.add(controllers, BorderLayout.SOUTH);
		returnPanel.setMinimumSize(new Dimension(50, 50) );
		return returnPanel;
	}
	
	public void removeGUI()
	{
		this.topFrame.setVisible(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		String tmpSt = e.getActionCommand();
		if (tmpSt.equals("saveBut"))
		{
			this.saveIndividualWithButton();
		} else if (tmpSt.equals("saveAllBut"))
		{
			this.savePopulationWithButton();
		} else if (tmpSt.equals("playBut"))
		{
			this.playIndividualWithButton();
		} else if (tmpSt.equals("stopBut"))
		{
			this.stopPlayingWithButton();
		} else if (tmpSt.equals("removeBut"))
		{
			this.removeIndividualWithButton();
		} else if (tmpSt.equals("addBut"))
		{
			this.addWithButton();
		} else if (tmpSt.equals("reInjectBut"))
		{
			this.reInjectWithButton();
		}

	}

	public void addWithButton()
	{
		JFileChooser fileChooser;
		String fileName = new String();
		//boolean state = false;

		fileChooser = new JFileChooser();
		if (fileChooser.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File tmpFile = fileChooser.getSelectedFile();
			if (tmpFile != null)
			{
				fileName = tmpFile.getPath();
				if (PopulationOfEventList.isPopulation(fileName))
				{
					this.addPopulation(fileName);
				} else this.addIndividual(fileName);
				this.makeDisplayEventList();
				this.listOfIndividuals = new JList(this.displayEventList);
				this.scrollPane.getViewport().setView(this.listOfIndividuals);
				this.topFrame.repaint();
			}
		}
	}

	protected void setPopulationWindow(OperationWindows opwin)
	{
		this.relationPopWindow = true;
		this.opwin = opwin;
	}

	protected void stopPlaying()
	{
		if (this.CurrentEv != null)
			this.CurrentEv.stopMIDISequence();
		if (this.relationPopWindow)
			this.opwin.stopAll();
	}

	public void stopPlayingWithButton()
	{
		this.stopPlaying();
	}

	public void playIndividualWithButton()
	{
		int index = this.listOfIndividuals.getSelectedIndex();
		if (index != -1)
		{
			this.stopPlaying();
			this.CurrentEv = (CommonEventList) this.eventLists.get(index);
			try {
				this.CurrentEv.playAsMIDISequence( opwin.getTempo() );
			} catch (MidiUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void removeIndividualWithButton()
	{
		int index = this.listOfIndividuals.getSelectedIndex();
		if (index != -1)
		{
			if (this.CurrentEv != null)
				this.CurrentEv.stopMIDISequence();
			if (this.removeIndividual(index))
			{
				this.makeDisplayEventList();
				this.listOfIndividuals = new JList(this.displayEventList);
				this.scrollPane.getViewport().setView(this.listOfIndividuals);
				this.topFrame.repaint();
			}
		}
	}

	public void saveIndividualWithButton()
	{
		int index = this.listOfIndividuals.getSelectedIndex();
		if (index != -1)
		{
			fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION)
			{
				File tmpFile = fileChooser.getSelectedFile();
				if (tmpFile != null)
				{
					String fileName = tmpFile.getPath();
					this.saveIndividual(index, fileName);
				}
			}
		}
	}

	public void reInjectWithButton()
	{
		if (relationPopWindow)
		{
			int index = this.listOfIndividuals.getSelectedIndex();
			if (index != -1)
			{
				this.reInject(index);
			}
		}
	}

	public void savePopulationWithButton()
	{
		fileChooser = new JFileChooser();
		if (fileChooser.showSaveDialog(new JFrame()) == JFileChooser.APPROVE_OPTION)
		{
			File tmpFile = fileChooser.getSelectedFile();
			if (tmpFile != null)
			{
				String fileName = tmpFile.getPath();
				this.savePopulation(fileName);
			}
		}
	}

	public ArrayList<Notes> getNotesOfPopulation()
	{
		int until = eventLists.getNumOfIndividuals();
		ArrayList<Notes> returnArray = new ArrayList<Notes>(until);
		returnArray.ensureCapacity(until);
		for (int i = 0; i < until; i++)
		{
			CommonEventList tmpEventList = (CommonEventList) eventLists.get(i);
			returnArray.add(tmpEventList.getNotes());
		}
		return returnArray;
	}

	public void addPopulation(String fileName)
	{
		PopulationOfEventList tmpEvPop = new PopulationOfEventList();
		if (tmpEvPop.readFromFile(fileName))
			this.addPopulation(tmpEvPop);
		else System.out.println("ManageTerminalNodes: Faild to read Population eventlist");
	}

	public void addPopulation(PopulationOfEventList evPop)
	{
		this.eventLists.add(evPop);
	}

	public void addIndividual(String fileName)
	{
		CommonEventList tmpEventList = new CommonEventList(0);
		if (tmpEventList.readFromFile(fileName))
			this.addIndividual(tmpEventList);
		else System.out.println("ManageTerminalNodes: Faild to read Individual eventlist");
	}

	public void reInject(int index)
	{
		CommonEventList tmpEv = (CommonEventList) this.eventLists.get(index);
		this.opwin.reInject(tmpEv);
	}

	public void addIndividual(CommonEventList tmpEventList)
	{
		this.eventLists.add(tmpEventList);
		if (this.uiMode == WITH_GUI)
		{
			this.makeDisplayEventList();
			this.listOfIndividuals = new JList(this.displayEventList);
			this.scrollPane.getViewport().setView(this.listOfIndividuals);
			this.topFrame.repaint();
		}
	}

	public void saveIndividual(int index, String fileName)
	{
		CommonEventList tmpEventList = (CommonEventList) this.eventLists.get(index);
		tmpEventList.setIDNumber(index);
		if (!tmpEventList.writeToFile(fileName))
			System.out.println("ManageTerminalNodes: Faild to write eventlist");
		if (!tmpEventList.saveAsMIDISequence(fileName + ".mid", this.opwin.getTempo()) )
			System.out.println("ManageTerminalNodes: Faild to write SMF");
	}

	public void savePopulation(String fileName)
	{
		if (!this.eventLists.writeToFile(fileName))
			System.out.println("ManageTerminalNodes: Faild to write eventlist");
	}

	public boolean removeIndividual(int index)
	{
		int limit = this.eventLists.getNumOfIndividuals();
		boolean returnValue = true;
		if (index < limit && index >= 0)
			this.eventLists.remove(index);
		else returnValue = false;
		return returnValue;
	}
	
	public Point getFramePoint()
	{
		return this.topFrame.getLocation();
	}

	public int getFrameWidth()
	{
		return this.topFrame.getWidth();
	}
}