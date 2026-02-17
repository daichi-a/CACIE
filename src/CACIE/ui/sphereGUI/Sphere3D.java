package CACIE.ui.sphereGUI;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import CACIE.RandomManager;

import com.sun.j3d.utils.geometry.Sphere;

public class Sphere3D {
	private static final double ROTATE_AMT = Math.toRadians(1); // 1 degrees
	private static final double SCALE_AMT = 0.1;
	// possible visual states for a cell
	private final static int INVISIBLE = 0;
	private final static int FADE_IN = 1;
	private final static int FADE_OUT = 2;
	private final static int VISIBLE = 3;

	// material colours
	private final static Color3f RED = new Color3f(1.0f, 0.0f, 0.0f);
	private final static Color3f RED1 = new Color3f(1.0f, 0.0f, 0.0f);
	private final static Color3f RED2 = new Color3f(1.0f, 0.0f, 0.2f);
	private final static Color3f RED3 = new Color3f(1.0f, 0.0f, 0.5f);
	private final static Color3f RED4 = new Color3f(1.0f, 0.0f, 0.7f);
	private final static Color3f RED5 = new Color3f(1.0f, 0.0f, 1.0f);
	private final static Color3f RED6 = new Color3f(1.0f, 0.2f, 0.5f);
	private final static Color3f RED7 = new Color3f(1.0f, 0.5f, 0.0f);
	private final static Color3f RED8 = new Color3f(1.0f, 1.0f, 0.0f);
	private final static Color3f RED9 = new Color3f(1.0f, 1.0f, 0.0f);
	private final static Color3f RED10 = new Color3f(0.7f, 1.0f, 0.0f);
	private final static Color3f RED11 = new Color3f(0.5f, 1.0f, 0.0f);
	private final static Color3f RED12 = new Color3f(0.0f, 1.0f, 0.0f);
	private final static Color3f RED13 = new Color3f(0.0f, 1.0f, 0.7f);
	private final static Color3f RED14 = new Color3f(0.0f, 1.0f, 0.5f);
	private final static Color3f RED15 = new Color3f(0.0f, 1.5f, 1.0f);
	private final static Color3f RED16 = new Color3f(0.0f, 0.0f, 1.0f);

	private final static Color3f MAGENTA1 = new Color3f(1.0f, 1.0f, 0.5f);
	private final static Color3f MAGENTA2 = new Color3f(1.0f, 0.0f, 1.0f);
	private final static Color3f ORANGE = new Color3f(1.0f, 0.5f, 0.0f);
	private final static Color3f YELLOW = new Color3f(1.0f, 1.0f, 0.0f);
	private final static Color3f GREEN = new Color3f(0.0f, 1.0f, 0.0f);
	private final static Color3f CYAN1 = new Color3f(0.0f, 1.0f, 0.5f);
	private final static Color3f CYAN2 = new Color3f(0.0f, 1.0f, 1.0f);
	private final static Color3f BLUE = new Color3f(0.0f, 0.0f, 1.0f);

	private final static Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
	private final static Color3f WHITE = new Color3f(1.0f, 1.0f, 1.0f);

	// length of cell side (== diameter when the cell is a ball)
	// private final static float CELL_LEN = 0.5f;
	private final static float SPHERE_LEN = 0.3f;
	private final static float SPHERES_RADIUS = 2.5f;
	private final static double OFFSET_THETA = (double) (Math.PI / 18.0);

	// space between cells : a factor multiplied to CELL_LEN
	private final static float SPACING = 1.5f;

	// scene graph elements
	private TransformGroup cellTG;
	private TransformGroup baseTG; // used to rotate the grid
	private TransformGroup scaleBaseTG;

	// reusable Transform3D object
	private Transform3D t3d = new Transform3D();
	private Transform3D rotT3d = new Transform3D();
	private Transform3D setScaleT3d = new Transform3D();
	private Transform3D bufSetScaleT3d = new Transform3D();
	private Transform3D scaleT3d = new Transform3D();
	private Transform3D bufScaleT3d = new Transform3D();
	private Transform3D translateT3d = new Transform3D();
	private Transform3D buftranslateT3d = new Transform3D();
	private Transform3D vibrationT3d = new Transform3D();
	private Transform3D bufvibrationT3d = new Transform3D();
	private Transform3D pulsationT3d = new Transform3D();
	private Transform3D bufPulsationT3d = new Transform3D();
	private Transform3D pullT3d = new Transform3D();
	private Transform3D bufPullT3d = new Transform3D();
	private Vector3d currV3d = new Vector3d();
	private Vector3d translation = new Vector3d();
	private Matrix3d Rz = new Matrix3d();
	private double 		turnAngle = ROTATE_AMT;
	private int turnAxis = 2; // y axis
	private int scale = 0;
	private int transCounter = 0;
	private int blinkTransCounter = 0;
	private int colorTransCounter = 0;
	private int scaleTransCounter = 0;
	private int pulsationTransCounter = 0;
	private int vibrationTransCounter = 0;
	private int pullTransCounter = 0;

	
	private double xPos;
	private double yPos;
	private double zPos;
	private double rPos;
	private double thetaPos;
	boolean fl = false;
	private boolean check = false;
	double delta_theta = 0;
	double delta_thetaCount = 0;
	// appearance elements
	private Appearance cellApp;
	// private Appearance cellApp = new Appearance();
	private TransparencyAttributes transAtt;
	private Material material;
	private Color3f cellCol, oldCol, newCol;

	// cell state information
	private boolean isAlive, newAliveState;
	private int visualState;
	private int blinkVisualState;
	private int colorVisualState;
	private int age = 0;
	private int fitness;
	
	private int tempRank = 0;
	private boolean changedFlag = false;

	private SphereProperties sphereProps;
	int num_sphere;
	SphereIndividual sindividual;
	SphereIndividual pickedSind;
	Spheres3D s3ds;

	// Transform3D currXform;
	// Vector3d currXV3d;

	public Sphere3D(Spheres3D s3, int index, SphereProperties sps)
	/*
	 * A cell is a shape below a TransformGroup which is used to position the
	 * cell in the cells grid. The scene branch is: cellTG --> cellShape -->
	 * Appearance {Material,Transparency}
	 */
	{
		this.s3ds = s3;
		sphereProps = sps;
		num_sphere = sphereProps.getNumSpheres();

		TransformGroup scaleBaseTG = new TransformGroup();
		scaleBaseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scaleBaseTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scaleBaseTG.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

		System.out.println("num_sphere: " + index);
		// isAlive = (RandomManager.getRandom()() < 0.1) ? true : false;
		isAlive = true;
		// it's more likely that a cell is initially dead (invisible)

		// create appearance
		cellApp = new Appearance();
		// currXform = new Transform3D();
		// currXV3d = new Vector3d();
		makeMaterial();
		setVisibility();

		// the cell shape as a cube
		// Box cellShape = new Box( CELL_LEN/2, CELL_LEN/2, CELL_LEN/2,
		// Box.GENERATE_NORMALS, cellApp);

		// the cell chape as a sphere
		Sphere cellShape = new Sphere(SPHERE_LEN / 2, Sphere.GENERATE_NORMALS,
				cellApp);

		// // fix cell's position
		// Transform3D t3d = new Transform3D();
		// double xPosn = index*CELL_LEN*SPACING;
		// double yPosn = index*CELL_LEN*SPACING;
		// double zPosn = index*CELL_LEN*SPACING;
		// t3d.setTranslation( new Vector3d(xPosn, yPosn, zPosn) );

		// fix cell's position
		double theta = (double) ((Math.PI / 180.0) * (360.0 / num_sphere));
		Transform3D t3d = new Transform3D();
		double xPosn = (double) (SPHERES_RADIUS * Math.cos(theta * index
				+ OFFSET_THETA));
		double yPosn = (double) (SPHERES_RADIUS * Math.sin(theta * index
				+ OFFSET_THETA));
		double zPosn = 0.0;
		t3d.setTranslation(new Vector3d(xPosn, yPosn, zPosn));

		// set userdata
		int iniFitnessValue = 50;
		sindividual = new SphereIndividual();
		sindividual.setFitness(iniFitnessValue);
		sindividual.setIndex(index);
		sindividual.setX(xPosn);
		sindividual.setY(yPosn);
		sindividual.setR(SPHERES_RADIUS);
		sindividual.setTheta(theta * index + OFFSET_THETA);
		sindividual.setSlope(Math.tan(theta * index + OFFSET_THETA));
		sindividual.setBlinkFlag(false);
		// cellShape.setUserData(sindividual);
		System.out.println("set User Data: fitness "
				+ sindividual.getTransformedFitness() + " x: "
				+ sindividual.getX() + "transformed x: "
				+ sindividual.getTransformedX() + " y: " + sindividual.getY()
				+ "transformed y: " + sindividual.getTransformedY() + "tan: "
				+ sindividual.getSlope());

		// build scene branch
		cellTG = new TransformGroup();
		cellTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		cellTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		cellTG.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
		cellTG.setCapability(TransformGroup.ALLOW_LOCAL_TO_VWORLD_READ);

		cellTG.setTransform(t3d);
		cellTG.setUserData(sindividual);

		// cellTG.getTransform(currXform);
		// currXform.get(currXV3d);
		// System.out.println("current x: " + currXV3d.x);
		// System.out.println("current y: " + currXV3d.y);
		// System.out.println("current z: " + currXV3d.z);
		cellTG.addChild(cellShape);

	} // end of Cell()

