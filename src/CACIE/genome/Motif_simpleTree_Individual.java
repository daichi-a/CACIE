package CACIE.genome;

import java.util.ArrayList;
import java.util.StringTokenizer;

import CACIE.RandomManager;
import CACIE.eventlist.CommonEventList;

public class Motif_simpleTree_Individual extends Abstract_Individual implements Runnable
{
  public static int MAX_DEPTH = -4;

  public static int CHROMOSOME_MAXLENGTH = 6;

  public static int CHROMOSOME_MINLENGTH = 2;

  protected ArrayList<TreeNodes> genomeArray;
  protected ArrayList<Notes> terminalNodesArray;
  protected boolean statusHavingNoteArray = false;
  protected int mode;
  protected ArrayList<Notes> finalNoteArray;
  protected ArrayList<Notes> notes;
  protected int numOfNodes;
  protected int numOfNotes;
  protected boolean operatorFrom = false;
  protected ArrayList<String> operatorArray;
  protected ArrayList<String> configArray;
  protected int chromosomeMaxDepth;
  protected int chromosomeMaxLength;
  protected int chromosomeMinLength;
  protected boolean rhythmFilter = false;
  protected String rhythmFilterBeat;
  protected boolean chordFilter = false;
  protected boolean harmonize = false;
  protected int frontOffset = 0;
  protected int replacingNTOffset = 0;
  private CommonEventList evStocker;// For Threading

  public void run()
  {
    this.evStocker = this.convertToEventList();
  }

  public boolean getRhythmFilterSwitch()
  {
    return rhythmFilter;
  }

  public boolean getChordFilterSwitch()
  {
    return chordFilter;
  }

  public boolean getHarmonizeSwitch()
  {
    return harmonize;
  }

  public CommonEventList getGeneratedEventList()
  {
    return this.evStocker;
  }

  public Motif_simpleTree_Individual()
  {
  }

  public Motif_simpleTree_Individual(int IDNumber, int mode, ArrayList<Notes> Notes)
  {
    // Notes : Array Of Notes
    notes = Notes;
    this.mode = mode;
    numOfNotes = (int) Math.round(Math.floor(RandomManager.getRandom() * (Motif_simpleTree_Individual.MAXNOTE - Motif_simpleTree_Individual.MINNOTE))) + Motif_simpleTree_Individual.MINNOTE;
    statusHavingNoteArray = true;
    this.IDNumber = IDNumber;
    terminalNodesArray = Notes;
    numOfNodes = -1;
    genomeArray = new ArrayList<TreeNodes>(0);
  }

  public Motif_simpleTree_Individual(int IDNumber, int mode, ArrayList<Notes> Notes, ArrayList<String> Operators, ArrayList<String> Configs)
  {
    this(IDNumber, mode, Notes);
    operatorArray = Operators;
    operatorFrom = true;
    configArray = Configs;
  }

  protected void extractConfigsForInitialize()
  {
    // Extract configs for chromosome initialize
    int lineCounter = 0;
    while (lineCounter < configArray.size())
    {
      String configLine = configArray.get(lineCounter);
      StringTokenizer st = new StringTokenizer(configLine);
      String command = st.nextToken();
      boolean depth = false, maxLength = false, minLength = false;
      if (command.equals("MAX_DEPTH"))
      {
        chromosomeMaxDepth = Integer.parseInt(st.nextToken());
        depth = true;
      } else if (command.equals("CHROMOSOME_MAXLENGTH"))
      {
        chromosomeMaxLength = Integer.parseInt(st.nextToken());
        maxLength = true;
      } else if (command.equals("CHROMOSOME_MINLENGTH"))
      {
        chromosomeMinLength = Integer.parseInt(st.nextToken());
        minLength = true;
      } else if (command.equals("MUTATION_REPLACING_NT_OFFSET"))
      {
        // ルートノードに近いノードをルートとするサブツリーを対象とした
        // Non-terminalからTerminalへのmutationをかけない
        // そのオフセット
        replacingNTOffset = Integer.parseInt(st.nextToken());
      } else if (command.equals("LOG_FILE_NAME"))
      {

      } else if (command.equals("RHYTHM_FILTER"))
      {
        String rhythmFilterSwitch = st.nextToken();
        if (rhythmFilterSwitch.equals("ON"))
        {
          rhythmFilter = true;
          rhythmFilterBeat = st.nextToken();
        }
      } else if (command.equals("CHORD_FIX"))
      {
        String codeFilterSwitch = st.nextToken();
        if (codeFilterSwitch.equals("ON"))
        {
          chordFilter = true;
        }
      } else if (command.equals("CHORD_HARMONIZE"))
      {
        String harmonizeFilterSwitch = st.nextToken();
        if (harmonizeFilterSwitch.equals("ON"))
        {
          if (chordFilter == true)
          {
            System.err.println("CHORD_FIX and CHORD_HARMONIZE are used exclusionary. Exiting.");
            System.exit(1);
          } else
            harmonize = true;
        }
      } else
      {
        System.err.println(command + " is not registrated. Ignoring.");
      }

      if (depth == true && maxLength == true && minLength == true)
        break;

      lineCounter++;
    }
  }

