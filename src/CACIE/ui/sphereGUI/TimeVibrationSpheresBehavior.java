package CACIE.ui.sphereGUI;

// TimeBehavior.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Update the cells grid every timeDelay ms 
*/


import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.Vector3d;


public class TimeVibrationSpheresBehavior extends Behavior
{
  private WakeupCondition timeOut;
  private int timeDelay;
  private Spheres3D cellsGrid;
  private WrapSpheres3D ws3;
  private Transform3D t3d = new Transform3D();
  private Transform3D posT3d = new Transform3D();
  private Vector3d currV3d = new Vector3d();
  private double xpos;
  private double ypos;
  private double zpos;
  private double r;
  private double theta;


  public TimeVibrationSpheresBehavior(WrapSpheres3D sp, int td, Spheres3D cg)
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
//      cellsGrid.revolutionUpdate();      // ignore criteria
	  int size = ws3.getBlinkFlags().length;
	  boolean[] fl = new boolean[size];
	  fl = ws3.getBlinkFlags();
	  
	  for(int i = 0; i<size; i++){


			  cellsGrid.getSphere3D(i).vibrationUpdate();

	  }
    wakeupOn(timeOut);
  }


}  // end of TimeBehavior class
