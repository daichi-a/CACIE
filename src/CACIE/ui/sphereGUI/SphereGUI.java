package CACIE.ui.sphereGUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColorInterpolator;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import CACIE.eventlist.CommonEventList;
import CACIE.ui.EvaluatingIndividual;

import CACIE.ui.GraphicalPopulationPresenterEvaluator;
import CACIE.ui.OperationWindows;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class SphereGUI extends GraphicalPopulationPresenterEvaluator {
  private Canvas3D canvas = null;
  private Canvas3D canvas3D = null;
  private boolean isStandalone = false;
  BranchGroup scene = null;
  BranchGroup root = null;
  BranchGroup branchGroup = null;
  SimpleUniverse universe = null;
  WrapSpheres3D w3d;
  SphereProperties spherePros;
  
  private Color3f scolor = new Color3f(1.0f, 0.0f, 0.0f);
  private Color3f ecolor = new Color3f(0.0f, 0.0f, 1.0f);
  private ColorInterpolator cinterp = null;
  Primitive[] shapes;
  public JPanel panel;
  JPanel listThisGene;
  JFrame jind;
  
  SphereIndividual sind;
  
  int num = 16;
  int[] fitness = new int[num];
  //private Material material = new Material();

  Color3f red = new Color3f(Color.RED);
  Color3f black = new Color3f(Color.BLACK);
  Color3f white = new Color3f(Color.WHITE);
  Color3f blue = new Color3f(Color.BLUE);
  Color3f gray = new Color3f(0.4f, 0.4f, 0.4f);

  Material redMat = new Material(red, black, red, white, 75.0f);
  Material blueMat = new Material(blue, black, blue, white, 75.0f);
  private JFrame topFrame;
  OperationWindows opw;
  
	JScrollPane ThisScrollPane;
	ArrayList<EvaluatingIndividual> DistInds;

  public SphereGUI(OperationWindows operationWindows){
  	super( operationWindows );
  	opw = operationWindows;
	topFrame = new JFrame("CACIE Sphere");
	topFrame.setLayout(new BorderLayout());
	JPanel listThisGene = new JPanel();
	listThisGene.setLayout(new GridLayout(operationWindows.populationSize, 1));

	DistInds = new ArrayList<EvaluatingIndividual>(operationWindows.populationSize);
	setupGUI(operationWindows);
	preInitialize();
	afterInitialize();
	//System.err.println("Size of DistInds is:" + operationWindows.DistInds.size()+ ", PopulationSize is: " + opwin.populationSize);
	
//	ThisScrollPane = new JScrollPane();
//	ThisScrollPane.getViewport().setView(listThisGene);
//	JPanel individualPanel = new JPanel();
//	individualPanel.setLayout(new GridLayout(1, 2));
//	individualPanel.add(this.ThisScrollPane);
//	ThisScrollPane.setPreferredSize(new Dimension(500, 250));

//	// upperPanel
//	JPanel upperPanel = new JPanel();
//	upperPanel.setLayout(new BorderLayout());
//	upperPanel.add(individualPanel);

    spherePros = new SphereProperties();


    // center this window
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
	
	//�X�[�p�[�N���X�ŏ������R���g���[���{�^���̃p�l����z�u
	topFrame.add(controlButtons, BorderLayout.SOUTH);
	
	// Create Spheres
	w3d = new WrapSpheres3D(this, spherePros);
	//init();
	canvas3D = w3d.getCanvas3D();
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(canvas3D, BorderLayout.CENTER);
	topFrame.add(panel, BorderLayout.CENTER);
	topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	jind = new JFrame();

	System.out.println(panel.getPreferredSize());

//	topFrame.setSize(1300, 600);
	topFrame.setSize(800, 800);
//	topFrame.setLocation(100, 100);
	topFrame.setVisible(true);
	//topFrame.pack();
  }
//
//  public SphereGUI(boolean isStandalone) {
//    this.isStandalone = isStandalone;
//  }
	protected void setupGUI(OperationWindows operationWindows){
		//topFrame = new JFrame("Population Display");
		listThisGene = new JPanel();
//		listThisGene.setLayout(new GridLayout(operationWindows.populationSize, 1));
		listThisGene.setLayout(new GridLayout(1, 1));

		for (int i = 0; i < operationWindows.populationSize; i++){
			EvaluatingIndividual tmpEvInd = new EvaluatingIndividual(new CommonEventList(0), operationWindows, i);
//			listThisGene.add(tmpEvInd);
//			listThisGene.add(tmpEvInd, BorderLayout.CENTER);
			DistInds.add(tmpEvInd);
		}

//		listThisGene.add(DistInds.get(0));
		System.err.println("Size of DistInds is:" + operationWindows.DistInds.size()+ ", PopulationSize is: " + operationWindows.populationSize);
		
		ThisScrollPane = new JScrollPane();
		ThisScrollPane.getViewport().setView(listThisGene);
		JPanel individualPanel = new JPanel();
		individualPanel.setLayout(new GridLayout(1, 2));
		//individualPanel.add(this.ThisScrollPane);
		ThisScrollPane.setPreferredSize(new Dimension(500, 250));

		// upperPanel
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new BorderLayout());
		upperPanel.add(individualPanel);
		
		//�X�[�p�[�N���X�ŏ������R���g���[���{�^���̃p�l����z�u
		upperPanel.add(controlButtons, BorderLayout.SOUTH);
		topFrame.add(upperPanel, BorderLayout.WEST);
		topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		System.out.println(upperPanel.getPreferredSize());

//		topFrame.setSize(1300, 600);
//		topFrame.setVisible(true);
		//topFrame.pack();
	}
	
	  public void callIndividual(int index){
		  listThisGene.removeAll();
		  listThisGene.add(DistInds.get(index), BorderLayout.CENTER);
		  listThisGene.validate();
		  listThisGene.repaint();
//		  jind = new JFrame();
//		  jind.removeAll();
		  jind.add(listThisGene);
//		  jind.setLocation(100, 300);
		  jind.setSize(300, 100);
		  jind.validate();
		  jind.setVisible(true);
		  opw.stopAll();
		  try {
			DistInds.get(index).startPlaying();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		  System.out.println("callIndividual");
	  }
	
	  private boolean hasJ3D()
	// check if Java 3D is available
	{
		try { // test for an essential Java 3D class
			Class.forName("com.sun.j3d.utils.universe.SimpleUniverse");
			return true;
		} catch (ClassNotFoundException e) {
			System.err.println("Java 3D not installed");
			return false;
		}
	} // end of hasJ3D()
	  

  public void init() {
	 
	for(int i = 0; i < num; i++){
		fitness[i] = 50;
	}
	  
    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    canvas = new Canvas3D(config);
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(canvas, BorderLayout.CENTER);

    
    universe = new SimpleUniverse(canvas);
    //SimpleUniverse universe = new SimpleUniverse(canvas);
    universe.getViewingPlatform().setNominalViewingTransform();
    //BranchGroup scene = null;
    scene = createSceneGraph();
    scene.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    scene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    scene.setCapability(BranchGroup.ALLOW_DETACH);
    universe.addBranchGraph(scene);
  }

  private BranchGroup createSceneGraph() {
//    BranchGroup root = new BranchGroup();
//    BranchGroup branchGroup = new BranchGroup();
    BranchGroup root = new BranchGroup();
    BranchGroup branchGroup = new BranchGroup();
    branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
    branchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
    branchGroup.setCapability(BranchGroup.ALLOW_DETACH);
    
    BoundingSphere bounds = new BoundingSphere( new Point3d(), 100.0 );
    
    //Create a Transformgroup to scale all objects
    TransformGroup objScale = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setScale(1.0);
    objScale.setTransform(t3d);
    root.addChild(objScale);
    
    // Attach picking behavior utlities to the scene root.
    // They will wake up when user manipulates a scene node.
    /*
    PickRotateBehavior rotator =
      new PickRotateBehavior(root, canvas, bounds);
    root.addChild(rotator);
    */

    /*
    PickTranslateBehavior translator =
      new PickTranslateBehavior(root, canvas, bounds);
    root.addChild(translator);
    */
    
    /*
    PickingLineTranslateBehavior translator =
        new PickingLineTranslateBehavior(root, canvas, bounds);
      root.addChild(translator);
    */
    
    
//    PickLineZoomBehavior zoomer =
//      new PickLineZoomBehavior(root, canvas, bounds);
//    root.addChild(zoomer);
    
    
    PickHighlightBehavior highlight = 
    	new PickHighlightBehavior(canvas, root, bounds);
    
//    IndividualPicking picker =
//        new IndividualPicking( root, canvas, bounds,
//  			 PickTool.GEOMETRY_INTERSECT_INFO, PickResult.SHAPE3D );
//      picker.setupCallback( new IndividualPickingCallback() {
//        public void picked(int type, Node node) {
//  	if (node != null) {
//					// String data = (String)node.getUserData();
//					sind = (SphereIndividual) node.getUserData();
//					int index = sind.getIndex();
//					int f = sind.getFitness();
//					fitness[index] = f;
//					// System.out.println(data);
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
//		root.addChild(picker);
    
    
    /*
    PickColorBehavior bcolor =
        new PickColorBehavior(root, canvas, bounds, obj);
      root.addChild(bcolor);
      */
    /*
    MouseDragBehavior translator =
        new MouseDragBehavior(canvas, root, new BoundingSphere(new Point3d(), 100.0));
      root.addChild(translator);
      */
      /*
      TimerBehavior timer = new TimerBehavior(1000, new TimerBehaviorCallback(){
    	  public void wakeup(){
    		  System.out.println("wakeup");
    	  }
      });
      timer.setSchedulingBounds(
    		  new BoundingSphere(new Point3d(), 100.0));
      root.addChild(timer);
	*/
    /*
    picking001 picker =
      new picking001( root, canvas, bounds,
			 PickObject.PRIMITIVE );
    picker.setupCallback( new pickingCallback001() {
      public void picked(int type, Node node) {
	  
	if (node != null) {
	  String data = (String)node.getUserData();
	  System.out.println(data);
	} else {
	  System.out.println("Error: node is null.");
			}
    }
    }
    );
    root.addChild(picker);
    */
    
    TransformGroup trans = new TransformGroup();
    root.addChild(trans);
    
    TransformGroup gtrans = new TransformGroup();
    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    gtrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    gtrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    trans.addChild(gtrans);
    

    double[] vertices = { -0.8, -0.8, 0.0, -0.8, 0.8, 0.0, -0.4, -0.8, 0.0,
				-0.4, 0.8, 0.0, 0.0, -0.8, 0.0, 0.0, 0.8, 0.0, 0.4, -0.8, 0.0,
				0.4, 0.8, 0.0, 0.8, -0.8, 0.0, 0.8, 0.8, 0.0, -0.8, -0.8, 0.0,
				0.8, -0.8, 0.0, -0.8, -0.4, 0.0, 0.8, -0.4, 0.0, -0.8, 0.0,
				0.0, 0.8, 0.0, 0.0, -0.8, 0.4, 0.0, 0.8, 0.4, 0.0, -0.8, 0.8,
				0.0, 0.8, 0.8, 0.0 };

    /*
	 * LineArray geom = new LineArray( vertices.length,
	 * GeometryArray.COORDINATES);
	 * 
	 * gom.setCoordinates(0, vertices);
	 * eom.setCapability(Geometry.ALLOW_INTERSECT); Shape3D grid = new
	 * Shape3D(geom); grid.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
	 * gtrans.addChild(grid);
	 */
    // Appearance ap = createAppearance();
    // Set up the background
    Color3f bgColor = new Color3f(1.0f, 1.0f, 1.0f);
    Background bg = new Background(bgColor);
    bg.setApplicationBounds(bounds);
    root.addChild(bg);

    // Set up the global lights
    Color3f lColor1 = new Color3f(0.9f, 0.9f, 0.9f);
    Vector3f lDir1 = new Vector3f(-1.0f, -1.0f, -1.0f);
    Color3f alColor = new Color3f(0.6f, 0.6f, 0.6f);

    AmbientLight aLgt = new AmbientLight(alColor);
    aLgt.setInfluencingBounds(bounds);
    DirectionalLight lgt1 = new DirectionalLight(lColor1, lDir1);
    lgt1.setInfluencingBounds(bounds);
    root.addChild(aLgt);
    root.addChild(lgt1);
    
    
    //Create TransformGroup to revolute spheres
    TransformGroup revolutionTrans = new TransformGroup();
    revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    revolutionTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    trans.addChild(revolutionTrans);

    Alpha alpha = new Alpha(-1, 100000);
    ZRotationInterpolator revolutionInterpolator =
      new ZRotationInterpolator(alpha, revolutionTrans);
    revolutionInterpolator.setSchedulingBounds(bounds);
    root.addChild(revolutionInterpolator);
    

	
    
    //Create a bunch of objects with a behavior and add them
    int num = 16;
    double r = 0.4;
    //Appearance app = createAppearance();
    Appearance capp = createColorAppearance();
    
    double theta = (double) ((Math.PI / 180.0) * ( 360.0f / num ));
    
    for (int i = 0; i < num; i++) {
			// shapes = new Primitive[num];

			double ypos = (double) (r * Math.sin(theta * i));
			double xpos = (double) (r * Math.cos(theta * i));
			branchGroup.addChild(createObject(i, capp/* app */, 0.1, xpos,
					ypos));
			// objScale.addChild(createObject(i, capp/* app */, 0.1, xpos,
			// ypos));
		}
    
    //Create TransformGroup to interpolate the color of spheres
    Alpha calpha = new Alpha();
    calpha.setTriggerTime(0);
    calpha.setPhaseDelayDuration(0);
    calpha.setIncreasingAlphaDuration(1000);
    calpha.setIncreasingAlphaRampDuration(0);
    calpha.setAlphaAtOneDuration(0);
    calpha.setAlphaAtZeroDuration(0);
    calpha.setDecreasingAlphaDuration(0);
    calpha.setDecreasingAlphaRampDuration(0);

    cinterp = createColorInterpolator(calpha, capp.getMaterial());
    cinterp.setStartColor(scolor);
    cinterp.setEnable(true);
    root.addChild(cinterp);
    
    /*
    Transform3D st3d = new Transform3D();
    st3d.set(new Vector3d(0.4, -0.4, 0.0));
    TransformGroup strans = new TransformGroup(st3d);
    strans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    strans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    strans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
    trans.addChild(strans);
    
    Sphere sphere = new Sphere( 0.05f,
                                Primitive.GENERATE_TEXTURE_COORDS |
                                Primitive.ENABLE_GEOMETRY_PICKING,
                                ap );
    sphere.setUserData("this is sphere.");
    strans.addChild(sphere);
    */
    //root.compile();
    root.addChild(branchGroup);
    //revolutionTrans.addChild(branchGroup);
    return root;
  }
  
  private ColorInterpolator createColorInterpolator(Alpha alpha, Material mat){
	  ColorInterpolator cinterp = 
		  new ColorInterpolator(alpha, mat, scolor, ecolor);
	  cinterp.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
	  return cinterp;
  }

  private Appearance createAppearance() {
	Appearance app = new Appearance();

	      // Set up the material properties
	Color3f objColor = new Color3f(0.3f, 0.4f, 1.5f);
	app.setMaterial(new Material(black, black, objColor, white, 80.0f));

	return app;
	  
	/*
    Appearance app = new Appearance();

    // Texture
    Image image = null;
    if (this.isStandalone) {
      // as application
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      image = toolkit.getImage("aqua.jpg");
    } else {
      // as applet
      image = getImage(getCodeBase(), "aqua.jpg");
    }
    MediaTracker mt = new MediaTracker(this);
    mt.addImage(image, 0);
    mt.checkAll(true);
    try { mt.waitForID(0); } catch (InterruptedException e) { e.printStackTrace(); }
    Texture texture = new TextureLoader(image, this).getTexture();
    
    app.setTexture(texture);
    
    return app;
    */
     
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
  
  private Group createObject(int i, Appearance app, double scale, 
		  double xpos, double ypos){
	  	double slope;
	  
	  	//Primitive obj = null;
	  	//Shape3D shape = new Cube();
	  	//Shape3D shape = new Tetrahedron();
	  	
	  	Primitive primitive = new Sphere(0.4f, Sphere.GENERATE_NORMALS
		          | Sphere.GENERATE_TEXTURE_COORDS, app);
	  	Shape3D shape = primitive.getShape(Sphere.BODY);
	  	
	  	shape.setAppearance(app);
	  	shape.setCapability(shape.ALLOW_APPEARANCE_READ);
	  	shape.setCapability(shape.ALLOW_APPEARANCE_WRITE);
	  	
	  	int iniFitnessValue = 50;
	  	SphereIndividual sindividual = new SphereIndividual();
	  	sindividual.setFitness(iniFitnessValue);
	  	sindividual.setIndex(i);
	  	shape.setUserData(sindividual);
	  	
	  	Transform3D t = new Transform3D();
	  	t.set(scale, new Vector3d(xpos, ypos, 0.0));
	  	
	  	slope = ypos / xpos;
	  	
	  	TransformGroup objTrans = new TransformGroup(t);
	  	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	  	objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	  	objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

	    // Create a second transform group node and initialize it to the
	    // identity. Enable the TRANSFORM_WRITE capability so that
	    // our behavior code can modify it at runtime.
	    TransformGroup spinTg = new TransformGroup();
	    spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	    spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
	    spinTg.addChild(shape.cloneNode(true));
	    //spinTg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
	    /*
	    obj = (Primitive) new Sphere(0.5f, Sphere.GENERATE_NORMALS
	          | Sphere.GENERATE_TEXTURE_COORDS, app);
	    obj.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);
		*/
	    //shapes[i] = obj;
	    //System.out.println("shpes: " + shapes[i]);
	    //material = redMat;
	    //app.setMaterial(material);
	    
	    
	    Transform3D yAxis = new Transform3D();
	    Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0,
	    		5000, 0, 0, 0, 0, 0);
	    RotationInterpolator rotator = new RotationInterpolator(rotationAlpha,
	    		spinTg, yAxis, 0.0f, (float)Math.PI * 2.0f);
	    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
	    		100.0);
	    rotator.setSchedulingBounds(bounds);
	    
	    /*
	    StretchBehavior m_StretchBehavior = new StretchBehavior((GeometryArray) shape
	            .getGeometry());
	        m_StretchBehavior.setSchedulingBounds(bounds);
	        objTrans.addChild(m_StretchBehavior);
	        m_StretchBehavior.setEnable(true);
	    */
	    
	    // add it to the scene graph.
	    //spinTg.addChild(obj);
	    
	    objTrans.addChild(rotator);
		objTrans.addChild(spinTg);

		return objTrans;
	}
  
  protected void removeShape(){
	  //root.removeChild(branchGroup);
	  try{
		  java.util.Iterator it = (Iterator) scene.getAllChildren();
		  int index = 0;
		  if(scene != null){
			  System.out.println("e: " + it);
			  BranchGroup brg = (BranchGroup)scene.getChild(index);
			  
		  }
		  while(it.hasNext() != false){
			  System.out.println("Removing");
			  //scene.removeChild(index);
			  scene.removeAllChildren();
			  index++;
			  System.out.println("index: " + index);
		  }
	  } catch(Exception e){
		  
	  }
	  System.out.println("removeShape");
  }
  
  public OperationWindows getOperationWindows(){
	  return this.opw;
  }


