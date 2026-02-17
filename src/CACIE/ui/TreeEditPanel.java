package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import javax.swing.tree.TreeModel;

import CACIE.genome.MotifSimpleTreeNode;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.TreeNodes;
import CACIE.genome.TreeOperators;

public class TreeEditPanel extends JPanel implements ActionListener, MouseListener
{
	private TreeGraph _treeA;
	private TreeGraph _treeB;
	private OperationWindows _operationWindows;
	private int _indexA;
	private int _indexB;

	// --- gui ---
	private JButton _btnExchange;

	// private JButton _btnRemove;
	// private JMenu _replaceMenu;

	public TreeEditPanel(OperationWindows operationWindow)
	{
		if (operationWindow == null)
			System.err.println("OperationWindow is null");

		_operationWindows = operationWindow;
		_treeA = new TreeGraph();
		_treeB = new TreeGraph();
		_indexA = -1;
		_indexB = -1;
		initGUI();
	}

	private void initGUI()
	{
		this.setLayout(new BorderLayout());
		// this.add(new JLabel("Genom Tree Editor"), BorderLayout.NORTH);
		// this.add(Border)

		// center
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(1, 2));
		centerPanel.add(_treeA);
		_treeA.addMouseListener(this);
		centerPanel.add(_treeB);
		_treeB.addMouseListener(this);
		this.add(centerPanel);

		// south
		_btnExchange = new JButton("Exchange");
		_btnExchange.addActionListener(this);
		ArrayList operationList = _operationWindows.getOperationList();

		JPanel southPanel = new JPanel();
		southPanel.add(_btnExchange);
		this.add(southPanel, BorderLayout.SOUTH);

		// size
		this.setMinimumSize(new Dimension(50, 50));
	}

	public void selectTree(TreeModel tree, int index)
	{
		if (_treeA.getTreeModel() != null && index != _indexA) // not allows
		// same tree
		{
			_treeB.setTreeModel(_treeA.getTreeModel());
			_indexB = _indexA;
		}
		_treeA.setTreeModel(tree);
		_indexA = index;

		repaint();
	}

	private int getIndexOnDFS(ArrayList<Object> dfs, TreeNodeUI nodeUI)
	{
		for (int i = 0; i < dfs.size(); i++)
		{
			if (nodeUI.getTreeNode() == dfs.get(i))
				return i;
		}
		return -1;
	}

	// ----------------- listener methods --------------------
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this._btnExchange) // subtree exchange
		// operation
		{
			if (_treeA == null || _treeB == null)
				return;
			TreeNodeUI selectedA = _treeA.getSelectedNode();
			TreeNodeUI selectedB = _treeB.getSelectedNode();
			if (selectedA == null || selectedB == null)
				return;

			// exchange
			Motif_simpleTree_Individual individualA = _operationWindows.getIndividual(_indexA);
			Motif_simpleTree_Individual individualB = _operationWindows.getIndividual(_indexB);
			ArrayList genomA = new ArrayList(individualA.getNumOfNodes());
			ArrayList genomB = new ArrayList(individualB.getNumOfNodes());

			int selectedIndexA = individualA.getIndex((TreeNodes) ((DefaultMutableTreeNode) selectedA.getTreeNode())
					.getUserObject());
			int sumOfStackCountA = 0;
			int selectedIndexB = individualB.getIndex((TreeNodes) ((DefaultMutableTreeNode) selectedB.getTreeNode())
					.getUserObject());
			int sumOfStackCountB = 0;

			// first, add own nodes
			for (int i = 0; i < selectedIndexA; i++)
			{
				genomA.add(individualA.getNode(i));
			}
			for (int i = 0; i < selectedIndexB; i++)
			{
				genomB.add(individualB.getNode(i));
			}

			// second, exchange subtree
			int subtreeIndexA = selectedIndexA;
			while (sumOfStackCountA != 1) // until complete subtree
			{
				sumOfStackCountA += individualA.getStackCount(subtreeIndexA);
				genomB.add(individualA.getNode(subtreeIndexA));
				subtreeIndexA++;
			}
			int subtreeIndexB = selectedIndexB;
			while (sumOfStackCountB != 1) // until complete subtree
			{
				sumOfStackCountB += individualB.getStackCount(subtreeIndexB);
				genomA.add(individualB.getNode(subtreeIndexB));
				subtreeIndexB++;
			}
			// last, fill remainder
			for (int i = subtreeIndexA; i < individualA.getNumOfNodes(); i++)
			{
				genomA.add(individualA.getNode(i));
			}
			for (int i = subtreeIndexB; i < individualB.getNumOfNodes(); i++)
			{
				genomB.add(individualB.getNode(i));
			}
						
			individualA.setGenomArray(genomA);
			individualB.setGenomArray(genomB);
			System.out.println("afo");
			Motif_simpleTree_Individual individual = ((Motif_simpleTree_Individual) _operationWindows.getPopulation().getIndividual(0, _indexB));
			System.out.println(individual);
			System.out.println(this._operationWindows.getIndividual(_indexB));
			_treeB.setTreeModel(this._operationWindows.reflesh(_indexB).getTreeModel(individualB));
			_treeA.setTreeModel(this._operationWindows.reflesh(_indexA).getTreeModel(individualA));
			repaint();
		} else if (e.getSource() instanceof JMenuItem)
		{
			System.out.println(e.getActionCommand());
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		TreeNodeUI node = _treeA.getSelectedNode();
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
