package com.thenomads.android.webcast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.VideoView;

public class LiveScreenFragment extends Fragment {

    private View mRootView;
    private Button mButton;
    private String mVideoPath;
    private WebView mTwitterBannerWebView;
    private VideoView mLiveVideoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        mLiveVideoView = (VideoView) mRootView.findViewById(R.id.fullscreen_content);
        mButton = (Button) mRootView.findViewById(R.id.playback_button);

        mVideoPath = getString(R.string.wowza_vod_hls);

        // Takes care of the video side
        mButton.setText("Playing from Wowza.");
        mLiveVideoView.setVideoPath(mVideoPath);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayback();
            }
        });


        // Sets up the twitter banner
        mTwitterBannerWebView = (WebView) mRootView.findViewById(R.id.twitter_banner);
        mTwitterBannerWebView.loadUrl("file:///android_asset/banner.html");


        return mRootView;
    }

    public void togglePlayback() {
        if (mButton.getText().toString().startsWith("Playing Local")) {

            // Retrieve video from Wowza stream
            mLiveVideoView.setVideoPath(mVideoPath);
            mButton.setText("Playing from Wowza.");

        } else {

            String path = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.dancefloor;

            mLiveVideoView.setVideoPath(path);
            mButton.setText("Playing Local File.");
        }
    }
}
