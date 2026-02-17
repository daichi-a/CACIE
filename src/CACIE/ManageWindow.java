package CACIE;

import CACIE.ui.ManageTerminalNodes;

public class ManageWindow {

	public static void main(String args[]) {
		new ManageTerminalNodes(ManageTerminalNodes.WITH_GUI);
		
		/*
		//ManageTerminalNodes manager;
		if (args.length < 1)
			ManageTerminalNodes manager = new ManageTerminalNodes();
		else {
			if (args[0].equals("--with-gui"))
				ManageTerminalNodes manager = new ManageTerminalNodes(ManageTerminalNodes.WITH_GUI);
			// else
			// manager = new ManageTerminalNodes(args[0]);
		}
		*/
	}

}