	private void makeMaterial()
	/*
	 * Make a coloured material, which is originally blue. The ambient and
	 * diffuse components of the material can be changed at run time.
	 */
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
	} // end of makeMaterial()

	private void resetColours()
	// intialization of the material's colour to blue
	{
		cellCol.set(BLUE);
		oldCol.set(cellCol); // blue as well
		newCol.set(cellCol);

		setMatColours(cellCol);
	} // end of resetColours()

	private void setMatColours(Color3f col)
	// the ambient colour is a darker shade of the diffuse colour
	{
		material.setAmbientColor(col.x / 3.0f, col.y / 3.0f, col.z / 3.0f);
		material.setDiffuseColor(col);
	} // end of setMatColours()

	private void setVisibility()
	/*
	 * A cell's transparency can change at run time, ranging from fully opaque
	 * when the cell is 'alive' to fully transparent when 'dead'. When the cell
	 * is coming to life or dieing, its transparency setting will be somewhere
	 * between these values.
	 */
	{
		// let transparency value change at run time
		transAtt = new TransparencyAttributes();
		transAtt.setTransparencyMode(TransparencyAttributes.BLENDED);
		transAtt.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

		if (isAlive) {
			visualState = VISIBLE;
			transAtt.setTransparency(0.0f); // opaque
		} else { // dead so invisible
			visualState = INVISIBLE;
			transAtt.setTransparency(1.0f); // totally transparent
		}

		cellApp.setTransparencyAttributes(transAtt);
	} // end of setVisibility()

	public TransformGroup getTG()
	// called by CellsGrid
	{
		return this.cellTG;
	}

	// ----------- get/set methods for cell's life ---------------------

	public boolean isAlive() {
		return isAlive;
	}

	public void newAliveState(boolean b) {
		newAliveState = b;
	}

	// -------------------------- state update --------------------------
	public void revolutionUpdate() {

	}

	public void blinkingUpdate() {
		// System.out.println("colorUpdate");
		// System.out.println("sphere: " + sindividual.getIndex() );
		// System.out.println("BlinkFlag: " + sindividual.getBlinkFlag() );
		boolean fl = s3ds.getWrapSpheres3D().getBlinkFlag(
				sindividual.getIndex());
		// System.out.println("now flag: " + fl);
		// if(sindividual.getBlinkFlag()){
		if (fl) {
			if (blinkTransCounter == 0) { // time for grid state change
				blinkUpdateState();
				// blinkTransCounter = rand.nextInt(3); // change rotation axis
				blinkTransCounter = 1;
			} else { // make a visual change
				blinkVisualChange(blinkTransCounter);
				blinkTransCounter++;
				if (blinkTransCounter > Spheres3D.MAX_TRANS - 3)
					blinkTransCounter = 0; // finished, so reset
			}
		}

		// doRotate(); // rotate in every update() call
	}

	public void disappearUpdate() {
		// System.out.println("colorUpdate");
		// System.out.println("sphere: " + sindividual.getIndex() );
		if (transCounter == 0) { // time for grid state change
			updateState();
			// turnAxis = rand.nextInt(3); // change rotation axis
			transCounter = 1;
		} else { // make a visual change
			visualChange(transCounter);
			transCounter++;
			if (transCounter > Spheres3D.MAX_TRANS)
				// transCounter = 0; // finished, so reset
				transCounter = 3;
		}

		// doRotate(); // rotate in every update() call
	}

	public void appearUpdate() {
		// System.out.println("colorUpdate");
		// System.out.println("sphere: " + sindividual.getIndex() );
		transAtt.setTransparency(1.0f);
		if (transCounter == 0) { // time for grid state change
			appearUpdateState();
			// turnAxis = rand.nextInt(3); // change rotation axis
			transCounter = 1;
		} else { // make a visual change
			appearVisualChange(transCounter);
			// /transCounter++;
			if (transCounter > Spheres3D.MAX_TRANS)
				transCounter = 1; // finished, so reset
		}

		// doRotate(); // rotate in every update() call
	}

	public void colorChangeUpdate() {
		boolean fl = s3ds.getWrapSpheres3D().getBlinkFlag(
				sindividual.getIndex());
		int fitness = s3ds.getWrapSpheres3D()
				.getFitness(sindividual.getIndex());
		int color_characteristics = s3ds.getWrapSpheres3D()
				.getColorCharacteristics(sindividual.getIndex());
		// int color_num = fitness / 10;
		// int scale_num = color_num;

		// System.out.println("now flag: " + fl);
		// System.out.println("now scale: " + scale_num);
		// System.out.println("now fitness: " + fitness);
		// System.out.println("now color_characteristics: " +
		// color_characteristics);
		// if(sindividual.getBlinkFlag()){
		// if(fl){
		if (colorTransCounter == 0 && scaleTransCounter == 0) { // time for grid
																// state change
			updateColorState(color_characteristics);
			// updateScaleState(scale_num);
			// turnAxis = rand.nextInt(3); // change rotation axis
			colorTransCounter = 1;
			scaleTransCounter = 1;
		} else { // make a visual change
			colorStaticChange(colorTransCounter);
			scaleVisualChange(scaleTransCounter);
			colorTransCounter++;
			scaleTransCounter++;
			if ((colorTransCounter > Spheres3D.MAX_TRANS && scaleTransCounter > Spheres3D.MAX_TRANS)) {
				colorTransCounter = 0; // finished, so reset
				scaleTransCounter = 0;
			}
			// }

		}
		// doScale();
	}
	
	public void pullSpheresUpdate() {
		double x0;
		double y0;
		double r0;
		double theta0;
		double theta_i;
		double theta1;
		double theta2;
		
		boolean quadrant_flag;
		int rank = 0;
//		if(delta_thetaCount <= 0){
//			delta_thetaCount = 0;
//		}
//		System.out.println("pullSpheresUpdate: " + sindividual.getIndex());
		fl = s3ds.getWrapSpheres3D().getBlinkFlag(
				sindividual.getIndex());
		s3ds.getWrapSpheres3D().checkFlag();
		check = s3ds.getWrapSpheres3D().getCheckFlag();
		rank = s3ds.getWrapSpheres3D().getRanking(sindividual.getIndex());
		if(rank != tempRank){
			changedFlag = true;
		}else{
			changedFlag = false;
		}
		
//		System.out.println("rank( " + sindividual.getIndex() + ") = " + rank);
		x0 = 0;
		y0 = 0;
		x0 = s3ds.getWrapSpheres3D().getPickedXpos();
		y0 = s3ds.getWrapSpheres3D().getPickedYpos();
		r0 = 0;
		r0 = s3ds.getWrapSpheres3D().getPickedRpos();
		theta0 = 0;
		theta0 = s3ds.getWrapSpheres3D().getPickedThetapos();
//		System.out.println("sphere0 (r, theta) = (" + r0 + ", " + Math.toDegrees(theta0) + ")");
		if(Math.toRadians(-360) <= theta0 && theta0 < Math.toRadians(0)){
			theta0 = Math.toRadians(360) + theta0;
		}else if(Math.toRadians(-360) > theta0){
			theta0 = Math.toRadians(720) + theta0;
		}else if(Math.toRadians(0) <= theta0 && theta0 <= Math.toRadians(360)){
			theta0 = theta0;
		}else if(Math.toRadians(360) <= theta0){
			theta0 = theta0 - Math.toRadians(360);
		}else{
			theta0 = theta0;
		}
		
//		System.out.println("sphere0 (r, theta) = (" + r0 + ", " + Math.toDegrees(theta0) + ")");
		cellTG.getTransform(bufPullT3d); 
		bufPullT3d.get(currV3d);	
		xPos = 0;
		xPos = currV3d.x;
		yPos = 0;
		yPos = currV3d.y;
		rPos = 0;
		rPos = Math.sqrt(xPos * xPos + yPos * yPos);
		thetaPos = 0;
		thetaPos = Math.atan2(yPos, xPos);
//		System.out.println("sphere (x, y) = (" + xPos + ", " + yPos + "), ( "
//				 + rPos * Math.cos(thetaPos) + " , " + rPos * Math.sin(thetaPos)
//				 + " )");
//		System.out.println("sphere( " + sindividual.getIndex() 
//				+ ") (r, theta) = (" + rPos + ", " + Math.toDegrees(thetaPos) + ")");
		if(Math.toRadians(-360) <= thetaPos && thetaPos < Math.toRadians(0)){
			thetaPos = Math.toRadians(360) + thetaPos;
		}else if(Math.toRadians(-360) > thetaPos){
			thetaPos = Math.toRadians(720) + thetaPos;
		}else if(Math.toRadians(0) <= thetaPos && thetaPos <= Math.toRadians(360)){
			thetaPos = thetaPos;
		}else if(Math.toRadians(360) <= thetaPos){
			thetaPos = thetaPos - Math.toRadians(360);
		}else{
			thetaPos = thetaPos;
		}
//		System.out.println("sphere( " + sindividual.getIndex() 
//				+ ") (r, theta) = (" + rPos + ", " + Math.toDegrees(thetaPos) + ")");
		
		theta_i = Math.toRadians(360.0 / this.num_sphere);
//		System.out.println("theta_i: " + Math.toDegrees(theta_i));
		theta1 = thetaPos - theta0;
//		System.out.println("theta1 = " + Math.toDegrees(theta_i) + " * " + sindividual.getIndex()
//				+ " = " +Math.toDegrees(thetaPos) + " - " +  Math.toDegrees(theta0) + " = " + Math.toDegrees(theta1));
		if(Math.toRadians(-360) <= theta1 && theta1 < Math.toRadians(0)){
			theta1 = Math.toRadians(360) + theta1;
		}else if(Math.toRadians(-360) > theta1){
			theta1 = Math.toRadians(720) + theta1;
		}else if(Math.toRadians(0) <= theta1 && theta1 <= Math.toRadians(360)){
			theta1 = theta1;
		}else if(Math.toRadians(360) <= theta1){
			theta1 = theta1 - Math.toRadians(360);
		}else{
			theta1 = theta1;
		}
//		System.out.println("theta1 = " + Math.toDegrees(theta_i) + " * " + sindividual.getIndex()
//				+ " = " +Math.toDegrees(thetaPos) + " - " +  Math.toDegrees(theta0) + " = " + Math.toDegrees(theta1));
//		System.out.println("theta1 converted: " + Math.toDegrees(theta1));
		theta2 = 0;
		if(rank == 1){
			theta2 = 0;
		} else {
			theta2 = theta_i / 2 * (rank - 1);
		}
		
		delta_theta = Math.toRadians(360) - theta1 + theta2;
//		System.out.println("delta_theta (" + sindividual.getIndex() +  ") = " 
//		+ theta_i / 2 + " * " + (rank - 1) + " = "+ Math.toDegrees(delta_theta));
		if(Math.toRadians(-360) <= delta_theta && delta_theta < Math.toRadians(0)){
			delta_theta = Math.toRadians(360) + delta_theta;
		}else if(Math.toRadians(-360) > delta_theta){
			delta_theta = Math.toRadians(720) + delta_theta;
		}else if(Math.toRadians(0) <= delta_theta && delta_theta <= Math.toRadians(360)){
			delta_theta = delta_theta;
		}else if(Math.toRadians(360) <= delta_theta){
			delta_theta = delta_theta - Math.toRadians(360);
		}else{
			delta_theta = delta_theta;
		}
//		System.out.println("delta_theta (" + sindividual.getIndex() +  ") = " 
//		+ Math.toDegrees((double)(theta_i / 2)) + " * " + (rank - 1) + " = "+ Math.toDegrees(delta_theta));


//		System.out.println("pullSpheresUpdate: " + sindividual.getIndex() + 
//				"check, flag :  " + check + "  " + fl);
//		pickedSind = s3ds.getWrapSpheres3D().getPickedIndividual();
//		pickedSind.CartesianUpdate();
//		System.out.println("pickedSind: " + pickedSind + ", xpos: " + 
//				 ", ypos: " + pickedSind.getY());
//		System.out.println("pullSpheresUpdate: " + sindividual.getIndex() + 
//		" (xpos, ypos) =  (" + s3ds.getWrapSpheres3D().getPickedXpos() + " ,  "
//		+ s3ds.getWrapSpheres3D().getPickedYpos() + " ) , ( " + 
//		s3ds.getWrapSpheres3D().getPickedRpos() * Math.cos(s3ds.getWrapSpheres3D().getPickedThetapos()) + ", " + 
//		s3ds.getWrapSpheres3D().getPickedRpos() * Math.sin(s3ds.getWrapSpheres3D().getPickedThetapos())
//		+ " )");

		if (/*(fl == false) &&*/ (check == true) && (changedFlag == true)) {
			double random_num = 0;
			int rand = 0;
			random_num = RandomManager.getRandom() * 2;
			rand = (int)random_num;
//			System.out.println("random_num: " + (int)random_num);
//			this.doPull(delta_theta);
			if(rand == 1){
				quadrant_flag = true;
			}else {
				quadrant_flag = false;
			}
//			System.out.println("quadrant_flag: " + quadrant_flag);
			if(quadrant_flag == true){
//				delta_thetaCount = Math.toDegrees(delta_theta);
				delta_thetaCount = Math.toDegrees(delta_theta) - 2 * Math.toDegrees(theta2);
			}else {
				delta_thetaCount = Math.toDegrees(delta_theta);
			}
//			delta_thetaCount = Math.toDegrees(delta_theta);
//			while(delta_thetaCount > 0){
//				this.doPull(delta_theta);
//				delta_thetaCount = delta_thetaCount - Math.toRadians(1.0);
//			}
		}
		if((fl == false) && delta_thetaCount > 0 && (check == true)){
		this.doPull(delta_theta);
		}
		delta_thetaCount = delta_thetaCount - 5.0;
		tempRank = rank;

	}
	
