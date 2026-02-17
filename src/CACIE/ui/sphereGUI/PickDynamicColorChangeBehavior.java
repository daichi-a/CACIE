package CACIE.ui.sphereGUI;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.picking.PickMouseBehavior;
import com.sun.j3d.utils.behaviors.picking.PickObject;
import com.sun.j3d.utils.behaviors.picking.PickingCallback;
import com.sun.j3d.utils.picking.PickResult;

//A mouse behavior that allows user to pick and translate scene graph objects.
//Common usage: 1. Create your scene graph. 2. Create this behavior with
//the root and canvas. See PickRotateBehavior for more details. 

/**
 * @deprecated As of Java 3D version 1.2, replaced by
 *             <code>com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior</code>
 * 
 * @see com.sun.j3d.utils.picking.behaviors.PickTranslateBehavior
 */

public class PickDynamicColorChangeBehavior extends PickMouseBehavior implements MouseBehaviorCallback
{
	MouseDynamicColorChange translate;
	int pickMode = PickObject.USE_BOUNDS;
	private PickingCallback callback = null;
	private TransformGroup currentTG;
    private Spheres3D cg;
    protected int nodeType = PickResult.SHAPE3D;
    SphereIndividual sind;
    private Sphere3D sphere;

	/**
	 * Creates a pick/translate behavior that waits for user mouse events for the
	 * scene graph. This method has its pickMode set to BOUNDS picking.
	 * 
	 * @param root
	 *          Root of your scene graph.
	 * @param canvas
	 *          Java 3D drawing canvas.
	 * @param bounds
	 *          Bounds of your scene.
	 */

	public PickDynamicColorChangeBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds, Spheres3D cd)
	{
		super(canvas, root, bounds);
		cg = cd;
		translate = new MouseDynamicColorChange(root, MouseBehavior.MANUAL_WAKEUP, cg);
		translate.setTransformGroup(currGrp);
		currGrp.addChild(translate);
		translate.setSchedulingBounds(bounds);
		this.setSchedulingBounds(bounds);
		
	}

	/**
	 * Creates a pick/translate behavior that waits for user mouse events for the
	 * scene graph.
	 * 
	 * @param root
	 *          Root of your scene graph.
	 * @param canvas
	 *          Java 3D drawing canvas.
	 * @param bounds
	 *          Bounds of your scene.
	 * @param pickMode
	 *          specifys PickObject.USE_BOUNDS or PickObject.USE_GEOMETRY. Note:
	 *          If pickMode is set to PickObject.USE_GEOMETRY, all geometry object
	 *          in the scene graph that allows pickable must have its
	 *          ALLOW_INTERSECT bit set.
	 */

	public PickDynamicColorChangeBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds,Spheres3D cd, int pickMode)
	{
		super(canvas, root, bounds);
		cg = cd;
		translate = new MouseDynamicColorChange(root, MouseBehavior.MANUAL_WAKEUP, cd);
		translate.setTransformGroup(currGrp);
		currGrp.addChild(translate);
		translate.setSchedulingBounds(bounds);
		this.setSchedulingBounds(bounds);
		this.pickMode = pickMode;
//	    nodeType = type;
	    sind = new SphereIndividual();
	}

	/**
	 * Sets the pickMode component of this PickTranslateBehavior to the value of
	 * the passed pickMode.
	 * 
	 * @param pickMode
	 *          the pickMode to be copied.
	 */

	public void setPickMode(int pickMode)
	{
		this.pickMode = pickMode;
	}

	/**
	 * Return the pickMode component of this PickTranslaeBehavior.
	 */

	public int getPickMode()
	{
		return pickMode;
	}

	/**
	 * Update the scene to manipulate any nodes. This is not meant to be called by
	 * users. Behavior automatically calls this. You can call this only if you
	 * know what you are doing.
	 * 
	 * @param xpos
	 *          Current mouse X pos.
	 * @param ypos
	 *          Current mouse Y pos.
	 */
	public void updateScene(int xpos, int ypos)
	{
		TransformGroup tg = null;

		if (!mevent.isAltDown() && !mevent.isMetaDown())
		{

			tg = (TransformGroup) pickScene.pickNode(pickScene.pickClosest(xpos, ypos, pickMode), PickObject.TRANSFORM_GROUP);
			// Check for valid selection.
		      Node node = pickScene.pickNode(pickScene.pickClosest(xpos, ypos), nodeType);
		      if(node != null){
		    	  sind = (SphereIndividual) node.getUserData();
		      }
			if ((tg != null) && (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ))
					&& (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE)))
			{

				translate.setTransformGroup(tg);
				translate.wakeup();
				currentTG = tg;
		    	SphereIndividual sind = (SphereIndividual) tg.getUserData(); 
			} else if (callback != null)
				callback.transformChanged(PickingCallback.NO_PICK, null);
		}
		//System.out.println( "updateScene" );
//		System.out.println("xpos: " + xpos);
//		System.out.println("ypos: " + ypos);
	


	    	
//	    	zoom.setSlope(- ((double)(ypos - m_height / 2) / (double)(xpos - m_width / 2)));
//	    	zoom.setDistance(d);
//	    	zoom.setX((double)(xpos - m_width / 2));
//	    	zoom.setY(- (double)(ypos - m_height / 2));
	    	
	
		
		
		
	}

	/**
	 * Callback method from MouseTranslate This is used when the Picking callback
	 * is enabled
	 */
	public void transformChanged(int type, Transform3D transform)
	{
		callback.transformChanged(PickingCallback.TRANSLATE, currentTG);
		System.out.println( "transformChanged" );
	}

	/**
	 * Register the class
	 * 
	 * @param callback
	 *          to be called each time the picked object moves
	 */
	public void setupCallback(PickingCallback callback)
	{
		System.out.println("setupCallback");
		this.callback = callback;
		if (callback == null)
			translate.setupCallback(null);
		else
			translate.setupCallback(this);
	}
}
