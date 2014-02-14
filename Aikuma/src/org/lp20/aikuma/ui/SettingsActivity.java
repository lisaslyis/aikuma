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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import org.lp20.aikuma.util.WiFiDirectBroadcastReceiver;

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

	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(this, getMainLooper(), null);
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupSensitivitySlider();

		// Register the broadcast receiver with the intent values to be
		// matched.
		registerReceiver(mReceiver, mIntentFilter);

		/*
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.i("p2p", "successfully discovered peers");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.i("p2p", "unsuccesful attempt to discover peers. reasonCode = "
				+ reasonCode);
			}
		});
		*/

		mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.i("p2p", "successfully create group");
				mManager.requestGroupInfo(mChannel,
						new WifiP2pManager.GroupInfoListener() {
					public void onGroupInfoAvailable(WifiP2pGroup group) {
						Log.i("p2p", "group.toString(): " + group.toString());
						Log.i("p2p", "group.getNetworkName(): " +
								group.getNetworkName());
						Log.i("p2p", "group.getPassphrase(): " +
								group.getPassphrase());
						Log.i("p2p", "group.getOwner(): " +
								group.getOwner());
					}
				});
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.i("p2p", "unsuccessful attempt to create group. reasonCode = "
				+ reasonCode);
			}
		});

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

		/*
		mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.i("p2p", "successfully removed group");
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.i("p2p", "unsuccessful attempt to removed group. reasonCode = " + reasonCode);
			}
		});
		*/

		// Unregister the broadcast receiver
		unregisterReceiver(mReceiver);
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
	 * When the p2pwifi button is pressed
	 *
	 * @param	view	the button
	 */
	public void p2pWifi(View view) {
		mManager.requestConnectionInfo(mChannel, new
				WifiP2pManager.ConnectionInfoListener() {
			public void onConnectionInfoAvailable(WifiP2pInfo info) {
				Log.i("p2p", "connection info: " + info);
			}
		});
	}
}
