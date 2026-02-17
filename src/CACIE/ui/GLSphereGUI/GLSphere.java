package CACIE.ui.GLSphereGUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.Color;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import CACIE.ui.*;

import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;

public class GLSphere extends JApplet
implements GLEventListener, MouseMotionListener, MouseListener, KeyListener{

	GraphicalPopulationPresenterEvaluator_GLSphere gppe;
	int numOfIndividual = 16;
	//size:800x600 
	int sizeX, sizeY;

	private GL gl;
	private GLU glu;
	private GLUT glut;
	
	private GLJPanel panel;
	
	private boolean mouseRightPressed, mouseLeftPressed;
	private Point previousMousePoint, currentMousePoint;
	private boolean shiftButtonPressed;
	
	private float rotateAngle; //in degree
	private ArrayList<Float> distanceFromCoC, apportedAngle, 
	spherePositionsInWorldCord, spherePositionsInScreenCord;
	// Array of Distances from Center of Circle of each individual
	// for internal process 0 to 100 in float, Displayed as 5 degree;
	//private float distFromCoCOffset = 10.f;
	
	private int currentDraggingIndex = -1;
	private int currentPlaybackingIndex = -1;
	private int beatingCounter = 0;
	private int currentOverWrappingIndex = -1;
	
	private float cameraDistanceFromCoC;
	//private float currentMouseDistanceFromCoC;
	
	private float cameraAngleX, cameraAngleY; //in radians
	private float previousLookFrom[];
	
	private float whRatio;
	
	private GLCapabilities glCapabilities;
	
	private double modelviewMatrix[], projectionMatrix[];
	private int viewport[];
	
	FPSAnimator animator;
	
	//色の設定
	private float diffuseColor[]; //色はsphereごとに設定
	//specularとambient, shininessは固定
	private float specular[] = {0.8f, 0.8f, 0.8f, 1.f};
	private float ambient[] = {0.4f, 0.4f, 0.4f, 1.f};
	private float shininess = 128.f;
	
	protected double distancesOfIndividuals[][];
	
	public GLSphere() {
		super();
		sizeX = 800; 
		sizeY = 600;
		setSize(sizeX, sizeY);

		whRatio = (float)sizeX / (float)sizeY;

		cameraDistanceFromCoC = 60;
		cameraAngleX = (float)Math.PI / 4.f;
		cameraAngleY = (float)Math.PI / 4.f;
		
		previousLookFrom = new float[3];
		previousLookFrom[0] =
			(float)(cameraDistanceFromCoC * Math.sin(cameraAngleY) * Math.cos(cameraAngleX));
		previousLookFrom[1] = 
			(float)(cameraDistanceFromCoC * Math.sin(cameraAngleY) * Math.sin(cameraAngleX));
		previousLookFrom[2] = 
			(float)(cameraDistanceFromCoC * Math.cos(cameraAngleY));
				
		panel = new GLJPanel(glCapabilities);
		panel.addGLEventListener(this);
		panel.setSize(sizeX,sizeY);
		add(panel);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		
		previousMousePoint = new Point(0,0);
		currentMousePoint = new Point(0,0);
		
		modelviewMatrix = new double[16];
		projectionMatrix = new double[16];
		viewport = new int[4];
		
		rotateAngle = 0.f;

		spherePositionsInWorldCord = new ArrayList<Float>(numOfIndividual*3);
		for(int i=0; i<numOfIndividual*3; i++)
			spherePositionsInWorldCord.add(new Float(0.f));
		spherePositionsInScreenCord = new ArrayList<Float>(numOfIndividual*3);
		for(int i=0; i<numOfIndividual*3; i++)
			spherePositionsInScreenCord.add(new Float(0.f));
		
		apportedAngle = new ArrayList<Float>(numOfIndividual);
		distanceFromCoC = new ArrayList<Float>(numOfIndividual);
		for(int i=0; i<numOfIndividual; i++){
			apportedAngle.add(new Float(22.5 * i));
			distanceFromCoC.add(new Float(3.f));
		}
		
		distancesOfIndividuals = gppe.getDistances();
		
		animator = new FPSAnimator(panel, 10, true);		
		animator.start();
	}
	
	public void setGraphicalPopulationPresenterEvaluator
	(GraphicalPopulationPresenterEvaluator_GLSphere newGppe){
		gppe = newGppe;
	}

	public void setNumOfIndividual(int newNumOfIndividual){
		numOfIndividual = newNumOfIndividual;
		distanceFromCoC = new ArrayList<Float>(numOfIndividual);
		for(int i=0; i<numOfIndividual; i++)
			distanceFromCoC.add(new Float(3.));
	}
		
	public void display(GLAutoDrawable arg0) {
		//回転++
		rotateAngle += 0.1f;
		if(rotateAngle >= 360.f)
			rotateAngle = 0.f;
		//拍動++
		if(currentPlaybackingIndex != 0)
			beatingCounter++;
		if(beatingCounter > 20)
			beatingCounter = 0;
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		//set view area
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		setViewArea(arg0);
		
		//set view angle and point
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		setViewPoints(arg0);
		
		//Draw Background
		drawBackGrounds(arg0);

		refreshMatrixes();
		getSpherePositionsInWorldCord();
		getSpherePositionsInScreenCord();
		double indexAndDistance[] = minDistances();

		if(indexAndDistance[1] > 30.0)
			currentOverWrappingIndex = -1;
		else
			currentOverWrappingIndex = new Double(indexAndDistance[0]).intValue();
		
        //Draw Spheres
		drawShperes(arg0);
	}
	
	protected void drawShperes(GLAutoDrawable gad){
		//gl.glMatrixMode(GL.GL_MODELVIEW);
		//gl.glLoadIdentity();
		
		gl.glPushMatrix();
		gl.glRotatef(rotateAngle, 0.f, 1.f, 0.f);
		for(int i=0; i<numOfIndividual; i++)
			drawEachSphere(gad, i);
		gl.glPopMatrix();
	}
	
	protected void drawEachSphere(GLAutoDrawable gad, int index){
		gl.glPushMatrix();
		gl.glRotatef(apportedAngle.get(index).floatValue(), 0.f, 1.f, 0.f);
		gl.glTranslatef(distanceFromCoC.get(index).floatValue() * (float)5., 0.f, 0.f);
		//gl.glColor4f(1.f, 1.f, 1.f, 1.f);
		//convert HSB to RGB
		float hue = 1.f / (float) numOfIndividual * (float) index; 
		Color rgbColor = new Color(Color.HSBtoRGB(hue, 1.f, 0.99f));
		
		//diffuseのカラー設定
		diffuseColor = new float[4];
		diffuseColor[0] = (float)rgbColor.getRed() / 255.f; 
		diffuseColor[1] = (float)rgbColor.getGreen() / 255.f; 
		diffuseColor[2] = (float)rgbColor.getBlue() / 255.f; 
		diffuseColor[3] = 1.f;
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuseColor, 0);

		//マウスオーバーラップ時にAmbientで明るくする
		if(index == currentOverWrappingIndex){
			ambient[0] = 1.f;
			ambient[1] = 1.f;
			ambient[2] = 1.f;
			ambient[3] = 1.f;	
		}
		else{
			ambient[0] = 0.4f;
			ambient[1] = 0.4f;
			ambient[2] = 0.4f;
			ambient[3] = 1.f;
		}
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient, 0);
		
		gl.glEnable(GL.GL_LIGHTING);		
		if(index == currentPlaybackingIndex)
			glut.glutSolidSphere(1.5f + Math.sin(beatingCounter * 0.05 * Math.PI), 20, 20);
		else
			glut.glutSolidSphere(1.5f, 20, 20);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glPopMatrix();
	}
	
	protected void drawBackGrounds(GLAutoDrawable gad){
		//Draw degree rings
		gl.glPushMatrix();
		gl.glRotatef(90, 1.0f, 0.f, 0.f);
		glut.glutSolidTorus(0.1f, 5.f, 5, 64);
		glut.glutSolidTorus(0.1f, 10.f, 5, 64);
		glut.glutSolidTorus(0.1f, 15.f, 5, 64);
		glut.glutSolidTorus(0.1f, 20.f, 5, 64);
		glut.glutSolidTorus(0.1f, 25.f, 5, 64);
		glut.glutSolidTorus(0.1f, 30.f, 5, 64);
		glut.glutSolidTorus(0.1f, 35.f, 5, 64);
		glut.glutSolidTorus(0.1f, 40.f, 5, 64);
		gl.glPopMatrix();
	}
	
	protected void setLighting(GLAutoDrawable gad){
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);

		float[] lightPosition = {-10.f, 10.f, 10.f, 0.f};		
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specular, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient, 0);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, shininess);
		
	}
	
	protected void setViewArea(GLAutoDrawable arg0){
		whRatio = (float)sizeX / (float)sizeY; 
		gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
		glu.gluPerspective(45, whRatio, 5, 200);
		//gl.glMatrixMode(GL.GL_MODELVIEW);
		//gl.glLoadIdentity();
	}
	
	protected void setViewPoints(GLAutoDrawable arg0){
		
		//Mouse Detection
		//mouse dragging with control button pressed changes view angle
		//moving half of screen rotates 2*PI (90 degree) angle.
		
		//System.err.println("in setViewPoints()");

		float cameraFrom[] = new float[4];
		if(shiftButtonPressed &&
				mouseLeftPressed &&
				currentMousePoint.getX() != previousMousePoint.getX() && 
				currentMousePoint.getY() != previousMousePoint.getY()){
			//If point dragging point moving, refresh point where from camera look

			float subtractMovementX = 
				(float)(currentMousePoint.getX() - previousMousePoint.getX());
			float subtractMovementY =
				(float)(currentMousePoint.getY() - previousMousePoint.getY());			
			
			if(mouseLeftPressed){
				//Change view Angles		
				float moveAngleY = (float)( Math.PI * subtractMovementX / sizeX);
				float moveAngleX = (float)( Math.PI * subtractMovementY / sizeY);
			
				if(cameraAngleX > Math.PI)
					moveAngleX = moveAngleX * -1.f;
				if(cameraAngleY > Math.PI)
					moveAngleY = moveAngleY * -1.f;
				
				//Get CameraAngle
				cameraAngleX += moveAngleX;
				cameraAngleY += moveAngleY;
				
				if(cameraAngleX > 2.* Math.PI)
					cameraAngleX = cameraAngleX - (float)(2. * Math.PI);
				else if(cameraAngleX < -1. * 2.* Math.PI)
					cameraAngleX = cameraAngleX + (float)(2. * Math.PI);
				if(cameraAngleY > 2.* Math.PI)
					cameraAngleY = cameraAngleY - (float)(2. * Math.PI);
				else if(cameraAngleY < -1. * 2.* Math.PI)
					cameraAngleY = cameraAngleY + (float)(2. * Math.PI);
				//System.err.println
				//("GLSphere:setViewPoints() : cameraAngleX:" + cameraAngleX + " cameraAngleY:" + cameraAngleY);
							 
			}
			else if(mouseRightPressed){
				//change distance from center of circle
				cameraDistanceFromCoC += subtractMovementX; 
			}

			//Multiply rotate Matrix
			//MatrixRotation3Df matrixRotation3Df = 
				//new MatrixRotation3Df(cameraAngleX, cameraAngleY, 0.f);
			//convert points
			//cameraFrom = 
				//matrixRotation3Df.calcOut(0, 0, cameraDistanceFromCoC);
			//System.err.println("CameraPos is: " + cameraFrom[0] +"," +cameraFrom[1] +","+cameraFrom[2]);
			
			cameraFrom[0] =
				(float)(cameraDistanceFromCoC * Math.sin(cameraAngleY) * Math.cos(cameraAngleX));
			cameraFrom[1] = 
				(float)(cameraDistanceFromCoC * Math.sin(cameraAngleY) * Math.sin(cameraAngleX));
			cameraFrom[2] = 
				(float)(cameraDistanceFromCoC * Math.cos(cameraAngleY));		
		}
		else{
			cameraFrom[0] = previousLookFrom[0];
			cameraFrom[1] = previousLookFrom[1];
			cameraFrom[2] = previousLookFrom[2];
		}
		

        glu.gluLookAt(cameraFrom[0], cameraFrom[1], cameraFrom[2], 0.f, 0.f, 0.f, 0, 1, 0);
        for(int i=0; i<3; i++)
        	previousLookFrom[i] = cameraFrom[i];
	}


	protected void setShperePosition(GLAutoDrawable arg0){

	}
	
	protected double[] minDistances(){
		double minDistance = 10000.;
		double minIndex = -1;
		double returnValue[] = new double[2];
		for(int i=0; i<numOfIndividual; i++){
			int sphereX = spherePositionsInScreenCord.get(i*3).intValue(); 
			int sphereY = spherePositionsInScreenCord.get(i*3+1).intValue();
			int mouseX = (int)currentMousePoint.getX();
			int mouseY = (int)currentMousePoint.getY();
			double currentDistance = 
				Math.sqrt(Math.pow(sphereX - mouseX,2.0) + Math.pow(sphereY - mouseY, 2.0));
			if(currentDistance < minDistance){
				minDistance = currentDistance; 
				minIndex = i;
			}
		}
		returnValue[0] = minIndex;
		returnValue[1] = minDistance;
		return returnValue;
	}
	
	/*
	protected boolean isOverWrapped(float marginPixels, int index){
		//marginPixels : margin width which from the projected point  
		//currentMousePoint
		boolean returnValue = false;
		int sphereX = spherePositionsInScreenCord.get(index*3).intValue();
		int sphereY = spherePositionsInScreenCord.get(index*3+1).intValue();
		int mouseX = (int)currentMousePoint.getX();
		int mouseY = (int)currentMousePoint.getY();
		if(sphereX - marginPixels <= mouseX && mouseX <= sphereX + marginPixels &&
				sphereY - marginPixels <= mouseY && mouseY <= sphereY + marginPixels)
			returnValue = true;
		return returnValue;
	}

	protected int isOverWrapped(float marginPixels){
		int returnIndex = -1;
		for(int i=0; i<numOfIndividual; i++){
			if(isOverWrapped(marginPixels, i)){
				returnIndex = i;
				break;
			}
		}
		return returnIndex;
	}
	*/
	
	protected void refreshMatrixes(){
		//Get Modelview Matrix
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelviewMatrix, 0);
		//Get Projection Matrix
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		//Get Viewport
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);		
	}
	
	protected void getSpherePositionsInWorldCord(){
		//spherePositionsInWorldCord.clear();
		for(int i=0; i<numOfIndividual; i++){
			double sphereAngleInDegree = rotateAngle+ apportedAngle.get(i).floatValue();
			double sphereAngleInRad = Math.toRadians(sphereAngleInDegree);
			
			//float x = (float)(distanceFromCoC.get(i) * 5. * Math.cos(sphereAngleInRad));
			//float y = (float)(distanceFromCoC.get(i) * 5. * Math.sin(sphereAngleInRad));
			//float z = (float)0.;
			
			float x = (float)(distanceFromCoC.get(i) * 5. * Math.cos(sphereAngleInRad));
			float y = (float)0.;
			float z = -1.f * (float)(distanceFromCoC.get(i) * 5. * Math.sin(sphereAngleInRad));
			
			spherePositionsInWorldCord.remove(i*3);
			spherePositionsInWorldCord.add(i*3, new Float(x));
			spherePositionsInWorldCord.remove(i*3+1);
			spherePositionsInWorldCord.add(i*3+1,new Float(y));
			spherePositionsInWorldCord.remove(i*3+2);
			spherePositionsInWorldCord.add(i*3+2,new Float(z));

			//System.err.print(sphereAngleInDegree + ", ");
		}
		//System.err.println();

	}
	
	protected void getSpherePositionsInScreenCord(){
		//refreshMatrixes();
		//spherePositionsInScreenCord.clear();
		double screenPos[] = new double[3];
		for(int i=0; i<numOfIndividual; i++){
			if(glu.gluProject(
					spherePositionsInWorldCord.get(i*3).doubleValue(), 
					spherePositionsInWorldCord.get(i*3+1).doubleValue(),
					spherePositionsInWorldCord.get(i*3+2).doubleValue(),
					modelviewMatrix, 0, 
					projectionMatrix, 0, 
					viewport, 0, screenPos, 0)){
				/*
				 * for(int j=0; j<3; j++){
				 
					spherePositionsInScreenCord.remove(i*3+j);
					spherePositionsInScreenCord.add(i*3+j, new Float(screenPos[j]));
				}
				*/
				spherePositionsInScreenCord.remove(i*3);
				spherePositionsInScreenCord.add(i*3, new Float(screenPos[0]));
				spherePositionsInScreenCord.remove(i*3+1);
				spherePositionsInScreenCord.add(i*3+1, new Float(viewport[3] - screenPos[1]));
				spherePositionsInScreenCord.remove(i*3+2);
				spherePositionsInScreenCord.add(i*3+2, new Float(screenPos[2]));
			}
			else{
				System.err.println("gluProject is failed");
			}
		}
	}
	
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	public void init(GLAutoDrawable glAutoDrawable) {
		// TODO Auto-generated method stub
		gl = glAutoDrawable.getGL();
		glu = new GLU(); 
		glut = new GLUT();
				

		gl.glViewport(0, 0, sizeX, sizeY);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glClearColor(0.f, 0.f, 0.f, 1.f);
				
		setLighting(glAutoDrawable);
		setViewArea(glAutoDrawable);
		setViewPoints(glAutoDrawable);
		refreshMatrixes();
	}

	public void reshape(GLAutoDrawable glAutoDrawable, 
			int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		sizeX = width;
		sizeY = height;
		
		gl.glViewport(0, 0, sizeX, sizeY);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		setLighting(glAutoDrawable);
		setViewArea(glAutoDrawable);
		setViewPoints(glAutoDrawable);
		refreshMatrixes();
	}


	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}
	
	protected void apportSimilarIndividualIcons(){
		// 正規化したレンジの20%内の距離をもつ個体を引き寄せる
		int targetIndex = currentDraggingIndex;

		double minDistance = 0.0, maxDistance = 1.0;
		// Normalize
		for (int i = 0; i < numOfIndividual; i++) {
			double currentDistance = distancesOfIndividuals[targetIndex][i];
			if (i == 0) {
				minDistance = currentDistance;
				maxDistance = currentDistance;
			} else if (currentDistance < minDistance)
				minDistance = currentDistance;
			else if (currentDistance > maxDistance)
				maxDistance = currentDistance;

		}
		int numOfApportingIndividual = 0;
		int[] apportingIndexes = new int[numOfIndividual];
		for(int i=0; i<numOfIndividual; i++){
			if(distancesOfIndividuals[targetIndex][i] <= 0.5 * maxDistance &&
					i != targetIndex){
				apportingIndexes[numOfApportingIndividual] = i;
				numOfApportingIndividual++;
			}
		}
		
	}
	
	protected int getFitnessValue(int index){
		int currentFitness = distanceFromCoC.get(index).intValue();
		if(currentFitness <= 1)
			return 0;
		else if(currentFitness <= 2)
			return 25;
		else if(currentFitness <= 3)
			return 50;
		else if(currentFitness <= 4)
			return 75;
		else
			return 100;
	}

	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		//装飾キー(Ctrl)がついているとこのイベントは検出されない模様
		//Shiftなら大丈夫っぽい
		
		previousMousePoint = currentMousePoint;
		currentMousePoint = e.getPoint();
		//センターと球の現在のスクリーン座標を取得
		int centerX = sizeX / 2; int centerY = sizeY / 2;
		refreshMatrixes();
		getSpherePositionsInWorldCord();
		getSpherePositionsInScreenCord();

		if(currentDraggingIndex > 0 && !shiftButtonPressed){
		float spherePositionXinScreen = 
			spherePositionsInScreenCord.get(currentDraggingIndex*3);
		float spherePositionYinScreen = 
			spherePositionsInScreenCord.get(currentDraggingIndex*3+1);
		float distanceInScreen = 
			(float)Math.sqrt
			(Math.pow((spherePositionXinScreen-centerX), 2) + 
					Math.pow(spherePositionYinScreen-centerY, 2));
		
		//マウスが移動した位置が，5段階のどこにあるのかを検知して，その場所に移動．
		float aUnitOfDistanceInScreenCord = 
			distanceInScreen / distanceFromCoC.get(currentDraggingIndex);
		float unitOfDistances[] = new float[5];
		for(int i=0; i<5; i++)
			unitOfDistances[i] = aUnitOfDistanceInScreenCord * (i+1);
		
		float mouseX = (float) currentMousePoint.getX();
		float mouseY = (float) currentMousePoint.getY();
		float mouseDistance =
			(float)Math.sqrt(Math.pow(centerX - mouseX,2) + Math.pow(centerY - mouseY, 2));
		float newDistanceOfTheSphere = -1;
		if(mouseDistance < unitOfDistances[0] + (aUnitOfDistanceInScreenCord / 2))
			newDistanceOfTheSphere = 1;
		else if(mouseDistance < unitOfDistances[1] + (aUnitOfDistanceInScreenCord / 2))
			newDistanceOfTheSphere = 2;
		else if(mouseDistance < unitOfDistances[2] + (aUnitOfDistanceInScreenCord / 2))
			newDistanceOfTheSphere = 3;
		else if(mouseDistance < unitOfDistances[3] + (aUnitOfDistanceInScreenCord / 2))
			newDistanceOfTheSphere = 4;
		else
			newDistanceOfTheSphere = 5;
		distanceFromCoC.remove(currentDraggingIndex);
		distanceFromCoC.add(currentDraggingIndex, new Float(newDistanceOfTheSphere));	
		}
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		currentMousePoint = e.getPoint();
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON1)
			mouseLeftPressed = true;
		else if(e.getButton() == MouseEvent.BUTTON3)
			mouseRightPressed = true;
		currentMousePoint.setLocation(e.getPoint());
		previousMousePoint.setLocation(e.getPoint());
		
		//Check projection and overrapp
		refreshMatrixes();
		getSpherePositionsInWorldCord();
		getSpherePositionsInScreenCord();
				
		double distances[] = minDistances();
		System.err.print("Nearest Sphere index is: " + distances[0] + 
				", Distance is: " + distances[1]);
		System.err.print(" ");
		if(distances[1] < 30){
			currentDraggingIndex = new Double(distances[0]).intValue();
			currentPlaybackingIndex = new Double(distances[0]).intValue();
			System.err.println("Index:" + currentDraggingIndex + "is Pressed.");
			//if(currentDraggingIndex >= 0){
				//currentMouseDistanceFromCoC = distanceFromCoC.get(currentDraggingIndex);
			//}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			//個体の再生の開始
			if(currentDraggingIndex >= 0){
				gppe.stopAll();
				gppe.playAsMIDISequence(currentDraggingIndex);
			}
			mouseLeftPressed = false;
		}
		else if(e.getButton() == MouseEvent.BUTTON3){
			if(currentDraggingIndex >= 0)
				apportSimilarIndividualIcons();
			mouseRightPressed = false;
		}
		currentMousePoint.setLocation(e.getPoint());
		previousMousePoint.setLocation(e.getPoint());
		currentDraggingIndex = -1;
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
			shiftButtonPressed = true;
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_SHIFT)
			shiftButtonPressed = false;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
