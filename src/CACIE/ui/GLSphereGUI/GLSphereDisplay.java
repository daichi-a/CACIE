package CACIE.ui.GLSphereGUI;

//import javax.media.opengl.*;
import javax.swing.JFrame;

public class GLSphereDisplay {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		GLSphere glSphere = new GLSphere();
		//glSphere.addGLEventListener();
		
		JFrame frame = new JFrame("GLSphere test Display");
		frame.add(glSphere);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	    frame.setSize(800, 600);		
	}

}