//	public void pullSpheresUpdateTest1() {
//		double x0;
//		double y0;
//		double r0;
//		double theta0;
//		double theta_i;
//		double theta1;
//		double theta2;
//		double delta_theta = 0;
//		int rank = 0;
////		System.out.println("pullSpheresUpdate: " + sindividual.getIndex());
//		fl = s3ds.getWrapSpheres3D().getBlinkFlag(
//				sindividual.getIndex());
//		s3ds.getWrapSpheres3D().checkFlag();
//		check = s3ds.getWrapSpheres3D().getCheckFlag();
//		rank = s3ds.getWrapSpheres3D().getRanking(sindividual.getIndex());
//		if(rank != tempRank){
//			changedFlag = true;
//		}else{
//			changedFlag = false;
//		}
//		
////		System.out.println("rank( " + sindividual.getIndex() + ") = " + rank);
//		x0 = 0;
//		y0 = 0;
//		x0 = s3ds.getWrapSpheres3D().getPickedXpos();
//		y0 = s3ds.getWrapSpheres3D().getPickedYpos();
//		r0 = 0;
//		r0 = s3ds.getWrapSpheres3D().getPickedRpos();
//		theta0 = 0;
//		theta0 = s3ds.getWrapSpheres3D().getPickedThetapos();
////		System.out.println("sphere0 (r, theta) = (" + r0 + ", " + Math.toDegrees(theta0) + ")");
//		if(Math.toRadians(-360) <= theta0 && theta0 < Math.toRadians(0)){
//			theta0 = Math.toRadians(360) + theta0;
//		}else if(Math.toRadians(-360) > theta0){
//			theta0 = Math.toRadians(720) + theta0;
//		}else if(Math.toRadians(0) <= theta0 && theta0 <= Math.toRadians(360)){
//			theta0 = theta0;
//		}else if(Math.toRadians(360) <= theta0){
//			theta0 = theta0 - Math.toRadians(360);
//		}else{
//			theta0 = theta0;
//		}
//		
////		System.out.println("sphere0 (r, theta) = (" + r0 + ", " + Math.toDegrees(theta0) + ")");
//		
//		xPos = 0;
//		xPos = sindividual.getX();
//		yPos = 0;
//		yPos = sindividual.getY();
//		rPos = 0;
//		rPos = sindividual.getTransformedR();
//		thetaPos = 0;
//		thetaPos = sindividual.getTransformedTheta();
////		System.out.println("sphere (x, y) = (" + xPos + ", " + yPos + "), ( "
////				 + rPos * Math.cos(thetaPos) + " , " + rPos * Math.sin(thetaPos)
////				 + " )");
////		System.out.println("sphere( " + sindividual.getIndex() 
////				+ ") (r, theta) = (" + rPos + ", " + Math.toDegrees(thetaPos) + ")");
//		if(Math.toRadians(-360) <= thetaPos && thetaPos < Math.toRadians(0)){
//			thetaPos = Math.toRadians(360) + thetaPos;
//		}else if(Math.toRadians(-360) > thetaPos){
//			thetaPos = Math.toRadians(720) + thetaPos;
//		}else if(Math.toRadians(0) <= thetaPos && thetaPos <= Math.toRadians(360)){
//			thetaPos = thetaPos;
//		}else if(Math.toRadians(360) <= thetaPos){
//			thetaPos = thetaPos - Math.toRadians(360);
//		}else{
//			thetaPos = thetaPos;
//		}
////		System.out.println("sphere( " + sindividual.getIndex() 
////				+ ") (r, theta) = (" + rPos + ", " + Math.toDegrees(thetaPos) + ")");
//		
//		theta_i = Math.toRadians(360.0 / this.num_sphere);
////		System.out.println("theta_i: " + Math.toDegrees(theta_i));
//		theta1 = thetaPos - theta0;
////		System.out.println("theta1 = " + Math.toDegrees(theta_i) + " * " + sindividual.getIndex()
////				+ " = " +Math.toDegrees(thetaPos) + " - " +  Math.toDegrees(theta0) + " = " + Math.toDegrees(theta1));
//		if(Math.toRadians(-360) <= theta1 && theta1 < Math.toRadians(0)){
//			theta1 = Math.toRadians(360) + theta1;
//		}else if(Math.toRadians(-360) > theta1){
//			theta1 = Math.toRadians(720) + theta1;
//		}else if(Math.toRadians(0) <= theta1 && theta1 <= Math.toRadians(360)){
//			theta1 = theta1;
//		}else if(Math.toRadians(360) <= theta1){
//			theta1 = theta1 - Math.toRadians(360);
//		}else{
//			theta1 = theta1;
//		}
////		System.out.println("theta1 = " + Math.toDegrees(theta_i) + " * " + sindividual.getIndex()
////				+ " = " +Math.toDegrees(thetaPos) + " - " +  Math.toDegrees(theta0) + " = " + Math.toDegrees(theta1));
////		System.out.println("theta1 converted: " + Math.toDegrees(theta1));
//		theta2 = 0;
//		if(rank == 1){
//			theta2 = 0;
//		} else {
//			theta2 = theta_i / 2 * (rank - 1);
//		}
//		
//		delta_theta = Math.toRadians(360) - theta1 + theta2;
////		System.out.println("delta_theta (" + sindividual.getIndex() +  ") = " 
////		+ theta_i / 2 + " * " + (rank - 1) + " = "+ Math.toDegrees(delta_theta));
//		if(Math.toRadians(-360) <= delta_theta && delta_theta < Math.toRadians(0)){
//			delta_theta = Math.toRadians(360) + delta_theta;
//		}else if(Math.toRadians(-360) > delta_theta){
//			delta_theta = Math.toRadians(720) + delta_theta;
//		}else if(Math.toRadians(0) <= delta_theta && delta_theta <= Math.toRadians(360)){
//			delta_theta = delta_theta;
//		}else if(Math.toRadians(360) <= delta_theta){
//			delta_theta = delta_theta - Math.toRadians(360);
//		}else{
//			delta_theta = delta_theta;
//		}
////		System.out.println("delta_theta (" + sindividual.getIndex() +  ") = " 
////		+ Math.toDegrees((double)(theta_i / 2)) + " * " + (rank - 1) + " = "+ Math.toDegrees(delta_theta));
//
//
////		System.out.println("pullSpheresUpdate: " + sindividual.getIndex() + 
////				"check, flag :  " + check + "  " + fl);
////		pickedSind = s3ds.getWrapSpheres3D().getPickedIndividual();
////		pickedSind.CartesianUpdate();
////		System.out.println("pickedSind: " + pickedSind + ", xpos: " + 
////				 ", ypos: " + pickedSind.getY());
////		System.out.println("pullSpheresUpdate: " + sindividual.getIndex() + 
////		" (xpos, ypos) =  (" + s3ds.getWrapSpheres3D().getPickedXpos() + " ,  "
////		+ s3ds.getWrapSpheres3D().getPickedYpos() + " ) , ( " + 
////		s3ds.getWrapSpheres3D().getPickedRpos() * Math.cos(s3ds.getWrapSpheres3D().getPickedThetapos()) + ", " + 
////		s3ds.getWrapSpheres3D().getPickedRpos() * Math.sin(s3ds.getWrapSpheres3D().getPickedThetapos())
////		+ " )");
//
//
//		if (/*(fl == false) &&*/ (check == true) && (changedFlag == true)) {
//
//			this.doPull(delta_theta);
//		}
//		tempRank = rank;
//	}

