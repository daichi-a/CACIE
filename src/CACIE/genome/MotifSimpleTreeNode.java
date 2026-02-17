package CACIE.genome;

import java.util.ArrayList;
import java.util.StringTokenizer;

import CACIE.RandomManager;

public class MotifSimpleTreeNode
{
  protected static TreeNodes generate(int operatorMode, int numOfTerminalNodes, ArrayList<String> configArray)
  {
    TreeNodes returnNode = new TreeNodes();
    returnNode.setConfigArray(configArray);
    
    int termornot = (int) Math.round(Math.floor(RandomManager.getRandom() * 2));
    if (termornot == 0)
      returnNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, operatorMode,
	  numOfTerminalNodes,configArray);
    else
      returnNode = MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, operatorMode,
	  numOfTerminalNodes, configArray);
    return returnNode;
  }
  
  protected static TreeNodes generateTerminal(int operatorMode, int numOfTerminalNodes, int terminalIndex, ArrayList<String> configArray)
  {
    TreeNodes returnNode = new TreeNodes();
    returnNode.setConfigArray(configArray);
    returnNode.setNumOfTerminalNodes(numOfTerminalNodes);
    
    //int index = (int) Math.round(Math.floor(RandomManager.getRandom() * numOfTerminalNodes));
    returnNode.setData(terminalIndex);
    returnNode.setStackCount((int) 1);
    returnNode.setTermOrNot(TreeNodes.TERMINAL);
    returnNode.setOperatorMode(operatorMode);
    return returnNode;
  }
  
  public static TreeNodes generate(int termOrNot, int operatorMode, int numOfTerminalNodes, ArrayList<String> configArray)
  {
    TreeNodes returnNode = new TreeNodes();
    returnNode.setConfigArray(configArray);
    
    returnNode.setNumOfTerminalNodes(numOfTerminalNodes);
    if (termOrNot == TreeNodes.TERMINAL)
    {
      int setted = (int) Math.round(Math.floor(RandomManager.getRandom() * 4));
      int index = (int) Math.round(Math.floor(RandomManager.getRandom() * numOfTerminalNodes));
      returnNode.setData(index);
      returnNode.setStackCount((int) 1);
      if (setted == 3)
      {
	returnNode.setTermOrNot(TreeNodes.RECURSIVENODE);
      } else
      {
	returnNode.setTermOrNot(TreeNodes.TERMINAL);
      }
    } else if (termOrNot == TreeNodes.NONTERMINAL)
    {
      MotifSimpleTreeOperator tmpOpr = new MotifSimpleTreeOperator(operatorMode, false);
      returnNode.setData(tmpOpr.getOperator());
      returnNode.setTermOrNot(TreeNodes.NONTERMINAL);
      if (tmpOpr.hasExtraArg() == true)
      {
	returnNode.setHasExtraArg(true);
	returnNode.setExtraArg(tmpOpr.getExtraArg());
      }
      if (tmpOpr.hasRecursivePotential() == true)
      {
	returnNode.setHasRecursivePotential(true);
	returnNode.setRecursivePower(tmpOpr.getRecursivePower());
      }
    } else if (termOrNot == TreeNodes.TOPNODE)
    {
      MotifSimpleTreeOperator tmpOpr = new MotifSimpleTreeOperator(operatorMode, true);
      returnNode.setData(tmpOpr.getOperator());
      returnNode.setTermOrNot(TreeNodes.NONTERMINAL);
      if (tmpOpr.hasExtraArg() == true)
      {
	returnNode.setHasExtraArg(true);
	returnNode.setExtraArg(tmpOpr.getExtraArg());
      }
      if (tmpOpr.hasRecursivePotential() == true)
      {
	returnNode.setHasRecursivePotential(true);
	returnNode.setRecursivePower(tmpOpr.getRecursivePower());
      }
    } else
    {
      System.err.println("MotifSimpleTreeNode : Faild to detect the termOrNot (generate()) : "
	  + termOrNot);
    }
    returnNode.setOperatorMode(operatorMode);
    return returnNode;
  }

  protected static TreeNodes generate(int operatorMode, int numOfTerminalNodes, ArrayList<String> Operators, ArrayList<String> configArray)
  {
    TreeNodes returnNode = new TreeNodes();
    returnNode.setConfigArray(configArray);
    
    int ton = (int) Math.round(Math.floor(RandomManager.getRandom() * 2));
    if (ton == 0)
      returnNode = MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, operatorMode,
	  numOfTerminalNodes, Operators, configArray);
    else
      returnNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, operatorMode,
	  numOfTerminalNodes, Operators, configArray);
    return returnNode;
  }

  
  protected static TreeNodes generate(int termOrNot, int operatorMode, int numOfTerminalNodes, 
		  ArrayList<String> Operators, String operatorName, ArrayList<String> configArray){
	  //ノード名指定で生成
	  //System.err.print("Generating " + operatorName + ": ");
	  TreeNodes returnNode = new TreeNodes();
	  returnNode.setConfigArray(configArray);
	  
	  MotifSimpleTreeOperator tmpOpr =
		  new MotifSimpleTreeOperator(Operators, operatorName);
	  returnNode.setData(tmpOpr.getOperator());
	  int stackCount = TreeOperators.getStackCount(tmpOpr.getOperator());
	  //System.err.print("StackCount is " + stackCount + ": ");
	  //System.err.println("Registed name is " + TreeOperators.getOperatorAsString(tmpOpr.getOperator()));
	  if(stackCount < 1)
		  returnNode.setTermOrNot(TreeNodes.NONTERMINAL);
	  else
		  returnNode.setTermOrNot(TreeNodes.TERMINAL);
	  returnNode.setStackCount(stackCount);
	  if(tmpOpr.hasExtraArg() == true){
		  returnNode.setHasExtraArg(true);
		  returnNode.setExtraArg(tmpOpr.getExtraArg());
	  }
	  return returnNode;
  }
  
  protected static TreeNodes generate(int termOrNot, int operatorMode, int numOfTerminalNodes,
      ArrayList<String> Operators, ArrayList<String> configArray)
  {
	  //オペレータリストからNonTerminalとTerminalを指定して生成
    TreeNodes returnNode = new TreeNodes();
    returnNode.setConfigArray(configArray);
    
    if (termOrNot == TreeNodes.TOPNODE)
    {
      MotifSimpleTreeOperator tmpOpr = new MotifSimpleTreeOperator(operatorMode, true);
      returnNode.setData(tmpOpr.getOperator());
      returnNode.setTermOrNot(TreeNodes.NONTERMINAL);
      if (tmpOpr.hasExtraArg() == true)
      {
	returnNode.setHasExtraArg(true);
	returnNode.setExtraArg(tmpOpr.getExtraArg());
      }
    } else if (termOrNot == TreeNodes.TERMINAL)
    {
      int setted = (int) Math.round(Math.floor(RandomManager.getRandom() * 4));
      int index = (int) Math.round(Math.floor(RandomManager.getRandom() * numOfTerminalNodes));
      returnNode.setData(index);
      returnNode.setStackCount((int) 1);
      if (setted == 3)
      {
	returnNode.setTermOrNot(TreeNodes.RECURSIVENODE);
      } else
      {
	returnNode.setTermOrNot(TreeNodes.TERMINAL);
      }
    } else
    {
      // NONTERMINAL
      MotifSimpleTreeOperator tmpOpr = new MotifSimpleTreeOperator(Operators);
      returnNode.setData(tmpOpr.getOperator());
      returnNode.setTermOrNot(TreeNodes.NONTERMINAL);
      if (tmpOpr.hasExtraArg() == true)
      {
	returnNode.setHasExtraArg(true);
	returnNode.setExtraArg(tmpOpr.getExtraArg());
      }
    }
    return returnNode;
  }

  public static TreeNodes generate(String fromEventList, int numOfTerminalNodes, ArrayList<String> configArray)
  {
	  //Terminal Node指定で生成
    TreeNodes returnNode = new TreeNodes();
    returnNode.setConfigArray(configArray);
    
    int termOrNot = TreeNodes.TERMINAL;
    int data = -1;
    boolean hasExtraArg = false;
    double extraArg = 0;
    //boolean hasRecursivePotential = false;
    //int recursivePower = 1;

    StringTokenizer tmpstken = new StringTokenizer(fromEventList, "_");
    int numOfParameter = tmpstken.countTokens();
    String eachEl[] = new String[numOfParameter];
    for (int i = 0; i < numOfParameter; i++)
    {
      eachEl[i] = tmpstken.nextToken();
    }

    int nodeID = TreeOperators.getOperatorFromString(eachEl[0]);
    if (nodeID == -1)
    {
      if (eachEl[0].equals("R")){
    	  // Recursive Node
    	  termOrNot = TreeNodes.RECURSIVENODE;
    	  data = Integer.parseInt(eachEl[1]);
      	}
      else{
	// Normal TerminalNode
	termOrNot = TreeNodes.TERMINAL;
	data = Integer.parseInt(eachEl[0]);
      }
    } else
    {
      // NonTerminal Node
      termOrNot = TreeNodes.NONTERMINAL;
      data = nodeID;
      if (numOfParameter > 1)
      {
	hasExtraArg = true;
	extraArg = Double.parseDouble(eachEl[1]);
	/*
	if (numOfParameter != 2){
	  // RSA, RSP, RSD
	  // Temporary don't use these functions
	  hasRecursivePotential = true;
	  recursivePower = Integer.parseInt(eachEl[2]);
	}
	else{
		hasRecursivePotential = false;
		recursivePower = 1;
	}
	*/
      }
    }

    returnNode.setData(data);
    returnNode.setTermOrNot(termOrNot);
    returnNode.setHasExtraArg(hasExtraArg);
    returnNode.setExtraArg(extraArg);

    return returnNode;
  }

}
