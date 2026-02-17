package CACIE.ui.sphereGUI;

// TimeBehavior.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Update the cells grid every timeDelay ms 
*/


import java.util.Enumeration;
import javax.media.j3d.*;


public class TimeDisappearBehavior extends Behavior
{
  private WakeupCondition timeOut;
  private int timeDelay;
  private Spheres3D cellsGrid;
  private WrapSpheres3D ws3;


  public TimeDisappearBehavior(WrapSpheres3D sp, int td, Spheres3D cg)
  { 
	this.ws3 = sp;
    timeDelay = td; 
    cellsGrid = cg;
    timeOut = new WakeupOnElapsedTime(timeDelay);
  }


  public void initialize()
  { wakeupOn(timeOut); }


  public void processStimulus(Enumeration criteria)
  {
      cellsGrid.revolutionUpdate();      // ignore criteria
	  int size = ws3.getBlinkFlags().length;
	  boolean[] fl = new boolean[size];
	  fl = ws3.getBlinkFlags();
	  
	  for(int i = 0; i<size; i++){
//		  if(fl[i])
		  for(int j = 0; j<8; j++){
		  	  cellsGrid.revolutionUpdate();
		  }
			  cellsGrid.getSphere3D(i).disappearUpdate();
//		  cellsGrid.getSphere3D(i).appearUpdate();
		  
	  }
    wakeupOn(timeOut);
  }


}  // end of TimeBehavior class
