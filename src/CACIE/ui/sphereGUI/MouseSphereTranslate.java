package CACIE.ui.sphereGUI;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.picking.PickCanvas;


/**
 * MouseZoom is a Java3D behavior object that lets users control the 
 * Z axis translation of an object via a mouse drag motion with the second
 * mouse button. See MouseRotate for similar usage info.
 */
 
public class MouseSphereTranslate extends MouseBehavior {
	
    double r_factor = .04;
    double acx = 0.01;
    double acy = 0.01;

    Vector3d translation = new Vector3d();
    Vector3d currXV3d = new Vector3d();
    Vector4d pastXV4d = new Vector4d();
    double m_slope;
    double distance;
    //int fitness = (int)((double)(distance / (m_height / 2)) * 100); //fitness: 0< < 100
    double xpos, ypos;
    Point3d currP3d = new Point3d(), viewP3d = new Point3d();
    private PickCanvas pickCanvas;
    private Transform3D plate2world = new Transform3D();
    TransformGroup currTG = null;
    
    private SphereProperties m_sps;
    int m_width;
    int m_height;
    int sphere_num;
    private MouseBehaviorCallback callback = null;
    PickSphereTranslateBehavior ptb;
    SphereIndividual sind1, sind2;
    WrapSpheres3D wrs3;
    /**
     * Creates a zoom behavior given the transform group.
     * @param transformGroup The transformGroup to operate on.
     */
    public MouseSphereTranslate(TransformGroup transformGroup) {
	super(transformGroup);
    }

    /**
     * Creates a default mouse zoom behavior.
     **/
    public MouseSphereTranslate(){
	super(0);
    }