//	public void pullSpheresUpdateTest() {
////		System.out.println("pullSpheresUpdate: " + sindividual.getIndex());
//		fl = s3ds.getWrapSpheres3D().getBlinkFlag(
//				sindividual.getIndex());
//		s3ds.getWrapSpheres3D().checkFlag();
//		check = s3ds.getWrapSpheres3D().getCheckFlag();
////		System.out.println("pullSpheresUpdate: " + sindividual.getIndex() + 
////				"check, flag :  " + check + "  " + fl);
////		pickedSind = s3ds.getWrapSpheres3D().getPickedIndividual();
////		pickedSind.CartesianUpdate();
////		System.out.println("pickedSind: " + pickedSind + ", xpos: " + 
////				 ", ypos: " + pickedSind.getY());
//		System.out.println("pullSpheresUpdate: " + sindividual.getIndex() + 
//		" (xpos, ypos) =  " + s3ds.getWrapSpheres3D().getPickedXpos() + " ,  "
//		+ s3ds.getWrapSpheres3D().getPickedYpos());
//		int rank = s3ds.getWrapSpheres3D().getColorCharacteristics(sindividual.getIndex());
//		double x0 = 0;
//		double y0 = 0;
//		x0 = s3ds.getWrapSpheres3D().getPickedXpos();
//		y0 = s3ds.getWrapSpheres3D().getPickedYpos();
//		double r0 = 0;
//		r0 = s3ds.getWrapSpheres3D().getPickedRpos();
//		double theta0 = 0;
//		theta0 = s3ds.getWrapSpheres3D().getPickedThetapos();
//		
//		xPos = 0;
//		xPos = sindividual.getX();
//		yPos = 0;
//		yPos = sindividual.getY();
//		rPos = 0;
//		rPos = sindividual.getTransformedR();
//		thetaPos = 0;
//		thetaPos = sindividual.getTransformedTheta();
//		System.out.println("sphere (x, y) = (" + xPos + ", " + yPos + ")");
//		
//		double theta_i = Math.toRadians(360.0 / this.num_sphere);
//		double theta1 = thetaPos - theta0;
//		double theta2 = 0;
//		if ((fl == false) && (check == true)) {
//		
//		if(rank == 1){
//			theta2 = theta0;
//		} else if(rank % 2 == 0){
//			theta2 = theta0 - theta_i * Math.floor(rank / 2);
//		} else if (rank % 2 == 1){
//			theta2 = theta0 + theta_i * Math.floor(rank / 2);
//		}
//		
//
//		
//
////			if(((0 < thetaPos) && (thetaPos < theta2 - Math.toRadians(10))) || 
////					((theta2 + Math.toRadians(10) < thetaPos) &&
////					(thetaPos < Math.toRadians(360)))){
////				if(theta1 <= Math.PI){
////					this.doPull(0);
////				}
////				if(theta1 > Math.PI){
////					this.doPull(1);
////				}
////			}
//		}
//	}
	public void vibrationUpdate() {
		// System.out.println("colorUpdate");
		// System.out.println("sphere: " + sindividual.getIndex() );
		// System.out.println("BlinkFlag: " + sindividual.getBlinkFlag() );
		boolean fl = s3ds.getWrapSpheres3D().getBlinkFlag(
				sindividual.getIndex());
		// System.out.println("now flag: " + fl);
		// if(sindividual.getBlinkFlag()){
		if (true) {
			if (vibrationTransCounter == 0) { // time for grid state change
				vibrationTransCounter = 1;
			} else { // make a visual change
				vibrationVisualChange(vibrationTransCounter);
				vibrationTransCounter++;
				if (vibrationTransCounter > 8)
					vibrationTransCounter = 0; // finished, so reset
			}
		}

		// doRotate(); // rotate in every update() call
	}
	
	public void pulsationUpdate() {
		// System.out.println("colorUpdate");
		// System.out.println("sphere: " + sindividual.getIndex() );
		// System.out.println("BlinkFlag: " + sindividual.getBlinkFlag() );
		boolean fl = s3ds.getWrapSpheres3D().getBlinkFlag(
				sindividual.getIndex());
		// System.out.println("now flag: " + fl);
		// if(sindividual.getBlinkFlag()){
		if (fl) {
			if (pulsationTransCounter == 0) { // time for grid state change
				pulsationTransCounter = 1;
			} else { // make a visual change
				pulsationVisualChange(pulsationTransCounter);
				pulsationTransCounter++;
				if (pulsationTransCounter > Spheres3D.MAX_TRANS - 3)
					pulsationTransCounter = 0; // finished, so reset
			}
		}

		// doRotate(); // rotate in every update() call
	}

	private void setTurnAngle(int rate)
	/*
	 * A faster speed property is converted into a larger rotation angle, which
	 * makes the grid turn faster at tun time.
	 */
	{
		int speed = sphereProps.getSpeed();

		// if (speed == SphereProperties.SLOW)
		// turnAngle = ROTATE_AMT/4;
		// else if (speed == SphereProperties.MEDIUM)
		// turnAngle = ROTATE_AMT/2;
		// else // fast --> large rotation
		// turnAngle = ROTATE_AMT;

		if (speed == SphereProperties.SLOW)
			turnAngle = ROTATE_AMT / (4 * 10)* rate;
		else if (speed == SphereProperties.MEDIUM)
			turnAngle = ROTATE_AMT / (2 * 10)* rate;
		else
			// fast --> large rotation
			turnAngle = ROTATE_AMT * rate;
	} // end of setTurnAngle()
	
	
	public void doPull(double delta)
	// rotate the object turnAngle radians around an axis
	{
		//mode:0 clockwise, 1:counterclockwise
		double update_theata;
		double xpos;
		double xpos1;
		double dx;
		double ypos;
		double ypos1;
		double zpos;
		double dy;
		double r;
		double theta;
		double d_theta1;
		double d_theta2;
		double update_xpos;
		double update_ypos;
		double slope;
		double tangentialSlope;
		
		
		d_theta1 = delta;
//		d_theta2 = d_theta1 + Math.toRadians(RandomManager.getRandom()() * 10.0);
		d_theta2 = Math.toRadians(5.0 + RandomManager.getRandom() * 0.1);
//		System.out.println("Delta Theta = " + Math.toDegrees(d_theta));
		cellTG.getTransform(bufPullT3d); 
		bufPullT3d.get(currV3d);
		xpos = currV3d.x;
		ypos = currV3d.y;
		zpos = currV3d.z;
		r = Math.sqrt(xpos * xpos + ypos * ypos);
		theta = Math.atan2(ypos, xpos);	
		if(Math.toRadians(-360) <= theta && theta < Math.toRadians(0)){
			theta = Math.toRadians(360) + theta;
		}else if(Math.toRadians(-360) > theta){
			theta = Math.toRadians(720) + theta;
		}else if(Math.toRadians(0) <= theta && theta <= Math.toRadians(360)){
			theta = theta;
		}else if(Math.toRadians(360) <= theta){
			theta = theta - Math.toRadians(360);
		}else{
			theta = theta;
		}
//		System.out.println("Sphere ( " + sindividual.getIndex() +
//				" ), (x, y) = ( " + xpos + ", " + ypos + " ), ( "
//				+ r * Math.cos(theta) + ", " + r * Math.sin(theta) + " )");
//		System.out.println("(r, theta) = ( " + r + ", " + Math.toDegrees(theta) + " )");
		Rz.rotZ(d_theta2);
		Rz.transform(currV3d);
		
		xpos = currV3d.x;
		ypos = currV3d.y;
		zpos = currV3d.z;
//		r = Math.sqrt(xpos * xpos + ypos * ypos);
//		theta = Math.atan2(ypos, xpos);
//		if(Math.toRadians(-360) <= theta && theta < Math.toRadians(0)){
//			theta = Math.toRadians(360) + theta;
//		}else if(Math.toRadians(-360) > theta){
//			theta = Math.toRadians(720) + theta;
//		}else if(Math.toRadians(0) <= theta && theta <= Math.toRadians(360)){
//			theta = theta;
//		}else if(Math.toRadians(360) <= theta){
//			theta = theta - Math.toRadians(360);
//		}else{
//			theta = theta;
//		}
//		System.out.println("Translated: ");
//		System.out.println("(x, y) = ( " + xpos + ", " + ypos + " ), ( "
//				+ r * Math.cos(theta) + ", " + r * Math.sin(theta) + " )");
//		System.out.println("(r, theta) = ( " + r + ", " + Math.toDegrees(theta) + " )");
		
//		translation.x = dx;
//		translation.y = dy;
//		translation.z = 0;
//		
//		pullT3d.set(translation);
//		bufPullT3d.mul(pullT3d);
		
		bufPullT3d.set(currV3d);
		
//		update_xpos = r * Math.cos(update_theata);
//		update_ypos = r * Math.sin(update_theata);
		
//		pullT3d.setIdentity(); // reset the rotation transform object
//		pullT3d.setTranslation(currV3d);
//		bufPullT3d.mul(pullT3d); // 'add' new rotation to current one
		 cellTG.setTransform(bufPullT3d); // update the TG
	} // end of doRotate()
	
