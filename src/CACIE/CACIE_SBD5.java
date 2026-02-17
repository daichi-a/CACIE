package CACIE;

import CACIE.ui.ShoppingBasketGUI.OperationWindows_GUI_ShoppingBasketD5;

public class CACIE_SBD5 extends CACIE_O {

	static {
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	}

	public static void main(String Args[]) {
		configures(Args);
		new OperationWindows_GUI_ShoppingBasketD5(16, notes, oprList, confList);
	}

}
