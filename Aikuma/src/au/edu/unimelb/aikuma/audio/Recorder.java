package au.edu.unimelb.aikuma.audio;

/**
 * A recorder is used to get input from a microphone and output into a file.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 */
public class Recorder {

	/**
	 * Creates a simple recorder that records all input at 44100Hz
	 */
	public Recorder() {
		this(new SimpleAnalyzer(), 44100);
	}

	/**
	 * Standard Constructor
	 *
	 * @param	analyzer	An audio analyzer that determines whether the
	 * Recorder should record or ignore the input.
	 */
	public Recorder(Analyzer analyzer, int sampleRate) {
		this.analyzer = analyzer;
		setUpListener(sampleRate);
		setUpFile();
	}

	/*
	public void prepare
	public void listen
	public void pause
	public void stop
	public boolean isRecording / isListening
	*/

	/**
	 * Sets up the listening device (the microphone)
	 *
	 * @param	sampleRate	The sample rate to record at.
	 */
	protected void setUpListener(int sampleRate) {
		listener = getListener(
				sampleRate,
				AudioFormat.ENCODING_PCM_16BIT,// Guaranteed to be supported
				AudioFormat.CHANNEL_IN_MONO);// Guaranteed to be supported
		do {} while (listener.getState() != AudioRecord.STATE_INITIALIZED);
	}

	/**
	 * Gets a listener for the internal/external microphone.
	 *
	 * @param	sampleRate	The sample rate to record at.
	 * @param	audioFormat	AudioFormat.ENCODING_PCM_16BIT or
	 * AudioFormat.ENCODING_PCM_8BIT
	 * @param	channelConfig	AudioFormat.CHANNEL_IN_MONO or
	 * AudioFormat.CHANNEL_IN_STEREO
	 */
	protected static AudioRecord getListener(
			int sampleRate, int audioFormat, int channelConfig) {

		int sampleSize;
		if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
			sampleSize = 16;
		} else if (audioFormat == AudioFormat.ENCODING_PCM_8BIT) {
			sampleSize = 8;
		}

		int numberOfChannels;
		if (channelConfig == AudioFormat.CHANNEL_IN_MONO) {
			numberOfChannels = 1;
		} else if (channelConfig == AudioFormat.CHANNEL_IN_STEREO) {
			numberOfChannels = 2;
		}

		// The period used for callbacks to onBufferFull.
		int framePeriod = sampleRate * 120 / 1000;

		// The buffer needed for the above period.
		int bufferSize = framePeriod * 2 * sampleSize * numberOfChannels / 8;

		return new AudioRecord(
				MediaRecorder.AudioSource.MIC,
				sampleRate,
				channelConfig,
				audioFormat,
				bufferSize);
	}

	/**
	 * A buffer used in ferrying samples to a PCM based file.
	 */
	protected short[] buffer = new short[1000];
}
