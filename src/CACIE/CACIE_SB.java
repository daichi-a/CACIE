package CACIE;

import CACIE.ui.ShoppingBasketGUI.OperationWindows_GUI_ShoppingBasket;

public class CACIE_SB extends CACIE_O {

	static {
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	}

	public static void main(String Args[]) {
		configures(Args);
		new OperationWindows_GUI_ShoppingBasket(16, notes, oprList, confList);
	}

}
