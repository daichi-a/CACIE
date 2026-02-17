package CACIE.ui.sphereGUI;
/*
 * $RCSfile: PickSphereColorChangeBehavior.java,v $
 *
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.2 $
 * $Date: 2012/05/18 06:07:19 $
 * $State: Exp $
 */

//package com.sun.j3d.utils.behaviors.picking;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.picking.PickMouseBehavior;
import com.sun.j3d.utils.behaviors.picking.PickObject;
import com.sun.j3d.utils.behaviors.picking.PickingCallback;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;


// A mouse behavior that allows user to pick and zoom scene graph objects.
// Common usage: 1. Create your scene graph. 2. Create this behavior with
// the root and canvas. See PickRotateBehavior for more details. 

/**
 * @deprecated As of Java 3D version 1.2, replaced by
 * <code>com.sun.j3d.utils.picking.behaviors.PickZoomBehavior</code>
 *
 * @see com.sun.j3d.utils.picking.behaviors.PickZoomBehavior
 */

public class PickSphereColorChangeBehavior extends PickMouseBehavior implements MouseBehaviorCallback {
	MouseSphereColorChange zoom;
  int pickMode = PickObject.USE_BOUNDS;
  protected MouseSphereTranslateCallback callback = null;
  private TransformGroup currentTG;
  private Transform3D localTG = new Transform3D();
  private Transform3D inverseTG = new Transform3D();
  private Transform3D currT3d = new Transform3D();
  Vector3d currV3d = new Vector3d();
  Point3d currP3d = new Point3d();
  Point3d cursorP3d = new Point3d();
  Vector4d currV4d = new Vector4d();
  protected int nodeType = PickResult.SHAPE3D;
  WrapSpheres3D ws3;
  SphereIndividual sind;

  
  private double slope;
  private PickCanvas pickCanvas;
  private Transform3D plate2world = new Transform3D();
  Point3d viewP3d = new Point3d();
  
  
  private SphereProperties m_sps;
  int m_width;
  int m_height;
  

  /**
   * Creates a pick/zoom behavior that waits for user mouse events for
   * the scene graph. This method has its pickMode set to BOUNDS picking. 
   * @param root   Root of your scene graph.
   * @param canvas Java 3D drawing canvas.
   * @param bounds Bounds of your scene.
   **/

  public PickSphereColorChangeBehavior(WrapSpheres3D ws, BranchGroup root, Canvas3D canvas, Bounds bounds, 
		  int type, SphereProperties sps){
    super(canvas, root, bounds);
    this.ws3 = ws;
    this.pickCanvas = new PickCanvas(canvas, root);
    this.m_sps = sps;
    this.m_width = sps.getWidth();
    this.m_height = sps.getHeight();
    this.setSchedulingBounds(bounds);
    sind = new SphereIndividual();
    zoom = new MouseSphereColorChange(this, MouseBehavior.MANUAL_WAKEUP, m_sps);
    zoom.setTransformGroup(currGrp);
    currGrp.addChild(zoom);
    zoom.setSchedulingBounds(bounds);
    nodeType = type;
    

  }

  /**
   * Creates a pick/zoom behavior that waits for user mouse events for
   * the scene graph.
   * @param root   Root of your scene graph.
   * @param canvas Java 3D drawing canvas.
   * @param bounds Bounds of your scene.
   * @param pickMode specifys PickObject.USE_BOUNDS or PickObject.USE_GEOMETRY.
   * Note: If pickMode is set to PickObject.USE_GEOMETRY, all geometry object in 
   * the scene graph that allows pickable must have its ALLOW_INTERSECT bit set. 
   **/

