package CACIE.genome;

import java.util.ArrayList;

import CACIE.eventlist.ScaleFilter;
import CACIE.eventlist.ScaleType;

// tanji's addtion ( "" -> public)
public class TreeNodes
{
  public static int TERMINAL = 1;
  public static int NONTERMINAL = 2;
  public static int TOPNODE = 3;
  public static int RECURSIVENODE = 4;

  private int termOrNot; // Terminal or NonTerminal
  private int operatorMode = -1;
  private int stackCount;
  private int data;
  private int numOfTerminalNodes;
  private boolean hasExtraArg = false;
  private double extraArg = 0;
  private boolean hasRecursivePotential = false;
  private int recursivePower = 1;
  private int scaleRoot = 0;
  private ScaleType scaleType = ScaleType.DIATONIC;

  private ArrayList<String> configArray;
  
  public TreeNodes()
  {}

  protected TreeNodes clone()
  {
    TreeNodes returnNode = new TreeNodes();
    returnNode.operatorMode = this.operatorMode;
    returnNode.numOfTerminalNodes = this.numOfTerminalNodes;
    returnNode.stackCount = this.stackCount;
    returnNode.termOrNot = this.termOrNot;
    returnNode.data = this.data;
    returnNode.hasExtraArg = this.hasExtraArg;
    returnNode.extraArg = this.extraArg;
    returnNode.hasRecursivePotential = this.hasRecursivePotential;
    returnNode.recursivePower = this.recursivePower;
    returnNode.scaleRoot = this.scaleRoot;
    returnNode.scaleType = this.scaleType;
    returnNode.configArray = this.configArray;
    
    return returnNode;
  }

  private void culcStackCount()
  {
    if (this.termOrNot == TreeNodes.TERMINAL)
      this.stackCount = 1;
    else if (this.termOrNot == TreeNodes.RECURSIVENODE)
      this.stackCount = 1;
    else if (this.termOrNot == TreeNodes.NONTERMINAL)
    {
      this.stackCount = TreeOperators.getStackCount(this.data);
    } else
    {
      System.out.println("TreeNodes : Faild to culcStackCount() : " + this.termOrNot);
    }
  }

  protected int getMode()
  {
    return this.termOrNot;
  }

  public void setData(int i)
  {
    this.data = i;
  }

  // tanji's addition (protected -> public)
  public int getData()
  {
    return this.data;
  }

  //
  // tanji's addition (protected -> public)
  public int getOperator()
  {
    return this.data;
  }

  //
  public void setOperatorMode(int operatorMode)
  {
    this.operatorMode = operatorMode;
  }

  public void setTermOrNot(int termOrNot)
  {
    this.termOrNot = termOrNot;
  }

  public int getTermOrNot()
  {
    return this.termOrNot;
  }

  public void setStackCount(int stackCount)
  {
    this.stackCount = stackCount;
  }

  protected void setNumOfTerminalNodes(int numOfTerminalNodes)
  {
    this.numOfTerminalNodes = numOfTerminalNodes;
  }

  public void setHasExtraArg(boolean hasExtraArg)
  {
    this.hasExtraArg = hasExtraArg;
  }

  public void setExtraArg(double extraArg)
  {
    this.extraArg = extraArg;
  }

  public void configureScale(int root, ScaleType type)
  {
    this.scaleRoot = Math.floorMod(root, 12);
    this.scaleType = type;
  }

  public int getScaleRoot()
  {
    return scaleRoot;
  }

  public ScaleType getScaleType()
  {
    return scaleType;
  }

  protected void setHasRecursivePotential(boolean hasRecursivePotential)
  {
    this.hasRecursivePotential = hasRecursivePotential;
  }

  protected void setRecursivePower(int recursivePower)
  {
    this.recursivePower = recursivePower;
  }

  public String getOperatorAsString()
  {
    String returnString = new String("NotOperator");
    if (this.termOrNot == TreeNodes.NONTERMINAL)
    {
	if (this.data == TreeOperators.SCALE)
	  returnString = "SCALE_" + ScaleFilter.tonicName(scaleRoot) + "_" + scaleType.name();
      else if (this.hasExtraArg == false)
	returnString = TreeOperators.getOperatorAsString(this.data);
      else if (this.hasExtraArg)
      {
	if (!this.hasRecursivePotential)
	{
	  returnString = TreeOperators.getOperatorAsString(this.data) + "_"
	      + String.valueOf(this.extraArg);
	} else
	{
	  returnString = TreeOperators.getOperatorAsString(this.data) + "_"
	      + String.valueOf(this.extraArg) + "_" + String.valueOf(this.recursivePower);
	}
      }
    } else
    {
      if (this.termOrNot == TreeNodes.RECURSIVENODE)
      {
	returnString = new String("R" + "_" + String.valueOf(this.data));
      } else
      {
	returnString = new String(String.valueOf(this.data));
      }
    }
    return returnString;
  }

  // tanji's addition (protected -> public)
  public int getStackCount()
  {
    this.culcStackCount();
    return this.stackCount;
  }

  //

  public void setConfigArray(ArrayList<String> configArray){
	  this.configArray = configArray;
  }
  
  protected ArrayList<Notes> evaluate(int opr, ArrayList<Notes> notes)
  {
    ArrayList<Notes> returnArrayList = new ArrayList<Notes>();
    if (opr == TreeOperators.SCALE)
    {
      returnArrayList.add(ScaleFilter.apply(notes.get(0), scaleType, scaleRoot));
    }
    else
      returnArrayList = TreeOperators.evlOperator(opr, extraArg, recursivePower, notes, configArray);
    return returnArrayList;
  }

  public String toString()
  {
    return this.getOperatorAsString();
  }

}
