package com.thenomads.android.nomadlive.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Calendar;

/**
 * A wrapper around the VideoView to enable smooth control of the player.
 *
 * @see android.widget.VideoView
 */
public class IntuitiveFullScreenVideoView extends VideoView {

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    public IntuitiveFullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initCustomFullScreenVideoView();
    }

    private void initCustomFullScreenVideoView() {
        this.loopVideo();
        this.clickToPause();
    }

    private void loopVideo() {
        this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Play the video once the player is ready
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    private void clickToPause() {

        this.setOnTouchListener(new View.OnTouchListener() {
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

                        if (clickDuration < MAX_CLICK_DURATION) {
                            if (isPlaying()) {
                                pause();
                                toast("Paused");
                                return false;
                            } else {
                                resume();
                                start();
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
        Toast.makeText(this.getContext(), s, Toast.LENGTH_SHORT).show();
    }

}
