package CACIE.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import CACIE.eventlist.CommonEventList;
import CACIE.genome.OneNote;

/** Eight evaluated individuals and their playback/scale parameters. */
public class BreedingPanel extends JPanel {
    public static final int SLOT_COUNT = 8;
    private static final int TICKS_PER_QUARTER = 16;

    public enum PlaybackLength {
        QUARTER_BEAT("1/4 beat", 4), HALF_BEAT("1/2 beat", 8),
        ONE_BEAT("1 beat", 16), TWO_BEATS("2 beats", 32),
        FOUR_BEATS("4 beats", 64), EIGHT_BEATS("8 beats", 128),
        SIXTEEN_BEATS("16 beats", 256), ALL("All", Long.MAX_VALUE);

        private final String label;
        private final long ticks;

        PlaybackLength(String label, long ticks) {
            this.label = label;
            this.ticks = ticks;
        }

        public long getTicks() { return ticks; }
        @Override public String toString() { return label; }
    }

    public enum Scale {
        DIATONIC("Diatonic"), MAJOR("Major"),
        NATURAL_MINOR("Natural minor"), HARMONIC_MINOR("Harmonic minor"),
        MELODIC_MINOR("Melodic minor"), PENTATONIC("Pentatonic"),
        CHROMATIC("Chromatic");

        private final String label;
        Scale(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private static final String[] TONICS = {
        "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"
    };

    @SuppressWarnings("unchecked")
    private final JComboBox<PlaybackLength>[] lengthBoxes = new JComboBox[SLOT_COUNT];
    @SuppressWarnings("unchecked")
    private final JComboBox<String>[] tonicBoxes = new JComboBox[SLOT_COUNT];
    @SuppressWarnings("unchecked")
    private final JComboBox<Scale>[] scaleBoxes = new JComboBox[SLOT_COUNT];
    private final JPanel[] individualCards = new JPanel[SLOT_COUNT];
    private final JLabel[] individualLabels = new JLabel[SLOT_COUNT];
    private final List<CommonEventList> eventLists = new ArrayList<CommonEventList>(SLOT_COUNT);
    private final JButton playButton = new JButton("Play sequence");
    private final JButton stopButton = new JButton("Stop");

    private Timer sequenceTimer;
    private CommonEventList playingExcerpt;
    private int playingIndex = -1;
    private int tempo = CommonEventList.DT;

    public BreedingPanel(List<CommonEventList> evaluatedIndividuals) {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(239, 242, 246));
        setPreferredSize(new Dimension(1280, 360));
        add(createHeader(), BorderLayout.NORTH);
        add(createSlots(), BorderLayout.CENTER);
        setEventLists(evaluatedIndividuals);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Playback area - evaluated individuals");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        header.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        playButton.addActionListener(e -> playSequence());
        stopButton.addActionListener(e -> stopPlayback());
        stopButton.setEnabled(false);
        controls.add(playButton);
        controls.add(stopButton);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel createSlots() {
        JPanel slots = new JPanel(new GridLayout(1, SLOT_COUNT, 8, 0));
        slots.setOpaque(false);
        for (int i = 0; i < SLOT_COUNT; i++) {
            JPanel column = new JPanel();
            column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
            column.setOpaque(false);

            lengthBoxes[i] = new JComboBox<PlaybackLength>(PlaybackLength.values());
            lengthBoxes[i].setSelectedItem(PlaybackLength.FOUR_BEATS);
            tonicBoxes[i] = new JComboBox<String>(TONICS);
            scaleBoxes[i] = new JComboBox<Scale>(Scale.values());
            scaleBoxes[i].setSelectedItem(Scale.DIATONIC);
            column.add(labeled("Length", lengthBoxes[i]));
            column.add(labeled("Tonic", tonicBoxes[i]));
            column.add(labeled("Scale", scaleBoxes[i]));

            individualCards[i] = new JPanel(new BorderLayout());
            individualCards[i].setPreferredSize(new Dimension(130, 105));
            individualCards[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
            individualLabels[i] = new JLabel("Individual " + (i + 1), SwingConstants.CENTER);
            individualLabels[i].setForeground(Color.WHITE);
            individualLabels[i].setFont(individualLabels[i].getFont().deriveFont(Font.BOLD));
            individualCards[i].add(individualLabels[i], BorderLayout.CENTER);
            column.add(individualCards[i]);
            slots.add(column);
        }
        updateCardBorders();
        return slots;
    }

    private JPanel labeled(String text, JComboBox<?> control) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(11f));
        panel.add(label, BorderLayout.NORTH);
        panel.add(control, BorderLayout.CENTER);
        return panel;
    }

