package au.edu.unimelb.aikuma.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.test.AndroidTestCase;
import android.util.Log;
import java.util.concurrent.TimeUnit;

/**
 * Tests for Recorder. 
 *
 * Note that these tests require manual intervention.
 * Run 'adb logcat -s "ManualTesting"' and follow the instructions.
 */
public class RecorderTest extends AndroidTestCase {

	public void testRecorder1() throws Exception {
		Log.i("ManualTesting",
			"Manual test 1: Recorder is recording for 10 seconds. Make some" +
			"noise and compare with the playback");
		Recorder recorder = new Recorder();
		recorder.prepare("/mnt/sdcard/bold/testrecordings/testrecord1.wav");
		recorder.listen();
		Log.i("ManualTesting", "Recording started.");
		TimeUnit.SECONDS.sleep(10);
		recorder.stop();
		Log.i("ManualTesting", "Recording stopped.");

		MediaPlayer mediaPlayer = new MediaPlayer();
		//mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setDataSource(
				"/mnt/sdcard/bold/testrecordings/testRecord1.wav");
		mediaPlayer.setOnCompletionListener(
				new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer _) {
						Log.i("ManualTesting", "Playback complete.");
					}
				});
		mediaPlayer.prepare();
		Log.i("ManualTesting", "Playback Starting.");
		mediaPlayer.start();
		TimeUnit.SECONDS.sleep(10);
		Log.i("ManualTesting", "Test ending.");
	}

	public void testRecorder2() throws Exception {
		Recorder recorder = new Recorder();
		recorder.prepare("/mnt/sdcard/bold/testrecordings/testrecord1.wav");
		recorder.listen();
		recorder.listen();
		recorder.listen();
		recorder.listen();
		recorder.stop();
	}

}
