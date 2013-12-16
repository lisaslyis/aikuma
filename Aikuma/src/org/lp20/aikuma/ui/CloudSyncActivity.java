package org.lp20.aikuma.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import com.google.api.client.util.store.DataStoreFactory;
/*
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.storage.Storage;
*/
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.client.extensions.android.AndroidHttp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import org.lp20.aikuma.R;
import org.lp20.aikuma.util.FileIO;

public class CloudSyncActivity extends AikumaActivity {

	private static final String APPLICATION_NAME = "Aikuma";
	private static final File DATA_STORE_DIR = FileIO.getAppRootPath();
	private static FileDataStoreFactory dataStoreFactory;
	private static final JsonFactory JSON_FACTORY =
			JacksonFactory.getDefaultInstance();
	private static HttpTransport httpTransport;
	private static Storage client;

	private static Credential authorize() throws Exception {
		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(CloudSyncActivity.class.getResourceAsStream(
				FileIO.getAppRootPath().getAbsolutePath())));

		Log.i("cloud", " " + clientSecrets.getDetails());

		Set<String> scopes = new HashSet<String>();
		scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
		scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
		scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

		GoogleAuthorizationCodeFlow flow = new 
				GoogleAuthorizationCodeFlow.Builder(
						httpTransport, JSON_FACTORY, clientSecrets, scopes)
				.setDataStoreFactory(dataStoreFactory)
				.build();
		return new AuthorizationCodeInstalledApp(flow, new
				LocalServerReceiver()).authorize("user");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cloud_sync);
	}

	public void onCloudSyncPressed(View view) {
		Log.i("cloud", "starting sync");
		try {
			httpTransport = AndroidHttp.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
			Credential credential = authorize();
			client = new Storage.Builder(httpTransport, JSON_FACTORY,
					credential).setApplicationName(APPLICATION_NAME).build();

			Log.i("cloud", "success... I think.");

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}


}
