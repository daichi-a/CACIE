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
import CACIE.genome.Population;
import CACIE.genome.DistanceCalculator;

public class OperationWindows implements ActionListener, ChangeListener
{
  private static int REPRODUCE = 1;

  private static int REPLACE = 2;

  private static int INITIALIZE = 3;

  private static int REINJECT = 4;

  // PopulationWindow
  JFrame topFrame;

  JScrollPane ThisScrollPane, NextScrollPane;

  JButton PWreplace, PWreproduct, PWinitPopulation;

  ArrayList fitnessValues;

  JPanel PWfitnessValues;

  // tanji
  JSlider tempoBar;
  JTextField tempoText;
  
  //
  JComboBox instrumentBox;
  int instrumentNumber;

  private TreeEditPanel _treeEditPanel;

  public int populationSize;

  protected Population thePopulation;

  protected Individual theIndividual;

  protected String genomeType;

  protected ArrayList<CommonEventList> cashEvThis, cashEvNext;

  public ArrayList<EvaluatingIndividual> DistInds;

  protected ArrayList<ListeningIndividual> ListInds;

  protected JPanel listNextGene;

  protected ManageTerminalNodes genomStocker;

  protected ArrayList operationList;

  protected ArrayList notes;

  public OperationWindows(int popSize, ArrayList notes, ArrayList<String> oprList, ArrayList<String> confList)
  {
    genomeType = "TREE";
    operationList = oprList;
    this.notes = notes;

    populationSize = popSize;
    thePopulation = new Population(populationSize, genomeType, 0, notes, oprList, confList);

    setUpGUI();

    thePopulation.initPopulation();
    cashEvThis = new ArrayList<CommonEventList>(this.populationSize);
    cashEvNext = new ArrayList<CommonEventList>(this.populationSize);
    for (int i = 0; i < populationSize; i++)
    {
      cashEvThis.add(thePopulation.convertToEventList(0, i));
    }
    this.setListThisGeneration(cashEvThis);
    this.setListNextGeneration(cashEvThis);

    this.genomStocker = new ManageTerminalNodes(ManageTerminalNodes.WITH_GUI);
    this.genomStocker.setPopulationWindow(this);

    // tree edit panel
    this.initTreeEditPanel();
    this.topFrame.setVisible(true);
    this.topFrame.pack();
    instrumentNumber = 0;
  }

  public OperationWindows()
  {
    this.populationSize = 16;
    this.setUpGUI();
  }

