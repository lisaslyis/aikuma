package au.edu.unimelb.aikuma.util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTPClient;

/**
 * Client that allows the application to sync its data with a server.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 */
public class Client {

	/**
	 * Class constructor.
	 */
	public Client() {
		apacheClient = new FTPClient();
		connected = false;
	}

	/**
	 * Connect to the server.
	 */
	public void connect(String hostname) throws ConnectionException {
		try {
			apacheClient.connect(hostname);
		} catch (SocketException e) {
			throw new ConnectionException(e);
		} catch (UnknownHostException e) {
			throw new ConnectionException(e);
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
		connected = true;
	}

	/**
	 * Disconnect from the server.
	 */
	public void disconnect() throws IOException {
		apacheClient.disconnect();
		connected = false;
	}

	/**
	 * Indicates whether the Client is connected to a server or not.
	 *
	 * @return	true if connected; false otherwise
	 */
	public boolean isConnected() {
		return this.connected;
	}

	/**
	 * The Apache FTPClient used by this Client
	 */
	private FTPClient apacheClient;

	/**
	 * Flag to indicate whether the Client is connected to a server or not.
	 */
	private boolean connected;

	/**
	 * Exception thrown when Client.connect() fails
	 */
	public class ConnectionException extends Exception {
		public ConnectionException() {
			super();
		}
		public ConnectionException(String message) {
			super(message);
		}
		public ConnectionException(String message, Throwable throwable) {
			super(message, throwable);
		}
		public ConnectionException(Throwable throwable) {
			super(throwable);
		}
	}
}
