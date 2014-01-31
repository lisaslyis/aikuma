package org.lp20.aikuma.model;

import java.util.LinkedHashMap;
import java.util.Iterator;
import org.lp20.aikuma.model.Segments;
import org.lp20.aikuma.model.Segments.Segment;
import org.lp20.aikuma.model.Recording;

/**
 * Used to represent transcripts for recordings; serves as a mapping from
 * segments of the audio to different pieces of the transcription text.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 */
public class Transcript {
	private LinkedHashMap<Segment, String> transcriptMap;

	/**
	 * Constructor
	 *
	 * @param	recording	The recording that this transcription is of.
	 */
	public Transcript(Recording recording) {
		transcriptMap = new LinkedHashMap<Segment, String>();
		genDummyMap();
	}

	private void genDummyMap() {
		transcriptMap.put(new Segment(0l, 49264l), "first shhh");
		transcriptMap.put(new Segment(49264l, 109728l), "second shhh");
		transcriptMap.put(new Segment(109728l, 142496l), "third shhh");
	}

	/**
	 * Gets an iterator over the segments of the recording that have associated
	 * transcriptions.
	 *
	 * @return	The iterator over recording segments.
	 */
	public Iterator<Segment> getSegmentIterator() {
		return transcriptMap.keySet().iterator();
	}

	/**
	 * Gives the corresponding transcription for a segment of the recording.
	 *
	 * @param	segment	The segment of the recording.
	 * @return	A string representing the text of the transcription
	 */
	public String getTranscriptSegment(Segment segment) {
		return transcriptMap.get(segment);
	}
}
