package com.thenomads.android.webcast;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.thenomads.android.webcast.util.SystemUiHider;

import java.util.Calendar;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenVideoActivity extends FragmentActivity {

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final VideoView myVideoView = (VideoView) findViewById(R.id.fullscreen_content);
        final Button button = (Button) findViewById(R.id.dummy_button);

        final String videoPath = getString(R.string.wowza_live_hls);

        // Retrieve video from Wowza stream
        myVideoView.setVideoPath(videoPath);
        button.setText(videoPath);

        myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Play the video once the player is ready
            public void onPrepared(MediaPlayer mp) {
                myVideoView.start();
            }
        });

        myVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {

                switch (me.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        // Checks for single tap based on timing
                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                        if(clickDuration < MAX_CLICK_DURATION) {
                            if (myVideoView.isPlaying()) {
                                myVideoView.pause();
                                toast("Paused");
                                return false;
                            } else {
                                myVideoView.resume();
                                toast("Resuming...");

                            }
                        }
                    }
                }
                return true;
            }
        });

    }

    private void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
