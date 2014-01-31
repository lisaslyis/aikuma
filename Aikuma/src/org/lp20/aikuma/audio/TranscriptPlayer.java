package org.lp20.aikuma.audio;

import android.app.Activity;
import android.widget.TextView;
import java.io.IOException;
import java.util.Iterator;
import org.lp20.aikuma.model.Recording;
import org.lp20.aikuma.model.Segments.Segment;
import org.lp20.aikuma.model.Transcript;
import org.lp20.aikuma.R;

/**
 * A player that plays recordings that have transcriptions; updates an
 * activity's transcriptView TextView with the transcriptions relevant to the
 * audio currently being listened to.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 */
public class TranscriptPlayer extends MarkedPlayer {
	// The transcription for the recording being played.
	private Transcript transcript;
	// The segment of the recording that is currently being played.
	private Segment segment;
	// The activity to display the transcript
	private Activity activity;
	// An iterator over the segments in the recording that correspond to some
	// transcription text
	private Iterator<Segment> segmentIterator;

	/**
	 * Constructor
	 *
	 * @param	recording	The recording that this transcript player is to
	 * play
	 * @param	activity	The activity to display the transcription is. The
	 * activity must have a transcriptView TextView.
	 * @param	playThroughSpeaker	True if the audio is to be played through the main
	 * speaker; false if through the ear piece (ie the private phone call style)
	 * @throws	IOException	If there is an issue reading the recording data.
	 */
	public TranscriptPlayer(Recording recording, Activity activity,
			boolean playThroughSpeaker) throws IOException {
		super(recording, playThroughSpeaker);

		this.activity = activity;
		transcript = new Transcript(recording);

	}

	private OnMarkerReachedListener onTranscriptMarkerReachedListener =
			new OnMarkerReachedListener() {
		public void onMarkerReached(MarkedPlayer p) {
			final TextView transcriptView = (TextView)
					activity.findViewById(R.id.transcriptView);
			activity.runOnUiThread(new Runnable() {
				public void run() {
					transcriptView.setText(transcript.getTranscriptSegment(
							TranscriptPlayer.this.segment));
					if (segmentIterator.hasNext()) {
						TranscriptPlayer.this.segment =
								TranscriptPlayer.this.segmentIterator.next();
						TranscriptPlayer.this.setNotificationMarkerPosition(
								TranscriptPlayer.this.segment);
					}
				}
			});
		}
	};
}