    public final void setEventLists(List<CommonEventList> individuals) {
        stopPlayback();
        eventLists.clear();
        if (individuals != null) {
            eventLists.addAll(individuals.subList(0, Math.min(SLOT_COUNT, individuals.size())));
        }
        for (int i = 0; i < SLOT_COUNT; i++) {
            boolean occupied = i < eventLists.size() && eventLists.get(i) != null;
            individualCards[i].setBackground(occupied
                ? new Color(105, 156, 205) : new Color(168, 176, 184));
            individualLabels[i].setText(occupied
                ? "<html><center>Individual " + (i + 1) + "<br>"
                    + eventLists.get(i).getNumOfNotes() + " notes</center></html>"
                : "Empty");
        }
        playButton.setEnabled(!eventLists.isEmpty());
    }

    public void playSequence() {
        stopPlayback();
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
        playNext(0);
    }

    private void playNext(int index) {
        if (index >= eventLists.size()) {
            finishPlayback();
            return;
        }
        playingIndex = index;
        updateCardBorders();
        long selectedTicks = getPlaybackTicks(index);
        playingExcerpt = createExcerpt(eventLists.get(index), selectedTicks);
        try {
            playingExcerpt.setInstrumentNumber(eventLists.get(index).getInstrumentNumber());
            playingExcerpt.playAsMIDISequence(tempo);
        } catch (MidiUnavailableException | InvalidMidiDataException ex) {
            ex.printStackTrace();
        }

        long ticks = effectiveDuration(playingExcerpt, selectedTicks);
        int delay = (int) Math.max(100L, Math.min(Integer.MAX_VALUE,
            Math.round(ticks * 60000.0 / (tempo * TICKS_PER_QUARTER))));
        sequenceTimer = new Timer(delay, e -> {
            playingExcerpt.stopMIDISequence();
            playingExcerpt = null;
            playNext(index + 1);
        });
        sequenceTimer.setRepeats(false);
        sequenceTimer.start();
    }

    private CommonEventList createExcerpt(CommonEventList source, long limit) {
        CommonEventList result = new CommonEventList(0);
        for (int i = 0; i < source.getNumOfNotes(); i++) {
            OneNote note = (OneNote) source.get(i);
            if (note.getPosition() >= limit) continue;
            long available = limit == Long.MAX_VALUE ? note.getDuration() : limit - note.getPosition();
            int duration = (int) Math.min(note.getDuration(), available);
            if (duration > 0) {
                result.add(new OneNote(note.getNoteNumber(), note.getVelocity(),
                    note.getPosition(), duration));
            }
        }
        return result;
    }

    private long effectiveDuration(CommonEventList list, long selectedTicks) {
        long duration = 0;
        for (int i = 0; i < list.getNumOfNotes(); i++) {
            OneNote note = (OneNote) list.get(i);
            duration = Math.max(duration, note.getPosition() + note.getDuration());
        }
        return Math.max(1, selectedTicks == Long.MAX_VALUE ? duration : Math.min(duration, selectedTicks));
    }

    public void stopPlayback() {
        if (sequenceTimer != null) {
            sequenceTimer.stop();
            sequenceTimer = null;
        }
        if (playingExcerpt != null) {
            playingExcerpt.stopMIDISequence();
            playingExcerpt = null;
        }
        finishPlayback();
    }

    private void finishPlayback() {
        playingIndex = -1;
        updateCardBorders();
        playButton.setEnabled(!eventLists.isEmpty());
        stopButton.setEnabled(false);
    }

    private void updateCardBorders() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            Color color = i == playingIndex ? new Color(255, 178, 36) : new Color(56, 82, 108);
            individualCards[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, i == playingIndex ? 4 : 2),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)));
        }
    }

    public long getPlaybackTicks(int slot) {
        return ((PlaybackLength) lengthBoxes[slot].getSelectedItem()).getTicks();
    }
    public String getTonic(int slot) { return (String) tonicBoxes[slot].getSelectedItem(); }
    public Scale getScale(int slot) { return (Scale) scaleBoxes[slot].getSelectedItem(); }
    public List<CommonEventList> getEventLists() { return Collections.unmodifiableList(eventLists); }

    public void setTempo(int tempo) {
        if (tempo <= 0) throw new IllegalArgumentException("Tempo must be positive.");
        this.tempo = tempo;
    }
}
