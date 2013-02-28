package au.edu.unimelb.aikuma.util;

import android.test.AndroidTestCase;
import android.util.Log;
import au.edu.unimelb.aikuma.util.Client.ConnectionException;

public class ClientTest extends AndroidTestCase {

	/**
	 * The client to be tested
	 */
	protected Client client;

	public void testConnection() {
		client = new Client();
		boolean connected = true;
		try {
			client.connect("192.168.1.1");
		} catch (ConnectionException e) {
			Log.e("ClientTest", "message", e);
			connected = false;
		}
		assertTrue(connected);
	}

	public void testConnectionFail() {
		client = new Client();
		boolean connected = true;
		try {
			client.connect("SillyHostname");
		} catch (ConnectionException e) {
			connected = false;
		}
		assertTrue(!connected);
	}
}
