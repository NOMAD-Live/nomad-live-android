package com.thenomads.android.nomadlive.video;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    private View mRootView;
    private Switch mSwitch;
    private String mVideoPath;
    private String mLocalPath;
    private String mIntroPath;

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
        mIntroPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.nomad720p;


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
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        mProgressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_ERROR_IO: {
                        mSwitch.setChecked(true);
                        mSwitch.setChecked(false);
                        return true;
                    }
                    default:
                        mProgressBar.animate();
                }
                return false;
            }
        };

        mLiveVideoView.setOnInfoListener(onInfoToPlayStateListener);
    }

    private void retrieveTwitterTickerContent() {

        new ReachabilityTest(mRootView.getContext(), getString(R.string.twitter_ticker_endpoint), new ReachabilityTest.Callback() {
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
                    mLiveVideoView.stopPlayback();
                    mLiveVideoView.setVideoPath(mVideoPath);
                    mLiveVideoView.start();
                } else {
                    mLiveVideoView.stopPlayback();
                    mLiveVideoView.setVideoPath(mLocalPath);
                    mLiveVideoView.start();
                }

            }
        });
    }

    private void checkServerAvailability() {
        new ReachabilityTest(mRootView.getContext(), mVideoPath, new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                mSwitch.setChecked(true);
            }

            @Override
            public void onReachabilityTestFailed() {
                mSwitch.setChecked(false);
            }
        }).execute();
    }
}
