package CACIE.ui.sphereGUI;


import java.util.*;

import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class Spheres3D
{
  // grid rotation amount 
  private static final double ROTATE_AMT = Math.toRadians(4);  // 4 degrees

  // number of updates used to complete a visual transition (used by Cell class)
  public static final int MAX_TRANS = 8;

  // number of cells along the x-, y-, and z- axes
  private final static int GRID_LEN = 10;  

  // storage for the cells making up the grid
  private Sphere3D[][][] cells;
  private Sphere3D[] spheres;

  // reusable Transform3D object
  private Transform3D t3d = new Transform3D(); 
  private Transform3D rotT3d = new Transform3D();
  private Transform3D scaleT3d = new Transform3D();

  private TransformGroup baseTG;   // used to rotate the grid
  private double turnAngle;
  private int turnAxis = 0;
  WrapSpheres3D ws3;
  

  // transition (transparency/colour change) step counter
  private int transCounter = 0;

  private SphereProperties sphereProps;

  // birth and die ranges used in the life rules
  boolean[] birthRange, dieRange;
  int num_sphere;
  Appearance[] spheres_app;

  private Random rand = new Random();



  public Spheres3D(WrapSpheres3D ws, SphereProperties sps)
  /* The grid (3D array) of Cells is created, and are connected
     to a baseTG TransformGroup. When baseTG is rotated at run 
     time, the entire grid moves. 
  */
  { 
	this.ws3 = ws;
    sphereProps = sps;

    // load birth and die ranges
    birthRange = sphereProps.getBirth();
    dieRange = sphereProps.getDie();
    num_sphere = sphereProps.getNumSpheres();


    setTurnAngle();

    /* Allow baseTG to be read and changed at run time (so
       it can be rotated). */
    baseTG = new TransformGroup();   
    baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    baseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    baseTG.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    
//    //Draw circle
//    TransformGroup ctrans = new TransformGroup();
//    float radius = 2.5f;
//    // 32 segments = 33 points.
//    int resolution = 32;
//    int length = resolution + 1;
//    LineStripArray lsa = new LineStripArray(length, GeometryArray.COORDINATES, new int[] {length});
//    // first and last points
//    Point3f pt0 = new Point3f(radius, 0.0f, 0.0f);
//    lsa.setCoordinate(0, pt0);
//    // computed points
//    Point3f pt = new Point3f();
//    for (int i = 1; i < 32; i++)
//    {
//        pt.x = (float)(radius * Math.cos(i * Math.PI / 16));
//        pt.y = (float)(radius * Math.sin(i * Math.PI / 16));
//        lsa.setCoordinate(i, pt);
//    }
//    lsa.setCoordinate(32, pt0);
//    Shape3D circle = new Shape3D(lsa);
//    ctrans.addChild(circle);
//    baseTG.addChild(ctrans);
    
    
    // initialize the grid with Cell objects
    //cells = new Sphere3D[GRID_LEN][GRID_LEN][GRID_LEN];
    spheres = new Sphere3D[num_sphere];
    for(int i = 0; i < num_sphere; i++){
    	spheres[i] = new Sphere3D(this, i, sphereProps);
    	baseTG.addChild(spheres[i].getTG());
    }
  }
  
  public Sphere3D getSphere3D(int index){
	  return spheres[index];
  }
  
  public Sphere3D[] getSphere3D(){
	  return spheres;
  }

  private void setTurnAngle()
  /* A faster speed property is converted into a larger 
     rotation angle, which makes the grid turn faster at
     tun time. */
  {
    int speed = sphereProps.getSpeed();

//    if (speed == SphereProperties.SLOW)
//      turnAngle = ROTATE_AMT/4;
//    else if (speed == SphereProperties.MEDIUM)
//      turnAngle = ROTATE_AMT/2;
//    else  // fast --> large rotation
//      turnAngle = ROTATE_AMT;
    
    if (speed == SphereProperties.SLOW)
        turnAngle = ROTATE_AMT/(4 * 10);
      else if (speed == SphereProperties.MEDIUM)
        turnAngle = ROTATE_AMT/( 2 * 10);
      else  // fast --> large rotation
        turnAngle = ROTATE_AMT;
  }  // end of setTurnAngle()


  public TransformGroup getBaseTG()
  {  return baseTG;  } 


  // ------------------------ update the grid ------------------------

  public void update()
  /* An update() call either triggers a state change or a visual
     change. update() is called periodically by TimeBehavior.

     The transCounter is incremented from 0 to MAX_TRANS, then
     repeats. When transCounter is 0, the state of the grid's 
     cells is updated, and the grid's rotation axis changed.

     At other times, the cells' visuals are changed, which may 
     mean their visibility and/or colour changing.

     At every update, the grid is rotated. 
  */
  {
    if (transCounter == 0) {   // time for grid state change
      stateChange();
      //turnAxis = rand.nextInt(3);  // change rotation axis
      transCounter = 1;
    }
    else {   // make a visual change
      for (int i=0; i < num_sphere; i++)
      		spheres[i].visualChange(transCounter);

      transCounter++;
      if (transCounter > MAX_TRANS)
        transCounter = 0;   // finished, so reset
    }

    //doRotate();   // rotate in every update() call
  }  // end of update()
  
  public void revolutionUpdate()
  /* An update() call either triggers a state change or a visual
     change. update() is called periodically by TimeBehavior.

     The transCounter is incremented from 0 to MAX_TRANS, then
     repeats. When transCounter is 0, the state of the grid's 
     cells is updated, and the grid's rotation axis changed.

     At other times, the cells' visuals are changed, which may 
     mean their visibility and/or colour changing.

     At every update, the grid is rotated. 
  */
  {
//    if (transCounter == 0) {   // time for grid state change
//      stateChange();
//      turnAxis = rand.nextInt(3);  // change rotation axis
//      transCounter = 1;
//    }
//    else {   // make a visual change
//      for (int i=0; i < num_sphere; i++)
//      		spheres[i].visualChange(transCounter);
//
//      transCounter++;
//      if (transCounter > MAX_TRANS)
//        transCounter = 0;   // finished, so reset
//    }
	  turnAxis = 2;

    doRotate();   // rotate in every update() call
  }
  
  public void revolutionSlowUpdate()
  /* An update() call either triggers a state change or a visual
     change. update() is called periodically by TimeBehavior.

     The transCounter is incremented from 0 to MAX_TRANS, then
     repeats. When transCounter is 0, the state of the grid's 
     cells is updated, and the grid's rotation axis changed.

     At other times, the cells' visuals are changed, which may 
     mean their visibility and/or colour changing.

     At every update, the grid is rotated. 
  */
  {
//    if (transCounter == 0) {   // time for grid state change
//      stateChange();
//      turnAxis = rand.nextInt(3);  // change rotation axis
//      transCounter = 1;
//    }
//    else {   // make a visual change
//      for (int i=0; i < num_sphere; i++)
//      		spheres[i].visualChange(transCounter);
//
//      transCounter++;
//      if (transCounter > MAX_TRANS)
//        transCounter = 0;   // finished, so reset
//    }
      turnAngle = ROTATE_AMT/(4 * 10000);
	  turnAxis = 2;

    doRotate();   // rotate in every update() call
  }
  
  public void ShrinkUpdate()

  {


    doShrink();   // rotate in every update() call
  }
  
  public void vanishChange()
  /* An update() call either triggers a state change or a visual
     change. update() is called periodically by TimeBehavior.

     The transCounter is incremented from 0 to MAX_TRANS, then
     repeats. When transCounter is 0, the state of the grid's 
     cells is updated, and the grid's rotation axis changed.

     At other times, the cells' visuals are changed, which may 
     mean their visibility and/or colour changing.

     At every update, the grid is rotated. 
  */
  {
    if (transCounter == 0) {   // time for grid state change
      stateChange();
      //turnAxis = rand.nextInt(3);  // change rotation axis
      transCounter = 1;
    }
    else {   // make a visual change
      for (int i=0; i < num_sphere; i++)
      		spheres[i].vanishChange(transCounter);

      transCounter++;
      if (transCounter > MAX_TRANS)
        transCounter = 0;   // finished, so reset
    }

    //doRotate();   // rotate in every update() call
  }  // end of update()
  
  public void vanishChangeEnd()
  /* An update() call either triggers a state change or a visual
     change. update() is called periodically by TimeBehavior.

     The transCounter is incremented from 0 to MAX_TRANS, then
     repeats. When transCounter is 0, the state of the grid's 
     cells is updated, and the grid's rotation axis changed.

     At other times, the cells' visuals are changed, which may 
     mean their visibility and/or colour changing.

     At every update, the grid is rotated. 
  */
  {

      for (int i=0; i < num_sphere; i++)
      		spheres[i].vanishChangeEnd(transCounter);

    //doRotate();   // rotate in every update() call
  }  // end of update()


  private void stateChange()
  /* A two phase operation: first calculate the next life state
     for each cell, then update the cells
  */
  {
    boolean willLive;

    // calculate next state for each cell
    for (int i=0; i < num_sphere; i++){
          willLive = aliveNextState(i);
          spheres[i].newAliveState(willLive);
    }
    // update each cell
    for (int i=0; i < num_sphere; i++){
          spheres[i].updateState();
          //spheres[i].visualChange(0);

    }

  }  // end of stateChange()


  // ---------------------- life calculations ------------------------


  private boolean aliveNextState(int i)
  /* The life calculation depends on the number of neigbouring cells
     which are currently alive, which is stored in numberLiving.

     The next state for cell[i][j[k] depends on it's current alive
     state and whether numberLiving appears in the birth or die
     ranges. These ranges are specified when Life3D is being 
     configured.
  */
  {
    // count all the living neighbours, but not the cell itself
    int numberLiving = 0;
    for(int r=i-1; r <= i+1; r++){  // range i-1 to i+1
          if ((r==i)){
            continue; 
          }// skip self
          else if (isAlive(r)){
            numberLiving++;
          }
    }    

    // get the cell's current life state
    boolean currAliveState = isAlive(i);

    // ** Life Rules **: calculate the cell's next life state
    if (birthRange[numberLiving] && !currAliveState)   // to be born && dead now
      return true;   // make alive
    else if (dieRange[numberLiving]  && currAliveState)  // to die && alive now
      return false;  // kill off
    else
      return currAliveState;  // no change
  }  // end of aliveNextState()


  private boolean isAlive(int i)
  {
    // deal with edge cases for cells array
    i = rangeCorrect(i);

    return  spheres[i].isAlive();
  }  // end of isAlive()


  private int rangeCorrect(int index)
  /* if the cell index is out of range then use the index of
     the opposite edge */
  {
    if (index < 0)
      return (num_sphere + index);
    else if (index > num_sphere-1)
      return (index - num_sphere);
    else // make no change
      return index;
  }  // end of rangeCorrect()


  // ------------------- rotation ------------------------------


//  private void doRotate()
//  // rotate the object turnAngle radians around an axis
//  {
//    baseTG.getTransform(t3d);  // get current rotation
//    rotT3d.setIdentity();      // reset the rotation transform object
//
//    switch (turnAxis) {    // set the rotation based on the current axis
//      case 0: rotT3d.rotX(turnAngle); break;
//      case 1: rotT3d.rotY(turnAngle); break;
//      case 2: rotT3d.rotZ(turnAngle); break;
//      default: System.out.println("Unknown axis of rotation"); break;
//    }
//
//    t3d.mul(rotT3d);            // 'add' new rotation to current one
//    baseTG.setTransform(t3d);   // update the TG
//  }  // end of doRotate()
  public void setSpheresApp(Appearance app){
	  for(int i = 0; i < num_sphere; i++){
		  spheres_app[i] = app;
	  }
  }
  
  public Appearance[] getSpheresApp(){
	  return spheres_app;
  }
  
  public void doRotate()
  // rotate the object turnAngle radians around an axis
  {
    baseTG.getTransform(t3d);  // get current rotation
    rotT3d.setIdentity();      // reset the rotation transform object

    switch (turnAxis) {    // set the rotation based on the current axis
      case 0: rotT3d.rotX(turnAngle); 
      break;
      case 1: rotT3d.rotY(turnAngle);
      break;
      case 2: rotT3d.rotZ(turnAngle); 
//      for(int i = 0; i < num_sphere; i++){
//      	double theta1 = spheres[i].getIndividual().getTheta();
//      	spheres[i].getIndividual().setTheta(theta1 + turnAngle);
//      }
      break;
      default: System.out.println("Unknown axis of rotation"); break;
    }
    //System.out.println("doRoate");

    t3d.mul(rotT3d);            // 'add' new rotation to current one
    baseTG.setTransform(t3d);   // update the TG
  }  // end of doRotate()
  
  public void doShrink()
  // rotate the object turnAngle radians around an axis
  {
    baseTG.getTransform(t3d);  // get current rotation
    scaleT3d.setIdentity();      // reset the rotation transform object

    scaleT3d.scaleAdd(0.1, t3d);

    //System.out.println("doRoate");
//
//    t3d.mul(rotT3d);            // 'add' new rotation to current one
    baseTG.setTransform(t3d);   // update the TG
  }  // end of doRotate()

	public WrapSpheres3D getWrapSpheres3D(){
		return ws3;
	}

}  // end of CellsGrid class

