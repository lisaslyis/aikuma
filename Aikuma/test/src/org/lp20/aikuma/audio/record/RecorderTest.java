package org.lp20.aikuma.audio.record;

import android.media.MediaPlayer;
import android.test.AndroidTestCase;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import java.io.File;

/**
 * Tests for Recorder.
 *
 * Note that these tests require manual intervention.
 * Run 'adb logcat -s "ManualTesting"' and follow the instructions.
 */
public class RecorderTest extends AndroidTestCase {
	public void testRecorder() throws Exception {
		File f = new File("/mnt/sdcard/aikuma/testrecordings/testrecord1.wav");
		Log.i("RecorderTest", "here3");
		Recorder recorder = new Recorder(f, 16000);
		recorder.listen();
		Log.i("RecorderTest", "here2");
		Log.i("ManualTesting", "Recording started.");
		TimeUnit.SECONDS.sleep(10);
		Log.i("RecorderTest", "here4");
		recorder.stop();
		Log.i("ManualTesting", "Recording stopped.");

		Log.i("RecorderTest", "here");

		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(f.getPath());
		mediaPlayer.prepare();
		mediaPlayer.start();
		Log.i("ManualTesting", "Playback started.");
		TimeUnit.SECONDS.sleep(10);
	}
}
