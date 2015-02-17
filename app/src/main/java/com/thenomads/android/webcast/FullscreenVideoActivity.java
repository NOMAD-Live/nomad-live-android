package com.thenomads.android.webcast;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;

import com.thenomads.android.webcast.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenVideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final VideoView myVideoView = (VideoView) findViewById(R.id.fullscreen_content);

        // Retrieve video from RTSP stream
        myVideoView.setVideoPath(getString(R.string.wowza_live_webcam));

        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Play the video once the player is ready
            public void onPrepared(MediaPlayer mp) {
                myVideoView.start();
            }
        });

    }
}
