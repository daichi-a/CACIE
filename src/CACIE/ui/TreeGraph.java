package CACIE.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

/**
 * TreeGraph class is used to display TreeModel.
 * @author Makoto Tanji
 */
public class TreeGraph extends JComponent implements MouseListener, MouseMotionListener
{
  /**
   * Default height between two stages.
   */
  public static final double DEFAULT_ROW_HEIGHT = 25;
  /**
   * Default node arc radius
   */
  public static final double DEFAULT_NODE_ARC_RADIUS = 8;

  /**
   * Default node size
   */
  public static final double DEFAULT_NODES_DISTANCE = 16;

  private TreeModel _treeModel;
  private double _nodeArcRadius;
  private double _rowHeight;
  private double _nodesDistance;
  private ArrayList<TreeNodeUI> _nodeUIArray; // in BFS order
  private ArrayList<Integer> _nodeCounts; // number of nodes on each stage
                                                // 0 to N
  private ArrayList<Object> _nodeModelArray;
  private TreeNodeUI _selected;

  /** constructs empty TreeGraph */
  public TreeGraph()
  {
    this(null);
  }

  /** constructs TreeGraph that represent specified TreeModel */
  public TreeGraph(TreeModel treeModel)
  {
    double _scale = 1;
    _nodeArcRadius = DEFAULT_NODE_ARC_RADIUS * _scale;
    _rowHeight = DEFAULT_ROW_HEIGHT * _scale;
    _nodesDistance = DEFAULT_NODES_DISTANCE * _scale;

    setTreeModel(treeModel); // calculates node positions in this method

    // event
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  }

  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.clearRect(0, 0, getWidth(), getHeight());

    // draw nodes
    ArrayList<TreeNodeUI> closedNodes = new ArrayList<TreeNodeUI>();

