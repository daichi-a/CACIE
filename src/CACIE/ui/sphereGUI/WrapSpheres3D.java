package CACIE.ui.sphereGUI;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Random;

import CACIE.genome.DistanceCalculator;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.picking.PickRotateBehavior;
//import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.picking.PickTranslateBehavior;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;


public class WrapSpheres3D extends JPanel 
// Holds the 3D canvas where the loaded image is displayed
{

  private static final int BOUNDSIZE = 100;  // larger than world

//  private static final Point3d USERPOSN = new Point3d(-3,5,10);
//  private static final Point3d USERPOSN = new Point3d(0,0,10);
  private static final Point3d USERPOSN = new Point3d(1, -7, 7);
    // initial user position

  // time delay (in ms) to regulate update speed
  private static final int TIME_DELAY = 60; //50
  private Color3f scolor = new Color3f(1.0f, 0.0f, 0.0f);
  private Color3f ecolor = new Color3f(0.0f, 0.0f, 1.0f);
  Color3f red = new Color3f(Color.RED);
  Color3f black = new Color3f(Color.BLACK);
  Color3f white = new Color3f(Color.WHITE);
  Color3f blue = new Color3f(Color.BLUE);
  Color3f gray = new Color3f(0.4f, 0.4f, 0.4f);



  private SimpleUniverse su;
  private BranchGroup sceneBG;
  private BoundingSphere bounds;   // for environment nodes
  private SphereGUI topLevel;   // required at quit time
  private SphereProperties m_sps;
  ZRotationInterpolator revolutionInterpolator;
  private Fog fog = null;
  private LinearFog lfog = null;
  private ExponentialFog efog = null;
  TimeRevolutionBehavior tb;
  TimeDisappearBehavior td;
  TimePullSpheresBehavior tps;
  TimeVibrationSpheresBehavior tvs;
  private TextureLoader loader = null;
  private int population_size;
  TransformGroup steerTG;
  
  TransformGroup revolutionTrans;
//  TimeColorChangeBehavior tb;
  public JPanel panel;
  Canvas3D canvas3D = null;
  SphereIndividual sind;
  SphereIndividual bufferSind;
  SphereIndividual pickedSind;
  Spheres3D cellsGrid;
  Sphere3D[] cells;
  int[] fitness;
  double[][] distanceMatrix;
  double[] relativeDistance;
  int[] color_characteristics;
  int[] pull_ranking;
  double[] temp_ranking1;
  int[] temp_ranking2;
  boolean[] blink_flag;
  boolean check_flag;
  boolean changed_flag;
  
  int click_counter = 0;
  int num_sphere;
  int index;
  int f;
  double pickedXpos;
  double pickedYpos;
  double pickedRpos;
  double pickedThetapos;
  


  public WrapSpheres3D(SphereGUI top, SphereProperties sps)
  // construct the 3D canvas
  {
    topLevel = top;
    this.m_sps = sps;
    this.num_sphere = sps.getNumSpheres();
    this.population_size = num_sphere;

    setLayout( new BorderLayout() );

    /* the size of the panel is dictated by the isFullScreen, width and
       height properties */
    if (m_sps.isFullScreen())
      setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize() );  // full-screen
    else {  // not full-screen
      int width = m_sps.getWidth();
      int height = m_sps.getHeight();
      setPreferredSize( new Dimension(width, height));
    }
    
    int num = m_sps.getNumSpheres();
    fitness = new int[num];
    blink_flag = new boolean[num];
    color_characteristics = new int[num];
    temp_ranking1 = new double[num];
    temp_ranking2 = new int[num];
    pull_ranking = new int[num];
    GraphicsConfiguration config =
					SimpleUniverse.getPreferredConfiguration();
    canvas3D = new Canvas3D(config);
    add("Center", canvas3D);

//    canvas3D.setFocusable(true);
//    canvas3D.requestFocus();    // the canvas now has focus, so receives key events

	canvas3D.addKeyListener( new KeyAdapter() {
	// listen for esc, q, end, ctrl-c on the canvas to
	// allow a convenient exit from the full screen configuration
       public void keyPressed(KeyEvent e)
       { int keyCode = e.getKeyCode();
         if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
             (keyCode == KeyEvent.VK_END) ||
             ((keyCode == KeyEvent.VK_C) && e.isControlDown()) ) {
//           topLevel.dispose();
           System.exit(0);    // exit() alone isn't sufficient most of the time
         }
       }
     });
	

    su = new SimpleUniverse(canvas3D);

    createSceneGraph();

    initUserPosition();        // set user's viewpoint
    orbitControls(canvas3D);   // controls for moving the viewpoint

    // depth-sort transparent objects on a per-geometry basis
    View view = su.getViewer().getView();
    view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);
    


    su.addBranchGraph(sceneBG);
  } // end of WrapLife3D()

  public Canvas3D getCanvas3D(){
	  return canvas3D;
  }

  private void createSceneGraph()
  {
	  distanceMatrix = new double[num_sphere][num_sphere];
	  relativeDistance  = new double[num_sphere];
		for (int i = 0; i < num_sphere; i++) {
			fitness[i] = 50;
			blink_flag[i] = false;
			color_characteristics[i] = 0;
			relativeDistance[i] = 0.0;
			for(int j = 0; j < num_sphere; j++){
				distanceMatrix[i][j] = 0.0;
			}
		}

		sceneBG = new BranchGroup();
		bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

		lightScene(); // add the lights
		addBackground(); // add the sky
//		addFog();
//		createBackground();
		addGrid(); // add cells grid

		sceneBG.compile(); // fix the scene

  }  // end of createSceneGraph()



