package au.edu.unimelb.aikuma.audio;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Tests for Recorder. 
 *
 * Note that these tests require manual intervention.
 * Run 'adb logcat -s "manual testing"' and follow the instructions.
 */
public class RecorderTest extends AndroidTestCase {

	public void testRecorder1() throws Exception {
		Log.i("manual testing",
			"Manual test 1: Recorder is recording for 10 seconds. Make some"
			"noise and compare with the playback");
		Recorder recorder = new Recorder();
		recorder.prepare("/mnt/sdcard/bold/target_file.wav");
		recorder.listen();
		Log.i("manual testing", "Recording started.");
		Thread.sleep(10000);
		recorder.stop();
		Log.i("manual testing", "Recording stopped.");
	}

}
