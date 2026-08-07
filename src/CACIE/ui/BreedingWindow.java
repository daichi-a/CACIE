package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Timer;
import javax.swing.JSplitPane;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import CACIE.genome.Notes;
import CACIE.genome.Population;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.eventlist.PlaybackSettings;
import CACIE.eventlist.CommonEventList;
import CACIE.eventlist.ScaleFilter;
import CACIE.eventlist.ScaleType;
import CACIE.genome.OneNote;

/** Main window for the generation-less Breeding workflow. */
public class BreedingWindow extends JFrame {
    private static final int PLAYBACK_SLOT_COUNT = 8;

    private final Population population;
    private final MultiLaneBreedingPanel breedingPanel;
    private final BreedingIndividualSlot[] initializedIcons = new BreedingIndividualSlot[PLAYBACK_SLOT_COUNT];
    private final BreedingIndividualSlot[] storageIcons = new BreedingIndividualSlot[32];
    private final BreedingIndividualSlot[] parentIcons = new BreedingIndividualSlot[2];
    private final BreedingIndividualSlot[] offspringIcons = new BreedingIndividualSlot[32];
    private final Motif_simpleTree_Individual[] initializedIndividuals = new Motif_simpleTree_Individual[PLAYBACK_SLOT_COUNT];
    private final Motif_simpleTree_Individual[] storageIndividuals = new Motif_simpleTree_Individual[32];
    private final JComboBox<BreedingPanel.PlaybackLength> standardLength = new JComboBox<BreedingPanel.PlaybackLength>(BreedingPanel.PlaybackLength.values());
    private final JComboBox<String> standardTonic = new JComboBox<String>(new String[]{"C","C#","D","Eb","E","F","F#","G","Ab","A","Bb","B"});
    private final JComboBox<ScaleType> standardScale = new JComboBox<ScaleType>(ScaleType.values());
    private final JSpinner standardTempo = new JSpinner(new SpinnerNumberModel(CommonEventList.DT,20,400,1));
    private CommonEventList auditionEventList;
    private JLabel auditionIcon;
    private JLabel selectedIcon;
    private Timer auditionTimer;
    private JButton reproductionButton;

