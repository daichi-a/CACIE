package CACIE.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import CACIE.genome.Motif_simpleTree_Individual;

/** One copy-oriented individual location in the Breeding interface. */
public final class BreedingIndividualSlot extends JLabel {
  public enum SlotType { INITIALIZED, PARENT, STORAGE, OFFSPRING, PLAYBACK }
  public interface ImportListener { void individualImported(BreedingIndividualSlot slot,Motif_simpleTree_Individual individual); }
  private final SlotType slotType; private final int slotIndex; private final boolean acceptsDrop;
  private Motif_simpleTree_Individual individual; private ImportListener importListener;
  private int pressX,pressY; private boolean dragging;

  public BreedingIndividualSlot(SlotType type,int index,boolean acceptsDrop){
    super("Empty",SwingConstants.CENTER);slotType=type;slotIndex=index;this.acceptsDrop=acceptsDrop;
    setOpaque(true);setFocusable(true);setBackground(new Color(205,211,218));setBorder(BorderFactory.createLineBorder(new Color(80,91,104)));setPreferredSize(new Dimension(104,104));
    setTransferHandler(new IndividualTransferHandler());
    addMouseListener(new MouseAdapter(){public void mousePressed(MouseEvent e){pressX=e.getX();pressY=e.getY();dragging=false;}public void mouseReleased(MouseEvent e){dragging=false;}});
    addMouseMotionListener(new MouseMotionAdapter(){public void mouseDragged(MouseEvent e){if(individual==null||dragging)return;int dx=e.getX()-pressX,dy=e.getY()-pressY;if(dx*dx+dy*dy>=36){dragging=true;getTransferHandler().exportAsDrag(BreedingIndividualSlot.this,e,TransferHandler.COPY);}}});
  }
  public SlotType getSlotType(){return slotType;} public int getSlotIndex(){return slotIndex;} public boolean acceptsDrop(){return acceptsDrop;}
  public Motif_simpleTree_Individual getIndividual(){return individual;}
  public void setIndividual(Motif_simpleTree_Individual value){individual=value;if(value==null){setIcon(null);setText("Empty");setToolTipText(null);} }
  public void setImportListener(ImportListener listener){importListener=listener;}
  void importCopy(Motif_simpleTree_Individual source){Motif_simpleTree_Individual copy=(Motif_simpleTree_Individual)source.clone();setIndividual(copy);if(importListener!=null)importListener.individualImported(this,copy);}
}
