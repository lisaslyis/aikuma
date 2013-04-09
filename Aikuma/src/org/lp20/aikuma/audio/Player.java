package org.lp20.aikuma.audio;

import android.media.MediaPlayer;
import java.io.IOException;
import org.lp20.aikuma.model.Recording;

public class Player {

	public Player(Recording recording) throws IOException {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(recording.getFile().getName());
		mediaPlayer.prepare();
	}

	public void play() {
		mediaPlayer.start();
	}

	private MediaPlayer mediaPlayer;
}
