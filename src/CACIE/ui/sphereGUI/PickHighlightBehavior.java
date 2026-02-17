package CACIE.ui.sphereGUI;
import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

class PickHighlightBehavior extends PickMouseBehavior {
	  Appearance savedAppearance = null;

	  Shape3D oldShape = null;
	  //Primitive oldShape = null;
	  //Primitive obj = null;
	  Appearance highlightAppearance;

	  public PickHighlightBehavior(Canvas3D canvas, BranchGroup root,
	      Bounds bounds) {
	    super(canvas, root, bounds);
	    this.setSchedulingBounds(bounds);
	    root.addChild(this);
	    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	    Color3f highlightColor = new Color3f(0.0f, 1.0f, 0.0f);
	    
	    Color3f color = new Color3f();
	    Material material = new Material();
	    material.getDiffuseColor(color);
	    color.z = (float) 0.9;
	    material.setDiffuseColor(color);
	    
	    
	    Material highlightMaterial = new Material(highlightColor, black,
	        highlightColor, white, 80.0f);
	    highlightAppearance = new Appearance();
	    highlightAppearance.setMaterial(material/*new Material(highlightColor, black,
	        highlightColor, white, 80.0f)*/);

	    pickCanvas.setMode(PickTool.BOUNDS);
	  }

	  public void updateScene(int xpos, int ypos) {
	    PickResult pickResult = null;
	    Shape3D shape = null;
	    //Primitive obj = null;

	    pickCanvas.setShapeLocation(xpos, ypos);

	    pickResult = pickCanvas.pickClosest();
	    if (pickResult != null) {
	      shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
	    	//obj = (Primitive) pickResult.getNode(PickResult.PRIMITIVE);
	    }

	    if (oldShape != null) {
	      oldShape.setAppearance(savedAppearance);
	    }
	    if (shape != null/*obj != null*/) {
	    	
	      savedAppearance = shape.getAppearance();
	      oldShape = shape;
	      shape.setAppearance(highlightAppearance);
	    	
	    	/*
		      savedAppearance = obj.getAppearance();
		      oldShape = obj;
		      obj.setAppearance(highlightAppearance);
	    	*/
	    }
	  }
	}
