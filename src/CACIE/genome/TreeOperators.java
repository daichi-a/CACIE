package CACIE.genome;

import java.util.ArrayList;

import CACIE.RandomManager;
import CACIE.genome.FunctionNodes.*;

public class TreeOperators
{

  public static int RSA = 2048;
  public static int RSP = 2049;
  public static int RSD = 2050;

  public static int MA = 2051;
  public static int MP = 2052;
  public static int MD = 2053;

  public static int MS = 2054;
  public static int MU = 2055;

  public static int CAR = 2056;
  public static int CDR = 2057;

  public static int FILP = 2058;
  public static int FILA = 2059;
  public static int FILD = 2060;

  public static int ACML = 2061;

  public static int S = 3064;
  public static int U = 3065;
  public static int D = 3066;
  public static int A = 3067;
  public static int P = 3068;
  public static int SA = 3069;
  public static int SP = 3070;
  public static int SD = 3071;

  public static int SR = 3072;

  public static int IV = 4096;
  public static int TP = 4097;
  public static int RV = 4098;
  
  public static int BARFIX = 5096;
  public static int SCALE = 5086;
  
  public static String getOperatorAsString(int operator)
  {
    String returnString = new String("Empty");
    if (operator == TreeOperators.S)
      returnString = new String("S");
    else if (operator == TreeOperators.U)
      returnString = new String("U");
    else if (operator == TreeOperators.D)
      returnString = new String("D");
    else if (operator == TreeOperators.A)
      returnString = new String("A");
    else if (operator == TreeOperators.P)
      returnString = new String("P");
    else if (operator == TreeOperators.SA)
      returnString = new String("SA");
    else if (operator == TreeOperators.SP)
      returnString = new String("SP");
    else if (operator == TreeOperators.SD)
      returnString = new String("SD");
    else if (operator == TreeOperators.IV)
      returnString = new String("IV");
    else if (operator == TreeOperators.RV)
      returnString = new String("RV");
    else if (operator == TreeOperators.TP)
      returnString = new String("TP");
    else if (operator == TreeOperators.SR)
      returnString = new String("SR");
    else if (operator == TreeOperators.RSA)
      returnString = new String("RSA");
    else if (operator == TreeOperators.RSP)
      returnString = new String("RSP");
    else if (operator == TreeOperators.RSD)
      returnString = new String("RSD");
    else if (operator == TreeOperators.MA)
      returnString = new String("MA");
    else if (operator == TreeOperators.MP)
      returnString = new String("MP");
    else if (operator == TreeOperators.MD)
      returnString = new String("MD");
    else if (operator == TreeOperators.MS)
      returnString = new String("MS");
    else if (operator == TreeOperators.MU)
      returnString = new String("MU");
    else if (operator == TreeOperators.CAR)
      returnString = new String("CAR");
    else if (operator == TreeOperators.CDR)
      returnString = new String("CDR");
    else if (operator == TreeOperators.FILP)
      returnString = new String("FILP");
    else if (operator == TreeOperators.FILA)
      returnString = new String("FILA");
    else if (operator == TreeOperators.ACML)
      returnString = new String("ACML");
    else if (operator == TreeOperators.BARFIX)
      returnString = new String("BARFIX");
    else if(operator == TreeOperators.SCALE)
      returnString = new String("SCALE");
    else{
    	System.err.println("the opertor ID: " + operator +" is not registered.");
    	System.exit(1);
    }

    return returnString;
  }

  public static int getOperatorFromString(String st)
  {
    int returnOperator = -1;
    if (st.equals("S"))
      returnOperator = TreeOperators.S;
    else if (st.equals("U"))
      returnOperator = TreeOperators.U;
    else if (st.equals("A"))
      returnOperator = TreeOperators.A;
    else if (st.equals("P"))
      returnOperator = TreeOperators.P;
    else if (st.equals("D"))
      returnOperator = TreeOperators.D;
    else if (st.equals("SA"))
      returnOperator = TreeOperators.SA;
    else if (st.equals("SP"))
      returnOperator = TreeOperators.SP;
    else if (st.equals("SD"))
      returnOperator = TreeOperators.SD;
    else if (st.equals("RSA"))
      returnOperator = TreeOperators.RSA;
    else if (st.equals("RSP"))
      returnOperator = TreeOperators.RSP;
    else if (st.equals("RSD"))
      returnOperator = TreeOperators.RSD;
    else if (st.equals("IV"))
      returnOperator = TreeOperators.IV;
    else if (st.equals("TP"))
      returnOperator = TreeOperators.TP;
    else if (st.equals("RV"))
      returnOperator = TreeOperators.RV;
    else if (st.equals("MA"))
      returnOperator = TreeOperators.MA;
    else if (st.equals("MP"))
      returnOperator = TreeOperators.MP;
    else if (st.equals("MD"))
      returnOperator = TreeOperators.MD;
    else if (st.equals("SR"))
      returnOperator = TreeOperators.SR;
    else if (st.equals("MS"))
      returnOperator = TreeOperators.MS;
    else if (st.equals("MU"))
      returnOperator = TreeOperators.MU;
    else if (st.equals("CAR"))
      returnOperator = TreeOperators.CAR;
    else if (st.equals("CDR"))
      returnOperator = TreeOperators.CDR;
    else if (st.equals("FILP"))
      returnOperator = TreeOperators.FILP;
    else if (st.equals("FILA"))
      returnOperator = TreeOperators.FILA;
    else if (st.equals("ACML"))
      returnOperator = TreeOperators.ACML;
    else if (st.equals("BARFIX"))
        returnOperator = TreeOperators.BARFIX;
    else if(st.equals("SCALE"))
        returnOperator = TreeOperators.SCALE;
    else{
    	System.err.println("Tree Operators: getOperatorFromString: the opertor string: " + st +" is not registered or terminal node.");
    	//System.exit(1);
    }
    return returnOperator;
  }

