package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import CACIE.genome.Notes;
import CACIE.genome.Population;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.eventlist.PlaybackSettings;
import CACIE.eventlist.CommonEventList;

/** Main window for the generation-less Breeding workflow. */
public class BreedingWindow extends JFrame {
    private static final int PLAYBACK_SLOT_COUNT = 8;

    private final Population population;
    private final BreedingPanel breedingPanel;
    private final JLabel[] initializedIcons = new JLabel[PLAYBACK_SLOT_COUNT];
    private final JLabel[] storageIcons = new JLabel[32];

    public BreedingWindow(int populationSize, ArrayList<Notes> notes,
            ArrayList<String> operatorList, ArrayList<String> configList) {
        super("CACIE Breeding");
        int actualPopulationSize = Math.max(PLAYBACK_SLOT_COUNT, populationSize);
        population = new Population(actualPopulationSize, "TREE", 0,
            notes, operatorList, configList);
        population.initPopulation();

        breedingPanel = new BreedingPanel(new ArrayList<Motif_simpleTree_Individual>());
        setLayout(new BorderLayout());
        JPanel workspace=new JPanel();
        workspace.setLayout(new javax.swing.BoxLayout(workspace,javax.swing.BoxLayout.Y_AXIS));
        workspace.setBackground(new Color(239,242,246));
        workspace.add(createInitializedArea());
        workspace.add(createStorageArea());
        workspace.add(breedingPanel);
        add(new JScrollPane(workspace), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                breedingPanel.stopPlayback();
            }
        });
        refreshInitializedArea();
        setPreferredSize(new Dimension(1320,900));
        pack();
        setMinimumSize(new Dimension(1000,700));
        setLocationByPlatform(true);
        setVisible(true);
    }

    private JPanel createInitializedArea() {
        JPanel outer=new JPanel(new BorderLayout(8,8));outer.setOpaque(false);
        outer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Initialized - newly generated individuals"),BorderFactory.createEmptyBorder(6,8,8,8)));
        JPanel header=new JPanel(new FlowLayout(FlowLayout.LEFT));header.setOpaque(false);
        JButton initialize=new JButton("Initialize");
        initialize.addActionListener(e->{population.initPopulation();refreshInitializedArea();});
        header.add(initialize);header.add(new JLabel("Generate eight new GP individuals"));outer.add(header,BorderLayout.NORTH);
        JPanel row=new JPanel(new GridLayout(1,PLAYBACK_SLOT_COUNT,10,0));row.setOpaque(false);
        for(int i=0;i<PLAYBACK_SLOT_COUNT;i++){initializedIcons[i]=iconCell("Empty");row.add(initializedIcons[i]);}
        outer.add(row,BorderLayout.CENTER);return outer;
    }

    private JPanel createStorageArea() {
        JPanel outer=new JPanel(new GridLayout(4,8,10,8));outer.setOpaque(false);
        outer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Genome Storage - 8 x 4"),BorderFactory.createEmptyBorder(8,8,8,8)));
        for(int i=0;i<storageIcons.length;i++){storageIcons[i]=iconCell("Empty");outer.add(storageIcons[i]);}
        return outer;
    }

    private JLabel iconCell(String text){JLabel label=new JLabel(text,SwingConstants.CENTER);label.setOpaque(true);label.setBackground(new Color(205,211,218));label.setBorder(BorderFactory.createLineBorder(new Color(80,91,104)));label.setPreferredSize(new Dimension(104,104));return label;}

    private void refreshInitializedArea(){
        List<Motif_simpleTree_Individual> individuals=createInitialEventLists();
        for(int i=0;i<PLAYBACK_SLOT_COUNT;i++){
            Motif_simpleTree_Individual source=individuals.get(i);
            CommonEventList playback=source.createPlaybackClone(PlaybackSettings.DEFAULT).convertToEventList();
            initializedIcons[i].setText(null);initializedIcons[i].setIcon(TreeQuiltIcon.create(source,playback));
            initializedIcons[i].setToolTipText("Individual "+(i+1)+" - "+source.getNumOfNodes()+" nodes");
        }
    }

    private List<Motif_simpleTree_Individual> createInitialEventLists() {
        List<Motif_simpleTree_Individual> result =
            new ArrayList<Motif_simpleTree_Individual>(PLAYBACK_SLOT_COUNT);
        for (int i = 0; i < PLAYBACK_SLOT_COUNT; i++) {
            result.add((Motif_simpleTree_Individual) population.getIndividual(0, i));
        }
        return result;
    }

    public Population getPopulation() { return population; }
    public BreedingPanel getBreedingPanel() { return breedingPanel; }
}
