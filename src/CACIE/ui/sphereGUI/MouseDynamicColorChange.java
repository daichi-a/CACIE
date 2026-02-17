package CACIE.ui.sphereGUI;




import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;

/**
 * MouseTranslate is a Java3D behavior object that lets users control the 
 * translation (X, Y) of an object via a mouse drag motion with the third
 * mouse button (alt-click on PC). See MouseRotate for similar usage info.
 */
 
public class MouseDynamicColorChange extends MouseBehavior {

    double x_factor = .02;
    double y_factor = .02;
    Vector3d translation = new Vector3d();
    
    private WakeupCondition timeOut;
    private int timeDelay;
    private Spheres3D cellsGrid;
    private Sphere3D sphere;
    private BranchGroup m_root;

    private MouseBehaviorCallback callback = null;
    private ColorInterpolator cinterp = null;

    Color3f red = new Color3f(Color.RED);
    Color3f black = new Color3f(Color.BLACK);
    Color3f white = new Color3f(Color.WHITE);
    Color3f blue = new Color3f(Color.BLUE);
    Color3f gray = new Color3f(0.4f, 0.4f, 0.4f);
    private Color3f scolor = new Color3f(1.0f, 0.0f, 0.0f);
    private Color3f ecolor = new Color3f(0.0f, 0.0f, 1.0f);
    /**
     * Creates a mouse translate behavior given the transform group.
     * @param transformGroup The transformGroup to operate on.
     */
    public MouseDynamicColorChange(TransformGroup transformGroup) {
	super(transformGroup);

    }
   
    /**
     * Creates a default translate behavior.
     */
    public MouseDynamicColorChange(){
	super(0);
    }

    /**
     * Creates a translate behavior.
     * Note that this behavior still needs a transform
     * group to work on (use setTransformGroup(tg)) and
     * the transform group must add this behavior.
     * @param flags
     */
    public MouseDynamicColorChange(BranchGroup root, int flags, Spheres3D cd) {
	super(flags);
	m_root = root;
	cellsGrid = cd;
    }

    /**
     * Creates a translate behavior that uses AWT listeners and behavior
     * posts rather than WakeupOnAWTEvent.  The behavior is added to the
     * specified Component. A null component can be passed to specify
     * the behavior should use listeners.  Components can then be added
     * to the behavior with the addListener(Component c) method.
     * @param c The Component to add the MouseListener
     * and MouseMotionListener to.
     * @since Java 3D 1.2.1
     */
    public MouseDynamicColorChange(Component c) {
	super(c, 0);

    } 

    /**
     * Creates a translate behavior that uses AWT listeners and behavior
     * posts rather than WakeupOnAWTEvent.  The behaviors is added to
     * the specified Component and works on the given TransformGroup.
     * A null component can be passed to specify the behavior should use
     * listeners.  Components can then be added to the behavior with the
     * addListener(Component c) method.
     * @param c The Component to add the MouseListener and
     * MouseMotionListener to.
     * @param transformGroup The TransformGroup to operate on.
     * @since Java 3D 1.2.1
     */
    public MouseDynamicColorChange(Component c, TransformGroup transformGroup) {
	super(c, transformGroup);
    }

    /**
     * Creates a translate behavior that uses AWT listeners and behavior
     * posts rather than WakeupOnAWTEvent.  The behavior is added to the
     * specified Component.  A null component can be passed to specify
     * the behavior should use listeners.  Components can then be added to
     * the behavior with the addListener(Component c) method.
     * Note that this behavior still needs a transform
     * group to work on (use setTransformGroup(tg)) and the transform
     * group must add this behavior.
     * @param flags interesting flags (wakeup conditions).
     * @since Java 3D 1.2.1
     */
    public MouseDynamicColorChange(Component c, int flags) {
	super(c, flags);
    }

    public void initialize() {
	super.initialize();
	if ((flags & INVERT_INPUT) == INVERT_INPUT) {
	    invert = true;
	    x_factor *= -1;
	    y_factor *= -1;
	}
    }
    
    /**
     * Return the x-axis movement multipler.
     **/
    public double getXFactor() {
	return x_factor;
    }
  
    /**
     * Return the y-axis movement multipler.
     **/
    public double getYFactor() {
	return y_factor;
    }
  
    /**
     * Set the x-axis amd y-axis movement multipler with factor.
     **/
    public void setFactor( double factor) {
	x_factor = y_factor = factor;
    }
  
