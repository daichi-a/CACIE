package CACIE.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import CACIE.eventlist.CommonEventList;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.Notes;
import CACIE.genome.OneNote;
import CACIE.genome.TreeNodes;

/** Deterministic 96px representation of a GP tree and its audible phenotype. */
public final class TreeQuiltIcon {
  public static final int SIZE=96, QUILT_HEIGHT=84, PIANO_HEIGHT=12;
  private static final int NODE_BAND=3;
  private TreeQuiltIcon() {}

  public static ImageIcon create(Motif_simpleTree_Individual individual,CommonEventList playback){return new ImageIcon(render(individual,playback));}
  public static BufferedImage render(Motif_simpleTree_Individual individual,CommonEventList playback){
    BufferedImage image=new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_ARGB);Graphics2D g=image.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.setColor(new Color(28,31,36));g.fillRect(0,0,SIZE,SIZE);
    Parse parse=new Parse(individual);Node root=parse.node();if(root!=null)paintNode(g,root,1,1,SIZE-2,QUILT_HEIGHT-2,0,individual);
    paintPianoRoll(g,playback,0,QUILT_HEIGHT,SIZE,PIANO_HEIGHT);g.setColor(new Color(30,35,42));g.drawRect(0,0,SIZE-1,SIZE-1);g.dispose();return image;
  }

  private static void paintNode(Graphics2D g,Node n,int x,int y,int w,int h,int depth,Motif_simpleTree_Individual individual){
    if(w<=0||h<=0)return;Color color=colorFor(key(n.node,individual));g.setColor(color);
    if(n.children.isEmpty()||w<5||h<5){g.fillRect(x,y,w,h);g.setColor(color.darker());g.drawRect(x,y,Math.max(0,w-1),Math.max(0,h-1));return;}
    g.fillRect(x,y,w,Math.min(NODE_BAND,h));int cy=y+Math.min(NODE_BAND,h),ch=h-Math.min(NODE_BAND,h);if(ch<=0)return;
    if(n.children.size()==1){paintNode(g,n.children.get(0),x,cy,w,ch,depth+1,individual);return;}
    Node a=n.children.get(0),b=n.children.get(1);int total=Math.max(2,a.size+b.size);
    if((depth&1)==0){int aw=Math.max(1,w*a.size/total);paintNode(g,a,x,cy,aw,ch,depth+1,individual);paintNode(g,b,x+aw,cy,w-aw,ch,depth+1,individual);}
    else{int ah=Math.max(1,ch*a.size/total);paintNode(g,a,x,cy,w,ah,depth+1,individual);paintNode(g,b,x,cy+ah,w,ch-ah,depth+1,individual);}
  }

  private static String key(TreeNodes node,Motif_simpleTree_Individual individual){
    if(node.getTermOrNot()==TreeNodes.TERMINAL||node.getTermOrNot()==TreeNodes.RECURSIVENODE){
      Notes notes=individual.getTerminalNotes(node.getData());StringBuilder s=new StringBuilder("T:");for(int i=0;i<notes.getNumOfNotes();i++){OneNote n=notes.getNote(i);s.append(n.getNoteNumber()).append('@').append(n.getPosition()).append('+').append(n.getDuration()).append(';');}return s.toString();
    } return "F:"+node.getOperatorAsString();
  }
  private static Color colorFor(String text){try{byte[] h=MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));float hue=(((h[0]&255)<<8)|(h[1]&255))/65535f;float sat=.55f+(h[2]&255)/255f*.25f;float bri=.58f+(h[3]&255)/255f*.20f;return Color.getHSBColor(hue,sat,bri);}catch(Exception e){return Color.GRAY;}}
  private static void paintPianoRoll(Graphics2D g,CommonEventList list,int x,int y,int w,int h){g.setColor(new Color(17,20,25));g.fillRect(x,y,w,h);if(list==null||list.getNumOfNotes()==0)return;long end=1;int lo=127,hi=0;for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);end=Math.max(end,n.getPosition()+n.getDuration());lo=Math.min(lo,n.getNoteNumber());hi=Math.max(hi,n.getNoteNumber());}int range=Math.max(1,hi-lo+1);g.setColor(new Color(218,232,244));for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);int nx=x+(int)(n.getPosition()*(w-1)/end),nw=Math.max(1,(int)(n.getDuration()*(w-1)/end));int ny=y+h-1-(n.getNoteNumber()-lo)*(h-2)/range;g.fillRect(nx,ny,Math.min(nw,w-nx),1);}g.setColor(new Color(70,78,88));g.drawLine(x,y,x+w-1,y);}

  private static final class Node{final TreeNodes node;final List<Node> children=new ArrayList<Node>();int size=1;Node(TreeNodes n){node=n;}}
  private static final class Parse{final Motif_simpleTree_Individual individual;int index;Parse(Motif_simpleTree_Individual i){individual=i;}Node node(){if(index>=individual.getNumOfNodes())return null;TreeNodes t=individual.getNode(index++);Node n=new Node(t);int children=Math.max(0,1-t.getStackCount());for(int i=0;i<children;i++){Node c=node();if(c!=null){n.children.add(c);n.size+=c.size;}}return n;}}
}