//	public void doPullTest1(double delta)
//	// rotate the object turnAngle radians around an axis
//	{
//		//mode:0 clockwise, 1:counterclockwise
//		double update_theata;
//		double xpos;
//		double xpos1;
//		double dx;
//		double ypos;
//		double ypos1;
//		double zpos;
//		double dy;
//		double r;
//		double theta;
//		double d_theta1;
//		double d_theta2;
//		double update_xpos;
//		double update_ypos;
//		double slope;
//		double tangentialSlope;
//		
//		
//		d_theta1 = delta;
//		d_theta2 = d_theta1 + Math.toRadians(RandomManager.getRandom()() * 10.0);
////		System.out.println("Delta Theta = " + Math.toDegrees(d_theta));
//		cellTG.getTransform(bufPullT3d); 
//		bufPullT3d.get(currV3d);
//		xpos = currV3d.x;
//		ypos = currV3d.y;
//		zpos = currV3d.z;
//		r = Math.sqrt(xpos * xpos + ypos * ypos);
//		theta = Math.atan2(ypos, xpos);	
//		if(Math.toRadians(-360) <= theta && theta < Math.toRadians(0)){
//			theta = Math.toRadians(360) + theta;
//		}else if(Math.toRadians(-360) > theta){
//			theta = Math.toRadians(720) + theta;
//		}else if(Math.toRadians(0) <= theta && theta <= Math.toRadians(360)){
//			theta = theta;
//		}else if(Math.toRadians(360) <= theta){
//			theta = theta - Math.toRadians(360);
//		}else{
//			theta = theta;
//		}
////		System.out.println("Sphere ( " + sindividual.getIndex() +
////				" ), (x, y) = ( " + xpos + ", " + ypos + " ), ( "
////				+ r * Math.cos(theta) + ", " + r * Math.sin(theta) + " )");
//		System.out.println("(r, theta) = ( " + r + ", " + Math.toDegrees(theta) + " )");
//		Rz.rotZ(d_theta2);
//		Rz.transform(currV3d);
//		
//		xpos = currV3d.x;
//		ypos = currV3d.y;
//		zpos = currV3d.z;
//		r = Math.sqrt(xpos * xpos + ypos * ypos);
//		theta = Math.atan2(ypos, xpos);
//		if(Math.toRadians(-360) <= theta && theta < Math.toRadians(0)){
//			theta = Math.toRadians(360) + theta;
//		}else if(Math.toRadians(-360) > theta){
//			theta = Math.toRadians(720) + theta;
//		}else if(Math.toRadians(0) <= theta && theta <= Math.toRadians(360)){
//			theta = theta;
//		}else if(Math.toRadians(360) <= theta){
//			theta = theta - Math.toRadians(360);
//		}else{
//			theta = theta;
//		}
////		System.out.println("Translated: ");
////		System.out.println("(x, y) = ( " + xpos + ", " + ypos + " ), ( "
////				+ r * Math.cos(theta) + ", " + r * Math.sin(theta) + " )");
////		System.out.println("(r, theta) = ( " + r + ", " + Math.toDegrees(theta) + " )");
//		
////		translation.x = dx;
////		translation.y = dy;
////		translation.z = 0;
////		
////		pullT3d.set(translation);
////		bufPullT3d.mul(pullT3d);
//		
//		bufPullT3d.set(currV3d);
//		
////		update_xpos = r * Math.cos(update_theata);
////		update_ypos = r * Math.sin(update_theata);
//		
////		pullT3d.setIdentity(); // reset the rotation transform object
////		pullT3d.setTranslation(currV3d);
////		bufPullT3d.mul(pullT3d); // 'add' new rotation to current one
//		 cellTG.setTransform(bufPullT3d); // update the TG
//	} // end of doRotate()
	
