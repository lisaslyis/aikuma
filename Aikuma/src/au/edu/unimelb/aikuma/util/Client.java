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
	public Client(File serverBaseDir) {
		apacheClient = new FTPClient();
		setClientBaseDir(FileIO.getAppRootPath());
		setServerBaseDir(serverBaseDir);
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
	 * Sync the client with the server.
	 *
	 * @return	true if completely successful; false otherwise.
	 */
	public boolean sync() {
		boolean pushResult = push();
		boolean pullResult = pull();
		return pushResult && pullResult;
	}

	/**
	 * Push files to server that are on the client but not the server.
	 *
	 * @return	true if successful; false otherwise.
	 */
	public boolean push() {
		return pushDirectory(".");
	}

	/**
	 * Pull file from the server that are on the server but not on the client.
	 *
	 * @return	true if successful; false otherwise.
	 */
	public boolean pull() {
		return pullDirectory(".");
	}

	/**
	 * Recursively push the directory to server.
	 *
	 * @param	directoryPath	the relative path from the base directory
	 */
	public boolean pushDirectory(File clientPath) {
		// Get the specified client side directory.
		File clientDir;
		if (clientBaseDir.endsWith("/")) {
			clientDir = new File(clientBaseDir + directoryPath);
		} else {
			clientDir = new File(clientBaseDir + "/" + directoryPath);
		}
		if (!clientDir.isDirectory()) {
			Log.i("ftp", "1");
			return false;
		}

		// Attempt to make the directory on the server side and then change
		// into it.
		try {
			apacheClient.makeDirectory(
					serverBaseDir + "/" + directoryPath);
			apacheClient.changeWorkingDirectory(
					serverBaseDir + "/" + directoryPath);
			Log.i("ftp", "now in " + serverBaseDir + "/" + directoryPath);
			Log.i("ftp", "pwd1: " + apacheClient.printWorkingDirectory());
		} catch (IOException e) {
			Log.i("ftp", "2");
			return false;
		}

		try {
			List<String> clientFilenames = Arrays.asList(clientDir.list());
			List<String> serverFilenames = Arrays.asList(
					apacheClient.listNames());
			File file = null;
			Boolean result = null;
			for (String filename : clientFilenames) {
				file = new File(clientDir.getPath() + "/" + filename);
				if (!file.getName().endsWith(".inprogress")) {
					if (!file.isDirectory()) {
						if (!serverFilenames.contains(filename)) {
							result = pushFile(directoryPath, file);
							if (!result) {
								Log.i("ftp", "3: " + directoryPath + ", " +
								file);
								return false;
							}
						}
					} else {
						apacheClient.makeDirectory(filename);
						result = pushDirectory(directoryPath + "/" + filename);
						apacheClient.changeWorkingDirectory(
								serverBaseDir + "/" + directoryPath);
						if (!result) {
							Log.i("ftp", "4");
							return false;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("ftp", "5");
			return false;
		}

		return true;
	}

	/**
	 * Recursively pull the directory from the server.
	 *
	 * @param	directoryPath	the relative path from the base directory
	 */
	public boolean pullDirectory(String directoryPath) {

		// Attempt to change to the directory on the server side.
		try {
			boolean result;
			result = apacheClient.changeWorkingDirectory(
					serverBaseDir + "/" + directoryPath);
			if (!result) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}

		File clientDir = new File(clientBaseDir + directoryPath);
		clientDir.mkdirs();

		try {
			List<String> clientFilenames = Arrays.asList(clientDir.list());
			List<FTPFile> serverFiles = Arrays.asList(
					apacheClient.listFiles());
			File file = null;
			OutputStream stream = null;
			Boolean result = null;
			for (FTPFile serverFile : serverFiles) {
				file = new File(
						clientBaseDir + directoryPath + "/" +
						serverFile.getName());
				if (!file.getName().endsWith(".inprogress")) {
					if (serverFile.isDirectory()) {
						file.mkdirs();
						result = pullDirectory(
								directoryPath + "/" + serverFile.getName());
						if (!result) {
							return false;
						}
					} else {
						if (!clientFilenames.contains(serverFile.getName())) {
							result = pullFile(directoryPath, file);
							if (!result) {
								return false;
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Push an individual file to the server.
	 *
	 * @param	directoryPath	Server side directory to push the file to.
	 * @param	file	The file to be pushed.
	 * @return	true if successful; false otherwise.
	 */
	public boolean pushFile(String directoryPath, File file) {
		boolean result = false;
		try {
			InputStream stream = new FileInputStream(file);
			result = apacheClient.storeFile(
					serverBaseDir + "/" + directoryPath + "/" + file.getName() +
							".inprogress",
					stream);
			stream.close();
			apacheClient.rename(
					serverBaseDir + "/" + directoryPath + "/" + file.getName() +
							".inprogress",
					serverBaseDir + "/" + directoryPath + "/" + file.getName());
		} catch (IOException e) {
			Log.e("ftp", "exception for 3", e);
			return false;
		}
		return result;
	}

	/**
	 * Pull an individual file from the server.
	 *
	 * @param	file	The file to be pulled.
	 * @return	true if successful; false otherwise.
	 */
	public boolean pullFile(String directoryPath, File file) {
		Log.i("zxcv", "pullFile: " + directoryPath + " " + file.getName());
		boolean result = false;
		try {
			File inProgressFile = new File(file.getPath() + ".inprogress");
					/*clientBaseDir + directoryPath + "/" + file.getName() +
							".inprogress");*/
			Log.i("zxcv", "inprogressfilename  " + inProgressFile.getPath());
			OutputStream stream = new FileOutputStream(inProgressFile);
			result = apacheClient.retrieveFile(
					serverBaseDir + "/" + directoryPath + "/" +
							file.getName(),
					stream);
			stream.close();
			Log.i("zxcv", "blah: " + inProgressFile.getName() + " " + result);
			inProgressFile.renameTo(file);
		} catch (IOException e) {
			Log.e("zxcv", "borg" , e);
			return false;
		}
		return result;
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
	 * Set the path to the server's application directory.
	 */
	private setServerBaseDir(File serverBaseDir) {
		this.serverBaseDir = serverBaseDir;
	}

	/**
	 * Set the path to the application directory on the client.
	 */
	private setClientBaseDir(File clientBaseDir) {
		this.clientBaseDir = clientBaseDir;
	}

	/**
	 * The Apache FTPClient used by this Client
	 */
	private FTPClient apacheClient;

	/**
	 * The path to the application directory on the client.
	 */
	private File clientBaseDir;

	/**
	 * The path to the application directory on the server.
	 */
	private File serverBaseDir;
}
