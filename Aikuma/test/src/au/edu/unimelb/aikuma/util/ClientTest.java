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

	/**
	 * Tests logging in, and that the Client stays logged in if it has
	 * previously been logged in and then tries to log in unsuccessfully as
	 * another user.
	 */
	public void testLogin() throws IOException, ConnectionException {
		client = new Client();
		client.connect("192.168.1.1");
		assertTrue(client.isConnected());
		assertTrue(client.login("admin", "admin"));
		assertTrue(!client.login("someguy", "someguy"));
		client.disconnect();
		assertTrue(!client.isConnected());
	}

}
