package org.lp20.aikuma.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;
import org.lp20.aikuma.model.Speaker;
import org.lp20.aikuma.R;

public class SpeakersActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speakers);
	}

	@Override
	public void onResume() {
		super.onResume();

		speakers = Speaker.readAll();
		ArrayAdapter<Speaker> adapter =
				new SpeakerArrayAdapter(this, speakers);
		setListAdapter(adapter);
	}

	public void addSpeakerButtonPressed(View view) {
		Intent intent = new Intent(this, AddSpeakerActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent();
		intent.putExtra("speaker", (Speaker)l.getItemAtPosition(position));
		setResult(RESULT_OK, intent);
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.record:
				new AlertDialog.Builder(this)
						.setMessage(R.string.restart_recording)
						.setPositiveButton(R.string.discard, new
						DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent =
									new Intent(SpeakersActivity.this,
												RecordActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								SpeakersActivity.this.finish();
								startActivity(intent);
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.show();
				return true;
			case R.id.mainlist:
				new AlertDialog.Builder(this)
						.setMessage(R.string.discard_dialog)
						.setPositiveButton(R.string.discard, new
						DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent =
									new Intent(SpeakersActivity.this,
												MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								SpeakersActivity.this.finish();
								startActivity(intent);
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.show();
				return true;
			case R.id.settings:
				new AlertDialog.Builder(this)
						.setMessage(R.string.discard_dialog)
						.setPositiveButton(R.string.discard, new
						DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent =
									new Intent(SpeakersActivity.this,
												SettingsActivity.class);
								SpeakersActivity.this.finish();
								startActivity(intent);
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private List<Speaker> speakers;

}
