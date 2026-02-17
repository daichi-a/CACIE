package CACIE.ui.sphereGUI;

import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class Circle3D
{
  // possible visual states for a cell
  private final static int INVISIBLE = 0;
  private final static int FADE_IN = 1;
  private final static int FADE_OUT = 2;
  private final static int VISIBLE = 3;

  // material colours
  private final static Color3f RED = new Color3f(1.0f, 0.0f, 0.0f);
  private final static Color3f ORANGE = new Color3f(1.0f, 0.5f, 0.0f);
  private final static Color3f YELLOW = new Color3f(1.0f, 1.0f, 0.0f);
  private final static Color3f GREEN = new Color3f(0.0f, 1.0f, 0.0f);
  private final static Color3f BLUE = new Color3f(0.0f, 0.0f, 1.0f);

  private final static Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
  private final static Color3f WHITE = new Color3f(0.9f, 0.9f, 0.9f);

  // length of cell side (== diameter when the cell is a ball)
//  private final static float CELL_LEN = 0.5f;  
  private final static float SPHERE_LEN = 0.3f;
  private final static float SPHERES_RADIUS = 2.5f;

  // space between cells : a factor multiplied to CELL_LEN
  private final static float SPACING = 1.5f;

  // scene graph elements
  private TransformGroup circleTG;  

  // appearance elements
  private Appearance cellApp;
  private TransparencyAttributes transAtt;
  private Material material;
  private Color3f cellCol, oldCol, newCol;

  // cell state information
  private boolean isAlive, newAliveState;
  private int visualState;
  private int age = 0;
  
  private SphereProperties sphereProps;
  int num_sphere;
  SphereIndividual sindividual;
 


  public Circle3D(int index,   SphereProperties sps)
  /*  A cell is a shape below a TransformGroup which is used to
      position the cell in the cells grid. The scene branch is:
             circleTG --> cellShape --> Appearance
                                     {Material,Transparency}
  */
  { 
	sphereProps = sps;
    num_sphere = sphereProps.getNumSpheres();

    System.out.println("num_sphere: " + index);
//    isAlive = (RandomManager.getRandom()() < 0.1) ? true : false; 
    isAlive = true;
      // it's more likely that a cell is initially dead (invisible)

    // create appearance
    cellApp = new Appearance();
    makeMaterial();
    setVisibility();

    // the cell shape as a cube
    // Box cellShape = new Box( CELL_LEN/2, CELL_LEN/2, CELL_LEN/2, 
	//					Box.GENERATE_NORMALS, cellApp);

    // the cell chape as a sphere
    Sphere cellShape = new Sphere(SPHERE_LEN/2, Sphere.GENERATE_NORMALS, cellApp); 

//    // fix cell's position
//    Transform3D t3d = new Transform3D();
//    double xPosn = index*CELL_LEN*SPACING; 
//    double yPosn = index*CELL_LEN*SPACING; 
//    double zPosn = index*CELL_LEN*SPACING; 
//    t3d.setTranslation( new Vector3d(xPosn, yPosn, zPosn) );
    

    
    // fix cell's position
    double theta = (double) ((Math.PI / 180.0) * ( 360.0 / num_sphere ));
    Transform3D t3d = new Transform3D();
//    double xPosn = (SPHERES_RADIUS * Math.cos(theta * index)); 
//    double yPosn = (SPHERES_RADIUS * Math.sin(theta * index)); 
//    double xPosn = 0.0; 
//    double yPosn = 0.0;
//    double zPosn = 0.0; 
    double xPosn = 0.5 * index; 
    double yPosn = 0.5 * index;
    double zPosn = 0.5 * index; 
    t3d.setScale(new Vector3d(xPosn, yPosn, zPosn));
//    t3d.setTranslation( new Vector3d(xPosn, yPosn, zPosn) );
    
 
  //Draw circle
  TransformGroup ctrans = new TransformGroup();
  float radius = 2.5f; /*(float)(0.5f * index);*/
  
  // 32 segments = 33 points.
  int resolution = 32;
  int length = resolution + 1;
  LineStripArray lsa = new LineStripArray(length, GeometryArray.COORDINATES, new int[] {length});
  // first and last points
  Point3f pt0 = new Point3f(radius, 0.0f, 0.0f);
  lsa.setCoordinate(0, pt0);
  // computed points
  Point3f pt = new Point3f();
  for (int i = 1; i < 32; i++)
  {
//      pt.x = (float) (Math.cos(theta * index));
//      pt.y = (float) (Math.sin(theta * index));
//      pt.x = (float) (radius * Math.cos(theta * index));
//      pt.y = (float) (radius * Math.sin(theta * index));
      pt.x = (float)(radius * Math.cos(i * Math.PI / 16));
      pt.y = (float)(radius * Math.sin(i * Math.PI / 16));
      lsa.setCoordinate(i, pt);
  }
  lsa.setCoordinate(32, pt0);
  Shape3D circle = new Shape3D(lsa);
  ctrans.addChild(circle);



    // build scene branch
    circleTG = new TransformGroup(); 
//    circleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//    circleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//    circleTG.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    circleTG.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);

    circleTG.setTransform(t3d);
//    circleTG.setUserData(sindividual);
//    circleTG.addChild(cellShape);
    circleTG.addChild(ctrans);

  }  // end of Cell()


  private void makeMaterial()
  /* Make a coloured material, which is originally blue. The
     ambient and diffuse components of the material can be changed
     at run time. */
  {
    cellCol = new Color3f();
    oldCol = new Color3f();
    newCol = new Color3f();

    // set material
    material = new Material(WHITE, BLACK, WHITE, WHITE, 100.f);
               // sets ambient, emissive, diffuse, specular, shininess

    material.setCapability(Material.ALLOW_COMPONENT_WRITE);
    material.setLightingEnable(true);
    resetColours();
    cellApp.setMaterial(material);
  }  // end of makeMaterial()


  private void resetColours()
  // intialization of the material's colour to blue
  {
    cellCol.set(BLUE);
    oldCol.set(cellCol);   // blue as well
    newCol.set(cellCol);

    setMatColours(cellCol);
  }  // end of resetColours()


  private void setMatColours(Color3f col)
  // the ambient colour is a darker shade of the diffuse colour
  {
    material.setAmbientColor(col.x/3.0f, col.y/3.0f, col.z/3.0f);
    material.setDiffuseColor(col);
  }  // end of setMatColours()


  private void setVisibility()
  /* A cell's transparency can change at run time, ranging from
     fully opaque when the cell is 'alive' to fully transparent
     when 'dead'. When the cell is coming to life or dieing, its
     transparency setting will be somewhere between these values. */
  {
    // let transparency value change at run time
    transAtt = new TransparencyAttributes();
    transAtt.setTransparencyMode(TransparencyAttributes.BLENDED);
    transAtt.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

    if (isAlive) {
      visualState = VISIBLE;
      transAtt.setTransparency(0.0f);     // opaque
    }
    else  { // dead so invisible
      visualState = INVISIBLE;
      transAtt.setTransparency(1.0f);   // totally transparent
    }

    cellApp.setTransparencyAttributes(transAtt);
  }  // end of setVisibility()


  public TransformGroup getTG()
  // called by CellsGrid
  {  return circleTG;  }


  // ----------- get/set methods for cell's life ---------------------


  public boolean isAlive()
  {  return isAlive;  }

  public void newAliveState(boolean b)
  {  newAliveState = b;  } 


  // -------------------------- state update --------------------------

  public void updateState()
  /* If the cell is coming alive or dieing then its visual state
     must be altered, so it will fade into/out of view.

     If the cell's life state isn't changing, and it's alive,
     then it's colour may change if it's old enough.
  */
  {
    if (isAlive != newAliveState) {  // there's a state change
      if (isAlive && !newAliveState)  // alive --> dead (die)
        visualState = FADE_OUT;   // from VISIBLE      
      else {  // dead --> alive (birth)
        visualState = FADE_IN;    // from INVISIBLE
        age = 0;    // reset age since born again
        resetColours();
      }
    }
    else { // current and new states are the same
      if (isAlive) {   // cell stays alive (survives)
        age++;   // get older
        ageSetColour();
      }
    }
  }  // end of updateState()


  private void ageSetColour()
  // hardwired age values for setting the cell's new colour
  {
    if (age > 16)
      newCol.set(RED);
    else if (age > 8)
      newCol.set(ORANGE);
    else if (age > 4)
      newCol.set(YELLOW);
    else if (age > 2)
      newCol.set(GREEN);
    else
      newCol.set(BLUE);
    
    if(age > 17){
    	resetColours();
    	age = 0;
    }
  }  // end of ageSetColour()


  // -------------------------- visual update --------------------------

  public void visualChange(int transCounter)
  /* A cell is in one of the folowing visual states:
      * FADE_OUT, where the cell gradually disappears;
      * FADE_IN, where the cell gradually apppears;
      * VISIBLE, where the cell's colour may gradually change;
      * INVISIBLE, where nothing happens to the cell's appearance.
  */
  {
    float transFrac = ((float)transCounter)/Spheres3D.MAX_TRANS;

    if(visualState == FADE_OUT) 
      transAtt.setTransparency(transFrac);  // 1.0f is totally transparent   
    else if (visualState == FADE_IN)
      transAtt.setTransparency(1.0f-transFrac);
    else if (visualState == VISIBLE) {
      changeColour(transFrac);
    }
    else if (visualState == INVISIBLE) {
    }
      // do nothing
    else
      System.out.println("Error in visualState");
    
//    if(visualState == FADE_OUT) 
//        transAtt.setTransparency(transFrac);  // 1.0f is totally transparent   
//      else if (visualState == VISIBLE)
//        transAtt.setTransparency(1.0f-transFrac);
//      else if (visualState == VISIBLE) {
//        changeColour(transFrac);
//      }
//      else if (visualState == INVISIBLE) {
//      }
//        // do nothing
//      else
//        System.out.println("Error in visualState");

    if (transCounter == Spheres3D.MAX_TRANS)
      endVisualTransition();
  }  // end of visualChange()


  private void changeColour(float transFrac)
  /* the current cell's colour is a mix of its old and
     new colours (if the two are different) */
  {
    if (!oldCol.equals(newCol)) {  // if colours are different
      float redFrac = oldCol.x*(1.0f-transFrac) + newCol.x*transFrac;
      float greenFrac = oldCol.y*(1.0f-transFrac) + newCol.y*transFrac;
      float blueFrac = oldCol.z*(1.0f-transFrac) + newCol.z*transFrac;

      cellCol.set(redFrac, greenFrac, blueFrac);
      setMatColours(cellCol);
    }
  }  // end of changeColour()


  private void endVisualTransition()
  /* At the end of a transition, the final colour is
     stored, the new cell's life state is stored, and
     the visual state is changed to VISIBLE or INVISIBLE.
  */
  {
    // store current colour as both the old and new colours;
    // used when fading in and when visible
    oldCol.set(cellCol);
    newCol.set(cellCol);

    isAlive = newAliveState;   // update alive state

    if (visualState == FADE_IN)
      visualState = VISIBLE;
    else if (visualState == FADE_OUT)
      visualState = INVISIBLE;
  }  // end of endVisualTransition()


}  // end of Cell class