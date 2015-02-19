package com.thenomads.android.webcast;

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
    private IntuitiveFullScreenVideoView that = this;
    private long startClickTime;

    public IntuitiveFullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initCustomFullScreenVideoView();
    }

    private void initCustomFullScreenVideoView() {
        this.autoStartOnReady();
        this.clickToPause();
    }

    private void autoStartOnReady() {
        this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Play the video once the player is ready
            public void onPrepared(MediaPlayer mp) {
                that.start();
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
                            if (that.isPlaying()) {
                                that.pause();
                                toast("Paused");
                                return false;
                            } else {
                                that.resume();
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
