package au.edu.unimelb.aikuma.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import au.edu.unimelb.aikuma.audio.Player;
import au.edu.unimelb.aikuma.audio.analyzers.Analyzer;
import au.edu.unimelb.aikuma.audio.analyzers.ThresholdSpeechAnalyzer;
import au.edu.unimelb.aikuma.audio.recognizers.AverageRecognizer;
import au.edu.unimelb.aikuma.FileIO;
import au.edu.unimelb.aikuma.audio.Segments.Segment;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/** Respeaker used to get input from eg. a microphone and
 *  output into a file.tIn addition, it also 
 * 
 *  Usage:
 *    Respeaker respeaker = new Respeaker();
 *    respeaker.prepare(
 *      "/mnt/sdcard/bold/recordings/source_file.wav",
 *      "/mnt/sdcard/bold/recordings/target_file.wav"
 *    );
 *    respeaker.listen();
 *    respeaker.pause();
 *    respeaker.resume();
 *    respeaker.stop();
 *
 *  Note that stopping the respeaker closes and finalizes the WAV file.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 */
public class ThumbRespeaker {

	public ThumbRespeaker() {
		recorder = new Recorder();
		player = new Player();
		segments = new Segments();
		setFinishedPlaying(false);
		playThroughSpeaker();
	}

	/** Prepare the respeaker by setting a source file and a target file. */
	public void prepare(String sourceFilename, String targetFilename,
			String mappingFilename) {
		player.prepare(sourceFilename);
		recorder.prepare(targetFilename);
		this.mappingFilename = mappingFilename;
	}

	public void playOriginal() {
		originalStartOfSegment = player.getCurrentSample();
		player.play();
	}

	public void pauseOriginal() {
		player.pause();
	}

	public void recordRespeaking() {
		originalEndOfSegment = player.getCurrentSample();
		respeakingStartOfSegment = recorder.getFile().getCurrentSample();
		recorder.listen();
	}

	public void pauseRespeaking() {
		respeakingEndOfSegment = recorder.getFile().getCurrentSample();
		storeSegmentEntry();
	}

	public void stop() {
		recorder.stop();
		player.stop();
		if (respeakingStartOfSegment != null) {
			storeSegmentEntry();
		}
		try {
			segments.write(new File(mappingFilename));
		} catch (IOException e) {
			// Couldn't write mapping. Oh well!
		}
	}

	/**
	 * To listen to the final piece of annotation after playing of the original
	 * has been completed.
	 */
	public void listenAfterFinishedPlaying() {
		recorder.listen();
	}

	/**
	 * Stores an original segment and corresponding respeaking segment in
	 * segments.
	 */
	private void storeSegmentEntry() {
		Segment originalSegment = new Segment(originalStartOfSegment,
				originalEndOfSegment);
		Segment respeakingSegment = new Segment(respeakingStartOfSegment,
				respeakingEndOfSegment);
		segments.put(originalSegment, respeakingSegment);
		originalStartOfSegment = null;
		originalEndOfSegment = null;
		respeakingStartOfSegment = null;
		respeakingEndOfSegment = null;
	}

	/**
	 * finishedPlaying mutator
	 */
	public void setFinishedPlaying(boolean finishedPlaying) {
		this.finishedPlaying = finishedPlaying;
	}

	/**
	 * finishedPlaying accessor
	 */
	public boolean getFinishedPlaying() {
		return this.finishedPlaying;
	}

	private void playThroughSpeaker() {
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	/** The recorder used to obtain audio for the respeaking */
	private Recorder recorder;

	/** Player to play the original with. */
	public Player player;

	/** Indicates whether the recording has finished playing */
	private boolean finishedPlaying;

	/** Fields to store segment boundaries */
	private Long originalStartOfSegment;
	private Long originalEndOfSegment;
	private Long respeakingStartOfSegment;
	private Long respeakingEndOfSegment;

	/** Stores segment information and is used to write it */
	private Segments segments;

	private String mappingFilename;

}
