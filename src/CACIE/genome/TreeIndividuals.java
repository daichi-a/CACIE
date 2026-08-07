package CACIE.genome;

import java.util.ArrayList;

import CACIE.RandomManager;

public class TreeIndividuals {

	public static int POLYPHONY_MODE = 1024;
	public static int MONOPHONY_MODE = 1025;
	

	protected static ArrayList<TreeNodes> recursiveGenomeDevelopment(ArrayList<TreeNodes> baseArray) {
		ArrayList<TreeNodes> returnArray = new ArrayList<TreeNodes>();
		ArrayList<TreeNodes> copyArray = copyGenomeArray(baseArray);
		boolean stopDevelop = false;
		int counter = copyArray.size() - 1;
		int indexToDevelop = 0;
		while (counter >= 0) {
			TreeNodes tmpNode = (TreeNodes) copyArray.get(counter);
			if (tmpNode.getTermOrNot() == TreeNodes.RECURSIVENODE) {
				indexToDevelop = counter;
				break;
			} else if (counter == 0) {
				stopDevelop = true;
			}
			counter--;
		}
		if (stopDevelop) {
			// Nothing to develop. Finally no R node in genom.
			returnArray = copyArray;
			// System.out.println("TreeIndividuals : dosent have R node");
		} else {
			// Sarch and Extract PartTree
			// System.out.println(getGenomString(copyArray));

			ArrayList<Integer> tmpInts = getPartTreeIndex(copyArray, indexToDevelop);
			Integer tmpInt = (Integer) tmpInts.get(0);
			int fromIndex = tmpInt.intValue();
			tmpInt = (Integer) tmpInts.get(1);
			int toIndex = tmpInt.intValue();

			// System.out.println("indexToDevelop:" + indexToDevelop
			// +",fromIndex:" + fromIndex + ",toIndex:" + toIndex);
			ArrayList<TreeNodes> partTree = extractGenomeArray(copyArray, fromIndex,
					toIndex);
			int deni = convertRtoNormalTerm(partTree);
			ArrayList<TreeNodes> nextArray = new ArrayList<TreeNodes>();
			nextArray.ensureCapacity(copyArray.size() + partTree.size() - 1);
			for (int i = 0; i < copyArray.size(); i++) {
				TreeNodes tmpNode = (TreeNodes) copyArray.get(i);
				nextArray.add(tmpNode.clone());
			}
			// System.out.println("Part Tree:" + getGenomString(partTree));
			replaceNodes(nextArray, partTree, indexToDevelop);
			if (deni != -1) {
				counter = 1;
				while (counter < deni) {
					replaceNodes(nextArray, partTree, indexToDevelop - counter);
					counter++;
				}
			}

			// Next
			returnArray = recursiveGenomeDevelopment(nextArray);
		}
		return returnArray;
	}

	static ArrayList<TreeNodes> copyGenomeArray(ArrayList<TreeNodes> base) {
		int until = base.size();
		ArrayList<TreeNodes> returnArray = new ArrayList<TreeNodes>(until);
		returnArray.ensureCapacity(until);

		for (int i = 0; i < until; i++) {
			TreeNodes tmpNode = (TreeNodes) base.get(i);
			returnArray.add(tmpNode.clone());
		}
		return returnArray;
	}

	static ArrayList<TreeNodes> extractGenomeArray(ArrayList<TreeNodes> base, int fromIndex,
			int toIndex) {
		// return genomArray range(fromIndex <= x <=toIndex)
		ArrayList<TreeNodes> returnArray = new ArrayList<TreeNodes>(toIndex - fromIndex + 1);

		returnArray.ensureCapacity(toIndex - fromIndex);
		for (int i = fromIndex; i <= toIndex; i++) {
			TreeNodes tmpNode = (TreeNodes) base.get(i);
			returnArray.add(tmpNode.clone());
		}
		return returnArray;
	}

	static ArrayList<Integer> getSubTreeIndex(ArrayList<TreeNodes> base, int point) {
		return getPartTreeIndex(base, point);
	}