  public static int getStackCount(int operator)
  {
    int returnValue = -1;
    if (operator == TreeOperators.S)
      returnValue = -1;
    else if (operator == TreeOperators.U)
      returnValue = -1;
    else if (operator == TreeOperators.D)
      returnValue = -1;
    else if (operator == TreeOperators.A)
      returnValue = -1;
    else if (operator == TreeOperators.P)
      returnValue = -1;
    else if (operator == TreeOperators.SA)
      returnValue = -1;
    else if (operator == TreeOperators.SD)
      returnValue = -1;
    else if (operator == TreeOperators.SP)
      returnValue = -1;
    else if (operator == TreeOperators.IV)
      returnValue = 0;
    else if (operator == TreeOperators.TP)
      returnValue = 0;
    else if (operator == TreeOperators.RV)
      returnValue = 0;
    else if (operator == TreeOperators.SR)
      returnValue = -1;
    else if (operator == TreeOperators.RSA)
      returnValue = -1;
    else if (operator == TreeOperators.RSP)
      returnValue = -1;
    else if (operator == TreeOperators.RSD)
      returnValue = -1;
    else if (operator == TreeOperators.MA || operator == TreeOperators.MD
	|| operator == TreeOperators.MD)
      returnValue = -1;
    else if (operator == TreeOperators.MS || operator == TreeOperators.MU)
      returnValue = -1;
    else if (operator == TreeOperators.CAR || operator == TreeOperators.CDR)
      returnValue = 0;
    else if (operator == TreeOperators.FILP || operator == TreeOperators.FILA)
      returnValue = -1;
    else if (operator == TreeOperators.ACML)
      returnValue = 0;
    else if (operator == TreeOperators.BARFIX)
      returnValue = 0;
    else if(operator == TreeOperators.SCALE)
      returnValue = 0;
    else{
    	System.err.println("The opertor ID: " + operator +" is not registered in stackCount List.");
    	System.exit(1);
    }

    return returnValue;
  }

