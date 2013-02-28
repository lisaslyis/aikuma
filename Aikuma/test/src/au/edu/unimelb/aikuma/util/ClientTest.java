package au.edu.unimelb.aikuma.util;

import android.test.AndroidTestCase;
import android.util.Log;
import au.edu.unimelb.aikuma.util.Client.ConnectionException;
import java.io.IOException;

public class ClientTest extends AndroidTestCase {

	/**
	 * The client to be tested
	 */
	protected Client client;

	/**
	 * Test for a successful connection
	 */
	public void testConnection() throws IOException, ConnectionException {
		client = new Client();
		client.connect("192.168.1.1");
		assertTrue(client.isConnected());
		client.disconnect();
		assertTrue(!client.isConnected());
	}

	/**
	 * Test for an unsuccessful connection
	 */
	public void testConnectionFail() throws IOException {
		client = new Client();
		try {
			client.connect("SillyHostname");
		} catch (ConnectionException e) {
			// It's cool.
		}
		assertTrue(!client.isConnected());
		client.disconnect();
		assertTrue(!client.isConnected());
	}
}
