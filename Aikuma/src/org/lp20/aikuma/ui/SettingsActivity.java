/*
	Copyright (C) 2013, The Aikuma Project
	AUTHORS: Oliver Adams and Florian Hanke
*/
package org.lp20.aikuma.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import org.lp20.aikuma.R;
import org.lp20.aikuma.util.FileIO;
import org.lp20.aikuma.util.UsageUtils;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Http;
import com.soundcloud.api.Params;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * The mother activity for settings - hosts buttons that link to various
 * specific settings activities.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 */
public class SettingsActivity extends AikumaActivity {

	/**
	 * The default default sensitivity
	 */
	public static final int DEFAULT_DEFAULT_SENSITIVITY  = 4000;

	private SeekBar sensitivitySlider;
	private int defaultSensitivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupSensitivitySlider();
	}

	@Override
	public void onPause() {
		super.onResume();
		try {
			FileIO.writeDefaultSensitivity(defaultSensitivity);
			Log.i("132", "wrote " + defaultSensitivity);
		} catch (IOException e) {
			//If it can't be written then just toast it.
			Toast.makeText(this, 
					"Failed to write default sensitivity setting to file", 
					Toast.LENGTH_LONG).show();
		}
	}

	// Define the sensitivity slider's functionality
	private void setupSensitivitySlider() {

		//Create the sensitivity slider functionality.
		sensitivitySlider = (SeekBar) findViewById(R.id.SensitivitySlider);
		//Read the sensitivity and set the slider accordingly.
		try {
			defaultSensitivity = FileIO.readDefaultSensitivity();
		} catch (IOException e) {
			defaultSensitivity = DEFAULT_DEFAULT_SENSITIVITY;
		}

		sensitivitySlider.setMax(DEFAULT_DEFAULT_SENSITIVITY*2);
		sensitivitySlider.setProgress(defaultSensitivity);

		sensitivitySlider.setOnSeekBarChangeListener(
			new OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar sensitivitySlider,
						int sensitivity, boolean fromUser) {
					if (sensitivity == 0) {
						defaultSensitivity = 1;
					} else {
						defaultSensitivity = sensitivity;
					}
				}
				public void onStartTrackingTouch(SeekBar seekBar) {}
				public void onStopTrackingTouch(SeekBar seekBar) {}
			}
		);
	}

	/**
	 * Starts up the default languages activity.
	 *
	 * @param	view	The default language activity button.
	 */
	public void onDefaultLanguagesButton(View view) {
		Intent intent = new Intent(this, DefaultLanguagesActivity.class);
		startActivity(intent);
	}

	/**
	 * Starts up the sync settings activity.
	 *
	 * @param	view	The sync settings activity button.
	 */
	public void onSyncSettingsButton(View view) {
		Intent intent = new Intent(this, SyncSettingsActivity.class);
		startActivity(intent);
	}

	/**
	 * Tests syncing some files to SoundCloud
	 *
	 * @param	_button	The SoundCloud sync button that was pressed.
	 */
	public void onSoundCloudSyncButton(View _button) {
		downloadTrackInfo();
	}

	/**
	 * Gets track information from SoundCloud
	 */
	private void downloadTrackInfo() {
		ApiWrapper wrapper = 
			new ApiWrapper("faf6c1ce9bcbae1975eece02d5040e80", 
				"d4c30a13a73f83d503ef8cdc7dbd0c91", null, null);
		Token token;
		try {
			token = wrapper.login("oliver.adams@gmail.com", "aikumatest");
			Toast.makeText(this, "token: " + token, Toast.LENGTH_LONG).show();
			HttpResponse resp = wrapper.get(Request.to(Endpoints.MY_TRACKS));
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.i("soundcloud","\n" + Http.formatJSON(Http.getString(resp)));
			} else {
				Log.i("soundcloud","Invalid status received: " + resp.getStatusLine());
			}

		} catch (IOException e) {
			Toast.makeText(this, "login exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}/* catch (JSONException e) {
			Toast.makeText(this, "json exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}*/
	}

	/**
	 * Pushes a dummy file to SoundCloud
	 */
	private void pushTestFile() {
		ApiWrapper wrapper = 
				new ApiWrapper("faf6c1ce9bcbae1975eece02d5040e80", "d4c30a13a73f83d503ef8cdc7dbd0c91", null, null);
		Token token;
		try {
			token = wrapper.login("oliver.adams@gmail.com", "aikumatest");
			Toast.makeText(this, "token: " + token, Toast.LENGTH_LONG).show();
			final File file = new File(FileIO.getAppRootPath(), "test.wav");
			HttpResponse resp = wrapper.post(Request.to(Endpoints.TRACKS)
					.add(Params.Track.TITLE, file.getName())
					.add(Params.Track.TAG_LIST, "demo upload")
					.withFile(Params.Track.ASSET_DATA, file)
					.setProgressListener(new Request.TransferProgressListener()
					{
						@Override
						public void transferred(long amount) {
							Log.i("soundcloud", "amount: " + amount);
						}
					}));
			if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				Log.i("soundcloud", "\n201 Created "+resp.getFirstHeader("Location").getValue());

				// dump the representation of the new track
				//Log.i("soundcloud","\n" + Http.getJSON(resp).toString(4));
			} else {
				Log.i("soundcloud", "Invalid status received: " + resp.getStatusLine());
			}

		} catch (IOException e) {
			Toast.makeText(this, "login exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}/* catch (JSONException e) {
			Toast.makeText(this, "json exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}*/
	}
}
