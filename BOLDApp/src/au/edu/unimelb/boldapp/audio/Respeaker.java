package au.edu.unimelb.aikuma.audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

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

import au.edu.unimelb.aikuma.audio.NewSegments.Segment;

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
public class Respeaker extends Recorder {

	/**
	 * Indicates whether the recording has finished playing
	 */
	private boolean finishedPlaying;

	/** Player to play the original with. */
	public Player player;

	private NewSegments segments;

	private Long originalStartOfSegment;
	private Long originalEndOfSegment;
	private Long respeakingStartOfSegment;
	private Long respeakingEndOfSegment;

	private String mappingFilename;

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

	public void setSensitivity(int divisor) {
		this.analyzer = new ThresholdSpeechAnalyzer(88, 3,
				new AverageRecognizer(divisor, divisor));
		Log.i("issue35", "lol");
	}

	/** Default constructor. */
	public Respeaker() {
		super();
		setFinishedPlaying(false);
		this.player = new Player();
		segments = new NewSegments();
		//0.18 is the highest volume that can be set without causing the
		//feedback problem for the respeak activity on the cheap huawei phones.
		//this.player.setVolume(0.18f,0.18f);
	}

	public Respeaker(Context context) {
		super(context);
		setFinishedPlaying(false);
		this.player = new Player();
		segments = new NewSegments();
		//0.18 is the highest volume that can be set without causing the
		//feedback problem for the respeak activity on the cheap huawei phones.
		//this.player.setVolume(0.18f,0.18f);
	}

	/** Default constructor. */
	public Respeaker(ThresholdSpeechAnalyzer analyzer, boolean
			shouldPlayThroughSpeaker) {
		super(analyzer);
		setFinishedPlaying(false);
		this.player = new Player();
		if (shouldPlayThroughSpeaker) {
			this.playThroughSpeaker();
		} else {
			this.playThroughEarpiece();
		}
		segments = new NewSegments();
		//0.18 is the highest volume that can be set without causing the
		//feedback problem for the respeak activity on the cheap huawei phones.
		//this.player.setVolume(0.18f,0.18f);
	}

	public Respeaker(ThresholdSpeechAnalyzer analyzer, Context context) {
		super(analyzer, context);
		setFinishedPlaying(false);
		this.player = new Player();
		segments = new NewSegments();
		//0.18 is the highest volume that can be set without causing the
		//feedback problem for the respeak activity on the cheap huawei phones.
		//this.player.setVolume(0.18f,0.18f);
	}

  
	/** Prepare the respeaker by setting a source file and a target file. */
	public void prepare(String sourceFilename, String targetFilename,
			String mappingFilename) {
		player.prepare(sourceFilename);
		super.prepare(targetFilename);
		this.mappingFilename = mappingFilename;
	}

	@Override
	public void listen() {
		super.listen();
		player.play();
	}

	public void listenToSpeaker() {
		super.listen();
	}

	/**
	 * To listen to the final piece of annotation after playing of the original
	 * has been completed.
	 */
	public void listenAfterFinishedPlaying() {
		super.listen();
	}

	public void play() {
		originalStartOfSegment = player.getCurrentSample();
		player.play();
	}

	@Override
	public void stop() {
		super.stop();
		player.stop();
		if (respeakingStartOfSegment != null) {
			Segment originalSegment = new Segment(
				originalStartOfSegment,
				originalEndOfSegment);
			Segment respeakingSegment = new Segment(
					respeakingStartOfSegment,
					file.getCurrentSample());
			segments.put(originalSegment, respeakingSegment);
		}
		try {
			Log.i("segments", "Samples file name: " + mappingFilename);
			segments.write(new File(mappingFilename));
		} catch (IOException e) {
			// Couldn't write mapping.
			Log.e("segments", "couldn't write mapping", e);
		}
	}

	/** Pause listening to the microphone. */
	@Override
	public void pause() {
		super.pause();
		player.pause();
		originalEndOfSegment = player.getCurrentSample();
		respeakingStartOfSegment = file.getCurrentSample();
		// Reset the analyzer to default values so it doesn't assume speech on
		// resuming.
		analyzer.reset();
	}

	/** Resume playing. */
	public void resume() {
		super.listen();
		originalStartOfSegment = player.getCurrentSample();
		switchToPlay();
	}

	/** Rewinds the player. */
	public void rewind(int milliseconds) {
		player.rewind(milliseconds);
	}
  
	/*
	 * Switches the mode to play mode for audio driven respeaking.
	 *
	 */
	protected void switchToPlay() {
		rewind(650);
		respeakingEndOfSegment = file.getCurrentSample();
		storeSegmentEntry();
		originalStartOfSegment = player.getCurrentSample();
		player.resume();
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


	@Override
	public void audioTriggered(short[] buffer, boolean justChanged) {
		file.write(buffer);
		if (justChanged) {
			respeakingStartOfSegment = file.getCurrentSample();
			player.pause();
		}
	}

	@Override
	public void silenceTriggered(short[] buffer, boolean justChanged) {
		if (justChanged) {
			//If the recording has finished playing and we've just been
			//annotating at the end, then we're finished and can stop the
			//respeaking.
			if (getFinishedPlaying()) {
				respeakingEndOfSegment = file.getCurrentSample();
				storeSegmentEntry();
				super.stop();
			} else {
				switchToPlay();
			}
		}
	}

	public void playThroughEarpiece() {
		player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

	}

	public void playThroughSpeaker() {
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}
}