  protected static ArrayList generateExtraArg(int operator)
  {
    boolean hasSpecialArg = false;
    double extraArg = 0.0;
    boolean hasRecursivePotential = false;
    int recursivePower = 1;
    ArrayList returnArray = new ArrayList();
    
    if (operator == TreeOperators.S)
      hasSpecialArg = false;
    else if (operator == TreeOperators.U)
      hasSpecialArg = false;
    else if (operator == TreeOperators.D)
        hasSpecialArg = false;
    else if (operator == TreeOperators.P)
      hasSpecialArg = false;
    else if (operator == TreeOperators.A)
      hasSpecialArg = false;
    else if (operator == TreeOperators.SA)
    {
      hasSpecialArg = true;
      extraArg = (double) (Math.round(Math.floor(RandomManager.getRandom() * 10))) * 0.1 + 0.5;
      String tmpSt = Double.toString(extraArg);
      tmpSt = tmpSt.substring(0, 3);
      extraArg = Double.parseDouble(tmpSt);

    } else if (operator == TreeOperators.SD)
    {
      hasSpecialArg = true;
      extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 5)) - 2.0;
    } else if (operator == TreeOperators.SP)
    {
      hasSpecialArg = true;
      extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 12)) - 6.0;
    } else if (operator == TreeOperators.IV)
    {
      hasSpecialArg = false;
    } else if (operator == TreeOperators.TP)
    {
      hasSpecialArg = true;
      extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 24)) - 12;
    } else if (operator == TreeOperators.RV)
    {
      hasSpecialArg = false;
    } else if (operator == TreeOperators.SR)
    {
      hasSpecialArg = true;
      extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 2) + 1.0 + 2.0);
    } else if (operator == TreeOperators.RSA)
    {
      hasSpecialArg = true;
      do
      {
	extraArg = (double) (Math.round(RandomManager.getRandom() * 10)) * 0.1 + 0.5;
      } while (extraArg == 1.0);
      hasRecursivePotential = true;
      recursivePower = (int) (Math.round(Math.floor(RandomManager.getRandom() * 3)) + 1);
    } else if (operator == TreeOperators.RSP)
    {
      hasSpecialArg = true;
      do
      {
	extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 12)) - 6.0;
      } while (extraArg == 0.0);
      hasRecursivePotential = true;
      recursivePower = (int) (Math.round(Math.floor(RandomManager.getRandom() * 3)) + 1);
    } else if (operator == TreeOperators.RSD)
    {
      hasSpecialArg = true;
      do
      {
	extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 5)) - 2.0;
      } while (extraArg == 0.0);
      hasRecursivePotential = true;
      recursivePower = (int) (Math.round(Math.floor(RandomManager.getRandom() * 3)) + 1);
    } else if (operator == TreeOperators.MA || operator == TreeOperators.MP
	|| operator == TreeOperators.MD)
    {
      hasSpecialArg = false;
    } else if (operator == MS || operator == MU)
    {
      hasSpecialArg = false;
    } else if (operator == CAR || operator == CDR)
    {
      hasSpecialArg = true;
      extraArg = (double) (Math.round(Math.floor(RandomManager.getRandom() * 10))) / 10.0;
      if (extraArg == 0.0)
	extraArg = 0.1;
    } else if (operator == FILP)
    {
      hasSpecialArg = true;
      extraArg = (double) Math.round(Math.floor(RandomManager.getRandom() * 6)) + 1.0;
    } else if (operator == FILA)
    {
      hasSpecialArg = true;
      int before = (int) Math.round(Math.floor((RandomManager.getRandom() * 0.4) * 10.0));
      String tmpSt = Double.toString((before * 0.1) + 0.1);
      tmpSt = tmpSt.substring(0, 3);
      extraArg = Double.parseDouble(tmpSt);
    } else if (operator == ACML)
    {
      hasSpecialArg = false;
    }
    else if (operator == BARFIX)
      hasSpecialArg = true;
    else if(operator == SCALE)
      hasSpecialArg = false;
    else{
    	System.err.println("the opertor ID: " + operator +" is not registered in extra arg list.");
    	System.exit(1);
    }

    ArrayList tmpArrayList = new ArrayList(2);
    tmpArrayList.ensureCapacity(2);
    Boolean tmpBoo = new Boolean(hasSpecialArg);
    tmpArrayList.add(tmpBoo);
    Double tmpDouble = new Double(extraArg);
    tmpArrayList.add(tmpDouble);
    tmpBoo = new Boolean(hasRecursivePotential);
    tmpArrayList.add(tmpBoo);
    Integer tmpInt = new Integer(recursivePower);
    tmpArrayList.add(tmpInt);
    return tmpArrayList;
  }

  protected static ArrayList<Notes> evlOperator(int opr, double baseArg, int recursivePower, ArrayList<Notes> Args, ArrayList<String> configArray)
  {
    ArrayList<Notes> returnArray = new ArrayList<Notes>(0);
    int numOfArg = Args.size();
    if (numOfArg == 1)
      returnArray = TreeOperators.evlOperator1(opr, baseArg, recursivePower, (Notes) Args.get(0), configArray);
    else if (numOfArg == 2)
      returnArray = TreeOperators.evlOperator2(opr, baseArg, recursivePower, (Notes) Args.get(0),
	  (Notes) Args.get(1), configArray);
    Notes tmpNotes = (Notes) returnArray.get(0);
    if (tmpNotes.getNumOfNotes() == 0)
    {
      System.out.println(" ");
      System.out.println("TreeOperators:evlOperator: returnArray has no note!! "
	  + TreeOperators.getOperatorAsString(opr));
    }

    return returnArray;
  }

  private static ArrayList<Notes> evlOperator1(int opr, double baseArg, int recursivePower, Notes first, ArrayList<String> configArray)
  {
    Notes firstNotes = (Notes) first.clone();
    Notes returnNotes = new Notes();
    firstNotes.fitPosition();
    ArrayList<Notes> returnArrayList = new ArrayList<Notes>(1);
    if (opr == TreeOperators.IV)
    {
      TreeOperators.IV(returnNotes, firstNotes);
    } else if (opr == TreeOperators.TP)
    {
      TreeOperators.TP(returnNotes, firstNotes, baseArg);
    } else if (opr == TreeOperators.RV)
    {
      TreeOperators.RV(returnNotes, firstNotes);
    } else if (opr == TreeOperators.CAR)
    {
      TreeOperators.CAR(returnNotes, firstNotes, baseArg);
    } else if (opr == TreeOperators.CDR)
    {
      TreeOperators.CDR(returnNotes, firstNotes, baseArg);
    } else if (opr == TreeOperators.ACML)
    {
      TreeOperators.ACML(returnNotes, firstNotes);
    }
    else if(opr == TreeOperators.BARFIX)
      returnNotes.addNotes(Function_BARFIX.evl(firstNotes, baseArg));

    else{
    	System.err.println("the opertor ID: " + opr +" is not registered in eval list.");
    	System.exit(1);
    }

    returnArrayList.add(returnNotes);
    return returnArrayList;
  }

  private static ArrayList<Notes> evlOperator2(int opr, double baseArg, int recursivePower, Notes first,
      Notes second, ArrayList<String> configArray)
  {
    Notes firstNotes = (Notes) first.clone();
    Notes secondNotes = (Notes) second.clone();
    Notes returnNotes = new Notes();
    firstNotes.fitPosition();
    secondNotes.fitPosition();
    ArrayList<Notes> returnArrayList = new ArrayList<Notes>(1);
    if (opr == TreeOperators.S)
    {
      TreeOperators.S(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.U)
    {
      TreeOperators.U(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.A)
    {
      TreeOperators.A(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.P)
    {
      TreeOperators.P(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.D)
    {
      TreeOperators.D(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.SA)
    {
      TreeOperators.SA(returnNotes, firstNotes, secondNotes, baseArg);
    } else if (opr == TreeOperators.SP)
    {
      TreeOperators.SP(returnNotes, firstNotes, secondNotes, baseArg);
    } else if (opr == TreeOperators.SD)
    {
      TreeOperators.SD(returnNotes, firstNotes, secondNotes, baseArg);
    } else if (opr == TreeOperators.SR)
    {
      TreeOperators.SR(returnNotes, firstNotes, secondNotes, baseArg);

    } else if (opr == TreeOperators.RSA)
    {
      TreeOperators.RSA(returnNotes, firstNotes, secondNotes, baseArg, recursivePower);
    } else if (opr == TreeOperators.RSP)
    {
      TreeOperators.RSP(returnNotes, firstNotes, secondNotes, baseArg, recursivePower);
    } else if (opr == TreeOperators.RSD)
    {
      TreeOperators.RSD(returnNotes, firstNotes, secondNotes, baseArg, recursivePower);
    } else if (opr == TreeOperators.MA)
    {
      TreeOperators.MA(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.MP)
    {
      TreeOperators.MP(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.MD)
    {
      TreeOperators.MD(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.MS)
    {
      TreeOperators.MS(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.MU)
    {
      TreeOperators.MU(returnNotes, firstNotes, secondNotes);
    } else if (opr == TreeOperators.FILP)
    {
      TreeOperators.FILP(returnNotes, firstNotes, secondNotes, baseArg);
    } else if (opr == TreeOperators.FILA)
    {
      TreeOperators.FILA(returnNotes, firstNotes, secondNotes, baseArg);
    }
    else{
    	System.err.println("the opertor ID: " + opr +" is not registered in eval list.");
    	System.exit(1);
    }
    
    returnArrayList.add(returnNotes);
    return returnArrayList;
  }

  private static void S(Notes returnNotes, Notes firstNotes, Notes secondNotes)
  {
    //OneNote tmpNote;
    returnNotes.addNotes(firstNotes);
    returnNotes.fitParameters();
    //long addDuration = returnNotes.getDuration();
    secondNotes.setPosition(returnNotes.getDuration());
    returnNotes.addNotes(secondNotes);
    returnNotes.fitParameters();
  }

  private static void U(Notes returnNotes, Notes firstNotes, Notes secondNotes)
  {
    returnNotes.addNotes(firstNotes);
    returnNotes.addNotes(secondNotes);
    returnNotes.fitParameters();
  }

  private static void A(Notes returnNotes, Notes firstNotes, Notes secondNotes)
  {
    int until = firstNotes.getNumOfNotes();
    int uroul = secondNotes.getNumOfNotes();
    int putpoint = 0;
    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) firstNotes.getNote(i);
      if (i >= uroul)
	putpoint = 0;
      OneNote tmpNote2 = (OneNote) secondNotes.getNote(putpoint);
      tmpNote1.setVelocity(tmpNote2.getVelocity());
      returnNotes.addNote(tmpNote1);
      putpoint++;
    }
    returnNotes.fitParameters();
  }

  private static void P(Notes returnNotes, Notes firstNotes, Notes secondNotes)
  {
    int until = firstNotes.getNumOfNotes();
    int uroul = secondNotes.getNumOfNotes();
    int putpoint = 0;
    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) firstNotes.getNote(i);
      if (i >= uroul)
	putpoint = 0;
      OneNote tmpNote2 = (OneNote) secondNotes.getNote(putpoint);
      tmpNote1.setNoteNumber(tmpNote2.getNoteNumber());
      returnNotes.addNote(tmpNote1);
      putpoint++;
    }
    returnNotes.fitParameters();
  }

  private static void D(Notes returnNotes, Notes firstNotes, Notes secondNotes)
  {
    int until = firstNotes.getNumOfNotes();
    int uroul = secondNotes.getNumOfNotes();
    int putpoint = 0;
    //long increasePosition = firstNotes.getDuration();
    long nextPosition = 0;
    long[] positionList = new long[until];
    int maxDuration = 0;
    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) firstNotes.getNote(i);
      positionList[i] = tmpNote1.getPosition();
    }

    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) firstNotes.getNote(i);
      if (i >= uroul)
	putpoint = 0;
      OneNote tmpNote2 = (OneNote) secondNotes.getNote(putpoint);
      tmpNote1.setDuration(tmpNote2.getDuration());
      if (i != 0)
      {
	tmpNote1.setPosition(nextPosition);
      }

      if (i < until - 1)
      {
	if (positionList[i] == positionList[i + 1])
	{
	  if (tmpNote1.getDuration() > maxDuration)
	    maxDuration = tmpNote1.getDuration();
	  nextPosition = tmpNote1.getPosition();
	} else
	{
	  if (maxDuration != 0)
	    nextPosition = tmpNote1.getPosition() + maxDuration;
	  else
	    nextPosition = tmpNote1.getPosition() + tmpNote1.getDuration();
	  maxDuration = 0;
	}
      } else
      {
	nextPosition = tmpNote1.getDuration() + tmpNote1.getPosition();
      }
      returnNotes.addNote(tmpNote1);
    }
    returnNotes.fitParameters();
  }

  private static void SA(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    //OneNote tmpNote, baseNote;
    returnNotes.addNotes(firstNotes);
    returnNotes.fitParameters();
    long addDuration = returnNotes.getDuration();
    Notes tmpNotes = new Notes();
    TreeOperators.AM(tmpNotes, secondNotes, firstNotes, baseArg);
    tmpNotes.setPosition(addDuration);
    returnNotes.addNotes(tmpNotes);
  }

  private static void AM(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    OneNote tmpNote, baseNote;
    int until = firstNotes.getNumOfNotes();
    int uroul = secondNotes.getNumOfNotes();
    int putpoint = 0;
    for (int i = 0; i < until; i++)
    {
      tmpNote = firstNotes.getNote(i);
      if (i >= uroul)
	putpoint = 0;
      baseNote = secondNotes.getNote(putpoint);
      int tmpVelocity = (int) (Math.round(Math.floor((double) baseNote.getVelocity() * baseArg)));
      tmpNote.setVelocity((int) (tmpVelocity));
      returnNotes.addNote(tmpNote);
      putpoint++;
    }
    returnNotes.fitParameters();
  }

  private static void SP(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    //OneNote tmpNote, baseNote;
    returnNotes.addNotes(firstNotes);
    returnNotes.fitParameters();
    long addDuration = returnNotes.getDuration();
    Notes tmpNotes = new Notes();
    TreeOperators.PM(tmpNotes, secondNotes, firstNotes, baseArg);
    tmpNotes.setPosition(addDuration);
    returnNotes.addNotes(tmpNotes);
  }

  private static void PM(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    OneNote tmpNote, baseNote;
    int until = firstNotes.getNumOfNotes();
    int uroul = secondNotes.getNumOfNotes();
    int putpoint = 0;
    for (int i = 0; i < until; i++)
    {
      tmpNote = firstNotes.getNote(i);
      tmpNote = (OneNote) tmpNote.clone();
      if (i >= uroul)
	putpoint = 0;
      baseNote = secondNotes.getNote(putpoint);
      tmpNote.setNoteNumber((int) baseNote.getNoteNumber() + (int) baseArg);
      returnNotes.addNote(tmpNote);
      putpoint++;
    }
    returnNotes.fitParameters();
  }

  private static void SD(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    returnNotes.addNotes(firstNotes);
    returnNotes.fitParameters();
    //int[] durationString = TreeOperators.getDurationString();
    long firstDuration = returnNotes.getDuration();
    Notes newSecond = new Notes();

    int until = secondNotes.getNumOfNotes();
    int uroul = firstNotes.getNumOfNotes();
    int putpoint = 0;
    //long increasePosition = returnNotes.getDuration();
    long nextPosition = 0;
    long[] positionList = new long[until];
    long maxDuration = 0;
    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) secondNotes.getNote(i);
      positionList[i] = tmpNote1.getPosition();
    }

    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) secondNotes.getNote(i);
      if (i >= uroul)
	putpoint = 0;
      OneNote tmpNote2 = (OneNote) firstNotes.getNote(putpoint);
      tmpNote1.setDuration(tmpNote2.getDuration());
      if (i != 0)
      {
	tmpNote1.setPosition(nextPosition);
      }

      if (i < until - 1)
      {
	if (positionList[i] == positionList[i + 1])
	{

	  if (tmpNote1.getDuration() > maxDuration)
	    maxDuration = tmpNote1.getDuration();
	  nextPosition = tmpNote1.getPosition();
	} else
	{
	  if (maxDuration != 0)
	    nextPosition = tmpNote1.getPosition() + maxDuration;
	  else
	    nextPosition = tmpNote1.getPosition() + tmpNote1.getDuration();
	  maxDuration = 0;
	}
      } else
      {
	nextPosition = tmpNote1.getDuration() + tmpNote1.getPosition();
      }
      newSecond.addNote(tmpNote1);
    }
    newSecond.setPosition(firstDuration);
    for (int i = 0; i < newSecond.getNumOfNotes(); i++)
    {
      OneNote tmpNote = newSecond.getNote(i);
      returnNotes.addNote((OneNote) tmpNote.clone());
    }
    returnNotes.fitParameters();
  }

  private static void DM(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    //int[] durationString = TreeOperators.getDurationString();
    long firstDuration = returnNotes.getDuration();
    Notes newSecond = new Notes();

    int until = secondNotes.getNumOfNotes();
    int uroul = firstNotes.getNumOfNotes();
    int putpoint = 0;
    //long increasePosition = returnNotes.getDuration();
    long nextPosition = 0;
    long[] positionList = new long[until];
    long maxDuration = 0;
    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) secondNotes.getNote(i);
      positionList[i] = tmpNote1.getPosition();
    }

    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote1 = (OneNote) secondNotes.getNote(i);
      if (i >= uroul)
	putpoint = 0;
      OneNote tmpNote2 = (OneNote) firstNotes.getNote(putpoint);
      tmpNote1.setDuration(tmpNote2.getDuration());
      if (i != 0)
      {
	tmpNote1.setPosition(nextPosition);
      }

      if (i < until - 1)
      {
	if (positionList[i] == positionList[i + 1])
	{

	  if (tmpNote1.getDuration() > maxDuration)
	    maxDuration = tmpNote1.getDuration();
	  nextPosition = tmpNote1.getPosition();
	} else
	{
	  if (maxDuration != 0)
	    nextPosition = tmpNote1.getPosition() + maxDuration;
	  else
	    nextPosition = tmpNote1.getPosition() + tmpNote1.getDuration();
	  maxDuration = 0;
	}
      } else
      {
	nextPosition = tmpNote1.getDuration() + tmpNote1.getPosition();
      }
      newSecond.addNote(tmpNote1);
    }
    newSecond.setPosition(firstDuration);
    for (int i = 0; i < newSecond.getNumOfNotes(); i++)
    {
      OneNote tmpNote = newSecond.getNote(i);
      returnNotes.addNote((OneNote) tmpNote.clone());
    }
    returnNotes.fitParameters();
  }

  private static void IV(Notes returnNotes, Notes firstNotes)
  {
    int hiestNumber = -1;
    int lowestNumber = 1024;

    for (int i = 0; i < firstNotes.getNumOfNotes(); i++)
    {
      OneNote tmpNote = firstNotes.getNote(i);
      int nn = tmpNote.getNoteNumber();
      if (nn > hiestNumber)
	hiestNumber = nn;
      if (nn < lowestNumber)
	lowestNumber = nn;
    }
    int width = hiestNumber - lowestNumber;
    int pitchArray[] = new int[firstNotes.getNumOfNotes()];
    for (int i = 0; i < pitchArray.length; i++)
    {
      OneNote tmpNote = firstNotes.getNote(i);
      pitchArray[i] = tmpNote.getNoteNumber() - lowestNumber;
      pitchArray[i] = pitchArray[i] * -1;
      pitchArray[i] = pitchArray[i] + width;
      pitchArray[i] = pitchArray[i] + lowestNumber;
      // tmpNote = (OneNote)tmpNote.clone();
      tmpNote.setNoteNumber(pitchArray[i]);
      returnNotes.addNote(tmpNote);
    }
    returnNotes.fitParameters();
  }

  private static void TP(Notes returnNotes, Notes firstNotes, double baseArg)
  {
    for (int i = 0; i < firstNotes.getNumOfNotes(); i++)
    {
      OneNote tmpNote = firstNotes.getNote(i);
      tmpNote.setNoteNumber(tmpNote.getNoteNumber() + (int) baseArg);
      returnNotes.addNote(tmpNote);
    }
    returnNotes.fitParameters();
  }

  private static void RV(Notes returnNotes, Notes firstNotes)
  {
    Notes gettingArray = (Notes) firstNotes.clone();
    ArrayList<Notes> samePosition = new ArrayList<Notes>();
    // samePosition = TreeOperators.recursiveSort(samePosition,
        // gettingArray);
    samePosition = TreeOperators.sortWithPosition(samePosition, gettingArray);
    int until = samePosition.size();
    long nextPosition = 0;
    for (int i = until - 1; i >= 0; i--)
    {
      Notes tmpNotes = (Notes) samePosition.get(i);
      tmpNotes = (Notes) tmpNotes.clone();
      tmpNotes.fitParameters();
      tmpNotes.setPosition(nextPosition);
      returnNotes.addNotes(tmpNotes);
      nextPosition = returnNotes.getDuration();
    }
    returnNotes.fitParameters();
  }

  private static void SR(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg)
  {
    //OneNote tmpNote;
    long currentDuration = 0;
    for (int i = 0; i < (int) Math.round(baseArg); i++)
    {
      String binSt = Integer.toBinaryString(i);
      int lastByte = Integer.parseInt(binSt.substring(binSt.length() - 1));
      Notes tmpNotes = new Notes();
      if (lastByte == 0)
      {
	tmpNotes = (Notes) firstNotes.clone();
      } else
      {
	tmpNotes = (Notes) secondNotes.clone();
      }
      tmpNotes.setPosition(currentDuration);
      returnNotes.addNotes(tmpNotes);
      returnNotes.fitParameters();
      currentDuration = returnNotes.getDuration();
    }
  }

  private static void RSA(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg,
      int recursivePower)
  {
    returnNotes.addNotes((Notes) secondNotes.clone());
    returnNotes.fitParameters();

    for (int i = 0; i < recursivePower; i++)
    {
      Notes tmpNotes = new Notes();
      TreeOperators.AM(tmpNotes, (Notes) secondNotes.clone(), returnNotes, baseArg);
      returnNotes.setPosition(tmpNotes.getDuration());
      returnNotes.addNotes(tmpNotes);
      returnNotes.fitParameters();
    }
    Notes tmpNotes = new Notes();
    TreeOperators.AM(tmpNotes, (Notes) firstNotes.clone(), returnNotes, baseArg);
    returnNotes.setPosition(tmpNotes.getDuration());
    returnNotes.addNotes(tmpNotes);
    returnNotes.fitParameters();
  }

  private static void RSP(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg,
      int recursivePower)
  {
    returnNotes.addNotes((Notes) secondNotes.clone());
    returnNotes.fitParameters();

    for (int i = 0; i < recursivePower; i++)
    {
      Notes tmpNotes = new Notes();
      TreeOperators.PM(tmpNotes, (Notes) secondNotes.clone(), returnNotes, baseArg);
      returnNotes.setPosition(tmpNotes.getDuration());
      returnNotes.addNotes(tmpNotes);
      returnNotes.fitParameters();
    }
    Notes tmpNotes = new Notes();
    TreeOperators.PM(tmpNotes, (Notes) firstNotes.clone(), returnNotes, baseArg);
    returnNotes.setPosition(tmpNotes.getDuration());
    returnNotes.addNotes(tmpNotes);
    returnNotes.fitParameters();
  }

  private static void RSD(Notes returnNotes, Notes firstNotes, Notes secondNotes, double baseArg,
      int recursivePower)
  {
    returnNotes.addNotes((Notes) secondNotes.clone());
    returnNotes.fitParameters();

    for (int i = 0; i < recursivePower; i++)
    {
      Notes tmpNotes = new Notes();
      TreeOperators.DM(tmpNotes, (Notes) secondNotes.clone(), returnNotes, baseArg);
      returnNotes.setPosition(tmpNotes.getDuration());
      returnNotes.addNotes(tmpNotes);
      returnNotes.sortNotes();
      returnNotes.fitParameters();
    }
    Notes tmpNotes = new Notes();
    TreeOperators.DM(tmpNotes, (Notes) firstNotes.clone(), returnNotes, baseArg);
    returnNotes.setPosition(tmpNotes.getDuration());
    returnNotes.addNotes(tmpNotes);
    returnNotes.sortNotes();
    returnNotes.fitParameters();
  }

  private static ArrayList<Notes> sortWithPosition(ArrayList<Notes> samePosition, Notes gettingArray)
  {

    int until = gettingArray.getNumOfNotes();
    for (int index = 0; index < until; index++)
    {
      OneNote currentNote = gettingArray.getNote(index);
      boolean onoff = false;
      for (int i = 0; i < samePosition.size(); i++)
      {
	Notes tmpNotes = (Notes) samePosition.get(i);
	if (tmpNotes.getPosition() == currentNote.getPosition())
	{
	  tmpNotes.addNote((OneNote) currentNote.clone());
	  tmpNotes.fitParameters();
	  tmpNotes.setPosition(currentNote.getPosition());
	  onoff = true;
	}
      }
      if (onoff == false)
      {
	if (onoff == false)
	{
	  Notes newNotes = new Notes((OneNote) currentNote.clone());
	  newNotes.setPosition(currentNote.getPosition());
	  samePosition.ensureCapacity(samePosition.size() + 1);
	  samePosition.add(newNotes);
	}
      }
    }
    return samePosition;
  }

  private static void MA(Notes returnNotes, Notes first, Notes second)
  {
    int until = second.getNumOfNotes();
    long increasePos = 0;
    System.out.println();
    System.out.println("MA, first.getNumOfNotes() : " + first.getNumOfNotes()
	+ " second.getNumOfNotes() : " + until);
    long tmpPos = 0;
    // if(until > 4)
    // until = 4;
    for (int i = 0; i < until; i++)
    {
      Notes secondArg = new Notes((OneNote) second.getNote(i));
      Notes storageNotes = new Notes();
      TreeOperators.A(storageNotes, first, secondArg);
      tmpPos = storageNotes.getDuration();
      storageNotes.setPosition(increasePos);
      returnNotes.addNotes(storageNotes);
      increasePos += tmpPos;
      System.out.print(i + ". ");
    }
  }

  private static void MP(Notes returnNotes, Notes first, Notes second)
  {
    int until = second.getNumOfNotes();
    long increasePos = 0;
    System.out.println();
    System.out.println("MA, first.getNumOfNotes() : " + first.getNumOfNotes()
	+ " second.getNumOfNotes() : " + until);
    long tmpPos = 0;
    // if(until > 4)
    // until = 4;
    for (int i = 0; i < until; i++)
    {
      Notes secondArg = new Notes((OneNote) second.getNote(i));
      Notes storageNotes = new Notes();
      TreeOperators.P(storageNotes, first, secondArg);
      tmpPos = storageNotes.getDuration();
      storageNotes.setPosition(increasePos);
      returnNotes.addNotes(storageNotes);
      // increasePos = returnNotes.getDuration();
      increasePos += tmpPos;
      System.out.print(i + ". ");
    }
  }

  private static void MD(Notes returnNotes, Notes first, Notes second)
  {
    int until = second.getNumOfNotes();
    long increasePos = 0;
    System.out.println();
    System.out.println("MA, first.getNumOfNotes() : " + first.getNumOfNotes()
	+ " second.getNumOfNotes() : " + until);
    long tmpPos = 0;
    // if(until > 4)
    // until = 4;
    for (int i = 0; i < until; i++)
    {
      Notes secondArg = new Notes((OneNote) second.getNote(i));
      Notes storageNotes = new Notes();
      TreeOperators.D(storageNotes, first, secondArg);
      tmpPos = storageNotes.getDuration();
      storageNotes.setPosition(increasePos);
      returnNotes.addNotes(storageNotes);
      // increasePos = returnNotes.getDuration();
      increasePos += tmpPos;
      System.out.print(i + ". ");
    }
  }

  private static void MS(Notes returnNotes, Notes first, Notes second)
  {
    ArrayList<Notes> firstR = TreeOperators.detectNoteOfSamePositions(first);
    ArrayList<Notes> secondR = TreeOperators.detectNoteOfSamePositions(second);
    int nfR = firstR.size();
    int nsR = secondR.size();
    int counter = 0;
    int until = nfR + nsR;
    //int fc = 0;
    //int fs = 0;
    long position = 0;
    while (counter < until)
    {
      Notes tmpNotes;
      if (firstR.size() > 0 && secondR.size() > 0)
      {
	if (counter % 2 == 0)
	{
	  tmpNotes = (Notes) firstR.get(0);
	  firstR.remove(0);
	} else
	{
	  tmpNotes = (Notes) secondR.get(0);
	  secondR.remove(0);
	}
      } else
      {
	if (secondR.size() == 0)
	{
	  tmpNotes = (Notes) firstR.get(0);
	  firstR.remove(0);
	} else
	{
	  tmpNotes = (Notes) secondR.get(0);
	  secondR.remove(0);
	}
      }
      tmpNotes.fitParameters();
      tmpNotes.setPosition(position);
      returnNotes.addNotes(tmpNotes);
      position = returnNotes.getDuration();
      counter++;
      System.out.print(". ");
    }
  }

  private static void MU(Notes returnNotes, Notes first, Notes second)
  {
    ArrayList<Notes> firstR = TreeOperators.detectNoteOfSamePositions(first);
    ArrayList<Notes> secondR = TreeOperators.detectNoteOfSamePositions(second);
    int nfR = firstR.size();
    int nsR = secondR.size();
    int counter = 0;
    int until = nfR + nsR;
    //int fc = 0;
    //int fs = 0;
    long position = 0;
    while (counter < until)
    {
      Notes tmpNotesFirst;
      Notes tmpNotesSecond;
      if (firstR.size() > 0 && secondR.size() > 0)
      {
	tmpNotesFirst = (Notes) firstR.get(0);
	firstR.remove(0);
	tmpNotesSecond = (Notes) secondR.get(0);
	secondR.remove(0);
	long vdr = 0;
	int uroul = tmpNotesFirst.getNumOfNotes();
	for (int i = 0; i < uroul; i++)
	{
	  OneNote afriNote = tmpNotesFirst.getNote(i);
	  afriNote.setPosition(0);
	  if (afriNote.getDuration() > vdr)
	    vdr = afriNote.getDuration();
	}
	uroul = tmpNotesSecond.getNumOfNotes();
	for (int i = 0; i < uroul; i++)
	{
	  OneNote afriNote = tmpNotesSecond.getNote(i);
	  afriNote.setPosition(0);
	  if (afriNote.getDuration() > vdr)
	    vdr = afriNote.getDuration();
	}
	uroul = tmpNotesFirst.getNumOfNotes();
	for (int i = 0; i < uroul; i++)
	{
	  OneNote afriNote = tmpNotesFirst.getNote(i);
	  afriNote.setDuration(vdr);
	}
	uroul = tmpNotesSecond.getNumOfNotes();
	for (int i = 0; i < uroul; i++)
	{
	  OneNote afriNote = tmpNotesSecond.getNote(i);
	  afriNote.setDuration(vdr);
	}
	tmpNotesFirst.fitParameters();
	tmpNotesSecond.fitParameters();
	Notes makeNotes = new Notes();
	TreeOperators.U(makeNotes, tmpNotesFirst, tmpNotesSecond);
	makeNotes.setPosition(position);
	returnNotes.addNotes(makeNotes);
	position = returnNotes.getDuration();
	counter += 2;
      } else
      {
	if (secondR.size() == 0)
	{
	  tmpNotesFirst = (Notes) firstR.get(0);
	  firstR.remove(0);
	} else
	{
	  tmpNotesFirst = (Notes) secondR.get(0);
	  secondR.remove(0);
	}
	tmpNotesFirst.fitParameters();
	tmpNotesFirst.setPosition(position);
	returnNotes.addNotes(tmpNotesFirst);
	position = returnNotes.getDuration();
	counter++;
      }
      System.out.print(". ");
    }
  }

  private static void FILP(Notes returnNotes, Notes first, Notes second, double baseArg)
  {
    int step = (int) Math.round(Math.floor(baseArg));
    OneNote tmpNote = first.getNote(first.getNumOfNotes() - 1);
    int lastPitchOfFirst = tmpNote.getNoteNumber();
    tmpNote = second.getNote(0);
    int firstPitchOfSecond = tmpNote.getNoteNumber();
    int tmpValue = lastPitchOfFirst;
    int until = 0;
    boolean dori = true;
    if (lastPitchOfFirst > firstPitchOfSecond)
    {
      dori = false;
      while (tmpValue > firstPitchOfSecond)
      {
	tmpValue -= step;
	until++;
      }
    } else
    {
      dori = true;
      while (tmpValue < firstPitchOfSecond)
      {
	tmpValue += step;
	until++;
      }
    }

    returnNotes.addNotes(first);
    returnNotes.fitParameters();
    long position = returnNotes.getDuration();
    Notes nextNotes = (Notes) first.clone();
    for (int i = 0; i < until; i++)
    {
      Notes tmpNotes = new Notes();
      if (dori)
	TreeOperators.TP(tmpNotes, nextNotes, (double) step);
      else
	TreeOperators.TP(tmpNotes, nextNotes, (double) step * -1.0);
      tmpNotes.fitParameters();
      tmpNotes.setPosition(position);
      returnNotes.addNotes(tmpNotes);
      returnNotes.fitParameters();
      position = returnNotes.getDuration();
      System.out.print(". ");
      nextNotes = (Notes) tmpNotes.clone();
    }
    Notes tmpNotes = (Notes) second.clone();
    tmpNotes.setPosition(position);
    returnNotes.addNotes(tmpNotes);
    returnNotes.fitParameters();
  }

  private static void FILA(Notes returnNotes, Notes first, Notes second, double baseArg){
    double step = baseArg;
    int counter = 0;

    OneNote tmpNote = new OneNote();
    int lastVelocityOfFirst = 0;

    while (counter < first.getNumOfNotes())
    {
      tmpNote = first.getNote(first.getNumOfNotes() - 1 - counter);
      lastVelocityOfFirst = tmpNote.getVelocity();
      if (lastVelocityOfFirst != 0)
	break;
      counter++;

    }
    if (lastVelocityOfFirst == 0)
      lastVelocityOfFirst = 10;

    int firstVelocityOfSecond = 0;
    int tmpValue = 0;
    counter = 0;
    while (counter < second.getNumOfNotes())
    {
      tmpNote = second.getNote(counter);
      firstVelocityOfSecond = tmpNote.getVelocity();
      if (firstVelocityOfSecond != 0)
	break;
      counter++;
    }

    if (lastVelocityOfFirst <= 10)
      lastVelocityOfFirst = 10;

    tmpValue = lastVelocityOfFirst;
    // System.out.println("lastVelocityOfFirst is : " + tmpValue + "
    // firstVelocityOfSecond is : " + firstVelocityOfSecond);
    int until = 0;
    boolean dori = true;
    if (lastVelocityOfFirst > firstVelocityOfSecond)
    {
      dori = false;
      while (tmpValue > firstVelocityOfSecond)
      {
	tmpValue = (int) Math.round(Math.floor(tmpValue * (1.0 - step)));
	until++;
	if (tmpValue == firstVelocityOfSecond)
	  break;
      }
    } else
    {
      dori = true;
      while (tmpValue < firstVelocityOfSecond)
      {
	tmpValue = (int) Math.round(Math.floor((double) tmpValue * (step + 1.0)));
	until++;
	if (tmpValue == firstVelocityOfSecond)
	  break;
      }
    }

    returnNotes.addNotes(first);
    returnNotes.fitParameters();
    long position = returnNotes.getDuration();

    Notes nextNotes = (Notes) first.clone();
    for (int i = 0; i < until; i++)
    {
      Notes tmpNotes = new Notes();
      if (dori)
	TreeOperators.TPA(tmpNotes, nextNotes, (double) (step + 1.0));
      else
	TreeOperators.TPA(tmpNotes, nextNotes, (double) (1.0 - step));
      tmpNotes.fitParameters();
      tmpNotes.setPosition(position);
      returnNotes.addNotes(tmpNotes);
      returnNotes.fitParameters();
      position = returnNotes.getDuration();
      nextNotes = (Notes) tmpNotes.clone();
      System.out.print(". ");
    }
    Notes tmpNotes = (Notes) second.clone();
    tmpNotes.setPosition(position);
    returnNotes.addNotes(tmpNotes);
    returnNotes.fitParameters();
  }

  private static void TPA(Notes returnNotes, Notes first, double baseArg)
  {
    int until = first.getNumOfNotes();
    for (int i = 0; i < until; i++)
    {
      OneNote tmpNote = first.getNote(i);
      OneNote tmpNote2 = (OneNote) tmpNote.clone();
      tmpNote2.setVelocity((int) Math.round(Math.floor((double) tmpNote.getVelocity() * baseArg)));
      returnNotes.addNote(tmpNote2);
    }
    returnNotes.fitParameters();
  }

  /*
  private static void FILD(Notes returnNotes, Notes first, Notes second, double baseArg)
  {

  }*/

  private static void CAR(Notes returnNotes, Notes first, double baseArg)
  {
    int until = first.getNumOfNotes();
    if (first.getNumOfNotes() > 1)
    {
      int carNum = (int) Math.round(Math.floor((double) first.getNumOfNotes() * baseArg)) + 1;
      if (carNum < 1)
	carNum = 1;

      until = carNum;
      for (int i = 0; i < until; i++)
      {
	OneNote tmpNote = (OneNote) first.getNote(i);
	returnNotes.addNote((OneNote) tmpNote.clone());
      }
      returnNotes.fitParameters();
    } else
      returnNotes.addNotes((Notes) first.clone());

    if (returnNotes.getNumOfNotes() == 0)
    {
      System.out.println("TreeOperators:CAR: returnNotes has no Note: " + first.getNumOfNotes());
    }
  }

  private static void CDR(Notes returnNotes, Notes first, double baseArg)
  {
    Notes tmpNotes = (Notes) first.clone();
    tmpNotes.fitParameters();
    int cdrNum = 1;
    if (returnNotes.getNumOfNotes() > 1)
    {
      cdrNum = (int) Math.round(Math.floor((double) tmpNotes.getNumOfNotes() * baseArg));
      if (cdrNum > tmpNotes.getNumOfNotes() - 1)
	cdrNum = tmpNotes.getNumOfNotes() - 1;
      for (int i = 0; i < cdrNum; i++)
      {
	tmpNotes.removeNote(0);
      }
      tmpNotes.fitParameters();
      returnNotes.addNotes(tmpNotes);
    } else
      returnNotes.addNotes((Notes) first.clone());
  }

  private static void ACML(Notes returnNotes, Notes first)
  {
    ArrayList<Notes> samePosition = TreeOperators.detectNoteOfSamePositions(first);
    int until = samePosition.size();
    returnNotes.addNotes(first);
    returnNotes.fitParameters();
    long position = returnNotes.getDuration();
    for (int i = 1; i < until; i++)
    {
      for (int j = 0; j < i; j++)
      {
	Notes tmpNotes = (Notes) samePosition.get(j);
	tmpNotes.fitParameters();
	tmpNotes.setPosition(position);
	returnNotes.addNotes(tmpNotes);
	returnNotes.fitParameters();
	position = returnNotes.getDuration();
	System.out.print(". ");
      }
    }
  }
  
  
  
