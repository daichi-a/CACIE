package CACIE.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import CACIE.genome.Motif_simpleTree_Individual;

/** Local-JVM, COPY-only transfer of raw GP individuals. */
public final class IndividualTransferHandler extends TransferHandler {
  private static final DataFlavor FLAVOR=new DataFlavor(Payload.class,"CACIE GP Individual");
  protected Transferable createTransferable(JComponent c){BreedingIndividualSlot slot=(BreedingIndividualSlot)c;return slot.getIndividual()==null?null:new Payload(slot.getIndividual());}
  public int getSourceActions(JComponent c){return COPY;}
  public boolean canImport(TransferSupport support){return support.isDrop()&&support.isDataFlavorSupported(FLAVOR)&&support.getComponent() instanceof BreedingIndividualSlot&&((BreedingIndividualSlot)support.getComponent()).acceptsDrop();}
  public boolean importData(TransferSupport support){if(!canImport(support))return false;try{Payload payload=(Payload)support.getTransferable().getTransferData(FLAVOR);((BreedingIndividualSlot)support.getComponent()).importCopy(payload.individual);return true;}catch(Exception e){e.printStackTrace();return false;}}
  private static final class Payload implements Transferable {final Motif_simpleTree_Individual individual;Payload(Motif_simpleTree_Individual i){individual=i;}public DataFlavor[] getTransferDataFlavors(){return new DataFlavor[]{FLAVOR};}public boolean isDataFlavorSupported(DataFlavor f){return FLAVOR.equals(f);}public Object getTransferData(DataFlavor f)throws UnsupportedFlavorException,IOException{if(!isDataFlavorSupported(f))throw new UnsupportedFlavorException(f);return this;}}
}