  private void initTreeEditPanel()
  {
    // make tree edit panel
    _treeEditPanel = new TreeEditPanel(this);
    int positionX = this.genomStocker.getFramePoint().x + this.genomStocker.getFrameWidth();
    int positionY = this.genomStocker.getFramePoint().y;
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

  private void setUpGUI()
  {
    this.DistInds = new ArrayList<EvaluatingIndividual>(this.populationSize);
    this.ListInds = new ArrayList<ListeningIndividual>(this.populationSize);
    this.setUpPopulationWindowGUI();
  }

  private void setUpPopulationWindowGUI()
  {
    this.topFrame = new JFrame("Population Window");

    JPanel listThisGene = new JPanel();
    listThisGene.setLayout(new GridLayout(this.populationSize, 1));
    // listThisGene.setSize(300, 300);
    for (int i = 0; i < this.populationSize; i++)
    {
      EvaluatingIndividual tmpEvInd = new EvaluatingIndividual(new CommonEventList(0), this, i);
      listThisGene.add(tmpEvInd);
      DistInds.add(tmpEvInd);
    }
    this.listNextGene = new JPanel();
    listNextGene.setLayout(new GridLayout(this.populationSize, 1));
    // listNextGene.setSize(200, 300);
    for (int i = 0; i < this.populationSize; i++)
    {
      ListeningIndividual tmpLsInd = new ListeningIndividual(new CommonEventList(0), this, i);
      listNextGene.add(tmpLsInd);
      this.ListInds.add(tmpLsInd);
    }

    this.ThisScrollPane = new JScrollPane();
    this.NextScrollPane = new JScrollPane();
    this.ThisScrollPane.getViewport().setView(listThisGene);
    // this.NextScrollPane.getViewport().setView(listNextGene);
    this.NextScrollPane.getViewport().setView(new JPanel());
    JPanel individualPanel = new JPanel();
    individualPanel.setLayout(new GridLayout(1, 2));
    individualPanel.add(this.ThisScrollPane);
    individualPanel.add(this.NextScrollPane);
    ThisScrollPane.setPreferredSize(new Dimension(500, 250));

    // upperPanel
    JPanel upperPanel = new JPanel();
    upperPanel.setLayout(new BorderLayout());
    this.topFrame.getContentPane().setLayout(new GridLayout(2, 1));
    upperPanel.add(individualPanel);

    JPanel controllers = new JPanel();
    JButton initBut = new JButton("Init");
    initBut.setActionCommand("initBut");
    initBut.addActionListener(this);
    JButton reproduceBut = new JButton("Reproduce");
    reproduceBut.setActionCommand("reproduceBut");
    reproduceBut.addActionListener(this);
    JButton replaceBut = new JButton("Replace");
    replaceBut.setActionCommand("replaceBut");
    replaceBut.addActionListener(this);
    JButton stopBut = new JButton("Stop");
    stopBut.setActionCommand("stopBut");

    JButton readBut = new JButton("Read");
    readBut.setActionCommand("readBut");
    readBut.addActionListener(this);

    tempoBar = new JSlider(10, 200);
    tempoBar.setValue(CommonEventList.DT);
    tempoBar.addChangeListener(this);
    tempoText = new JTextField("Tempo:" + CommonEventList.DT + "  ");
    tempoText.setEditable(false);
    stopBut.addActionListener(this);
    controllers.setLayout(new FlowLayout());
    controllers.add(initBut);
    controllers.add(reproduceBut);
    controllers.add(replaceBut);
    controllers.add(stopBut);
    controllers.add(readBut);
    controllers.add(tempoBar);
    controllers.add(tempoText);

    // this.topFrame.getContentPane().add(controllers, BorderLayout.SOUTH);
    upperPanel.add(controllers, BorderLayout.SOUTH);
    this.topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.topFrame.add(upperPanel);
    System.out.println(upperPanel.getPreferredSize());

    // this.topFrame.setVisible(true);
    // this.topFrame.pack();
    // this.topFrame.setSize(500, 600);
  }

  public void actionPerformed(ActionEvent e)
  {
    String aes = e.getActionCommand();
    if (aes.equals("reproduceBut"))
    {
      this.reproduce();
    } else if (aes.equals("replaceBut"))
    {
      this.replace();
    } else if (aes.equals("initBut"))
    {
      this.init();
    } else if (aes.equals("stopBut"))
    {
      this.stopAll();
    }

    else if (aes.substring(0, 4).equals("Store"))
    {
      StringTokenizer tmpStkn = new StringTokenizer(aes, "_");
      tmpStkn.nextToken();
      System.out.println(tmpStkn.nextToken());
      System.out.println(tmpStkn.nextToken());
    }
  }

  public void init()
  {
    this.thePopulation.initPopulation();
    this.cashEvThis.clear();
    this.cashEvThis.ensureCapacity(this.populationSize);
    for (int i = 0; i < this.populationSize; i++)
    {
      cashEvThis.add(thePopulation.convertToEventList(0, i));
    }
    makeIndividualList(INITIALIZE);

  }

  public void reproduce_and_replace()
  {
    reproduce();
    replace();
  }

  public void reproduce()
  {
    thePopulation.refreshEvaluated();
    for (int i = 0; i < this.populationSize; i++)
    {
      EvaluatingIndividual evlInd = (EvaluatingIndividual) DistInds.get(i);
      thePopulation.evaluate(i, evlInd.getFitnessValue());
    }
    thePopulation.reproductPopulation();

    this.cashEvNext.clear();
    this.cashEvNext.ensureCapacity(this.populationSize);
    for (int i = 0; i < this.populationSize; i++)
    {
      cashEvNext.add(thePopulation.convertToEventList(1, i));
    }
    makeIndividualList(REPRODUCE);
  }

  public void replace()
  {
    thePopulation.replacePopulation();
    this.cashEvNext.clear();
    this.cashEvThis.clear();
    this.cashEvThis.ensureCapacity(this.populationSize);
    for (int i = 0; i < this.populationSize; i++)
    {
      cashEvThis.add(thePopulation.convertToEventList(0, i));
    }
    makeIndividualList(REPLACE);
  }

  private void makeIndividualList(int mode)
  {
    if (mode == REPRODUCE)
    {
      this.NextScrollPane.getViewport().setView(this.listNextGene);
      setListNextGeneration(this.cashEvNext);
    } else if (mode == REPLACE)
    {
      setListThisGeneration(this.cashEvThis);
      this.NextScrollPane.getViewport().setView(new JPanel());
    } else if (mode == INITIALIZE)
    {
      setListThisGeneration(this.cashEvThis);
      this.NextScrollPane.getViewport().setView(new JPanel());
    } else if (mode == REINJECT)
    {
      setListThisGeneration(this.cashEvThis);
    }
    this.topFrame.repaint();
  }

  public void reInject(CommonEventList eventList)
  {
    thePopulation.refreshEvaluated();
    int temporaryFitness[] = new int[this.populationSize];
    for (int i = 0; i < this.populationSize; i++)
    {
      EvaluatingIndividual evlInd = (EvaluatingIndividual) DistInds.get(i);
      int tmpFitness = evlInd.getFitnessValue();
      thePopulation.evaluate(i, tmpFitness);
      temporaryFitness[i] = tmpFitness;
    }
    int indexToApply = thePopulation.reInject(eventList, -1);
    CommonEventList tmpEv = thePopulation.convertToEventList(0, indexToApply);
    this.cashEvThis.remove(indexToApply);
    this.cashEvThis.add(indexToApply, tmpEv);
    makeIndividualList(REINJECT);
    for (int i = 0; i < this.populationSize; i++)
    {
      EvaluatingIndividual evlInd = (EvaluatingIndividual) DistInds.get(i);
      if (i != indexToApply)
      {
        evlInd.setSliderOfFitness(temporaryFitness[i]);
      } else
      {
        evlInd.setSliderOfFitness(50);
      }
    }
    System.out.println("OperationWindows : finish Re-Inject : Replace to " + indexToApply);
  }

  public void sendStore(CommonEventList evt)
  {
    this.genomStocker.addIndividual(evt);
  }

  public void setListThisGeneration(ArrayList eventListSet)
  {
    for (int i = 0; i < this.populationSize; i++)
    {
      EvaluatingIndividual evlInd = (EvaluatingIndividual) this.DistInds.get(i);
      CommonEventList tmpEventList = (CommonEventList) eventListSet.get(i);
      if (tmpEventList == null)
        System.out.println("OperationWindows:setListThisGeneration: Cannot find " + i + "'th eventlist");
      evlInd.setEventList((CommonEventList) eventListSet.get(i));
    }
  }

  public void setListNextGeneration(ArrayList eventlistSet)
  {
    int until = this.ListInds.size();
    for (int i = 0; i < until; i++)
    {
      ListeningIndividual lstInd = (ListeningIndividual) this.ListInds.get(i);
      lstInd.setEventList((CommonEventList) eventlistSet.get(i));
    }
  }

  public void stopAll()
  {
    int until = cashEvThis.size();
    for (int i = 0; i < until; i++)
    {
      CommonEventList tmpEventList = (CommonEventList) cashEvThis.get(i);
      tmpEventList.stopMIDISequence();
    }
    until = cashEvNext.size();
    for (int i = 0; i < until; i++)
    {
      CommonEventList tmpEventList = (CommonEventList) cashEvNext.get(i);
      tmpEventList.stopMIDISequence();
    }
  }

  public Motif_simpleTree_Individual getIndividual(int index)
  {
    return (Motif_simpleTree_Individual) this.thePopulation.getIndividual(0, index);
  }

  public Population getPopulation()
  {
    return this.thePopulation;
  }

  public int getPopulationSize()
  {
    return populationSize;
  }

  public ArrayList getOperationList()
  {
    return operationList;
  }

  public ArrayList getNotes()
  {
    return notes;
  }

  public int getTempo()
  {
    return tempoBar.getValue();
  }

  public void treeSelect(TreeModel tree, int index)
  {
    this._treeEditPanel.selectTree(tree, index);
  }

  // implementation from changeListener method
  public void stateChanged(ChangeEvent ce)
  {
    tempoText.setText("Tempo:" + String.valueOf(tempoBar.getValue()));
    this.topFrame.repaint();
  }

  public EvaluatingIndividual reflesh(int index)
  {
    Motif_simpleTree_Individual individual = this.getIndividual(index);
    // individual = (Motif_simpleTree_Individual)
    // this.thePopulation.getIndividual(0, index);
    // individual = (Motif_simpleTree_Individual)
    // this.thePopulation.getIndividual(0, index);
    System.out.println("individual = " + individual);
    individual.run();
    System.out.println("eventList = " + individual.getEventList());
    System.out.println(cashEvThis);
    cashEvThis.set(index, individual.getEventList());
    CommonEventList eventList = (CommonEventList) this.cashEvThis.get(index);
    ((EvaluatingIndividual) this.DistInds.get(index)).setEventList(eventList);
    // this.treeSelect(
    // ((EvaluatingIndividual)DistInds.get(index)).getTreeModel( individual
    // ), index );
    return ((EvaluatingIndividual) this.DistInds.get(index));
  }

  public double[][] getDistances()
  {
    double[][] distancesWithZero = DistanceCalculator.getDistanceMatrix(getPopulation(), getPopulationSize());
    double[][] distancesMatrixNoZero = new double[populationSize][populationSize];
    for (int i = 0; i < populationSize; i++)
    {
      for (int j = 0; j < populationSize; j++)
      {
        if (j < i)
          distancesMatrixNoZero[i][j] = distancesWithZero[j][i];
        else if (i == j)
          distancesMatrixNoZero[i][j] = 0.0;
        else
          distancesMatrixNoZero[i][j] = distancesWithZero[i][j];
      }
    }

    // テスト用プリント
    // for(int i=0; i<populationSize; i++){
    // for(int j=0; j<populationSize; j++){
    // System.err.print(distancesMatrixNoZero[i][j] + ",");
    // }
    // System.err.println();
    // }
    return distancesMatrixNoZero;
  }

}