/*
  private static void PMA(Notes returnNotes, Notes first, Notes second)
  {

  }

  private static void PMP(Notes returnNotes, Notes first, Notes second)
  {

  }

  private static void PMD(Notes returnNotes, Notes first, Notes second)
  {

  }

  private static int[] extractPattern(Notes first, int mode)
  {
    // Pattern
    // 1:/ 2:\ 3:/\ 4:\/ 5:-
    if (mode == TreeOperators.A)
    {

    } else if (mode == TreeOperators.P)
    {

    } else if (mode == TreeOperators.D)
    {

    }
    return new int[1];

  }
*/
  public static ArrayList<Notes> detectNoteOfSamePositions(Notes notes)
  {
	  //同じポジションを持つNoteをNotesにまとめ，そのArrayListで返す
    ArrayList<Notes> sortedNotes = new ArrayList<Notes>(1);
    OneNote tmpNote = (OneNote) notes.getNote(0);
    sortedNotes.add(new Notes(tmpNote));

    int until = notes.getNumOfNotes();
    for (int i = 1; i < until; i++){
      int nomul = sortedNotes.size();
      int counter = 0;
      tmpNote = (OneNote) notes.getNote(i);
      long inPosition = tmpNote.getPosition();
      boolean inSame = false;
      while (counter < nomul)
      {
	Notes tmpNotesIn = (Notes) sortedNotes.get(counter);
	OneNote tmpNoteIn = tmpNotesIn.getNote(0).clone();
	if (tmpNoteIn.getPosition() == inPosition)
	{
	  tmpNotesIn.addNote(tmpNote);
	  inSame = true;
	  break;
	}
	counter++;
      }
      if (!inSame)
      {
	sortedNotes.ensureCapacity(sortedNotes.size() + 1);
	sortedNotes.add(new Notes(tmpNote));
      }
    }
    return sortedNotes;
  }
/*
  private static int[] getDurationString()
  {
    int tickBase = 16;
    //int kindOfNoteLength = 8;
    int[] lengthString = new int[8];

    int shibu = tickBase;
    lengthString[4] = shibu;

    int hachibu = shibu / 2;
    lengthString[6] = hachibu;

    int juurokubu = hachibu / 4;
    lengthString[7] = juurokubu;

    int nibu = shibu * 2;
    lengthString[2] = nibu;

    int zen = shibu * 4;
    lengthString[0] = zen;

    int futenshibu = shibu + hachibu;
    lengthString[3] = futenshibu;

    int futennibu = nibu + shibu;
    lengthString[1] = futennibu;

    int futenhachibu = hachibu + juurokubu;
    lengthString[5] = futenhachibu;

    return lengthString;
  }
  */
}
