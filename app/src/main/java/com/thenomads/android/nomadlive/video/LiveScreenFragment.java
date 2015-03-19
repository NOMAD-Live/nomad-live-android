package com.thenomads.android.nomadlive.video;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.VideoView;

import com.thenomads.android.nomadlive.R;
import com.thenomads.android.nomadlive.internet.ReachabilityTest;

public class LiveScreenFragment extends Fragment {

    private static final String TAG = "LiveScreenFragment";

    private View mRootView;
    private Switch mSwitch;
    private String mVideoPath;
    private String mLocalPath;
//    private String mIntroPath;

    private WebView mTwitterBannerWebView;
    private VideoView mLiveVideoView;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        mLiveVideoView = (VideoView) mRootView.findViewById(R.id.fullscreen_content);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.my_spinner);


        mSwitch = (Switch) mRootView.findViewById(R.id.offline_switch);

        mVideoPath = getString(R.string.wowza_vod_hls);
        mLocalPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.dancefloor;
//        mIntroPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.nomad720p;


        // Takes care of the video side, defaults to offline
        mSwitch.setChecked(false);
        mSwitch.setTextOff("Offline");
        mSwitch.setTextOn("Online");

        // Makes sure the switch controls the playback (Server or Local)
        bindSwitchToVideoPlaybackSource();

        // Adds a spinner to give loading feedback to the user
        displayLoadingSpinnerIfNeeded();

        // Go online if available
        checkServerAvailability();

        // Sets up the twitter banner
        mTwitterBannerWebView = (WebView) mRootView.findViewById(R.id.twitter_banner);
        retrieveTwitterTickerContent();

        return mRootView;
    }

    public void onStart() {
        super.onStart();

        int mScreenOrientation = getResources().getConfiguration().orientation;

        // Waits for landscape before playing video
        if (mScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            //TODO Listen for a single change to landscape then play video.
            mLiveVideoView.start();
        } else {
            mLiveVideoView.start();
        }
    }

    private void displayLoadingSpinnerIfNeeded() {

        final MediaPlayer.OnInfoListener onInfoToPlayStateListener = new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        mProgressBar.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        mProgressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_ERROR_IO: {
                        mSwitch.setChecked(false);
                        return true;
                    }
                }
                return false;
            }
        };

        mLiveVideoView.setOnInfoListener(onInfoToPlayStateListener);
    }

    private void retrieveTwitterTickerContent() {

        new ReachabilityTest(getString(R.string.twitter_ticker_endpoint), 80, mRootView.getContext(), new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                mTwitterBannerWebView.loadUrl(getString(R.string.twitter_ticker_endpoint));
            }

            @Override
            public void onReachabilityTestFailed() {
                mTwitterBannerWebView.loadUrl(getString(R.string.twitter_ticker_fallback));
            }
        }).execute();
//

    }

    private void bindSwitchToVideoPlaybackSource() {
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    mLiveVideoView.setVideoPath(mVideoPath);
                    mLiveVideoView.start();
                    Log.i(TAG, "Now playing: " + mVideoPath);
                } else {
                    mLiveVideoView.setVideoPath(mLocalPath);
                    mLiveVideoView.start();
                    Log.i(TAG, "Now playing: " + mLocalPath);
                }

            }
        });
    }

    private void checkServerAvailability() {
        new ReachabilityTest(mVideoPath, 1935, mRootView.getContext(), new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                mSwitch.setChecked(true);
                Log.i(TAG, "Internet available.");
            }

            @Override
            public void onReachabilityTestFailed() {
                mSwitch.setChecked(false);
                Log.i(TAG, "Internet NOT available.");
            }
        }).execute();
    }
}
