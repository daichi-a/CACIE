package CACIE.ui.sphereGUI;

// TimeBehavior.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Update the cells grid every timeDelay ms 
*/


import java.util.Enumeration;
import javax.media.j3d.*;
import javax.vecmath.Vector3d;


public class TimePullSpheresBehavior extends Behavior
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


  public TimePullSpheresBehavior(WrapSpheres3D sp, int td, Spheres3D cg)
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
	  int[] fit = new int[size];
	  fit = ws3.getFitnesses();
	  
	  for(int i = 0; i<size; i++){
		  if(fl[i]){
			  cellsGrid.getSphere3D(i).getTG().getTransform(t3d);
			  t3d.get(currV3d);
				xpos = currV3d.x;
				ypos = currV3d.y;
				zpos = currV3d.z;
				r = Math.sqrt(xpos * xpos + ypos * ypos);
				theta = Math.atan2(ypos, xpos);
//				if(Math.toRadians(-360) <= theta && theta < Math.toRadians(0)){
//					theta = Math.toRadians(360) + theta;
//				}else if(Math.toRadians(-360) > theta){
//					theta = Math.toRadians(720) + theta;
//				}else if(Math.toRadians(0) <= theta && theta <= Math.toRadians(360)){
//					theta = theta;
//				}else if(Math.toRadians(360) <= theta){
//					theta = theta - Math.toRadians(360);
//				}else{
//					theta = theta;
//				}
				this.ws3.setPickedXpos(xpos);
				this.ws3.setPickedYpos(ypos);
				this.ws3.setPickedRpos(r);
				this.ws3.setPickedThetapos(theta);
		  }
	  }
	  
	  for(int i = 0; i<size; i++){
//		  if(!fl[i]){
			  cellsGrid.getSphere3D(i).pullSpheresUpdate();
//			  cellsGrid.getSphere3D(i).setScale();
//			  cellsGrid.getSphere3D(i).doScale((double)(fit[i] / 100.0 * 0.7 + 0.5 ));
//			  cellsGrid.getSphere3D(i).vibrationUpdate();

//		  }
	  }
	  for(int i = 0; i<size; i++){
		  if(!fl[i]){

			  cellsGrid.getSphere3D(i).setScale();


		  }
	  }
    wakeupOn(timeOut);
  }


}  // end of TimeBehavior class
