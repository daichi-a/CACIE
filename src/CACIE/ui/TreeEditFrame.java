package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;


public class TreeEditFrame {

	JFrame topFrame;
	OperationWindows opwin;
	int positionX = 0, positionY = 0;
	TreeEditPanel treeEditPanel;

	TreeEditFrame(OperationWindows operationWindows){
		opwin = operationWindows;
		topFrame = new JFrame("CACIE Manual Crossover Editor");
		initTreeEditPanel();
	}
	
	public void setPosition(int x, int y){
		positionX = x;
		positionY = y;
		topFrame.setLocation(positionX, positionY);
		topFrame.repaint();
	}
	
	public TreeEditPanel getTreeEditPanel(){
		return treeEditPanel;
	}
	
	private void initTreeEditPanel()
	  {
	    // make tree edit panel
	    treeEditPanel = new TreeEditPanel(opwin);
	    //int positionX = opwin.getFramePoint().x + opwin.getFrameWidth();
	    //int positionY = this.genomeStorage.getFramePoint().y;
	    treeEditPanel.setLocation(positionX, positionY);
	    treeEditPanel.setVisible(true);

	    // add to frame
	    //JPanel bottomPanel = new JPanel();
	    //bottomPanel.setLayout(new GridLayout(1, 2));
	    //JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
	    //bottomSplitPane.setRightComponent(treeEditPanel);
	    //bottomSplitPane.setLeftComponent(opwin.genomeStorage.getTopFrame());
	    //bottomPanel.add(bottomSplitPane);

	    topFrame.add(treeEditPanel);
	    
		topFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		topFrame.pack();
		topFrame.setLocation(this.positionX, this.positionY);
		topFrame.setVisible(true);
	  }
	
		
}
