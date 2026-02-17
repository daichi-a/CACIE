package CACIE.ui.sphereGUI;


//package com.sun.j3d.utils.behaviors.picking;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.picking.PickMouseBehavior;
import com.sun.j3d.utils.behaviors.picking.PickObject;
import com.sun.j3d.utils.behaviors.picking.PickingCallback;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;

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

public class PickColorBehavior extends PickMouseBehavior implements MouseBehaviorCallback {
  MouseColor bcolor;
  int pickMode = PickObject.USE_BOUNDS;
  private PickingCallback callback = null;
  private TransformGroup currentTG;
  private Transform3D localTG = new Transform3D();
  private Transform3D inverseTG = new Transform3D();
  private Transform3D currT3d = new Transform3D();
  Vector3d currV3d = new Vector3d();
  Point3d currP3d = new Point3d();
  private double slope;
  private PickCanvas pickCanvas;
  private Transform3D plate2world = new Transform3D();
  Material material;
  Primitive obj;
  

  /**
   * Creates a pick/bcolor behavior that waits for user mouse events for
   * the scene graph. This method has its pickMode set to BOUNDS picking. 
   * @param root   Root of your scene graph.
   * @param canvas Java 3D drawing canvas.
   * @param bounds Bounds of your scene.
   **/

  public PickColorBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds, Primitive obj){
    super(canvas, root, bounds);
    bcolor = new MouseColor(MouseBehavior.MANUAL_WAKEUP, obj);
    bcolor.setTransformGroup(currGrp);
    currGrp.addChild(bcolor);
    bcolor.setSchedulingBounds(bounds);
    this.setSchedulingBounds(bounds);
    this.obj = obj;
  }

  /**
   * Creates a pick/bcolor behavior that waits for user mouse events for
   * the scene graph.
   * @param root   Root of your scene graph.
   * @param canvas Java 3D drawing canvas.
   * @param bounds Bounds of your scene.
   * @param pickMode specifys PickObject.USE_BOUNDS or PickObject.USE_GEOMETRY.
   * Note: If pickMode is set to PickObject.USE_GEOMETRY, all geometry object in 
   * the scene graph that allows pickable must have its ALLOW_INTERSECT bit set. 
   **/

  public PickColorBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds,
			  int pickMode){
    super(canvas, root, bounds);
    bcolor = new MouseColor(MouseBehavior.MANUAL_WAKEUP, obj);
    bcolor.setTransformGroup(currGrp);
    currGrp.addChild(bcolor);
    bcolor.setSchedulingBounds(bounds);
    this.setSchedulingBounds(bounds);
    this.pickMode = pickMode;  
  }
  
  /**
   * Sets the pickMode component of this PickbcolorBehavior to the value of
   * the passed pickMode.
   * @param pickMode the pickMode to be copied.
   **/  

  public void setPickMode(int pickMode) {
    this.pickMode = pickMode;
  }
  
  
 /**
   * Return the pickMode component of this PickbcolorBehavior.
   **/ 

  public int getPickMode() {
    return pickMode;
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
    
    int width = 800;
    int height = 600;
    int dd =  (xpos - width / 2) * (xpos - width / 2) + 
    	(ypos - height / 2) * (ypos - height / 2);
    int d = (int) Math.sqrt((double)dd);
    
    //object position
    //System.out.println("xpos updateScene: " + xpos);
    //System.out.println("ypos updateScene: " + ypos);
    //System.out.println("distance: " + d); // about(0 < d < height / 2)

      
    if (mevent.isAltDown() && !mevent.isMetaDown()){
	   
      tg =(TransformGroup)pickScene.pickNode(pickScene.pickClosest(xpos, ypos, pickMode),
					     PickObject.TRANSFORM_GROUP);
      
      // Check for valid selection
      if ((tg != null) && 
	  (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ)) && 
	  (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))){

    	bcolor.setTransformGroup(tg);
    	bcolor.wakeup();
    	currentTG = tg;
    	bcolor.setSlope(- ((double)(ypos - height / 2) / (double)(xpos - width / 2)));
    	bcolor.setDistance(d);
    	bcolor.setX((double)(xpos - width / 2));
    	bcolor.setY(- (double)(ypos - height / 2));
    	bcolor.getMaterial();

    	//System.out.println("slope @updateScene: " + bcolor.getSlope());
    	System.out.println("distance @updateScene: " + bcolor.getDistance());
    	System.out.println("xpos @updateScene: " + bcolor.getX());
    	System.out.println("ypos @updateScene: " + bcolor.getY());
	
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
	
	/*
	currentTG.getLocalToVworld(localTG);
    System.out.println(localTG);
    inverse.invert(localTG);
    System.out.println(inverse);
	*/
	
      } else if (callback!=null)
          callback.transformChanged( PickingCallback.NO_PICK, null );
    }
	//System.out.println("xpos: " + xpos);
	//System.out.println("ypos: " + ypos);
  }

  /**
    * Callback method from Mousebcolor
    * This is used when the Picking callback is enabled
    */
  public void transformChanged( int type, Transform3D transform ) {
      callback.transformChanged( PickingCallback.NO_PICK, currentTG );
  }
 
  /**
    * Register the class @param callback to be called each
    * time the picked object moves
    */
  public void setupCallback( PickingCallback callback ) {
      this.callback = callback;
      if (callback==null)
          bcolor.setupCallback( null );
      else
          bcolor.setupCallback( this );
  }
}