    /**
     * Creates a zoom behavior.
     * Note that this behavior still needs a transform
     * group to work on (use setTransformGroup(tg)) and
     * the transform group must add this behavior.
     * @param flags
     */
    public MouseSphereTranslate(PickSphereTranslateBehavior pt, int flags, SphereProperties sps) {
	super(flags);
	this.ptb = pt;
	m_sps = sps;
	this.wrs3 = pt.getWrapSpheres3D();
    this.m_sps = sps;
    this.m_width = sps.getWidth();
    this.m_height = sps.getHeight();
    this.sphere_num = sps.getNumSpheres();
    sind1 = new SphereIndividual();
    sind2 = new SphereIndividual();
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
    public MouseSphereTranslate(Component c) {
	super(c, 0);
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
    public MouseSphereTranslate(Component c, TransformGroup transformGroup) {
	super(c, transformGroup);
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
    public MouseSphereTranslate(Component c, int flags) {
	super(c, flags);
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
    	return m_slope;
    }
    
    public void setSlope(double slope){
    	if(slope < 5.0 && slope > -5.0){
    		m_slope = slope;
    		this.setFactor(.04);
    	} else if(slope > 100.0){
    		//m_slope = 100.0;
    	} else {
    		m_slope = slope;
    		this.setFactor(.009);
    	}
    		m_slope = slope;

    	
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
    
//    public int getFitness(){
//    	return fitness;
//    }
//    
//    public void setFitness(int _fitness){
//    		fitness = _fitness;
//    }
  

    public void processStimulus (Enumeration criteria) {
	WakeupCriterion wakeup;
	AWTEvent[] events;
 	MouseEvent evt;
 	
 	//int xpos = 0, ypos = 0;
// 	int id;
// 	int dx, dy;
 	
//    transformGroup.getTransform(currXform);
//    currXform.get(currXV3d);
//	System.out.println("mouse x: " + x);
//	System.out.println("mouse y: " + y);
//	System.out.println("current x: " + currXV3d.x);
//	System.out.println("current y: " + currXV3d.y);
//	System.out.println("current z: " + currXV3d.z);
	
    
	while (criteria.hasMoreElements()) {
	    wakeup = (WakeupCriterion) criteria.nextElement();
	    if (wakeup instanceof WakeupOnAWTEvent) {
		events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
		//System.out.println("events: " + events);

		if (events.length > 0) {
		    evt = (MouseEvent) events[events.length-1];
		    //System.out.println("evt: " + evt);
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
	double dx1 = 0.0;
	double dy1 = 0.0;
	double dx0 = 0.0;
	double dy0 = 0.0;
	
	processMouseEvent(evt);
	
	if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
	    ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){
	    id = evt.getID();
	    if ((id == MouseEvent.MOUSE_DRAGGED) &&
		!evt.isAltDown() && !evt.isMetaDown()){
		
			x = evt.getX();
			y = evt.getY();
			//System.out.println("x: " + x);
			//System.out.println("y: " + y);


		dx = (x - x_last) * 0.30;
//		dy = (y - y_last) * 0.30;
		
//		dx0 = dx - dx1;
//		dy0 = dy - dy1;
		
		//xpos += - dy;
		//ypos -= dy;
		//distance =  Math.sqrt(xpos * xpos + ypos * ypos);
		
//		System.out.println("dx: " + dx);
//		System.out.println("dy: " + dy);
//		System.out.println("distance: " + distance);
		
		//m_slope = (double)(y / x) ;
		//System.out.println("m_slope: " + m_slope);
		
//		currentTG.getTransform(currT3d);
//		currT3d.get(currV3d);
//		System.out.println("current x: " + currV3d.x);
//		System.out.println("current y: " + currV3d.y);
//		System.out.println("current z: " + currV3d.z);
		
		if (!reset){
		   
		    
		    
		    transformGroup.getTransform(currXform);
		    currXform.get(currXV3d);
		    double xpos = currXV3d.x;
		    double ypos = currXV3d.y;
		    double rpos = Math.sqrt(xpos * xpos + ypos * ypos);
			double f = rpos / (double) (4.5) * 100.0;
			int fitn = (int) f;
			if (fitn >= 100) {
				fitn = 100;
			}
			if (fitn <= 0) {
				fitn = 0;
			}

			fitn = 100 - fitn;
//			System.out.println("mouse x: " + x);
//			System.out.println("mouse y: " + y);
//			System.out.println("current x: " + currXV3d.x);
//			System.out.println("current y: " + currXV3d.y);
//			System.out.println("current z: " + currXV3d.z);
//			System.out.println("fitness: " + fitn);
		    
			


//			translation.x = dy * r_factor;
//			translation.y = m_slope * dy * r_factor;
			translation.x = dx * r_factor;
			translation.y = m_slope * dx * r_factor;
//			
//			sind1 = (SphereIndividual) ptb.getCurrentTG().getUserData();
//			sind2.setIndex(sind1.getIndex());
//			sind2.setX(sind1.getX() + translation.x);
//			sind2.setY(sind1.getY() + translation.y);
//			sind2.setSlope((sind1.getY() + translation.y) / (sind1.getX() + translation.x));
//		    double dd =  sind2.getX() * sind2.getX() + 
//		    sind2.getY() * sind2.getY();
//		    System.out.println("dd: " + dd);
//			double f = dd / (double) (15.0) * 100.0;
//			int fitness = (int) f;
//			if (fitness >= 100) {
//				fitness = 100;
//			}
//			if (fitness <= 0) {
//				fitness = 0;
//			}
//
//			fitness = 100 - fitness;
//			System.out.println("fitness: " + fitness);
//			sind2.setFitness(fitness);
//			ptb.getCurrentTG().setUserData(sind2);
//			wrs3.setFitness(sind1.getIndex(), fitness);
////			currTG = ptb.getCurrentTG();
//			
//			
//
//			System.out.println("sind x: " + sind2.getX());
//			System.out.println("sind y: " + sind2.getY());
//			
//			
//			System.out.println("translation.x: " + translation.x);
//			System.out.println("translation.y: " + translation.y);
//			System.out.println("xpos@doProcess: " + xpos);
//			System.out.println("ypos@doProcess: " + ypos);
		    
//			translation.x = dy * r_factor;
//			translation.y = m_slope * dy * r_factor;
//			translation.x = dx * r_factor ;
//			translation.y = - dy * r_factor;
			
			sind1 = (SphereIndividual) ptb.getCurrentTG().getUserData();
			int ind = sind1.getIndex();
			sind2.setIndex(sind1.getIndex());
//			sind2.setX(sind1.getTransformedX() + translation.x);
//			sind2.setY(sind1.getTransformedY() + translation.y);
			sind2.setX(sind1.getX() + translation.x);
			sind2.setY(sind1.getY() + translation.y);
			
			  double r = Math.sqrt((sind1.getTransformedX() + translation.x) * 
					  (sind1.getTransformedX() + translation.x)
					  +(sind1.getTransformedY() + translation.y) * 
					  (sind1.getTransformedY() + translation.y));
			  double theta = Math.atan((sind1.getTransformedY() + translation.y)
					  / (sind1.getTransformedX() + translation.x));
			  sind2.setR(r);
			  sind2.setTheta(theta);
			  sind2.setSlope(Math.tan(theta));
			  
			  //flag
//			  for(int i = 0; i< sphere_num; i++){
//				  ptb.getWrapSpheres3D().setBlinkFlag(i, false);
//			  }
//			  ptb.getWrapSpheres3D().setBlinkFlag(ind, true);

//		    double dd =  sind2.getTransformedX() * sind2.getTransformedX() + 
//		    sind2.getTransformedY() * sind2.getTransformedY();
//		    System.out.println("dd: " + dd);
//			double f = dd / (double) (15.0) * 100.0;
//			int fitness = (int) f;
//			if (fitness >= 100) {
//				fitness = 100;
//			}
//			if (fitness <= 0) {
//				fitness = 0;
//			}
//
//			fitness = 100 - fitness;
//			int fitness = sind2.getTransformedFitness();
//			System.out.println("fitness: " + fitness);
			sind2.setFitness(fitn);
			ptb.getCurrentTG().setUserData(sind2);
			wrs3.setFitness(sind1.getIndex(), fitn);
//			currTG = ptb.getCurrentTG();
			
			
//
//			System.out.println("sind x: " + sind2.getX());
//			System.out.println("sind y: " + sind2.getY());
//
//			sind2.PolarUpdate();
//			sind2.CartesianUpdate();
//			System.out.println("sind r: " + sind2.getR());
//			System.out.println("sind theta: " + sind2.getThetaRad());
//			System.out.println("sind theta(Deg): " + sind2.getThetaDeg());
//			System.out.println("sind transformed x: " + sind2.getTransformedX());
//			System.out.println("sind transformed y: " + sind2.getTransformedY());
//			
//			
//			System.out.println("translation.x: " + translation.x);
//			System.out.println("translation.y: " + translation.y);
//			System.out.println("xpos@doProcess: " + xpos);
//			System.out.println("ypos@doProcess: " + ypos);

			//translation.y = m_slope * dx;
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
//		dx1 = dx;
//		dy1 = dy;
//		System.out.println("x_last: " + x_last);
//		System.out.println("y_last: " + y_last);
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

