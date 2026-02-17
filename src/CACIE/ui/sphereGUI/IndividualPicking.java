package CACIE.ui.sphereGUI;


import javax.media.j3d.*;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.PickResult;
//import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.behaviors.picking.*; //

public class IndividualPicking extends PickMouseBehavior {
  protected IndividualPickingCallback callback = null;
  protected int pickMode = PickTool.BOUNDS;
  protected int nodeType = PickResult.SHAPE3D;
  private SphereProperties sphereProps;
  int width;
  int height;
  //SphereIndividual data = null;

  public IndividualPicking(BranchGroup root, Canvas3D canvas, Bounds bounds) {
    super(canvas, root, bounds);


    this.setSchedulingBounds(bounds);
  }

  public IndividualPicking( BranchGroup root, Canvas3D canvas, Bounds bounds,
		        int mode)
  {
    super(canvas, root, bounds);
    pickMode = mode;

    this.setSchedulingBounds(bounds);
  }

  public IndividualPicking( BranchGroup root, Canvas3D canvas, Bounds bounds,
		        int mode, int type, SphereProperties sps )
  {
    super(canvas, root, bounds);
    pickMode = mode;
    nodeType = type;
    sphereProps = sps;
    width = sphereProps.getWidth();
    height = sphereProps.getHeight();

    this.setSchedulingBounds(bounds);
  }

  public void setPickMode(int mode) { pickMode = mode; }
  public int getPickMode() { return pickMode; }

  public void setNodeType(int type) { nodeType = type; }
  public int getNodeType() { return nodeType; }

  public void setupCallback(IndividualPickingCallback callback) { this.callback = callback; }

  public void updateScene(int x, int y) {
//	    width = 800;
//	    height = 600;
	    int dd =  (x - width / 2) * (x - width / 2) + 
	    	(y - height / 2) * (y - height / 2);
	    int d = (int) Math.sqrt((double)dd);
	    double f = d / (double)(height / 2) * 100.0;
	    int fitness = (int)f;
	    if(fitness >= 100){
	    	fitness = 100;
	    }
	    if(fitness <= 0){
	    	fitness = 0;
	    }
	    
	    fitness = 100 - fitness;
	  
	    /*
    pickCanvas.setShapeLocation(x, y);
    PickResult pickResult = pickCanvas.pickClosest();

    //System.out.println(pickResult);//DEBUG
    Node node = pickResult.getNode(nodeType);
    */
	    Node node = pickScene.pickNode(pickScene.pickClosest(x, y), nodeType);
	    
    if(node != null){
//    	System.out.println("pickScene.pickClosest(x, y)" + pickScene.pickClosest(x, y));
//    	System.out.println("nodeType" + nodeType);
//    	SphereIndividual data;
//    	data = (SphereIndividual) node.getUserData();
//    	System.out.println("node: " + node);
//    	System.out.println("nodeType: " + nodeType);
//  		System.out.println("getUserData: " + data);
//  		if(data != null){
//  			
//    	data.setX((double)(x - width / 2));
//    	data.setY((double)(y - height / 2));
//    	data.setDistance(d);
//    	data.setFitness(fitness);
//    	node.setUserData(data);
//  		System.out.println("Index@SimplePicking: " + data.index);
//  		System.out.println("Fitness@SimplePicking: " + data.fitness);
//  		}
    }
    callback.picked(nodeType, node);
  }
}
