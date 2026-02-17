package CACIE.ui.sphereGUI;
import javax.media.j3d.Node;

public interface ZoomPickingCallback {
  void picked(int nodeType, Node node);
}