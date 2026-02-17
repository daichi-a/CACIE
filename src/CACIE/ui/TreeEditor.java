package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import CACIE.genome.MotifSimpleTreeNode;
import CACIE.genome.MotifSimpleTreeOperator;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.TreeNodes;
import CACIE.genome.TreeOperators;
import CACIE.genome.TreeIndividuals;

public class TreeEditor extends JPanel implements ActionListener, MouseListener
{
  private TreeGraph _treeGraph;
  private OperationWindows _operationWindows;
  private int _individualIndex;
  private Motif_simpleTree_Individual _individual;

  // --- gui ---
  private JButton _btnRemove;
  private JButton _btnSave;
  private JMenu _symbolMenu;
  private JMenuItem[] _symbolItems;

  public TreeEditor(OperationWindows operationWindow, int index)
  {
    _operationWindows = operationWindow;
    _individualIndex = index;
    _individual = _operationWindows.getIndividual(index);
    _treeGraph = new TreeGraph(EvaluatingIndividual.getTreeModel(_individual));

    initGUI();
  }

  private void initGUI()
  {
    this.setLayout(new BorderLayout());
    this.add(new JLabel("Genom Tree Editor"), BorderLayout.NORTH);

    // menu test
    JMenuBar menuBar = new JMenuBar();
    _symbolMenu = new JMenu("Replace");
    menuBar.add(_symbolMenu);
    _symbolItems = new JMenuItem[_operationWindows.getOperationList().size() + _operationWindows.getNotes().size()];
    for (int i = 0; i < _operationWindows.getNotes().size(); i++)
    {
      _symbolItems[i] = new JMenuItem("Symbol " + ": " + i);
      _symbolItems[i].addActionListener(this);
      _symbolMenu.add(_symbolItems[i]);
    }
    for (int i = 0; i < _operationWindows.getOperationList().size(); i++)
    {
      _symbolItems[i] = new JMenuItem("Symbol " + ": " + _operationWindows.getOperationList().get(i).toString());
      _symbolItems[i].addActionListener(this);
      _symbolMenu.add(_symbolItems[i]);
    }

    // center
    _treeGraph.addMouseListener(this);
    this.add(_treeGraph, BorderLayout.CENTER);

    // east
    _btnRemove = new JButton("Remove");
    _btnRemove.addActionListener(this);
    _btnSave = new JButton("Save");
    _btnSave.addActionListener(this);

    JPanel editMenuPanel = new JPanel();
    editMenuPanel.setLayout(new FlowLayout());
    editMenuPanel.add(_btnRemove);
    editMenuPanel.add(menuBar);
    editMenuPanel.add(_btnSave);
    editMenuPanel.setPreferredSize(new Dimension(100, 200));
    this.add(editMenuPanel, BorderLayout.EAST);

    // size
    this.setMinimumSize(new Dimension((int) _treeGraph.getPreferredSize().getWidth() + (int) editMenuPanel.getPreferredSize().getWidth() + 50, (int) _treeGraph.getPreferredSize().getHeight()));
    this.setPreferredSize(new Dimension((int) _treeGraph.getPreferredSize().getWidth() + (int) editMenuPanel.getPreferredSize().getWidth() + 50, (int) _treeGraph.getPreferredSize().getHeight()));
  }