	static ArrayList<Integer> getPartTreeIndex(ArrayList<TreeNodes> base, int point) {
		// returned int [0] is fromIndex
		// returned int [1] is toIndex
		int fromIndex = -1;
		int toIndex = -1;
		int counter = point - 1;
		int until = base.size();
		int internalCounter = 0;
		int SCCounter = 0;
		int[] SCArray = new int[until];

		for (int i = 0; i < until; i++) {
			TreeNodes tmpNode = (TreeNodes) base.get(i);
			SCArray[i] = tmpNode.getStackCount();
		}

		while (counter >= 0) {
			TreeNodes tmpNode = (TreeNodes) base.get(counter);
			if (tmpNode.getTermOrNot() == TreeNodes.NONTERMINAL) {
				SCCounter = 0;
				internalCounter = 0;
				while (counter + internalCounter < until) {
					SCCounter += SCArray[counter + internalCounter];
					if (SCCounter == 1) {
						break;
					}
					internalCounter++;
				}
				// System.out.println();
				if (point <= counter + internalCounter && point > counter) {
					// System.out.println("point:" + point +",counter:" +
					// counter + ",internalCounter:" + internalCounter);
					fromIndex = counter;
					toIndex = counter + internalCounter;
					break;
				}
			}
			counter--;
		}
		ArrayList<Integer> returnValue = new ArrayList<Integer>(2);
		returnValue.ensureCapacity(2);
		returnValue.add(new Integer(fromIndex));
		returnValue.add(new Integer(toIndex));
		return returnValue;
	}

	static int getToIndex(ArrayList<TreeNodes> base, int fromIndex) {
		int toIndex = -1;
		int SCCounter = 0;
		int until = base.size();
		int counter = fromIndex;
		while (counter < until) {
			TreeNodes tmpNode = (TreeNodes) base.get(counter);
			SCCounter += tmpNode.getStackCount();
			if (SCCounter == 1) {
				toIndex = counter;
				break;
			}
			counter++;
		}
		return toIndex;
	}

	private static int convertRtoNormalTerm(ArrayList<TreeNodes> partTree) {
		// transfar One or Two RecursiveNode to normal node
		// Ʊ??��?��??��?��??��?��ĥ꡼??��?��??��?��ˤդ??��?��??��?��İʾ??��?��??��?��¤??��?��$Τ??��?��??��?��??��?��??��?��ä??��?��??��?��顤??��?��??��?��??��?��??��?��??��?��ͥ??��?��??��?��Ū??��?��ˤ??��?��??��?��ʤ??��?��??��?��ȶ??��?��??��?��??��?��
		// ??��?��??��?��??��?��Ĥ??��?��??��?��??��?��??��?��??��?��??��?��ޤ??��?��??��?��ɤd??��?��Ǥ??��?��??��?��Ѥ??��?��??��?��?���ɡ??��?��
		int returnint = -1;
		//int returnPos = -1;
		int numOfRecursiveNodes = getNumOfRecursiveNodes(partTree);
		if (numOfRecursiveNodes <= 1) {
			int until = partTree.size();
			for (int i = 0; i < until; i++) {
				TreeNodes tmpNode = (TreeNodes) partTree.get(i);
				if (tmpNode.getTermOrNot() == TreeNodes.RECURSIVENODE) {
					tmpNode.setTermOrNot(TreeNodes.TERMINAL);
					tmpNode.setHasExtraArg(false);
					break;
				}
			}
		} else {
			int until = partTree.size();
			TreeNodes tmpNode = (TreeNodes) partTree.get(0);
			int[] RPoint = new int[numOfRecursiveNodes];
			int arrayCounter = 0;
			for (int i = 0; i < until; i++) {
				tmpNode = (TreeNodes) partTree.get(i);
				if (tmpNode.getTermOrNot() == TreeNodes.RECURSIVENODE) {
					RPoint[arrayCounter] = i;
					arrayCounter++;
					if (arrayCounter >= numOfRecursiveNodes)
						break;
				}
			}
			boolean inTheSameTree = false;
			int queuedPoint = -1;
			if (numOfRecursiveNodes == 2) {
				if (RPoint[0] + 1 == RPoint[1]) {
					queuedPoint = RPoint[1];
				}
			} else {
				// numOfRecursiveNodes > 2
				int counter = RPoint.length - 1;
				int previous = RPoint[counter];
				int contemporary = 0;
				counter--;
				while (counter >= 1) {
					contemporary = RPoint[counter];
					if (previous - 1 == contemporary) {
						queuedPoint = RPoint[counter + 1];
						break;
					}
					previous = contemporary;
					counter--;
				}
			}
			int fromIndex = 0;
			int toIndex = 0;
			if (queuedPoint != -1) {
				// System.out.println("TreeIndividuals:QueuedPoint : " +
				// queuedPoint);
				ArrayList<Integer> index = getPartTreeIndex(partTree, queuedPoint);
				Integer tmpInt = (Integer) index.get(0);
				fromIndex = tmpInt.intValue();
				tmpInt = (Integer) index.get(1);
				toIndex = tmpInt.intValue();
				// System.out.println("QueuedPoint - fromIndex:"+fromIndex +
				// ",toIndex:" + toIndex);
				if (toIndex == RPoint[RPoint.length - 1]) {
					if (toIndex == queuedPoint
							&& fromIndex + 1 == queuedPoint - 1) {
						inTheSameTree = true;
						//returnPos = queuedPoint;
						// System.out.println("TreeIndividual:SpecialCase");
					}
				}
			}
			int directedNum = 0;
			if (inTheSameTree) {
				for (int i = fromIndex + 1; i <= toIndex; i++) {
					tmpNode = (TreeNodes) partTree.get(i);
					if (tmpNode.getTermOrNot() == TreeNodes.RECURSIVENODE) {
						tmpNode.setTermOrNot(TreeNodes.TERMINAL);
						tmpNode.setHasExtraArg(false);
						directedNum++;
					}
				}
				returnint = directedNum;
			} else {
				tmpNode = (TreeNodes) partTree.get(RPoint[RPoint.length - 1]);
				tmpNode.setTermOrNot(TreeNodes.TERMINAL);
				tmpNode.setHasExtraArg(false);
			}
		}
		return returnint;
	}

