package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.Notes;
import CACIE.genome.Population;

/** Main window for the generation-less Breeding workflow. */
public class BreedingWindow extends JFrame {
    private static final int PLAYBACK_SLOT_COUNT = 8;

    private final Population population;
    private final BreedingPanel breedingPanel;

    public BreedingWindow(int populationSize, ArrayList<Notes> notes,
            ArrayList<String> operatorList, ArrayList<String> configList) {
        super("CACIE Breeding");
        int actualPopulationSize = Math.max(PLAYBACK_SLOT_COUNT, populationSize);
        population = new Population(actualPopulationSize, "TREE", 0,
            notes, operatorList, configList);
        population.initPopulation();

        breedingPanel = new BreedingPanel(createInitialEventLists());
        setLayout(new BorderLayout());
        add(breedingPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                breedingPanel.stopPlayback();
            }
        });
        pack();
        setMinimumSize(getSize());
        setLocationByPlatform(true);
        setVisible(true);
    }

    private List<CommonEventList> createInitialEventLists() {
        List<CommonEventList> result = new ArrayList<CommonEventList>(PLAYBACK_SLOT_COUNT);
        for (int i = 0; i < PLAYBACK_SLOT_COUNT; i++) {
            result.add(population.convertToEventList(0, i));
        }
        return result;
    }

    public Population getPopulation() { return population; }
    public BreedingPanel getBreedingPanel() { return breedingPanel; }
}