    for (int i = 0; i < _nodeUIArray.size(); i++)
    {
      if (_nodeUIArray.get(i) == _selected)
	_nodeUIArray.get(i).draw(g2, Color.RED);
      else
	_nodeUIArray.get(i).draw(g2, Color.BLACK);
    }
  }

  // calculates node position
  private void calculateNodePosition2()
  {
    ArrayList<ArrayList<Object>> bfs = breadthFirstSearch();

    double minimumDistance = 4 * _nodeArcRadius;
    double maxWidth = 0;
    int nodeUIIndex = 0;
    int stage = _nodeCounts.size() - 1;

    for (int i = 0; i < _nodeCounts.size() - 1; i++)
    {
      nodeUIIndex += _nodeCounts.get(i);
    }

    // start from bottom stage
    for (int i = 0; i < _nodeCounts.get(stage); i++)
    {
      _nodeUIArray.get(nodeUIIndex + i).setPosition(_nodeArcRadius + i * minimumDistance,
	  (bfs.size() - 1) * _rowHeight + _nodeArcRadius);
    }

    // climb up to stage 0
    for (int i = stage - 1; i >= 0; i--)
    {
      double lastPosition = Double.NEGATIVE_INFINITY;
      nodeUIIndex = nodeUIIndex - _nodeCounts.get(i);
      for (int j = 0; j < _nodeCounts.get(i); j++)
      {
	TreeNodeUI node = node = _nodeUIArray.get(nodeUIIndex + j);
	double positionX = _nodeArcRadius + j * minimumDistance;
	if (!_treeModel.isLeaf(node.getTreeNode()))
	{
	  positionX = 0;
	  for (int c = 0; c < node.getChildCount(); c++)
	  {
	    positionX += node.getChildNodeUI(c).getCenterX();
	  }
	  positionX /= node.getChildCount(); // get average of children's X
                                                // position
	}
	node.setPosition(positionX, i * _rowHeight + _nodeArcRadius);

	// adjusts if collision occur
	if (positionX - lastPosition < minimumDistance)
	{
	  moveToRight(node, minimumDistance - (positionX - lastPosition), bfs, i, j, false);
	}
	lastPosition = node.getCenterX();
	if( lastPosition > maxWidth ) {
	  maxWidth = lastPosition;
	}
      }
    }

    // finally, draws all links
    for (int i = 0; i < _nodeUIArray.size(); i++)
    {
      TreeNodeUI node = _nodeUIArray.get(i);
      int childSize = node.getChildCount();
      for (int j = 0; j < childSize; j++)
      {
	node.getChildNodeUI(j).setLine(node.getCenterX(), node.getCenterY());
      }
    }
    this.setPreferredSize(new Dimension((int) maxWidth,	(int) (bfs.size() * _rowHeight + _nodeArcRadius)));
  }

  // -----------------------
  private void moveToRight(TreeNodeUI node, double d, ArrayList<ArrayList<Object>> bfs, int stage,
      int index, boolean isRightNodeGenerated)
  {
    node.setPosition(node.getCenterX() + d, node.getCenterY());
    if (isRightNodeGenerated
	&& bfs.get(stage).size() > index + 1
	&& getNodeUI(bfs.get(stage).get(index + 1)).getCenterX() - node.getCenterX() < 4 * _nodeArcRadius)
    {
      TreeNodeUI rightNode = getNodeUI(bfs.get(stage).get(index + 1));
      // System.out.println("collision chain at " + stage + " " + index + " "
        // + (rightNode.getCenterX() - node.getCenterX()) );
      moveToRight(rightNode, (4 * _nodeArcRadius) - (rightNode.getCenterX() - node.getCenterX()),
	  bfs, stage, index + 1, true);
    }
    for (int i = node.getChildCount() - 1; i != -1; i--)
    {
      int p = 0;
      int sum = 0;
      while (p < index)
      {
	sum += _treeModel.getChildCount(bfs.get(stage).get(p++));
      }
      moveToRight(node.getChildNodeUI(i), d, bfs, stage + 1, sum + i, true);
    }
    // move right node if collision occur
  }

  private TreeNodeUI getNodeUI(Object node)
  {
    for (int i = 0; i < _nodeUIArray.size(); i++)
    {
      if (_nodeUIArray.get(i).getTreeNode() == node)
	return _nodeUIArray.get(i);
    }
    return null;
  }

  /**
         * returns BFS result
         * @return array of result on breadth first search
         */
  protected ArrayList<ArrayList<Object>> breadthFirstSearch()
  {
    ArrayList<ArrayList<Object>> bfs = new ArrayList<ArrayList<Object>>();
    ArrayList<Object> nextStage = new ArrayList<Object>();
    ArrayList<Object> currentStage = new ArrayList<Object>();
    _nodeUIArray.clear();
    _nodeCounts.clear();

    // first stage
    Object parent = _treeModel.getRoot();
    currentStage.add(parent);
    _nodeUIArray.add(new TreeNodeUI(this, parent)); // nodeUI
    _nodeCounts.add(1); // the number of root node
    bfs.add(currentStage);
    _nodeModelArray.add(parent);

    while (true)
    {
      int childCountSum = 0;
      for (int i = 0; i < currentStage.size(); i++)
      {
	int childSize = _treeModel.getChildCount(currentStage.get(i));
	for (int j = 0; j < childSize; j++)
	{
	  // model
	  Object node = _treeModel.getChild(currentStage.get(i), j);
	  nextStage.add(node);
	  _nodeModelArray.add(node);

	  // graphical UI
	  TreeNodeUI nodeUI = new TreeNodeUI(this, node);
	  nodeUI.setParentNodeUI(_nodeUIArray.get(_nodeUIArray.size() - childCountSum
	      - currentStage.size() + i));
	  _nodeUIArray.add(nodeUI);
	  childCountSum++;
	}
      }
      if (nextStage.size() == 0)
	break;
      bfs.add(nextStage);
      _nodeCounts.add(nextStage.size());
      currentStage = nextStage;
      nextStage = new ArrayList<Object>();
    }
    return bfs;
  }

  /**
         * returns DFS result
         * @return array of result on depth first search
         */
  public ArrayList<Object> depthFirstSearch()
  {
    ArrayList<Object> dfsArray = new ArrayList<Object>();
    Stack<Object> searchStack = new Stack<Object>();

    // first stage
    Object parent = _treeModel.getRoot();
    searchStack.push(parent);

    dfsSearch(_treeModel.getRoot(), dfsArray);

    return dfsArray;
  }

  /** DFS search method, this is recursive method */
  private void dfsSearch(Object node, ArrayList<Object> resultArray)
  {
    resultArray.add(node);
    if (_treeModel.isLeaf(node))
      return;

    for (int i = 0; i < _treeModel.getChildCount(node); i++) // has childlen
    {
      dfsSearch(_treeModel.getChild(node, i), resultArray);
    }
  }

  /** returns radius of node arc */
  public double getNodeArcRadius()
  {
    return _nodeArcRadius;
  }

  /** sets radius of node arc */
  public void setNodeArcRadius(double nodeArcRadius)
  {
    _nodeArcRadius = nodeArcRadius;
  }

  /** returns height between two stages */
  public double getRowHeight()
  {
    return _rowHeight;
  }

  /** sets height between two stages */
  public void setRowHeight(double rowHeight)
  {
    _rowHeight = rowHeight;
  }

  /** returns horizontal distance of two nodes */
  public double getNodesDistance()
  {
    return _nodesDistance;
  }

  /** sets horizontal distance of two nodes */
  public void setNodesDistance(double nodesDistance)
  {
    _nodesDistance = nodesDistance;
  }

  /** returns TreeModel */
  public TreeModel getTreeModel()
  {
    return _treeModel;
  }

  /** sets TreeModel */
  public void setTreeModel(TreeModel treeModel)
  {
    _treeModel = treeModel;
    _nodeUIArray = new ArrayList<TreeNodeUI>();
    _nodeModelArray = new ArrayList<Object>();
    _nodeCounts = new ArrayList<Integer>();
    _selected = null;
    if (treeModel != null)
      calculateNodePosition2();
  }

  private void setCloseOrOpenFollowings(TreeNodeUI selected, boolean closed)
  {
    for (int i = 0; i < selected.getChildCount(); i++)
    {
      TreeNodeUI nodeUI = selected.getChildNodeUI(i);
      nodeUI.setClosed(closed);
      if (nodeUI.isClosedPoint())
      {
	// stop close or open recursive
      } else
	setCloseOrOpenFollowings(nodeUI, closed);
    }
  }

  public ArrayList getDFS()
  {
    return this.depthFirstSearch();
  }

  public TreeNodeUI getSelectedNode()
  {
    return _selected;
  }

  // ----- listener methods -----
  public void mouseClicked(MouseEvent e)
  {
    for (TreeNodeUI nodeUI : _nodeUIArray)
    {
      if (nodeUI.getShape().intersects(e.getX(), e.getY(), 1, 1))
      {
	if (_selected == nodeUI) // opeartion close or open tree node
	{
	  if (_treeModel.isLeaf(nodeUI.getTreeNode()))
	    return; // is leaf

	  if (_selected.isClosedPoint())
	  {
	    _selected.setClosedPoint(false); // open
	    setCloseOrOpenFollowings(_selected, false);
	  } else
	  {
	    _selected.setClosedPoint(true); // close
	    setCloseOrOpenFollowings(_selected, true);
	  }
	} else
	  _selected = nodeUI;
      }
    }
    repaint();
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

  public void mouseDragged(MouseEvent e)
  {
  // TODO Auto-generated method stub

  }

  public void mouseMoved(MouseEvent e)
  {
  // TODO Auto-generated method stub

  }

  // -------------------- for test
  public static void main(String args[])
  {
    // creates sample tree
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("");
    DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("");
    root.add(child1);
    root.add(child2);
    DefaultMutableTreeNode child11 = new DefaultMutableTreeNode("");
    DefaultMutableTreeNode child12 = new DefaultMutableTreeNode("");
    DefaultMutableTreeNode child13 = new DefaultMutableTreeNode("");
    child1.add(child11);
    child1.add(child12);
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(child13);
    DefaultMutableTreeNode child21 = new DefaultMutableTreeNode("");
    DefaultMutableTreeNode child22 = new DefaultMutableTreeNode("");
    child2.add(child21);
    child2.add(child22);
    DefaultMutableTreeNode child121 = new DefaultMutableTreeNode("");
    child12.add(child121);
    DefaultMutableTreeNode child131 = new DefaultMutableTreeNode("");
    child13.add(child131);
    DefaultTreeModel treeModel = new DefaultTreeModel(root);
    child131.add(new DefaultMutableTreeNode(""));
    child131.add(new DefaultMutableTreeNode(""));
    child131.add(new DefaultMutableTreeNode(""));
    child131.add(new DefaultMutableTreeNode(""));
    child131.add(new DefaultMutableTreeNode(""));
    child131.add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child131.getChildAt(3)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child131.getChildAt(3)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child131.getChildAt(3)).add(new DefaultMutableTreeNode(""));
    child121.add(new DefaultMutableTreeNode(""));
    child121.add(new DefaultMutableTreeNode(""));
    child121.add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child121.getChildAt(0)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child121.getChildAt(0)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child121.getChildAt(0)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child121.getChildAt(0)).add(new DefaultMutableTreeNode(""));
    child21.add(new DefaultMutableTreeNode(""));
    child21.add(new DefaultMutableTreeNode(""));
    child21.add(new DefaultMutableTreeNode(""));
    child21.add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child21.getChildAt(2)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child21.getChildAt(2)).add(new DefaultMutableTreeNode(""));
    ((DefaultMutableTreeNode) child21.getChildAt(2)).add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));
    child1.add(new DefaultMutableTreeNode(""));

    // tree graph
    TreeGraph treeGraph = new TreeGraph(treeModel);
    ArrayList<Object> dfs = treeGraph.depthFirstSearch();
    for (Object node : dfs)
    {
      System.out.println(node);
    }
    // draw tree
    JFrame frame = new JFrame("Tree view test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame
    frame.add(treeGraph);
    frame.setSize(600, 400);
    frame.repaint();
    frame.setVisible(true);
  }
}