// public static void main(String[] args) {
//    SphereGUI applet = new SphereGUI(); // isStandalone = true;
//    applet.init();
//    //Frame frame = new MainFrame(applet, 800, 600);  // 800x600, 500x500
//    JFrame frame = new JFrame();
//    frame.add( applet.panel );
//    frame.setSize( 800, 600 );
//    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//    frame.setVisible( true );
//  }

	@Override
	public void afterInitialize()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterReproduce()
	{
		// TODO Auto-generated method stub
//		removeShape();
//	    scene = createSceneGraph();
//	    universe.addBranchGraph(scene);
//		System.out.println("afterReproduce");
//		w3d.removeAll();
//		w3d = new WrapSpheres3D(this, spherePros);
//		canvas3D = w3d.getCanvas3D();
//	    panel.add(canvas3D, BorderLayout.CENTER);
//		panel.validate();
//		panel.repaint();
		
//		topFrame = new JFrame("CACIE Sphere");
//		topFrame.setLayout(new BorderLayout());
//		topFrame.removeAll();
		//�X�[�p�[�N���X�ŏ������R���g���[���{�^���̃p�l����z�u
//		topFrame.add(controlButtons, BorderLayout.SOUTH);
		// Create Spheres

		//init();
		//new Dissolver().dissolveExit(topFrame);
//		for(int i = 0; i < 100000; i++){
//		w3d.getSpheres3D().doRotate();
//		}
		
		w3d = new WrapSpheres3D(this, spherePros);
		canvas3D = w3d.getCanvas3D();
		topFrame.remove(panel);
		panel = new JPanel();
	    panel.setLayout(new BorderLayout());
//		panel.removeAll();
	    panel.add(canvas3D, BorderLayout.CENTER);
	    panel.validate();
	    panel.repaint();
	    
	    
	    //topFrame.removeAll();
	    //topFrame.setVisible(false);
		//topFrame = new JFrame("CACIE Sphere");
//		topFrame.removeAll();
		//topFrame.setLayout(new BorderLayout());
	    
		//�X�[�p�[�N���X�ŏ������R���g���[���{�^���̃p�l����z�u
		//topFrame.add(controlButtons, BorderLayout.SOUTH);
		topFrame.add(panel, BorderLayout.CENTER);
		//topFrame.setSize(800, 800);
		//topFrame.setLocation(100, 100);
		topFrame.validate();
		w3d.getTimeDisappearBehavior().setEnable(false);
		topFrame.setVisible(true);

//		topFrame.setSize(1300, 600);
//		topFrame.setSize(800, 800);

		
		

	}

	@Override
	public void closingProcessingAnimation()
	{
		// TODO Auto-generated method stub

		
	}

	@Override
	public int getFitnessValue(int index)
	{
		// TODO Auto-generated method stub
  		//System.out.println("Index@getFitnessValue: " + index);
  		//System.out.println("Fitness@getFitnessValue: " + fitness[index]);
//		fitness = w3d.getFitness(index);
		return w3d.getFitness(index);
	}

	@Override
	public void openingProcessingAnimation()
	{
		// TODO Auto-generated method stub

		
	}

	@Override
	public void preInitialize()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preReproduce()
	{
		// TODO Auto-generated method stub
		w3d.getTimeRevolutionBehavior().setEnable(false);
		w3d.getTimeDisappearBehavior().setEnable(true);
		
		Transform3D t3d = new Transform3D();
		Transform3D cam3d = new Transform3D();	
		
		for(int i = 0; i < 10 *Math.exp(9); i++){
//			w3d.getSteerTG().getTransform(t3d);
//			cam3d.setIdentity();
//			cam3d.rotZ(Math.toRadians(0.0001));
//			cam3d.rotX(Math.toRadians(0.0001));
//			t3d.mul(cam3d);
//			w3d.getSteerTG().setTransform(t3d);	
		}
//		w3d.startRevolutionInterpolator();
//		w3d.getSpheres3D().doRotate();
//		new Dissolver().dissolveExit(topFrame);
//		for(int i = 0; i < 10000; i++){
//			w3d.getSpheres3D().revolutionUpdate();
//		//	w3d.getSpheres3D().update();
//		}
//		for(int i = 0; i < 10000; i++){
//			w3d.getSpheres3D().revolutionSlowUpdate();
//			w3d.getSpheres3D().update();
//			w3d.getSpheres3D().vanishChange();
//		}
//		for(int i = 0; i < 2000; i++){
//			w3d.getSpheres3D().vanishChangeEnd();
//		}
//		for(int i = 0; i < 20000; i++){
//		w3d.getSpheres3D().ShrinkUpdate();
//		}
		
	}

	@Override
	public void setEventList(int index, CommonEventList eventList)
	{
		// TODO Auto-generated method stub
		EvaluatingIndividual tmpInd = DistInds.get(index);
		if(eventList == null){
			System.err.println("SphereGUI:setEventList: given eventList is null.");
		}
		tmpInd.setEventList(eventList);

	}

	@Override
	public void setFitnessValue(int index, int position)
	{
		// TODO Auto-generated method stub
		fitness[index] = position;
		
	}
}