  // ----------------- listener methods --------------------
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().startsWith("Symbol"))
    {
      String menuString = e.getActionCommand();
      symbolChangeOperation(menuString.substring(menuString.indexOf(":") + 1).trim());
    } else if (e.getSource() == this._btnRemove) // remove operation
    {
      removeOperation();
    } else if (e.getSource() == this._btnSave)
    {
      saveOperation();
    }
  }

  private void removeOperation()
  {
    if (_treeGraph == null || _treeGraph.getSelectedNode() == null)
      return;
    ArrayList<String> configArray = _individual.getConfigArray();
    ArrayList genom = _individual.getGenomeArray();
    int removeIndex = _individual.getIndex((TreeNodes) ((DefaultMutableTreeNode) _treeGraph.getSelectedNode().getTreeNode()).getUserObject());
    ArrayList newGenom = new ArrayList(genom.size());

    for (int i = 0; i < removeIndex; i++)
    {
      newGenom.add(genom.get(i));
    }
    int sumOfStackCount = _individual.getStackCount(removeIndex);
    while (sumOfStackCount != 1)
    {
      removeIndex++;
      sumOfStackCount += _individual.getStackCount(removeIndex);
    }
    TreeNodes replaceNode = MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, TreeIndividuals.MONOPHONY_MODE, _operationWindows.getNotes().size(), configArray);
    newGenom.add(replaceNode);

    for (int i = removeIndex + 1; i < genom.size(); i++)
    {
      newGenom.add(genom.get(i));
    }
    _individual.setGenomArray(newGenom);
    _treeGraph.setTreeModel(EvaluatingIndividual.getTreeModel(_individual));
    repaint();
  }

  private void exchangeOperation()
  {

  }

  private void symbolChangeOperation(String symbolName)
  {
    if (_treeGraph == null || _treeGraph.getSelectedNode() == null)
      return;

    ArrayList<String> configArray = _individual.getConfigArray();
    ArrayList genom = _individual.getGenomeArray();
    int selectedIndex = _individual.getIndex((TreeNodes) ((DefaultMutableTreeNode) _treeGraph.getSelectedNode().getTreeNode()).getUserObject());

    int sumOfStackCount = _individual.getStackCount(selectedIndex);
    while (sumOfStackCount != 1)
    {
      System.out.println("plus = " + sumOfStackCount);
      sumOfStackCount += _individual.getStackCount(selectedIndex + 1);
      genom.remove(selectedIndex + 1);
    }

    TreeNodes replaceNode = new TreeNodes();
    System.out.println(symbolName);
    if (symbolName.matches("\\d+")) // terminal node
    {
      replaceNode.setData(Integer.parseInt(symbolName));
      replaceNode.setStackCount(1);
      replaceNode.setTermOrNot(TreeNodes.TERMINAL);
      replaceNode.setHasExtraArg(false);
      replaceNode.setOperatorMode(0);
      genom.set(selectedIndex, replaceNode);
    } else
    // nonterminal node
    {
      MotifSimpleTreeOperator operator = new MotifSimpleTreeOperator(_operationWindows.getOperationList(), symbolName);
      replaceNode.setData(operator.getOperator());
      replaceNode.setTermOrNot(TreeNodes.NONTERMINAL);
      if (operator.hasExtraArg() == true)
      {
        replaceNode.setHasExtraArg(true);
        replaceNode.setExtraArg(operator.getExtraArg());
      }
      genom.set(selectedIndex, replaceNode);
      for (int i = 0; i < -replaceNode.getStackCount() + 1; i++)
      {
        TreeNodes child = MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, TreeIndividuals.MONOPHONY_MODE, _operationWindows.getNotes().size(), configArray);
        genom.add(selectedIndex + 1, child);
      }
    }

    _treeGraph.setTreeModel(EvaluatingIndividual.getTreeModel(_individual));
    repaint();
  }

  private void saveOperation()
  {
    _operationWindows.reflesh(_individualIndex);
  }

  // public void actionPerformed(ActionEvent e)
  // {
  // if (e.getSource() == this._btnRemove) // remove operation
  // {
  // if (_treeGraph == null || _treeGraph.getSelectedNode() == null)
  // return;
  // Motif_simpleTree_Individual individual =
  // _operationWindows.getIndividual(_individualIndex);
  // ArrayList genom = individual.getGenomArray();
  // int removeIndex = individual.getIndex((TreeNodes)
  // ((DefaultMutableTreeNode)
  // _treeGraph
  // .getSelectedNode().getTreeNode()).getUserObject());
  // ArrayList newGenom = new ArrayList(genom.size());
  //
  // for (int i = 0; i < removeIndex; i++)
  // {
  // newGenom.add(genom.get(i));
  // }
  // int sumOfStackCount = individual.getStackCount(removeIndex);
  // while (sumOfStackCount != 1)
  // {
  // removeIndex++;
  // sumOfStackCount += individual.getStackCount(removeIndex);
  // }
  // TreeNodes replaceNode =
  // MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, 0,
  // _operationWindows
  // .getNotes().size());
  // newGenom.add(replaceNode);
  //
  // for (int i = removeIndex + 1; i < genom.size(); i++)
  // {
  // newGenom.add(genom.get(i));
  // }
  // individual.setGenomArray(newGenom);
  // _treeGraph.setTreeModel(this._operationWindows.reflesh(_individualIndex).getTreeModel(individual));
  // repaint();
  // } else if (e.getSource() == this._btnExchange) // subtree exchange
  // {
  // // exchange
  // Motif_simpleTree_Individual individualA =
  // _operationWindows.getIndividual(_individualIndex);
  // Motif_simpleTree_Individual individualB =
  // _operationWindows.getIndividual(_indexB);
  // ArrayList genomA = new ArrayList(individualA.getNumOfNodes());
  // ArrayList genomB = new ArrayList(individualB.getNumOfNodes());
  //
  // int selectedIndexA = individualA.getIndex((TreeNodes)
  // ((DefaultMutableTreeNode) selectedA
  // .getTreeNode()).getUserObject());
  // int sumOfStackCountA = 0;
  // int selectedIndexB = individualB.getIndex((TreeNodes)
  // ((DefaultMutableTreeNode) selectedB
  // .getTreeNode()).getUserObject());
  // int sumOfStackCountB = 0;
  //
  // // first, add own nodes
  // for (int i = 0; i < selectedIndexA; i++)
  // {
  // genomA.add(individualA.getNode(i));
  // }
  // for (int i = 0; i < selectedIndexB; i++)
  // {
  // genomB.add(individualB.getNode(i));
  // }
  //
  // // second, exchange subtree
  // int subtreeIndexA = selectedIndexA;
  // while (sumOfStackCountA != 1) // until complete subtree
  // {
  // sumOfStackCountA += individualA.getStackCount(subtreeIndexA);
  // genomB.add(individualA.getNode(subtreeIndexA));
  // subtreeIndexA++;
  // }
  // int subtreeIndexB = selectedIndexB;
  // while (sumOfStackCountB != 1) // until complete subtree
  // {
  // sumOfStackCountB += individualB.getStackCount(subtreeIndexB);
  // genomA.add(individualB.getNode(subtreeIndexB));
  // subtreeIndexB++;
  // }
  // // last, fill remainder
  // for (int i = subtreeIndexA; i < individualA.getNumOfNodes(); i++)
  // {
  // genomA.add(individualA.getNode(i));
  // }
  // for (int i = subtreeIndexB; i < individualB.getNumOfNodes(); i++)
  // {
  // genomB.add(individualB.getNode(i));
  // }
  //
  // individualA.setGenomArray(genomA);
  // individualB.setGenomArray(genomB);
  // _treeB.setTreeModel(this._operationWindows.reflesh(_indexB).getTreeModel(individualB));
  // _treeGraph.setTreeModel(this._operationWindows.reflesh(_individualIndex).getTreeModel(individualA));
  // repaint();
  // } else if (e.getSource() instanceof JMenuItem)
  // {
  // System.out.println(e.getActionCommand());
  // }
  // }

  public void mouseClicked(MouseEvent e)
  {
    TreeNodeUI node = _treeGraph.getSelectedNode();
    if (node == null)
      return;
  }

  public void mousePressed(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

  public void mouseReleased(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

  public void mouseEntered(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }

  public void mouseExited(MouseEvent e)
  {
    // TODO Auto-generated method stub

  }
}
