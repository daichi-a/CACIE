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
import javax.swing.JProgressBar;
import javax.sound.midi.Sequencer;

import CACIE.eventlist.CommonEventList;
import CACIE.eventlist.ScaleFilter;
import CACIE.eventlist.ScaleType;
import CACIE.eventlist.PlaybackSettings;
import CACIE.genome.Motif_simpleTree_Individual;
import CACIE.genome.OneNote;
import CACIE.midi.EightBeatDrumPattern;

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

    private static final String[] TONICS = {
        "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"
    };

    @SuppressWarnings("unchecked")
    private final JComboBox<PlaybackLength>[] lengthBoxes = new JComboBox[SLOT_COUNT];
    @SuppressWarnings("unchecked")
    private final JComboBox<String>[] tonicBoxes = new JComboBox[SLOT_COUNT];
    @SuppressWarnings("unchecked")
    private final JComboBox<ScaleType>[] scaleBoxes = new JComboBox[SLOT_COUNT];
    private final JPanel[] individualCards = new JPanel[SLOT_COUNT];
    private final BreedingIndividualSlot[] individualLabels = new BreedingIndividualSlot[SLOT_COUNT];
    private final List<CommonEventList> eventLists = new ArrayList<CommonEventList>(SLOT_COUNT);
    private final List<Motif_simpleTree_Individual> sourceIndividuals =
        new ArrayList<Motif_simpleTree_Individual>(SLOT_COUNT);
    private final List<Motif_simpleTree_Individual> playbackIndividuals =
        new ArrayList<Motif_simpleTree_Individual>(SLOT_COUNT);
    private final JButton playButton = new JButton("Play sequence");
    private final JButton drumPlayButton = new JButton("PlayWithDrumBeat");
    private final JButton loopPlayButton = new JButton("Loop Sequence");
    private final JButton loopDrumPlayButton = new JButton("Loop With Drum Beat");
    private final JButton stopButton = new JButton("Stop");
    private final JLabel pendingLoopLabel = new JLabel(" ");

    private Timer sequenceTimer;
    private Sequencer sequencePlayer;
    private PlaybackSequencePlan sequencePlan;
    private RealtimeLoopSequence loopSequence;
    private boolean looping;
    private boolean loopWithDrums;
    private long displayedLoop;
    private long committedFromLoop=-1;
    private long settingsRevision;
    private long appliedRevision;
    private boolean updatingSlotControls;
    private final JProgressBar[] progressBars=new JProgressBar[SLOT_COUNT];
    private int playingIndex = -1;
    private int tempo = CommonEventList.DT;

    public BreedingPanel(List<Motif_simpleTree_Individual> individuals) {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(new Color(239, 242, 246));
        setPreferredSize(new Dimension(900, 300));
        add(createHeader(), BorderLayout.NORTH);
        add(createSlots(), BorderLayout.CENTER);
        setIndividuals(individuals);
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
        drumPlayButton.addActionListener(e -> playSequence(true));
        loopPlayButton.addActionListener(e -> playLoop(false));
        loopDrumPlayButton.addActionListener(e -> playLoop(true));
        stopButton.addActionListener(e -> stopPlayback());
        stopButton.setEnabled(false);
        controls.add(playButton);
        controls.add(drumPlayButton);
        controls.add(loopPlayButton);
        controls.add(loopDrumPlayButton);
        controls.add(stopButton);
        pendingLoopLabel.setForeground(new Color(56,82,108));
        controls.add(pendingLoopLabel);
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
            scaleBoxes[i] = new JComboBox<ScaleType>(ScaleType.values());
            scaleBoxes[i].setSelectedItem(ScaleType.DIATONIC);
            final int slot = i;
            lengthBoxes[i].addActionListener(e -> playbackSettingChanged(slot));
            tonicBoxes[i].addActionListener(e -> playbackSettingChanged(slot));
            scaleBoxes[i].addActionListener(e -> playbackSettingChanged(slot));
            column.add(labeled("Length", lengthBoxes[i]));
            column.add(labeled("Tonic", tonicBoxes[i]));
            column.add(labeled("Scale/Chord", scaleBoxes[i]));

            individualCards[i] = new JPanel(new BorderLayout());
            individualCards[i].setPreferredSize(new Dimension(130, 105));
            individualCards[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
            individualLabels[i] = new BreedingIndividualSlot(BreedingIndividualSlot.SlotType.PLAYBACK,i,true);
            individualLabels[i].setForeground(Color.WHITE);
            individualLabels[i].setFont(individualLabels[i].getFont().deriveFont(Font.BOLD));
            individualCards[i].add(individualLabels[i], BorderLayout.CENTER);
            column.add(individualCards[i]);
            progressBars[i]=new JProgressBar(0,1000);progressBars[i].setPreferredSize(new Dimension(100,4));progressBars[i].setMaximumSize(new Dimension(Integer.MAX_VALUE,4));progressBars[i].setBorderPainted(false);progressBars[i].setForeground(new Color(255,178,36));
            column.add(progressBars[i]);
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

    public final void setIndividuals(List<Motif_simpleTree_Individual> individuals) {
        if(!looping)stopPlayback();
        sourceIndividuals.clear();
        playbackIndividuals.clear();
        eventLists.clear();
        if (individuals != null) {
            sourceIndividuals.addAll(individuals.subList(0, Math.min(SLOT_COUNT, individuals.size())));
            for (int i = 0; i < sourceIndividuals.size(); i++) {
                playbackIndividuals.add(null);
                eventLists.add(null);
                rebuildSlot(i);
            }
        }
        for (int i = 0; i < SLOT_COUNT; i++) {
            boolean occupied = i < eventLists.size() && eventLists.get(i) != null;
            individualCards[i].setBackground(occupied
                ? new Color(105, 156, 205) : new Color(168, 176, 184));
            if(!occupied)individualLabels[i].setIndividual(null);
        }
        playButton.setEnabled(!eventLists.isEmpty());
        drumPlayButton.setEnabled(!eventLists.isEmpty());
    }

    /** Copies a raw individual into a playback slot and initializes its controls. */
    public void setIndividual(int slot,Motif_simpleTree_Individual individual,PlaybackSettings settings) {
        if(slot<0||slot>=SLOT_COUNT)throw new IllegalArgumentException("Invalid playback slot: "+slot);
        // A loop owns one continuously running Sequencer.  A drop only updates the
        // pending GUI model; the last active slot commits it to the next loop.
        if(!looping)stopPlayback();
        while(sourceIndividuals.size()<=slot){sourceIndividuals.add(null);playbackIndividuals.add(null);eventLists.add(null);}
        sourceIndividuals.set(slot,(Motif_simpleTree_Individual)individual.clone());
        updatingSlotControls=true;
        long ticks=Double.isInfinite(settings.getBeats())?Long.MAX_VALUE:Math.round(settings.getBeats()*16.0);
        for(PlaybackLength length:PlaybackLength.values())if(length.getTicks()==ticks){lengthBoxes[slot].setSelectedItem(length);break;}
        tonicBoxes[slot].setSelectedItem(ScaleFilter.tonicName(settings.getTonic()));
        scaleBoxes[slot].setSelectedItem(settings.getScale());
        updatingSlotControls=false;
        rebuildSlot(slot);
        queueNextLoopUpdate();
        individualCards[slot].setBackground(new Color(105,156,205));
        playButton.setEnabled(true);
        drumPlayButton.setEnabled(true);
        loopPlayButton.setEnabled(true);
        loopDrumPlayButton.setEnabled(true);
    }

    private void rebuildSlot(int slot) {
        if (slot < 0 || slot >= sourceIndividuals.size() || sourceIndividuals.get(slot)==null) return;
        double beats = getPlaybackTicks(slot) == Long.MAX_VALUE
            ? Double.POSITIVE_INFINITY : getPlaybackTicks(slot) / 16.0;
        int tonic = ScaleFilter.tonicFromName(getTonic(slot));
        Motif_simpleTree_Individual decorated = sourceIndividuals.get(slot)
            .createBreedingPlaybackClone(beats, tonic, getScale(slot));
        CommonEventList eventList = decorated.convertToEventList();
        if (slot < playbackIndividuals.size()) playbackIndividuals.set(slot, decorated);
        if (slot < eventLists.size()) eventLists.set(slot, eventList);
        if (slot < individualLabels.length && individualLabels[slot] != null) {
            individualLabels[slot].setIndividual(sourceIndividuals.get(slot));
            individualLabels[slot].setText(null);
            individualLabels[slot].setIcon(TreeQuiltIcon.create(sourceIndividuals.get(slot),eventList));
            individualLabels[slot].setToolTipText("Playback "+(slot+1)+" - "+eventList.getNumOfNotes()+" notes");
        }
    }

    private void playbackSettingChanged(int slot){if(updatingSlotControls)return;rebuildSlot(slot);queueNextLoopUpdate();}

    /** Rebuilds only the next not-yet-started absolute-tick loop block. */
    private void queueNextLoopUpdate(){
        if(!looping||sequencePlayer==null||loopSequence==null)return;
        final long revision=++settingsRevision;
        try{
            long tick=sequencePlayer.getTickPosition();RealtimeLoopSequence.Block current=blockAt(tick);if(current==null)return;
            long targetNumber=current.loopNumber+1;RealtimeLoopSequence.Block target=blockNumber(targetNumber);
            if(target==null){PlaybackSequencePlan seed=buildCurrentPlan(loopWithDrums);target=loopSequence.append(targetNumber,seed);}
            PlaybackSequencePlan pending=buildCurrentPlan(loopWithDrums);
            RealtimeLoopSequence.Block replaced=loopSequence.replaceFuture(targetNumber,pending,tick);
            appliedRevision=revision;pendingLoopLabel.setText("Pending: Loop "+(replaced.loopNumber+1)+"  r"+revision);
            // Keep a guard block beyond the newly sized target.
            long guardNumber=targetNumber+1;if(blockNumber(guardNumber)==null)loopSequence.append(guardNumber,pending);
        }catch(IllegalStateException late){
            // The boundary was crossed during rebuilding. Re-evaluate against the new current loop.
            try{long tick=sequencePlayer.getTickPosition();RealtimeLoopSequence.Block current=blockAt(tick);if(current!=null){long target=current.loopNumber+1;PlaybackSequencePlan pending=buildCurrentPlan(loopWithDrums);if(blockNumber(target)==null)loopSequence.append(target,pending);RealtimeLoopSequence.Block replaced=loopSequence.replaceFuture(target,pending,tick);appliedRevision=revision;pendingLoopLabel.setText("Pending: Loop "+(replaced.loopNumber+1)+"  r"+revision);}}catch(Exception ex){ex.printStackTrace();}
        }catch(Exception ex){ex.printStackTrace();}
    }

    private RealtimeLoopSequence.Block blockAt(long tick){for(RealtimeLoopSequence.Block b:loopSequence.getBlocks())if(tick>=b.startTick&&tick<b.endTick)return b;return null;}
    private RealtimeLoopSequence.Block blockNumber(long number){for(RealtimeLoopSequence.Block b:loopSequence.getBlocks())if(b.loopNumber==number)return b;return null;}

    public void playSequence() {
        playSequence(false);
    }

    public void playSequence(boolean withDrums) {
        stopPlayback();
        try {
            ArrayList<Long> lengths=new ArrayList<Long>(SLOT_COUNT);for(int i=0;i<SLOT_COUNT;i++)lengths.add(getPlaybackTicks(i));
            sequencePlan=PlaybackSequenceBuilder.build(eventLists,lengths,tempo,withDrums);
            if(sequencePlan.getSegments().isEmpty()){finishPlayback();return;}
            sequencePlayer=sequencePlan.getMIDISequence().playMIDISequenceWithSequencer();
            playButton.setEnabled(false);drumPlayButton.setEnabled(false);stopButton.setEnabled(true);
            sequenceTimer=new Timer(33,e->updatePlaybackAnimation());sequenceTimer.start();updatePlaybackAnimation();
        } catch(Exception ex){ex.printStackTrace();stopPlayback();}
    }

    private PlaybackSequencePlan buildCurrentPlan(boolean withDrums)throws Exception{ArrayList<CommonEventList> lists=new ArrayList<CommonEventList>(eventLists);ArrayList<Long> lengths=new ArrayList<Long>(SLOT_COUNT);for(int i=0;i<SLOT_COUNT;i++)lengths.add(getPlaybackTicks(i));return PlaybackSequenceBuilder.build(lists,lengths,tempo,withDrums);}

    public void playLoop(boolean withDrums){
        stopPlayback();
        try{
            PlaybackSequencePlan initial=buildCurrentPlan(withDrums);if(initial.getSegments().isEmpty()){finishPlayback();return;}
            loopSequence=new RealtimeLoopSequence(tempo);loopSequence.append(0,initial);loopSequence.append(1,initial);loopSequence.append(2,initial);
            sequencePlayer=loopSequence.getMIDISequence().playMIDISequenceWithSequencer();sequencePlan=initial;looping=true;loopWithDrums=withDrums;displayedLoop=0;committedFromLoop=-1;settingsRevision=0;appliedRevision=0;pendingLoopLabel.setText("Loop 1");
            setPlayingButtons(false);stopButton.setEnabled(true);sequenceTimer=new Timer(33,e->updatePlaybackAnimation());sequenceTimer.start();updatePlaybackAnimation();
        }catch(Exception ex){ex.printStackTrace();stopPlayback();}
    }

    private void updatePlaybackAnimation(){if(sequencePlayer==null||sequencePlan==null)return;long tick=sequencePlayer.getTickPosition();long localTick=tick;RealtimeLoopSequence.Block block=null;if(looping){for(RealtimeLoopSequence.Block b:loopSequence.getBlocks())if(tick>=b.startTick&&tick<b.endTick){block=b;break;}if(block!=null){displayedLoop=block.loopNumber;sequencePlan=block.plan;localTick=tick-block.startTick;PlaybackSequencePlan.Segment last=lastSegment(sequencePlan);if(last!=null&&localTick>=last.startTick&&committedFromLoop<block.loopNumber)commitNextLoop(block);}}
        PlaybackSequencePlan.Segment active=sequencePlan.segmentAt(localTick);playingIndex=active==null?-1:active.slot;for(int i=0;i<SLOT_COUNT;i++){int value=0;for(PlaybackSequencePlan.Segment segment:sequencePlan.getSegments())if(segment.slot==i){value=localTick>=segment.endTick?1000:localTick<segment.startTick?0:(int)Math.round(segment.progress(localTick)*1000);break;}progressBars[i].setValue(value);}updateCardBorders();if(!looping&&!sequencePlayer.isRunning()&&tick>=sequencePlan.getTotalTicks())finishPlayback();}

    private PlaybackSequencePlan.Segment lastSegment(PlaybackSequencePlan plan){List<PlaybackSequencePlan.Segment> segments=plan.getSegments();return segments.isEmpty()?null:segments.get(segments.size()-1);}
    private void commitNextLoop(RealtimeLoopSequence.Block current){committedFromLoop=current.loopNumber;try{PlaybackSequencePlan pending=buildCurrentPlan(loopWithDrums);long target=current.loopNumber+1;loopSequence.replaceFuture(target,pending,sequencePlayer.getTickPosition());appliedRevision=settingsRevision;pendingLoopLabel.setText("Next: Loop "+(target+1)+"  r"+appliedRevision);long need=target+2;boolean exists=false;for(RealtimeLoopSequence.Block b:loopSequence.getBlocks())if(b.loopNumber==need)exists=true;if(!exists)loopSequence.append(need,pending);}catch(Exception ex){ex.printStackTrace();}}

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
        if(sequencePlayer!=null){if(sequencePlayer.isRunning())sequencePlayer.stop();sequencePlayer.close();sequencePlayer=null;}
        sequencePlan=null;
        loopSequence=null;looping=false;
        pendingLoopLabel.setText(" ");
        finishPlayback();
    }

    private void finishPlayback() {
        if(sequenceTimer!=null){sequenceTimer.stop();sequenceTimer=null;}
        if(sequencePlayer!=null&&!sequencePlayer.isRunning()){sequencePlayer.close();sequencePlayer=null;}
        playingIndex = -1;
        updateCardBorders();
        playButton.setEnabled(!eventLists.isEmpty());
        drumPlayButton.setEnabled(!eventLists.isEmpty());
        loopPlayButton.setEnabled(!eventLists.isEmpty());loopDrumPlayButton.setEnabled(!eventLists.isEmpty());
        stopButton.setEnabled(false);
        for(JProgressBar progress:progressBars)if(progress!=null)progress.setValue(0);
    }

    private void setPlayingButtons(boolean enabled){playButton.setEnabled(enabled);drumPlayButton.setEnabled(enabled);loopPlayButton.setEnabled(enabled);loopDrumPlayButton.setEnabled(enabled);}

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
    public ScaleType getScale(int slot) { return (ScaleType) scaleBoxes[slot].getSelectedItem(); }
    public List<CommonEventList> getEventLists() { return Collections.unmodifiableList(eventLists); }
    public List<Motif_simpleTree_Individual> getSourceIndividuals() {
        return Collections.unmodifiableList(sourceIndividuals);
    }

    /** True while the uninterrupted real-time loop transport is active. */
    public boolean isLooping(){return looping&&sequencePlayer!=null&&sequencePlayer.isOpen();}

    public void setPlaybackImportListener(BreedingIndividualSlot.ImportListener listener){for(BreedingIndividualSlot slot:individualLabels)slot.setImportListener(listener);}

    public void setTempo(int tempo) {
        if (tempo <= 0) throw new IllegalArgumentException("Tempo must be positive.");
        this.tempo = tempo;
    }
}
