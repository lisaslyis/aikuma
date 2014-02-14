package org.lp20.aikuma.util;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import org.lp20.aikuma.ui.SettingsActivity;
/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 *
 * @author	UNK	UNK
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

	private WifiP2pManager mManager;
	private Channel mChannel;
	private SettingsActivity mActivity;

	/**
	 * Constructor
	 *
	 * @param	manager	The WifiP2pManager
	 * @param	channel	The channel that connects the application to the Wifi
	 * p2p framework.
	 * @param	activity	The activity that uses p2p Wifi.
	 */
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
			SettingsActivity activity) {
		super();
		this.mManager = manager;
		this.mChannel = channel;
		this.mActivity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// Check to see if Wi-Fi is enabled and notify appropriate activity
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi P2P is enabled
			} else {
				// Wi-Fi P2P is not enabled
			}
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// Call WifiP2pManager.requestPeers() to get a list of current peers
			if (mManager != null) {
				mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
					public void onPeersAvailable(WifiP2pDeviceList peers) {
						for (WifiP2pDevice device : peers.getDeviceList()) {
							Log.i("p2p", device.toString());
						}
					}
				});
			}
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			// Respond to new connection or disconnections
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
			// Respond to this device's wifi state changing
		}
	}
}
