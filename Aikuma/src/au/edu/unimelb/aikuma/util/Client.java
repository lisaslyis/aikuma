package au.edu.unimelb.aikuma.util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTP;
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
	}

	/**
	 * Disconnect from the server.
	 */
	public void disconnect() throws IOException {
		apacheClient.disconnect();
	}

	/**
	 * Indicates whether the Client is connected to a server or not.
	 *
	 * @return	true if connected; false otherwise.
	 */
	public boolean isConnected() {
		return apacheClient.isConnected();
	}

	/**
	 * Attempt to log in to the server using a username and password.
	 *
	 * @param	username	The username to login under.
	 * @param	password	The password to use.
	 * @return	true if successful; false otherwise.
	 */
	public boolean login(String username, String password) throws IOException,
			ConnectionException {
		apacheClient.login(username, password);
		return apacheClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	/**
	 * Logout of the server.
	 *
	 * @return	true if successful; false otherwise.
	 */
	public boolean logout() throws IOException {
		return apacheClient.logout();
	}

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

	/**
	 * The Apache FTPClient used by this Client
	 */
	private FTPClient apacheClient;
}
