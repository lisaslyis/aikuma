/*
	Copyright (C) 2013, The Aikuma Project
	AUTHORS: Sangyeop Lee
*/
package org.lp20.aikuma.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lp20.aikuma.Aikuma;
import org.lp20.aikuma.MainActivity;
import org.lp20.aikuma2.R;
import org.lp20.aikuma.model.Recording;
import org.lp20.aikuma.storage.FusionIndex;
import org.lp20.aikuma.storage.Index;
import org.lp20.aikuma.util.AikumaSettings;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

/**
 * @author	Sangyeop Lee	<sangl1@student.unimelb.edu.au>
 * 
 * Activity class dealing with recording item search interface
 */
public class CloudSearchActivity extends AikumaListActivity {
	
	private static final String TAG = "CloudSearchActivity";
	
	private MediaPlayer mediaPlayer;
	
	private EditText searchQueryView;
	
	private QuickActionMenu<Recording> quickMenu;
	
	private List<Recording> recordings;
	private Map<String, String> recordingsDownUri;
	private RecordingArrayAdapter adapter;
	private Parcelable listViewState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cloud_search);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		mediaPlayer = new MediaPlayer();
		setUpQuickMenu();
		recordings = new ArrayList<Recording>();
		recordingsDownUri = new HashMap<String, String>();
		adapter = new RecordingArrayAdapter(this, recordings, quickMenu);
		setListAdapter(adapter);
		
		searchQueryView = (EditText) findViewById(R.id.searchQuery);
		
		searchQueryView.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if((event.getAction() == KeyEvent.ACTION_DOWN && 
						(event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
					onSearchButton(null);
					return true;
				}
				return false;
			}
			
		});		
			
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(listViewState != null) {
			getListView().onRestoreInstanceState(listViewState);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		listViewState = getListView().onSaveInstanceState();
		MainActivity.locationDetector.stop();
	}
	
	
	/**
	 * Search the recordings using the query
	 * 
	 * @param view	View having the query
	 */
	public void onSearchButton(View view) {
		if(!Aikuma.isDeviceOnline())
			Aikuma.showAlertDialog(this, "Network is disconnected");
		
		recordings.clear();
		recordingsDownUri.clear();
		String langQuery = searchQueryView.getText().toString().toLowerCase();
		String emailAccount = AikumaSettings.getCurrentUserId();
		String accessToken = AikumaSettings.getCurrentUserToken();
		
		new GetSearchResultsTask(0, langQuery, emailAccount, accessToken).execute();
	}

	private void showRecordingsOnCloud() {
		if(recordings.size() == 0)
			return;
			
		adapter.notifyDataSetChanged();
	}
	
	// Creates the quickMenu for the original recording 
	//(quickMenu: download)
	private void setUpQuickMenu() {
		quickMenu = new QuickActionMenu<Recording>(this);
		
		if(AikumaSettings.getCurrentUserToken() != null) {
			QuickActionItem samplePlayAct =
					new QuickActionItem("Sample", R.drawable.play_32);
			QuickActionItem downloadAct = 
					new QuickActionItem("Down", R.drawable.download_32);
			
			quickMenu.addActionItem(samplePlayAct);
			quickMenu.addActionItem(downloadAct);
		}
		
		
		//setup the action item click listener
		quickMenu.setOnActionItemClickListener(new QuickActionMenu.OnActionItemClickListener<Recording>() {			
			@Override
			public void onItemClick(int pos, Recording recording) {
				Aikuma.showAlertDialog(CloudSearchActivity.this, "download");
				
				if (pos == 0) { //Download and Play Sample
					Log.i(TAG, recording.getCloudIdentifier());
					String downUri = recordingsDownUri.get(recording.getId());
					//TODO: If preview file doesn't exist, Download the sample(preview)
					
					// Play sample
					//setUpPlayer(recording);
					//mediaPlayer.start();	
	
				} else if (pos == 1) { //Download the item
					Log.i(TAG, recording.getId());
					// TODO: Search the files belong to the item_id except for a sample file
					
					// TODO: Collect speaker IDs and download the files belonging to speaker_id
					
					// TODO: Re-indexing
					
				}
			}
		});
	}
	
	private void setUpPlayer(Recording recording) {
		mediaPlayer.reset();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(recording.getPreviewFile().getCanonicalPath());
			mediaPlayer.prepare();
		} catch (IOException e) {
			Log.e(TAG, "Failed to prepare MediaPlayer: " + e.getMessage());
		}
	}
	
	/**
     * Inner class to get an access token from google server
     * @author Sangyeop Lee	<sangl1@student.unimelb.edu.au>
     *
     */
    private class GetSearchResultsTask extends AsyncTask<Void, Void, Boolean>{
    	
    	private static final String TAG = "GetSearchResultsTask";

    	private int queryType;
    	private String mEmailAccount;
    	private String mAccessToken;
    	private String mQuery;

        GetSearchResultsTask(int queryType, String query, String emailAccount, String accessToken) {
        	this.queryType = queryType;
        	this.mEmailAccount = emailAccount;
        	this.mAccessToken = accessToken;
        	this.mQuery = query;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
        	FusionIndex fi = new FusionIndex(mAccessToken);
        	Map<String, String> constraints = new TreeMap<String, String>();
        	
        	if(queryType == 0) {
        		constraints.put("languages", mQuery);
            	constraints.put("file_type", "source");
            	//constraints.put("user_id", mEmailAccount);
            	
            	fi.search(constraints, new Index.SearchResultProcessor() {
    				@Override
    				public boolean process(Map<String, String> result) {
    					String metadataJSONStr = result.get("metadata");
    					String downloadUri = result.get("data_store_uri");
    					Log.i(TAG, metadataJSONStr);
    					Log.i(TAG, "downUri: " + downloadUri);
    					JSONParser parser = new JSONParser();
    					try {
    						JSONObject jsonObj = (JSONObject) parser.parse(metadataJSONStr);
    						Recording recording = Recording.read(jsonObj);
    						recordings.add(recording);
    						recordingsDownUri.put(recording.getId(), downloadUri);
    					} catch (ParseException e) {
    						Log.e(TAG, e.getMessage());
    					} catch (IOException e) {
    						Log.e(TAG, e.getMessage());
    					}
    					
    					return true;
    				}
    			});
            	
            	if(recordings.size() > 0)
            		return true;
            	else
            		return false;
            	
        	} else if (queryType == 1) {
        		constraints.put("item_id", mQuery);
        		
        		fi.search(constraints, new Index.SearchResultProcessor() {
    				@Override
    				public boolean process(Map<String, String> result) {
    					// TODO: Collect metadata for recording/speaker
    					// TODO: Collect downloadUri for all files(recording, preview, mapping, ...)
    					
    					return true;
    				}
    			});
        		
        	}
        	return false;
        }
        
        @Override
        protected void onPostExecute(Boolean result) {
        	if(result && queryType == 0)
        		showRecordingsOnCloud();
        }
    }
}