  public PickSphereColorChangeBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds,
			  int pickMode){
    super(canvas, root, bounds);
    zoom = new MouseSphereColorChange(this, MouseBehavior.MANUAL_WAKEUP, m_sps);
    zoom.setTransformGroup(currGrp);
    currGrp.addChild(zoom);
    zoom.setSchedulingBounds(bounds);
    this.setSchedulingBounds(bounds);
    this.pickMode = pickMode;  
  }
  
  /**
   * Sets the pickMode component of this PickZoomBehavior to the value of
   * the passed pickMode.
   * @param pickMode the pickMode to be copied.
   **/  

  public void setPickMode(int pickMode) {
    this.pickMode = pickMode;
  }
  
  
 /**
   * Return the pickMode component of this PickZoomBehavior.
   **/ 

  public int getPickMode() {
    return pickMode;
  }
  
  public TransformGroup getCurrentTG(){
	  return currentTG;
  }
  
  public void setCurrentTG(TransformGroup tg){
	  this.currentTG = tg;
  }
  
  public void setupCallback( MouseSphereTranslateCallback callback ) {
      this.callback = callback;
//      if (callback==null)
//          zoom.setupCallback( null );
//      else
//          zoom.setupCallback( this );
  }

  /**
   * Update the scene to manipulate any nodes. This is not meant to be 
   * called by users. Behavior automatically calls this. You can call 
   * this only if you know what you are doing.
   * 
   * @param xpos Current mouse X pos.
   * @param ypos Current mouse Y pos.
   **/

  public void updateScene(int xpos, int ypos){
    TransformGroup tg = null;
    
//    int m_width = 800;
//    int m_height = 600;
//    int dd =  (xpos - m_width / 2) * (xpos - m_width / 2) + 
//    	(ypos - m_height / 2) * (ypos - m_height / 2);
//    int d = (int) Math.sqrt((double)dd);
    
    //object position
//    System.out.println("xpos updateScene: " + xpos);
//    System.out.println("ypos updateScene: " + ypos);
    //System.out.println("distance: " + d); // about(0 < d < m_height / 2)
//    Node node = pickScene.pickNode(pickScene.pickClosest(xpos, ypos), nodeType);

      
    if (!mevent.isAltDown() && !mevent.isMetaDown()){
	   
      tg =(TransformGroup)pickScene.pickNode(pickScene.pickClosest(xpos, ypos, pickMode),
					     PickObject.TRANSFORM_GROUP);
      Node node = pickScene.pickNode(pickScene.pickClosest(xpos, ypos), nodeType);
      if(node != null){
    	  sind = (SphereIndividual) node.getUserData();
//    	  ws3.setFitness(sind.getIndex(), sind.getFitness());
    	  ws3.setFitness(sind.getIndex(), sind.getTransformedFitness());
      }
//      Node node = pickScene.pickNode(pickScene.pickClosest(xpos, ypos), nodeType);
      // Check for valid selection
      if ((tg != null) && 
	  (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ)) && 
	  (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))){

//    	zoom.setTransformGroup(tg);
//    	zoom.wakeup();
    	  
    	currentTG = tg;
    	
//    	zoom.setSlope(- ((double)(ypos - m_height / 2) / (double)(xpos - m_width / 2)));
//    	zoom.setDistance(d);
//    	zoom.setX((double)(xpos - m_width / 2));
//    	zoom.setY(- (double)(ypos - m_height / 2));
    	
    	SphereIndividual sind = (SphereIndividual) tg.getUserData(); 	
//    	zoom.setSlope(- ((double)(sind.getY() - m_height / 2) / (double)(sind.getX() - m_width / 2)));
//    	zoom.setSlope(sind.getSlope());
//    	zoom.setDistance(d);
//    	zoom.setX(sind.getX());
//    	zoom.setY(sind.getY());
//    	System.out.println("slope @updateScene: " + zoom.getSlope());
//    	System.out.println("xpos @updateScene: " + xpos);
//    	System.out.println("ypos @updateScene: " + ypos);
//    	System.out.println("slope of SphereIndividual @updateScene: " + sind.getSlope());
//    	System.out.println("x of SphereIndividual @updateScene: " + sind.getX());
//    	System.out.println("y of SphereIndividual @updateScene: " + sind.getY());
    	
    	/*
    	SphereIndividual sindividual = (SphereIndividual) tg.getUserData();
    	sindividual.setX((double)(xpos - m_width / 2));
    	sindividual.setY((double)(ypos - m_height / 2));
    	sindividual.setDistance(d);
    	sindividual.setFitness((int)d);
    	*/
    	//tg.setUserData(sindividual);
    	
    	//System.out.println("slope @updateScene: " + zoom.getSlope());
    	//System.out.println("distance @updateScene: " + zoom.getDistance());
    	//System.out.println("xpos @updateScene: " + zoom.getX());
    	//System.out.println("ypos @updateScene: " + zoom.getY());
    	//System.out.println("Index of SphereIndividual @updateScene: " + sindividual.index);
    	//System.out.println("Fitness of SphereIndividual @updateScene: " + sindividual.fitness);
	
	/*
    pickCanvas.getCanvas().
    getPixelLocationInImagePlate(xpos,ypos,currP3d);
    plate2world.transform(currP3d);
	*/
	/*
	System.out.println("success to pick: " + tg);
	currentTG.getTransform(currT3d);
	currT3d.get(currV3d);

	System.out.println("current vx: " + currV3d.x);
	System.out.println("current vy: " + currV3d.y);
	System.out.println("current vz: " + currV3d.z);
	*/
	
	
	currentTG.getLocalToVworld(localTG);
//    System.out.println(localTG);
    inverseTG.invert(localTG);
//    System.out.println(inverseTG);
    pickCanvas.getCanvas().getImagePlateToVworld(plate2world);
//    System.out.println("plate2world");
    pickCanvas.getCanvas().getCenterEyeInImagePlate(viewP3d);
    plate2world.transform(viewP3d);
    
	  pickCanvas.getCanvas().getPixelLocationInImagePlate(xpos, ypos, cursorP3d);
	  plate2world.transform(cursorP3d);
	  currentTG.getTransform( currT3d);
	  currT3d.get(currV3d);
	  currV4d.set(currV3d.x,
			  currV3d.y,
			  currV3d.z,
			  1);
	  localTG.transform(currV4d);
	  currV3d.set(currV4d.x,
			  currV4d.y,
			  currV4d.z);
//	  System.out.println("cursor =  " + cursorP3d);
//	  System.out.println("current =  " + currV3d);
	  System.out.println("current.x = " + currV3d.x);
	  System.out.println("current.y = " + currV3d.y);
//	  System.out.println("current.z = " + currV3d.z);
	  System.out.println("current.slope = " + currV3d.y / currV3d.x);
	  
	  sind.setX(currV3d.x);
	  sind.setY(currV3d.y);
	  
	  double r = Math.sqrt(currV3d.x * currV3d.x +currV3d.y * currV3d.y);
	  double theta = Math.atan(currV3d.y/ currV3d.x);
	  sind.setR(r);
	  sind.setTheta(theta);
	  sind.setSlope(Math.tan(theta));
	  
//  	zoom.setTransformGroup(tg);
	zoom.wakeup();
//	zoom.setSlope(Math.tan(theta));

    
//    translate(xpos, ypos);
	
    callback.pickIndividual(nodeType, node);
//      } else if (callback!=null)
//          callback.transformChanged( PickingCallback.NO_PICK, null );
   }
    
  } else if (callback!=null){
      //callback.transformChanged( MouseBehaviorCallback.TRANSLATE, currentTG );
}
//    callback.pickIndividual(nodeType, node);
	//System.out.println("xpos: " + xpos);
	//System.out.println("ypos: " + ypos);
}
  
  private void translate(int xpos, int ypos){
//	  pickCanvas.getCanvas().getPixelLocationInImagePlate(xpos, ypos, cursorP3d);
//	  plate2world.transform(cursorP3d);
//	  currentTG.getTransform( currT3d);
//	  currT3d.get(currV3d);
//	  currV4d.set(currV3d.x,
//			  currV3d.y,
//			  currV3d.z,
//			  1);
//	  localTG.transform(currV4d);
//	  currV3d.set(currV4d.x,
//			  currV4d.y,
//			  currV4d.z);
//	  System.out.println("cursor =  " + cursorP3d);
//	  System.out.println("current =  " + currV3d);
//	  System.out.println("current.x = " + currV3d.x);
//	  System.out.println("current.y = " + currV3d.y);
//	  System.out.println("current.z = " + currV3d.z);
//	  
//	  currV3d.sub(viewP3d);
//	  cursorP3d.sub(viewP3d);
//	  
//	  double alpha = (currV3d.x * viewP3d.x + currV3d.y * viewP3d.y + currV3d.z
//				* viewP3d.z)
//				/ (cursorP3d.x * viewP3d.x + cursorP3d.y * viewP3d.y
//						+ cursorP3d.z + viewP3d.z);
//	  
//	  currV3d.scaleAdd(alpha, cursorP3d, viewP3d);
//	  currV4d.set(currV3d.x, currV3d.y, currV3d.z, 1);
//	  inverseTG.transform(currV4d);
//	  currV3d.set(currV4d.x, currV4d.y, currV4d.z);
//	  currT3d.setTranslation(currV3d);
//	  currentTG.setTransform(currT3d);
	  
	  
  }

public void transformChanged(int type, Transform3D transform) {
	// TODO Auto-generated method stub
	
}

public void setNodeType(int type) { nodeType = type; }
public int getNodeType() { return nodeType; }

public WrapSpheres3D getWrapSpheres3D(){
	return ws3;
}

public void setUsetupCallback(MouseSphereColorChangeCallback callback2) {
	// TODO Auto-generated method stub
	
}

  /**
	 * Callback method from MouseZoom This is used when the Picking callback is
	 * enabled
	 */
//  public void transformChanged( int type, Transform3D transform ) {
//      callback.transformChanged( PickingCallback.ZOOM, currentTG );
//  }
 
  /**
    * Register the class @param callback to be called each
    * time the picked object moves
    */

}