  public Abstract_Individual clone()
  {
    Motif_simpleTree_Individual returnInd = new Motif_simpleTree_Individual(IDNumber, mode, notes);
    returnInd.numOfNotes = numOfNotes;
    returnInd.statusHavingNoteArray = statusHavingNoteArray;
    returnInd.operatorFrom = operatorFrom;
    returnInd.chromosomeMaxDepth = chromosomeMaxDepth;
    returnInd.chromosomeMaxLength = chromosomeMaxLength;
    returnInd.chromosomeMinLength = chromosomeMinLength;
    if (returnInd.operatorFrom == true)
    {
      returnInd.operatorArray = operatorArray;
    }
    int until = this.genomeArray.size();
    returnInd.genomeArray.clear();
    returnInd.genomeArray.ensureCapacity(until);
    for (int i = 0; i < until; i++)
    {
      TreeNodes tmpNode = (TreeNodes) this.genomeArray.get(i);
      returnInd.genomeArray.add(tmpNode.clone());
    }

    return returnInd;
  }

  public void setConfigArray(ArrayList<String> configArray)
  {
    this.configArray = configArray;
  }

  public ArrayList<String> getConfigArray()
  {
    return configArray;
  }

  public void Mutation()
  {
    // For test only
    this.mutate();
    // System.out.println(this.getGenomeString());
  }

  protected void mutate()
  {
    // if(TreeIndividuals.checkStackCount(genomeArray)){
    // System.err.println("Motif_simpleTree_Individual: mutate: Wrong chromosome given.");
    // System.err.println(getGenomeString());
    // System.exit(1);
    // }
    // 特殊なノードがトップにあった時
    frontOffset = 0;
    if (getHarmonizeSwitch() || getChordFilterSwitch())
      frontOffset++;
    if (getRhythmFilterSwitch())
      frontOffset++;

    // SpecialCale : genome length is too short
    if (genomeArray.size() <= chromosomeMinLength * 3)
    {
      replaceTtoN();
    } else if (genomeArray.size() >= chromosomeMaxLength * 3)
    {
      replaceNtoT();
    } else
    {
      // Normal
      int selecting = (int) (Math.round(Math.floor(RandomManager.getRandom() * 3)));
      if (selecting == 0)
        replaceNtoT();
      else if (selecting == 1)
        replaceTtoN();
      else
      {
        swapSubTree();
      }
      if (!TreeIndividuals.checkStackCount(genomeArray))
      {
        System.err.println("Motif_simplteTree_Individual: mutate: Faild to Mutate in mutation " + selecting);
        System.exit(1);
      }
    }
  }

