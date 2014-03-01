/*
	Copyright (C) 2013, The Aikuma Project
	AUTHORS: Oliver Adams and Florian Hanke
*/
package org.lp20.aikuma.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.lp20.aikuma.MainActivity;
import org.lp20.aikuma.http.Server;
import org.lp20.aikuma.R;

import java.util.UUID;
import android.provider.MediaStore;
import org.lp20.aikuma.util.VideoUtils;
import android.content.Intent;

/**
 * Class that unifies some inter-activity navigation code.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 */
public class MenuBehaviour {

	/**
	 * The sole constructor, requires an activity.
	 *
	 * @param	activity	The activity whose menu behaviour we are defining.
	 */
	public MenuBehaviour(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Creates the options menu appropriate for the activity.
	 *
	 * @param	menu	The menu in question.
	 * @return	Always true, so that when Activity.onCreateOptionsMenu uses
	 * this implementation, the display is shown.
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = activity.getMenuInflater();
		if (activity instanceof MainActivity) {
			inflater.inflate(R.menu.main, menu);
		} else {
			inflater.inflate(R.menu.other, menu);
		}
		return true;
	}

	/**
	 * Defines what happens when an menu item is selected and safe transitions
	 * are off.
	 *
	 * @param	item	The menu item selected.
	 * @return	Always true.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home:
				goToMainActivity();
				return true;
			case R.id.record:
				intent = new Intent(activity, RecordActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.speakers:
				intent = new Intent(activity, MainSpeakersActivity.class);
				activity.startActivity(intent);
			case R.id.video_record:
				intent = new Intent(activity, VideoReviewActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.help:
				openHelpInBrowser();
				return true;
			case R.id.settings:
				intent = new Intent(activity, SettingsActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.about:
				intent = new Intent(activity, AboutActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.start_http_server:
				intent = new Intent(activity, HttpServerActivity.class);
				activity.startActivity(intent);
				return true;
			default:
				return true;
		}
	}

	/**
	 * Defines what happens when an menu item is selected and safe transitions
	 * are on.
	 *
	 * @param	item	The menu item selected.
	 * @param	safeActivityTransitionMessage	The message to display warning
	 * about data loss.
	 * @return	Always true.
	 */
	public boolean safeOnOptionsItemSelected(MenuItem item,
			String safeActivityTransitionMessage) {
		Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home:
				safeGoToMainActivity(safeActivityTransitionMessage);
				return true;
			case R.id.record:
				intent = new Intent(activity, RecordActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.speakers:
				intent = new Intent(activity, MainSpeakersActivity.class);
				activity.startActivity(intent);
			case R.id.video_record:
				intent = new Intent(activity, VideoReviewActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.help:
				openHelpInBrowser();
				return true;
			case R.id.settings:
				intent = new Intent(activity, SettingsActivity.class);
				activity.startActivity(intent);
				return true;
			case R.id.about:
				intent = new Intent(activity, AboutActivity.class);
				activity.startActivity(intent);
				return true;
			default:
				return true;
		}
	}

	/**
	 * Opens the howto from lp20.org in a browser.
	 */
	private void openHelpInBrowser() {
		Uri uri = Uri.parse("http://lp20.org/aikuma/howto.html");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(intent);
	}

	/**
	 * Simply transitions to the MainActivity popping every Activity off the stack.
	 */
	private void goToMainActivity() {
		Intent intent = new Intent(activity, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

	/**
	 * Transitions to the MainActivity prompting the user with some dialogue
	 * with the supplied text for the message and the positive and negative
	 * buttons.
	 *
	 * @param	safeActivityTransitionMessage	The string to display in a warning message.
	 */
	public void safeGoToMainActivity(String safeActivityTransitionMessage) {
		String message = DEFAULT_MESSAGE;
		if (safeActivityTransitionMessage != null) {
			message = safeActivityTransitionMessage;
		}
		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("Discard",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent =
									new Intent(activity, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								activity.startActivity(intent);
							}
						})
				.setNegativeButton("Cancel", null)
				.show();
	}

	/**
	 * Allows the activity to use the back button to return to the previous
	 * activity in the stack, while ensuring the user is aware they'll lose
	 * data.
	 *
	 * @param	safeActivityTransitionMessage	The string to display in a warning message.
	 */
	public void safeGoBack(String safeActivityTransitionMessage) {
		String message = DEFAULT_MESSAGE;
		if (safeActivityTransitionMessage != null) {
			message = safeActivityTransitionMessage;
		}
		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("Discard",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								activity.finish();
							}
						})
				.setNegativeButton("Cancel", null)
				.show();
	}

	/**
	 * Requests a video to be recorded and forwards the information to the
	 * VideoReview activity.
	 */
	 /*
	public void recordVideo() {
		int ACTION_TAKE_VIDEO = 1;
		UUID uuid = UUID.randomUUID();
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE,
		VideoReviewActivity.class);
		videoFile = VideoUtils.getNoSyncVideoFile(uuid);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
		startActivityForResult(intent, ACTION_TAKE_VIDEO);
	}
	*/

	private Activity activity;
	private String DEFAULT_MESSAGE = "This will discard the new data. Are you sure?";
}
