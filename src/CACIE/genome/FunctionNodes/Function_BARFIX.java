package CACIE.genome.FunctionNodes;

import java.util.ArrayList;

import CACIE.genome.Notes;
import CACIE.genome.OneNote;

/** Limits a phrase to a number of quarter-note beats without changing the source. */
public final class Function_BARFIX {
    private Function_BARFIX() {}

    public static Notes evl(Notes first, double beats) {
        long limit = Math.max(1L, Math.round(beats * 16.0));
        Notes result = new Notes();
        for (int i = 0; i < first.getNumOfNotes(); i++) {
            OneNote note = first.getNote(i);
            if (note.getPosition() >= limit) continue;
            int duration = (int) Math.min(note.getDuration(), limit - note.getPosition());
            if (duration > 0) {
                result.addNote(new OneNote(note.getNoteNumber(), note.getVelocity(),
                    note.getPosition(), duration));
            }
        }
        result.fitParameters();
        return result;
    }
}