  protected void swapSubTree()
  {
    ArrayList<TreeNodes> firstArray = new ArrayList<TreeNodes>(0);
    ArrayList<TreeNodes> secondArray = new ArrayList<TreeNodes>(0);
    ArrayList<TreeNodes> copyArray = TreeIndividuals.copyGenomeArray(genomeArray);
    if (!TreeIndividuals.checkStackCount(genomeArray))
    {
      System.err.println("Motif_simpleTree_Individual:swapSubTree: Illegal genome Array");
      System.exit(1);
    }
    if (!TreeIndividuals.checkStackCount(copyArray))
    {
      System.err.println("Motif_simpleTree_Individual:swapSubTree: Illegal copy Array");
      System.exit(1);
    }
    int until = copyArray.size();
    int firstFromIndex, firstToIndex, secondFromIndex, secondToIndex;

    // first
    int firstIndex = (int) Math.round(Math.floor(RandomManager.getRandom() * (until - frontOffset))) + frontOffset;
    if (firstIndex == 0)
      firstIndex = 1;
    TreeNodes tmpNode = copyArray.get(firstIndex);
    if (tmpNode.getTermOrNot() == TreeNodes.NONTERMINAL)
    {
      ArrayList<Integer> indexes = TreeIndividuals.getSubTreeIndex(copyArray, firstIndex);
      Integer tmpInt = (Integer) indexes.get(0);
      firstFromIndex = tmpInt.intValue();
      tmpInt = (Integer) indexes.get(1);
      firstToIndex = tmpInt.intValue();
      firstArray = TreeIndividuals.extractGenomeArray(copyArray, firstFromIndex, firstToIndex);
      if (!TreeIndividuals.checkStackCount(firstArray))
      {
        System.err.println("Motif_simpleTree_Individuals: swapSubTree: First Array has wrong stack counts.");
        System.err.println(TreeIndividuals.getGenomeString(firstArray));
        System.err.println(firstFromIndex + "," + firstToIndex);
        // System.exit(1);
      }
    } else
    {
      // If case terminal node, swap target is one node.
      firstArray.add(tmpNode.clone());
      firstFromIndex = firstIndex;
      firstToIndex = firstIndex;
    }

    // second
    int secondIndex = (int) Math.round(Math.floor(RandomManager.getRandom() * (until - frontOffset))) + frontOffset;
    if (secondIndex == 0)
      secondIndex = 1;
    tmpNode = (TreeNodes) copyArray.get(secondIndex);
    if (tmpNode.getTermOrNot() == TreeNodes.NONTERMINAL)
    {
      ArrayList<Integer> indexes = TreeIndividuals.getSubTreeIndex(copyArray, secondIndex);
      Integer tmpInt = (Integer) indexes.get(0);
      secondFromIndex = tmpInt.intValue();
      tmpInt = (Integer) indexes.get(1);
      secondToIndex = tmpInt.intValue();
      secondArray = TreeIndividuals.extractGenomeArray(copyArray, secondFromIndex, secondToIndex);
    } else
    {
      secondArray.add(tmpNode.clone());
      secondFromIndex = secondIndex;
      secondToIndex = secondIndex;
    }
    // if(TreeIndividuals.checkStackCount(secondArray)){
    // System.err.println("Motif_simpleTree_Individuals:swapSubTree:Second Array has wrong stack counts.");
    // System.err.println(TreeIndividuals.getGenomString(secondArray));
    // System.exit(1);
    // }

    TreeIndividuals.replaceNodes(copyArray, firstArray, firstFromIndex, firstToIndex);
    TreeIndividuals.replaceNodes(copyArray, secondArray, secondFromIndex, secondToIndex);
    genomeArray = copyArray;
  }

  protected void replaceNtoT()
  {
    // 特殊なノードがトップに来ている時の余白の設定
    int frontOffset = 0;
    if (getHarmonizeSwitch() || getChordFilterSwitch())
      frontOffset++;
    if (getRhythmFilterSwitch())
      frontOffset++;

    // Check
    ArrayList<TreeNodes> copyArray = TreeIndividuals.copyGenomeArray(genomeArray);
    if (copyArray.size() < frontOffset + replacingNTOffset)
    {
      // 短すぎる時はそのまま返す
      return;
    }
    int until = copyArray.size();
    int ntcounter = 0;
    for (int i = 0; i < until; i++)
    {
      TreeNodes tmpNode = (TreeNodes) copyArray.get(i);
      if (tmpNode.getTermOrNot() == TreeNodes.NONTERMINAL)
      {
        ntcounter++;
      }
    }
    int ntposition = (int) Math.round(Math.floor(RandomManager.getRandom() * (ntcounter - frontOffset - replacingNTOffset))) - 1 + frontOffset + replacingNTOffset;
    if (ntposition <= 1)
      ntposition = 1;
    else if (ntposition > (ntcounter - 2))
      ntposition = ntcounter - 2;
    ntcounter = 0;
    int counter = 0;
    while (counter < until)
    {
      TreeNodes tmpNode = (TreeNodes) copyArray.get(counter);
      if (tmpNode.getTermOrNot() == TreeNodes.NONTERMINAL)
      {
        ntcounter++;
      }
      counter++;
      if (ntcounter == ntposition)
        break;
    }
    while (counter < until)
    {
      TreeNodes tmpNode = (TreeNodes) copyArray.get(counter);
      counter++;
      if (tmpNode.getTermOrNot() != TreeNodes.NONTERMINAL)
        break;
    }
    ArrayList<Integer> index = TreeIndividuals.getPartTreeIndex(copyArray, counter - 1);
    Integer tmpInt = (Integer) index.get(0);
    int fromIndex = tmpInt.intValue();
    tmpInt = (Integer) index.get(1);
    int toIndex = tmpInt.intValue();
    ArrayList<TreeNodes> newNode = new ArrayList<TreeNodes>(1);
    newNode.ensureCapacity(1);
    newNode.add(copyArray.get(counter - 1));
    TreeIndividuals.replaceNodes(copyArray, newNode, fromIndex, toIndex);
    if (copyArray.size() == 1)
      replaceTtoN();
    else
      genomeArray = copyArray;
  }