//	public void doPullTest(int mode)
//	// rotate the object turnAngle radians around an axis
//	{
//		//mode:0 clockwise, 1:counterclockwise
//		double update_theata;
//		double xpos;
//		double xpos1;
//		double ypos;
//		double ypos1;
//		double zpos;
//		double dx;
//		double r;
//		double theta;
//		double update_xpos;
//		double update_ypos;
//		double slope;
//		double tangentialSlope;
//		
//		cellTG.getTransform(bufPullT3d); // get current rotation
//		bufPullT3d.get(currV3d);
//		xpos = currV3d.x;
//		ypos = currV3d.y;
//		zpos = currV3d.z;
//		r = Math.sqrt(xpos * xpos + ypos * ypos);
//		theta = Math.atan2(ypos, xpos);
//		
////		xpos = xPos;
////		ypos = yPos;
//		slope = ypos / xpos;
//		tangentialSlope = - 1 / slope;
//		dx = 0.5;
//		
//		r = rPos;
//		theta = thetaPos;
//		
//		if(mode == 0 && ypos >= 0){
//			xpos1 = dx;
//			ypos1 = tangentialSlope * (xpos1 - xpos) + ypos;
//			
//			update_theata = theta + Math.toRadians(0.001);
//		}else if (mode == 0 && ypos <= 0 ){
//			xpos1 = dx;
//			ypos1 = tangentialSlope * (xpos1 - xpos) + ypos;
//			update_theata = theta - Math.toRadians(0.001);
//		}else if (mode == 1 && ypos >= 0){
//			xpos1 = dx;
//			ypos1 = tangentialSlope * (xpos1 - xpos) + ypos;
//		}else{
//			xpos1 = dx;
//			ypos1 = tangentialSlope * (xpos1 - xpos) + ypos;
//		}
//		
//		translation.x = xpos1;
//		translation.y = ypos1;
//		translation.z = 0;
//		
//		pullT3d.set(translation);
//		bufPullT3d.mul(bufPullT3d, pullT3d);
//		
////		update_xpos = r * Math.cos(update_theata);
////		update_ypos = r * Math.sin(update_theata);
//		
////		pullT3d.setIdentity(); // reset the rotation transform object
////		pullT3d.setTranslation(currV3d);
////		bufPullT3d.mul(pullT3d); // 'add' new rotation to current one
//		 cellTG.setTransform(bufPullT3d); // update the TG
//	} // end of doRotate()
	
	public void doTranslate()
	// rotate the object turnAngle radians around an axis
	{
		cellTG.getTransform(buftranslateT3d); // get current rotation
		translateT3d.setIdentity(); // reset the rotation transform object
		translateT3d.setTranslation(new Vector3d(SPHERES_RADIUS, SPHERES_RADIUS, 0));

//		 System.out.println("doScale");

		 buftranslateT3d.mul(translateT3d); // 'add' new rotation to current one
		 cellTG.setTransform(buftranslateT3d); // update the TG
	} // end of doRotate()
	
	public void setScalingDirection(int direction){
		this.scale = direction; //0: outwards, 1: inwards
	}
	
	public void setScale()
	// rotate the object turnAngle radians around an axis
	{
		cellTG.getTransform(bufSetScaleT3d); 
		bufSetScaleT3d.get(currV3d);	
		double xpos = 0;
		xpos = currV3d.x;
		double ypos = 0;
		ypos = currV3d.y;
		double rpos = 0;
		rpos = Math.sqrt(xPos * xPos + yPos * yPos);
		double f = rPos / (double) (4.5) * 100.0;
		int fitness = (int) f;
		if (fitness >= 100) {
			fitness = 100;
		}
		if (fitness <= 0) {
			fitness = 0;
		}
		fitness = 100 - fitness;
//		s3ds.getWrapSpheres3D().setFitness(sindividual.getIndex(), fitness);
		double fit_scale = 0.0;
		fit_scale = (double)fitness / 100.0 * 0.7 + 0.5;
//		scaleT3d.setIdentity(); // reset the rotation transform object
		if(fit_scale <= 0.5){
			fit_scale = 0.5;
		}
		
		if(fit_scale >= 1.2){
			fit_scale = 1.2;
		}
		
//		scaleT3d.setScale(fit_scale);
//		bufScaleT3d.mul(scaleT3d);
//		cellTG.setTransform(bufScaleT3d); // update the TG

		bufSetScaleT3d.setScale(fit_scale);
		cellTG.setTransform(bufSetScaleT3d); // update the TG

//		switch (scale) { // set the rotation based on the current axis
//		case 0:
//			scaleT3d.setScale(- this.SCALE_AMT);
//			break;
//		case 1:
//			scaleT3d.setScale(this.SCALE_AMT);
//			break;
//		default:
//			System.out.println("Unknown axis of Scaling");
//			break;
//		}
////		 System.out.println("doScale");
//
//		bufScaleT3d.mul(scaleT3d); // 'add' new rotation to current one
//		cellTG.setTransform(bufScaleT3d); // update the TG
	} // end of doRotate()

	public void doScale(double fit_scale)
	// rotate the object turnAngle radians around an axis
	{
		cellTG.getTransform(bufScaleT3d); // get current rotation
		scaleT3d.setIdentity(); // reset the rotation transform object
		
		if(fit_scale <= 0.5){
			fit_scale = 0.5;
		}
		
		if(fit_scale >= 1.2){
			fit_scale = 1.2;
		}
		
		scaleT3d.setScale(fit_scale);
		bufScaleT3d.mul(scaleT3d);
		cellTG.setTransform(bufScaleT3d); // update the TG

//		bufScaleT3d.setScale(fit_scale);
//		cellTG.setTransform(bufScaleT3d); // update the TG

//		switch (scale) { // set the rotation based on the current axis
//		case 0:
//			scaleT3d.setScale(- this.SCALE_AMT);
//			break;
//		case 1:
//			scaleT3d.setScale(this.SCALE_AMT);
//			break;
//		default:
//			System.out.println("Unknown axis of Scaling");
//			break;
//		}
////		 System.out.println("doScale");
//
//		bufScaleT3d.mul(scaleT3d); // 'add' new rotation to current one
//		cellTG.setTransform(bufScaleT3d); // update the TG
	} // end of doRotate()

	public void doRotate()
	// rotate the object turnAngle radians around an axis
	{
//		turnAxis = axis;
		cellTG.getTransform(t3d); // get current rotation
		rotT3d.setIdentity(); // reset the rotation transform object

		switch (turnAxis) { // set the rotation based on the current axis
		case 0:
			rotT3d.rotX(turnAngle);
			break;
		case 1:
			rotT3d.rotY(turnAngle);
			break;
		case 2:
			rotT3d.rotZ(turnAngle);
			break;
		default:
			System.out.println("Unknown axis of rotation");
			break;
		}
//		 System.out.println("doRoate");

		t3d.mul(rotT3d); // 'add' new rotation to current one
		cellTG.setTransform(t3d); // update the TG
	} // end of doRotate()

	public void updateState()
	/*
	 * If the cell is coming alive or dieing then its visual state must be
	 * altered, so it will fade into/out of view.
	 * 
	 * If the cell's life state isn't changing, and it's alive, then it's colour
	 * may change if it's old enough.
	 */
	{
		if (isAlive != newAliveState) { // there's a state change
			if (isAlive && !newAliveState) // alive --> dead (die)
				visualState = FADE_OUT; // from VISIBLE
			else { // dead --> alive (birth)
				visualState = FADE_IN; // from INVISIBLE
				age = 0; // reset age since born again
				resetColours();
			}
		} else { // current and new states are the same
			if (isAlive) { // cell stays alive (survives)
				age++; // get older
				ageSetColour();
			}
		}

		visualState = FADE_OUT; // from VISIBLE

		age++; // get older
		ageSetColour();

	} // end of updateState()

	public void blinkUpdateState()
	/*
	 * If the cell is coming alive or dieing then its visual state must be
	 * altered, so it will fade into/out of view.
	 * 
	 * If the cell's life state isn't changing, and it's alive, then it's colour
	 * may change if it's old enough.
	 */
	{
		// if (isAlive != newAliveState) { // there's a state change
		// if (isAlive && !newAliveState) // alive --> dead (die)
		// visualState = FADE_OUT; // from VISIBLE
		// else { // dead --> alive (birth)
		// visualState = FADE_IN; // from INVISIBLE
		// age = 0; // reset age since born again
		// resetColours();
		// }
		// }
		// else { // current and new states are the same
		// if (isAlive) { // cell stays alive (survives)
		// age++; // get older
		// ageSetColour();
		// }
		// }

		blinkVisualState = FADE_OUT; // from VISIBLE

		// age++; // get older
		// ageSetColour();

	} // end of updateState()

	public void appearUpdateState()
	/*
	 * If the cell is coming alive or dieing then its visual state must be
	 * altered, so it will fade into/out of view.
	 * 
	 * If the cell's life state isn't changing, and it's alive, then it's colour
	 * may change if it's old enough.
	 */
	{
		if (isAlive != newAliveState) { // there's a state change
			if (isAlive && !newAliveState) // alive --> dead (die)
				visualState = FADE_OUT; // from VISIBLE
			else { // dead --> alive (birth)
				visualState = FADE_IN; // from INVISIBLE
				age = 0; // reset age since born again
				// resetColours();
			}
		} else { // current and new states are the same
			if (isAlive) { // cell stays alive (survives)
				age++; // get older
				ageSetColour();
			}
		}
	} // end of updateState()

	public void updateColorState(int color_num)
	/*
	 * If the cell is coming alive or dieing then its visual state must be
	 * altered, so it will fade into/out of view.
	 * 
	 * If the cell's life state isn't changing, and it's alive, then it's colour
	 * may change if it's old enough.
	 */
	{
		// if (isAlive != newAliveState) { // there's a state change
		// if (isAlive && !newAliveState) // alive --> dead (die)
		// visualState = FADE_OUT; // from VISIBLE
		// else { // dead --> alive (birth)
		// visualState = FADE_IN; // from INVISIBLE
		// age = 0; // reset age since born again
		// resetColours();
		// }
		// }
		// else { // current and new states are the same
		// if (isAlive) { // cell stays alive (survives)
		// age++; // get older
		// ageSetColour();
		// }
		// }
		colorVisualState = FADE_OUT; // from VISIBLE
		age = color_num;
		// age = 9;//RED
		// age = 0;//BLUE
		// age = 5;//ORANGE
		// age++; // get older
		ageSetColour();
	} // end of updateState()

	private void ageSetColour()
	// hardwired age values for setting the cell's new colour
	{
		// if (age > 16)
		// newCol.set(RED);
		// else if (age > 8)
		// newCol.set(ORANGE);
		// else if (age > 4)
		// newCol.set(YELLOW);
		// else if (age > 2)
		// newCol.set(GREEN);
		// else
		// newCol.set(BLUE);
		//    
		// if(age > 17){
		// resetColours();
		// age = 0;
		// }
		switch (age) {
		case 1:
			newCol.set(RED1);
			break;
		case 2:
			newCol.set(RED2);
			break;
		case 3:
			newCol.set(RED3);
			break;
		case 4:
			newCol.set(RED4);
			break;
		case 5:
			newCol.set(RED5);
			break;
		case 6:
			newCol.set(RED6);
			break;
		case 7:
			newCol.set(RED7);
			break;
		case 8:
			newCol.set(RED8);
			break;
		case 9:
			newCol.set(RED9);
			break;
		case 10:
			newCol.set(RED10);
			break;
		case 11:
			newCol.set(RED11);
			break;
		case 12:
			newCol.set(RED12);
			break;
		case 13:
			newCol.set(RED13);
			break;
		case 14:
			newCol.set(RED14);
			break;
		case 15:
			newCol.set(RED15);
			break;
		case 16:
			newCol.set(RED16);
			break;
		default:
			// System.out.println("Unrecognized Number");

		}

	} // end of ageSetColour()

	public void updateScaleState(int scale_num)
	/*
	 * If the cell is coming alive or dieing then its visual state must be
	 * altered, so it will fade into/out of view.
	 * 
	 * If the cell's life state isn't changing, and it's alive, then it's colour
	 * may change if it's old enough.
	 */
	{
		scale = scale_num;
		scaleSet();
	} // end of updateState()

	private void scaleSet()
	// hardwired age values for setting the cell's new colour
	{
		if (scale > 8)
			scaleT3d.set(2.0);
		else if (scale > 6)
			scaleT3d.set(1.5);
		else if (scale > 5)
			scaleT3d.set(1.0);
		else if (scale > 2)
			scaleT3d.set(0.5);
		else
			scaleT3d.set(0.2);

	} // end of ageSetColour()

	// -------------------------- visual update --------------------------

	public void blinkVisualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		if (blinkVisualState == FADE_OUT)
			transAtt.setTransparency(transFrac - 0.2f); // 1.0f is totally
														// transparent
		else if (blinkVisualState == FADE_IN)
			transAtt.setTransparency(1.0f - transFrac);
		else if (blinkVisualState == VISIBLE) {
			changeColour(transFrac);
		} else if (blinkVisualState == INVISIBLE) {
		}
		// do nothing
		else
			System.out.println("Error in visualState");

		// if(visualState == FADE_OUT)
		// transAtt.setTransparency(transFrac); // 1.0f is totally transparent
		// else if (visualState == VISIBLE)
		// transAtt.setTransparency(1.0f-transFrac);
		// else if (visualState == VISIBLE) {
		// changeColour(transFrac);
		// }
		// else if (visualState == INVISIBLE) {
		// }
		// // do nothing
		// else
		// System.out.println("Error in visualState");

		// if (blinkTransCounter == Spheres3D.MAX_TRANS)
		// endVisualTransition();
	} // end of visualChange()
	
	public void vibrationVisualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		double vibration_rate;
		cellTG.getTransform(this.bufvibrationT3d); // get current rotation
		vibrationT3d.setIdentity(); // reset the rotation transform object
		bufvibrationT3d.get(currV3d);

		vibration_rate = 0.0009 * transCounter; //0.9 + 0.035
		translation.x = vibration_rate;