    public BreedingWindow(int populationSize, ArrayList<Notes> notes,
            ArrayList<String> operatorList, ArrayList<String> configList) {
        super("CACIE Breeding");
        int actualPopulationSize = Math.max(PLAYBACK_SLOT_COUNT, populationSize);
        population = new Population(actualPopulationSize, "TREE", 0,
            notes, operatorList, configList);
        population.initPopulation();

        breedingPanel = new MultiLaneBreedingPanel(new ArrayList<Motif_simpleTree_Individual>());
        setLayout(new BorderLayout());
        JPanel left=new JPanel();left.setLayout(new javax.swing.BoxLayout(left,javax.swing.BoxLayout.Y_AXIS));left.setBackground(new Color(239,242,246));left.add(createInitializedArea());left.add(breedingPanel);
        JPanel content=new JPanel(new BorderLayout());content.add(createStandardPlaybackArea(),BorderLayout.NORTH);
        JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,createReproductionArea());split.setResizeWeight(.65);split.setDividerLocation(1150);content.add(split,BorderLayout.CENTER);add(content,BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                breedingPanel.stopPlayback();
                stopAudition();
            }
        });
        refreshInitializedArea();
        setPreferredSize(new Dimension(1780,980));
        pack();
        Rectangle available=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int width=Math.min(1880,available.width),height=Math.min(1020,available.height);
        setBounds(available.x,available.y,width,height);
        setMinimumSize(new Dimension(1400,820));
        setVisible(true);
    }

    private JPanel createStandardPlaybackArea(){
        JPanel panel=new JPanel(new FlowLayout(FlowLayout.LEFT,8,6));panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Standard playback settings"));
        standardLength.setSelectedItem(BreedingPanel.PlaybackLength.FOUR_BEATS);
        standardScale.setSelectedItem(ScaleType.DIATONIC);
        panel.add(new JLabel("Length"));panel.add(standardLength);panel.add(new JLabel("Tonic"));panel.add(standardTonic);panel.add(new JLabel("Scale/Chord"));panel.add(standardScale);panel.add(new JLabel("Tempo"));panel.add(standardTempo);
        JButton stop=new JButton("Stop");stop.addActionListener(e->stopAudition());panel.add(stop);
        breedingPanel.setTempo((Integer)standardTempo.getValue());
        standardLength.addActionListener(e->settingsChanged());standardTonic.addActionListener(e->settingsChanged());standardScale.addActionListener(e->settingsChanged());standardTempo.addChangeListener(e->{if(auditionEventList!=null)stopAudition();breedingPanel.setTempo((Integer)standardTempo.getValue());});
        return panel;
    }

    private JPanel createInitializedArea() {
        JPanel outer=new JPanel(new BorderLayout(8,8));outer.setOpaque(false);
        outer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Initialized - newly generated individuals"),BorderFactory.createEmptyBorder(6,8,8,8)));
        JPanel header=new JPanel(new FlowLayout(FlowLayout.LEFT));header.setOpaque(false);
        JButton initialize=new JButton("Initialize");
        initialize.addActionListener(e->{stopAudition();selectedIcon=null;population.initPopulation();refreshInitializedArea();updateIconBorders();});
        header.add(initialize);header.add(new JLabel("Generate eight new GP individuals"));outer.add(header,BorderLayout.NORTH);
        JPanel row=new JPanel(new GridLayout(1,PLAYBACK_SLOT_COUNT,10,0));row.setOpaque(false);
        for(int i=0;i<PLAYBACK_SLOT_COUNT;i++){initializedIcons[i]=iconCell(BreedingIndividualSlot.SlotType.INITIALIZED,i,true);installAuditionActions(initializedIcons[i],true,i);row.add(initializedIcons[i]);}
        outer.add(row,BorderLayout.CENTER);return outer;
    }

    private JPanel createStorageArea() {
        JPanel outer=new JPanel(new GridLayout(4,8,10,8));outer.setOpaque(false);
        outer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Genome Storage - 8 x 4"),BorderFactory.createEmptyBorder(8,8,8,8)));
        for(int i=0;i<storageIcons.length;i++){storageIcons[i]=iconCell(BreedingIndividualSlot.SlotType.STORAGE,i,true);installAuditionActions(storageIcons[i],false,i);outer.add(storageIcons[i]);}
        return outer;
    }

    private JPanel createReproductionArea(){
        JPanel right=new JPanel(new BorderLayout(8,8));right.setBackground(new Color(239,242,246));right.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JPanel top=new JPanel(new BorderLayout(8,0));top.setOpaque(false);JPanel parents=new JPanel(new GridLayout(1,2,10,0));parents.setOpaque(false);parents.setBorder(BorderFactory.createTitledBorder("Parents - 2 x 1"));
        for(int i=0;i<parentIcons.length;i++){parentIcons[i]=iconCell(BreedingIndividualSlot.SlotType.PARENT,i,true);parentIcons[i].setToolTipText(i==0?"Parent A":"Parent B");parentIcons[i].setImportListener((slot,individual)->{refreshImportedSlot(slot,individual);updateReproductionButton();});installAuditionActions(parentIcons[i],false,-1);parents.add(parentIcons[i]);}
        reproductionButton=new JButton("Reproduction");reproductionButton.setEnabled(false);reproductionButton.setToolTipText("Offspring generation will be implemented next");top.add(parents,BorderLayout.CENTER);top.add(reproductionButton,BorderLayout.EAST);right.add(top,BorderLayout.NORTH);
        JPanel offsprings=new JPanel(new GridLayout(4,8,8,8));offsprings.setOpaque(false);offsprings.setBorder(BorderFactory.createTitledBorder("Offsprings - 8 x 4"));
        for(int i=0;i<offspringIcons.length;i++){offspringIcons[i]=iconCell(BreedingIndividualSlot.SlotType.OFFSPRING,i,false);offsprings.add(offspringIcons[i]);}
        JPanel lower=new JPanel(new GridLayout(2,1,0,6));lower.setOpaque(false);lower.add(offsprings);lower.add(createStorageArea());right.add(lower,BorderLayout.CENTER);return right;
    }

    private BreedingIndividualSlot iconCell(BreedingIndividualSlot.SlotType type,int index,boolean acceptsDrop){BreedingIndividualSlot slot=new BreedingIndividualSlot(type,index,acceptsDrop);slot.setImportListener(this::refreshImportedSlot);return slot;}

    private void refreshInitializedArea(){
        List<Motif_simpleTree_Individual> individuals=createInitialEventLists();
        for(int i=0;i<PLAYBACK_SLOT_COUNT;i++){
            Motif_simpleTree_Individual source=individuals.get(i);
            initializedIndividuals[i]=source;
            initializedIcons[i].setIndividual(source);
            CommonEventList playback=createAuditionEventList(source);
            initializedIcons[i].setText(null);initializedIcons[i].setIcon(TreeQuiltIcon.create(source,playback));
            initializedIcons[i].setToolTipText("Individual "+(i+1)+" - "+source.getNumOfNodes()+" nodes");
        }
    }

    private PlaybackSettings standardSettings(){long ticks=((BreedingPanel.PlaybackLength)standardLength.getSelectedItem()).getTicks();double beats=ticks==Long.MAX_VALUE?Double.POSITIVE_INFINITY:ticks/16.0;return new PlaybackSettings(beats,ScaleFilter.tonicFromName((String)standardTonic.getSelectedItem()),(ScaleType)standardScale.getSelectedItem());}
    private CommonEventList createAuditionEventList(Motif_simpleTree_Individual source){return source.createPlaybackClone(standardSettings()).convertToEventList();}
    private void settingsChanged(){stopAudition();refreshAllIcons();}
    private void refreshAllIcons(){for(BreedingIndividualSlot slot:initializedIcons)if(slot!=null)refreshIcon(slot,slot.getIndividual());for(BreedingIndividualSlot slot:storageIcons)if(slot!=null)refreshIcon(slot,slot.getIndividual());for(BreedingIndividualSlot slot:parentIcons)if(slot!=null)refreshIcon(slot,slot.getIndividual());}
    private void refreshIcon(JLabel icon,Motif_simpleTree_Individual individual){if(individual==null){icon.setIcon(null);icon.setText("Empty");return;}icon.setText(null);icon.setIcon(TreeQuiltIcon.create(individual,createAuditionEventList(individual)));}
    private void refreshImportedSlot(BreedingIndividualSlot slot,Motif_simpleTree_Individual individual){stopAudition();if(slot.getSlotType()==BreedingIndividualSlot.SlotType.STORAGE)storageIndividuals[slot.getSlotIndex()]=individual;else if(slot.getSlotType()==BreedingIndividualSlot.SlotType.INITIALIZED)initializedIndividuals[slot.getSlotIndex()]=individual;refreshIcon(slot,individual);slot.setToolTipText(slot.getSlotType()+" "+(slot.getSlotIndex()+1)+" - "+individual.getNumOfNodes()+" nodes");}
    private void updateReproductionButton(){if(reproductionButton!=null)reproductionButton.setEnabled(parentIcons[0]!=null&&parentIcons[0].getIndividual()!=null&&parentIcons[1]!=null&&parentIcons[1].getIndividual()!=null);}

    private void installAuditionActions(final BreedingIndividualSlot icon,final boolean initialized,final int index){
        icon.addMouseListener(new MouseAdapter(){@Override public void mousePressed(MouseEvent e){icon.requestFocusInWindow();selectIcon(icon);if(javax.swing.SwingUtilities.isRightMouseButton(e))toggleAudition(icon,icon.getIndividual());} @Override public void mouseClicked(MouseEvent e){if(javax.swing.SwingUtilities.isLeftMouseButton(e))selectIcon(icon);}});
        icon.getInputMap(JLabel.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0),"audition");
        icon.getActionMap().put("audition",new AbstractAction(){public void actionPerformed(java.awt.event.ActionEvent e){toggleAudition(icon,icon.getIndividual());}});
    }
    private void selectIcon(JLabel icon){selectedIcon=icon;updateIconBorders();}
    private void toggleAudition(JLabel icon,Motif_simpleTree_Individual individual){if(individual==null)return;if(auditionIcon==icon&&auditionEventList!=null){stopAudition();return;}stopAudition();auditionIcon=icon;selectedIcon=icon;auditionEventList=createAuditionEventList(individual);updateIconBorders();try{auditionEventList.playAsMIDISequence((Integer)standardTempo.getValue());}catch(MidiUnavailableException|InvalidMidiDataException ex){ex.printStackTrace();stopAudition();return;}long ticks=duration(auditionEventList);int delay=(int)Math.max(100,Math.min(Integer.MAX_VALUE,Math.round(ticks*60000.0/(((Integer)standardTempo.getValue())*16.0))));auditionTimer=new Timer(delay,e->stopAudition());auditionTimer.setRepeats(false);auditionTimer.start();}
    private long duration(CommonEventList list){long end=1;for(int i=0;i<list.getNumOfNotes();i++){OneNote n=(OneNote)list.get(i);end=Math.max(end,n.getPosition()+n.getDuration());}return end;}
    private void stopAudition(){if(auditionTimer!=null){auditionTimer.stop();auditionTimer=null;}if(auditionEventList!=null){auditionEventList.stopMIDISequence();auditionEventList=null;}auditionIcon=null;updateIconBorders();}
    private void updateIconBorders(){for(JLabel icon:initializedIcons)if(icon!=null)setIconBorder(icon);for(JLabel icon:storageIcons)if(icon!=null)setIconBorder(icon);}
    private void setIconBorder(JLabel icon){Color color=icon==auditionIcon?new Color(255,166,30):icon==selectedIcon?new Color(55,120,190):new Color(80,91,104);int width=icon==auditionIcon?4:icon==selectedIcon?2:1;icon.setBorder(BorderFactory.createLineBorder(color,width));}

    private List<Motif_simpleTree_Individual> createInitialEventLists() {
        List<Motif_simpleTree_Individual> result =
            new ArrayList<Motif_simpleTree_Individual>(PLAYBACK_SLOT_COUNT);
        for (int i = 0; i < PLAYBACK_SLOT_COUNT; i++) {
            result.add((Motif_simpleTree_Individual) population.getIndividual(0, i));
        }
        return result;
    }

    public Population getPopulation() { return population; }
    public MultiLaneBreedingPanel getBreedingPanel() { return breedingPanel; }
}