private void lightScene()
  /* One ambient light, 2 directional lights */
  {
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    // Set up the ambient light
    AmbientLight ambientLightNode = new AmbientLight(white);
    ambientLightNode.setInfluencingBounds(bounds);
    sceneBG.addChild(ambientLightNode);

    // Set up the directional lights
    Vector3f light1Direction  = new Vector3f(-1.0f, 1.0f, -1.0f);
          // light coming from left, up, and back quadrant
    Vector3f light2Direction  = new Vector3f(1.0f, 1.0f, 1.0f);
          // light coming from right, up, and front quadrant
    Vector3f light3Direction  = new Vector3f(-1.0f, -1.0f, 1.0f);
    Vector3f light4Direction  = new Vector3f(1.0f, -1.0f, 1.0f);
    Vector3f light5Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
    Vector3f light6Direction  = new Vector3f(1.0f, -1.0f, -1.0f);

    DirectionalLight light1 = 
            new DirectionalLight(white, light1Direction);
    light1.setInfluencingBounds(bounds);
    sceneBG.addChild(light1);

    DirectionalLight light2 = 
        new DirectionalLight(white, light2Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light2);
    
    DirectionalLight light3 = 
        new DirectionalLight(white, light3Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light3);
    
    DirectionalLight light4 = 
        new DirectionalLight(white, light4Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light4);
    
    DirectionalLight light5 = 
        new DirectionalLight(white, light5Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light5);
    
    DirectionalLight light6 = 
        new DirectionalLight(white, light6Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light6);
  }  // end of lightScene()

  private void addFog(){
	    lfog = new LinearFog();
	    lfog.setCapability(Fog.ALLOW_COLOR_READ);
	    lfog.setCapability(Fog.ALLOW_COLOR_WRITE);
	    lfog.setCapability(LinearFog.ALLOW_DISTANCE_WRITE);
	    lfog.setInfluencingBounds(bounds);

	    efog = new ExponentialFog();
	    efog.setCapability(Fog.ALLOW_COLOR_READ);
	    efog.setCapability(Fog.ALLOW_COLOR_WRITE);
	    efog.setCapability(ExponentialFog.ALLOW_DENSITY_WRITE);
	    efog.setInfluencingBounds(bounds);
	    efog.setDensity(0.01f);

	    fog = efog;
	    sceneBG.addChild( fog );
  }


  private void addBackground()
  /* The background colour is obtained from the properties
     object (blue, green, white, or black). */
  { 
    Background back = new Background();
    back.setApplicationBounds( bounds );

    int bgColour = m_sps.getBGColour();
    if (bgColour == SphereProperties.BLUE)
      back.setColor(0.17f, 0.65f, 0.92f);    // sky blue colour
    else if (bgColour == SphereProperties.GREEN)
      back.setColor(0.5f, 1.0f, 0.5f);       // grass colour
    else if (bgColour == SphereProperties.WHITE)
      back.setColor(1.0f, 1.0f, 0.8f);       // off-white
    // else black by default
    sceneBG.addChild( back );
  }  // end of addBackground()
  
  private void createBackground() {

	    Image image = null;
	      Toolkit toolkit = Toolkit.getDefaultToolkit();
	      image = toolkit.getImage("bg.jpg");

	    MediaTracker mt = new MediaTracker(this);
	    mt.addImage(image, 0);
	    mt.checkAll(true);
	    try { mt.waitForID(0); } catch (InterruptedException e) { e.printStackTrace(); }
	    this.loader = new TextureLoader(image, this);
	    Background bg = new Background(this.loader.getImage());
	    bg.setCapability(Background.ALLOW_COLOR_READ);
	    bg.setCapability(Background.ALLOW_COLOR_WRITE);
	    bg.setCapability(Background.ALLOW_IMAGE_WRITE);
	    bg.setApplicationBounds(bounds);
	    sceneBG.addChild( bg );
	  }


  private void orbitControls(Canvas3D c)
  /* OrbitBehaviour allows the user to rotate around the scene, and to
     zoom in and out.
  */
  {
    ViewOrbitBehavior orbit = 
		new ViewOrbitBehavior(this, c, OrbitBehavior.REVERSE_ALL);
    orbit.setSchedulingBounds(this.bounds);
    Point3d center = new Point3d();
    this.bounds.getCenter(center);
    ViewingPlatform vp = this.su.getViewingPlatform();
    vp.setViewPlatformBehavior(orbit);	    
    
//	OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
//	orbit.setSchedulingBounds(this.bounds);
//	Point3d center = new Point3d();
//	this.bounds.getCenter(center);
//	ViewingPlatform vp = this.su.getViewingPlatform();
//	vp.setViewPlatformBehavior(orbit);
  }  // end of orbitControls()



  private void initUserPosition()
  /* Set the user's initial viewpoint using lookAt()  */
  {
//    ViewingPlatform vp = su.getViewingPlatform();
//    TransformGroup steerTG = vp.getViewPlatformTransform();
//
//    Transform3D t3d = new Transform3D( );
//    steerTG.getTransform( t3d );
//
//    // args are: viewer posn, where looking, up direction
//    t3d.lookAt( USERPOSN, new Point3d(0,0,0), new Vector3d(0,1,0));
//    t3d.invert();
//
//    steerTG.setTransform(t3d);
	  
	    ViewingPlatform vp = su.getViewingPlatform();
		steerTG = vp.getViewPlatformTransform();

		Transform3D t3d = new Transform3D();
		steerTG.getTransform(t3d);

		// args are: viewer posn, where looking, up direction
		t3d.lookAt(USERPOSN, new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
		t3d.invert();
	  
//    // Create TransformGroup to revolute spheres
//		TransformGroup revolutionTrans = new TransformGroup();
//		revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//		revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		steerTG.addChild(revolutionTrans);
//
//		Alpha alpha = new Alpha(-1, 100000);
//		ZRotationInterpolator revolutionInterpolator = new ZRotationInterpolator(
//				alpha, revolutionTrans);
//		revolutionInterpolator.setSchedulingBounds(bounds);
//		steerTG.addChild(revolutionInterpolator);
    


		steerTG.setTransform(t3d);
  }  // end of initUserPosition()


// ------------------------ cells grid -------------------------

  @SuppressWarnings("deprecation")
private void addGrid()
  /*  Create the cells grid and a time behaviour to update
      it at TIME_DELAY intervals. */
  {
	  	cellsGrid = new Spheres3D(this, m_sps);
//	  	setTranslateInterpolator();
//	  	setRevolutionInterpolator();
		sceneBG.addChild(cellsGrid.getBaseTG());
//		
//		Circles3D circleGrid = new Circles3D(m_sps);
//		sceneBG.addChild(circleGrid.getBaseTG());
		
		

//		MouseDragBehavior mdb = new MouseDragBehavior(canvas3D,
//		sceneBG, bounds);
//		sceneBG.addChild(mdb);
		
		tb = new TimeRevolutionBehavior(this, TIME_DELAY, cellsGrid);
		tb.setSchedulingBounds(bounds);
		sceneBG.addChild(tb);
//		tb.setEnable(false);
		td = new TimeDisappearBehavior(this, TIME_DELAY, cellsGrid);
		td.setSchedulingBounds(bounds);
		sceneBG.addChild(td);
		td.setEnable(false);
		
		tps = new TimePullSpheresBehavior(this, TIME_DELAY / 4, cellsGrid);
		tps.setSchedulingBounds(bounds);
		sceneBG.addChild(tps);
//		tps.setEnable(false);
		
//		tvs = new TimeVibrationSpheresBehavior(this, TIME_DELAY, cellsGrid);
//		tvs.setSchedulingBounds(bounds);
//		sceneBG.addChild(tvs);
//		tps.setEnable(false);

	  	
//	    PickHighlightBehavior highlight = 
//	    	new PickHighlightBehavior(canvas3D, sceneBG, bounds);
	    
//		tb = new TimeColorChangeBehavior(TIME_DELAY, cellsGrid);
//		tb.setSchedulingBounds(bounds);
//		sceneBG.addChild(tb);
//		tb.setEnable(false);

//		PickRotateBehavior rotator = new PickRotateBehavior(sceneBG, canvas3D,
//				bounds);
//		sceneBG.addChild(rotator);
		
//		PickLineRotateBehavior rotator = new PickLineRotateBehavior(sceneBG, canvas3D,
//				bounds);
//		sceneBG.addChild(rotator);
		
		
//	    //Create TransformGroup to revolute spheres
//	    TransformGroup trans = new TransformGroup();
//	    sceneBG.addChild(trans);
//	    
//	    TransformGroup gtrans = new TransformGroup();
//	    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//	    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//	    gtrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
//	    trans.addChild(gtrans);
//	    TransformGroup revolutionTrans = new TransformGroup();
//	    revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//	    revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//	    sceneBG.addChild(revolutionTrans);
//
//	    Alpha alpha = new Alpha(-1, 100000);
//	    ZRotationInterpolator revolutionInterpolator =
//	      new ZRotationInterpolator(alpha, revolutionTrans);
//	    revolutionInterpolator.setSchedulingBounds(bounds);
//	    sceneBG.addChild(revolutionInterpolator);
//	    revolutionTrans.addChild(cellsGrid.getBaseTG());
		
//	    //Create TransformGroup to interpolate the color of spheres
//	    Alpha calpha = new Alpha();
//	    calpha.setTriggerTime(0);
//	    calpha.setPhaseDelayDuration(0);
//	    calpha.setIncreasingAlphaDuration(1000);
//	    calpha.setIncreasingAlphaRampDuration(0);
//	    calpha.setAlphaAtOneDuration(0);
//	    calpha.setAlphaAtZeroDuration(0);
//	    calpha.setDecreasingAlphaDuration(0);
//	    calpha.setDecreasingAlphaRampDuration(0);
//	    
//	    Appearance capp = createColorAppearance();
//	    ColorInterpolator cinterp = createColorInterpolator(calpha, capp.getMaterial());
//	    cinterp = createColorInterpolator(calpha, capp.getMaterial());
//	    cinterp.setStartColor(scolor);
//	    cinterp.setEnable(true);
//	    sceneBG.addChild(cinterp);
	    
//	  	cellsGrid = new Spheres3D(m_sps);
//	  	for(int i = 0; i < num_sphere; i++){
//	  	cellsGrid.getSphere3D(i).setAppearacne(capp);
//	  	}
//		sceneBG.addChild(cellsGrid.getBaseTG());
		
		Circles3D circleGrid = new Circles3D(m_sps);
		sceneBG.addChild(circleGrid.getBaseTG());
		
		cells = cellsGrid.getSphere3D();
		
//	    cells = new Sphere3D[num_sphere];
//	    for(int i = 0; i < num_sphere; i++){
//	    	cells[i] = new Sphere3D(i, sphereProps);
//	    	baseTG.addChild(spheres[i].getTG());
//	    }

//		
//		PickIndividualDynamicColorChangeBehavior[] icbehavior;	
//		for(int i = 0; i < num_sphere; i++){
//			PickIndividualDynamicColorChangeBehavior icbehavior = new PickIndividualDynamicColorChangeBehavior(sceneBG,
//				canvas3D, bounds, cells[i]);
//		}
//		sceneBG.addChild(icbehavior);
		
		
//		PickIndividualDynamicColorChangeBehavior[] icbehaviors;
//		icbehaviors = new PickIndividualDynamicColorChangeBehavior[num_sphere];
//		for(int i = 0; i < num_sphere; i++){
//			icbehaviors[i] = new PickIndividualDynamicColorChangeBehavior(sceneBG,
//				canvas3D, bounds, cells[i]);
//				sceneBG.addChild(icbehaviors[i]);
//		}
		
//		PickIndividualDynamicColorChangeBehavior icbehaviors;
//		for(int i = 0; i < num_sphere; i++){
//			PickIndividualDynamicColorChangeBehavior icbehaviors = new PickIndividualDynamicColorChangeBehavior(this, sceneBG,
//				canvas3D, bounds, cells[i]);
//				sceneBG.addChild(icbehaviors);
//		}



//		PickDynamicColorChangeBehavior transer = new PickDynamicColorChangeBehavior(sceneBG,
//				canvas3D, bounds, cellsGrid);
//				sceneBG.addChild(transer);
				
//		PickTranslateBehavior translator = new PickTranslateBehavior(sceneBG,
//				canvas3D, bounds);
//		sceneBG.addChild(translator);

//		IndividualPicking picker = new IndividualPicking(sceneBG, canvas3D,
//				bounds, PickTool.GEOMETRY_INTERSECT_INFO,
//				PickResult.TRANSFORM_GROUP, m_sps);
//		picker.setupCallback(new IndividualPickingCallback() {
//			public void picked(int type, Node node) {
//				if (node != null) {
//					// String data = (String)node.getUserData();
//					sind = (SphereIndividual) node.getUserData();
//					int index = sind.getIndex();
//					int f = sind.getFitness();
//					fitness[index] = f;
//					
//					topLevel.callIndividual(index);
//					
//					// System.out.println(sind);
//					// System.out.println("Index: " + data.index);
//					// System.out.println("Fitness: " + data.fitness);
//					System.out.println("fitness[" + index + "]: "
//							+ fitness[index]);
//
//				} else {
//					System.out.println("Error: node is null.");
//				}
//			}
//		});
//		sceneBG.addChild(picker);
		
		PickSphereTranslateBehavior zoomer = new PickSphereTranslateBehavior(this, sceneBG,
				canvas3D, bounds, PickResult.TRANSFORM_GROUP, m_sps);
		zoomer.setupCallback(new MouseSphereTranslateCallback() {
			public void pickIndividual(final int type, final Node node) {
				if (node != null) {
					tps.setEnable(false);
//					tb.setEnable(true);
//					System.out.println("pickIndividual");
					// String data = (String)node.getUserData();
					sind = (SphereIndividual) node.getUserData();
//					pickedXpos = sind.getX();
//					pickedYpos = sind.getY();
//					pickedRpos = sind.getTransformedR();
//					pickedThetapos = sind.getTransformedTheta(); 
					pickedSind = (SphereIndividual) node.getUserData();

					index = sind.getIndex();
					f = sind.getTransformedFitness();
					fitness[index] = f;
					if(sind != bufferSind){
						changed_flag = true;
					}else{
						changed_flag = false;
					}
					if(blink_flag[index] == true && changed_flag == false){
						click_counter += 1;
					}else{
						click_counter = 0;
					}
					
//					if(click_counter > 0){
//						tps.setEnable(true);
//					}else{
//						tps.setEnable(false);
//					}
					
				    calculateDistanceMatrix(index);
				    
					for(int i = 0; i< fitness.length; i++){
					cellsGrid.getSphere3D(i).setScale();
//					cellsGrid.getSphere3D(i).pullSpheresUpdate();
					
					}
					
					//flag
//					sind.setBlinkFlag(true);
//					for(int i = 0; i < num_sphere; i++){
//						blink_flag[i] = false;
//					}
//					blink_flag[index] = true;
	
				    
//				    int now_picked = 0;
//				    int rank_counter = 0;
//					for(int i = 0; i< fitness.length; i++){
//						if(blink_flag[i] == true){
//							now_picked = i;
//							pull_ranking[i] = 1;
//						}
//					}
//					for(int j = 0; j< fitness.length; j++){
//					for(int i = 0; i< fitness.length; i++){
//						if(color_characteristics[i] == now_picked + rank_counter){
//							pull_ranking[i] = rank_counter;
//						}
//						
//						if(color_characteristics[i] == now_picked - rank_counter){
//							pull_ranking[i] = rank_counter;
//						}
//					}
//					rank_counter++;
//					}

					
					
					topLevel.callIndividual(index);
//					tps.setEnable(true);
//					revolutionInterpolator.setEnable(false);
					
					// System.out.println(sind);
					// System.out.println("Index: " + data.index);
					//System.out.println("Fitness: " + data.fitness);
//					System.out.println("fitness[" + index + "]: "
//							+ fitness[index]);
//					System.out.println("blink_flag[" + index + "]: "
//							+ blink_flag[index]);
					bufferSind = (SphereIndividual) node.getUserData();
				} else {
					System.out.println("Error: node is null.");
					//flag
					
//					tb.setEnable(false);
				}
			}
		});
		sceneBG.addChild(zoomer);
		
//		PickSphereColorChangeBehavior changer = new PickSphereColorChangeBehavior(this, sceneBG,
//				canvas3D, bounds, PickResult.TRANSFORM_GROUP, m_sps);
//		changer.setUsetupCallback(new MouseSphereColorChangeCallback() {
//			public void pickIndividual(final int type, final Node node) {
//				if (node != null) {
//
//				} else {
//					System.out.println("Error: node is null.");
////					tb.setEnable(false);
//				}
//			}
//		});
//		sceneBG.addChild(changer);
        
//		OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
//		orbit.setSchedulingBounds(this.bounds);
//		Point3d center = new Point3d();
//		this.bounds.getCenter(center);
//		ViewingPlatform vp = this.su.getViewingPlatform();
//		vp.setViewPlatformBehavior(orbit);
    
  }  // end of addGrid()
  
  public void setRevolutionInterpolator(){
	    //Create TransformGroup to revolute spheres
	    TransformGroup trans = new TransformGroup();
	    sceneBG.addChild(trans);
	    
	    TransformGroup gtrans = new TransformGroup();
	    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    gtrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	    trans.addChild(gtrans);
	    revolutionTrans = new TransformGroup();
	    revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    sceneBG.addChild(revolutionTrans);

	    Alpha alpha = new Alpha(-1, 100000);
	    revolutionInterpolator =
	      new ZRotationInterpolator(alpha, revolutionTrans);
	    revolutionInterpolator.setSchedulingBounds(bounds);
	    sceneBG.addChild(revolutionInterpolator);
//	    revolutionTrans.addChild(cellsGrid.getBaseTG());
//	    revolutionInterpolator.setEnable(false);

  }
  
  public void setTranslateInterpolator(){
	    TransformGroup trans = new TransformGroup();
	    sceneBG.addChild(trans);
	    
	    TransformGroup gtrans = new TransformGroup();
	    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    gtrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	    trans.addChild(gtrans);
	    TransformGroup translateTrans = new TransformGroup();
	    translateTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    translateTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    sceneBG.addChild(translateTrans);
	    


	       
//        Alpha alpha = new Alpha(
//                -1,  
//                Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,
//                0, 
//                0, 
//                3000, 
//                400, 
//                0, 
//                3000, 
//                400, 
//                0
//             );
	    
        Alpha alpha = new Alpha(
                -1,  
                Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE,
                0, 
                0, 
                3000, 
                400, 
                0, 
                3000, 
                400, 
                0
             );
        
//        	float sp, ep;
//        	Random rand1 = new Random();
//        	float tp1 = rand1.nextFloat();
//        	float p1 = tp1 * 0.40f - 0.20f;
//        	Random rand2 = new Random();
//        	float tp2 = rand2.nextFloat();
//        	float p2 = tp2 * 0.40f - 0.20f;
//        	if(p1 >= p2){
//        		sp = p2;
//        		ep = p1;
//        	}else{
//        		sp = p1;
//        		ep = p2;
//        	}

             Transform3D axis = new Transform3D();
             PositionInterpolator posit = 
                 new PositionInterpolator(alpha, translateTrans, axis, 0.03f, -0.12f);
//                 new PositionInterpolator(alpha, translateTrans, axis, sp, ep);
             posit.setSchedulingBounds(bounds);
             sceneBG.addChild(posit);
     	  translateTrans.addChild(cellsGrid.getBaseTG());
             
//	    revolutionInterpolator.setEnable(false);
  }
  
  private ColorInterpolator createColorInterpolator(Alpha alpha, Material mat){
	  ColorInterpolator cinterp = 
		  new ColorInterpolator(alpha, mat, scolor, ecolor);
	  cinterp.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
	  return cinterp;
  }
  private Appearance createColorAppearance() {
		Appearance app = new Appearance();
		app.setCapability(Appearance.ALLOW_MATERIAL_READ);
		app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

		Material mat = new Material();
		mat.setCapability(Material.ALLOW_COMPONENT_READ);
		mat.setCapability(Material.ALLOW_COMPONENT_WRITE);
		/*
		 * mat.setDiffuseColor( new Color3f(1.0f, 0.0f, 0.0f) );
		 * mat.setShininess(100.0f); app.setMaterial(mat);
		 */
		Color3f objColor = new Color3f(0.3f, 0.4f, 1.5f);
		app.setMaterial(new Material(black, black, objColor, white, 80.0f));

		return app;
	}
  
  public double[][] getDistanceMatrix(){
	  return this.distanceMatrix;
  }
  public int getFitness(int index){
	  return fitness[index];
  }
  
  public int[] getFitnesses(){
	  return fitness;
  }
  
  public void setFitness(int index, int fitness){
	  this.fitness[index] = fitness;
  }
  
  public boolean[] getBlinkFlags(){
	  return blink_flag;
  }
  
  public boolean getBlinkFlag(int index){
	  return blink_flag[index];
  }
  
  public void setBlinkFlag(int index, boolean flag){
	  this.blink_flag[index] = flag;
  }
  
  public void checkFlag(){
	  for(int i = 0; i < this.num_sphere; i++){
		  if(this.blink_flag[i] == true){
			  this.check_flag = true;
		  }
	  }
	  
  }
  
  public boolean getCheckFlag(){
	  return this.check_flag;
  }
  
  
  public Spheres3D getSpheres3D(){
	  return this.cellsGrid;
  }
  
  public TimeRevolutionBehavior getTimeRevolutionBehavior(){
	  return this.tb;
  }
  
  public TimeDisappearBehavior getTimeDisappearBehavior(){
	  return this.td;
  }
  
  public TimePullSpheresBehavior getTimePullSpheresBehavior(){
	  return this.tps;
  }
  
  public int[] getColorCharacteristicses(){
	  return this.color_characteristics;
  }
  
  public int getColorCharacteristics(int index){
	  return this.color_characteristics[index];
  }
  
  public int[] getRankings(){
	  return this.pull_ranking;
  }
  
  public int getRanking(int index){
	  return this.pull_ranking[index];
  }
  
  public TransformGroup getSteerTG(){
	  return this.steerTG;
  }
  
  public SphereIndividual getPickedIndividual(){
	  return this.pickedSind;
  }
  
  public void setPickedXpos(double x){
	  this.pickedXpos = x;
  }
  
  public double getPickedXpos(){
	  return this.pickedXpos;
  }
  
  public void setPickedYpos(double y){
	  this.pickedYpos = y;
  }
  
  public double getPickedYpos(){
	  return this.pickedYpos;
  }
  
  public void setPickedRpos(double r){
	  this.pickedRpos = r;
  }
  
  public double getPickedRpos(){
	  return this.pickedRpos;
  }
  
  public void setPickedThetapos(double theta){
	  this.pickedThetapos = theta;
  }
  
  public double getPickedThetapos(){
	  return this.pickedThetapos;
  }
  public void calculateDistanceMatrix(int index){
			  distanceMatrix = DistanceCalculator.getDistanceMatrix(topLevel.getOperationWindows().getPopulation(), num_sphere);
			  double[] temp1 = new double[num_sphere];
//			  double[] temp2 = new double[num_sphere];
//			  double[] temp3 = new double[num_sphere];
			  for(int i = 0; i < num_sphere; i++){
				  temp1[i] = distanceMatrix[0][i];
			  }
			  Rank rank1 = new Rank();
			  color_characteristics = rank1.getRank(temp1);
			  
			  for(int j = index; j < num_sphere; j++){
				  temp_ranking1[j] = distanceMatrix[index][j];
			  }
			  
			  
			  for(int k = 0; k < index; k++){
					  temp_ranking1[k] = distanceMatrix[k][index];
			  }
			  Rank rank2 = new Rank();
			  temp_ranking2 = rank2.getRank(temp_ranking1);
			  
			  for(int n = 0; n < num_sphere; n++){
				  pull_ranking[n] = num_sphere - temp_ranking2[n] + 1;
			  }
			  
			  
//			  System.out.println("DistanceMatrix: ");
//			  for(int i = 0; i < this.num_sphere; i++){
//				  for(int j = 0; j < this.num_sphere; j++){
//					  System.out.print(distanceMatrix[i][j] + "  ");
//				  }
//				  System.out.println();
//			  }
//			  ArrayIns qsort = new ArrayIns(num_sphere);

//			  for(int j = 0; j < num_sphere; j++){
//				  double n = temp1[j];
//				  qsort.insert(n);
//			  }
//			  qsort.display();
//			  qsort.setNum(temp1);
//			  qsort.quickSort();
//			  qsort.display();
//			  temp2 = qsort.getNums();
//			  double min = temp2[0];
//			  double max = temp2[num_sphere-1];
//			  double range = max - min;
//			  
//			  for(int j = 0; j < this.num_sphere; j++){
//				  System.out.println("temp1[" + j +
//						  "]: " + temp1[j]);
//				  System.out.println("temp2[" + j +
//						  "]: " + temp2[j]);
//				  System.out.println("temp_ranking1[" + j +
//						  "]: " + temp_ranking1[j]);
//				  System.out.println("pull_ranking[" + j +
//						  "]: " + pull_ranking[j]);
//				  
//				  System.out.println("color_characteristics[" + j +
//						  "]: " + color_characteristics[j]);
//				  temp3[j] = (temp1[j] - min) / range * 10.0;
//				  color_characteristics[j] = temp3[j];
//				  System.out.println("color_characteristics[" + j + "]: " +
//						  color_characteristics[j]	  );
//				  for(int k = 0; k < num_sphere; k++){
//				  if(temp2[j] == temp1[k]){
//					  color_characteristics[k] = j;
//				  }
//				  }
//
//			  }
//			  for(int j = 0; j < this.num_sphere; j++){
//
//				  System.out.println("color_characteristics[" + j +
//						  "]: " + color_characteristics[j]);
//			  }
//			  for(int j = 0; j < this.num_sphere; j++){
//				  System.out.println("temp_ranking1[" + j +
//						  "]: " + temp_ranking1[j]);
//			  }
//			  for(int j = 0; j < this.num_sphere; j++){
//				  System.out.println("pull_ranking[" + j +
//						  "]: " + pull_ranking[j]);
//				  
//			  }

			    
			    
  }


} // end of WrapLife3D class