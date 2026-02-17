package CACIE.ui.sphereGUI;

// TimeBehavior.java
// Andrew Davison, July 2006, ad@fivedots.coe.psu.ac.th

/* Update the cells grid every timeDelay ms 
*/


import java.util.Enumeration;
import javax.media.j3d.*;


public class TimeRevolutionBehavior extends Behavior
{
  private WakeupCondition timeOut;
  private int timeDelay;
  private Spheres3D cellsGrid;
  private WrapSpheres3D ws3;
  private SphereIndividual s[];


  public TimeRevolutionBehavior(WrapSpheres3D sp, int td, Spheres3D cg)
  { 
	this.ws3 = sp;
    timeDelay = td; 
    cellsGrid = cg;
    timeOut = new WakeupOnElapsedTime(timeDelay);
    s = new SphereIndividual[ws3.getBlinkFlags().length];
  }


  public void initialize()
  { wakeupOn(timeOut); }


  public void processStimulus(Enumeration criteria)
  {
      cellsGrid.revolutionUpdate();      // ignore criteria
	  int size = ws3.getBlinkFlags().length;
	  boolean[] fl = new boolean[size];
	  fl = ws3.getBlinkFlags();
	  int[] fit = new int[size];
	  fit = ws3.getFitnesses();
	  
	  for(int i = 0; i<size; i++){
//		  if(fl[i]){
//		  s[i] = cellsGrid.getSphere3D(i).getIndividual();
//		  int index = s[i].getIndex();
//		  fit[index] = s[i].getTransformedFitness();
//		  ws3.setFitness(index, fit[index]);
		  
	  cellsGrid.getSphere3D(i).colorChangeUpdate();
	  cellsGrid.getSphere3D(i).doRotate();
	  cellsGrid.getSphere3D(i).blinkingUpdate();
	  cellsGrid.getSphere3D(i).pulsationUpdate();
	  cellsGrid.getSphere3D(i).vibrationUpdate();
//	  cellsGrid.getSphere3D(i).pullSpheresUpdate();
//	  cellsGrid.getSphere3D(i).pullSpheresUpdate();
//	  cellsGrid.getSphere3D(i).setScale((double)(fit[i] / 100.0 * 0.7 + 0.5 ));
//	  cellsGrid.getSphere3D(i).blinkingUpdate();
//		  }
	  if(fl[i]){
//	  cellsGrid.getSphere3D(i).colorChangeUpdate();

//	  cellsGrid.getSphere3D(i).pullSpheresUpdate();
//	  cellsGrid.getSphere3D(i).doScale((double)(fit[i] / 100.0 * 2.0 ));
//	  System.out.println("fitness: " + fit[i]);
		  cellsGrid.getSphere3D(i).doScale((double)(fit[i] / 100.0 * 0.7 + 0.5 ));
		  }
	  }
    wakeupOn(timeOut);
  }


}  // end of TimeBehavior class
