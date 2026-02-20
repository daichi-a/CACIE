package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeModel;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.Individual;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.Notes;
import CACIE.genome.Population;
import CACIE.genome.DistanceCalculator;

public class BreedingWindow extends OperationWindows implements ActionListener, ChangeListener {
    JFrame topFrame;
    JScrollPane ThisScrollPane, NextScrollPane;
    JButton PWreplace, PWreproduct, PWinitPopulation;

    // tanji
    JSlider tempoBar;
    JTextField tempoText;
    
    JComboBox instrumentBox;
    int instrumentNumber;

    private TreeEditPanel _treeEditPanel;
    private BreedingPanel breedingPanel;

    public int populationSize;

    protected Population population;
    protected String genomeType;

    protected ArrayList<CommonEventList> currentEventList;

    protected ManageTerminalNodes genomeStorage;

    public BreedingWindow(int popSize, ArrayList<Notes> notes, ArrayList<String> oprList, ArrayList<String> confList){
        genomeType = "TREE";

        populationSize = popSize;
        population = new Population(populationSize, genomeType, 0, notes, oprList, confList);

        population.initPopulation();
        currentEventList = new ArrayList<CommonEventList>(this.populationSize);
        for (int i = 0; i < populationSize; i++)
        {
            currentEventList.add(population.convertToEventList(0, i));
        }

        genomeStorage = new ManageTerminalNodes(ManageTerminalNodes.WITH_GUI);
        genomeStorage.setPopulationWindow(this);

        setupGUI();
        // tree edit panel
        this.initTreeEditPanel();
        instrumentNumber = 0;
    }

    private void initTreeEditPanel()
    {
        // make tree edit panel
        _treeEditPanel = new TreeEditPanel(this);
        int positionX = genomeStorage.getFramePoint().x + genomeStorage.getFrameWidth();
        int positionY = genomeStorage.getFramePoint().y;
        this._treeEditPanel.setLocation(positionX, positionY);
        _treeEditPanel.setVisible(true);

        // add to frame
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 2));
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        bottomSplitPane.setRightComponent(_treeEditPanel);
        bottomSplitPane.setLeftComponent(this.genomStocker.getTopPanel());
        bottomPanel.add(bottomSplitPane);

        this.topFrame.add(bottomPanel);
    }
    
    private void setupGUI(){
        topFrame = new JFrame("CACIE Breeding");
        topFrame.setVisible(true);
        topFrame.pack();
        topFrame.setSize(1000, 700);
        
        // Create and add the BreedingPanel
        breedingPanel = new BreedingPanel();
        topFrame.add(breedingPanel, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e){

    }

    public void stateChanged(ChangeEvent e){

        
    }

    public void init(){
        population.initPopulation();
        currentEventList.clear();
        currentEventList.ensureCapacity(this.populationSize);
        for (int i = 0; i < this.populationSize; i++)
        {
            currentEventList.add(population.convertToEventList(0, i));
        }
    }
}