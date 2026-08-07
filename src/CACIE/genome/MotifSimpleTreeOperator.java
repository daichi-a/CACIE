package CACIE.genome;

import java.util.ArrayList;

import CACIE.RandomManager;

public class MotifSimpleTreeOperator
{

  private int operator = -1; //ノードのID
  private boolean hasSpecialArg = false;
  private double extraArg = 0;
  private boolean hasRecursivePotential = false;
  private int recursivePower = 1;

  MotifSimpleTreeOperator()
  {}

  MotifSimpleTreeOperator(int mode, boolean headOrNot)
  {
    if (headOrNot){
      int setV = (int) Math.round(Math.floor(RandomManager.getRandom() * 2));
      if (setV == 0)
	this.operator = TreeOperators.S;
      else if (setV == 1)
	this.operator = TreeOperators.U;
      else if (setV == 2)
	this.operator = TreeOperators.SR;
    } else if (mode == TreeIndividuals.MONOPHONY_MODE)
    {
      int setV = (int) Math.round(RandomManager.getRandom() * 11);
      if (setV == 0)
	this.operator = TreeOperators.S;
      else if (setV == 1)
	this.operator = TreeOperators.SR;
      else if (setV == 2)
	this.operator = TreeOperators.SA;
      else if (setV == 3)
	this.operator = TreeOperators.SP;
      else if (setV == 4)
	this.operator = TreeOperators.SD;
      else if (setV == 5)
	this.operator = TreeOperators.P;
      else if (setV == 6)
	this.operator = TreeOperators.D;
      else if (setV == 7)
	this.operator = TreeOperators.A;
      else if (setV == 8)
	this.operator = TreeOperators.RSA;
      else if (setV == 9)
	this.operator = TreeOperators.RSP;
      else
	this.operator = TreeOperators.RSD;

    } else if (mode == TreeIndividuals.POLYPHONY_MODE)
    {
      int setV = (int) Math.round(Math.floor(RandomManager.getRandom() * 10));
      if (setV == 0)
	this.operator = TreeOperators.S;
      else if (setV == 1)
	this.operator = TreeOperators.U;
      else if (setV == 2)
	this.operator = TreeOperators.S;
      else if (setV == 3)
	this.operator = TreeOperators.MA;
      else if (setV == 4)
	this.operator = TreeOperators.MP;
      else if (setV == 5)
	this.operator = TreeOperators.MD;
      else if (setV == 6)
	this.operator = TreeOperators.RSA;
      else if (setV == 7)
	this.operator = TreeOperators.RSP;
      else if (setV == 8)
	this.operator = TreeOperators.RSD;
      else if (setV == 9)
	this.operator = TreeOperators.SR;
    } else
    {
      System.err.println("MotifSimpleTreeOperator : Faild to generate operator : " + mode);
      System.exit(1);
    }
    ArrayList pointArgs = TreeOperators.generateExtraArg(this.operator);
    Boolean tmpBoo = (Boolean) pointArgs.get(0);
    this.hasSpecialArg = tmpBoo.booleanValue();
    Double tmpDouble = (Double) pointArgs.get(1);
    this.extraArg = tmpDouble.doubleValue();
    tmpBoo = (Boolean) pointArgs.get(2);
    this.hasRecursivePotential = tmpBoo.booleanValue();
    Integer tmpInt = (Integer) pointArgs.get(3);
    this.recursivePower = tmpInt.intValue();
  }
  
  public MotifSimpleTreeOperator(ArrayList<String> Operators, String operatorString)
  {
	  //名前指定して生成
    this.operator = TreeOperators.getOperatorFromString(operatorString);
    ArrayList pointArgs = TreeOperators.generateExtraArg(this.operator);
    Boolean tmpBoo = (Boolean) pointArgs.get(0);
    this.hasSpecialArg = tmpBoo.booleanValue();
    Double tmpDouble = (Double) pointArgs.get(1);
    this.extraArg = tmpDouble.doubleValue();
    tmpBoo = (Boolean) pointArgs.get(2);
    this.hasRecursivePotential = tmpBoo.booleanValue();
    Integer tmpInt = (Integer) pointArgs.get(3);
    this.recursivePower = tmpInt.intValue();
  }
  
  MotifSimpleTreeOperator(ArrayList<String> Operators)
  {
    int indexe = (int) Math.round(Math.floor(RandomManager.getRandom() * Operators.size()));
    String opStr = (String) Operators.get(indexe);
    this.operator = TreeOperators.getOperatorFromString(opStr);
    ArrayList pointArgs = TreeOperators.generateExtraArg(this.operator);
    Boolean tmpBoo = (Boolean) pointArgs.get(0);
    this.hasSpecialArg = tmpBoo.booleanValue();
    Double tmpDouble = (Double) pointArgs.get(1);
    this.extraArg = tmpDouble.doubleValue();
    tmpBoo = (Boolean) pointArgs.get(2);
    this.hasRecursivePotential = tmpBoo.booleanValue();
    Integer tmpInt = (Integer) pointArgs.get(3);
    this.recursivePower = tmpInt.intValue();
  }

  public boolean hasExtraArg()
  {
    return hasSpecialArg;
  }

  public double getExtraArg()
  {
    return extraArg;
  }

  protected boolean hasRecursivePotential()
  {
    return hasRecursivePotential;
  }

  protected int getRecursivePower()
  {
    return recursivePower;
  }

  protected Object clone()
  {
    MotifSimpleTreeOperator tmpOprtr = new MotifSimpleTreeOperator();
    tmpOprtr.operator = this.operator;
    tmpOprtr.hasSpecialArg = this.hasSpecialArg;
    tmpOprtr.extraArg = this.extraArg;
    tmpOprtr.hasRecursivePotential = this.hasRecursivePotential;
    tmpOprtr.recursivePower = this.recursivePower;
    return (Object) tmpOprtr;
  }

  protected void setOperator(int operator)
  {
    this.operator = operator;
  }

  public int getOperator()
  {
    return operator;
  }

}
