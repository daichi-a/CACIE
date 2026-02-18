package CACIE.eventlist;

import CACIE.gui.ScoreTrackPanel;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import CACIE.genome.OneNote;
import basic.Fraction;
import basic.NoteEvent;
import basic.TrackData;

public class DJavaMusicLibraryHandler
{
	private static boolean initFlag = false;
	private static JFrame frame;
	private static ScoreTrackPanel scorePanel;
	
	public static TrackData convertToTrackData(CommonEventList eventList)
	{
		TrackData track = new TrackData();
		for( int i = 0; i < eventList.getNumOfNotes(); i++ )
		{
			OneNote note = (OneNote)eventList.get(i);
			NoteEvent noteEvent = new NoteEvent( Fraction.getFraction((int)note.getPosition(), 64), Fraction.getFraction(note.getDuration(), 64), CommonEventList.convertChromaticToDiatonicInC(note.getNoteNumber()) );
			track.addEvent( noteEvent );
		}
		return track;
	}
	
	public static void showScore( TrackData track )
	{
		if( !initFlag )
		{
			initFrame();
		}
		scorePanel.setTrack( track );
		frame.repaint();
	}
	
	public static void initFrame()
	{
		frame = new JFrame();
		scorePanel = new ScoreTrackPanel(new TrackData());
		frame.add( new JScrollPane(scorePanel) );
		frame.setSize( 500, 300 ); // tekitou
		frame.setVisible( true );
		initFlag = true;
	}
}