  protected void replaceTtoN()
  {
    // 特殊なノードがトップに来ている時の余白の設定
    int frontOffset = 0;
    if (getHarmonizeSwitch() || getChordFilterSwitch())
      frontOffset++;
    if (getRhythmFilterSwitch())
      frontOffset++;

    // ArrayList<TreeNodes> returnArray = new ArrayList<TreeNodes>();
    ArrayList<TreeNodes> copyArray = TreeIndividuals.copyGenomeArray(genomeArray);
    // boolean stopDevelop = false;
    int counter = copyArray.size() - 1;
    int indexToDevelop = 0;
    int numOfTerminalNodes = 0;
    while (counter > frontOffset)
    {
      TreeNodes tmpNode = (TreeNodes) copyArray.get(counter);
      int termOrNot = tmpNode.getTermOrNot();
      if (termOrNot == TreeNodes.RECURSIVENODE || termOrNot == TreeNodes.TERMINAL)
        numOfTerminalNodes++;
      counter--;
    }

    int indexOfTerm = (int) Math.round(Math.floor(RandomManager.getRandom() * numOfTerminalNodes));

    counter = frontOffset;
    int countIndex = 0;
    int max = copyArray.size();
    while (counter < max)
    {
      TreeNodes tmpNode = (TreeNodes) copyArray.get(counter);
      int termOrNot = tmpNode.getTermOrNot();
      if (termOrNot == TreeNodes.RECURSIVENODE || termOrNot == TreeNodes.TERMINAL)
      {
        countIndex++;
        if (countIndex >= indexOfTerm)
        {
          indexToDevelop = counter;
          break;
        }
      }
      counter++;
    }
    ArrayList<Integer> tmpInts = TreeIndividuals.getPartTreeIndex(copyArray, indexToDevelop);
    Integer tmpInt = (Integer) tmpInts.get(0);
    int fromIndex = tmpInt.intValue();
    tmpInt = (Integer) tmpInts.get(1);
    int toIndex = tmpInt.intValue();

    if (fromIndex <= frontOffset || frontOffset > toIndex)
    {
      System.out.println("Motif_simpleTree_Individual: replaceTtoN: Faild to getSubtree.");
      genomeArray = TreeIndividuals.copyGenomeArray(genomeArray);
    } else
    {
      ArrayList<TreeNodes> partTree = TreeIndividuals.extractGenomeArray(copyArray, fromIndex, toIndex);
      ArrayList<TreeNodes> nextArray = new ArrayList<TreeNodes>();
      nextArray.ensureCapacity(copyArray.size() + partTree.size() - 1);
      for (int i = 0; i < copyArray.size(); i++)
      {
        TreeNodes tmpNode = (TreeNodes) copyArray.get(i);
        nextArray.add(tmpNode.clone());
      }
      TreeIndividuals.replaceNodes(nextArray, partTree, indexToDevelop);

      this.genomeArray = nextArray;
    }
  }

  public void generate()
  {
    if (this.operatorFrom)
      generateWithGivenOperator();
    else
      generateRandomly();
  }

