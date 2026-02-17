package CACIE;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JFileChooser;

import CACIE.genome.Notes;
import CACIE.ui.ConfigFile;
import CACIE.ui.ManageTerminalNodes;
import CACIE.ui.OperationWindows_GUI_Traditional;

public class CACIE_O {
	static {
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	}

	public static MidiDevice externalDevice;

	public static int indexOfDevice;
	protected static ArrayList<String> oprList;
	protected static ArrayList<String> confList;
	protected static ArrayList<Notes> notes;

	public static void main(String Args[]) {
		configures(Args);
		new OperationWindows_GUI_Traditional(16, notes, oprList, confList);
	}
	
    protected static void configures(String configFile, String dataFile){
      setSEED();
      setupMiDiDevice();
      String arg0;
      String arg1;
      //InputStream configStream = CACIE_O.class.getResourceAsStream("./ConfigData/NHK_Yasu_2nd_config.config");
      //InputStream dataStream = CACIE_O.class.getResourceAsStream("./ConfigData/demo_nhk_diatonic_term.data");
      //System.out.println(configStream);
      //System.out.println(dataStream);
      System.out.println("Here = " + CACIE_O.class.getResource("../"));
      
      //ArrayList<ArrayList<String>> configList = ConfigFile.readParametersFromFile(configStream);
      ArrayList<ArrayList<String>> configList = ConfigFile.readParametersFromFile(configFile);
      oprList = configList.get(0);
      confList = configList.get(1);
      for (int i = 0; i < oprList.size(); i++) {
          String tmpSt = (String) oprList.get(i);
          System.out.print(tmpSt + " ");
      }
      System.out.println();
      ManageTerminalNodes termNodes = new ManageTerminalNodes(dataFile);
      //ManageTerminalNodes termNodes = new ManageTerminalNodes(dataStream);
      notes = termNodes.getNotesOfPopulation();
      //System.err.println("Number of terminal nodes in file is " + notes.size());
  }
    
	protected static void configures(String Args[]){
		setSEED();
		setupMiDiDevice();
		String arg0;
		String arg1;
		//InputStream configStream = CACIE_O.class.getResourceAsStream("./ConfigData/NHK_Yasu_2nd_config.config");
		//InputStream dataStream = CACIE_O.class.getResourceAsStream("./ConfigData/demo_nhk_diatonic_term.data");
		//System.out.println(configStream);
		//System.out.println(dataStream);
		System.out.println("Here = " + CACIE_O.class.getResource("../"));
		if (Args.length < 2) {
		  arg0 = configFileSelector("Please Select Config File.");
		  arg1 = configFileSelector("Please Select Terminal Node File.");
		} else {
			arg0 = Args[0];
			arg1 = Args[1];
		}
		//ArrayList<ArrayList<String>> configList = ConfigFile.readParametersFromFile(configStream);
		ArrayList<ArrayList<String>> configList = ConfigFile.readParametersFromFile(arg0);
		oprList = configList.get(0);
		confList = configList.get(1);
		for (int i = 0; i < oprList.size(); i++) {
			String tmpSt = (String) oprList.get(i);
			System.out.print(tmpSt + " ");
		}
		System.out.println();
		ManageTerminalNodes termNodes = new ManageTerminalNodes(arg1);
		//ManageTerminalNodes termNodes = new ManageTerminalNodes(dataStream);
		notes = termNodes.getNotesOfPopulation();
		//System.err.println("Number of terminal nodes in file is " + notes.size());
	}
	
	protected static void setupMiDiDevice() {
		MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();

//		System.err.println("uhawwwwwwwwwwww okwwwwwwww");
//		System.err.println("There are " + info.length + " devices");
		for (int i = 0; i < info.length; i++) {
			System.err.println(i + ": " + info[i].getName() + ", "
					+ info[i].getDescription());
		}
//		System.err.println("uhawwwwwwwwwwww okwwwwwwww");

		InputStreamReader ris = new InputStreamReader(System.in);
		BufferedReader dis = new BufferedReader(ris);
//		System.err.println("Please input device number. Use internal synthesizer with no input");
		String line = "";
//		try {
//			line = dis.readLine();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		if (line.equals(""))
			indexOfDevice = -1;
		else
			indexOfDevice = Integer.parseInt(line);

		if (indexOfDevice != -1) {
			try {
				externalDevice = MidiSystem.getMidiDevice(info[indexOfDevice]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (externalDevice == null) {
				System.err
						.println("Selected Device is not working. Using default device");
				indexOfDevice = -1;
			}

			sendSoundLogo(externalDevice);
		}
	}

	protected static void sendSoundLogo(MidiDevice device) {
		Sequencer sequencer = null;
		try {
			device.open();
			if (indexOfDevice == -1)
				sequencer = (Sequencer) MidiSystem.getSequencer();
			else {
				sequencer = (Sequencer) MidiSystem.getSequencer(false); //falseが効かなくなっている
				//sequencer = (Sequencer) MidiSystem.getSequencer(); //これだといく2重再生の可能性あり
				sequencer.getTransmitter().setReceiver(device.getReceiver());
			}

			Sequence seq = null;
			seq = new Sequence(Sequence.PPQ, 240);
			Track track = seq.createTrack();

			MetaMessage tempo = new MetaMessage();
			tempo.setMessage(0x51, new byte[] { 0x07, (byte) 0xa1, 0x20 }, 3);
			track.add(new MidiEvent(tempo, 0));

			ShortMessage noteOn1 = new ShortMessage();
			noteOn1.setMessage(ShortMessage.NOTE_ON, 60, 64);
			track.add(new MidiEvent(noteOn1, 0));

			ShortMessage noteOn2 = new ShortMessage();
			noteOn2.setMessage(ShortMessage.NOTE_OFF, 60, 0);
			track.add(new MidiEvent(noteOn2, 480));

			sequencer.setSequence(seq);
			sequencer.open();
			sequencer.start();
			while (sequencer.isRunning())
				Thread.sleep(100);

			device.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void setSEED() {
		int seedNumber = -1;
		InputStreamReader ris = new InputStreamReader(System.in);
		BufferedReader dis = new BufferedReader(ris);

		//System.err.println("Uha wwww OK wwww");
		//System.err.println("Please input SEED Number. Using time with no input.");

		String line = "";
		//try {
			//line = dis.readLine();
		//} catch (Exception e) {
//			e.printStackTrace();
		//}
		if (line.equals(""))
		  seedNumber = (int) System.currentTimeMillis();
		else
		  seedNumber = Integer.parseInt(line);
		RandomManager.setSeed(seedNumber);
	}

	protected static String configFileSelector(String message){
		System.err.println(message);
		String filePath = "";
		JFileChooser fc = new JFileChooser(".");
		int result = fc.showOpenDialog(null);
		if(result == JFileChooser.APPROVE_OPTION)
			filePath = fc.getSelectedFile().getPath();
		else if(result == JFileChooser.CANCEL_OPTION){
			System.err.println("Canceled. Exiting.");
			System.exit(0);
		}
		else{
			System.err.println("Error. Exiting.");
			System.exit(1);
		}
		return filePath;
	}
}
