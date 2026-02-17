package CACIE.ui.sphereGUI;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickCanvas;


/**
 * MouseZoom is a Java3D behavior object that lets users control the 
 * Z axis translation of an object via a mouse drag motion with the second
 * mouse button. See MouseRotate for similar usage info.
 */
 
public class MouseColor extends MouseBehavior {
	int height = 600;
	
    double r_factor = .04;
    Vector3d translation = new Vector3d();
    Vector3d currXV3d = new Vector3d();
    Vector4d pastXV4d = new Vector4d();
    double slope = 0;
    double distance;
    int fitness = (int)((double)(distance / (height / 2)) * 100); //fitness: 0< < 100
    double xpos, ypos;
    Point3d currP3d = new Point3d(), viewP3d = new Point3d();
    private PickCanvas pickCanvas;
    private Transform3D plate2world = new Transform3D();
    Primitive[] shapes;
    
    Color3f red = new Color3f(Color.RED);
    Color3f black = new Color3f(Color.BLACK);
    Color3f white = new Color3f(Color.WHITE);
    Color3f blue = new Color3f(Color.BLUE);
    Material redMat = new Material(red, black, red, white, 75.0f);
    Material blueMat = new Material(blue, black, blue, white, 75.0f);
    Material material;
    Primitive obj;
  
    private MouseBehaviorCallback callback = null;

    /**
     * Creates a zoom behavior given the transform group.
     * @param transformGroup The transformGroup to operate on.
     */
    public MouseColor(TransformGroup transformGroup, Primitive obj) {
	super(transformGroup);
	this.obj = obj;
    }

    /**
     * Creates a default mouse zoom behavior.
     **/
    public MouseColor(){
	super(0);
    }

    /**
     * Creates a zoom behavior.
     * Note that this behavior still needs a transform
     * group to work on (use setTransformGroup(tg)) and
     * the transform group must add this behavior.
     * @param flags
     */
    public MouseColor(int flags, Primitive obj) {
	super(flags);
	this.material = material;
	this.obj = obj;
    }

    /**
     * Creates a zoom behavior that uses AWT listeners and behavior
     * posts rather than WakeupOnAWTEvent.  The behavior is added to the
     * specified Component.  A null component can be passed to specify
     * the behavior should use listeners.  Components can then be added
     * to the behavior with the addListener(Component c) method.
     * @param c The Component to add the MouseListener
     * and MouseMotionListener to.
     * @since Java 3D 1.2.1
     */
    public MouseColor(Component c, Primitive obj) {
	super(c, 0);
	this.obj = obj;
    }

    /**
     * Creates a zoom behavior that uses AWT listeners and behavior
     * posts rather than WakeupOnAWTEvent.  The behaviors is added to
     * the specified Component and works on the given TransformGroup.
     * @param c The Component to add the MouseListener and
     * MouseMotionListener to.  A null component can be passed to specify
     * the behavior should use listeners.  Components can then be added
     * to the behavior with the addListener(Component c) method.
     * @param transformGroup The TransformGroup to operate on.
     * @since Java 3D 1.2.1
     */
    public MouseColor(Component c, TransformGroup transformGroup, Primitive obj) {
	super(c, transformGroup);
	this.obj = obj;
    }

    /**
     * Creates a zoom behavior that uses AWT listeners and behavior
     * posts rather than WakeupOnAWTEvent.  The behavior is added to the
     * specified Component.  A null component can be passed to specify
     * the behavior should use listeners.  Components can then be added
     * to the behavior with the addListener(Component c) method.
     * Note that this behavior still needs a transform
     * group to work on (use setTransformGroup(tg)) and the transform
     * group must add this behavior.
     * @param flags interesting flags (wakeup conditions).
     * @since Java 3D 1.2.1
     */
    public MouseColor(Component c, int flags, Primitive obj) {
	super(c, flags);
	this.obj = obj;
    }

    public void initialize() {
	super.initialize();
	
	if ((flags & INVERT_INPUT) == INVERT_INPUT) {
	    r_factor *= -1;
	    invert = true;
	}
	
	/*
	currP3d.x = 1.0;
	currP3d.y = 1.0;
	currP3d.z = 1.0;
	System.out.println("cursor = " + currP3d);
	*/
    }
    
    /**
     * Return the y-axis movement multipler.
     **/
    public double getFactor() {
	return r_factor;
    }
  
    /**
     * Set the y-axis movement multipler with factor.
     **/
    public void setFactor( double factor) {
    	r_factor = factor;
    }
    
    public double getSlope(){
    	return slope;
    }
    
    public void setSlope(double _slope){
    	if(_slope < 5.0 && _slope > -5.0){
    		slope = _slope;
    		this.setFactor(.04);
    	} else {
    		slope = _slope;
    		this.setFactor(.001);
    	}
    }
    
    public double getDistance(){
    	return distance;
    }
    
    public void setDistance(double _distance){
    		distance = _distance;
    }
    
    public double getX(){
    	return xpos;
    }
    
    public void setX(double _xpos){
    		xpos = _xpos;
    }
    
    public double getY(){
    	return ypos;
    }
    
    public void setY(double _ypos){
    		ypos = _ypos;
    }
    
    public int getFitness(){
    	return fitness;
    }
    
    public void setFitness(int _fitness){
    		fitness = _fitness;
    }
    
