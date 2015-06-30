package com.thenomads.android.nomadlive.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * A wrapper around the VideoView to enable smooth control of the player.
 *
 * @see android.widget.VideoView
 */
public class IntuitiveFullScreenVideoView extends VideoView {

    public IntuitiveFullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initCustomFullScreenVideoView();
    }

    private void initCustomFullScreenVideoView() {
        this.loopVideo();
    }

    private void loopVideo() {
        this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Play the video once the player is ready
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
    }
}