  private void generateWithGivenOperator()
  {
    int counter = 0;
    int stackCount = 0;
    int tmpNumOfNodes = 0;
    extractConfigsForInitialize();

    genomeArray = new ArrayList<TreeNodes>();
    // 特殊なトップノードの指定 HARMONIZE or CHORDFIX, BARFIX

    // HARMONIZE or CHORDFIX
    genomeArray.ensureCapacity(genomeArray.size() + 1);
    if (chordFilter)
    {
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), operatorArray, "CHORDFIX", configArray);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    } else if (harmonize)
    {
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), operatorArray, "HARMONIZE", configArray);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    }

    // BARFIX
    genomeArray.ensureCapacity(genomeArray.size() + 1);
    if (rhythmFilter || chordFilter || harmonize)
    {
      // RHYTHM_FILTERがONの時 (ChordFilterかHarmonizeがONの時には，自動的にONになる
      String filterBeatIDString = "";
      if (rhythmFilterBeat.equals("4/4"))
        filterBeatIDString = "44";
      else if (rhythmFilterBeat.equals("3/4"))
        filterBeatIDString = "34";
      else if (rhythmFilterBeat.equals("6/8"))
        filterBeatIDString = "68";
      else
      {
        System.err.println("RhythmFilterBeat is not able to be recognized: " + rhythmFilterBeat);
        System.exit(1);
      }
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), operatorArray, "BARFIX" + filterBeatIDString, configArray);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    } else
    {
      // Filter関係機能ががオフのとき
      // 通常のNONTERMINAL NODEになる
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), operatorArray, configArray);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    }

    while (stackCount != 1)
    {
      // System.out.println("In generate Individual" + this.notes.size());
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(mode, notes.size(), operatorArray, configArray);
      // int modeOfNode = tmpNode.getMode();

      // In the special case (Too deep tree or too short genome)
      if (tmpNumOfNodes == 0)
      {
        tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), operatorArray, configArray);
      }
      if (tmpNumOfNodes < chromosomeMinLength)
      {
        tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), operatorArray, configArray);
      } else if (stackCount < chromosomeMaxDepth || tmpNumOfNodes > chromosomeMaxLength)
      {
        tmpNode = MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, mode, notes.size(), operatorArray, configArray);
      }

      genomeArray.ensureCapacity(genomeArray.size() + 1);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    }

    numOfNodes = genomeArray.size();
    numOfNotes = counter;
    int sumOfStackCount = 0;
    for (int i = 0; i < numOfNodes; i++)
    {
      TreeNodes tmpNode = genomeArray.get(i);
      sumOfStackCount += tmpNode.getStackCount();
      System.out.print(tmpNode.getOperatorAsString() + ":" + tmpNode.getStackCount() + ":" + sumOfStackCount + " ");
    }
    System.out.println(" ");

    if (!TreeIndividuals.checkStackCount(genomeArray))
    {
      System.err.println("Motif_simpleTree_Individual:generateWithGivenOperator: generate illegal chromosome.");
      System.err.println(getGenomeString());
      System.exit(1);
    }

  }

  private void generateRandomly()
  {
    int counter = 0;
    int stackCount = 0;
    int tmpNumOfNodes = 0;
    genomeArray = new ArrayList<TreeNodes>();
    {
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(TreeNodes.TOPNODE, mode, notes.size(), configArray);
      genomeArray.ensureCapacity(genomeArray.size() + 1);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    }

    while (stackCount != 1)
    {
      // System.out.println("In generate Individual" + this.notes.size());
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(mode, notes.size(), configArray);
      // int modeOfNode = tmpNode.getMode();

      // In the special case (Too deep tree or too short genome)
      if (tmpNumOfNodes == 0)
      {
        tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), configArray);
      }
      if (tmpNumOfNodes < CHROMOSOME_MINLENGTH)
      {
        tmpNode = MotifSimpleTreeNode.generate(TreeNodes.NONTERMINAL, mode, notes.size(), configArray);
      } else if (stackCount < MAX_DEPTH || tmpNumOfNodes > CHROMOSOME_MAXLENGTH)
      {
        tmpNode = MotifSimpleTreeNode.generate(TreeNodes.TERMINAL, mode, notes.size(), configArray);
      }

      genomeArray.ensureCapacity(genomeArray.size() + 1);
      genomeArray.add(tmpNode);
      stackCount += tmpNode.getStackCount();
      tmpNumOfNodes++;
    }
    numOfNodes = genomeArray.size();
    numOfNotes = counter;
    for (int i = 0; i < this.numOfNodes; i++)
    {
      TreeNodes tmpNode = (TreeNodes) genomeArray.get(i);
      System.out.print(tmpNode.getOperatorAsString() + ":" + tmpNode.getStackCount() + " ");
    }
    System.out.println(" ");
  }
  
  public void generateFromString(String str)
  {
    str = str.replaceAll("\\(", " ");
    str = str.replaceAll("\\)", " ").trim();
    System.out.println("str = \"" + str + "\"");
    String[] nodeStrArray = str.split("\\s+");
    
    int counter = 0;
    int stackCount = 0;
    int tmpNumOfNodes = 0;
    
    genomeArray = new ArrayList<TreeNodes>();
    
    for(int i = 0; i < nodeStrArray.length; i++ )
    {
      TreeNodes node = null;
      String nodeStr = nodeStrArray[i];
      
      boolean isTerminal = true;
      for(int s = 0; s < nodeStr.length(); s++ )
      {
        if( !('0' <= nodeStr.charAt(s) && '9' >= nodeStr.charAt(s)) )
        {
          isTerminal = false;
          break;
        }
      }
      if( isTerminal ) 
      {
        node = MotifSimpleTreeNode.generateTerminal(0, 0, Integer.valueOf(nodeStr), configArray);  
      }
      else
      {
        node = MotifSimpleTreeNode.generate(-1, -1, -1, this.operatorArray, nodeStrArray[i], configArray);
      }
      stackCount += node.getStackCount();
      genomeArray.add(node);
      tmpNumOfNodes++;
    }
    
    numOfNodes = genomeArray.size();
    numOfNotes = counter;
    for (int i = 0; i < this.numOfNodes; i++)
    {
      TreeNodes tmpNode = (TreeNodes) genomeArray.get(i);
      System.out.print(tmpNode.getOperatorAsString() + ":" + tmpNode.getStackCount() + " ");
    }
    System.out.println("end: generate from string");
  }
  
  public boolean setGenomArray(ArrayList<TreeNodes> newGenome)
  {
    boolean returnValue = false;
    // genomeArray.clear();
    // for(int i=0; i<newGenome.size(); i++)
    // genomeArray.add(newGenome.get(i));

    int until = newGenome.size();
    genomeArray.clear();
    genomeArray.ensureCapacity(until);
    for (int i = 0; i < until; i++)
    {
      TreeNodes tmpNode = (TreeNodes) newGenome.get(i);
      genomeArray.add(tmpNode.clone());
    }
    numOfNodes = genomeArray.size();
    int tmpSC = 0;
    for (int i = 0; i < this.numOfNodes; i++)
    {
      TreeNodes tmpNode = (TreeNodes) genomeArray.get(i);
      tmpSC += tmpNode.getStackCount();
    }
    if (tmpSC == 1)
      returnValue = true;
    return returnValue;
  }

  // tanji's addition (protected -> public to use in EvaluationIndividual
  // class)
  public ArrayList<TreeNodes> getGenomeArray()
  {
    return genomeArray;
  }

  //

  public void makeEmptyGenome(int size)
  {
    genomeArray = new ArrayList<TreeNodes>(size);
  }

  public CommonEventList convertToEventList()
  {
    CommonEventList eventList = this.culcEventList();
    return eventList;
  }

  public void convertFromEventList(CommonEventList eventList)
  {
    String tmpGenomString = eventList.getGenomeString();
    // int beforeIndex = 0, tmpIndex;
    StringTokenizer tmpstken = new StringTokenizer(tmpGenomString, " ");
    int numOfElements = tmpstken.countTokens();
    String tmpStEl[] = new String[numOfElements];
    for (int i = 0; i < numOfElements; i++)
    {
      tmpStEl[i] = tmpstken.nextToken();
    }
    genomeArray = new ArrayList<TreeNodes>(numOfElements);

    for (int i = 0; i < numOfElements; i++)
    {
      TreeNodes tmpNode = MotifSimpleTreeNode.generate(tmpStEl[i], this.terminalNodesArray.size(), configArray);
      genomeArray.add(tmpNode);
    }
    numOfElements = genomeArray.size();
    int checkSC = 0;
    for (int i = 0; i < numOfElements; i++)
    {
      TreeNodes tmpNode = (TreeNodes) genomeArray.get(i);
      checkSC += tmpNode.getStackCount();
    }
    TreeIndividuals.fitTerminalNodes(genomeArray, terminalNodesArray.size());
    if (checkSC != 1)
    {
      System.err.println("ConvertFromEventList : Faild to convert. Illegal Genome!");
      System.err.println(eventList.getGenomeString());
    }
    numOfNodes = genomeArray.size();
  }

  public String getGenomeString()
  {
    String returnString = new String();
    for (int i = 0; i < genomeArray.size(); i++)
    {
      TreeNodes tmpNode = (TreeNodes) genomeArray.get(i);
      returnString = new String(returnString.toString() + tmpNode.getOperatorAsString() + " ");

    }
    return returnString;
  }

  public int getNumOfNotes()
  {
    return this.numOfNotes;
  }

  public int getNumOfNodes()
  {
    return genomeArray.size();
  }

  public int getStackCount(int index)
  {
    TreeNodes tmpNode = (TreeNodes) genomeArray.get(index);
    return tmpNode.getStackCount();
  }

  protected int getNodeMode(int index)
  {
    TreeNodes tmpNode = (TreeNodes) genomeArray.get(index);
    return tmpNode.getMode();
  }

  public TreeNodes getNode(int index)
  {
    return genomeArray.get(index);
  }

  public int getIndex(TreeNodes node)
  {
    for (int i = 0; i < genomeArray.size(); i++)
    {
      if (genomeArray.get(i) == node)
        return i;
    }
    return -1;
  }

  protected int getOperator(int index)
  {
    TreeNodes tmpNode = (TreeNodes) genomeArray.get(index);
    return tmpNode.getOperator();
  }

  private CommonEventList culcEventList()
  {
    ArrayList<TreeNodes> developpedArray = TreeIndividuals.recursiveGenomeDevelopment(genomeArray);

    CommonEventList eventList = new CommonEventList(IDNumber);
    int[] stackCountOfEachNode = new int[developpedArray.size()];
    int[] stackCountArrayCulced = new int[developpedArray.size()];
    int tmpSCAdded = 0;
    finalNoteArray = new ArrayList<Notes>();
    TreeNodes tmpNode;
    // System.out.println(this.getGenomString());
    for (int i = 0; i < developpedArray.size(); i++)
    {
      tmpNode = (TreeNodes) developpedArray.get(i);
      stackCountOfEachNode[i] = tmpNode.getStackCount();
      tmpSCAdded = stackCountArrayCulced[i] = tmpSCAdded + stackCountOfEachNode[i];
      System.out.print(tmpNode.getOperatorAsString() + " ");
    }
    System.out.println();

    finalNoteArray = recursiveEvaluation(developpedArray, new ArrayList<Notes>(), new ArrayList<TreeNodes>());

    System.out.println();

    for (int i = 0; i < finalNoteArray.size(); i++)
    {
      Notes tmpNotes = (Notes) finalNoteArray.get(i);
      eventList.add((Notes) tmpNotes.clone());
    }
    System.out.println(getGenomeString());
    if (getGenomeString() != null)
      eventList.setGenomeString(getGenomeString());
    else
    {
      System.err.println("Motif_simpleTree_Individual:culcEventList: genomeString is null.");
      System.exit(1);
    }

    return eventList;
  }

  private ArrayList<Notes> recursiveEvaluation(ArrayList<TreeNodes> genomList, ArrayList<Notes> noteList, ArrayList<TreeNodes> returnResultGenomeList)
  {

    // ArrayList result[] = new ArrayList[2];
    ArrayList<TreeNodes> resultGenomList = new ArrayList<TreeNodes>();
    ArrayList<Notes> resultNoteList = new ArrayList<Notes>();

    // ArrayList returnArray[] = new ArrayList[2];
    ArrayList<TreeNodes> returnGenomList = new ArrayList<TreeNodes>();
    ArrayList<Notes> returnNoteList = new ArrayList<Notes>();

    // System.out.println("Motif_simpleTree_Individual: genomList.size : " +
    // genomList.size());

    if (genomList.size() > 1)
    {
      TreeNodes tmpNode = (TreeNodes) genomList.get(0);
      ArrayList<TreeNodes> nextGenomList = new ArrayList<TreeNodes>(genomList.size());
      for (int i = 0; i < genomList.size(); i++)
        nextGenomList.add(genomList.get(i));

      nextGenomList.remove(0);

      if (tmpNode.getMode() == TreeNodes.TERMINAL || tmpNode.getMode() == TreeNodes.RECURSIVENODE)
      {
        noteList.ensureCapacity(noteList.size() + 1);
        Notes tmpNotes = (Notes) this.notes.get(tmpNode.getData());
        if (tmpNotes.getNumOfNotes() == 0)
          System.out.println("Motif_simpleTree_Individual: No note in " + tmpNode.getData());
        noteList.add(tmpNotes.clone());
        returnNoteList = noteList;
        returnGenomList = nextGenomList;
      } else if (tmpNode.getMode() == TreeNodes.NONTERMINAL)
      {

        ArrayList<TreeNodes> currentResultGenomeList = new ArrayList<TreeNodes>();

        resultNoteList = recursiveEvaluation(nextGenomList, new ArrayList<Notes>(), currentResultGenomeList);

        resultGenomList = new ArrayList<TreeNodes>();
        for (int i = 0; i < currentResultGenomeList.size(); i++)
          resultGenomList.add(currentResultGenomeList.get(i));

        Notes firstArg = (Notes) resultNoteList.get(0);
        // resultNoteList.remove(0);

        if (tmpNode.getStackCount() == 0)
        {
          // In the Case of the node has only 1 branch
          ArrayList<Notes> arrayToSend = new ArrayList<Notes>(1);
          arrayToSend.ensureCapacity(1);
          arrayToSend.add(firstArg);
          returnNoteList = tmpNode.evaluate(tmpNode.getOperator(), arrayToSend);
        } else if (tmpNode.getStackCount() == -1)
        {
          // In the Case of the node has 2 branch

          currentResultGenomeList.clear();

          resultNoteList = recursiveEvaluation(resultGenomList, new ArrayList<Notes>(), currentResultGenomeList);

          resultGenomList = new ArrayList<TreeNodes>();
          for (int i = 0; i < currentResultGenomeList.size(); i++)
            resultGenomList.add(currentResultGenomeList.get(i));

          Notes secondArg = (Notes) resultNoteList.get(0);
          // resultNoteList.remove(0);

          // Process each operator
          ArrayList<Notes> arrayToSend = new ArrayList<Notes>(2);
          arrayToSend.ensureCapacity(2);
          arrayToSend.add(firstArg);
          arrayToSend.add(secondArg);
          returnNoteList = tmpNode.evaluate(tmpNode.getOperator(), arrayToSend);
        }
        returnGenomList = resultGenomList;
      }
    } else
    {
      // Last 1
      noteList.ensureCapacity(noteList.size() + 1);
      TreeNodes tmpNode = (TreeNodes) genomList.get(0);
      // Finish to get last Genom
      Notes singleNote = (Notes) this.notes.get(tmpNode.getData());
      noteList.add(singleNote.clone());
      // System.out.println("last NoteList : " + noteList.size());
      returnNoteList = noteList;
    }
    if (returnNoteList.size() == 0)
    {
      System.out.println(" ");
      System.out.println("Motif_simpleTree_Indivdual:recursiveEvaluate: returnNoteList has no note!!");
    }

    returnResultGenomeList.ensureCapacity(returnGenomList.size());
    for (int i = 0; i < returnGenomList.size(); i++)
      returnResultGenomeList.add(returnGenomList.get(i));
    // returnResultNoteList.ensureCapacity(returnNoteList.size());
    // for(int i=0; i<returnNoteList.size(); i++)
    // returnResultNoteList.add(resultNoteList.get(i));

    // returnArray[0] = returnGenomList;
    // returnArray[1] = returnNoteList;
    System.out.print(". ");
    // return returnArray;
    return returnNoteList;
  }

  public String makeGenomeString(int[] stackCountArrayCulced, int[] stackCountOfEachNode)
  {
    String returnString = new String();
    return returnString;
  }
}