	static String getGenomeString(ArrayList<TreeNodes> processingGenome) {
		String returnString = new String();
		for (int i = 0; i < processingGenome.size(); i++) {
			TreeNodes tmpNode = (TreeNodes) processingGenome.get(i);
			returnString = returnString.toString()
					+ tmpNode.getOperatorAsString() + " ";
		}
		return returnString;
	}

	static void replaceNodes(ArrayList<TreeNodes> base, ArrayList<TreeNodes> part, int index) {
		base.ensureCapacity(base.size() + part.size() - 1);
		base.remove(index);
		int until = part.size();
		for (int i = 0; i < until; i++) {
			TreeNodes tmpNode = (TreeNodes) part.get(i);
			base.add(index + i, tmpNode);
		}
	}

	static void replaceNodes(ArrayList<TreeNodes> base, ArrayList<TreeNodes> part, int fromIndex,
			int toIndex) {
		base.ensureCapacity(base.size() + part.size() - (toIndex - fromIndex));
		int until = toIndex - fromIndex;
		for (int i = 0; i <= until; i++) {
			base.remove(fromIndex);
		}
		until = part.size();
		for (int i = 0; i < until; i++) {
			TreeNodes tmpNode = (TreeNodes) part.get(i);
			base.add(fromIndex + i, tmpNode.clone());
		}
	}

	static int getNumOfRecursiveNodes(ArrayList<TreeNodes> base) {
		int numOfRecursiveNode = 0;
		int until = base.size();
		for (int i = 0; i < until; i++) {
			TreeNodes tmpNodes = (TreeNodes) base.get(i);
			if (tmpNodes.getTermOrNot() == TreeNodes.RECURSIVENODE)
				numOfRecursiveNode++;
		}
		return numOfRecursiveNode;
	}

	protected static void fitTerminalNodes(ArrayList<TreeNodes> genomArray,
			int numOfTerminalNodes) {
		int until = genomArray.size();
		for (int i = 0; i < until; i++) {
			TreeNodes tmpNode = (TreeNodes) genomArray.get(i);
			int termOrNot = tmpNode.getTermOrNot();
			if (termOrNot == TreeNodes.TERMINAL
					|| termOrNot == TreeNodes.RECURSIVENODE) {
				if (tmpNode.getData() >= numOfTerminalNodes) {
					System.out
							.println("TreeIndividuals:fitTerminalNodes: fit the nodeID : "
									+ tmpNode.getData());
					tmpNode.setData((int) Math.round(Math.floor(RandomManager
							.getRandom()
							* numOfTerminalNodes)));
				}
			}
		}
	}

	protected static boolean checkStackCount(ArrayList<TreeNodes> base) {
		boolean returnBool = false;
		int until = base.size();
		TreeNodes tmpNode = (TreeNodes) base.get(0);
		int SCAll = tmpNode.getStackCount();
		for (int i = 1; i < until; i++) {
			tmpNode = (TreeNodes) base.get(i);
			SCAll += tmpNode.getStackCount();
		}
		if (SCAll == 1)
			returnBool = true;
		return returnBool;
	}

}
