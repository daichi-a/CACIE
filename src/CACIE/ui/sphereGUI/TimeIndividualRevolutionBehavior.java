package CACIE.ui.sphereGUI;

// TimeBehavior.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Update the cells grid every timeDelay ms 
*/


import java.util.Enumeration;
import javax.media.j3d.*;


public class TimeIndividualRevolutionBehavior extends Behavior
{
  private WakeupCondition timeOut;
  private int timeDelay;
  private Sphere3D cell;


  public TimeIndividualRevolutionBehavior(int td, Sphere3D ce)
  { 
    timeDelay = td; 
    cell = ce;
    timeOut = new WakeupOnElapsedTime(timeDelay);
  }


  public void initialize()
  { wakeupOn(timeOut); }


  public void processStimulus(Enumeration criteria)
  {
	  cell.revolutionUpdate();      // ignore criteria
    wakeupOn(timeOut);
  }


}  // end of TimeBehavior class