//		translation.y = vibration_rate;
//		translation.z = vibration_rate;
		vibrationT3d.set(translation);
		bufvibrationT3d.mul(vibrationT3d);
		
//		translation.x = dx;
//		translation.y = dy;
//		translation.z = 0;
//		
//		pullT3d.set(translation);
//		bufPullT3d.mul(pullT3d);
		
		cellTG.setTransform(bufvibrationT3d); // update the TG
		
	} // end of visualChange()
	
	public void pulsationVisualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		cellTG.getTransform(this.bufPulsationT3d); // get current rotation
		pulsationT3d.setIdentity(); // reset the rotation transform object
		
		double pulsation_scale = 0.9;
		
		pulsation_scale = 0.7 + 0.105 * transCounter; //0.9 + 0.035
//		pulsation_scale = 0.7 + 0.08 * transCounter; //0.9 + 0.035
		pulsationT3d.setScale(pulsation_scale);
//		bufPulsationT3d.mul(pulsationT3d);
//		cellTG.setTransform(bufPulsationT3d); // update the TG
		bufPulsationT3d.setScale(pulsation_scale);
		cellTG.setTransform(bufPulsationT3d); // update the TG
		
	} // end of visualChange()

	public void visualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		if (visualState == FADE_OUT)
			transAtt.setTransparency(transFrac); // 1.0f is totally
													// transparent
		else if (visualState == FADE_IN)
			transAtt.setTransparency(1.0f - transFrac);
		else if (visualState == VISIBLE) {
			changeColour(transFrac);
		} else if (visualState == INVISIBLE) {
		}
		// do nothing
		else
			System.out.println("Error in visualState");

		// if(visualState == FADE_OUT)
		// transAtt.setTransparency(transFrac); // 1.0f is totally transparent
		// else if (visualState == VISIBLE)
		// transAtt.setTransparency(1.0f-transFrac);
		// else if (visualState == VISIBLE) {
		// changeColour(transFrac);
		// }
		// else if (visualState == INVISIBLE) {
		// }
		// // do nothing
		// else
		// System.out.println("Error in visualState");

		if (transCounter == Spheres3D.MAX_TRANS)
			endVisualTransition();
	} // end of visualChange()

	public void appearVisualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		if (visualState == FADE_OUT)
			transAtt.setTransparency(transFrac); // 1.0f is totally
													// transparent
		else if (visualState == FADE_IN)
			transAtt.setTransparency(1.0f - transFrac);
		else if (visualState == VISIBLE) {
			changeColour(transFrac);
		} else if (visualState == INVISIBLE) {
		}
		// do nothing
		else
			System.out.println("Error in visualState");

		// if(visualState == FADE_OUT)
		// transAtt.setTransparency(transFrac); // 1.0f is totally transparent
		// else if (visualState == VISIBLE)
		// transAtt.setTransparency(1.0f-transFrac);
		// else if (visualState == VISIBLE) {
		// changeColour(transFrac);
		// }
		// else if (visualState == INVISIBLE) {
		// }
		// // do nothing
		// else
		// System.out.println("Error in visualState");

		if (transCounter == Spheres3D.MAX_TRANS)
			endVisualTransition();
	} // end of visualChange()

	public void colorVisualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		// float transFrac = ((float)transCounter)/Spheres3D.MAX_TRANS;
		//
		// if(visualState == FADE_OUT)
		// transAtt.setTransparency(transFrac); // 1.0f is totally transparent
		// else if (visualState == FADE_IN)
		// transAtt.setTransparency(1.0f-transFrac);
		// else if (visualState == VISIBLE) {
		// changeColour(transFrac);
		// }
		// else if (visualState == INVISIBLE) {
		// }
		// // do nothing
		// else
		// System.out.println("Error in visualState");

		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		changeColour(transFrac);

		// if(visualState == FADE_OUT)
		// transAtt.setTransparency(transFrac); // 1.0f is totally transparent
		// else if (visualState == VISIBLE)
		// transAtt.setTransparency(1.0f-transFrac);
		// else if (visualState == VISIBLE) {
		// changeColour(transFrac);
		// }
		// else if (visualState == INVISIBLE) {
		// }
		// // do nothing
		// else
		// System.out.println("Error in visualState");

		if (blinkTransCounter == Spheres3D.MAX_TRANS)
			endVisualTransition();
	} // end of visualChange()

	private void changeColour(float transFrac)
	/*
	 * the current cell's colour is a mix of its old and new colours (if the two
	 * are different)
	 */
	{
		if (!oldCol.equals(newCol)) { // if colours are different
			float redFrac = oldCol.x * (1.0f - transFrac) + newCol.x
					* transFrac;
			float greenFrac = oldCol.y * (1.0f - transFrac) + newCol.y
					* transFrac;
			float blueFrac = oldCol.z * (1.0f - transFrac) + newCol.z
					* transFrac;

			cellCol.set(redFrac, greenFrac, blueFrac);
			setMatColours(cellCol);
		}
	} // end of changeColour()

	public void endVisualTransition()
	/*
	 * At the end of a transition, the final colour is stored, the new cell's
	 * life state is stored, and the visual state is changed to VISIBLE or
	 * INVISIBLE.
	 */
	{
		// store current colour as both the old and new colours;
		// used when fading in and when visible
		oldCol.set(cellCol);
		newCol.set(cellCol);

		isAlive = newAliveState; // update alive state

		if (visualState == FADE_IN)
			visualState = VISIBLE;
		else if (visualState == FADE_OUT)
			visualState = INVISIBLE;
	} // end of endVisualTransition()

	public void colorStaticChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{

		float transFrac = ((float) Spheres3D.MAX_TRANS) / Spheres3D.MAX_TRANS;

		changeColour(transFrac);

		if (blinkTransCounter == Spheres3D.MAX_TRANS)
			endVisualTransition();
	} // end of visualChange()

	private void changeStateColour(float transFrac)
	/*
	 * the current cell's colour is a mix of its old and new colours (if the two
	 * are different)
	 */
	{
		if (!oldCol.equals(newCol)) { // if colours are different
			float redFrac = oldCol.x * (1.0f - transFrac) + newCol.x
					* transFrac;
			float greenFrac = oldCol.y * (1.0f - transFrac) + newCol.y
					* transFrac;
			float blueFrac = oldCol.z * (1.0f - transFrac) + newCol.z
					* transFrac;

			cellCol.set(redFrac, greenFrac, blueFrac);
			setMatColours(cellCol);
		}
	} // end of changeColour()

	public void scaleVisualChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{

		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		changeScale(transFrac);

	} // end of visualChange()

	private void changeScale(float alphaValue)
	/*
	 * the current cell's colour is a mix of its old and new colours (if the two
	 * are different)
	 */
	{

		float minimumScale = 0.1f;
		float maximumScale = 1.0f;

		// scaleBaseTG.getTransform(); // get current rotation
		// scaleT3d.setIdentity(); // reset the rotation transform object

		double val = (1.0 - alphaValue) * minimumScale + alphaValue
				* maximumScale;

		// scaleT3d.set(val);
		// t3d.mul(scaleT3d); // 'add' new rotation to current one
		// scaleBaseTG.setTransform(t3d);

		// double val = (1.0-alphaValue)*minimumScale + alphaValue*maximumScale;

		// scaleT3d.set(val);
		//	
		// scaleBaseTG.getTransform(t3d); // get current rotation
		// scaleT3d.setIdentity(); // reset the rotation transform object
		//
		// t3d.mul(scaleT3d); // 'add' new rotation to current one
		// scaleBaseTG.setTransform(t3d);

		// double xPosn = (double) (12.0);
		// double yPosn = (double) (12.0);
		// double zPosn = 0.0;
		// scaleBaseTG.getTransform(t3d); // get current rotation
		// scaleT3d.setIdentity(); // reset the rotation transform object
		// scaleT3d.setTranslation(new Vector3d(xPosn, yPosn, zPosn));
		// t3d.mul(scaleT3d); // 'add' new rotation to current one
		// scaleBaseTG.setTransform(t3d);

	}

	public void vanishChange(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		transAtt.setTransparency(transFrac); // 1.0f is totally transparent

		endVisualTransition();
	} // end of visualChange()

	public void vanishChangeEnd(int transCounter)
	/*
	 * A cell is in one of the folowing visual states: FADE_OUT, where the cell
	 * gradually disappears; FADE_IN, where the cell gradually apppears;
	 * VISIBLE, where the cell's colour may gradually change; INVISIBLE, where
	 * nothing happens to the cell's appearance.
	 */
	{
		float transFrac = ((float) transCounter) / Spheres3D.MAX_TRANS;

		transAtt.setTransparency(1.0f); // 1.0f is totally transparent

		endVisualTransition();
	} // end of visualChange()

	public SphereIndividual getIndividual() {
		return sindividual;
	}

	public void setIndividual(SphereIndividual sind) {
		this.sindividual = sind;
	}

	public void setAppearacne(Appearance app) {
		this.cellApp = app;
	}

	public Appearance getAppearance() {
		return this.cellApp;
	}

	public TransformGroup getScaleBaseTG() {
		return this.scaleBaseTG;
	}

} // end of Cell class