    public Material getMaterial(){
    	return material;
    }
  

    public void processStimulus (Enumeration criteria) {
	WakeupCriterion wakeup;
	AWTEvent[] events;
 	MouseEvent evt;
 	
 	//int xpos = 0, ypos = 0;
// 	int id;
// 	int dx, dy;
 	
    transformGroup.getTransform(currXform);
    currXform.get(currXV3d);
	//System.out.println("mouse x: " + x);
	//System.out.println("mouse y: " + y);
	//System.out.println("current x: " + currXV3d.x);
	//System.out.println("current y: " + currXV3d.y);
	//System.out.println("current z: " + currXV3d.z);
	
    
	while (criteria.hasMoreElements()) {
	    wakeup = (WakeupCriterion) criteria.nextElement();
	    if (wakeup instanceof WakeupOnAWTEvent) {
		events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();

		if (events.length > 0) {
		    evt = (MouseEvent) events[events.length-1];
			//xpos = evt.getPoint().x;
			//ypos = evt.getPoint().y;
			//System.out.println("xpos: " + xpos);
			//System.out.println("ypos: " + ypos);
		    doProcess(evt);
		}

		
	    }

	    else if (wakeup instanceof WakeupOnBehaviorPost) {
		while (true) {
		    synchronized (mouseq) {
			if (mouseq.isEmpty()) break;
			evt = (MouseEvent)mouseq.remove(0);
			//xpos = evt.getPoint().x;
			//ypos = evt.getPoint().y;
			//System.out.println("xpos: " + xpos);
			//System.out.println("ypos: " + ypos);
			// consolodate MOUSE_DRAG events
			while((evt.getID() == MouseEvent.MOUSE_DRAGGED) &&
			      !mouseq.isEmpty() &&
			      (((MouseEvent)mouseq.get(0)).getID() ==
			       MouseEvent.MOUSE_DRAGGED)) {
			    evt = (MouseEvent)mouseq.remove(0);
			    //xpos = evt.getPoint().x;
				//ypos = evt.getPoint().y;
				//System.out.println("xpos: " + xpos);
				//System.out.println("ypos: " + ypos);
			}
		    }
		    doProcess(evt);
		}
	    }
	    
	}
	wakeupOn (mouseCriterion);
    }

    void doProcess(MouseEvent evt) {
	int id;
	double dx, dy;
	
	processMouseEvent(evt);
	
	if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
	    ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){
	    id = evt.getID();
	    if ((id == MouseEvent.MOUSE_DRAGGED) &&
		evt.isAltDown() && !evt.isMetaDown()){
		
			x = evt.getX();
			y = evt.getY();
			
			//System.out.println("x: " + x);
			//System.out.println("y: " + y);
		
		//dx = x - x_last;
		dy = y - y_last;
		

	    material = blueMat;
	    Appearance app = new Appearance();
	    app.setMaterial(material);
		obj.setAppearance(app);
		//xpos += - dy;
		//ypos -= dy;
		//distance =  Math.sqrt(xpos * xpos + ypos * ypos);
		
		//System.out.println("dx: " + dx);
		//System.out.println("dy: " + dy);
		//System.out.println("distance: " + distance);
		
		//slope = (double)(y / x) ;
		//System.out.println("slope: " + slope);
		/*
		currentTG.getTransform(currT3d);
		currT3d.get(currV3d);
		System.out.println("current x: " + currV3d.x);
		System.out.println("current y: " + currV3d.y);
		System.out.println("current z: " + currV3d.z);
		*/
		if (!reset){
		   
		    
		    /*
		    transformGroup.getTransform(currXform);
		    currXform.get(currXV3d);
			System.out.println("mouse x: " + x);
			System.out.println("mouse y: " + y);
			System.out.println("current x: " + currXV3d.x);
			System.out.println("current y: " + currXV3d.y);
			System.out.println("current z: " + currXV3d.z);
		    */
			
		    app.setMaterial(material);
			obj.setAppearance(app);

			translation.x = dy * r_factor;
			translation.y = slope * dy * r_factor;
			
			



			//System.out.println("translation.x: " + translation.x);
			//System.out.println("translation.y: " + translation.y);
			//System.out.println("xpos@doProcess: " + xpos);
			//System.out.println("ypos@doProcess: " + ypos);

			//translation.y = slope * dx;
		    //translation.z  = dy*r_factor;
		    transformX.set(translation);
		    if (invert) {
			currXform.mul(currXform, transformX);
		    } else {
			currXform.mul(transformX, currXform);
		    }
		    
		    transformGroup.setTransform(currXform);
		    
		    transformChanged( currXform );
		    
		    if (callback!=null)
			callback.transformChanged( MouseBehaviorCallback.ZOOM,
						   currXform );
		    
		}
		else {
		    reset = false;
		}
		
		x_last = x;
		y_last = y;
	    }
	    else if (id == MouseEvent.MOUSE_PRESSED) {
		x_last = evt.getX();
		y_last = evt.getY();
	    }
	}
    }

    
  /**
    * Users can overload this method  which is called every time
    * the Behavior updates the transform
    *
    * Default implementation does nothing
    */
  public void transformChanged( Transform3D transform ) {
  }
 
  /**
    * The transformChanged method in the callback class will
    * be called every time the transform is updated
    */
  public void setupCallback( MouseBehaviorCallback callback ) {
      this.callback = callback;
  }
}

