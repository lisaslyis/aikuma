package org.lp20.aikuma.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import org.lp20.aikuma.R;

public class CloudSyncActivity extends AikumaActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cloud_sync);
	}

	public void onCloudSyncPressed(View view) {
		try {
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	private static final String APPLICATION_NAME = "Aikuma";

}