    /**
     * Set the x-axis amd y-axis movement multipler with xFactor and yFactor
     * respectively.
     **/
    public void setFactor( double xFactor, double yFactor) {
	x_factor = xFactor;
	y_factor = yFactor;    
    }

    public void processStimulus (Enumeration criteria) {
	WakeupCriterion wakeup;
	AWTEvent[] events;
 	MouseEvent evt;
// 	int id;
// 	int dx, dy;
    
	while (criteria.hasMoreElements()) {
	    wakeup = (WakeupCriterion) criteria.nextElement();
      
	    if (wakeup instanceof WakeupOnAWTEvent) {
		events = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
		if (events.length > 0) {
		    evt = (MouseEvent) events[events.length-1];
		    doProcess(evt);
		}
	    }

	    else if (wakeup instanceof WakeupOnBehaviorPost) {
		while (true) {
		    // access to the queue must be synchronized
		    synchronized (mouseq) {
			if (mouseq.isEmpty()) break;
			evt = (MouseEvent)mouseq.remove(0);
			// consolodate MOUSE_DRAG events
			while ((evt.getID() == MouseEvent.MOUSE_DRAGGED) &&
			       !mouseq.isEmpty() &&
			       (((MouseEvent)mouseq.get(0)).getID() ==
				MouseEvent.MOUSE_DRAGGED)) {
			    evt = (MouseEvent)mouseq.remove(0);
			}
		    }
		    doProcess(evt);
		}
	    }

	}
 	wakeupOn(mouseCriterion);
    }

    void doProcess(MouseEvent evt) {
	int id;
	int dx, dy;

	processMouseEvent(evt);
	
	if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
	    ((wakeUp)&&((flags & MANUAL_WAKEUP) != 0))){
	    id = evt.getID();
	    if ((id == MouseEvent.MOUSE_DRAGGED) &&
		!evt.isAltDown() && !evt.isMetaDown()) {
	    	for(int i = 0; i < 2; i++){
	        cellsGrid.update();
	    	}
	        
	        //Create TransformGroup to interpolate the color of spheres
//	        Alpha calpha = new Alpha();
//	        calpha.setTriggerTime(0);
//	        calpha.setPhaseDelayDuration(0);
//	        calpha.setIncreasingAlphaDuration(1000);
//	        calpha.setIncreasingAlphaRampDuration(0);
//	        calpha.setAlphaAtOneDuration(0);
//	        calpha.setAlphaAtZeroDuration(0);
//	        calpha.setDecreasingAlphaDuration(0);
//	        calpha.setDecreasingAlphaRampDuration(0);
//	        
//	        Appearance capp = createColorAppearance();
//	        cinterp = createColorInterpolator(calpha, capp.getMaterial());
//	        cinterp.setStartColor(scolor);
//	        cinterp.setEnable(true);
//	        m_root.addChild(cinterp);
		
//		x = evt.getX();
//		y = evt.getY();
//		
//		dx = x - x_last;
//		dy = y - y_last;
//		
//		if ((!reset) && ((Math.abs(dy) < 50) && (Math.abs(dx) < 50))) {
//		    //System.out.println("dx " + dx + " dy " + dy);
//		    transformGroup.getTransform(currXform);
//		    
//		    translation.x = dx*x_factor; 
//		    translation.y = -dy*y_factor;
//		    
//		    transformX.set(translation);
//		    
//		    if (invert) {
//			currXform.mul(currXform, transformX);
//		    } else {
//			currXform.mul(transformX, currXform);
//		    }
//		    
//		    transformGroup.setTransform(currXform);
//		    
//		    transformChanged( currXform );
//		    
//		    if (callback!=null)
//			callback.transformChanged( MouseBehaviorCallback.TRANSLATE,
//						   currXform );
//		    
//		}
//		else {
//		    reset = false;
//		}
//		x_last = x;
//		y_last = y;
//	    }
//	    else if (id == MouseEvent.MOUSE_PRESSED) {
//		x_last = evt.getX();
//		y_last = evt.getY();
	    }
	}
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
    
    private ColorInterpolator createColorInterpolator(Alpha alpha, Material mat){
  	  ColorInterpolator cinterp = 
  		  new ColorInterpolator(alpha, mat, scolor, ecolor);
  	  cinterp.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
  	  return cinterp;
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

