package com.thenomads.android.webcast;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.VideoView;

import com.thenomads.android.webcast.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenVideoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final VideoView myVideoView = (VideoView) findViewById(R.id.fullscreen_content);
        final Button button = (Button) findViewById(R.id.dummy_button);

        String videoPath = getString(R.string.wowza_vod_hls);

        // Retrieve video from Wowza stream
        myVideoView.setVideoPath(videoPath);


        button.setText(videoPath);
    }
}
