package CACIE;

import CACIE.ui.*;

class CACIE_DD5 extends CACIE_O {
	static {
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	}

	public static void main(String Args[]) {
		configures(Args);
		new OperationWindows_GUI(16, notes, oprList, confList);
	}
}