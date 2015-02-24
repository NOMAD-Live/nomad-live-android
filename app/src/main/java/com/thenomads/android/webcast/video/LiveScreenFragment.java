package com.thenomads.android.webcast.video;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.VideoView;

import com.thenomads.android.webcast.R;
import com.thenomads.android.webcast.internet.ReachabilityTest;

public class LiveScreenFragment extends Fragment {

    private View mRootView;
    private Switch mSwitch;
    private String mVideoPath;
    private String mLocalPath;
    private WebView mTwitterBannerWebView;
    private VideoView mLiveVideoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        mLiveVideoView = (VideoView) mRootView.findViewById(R.id.fullscreen_content);

        mSwitch = (Switch) mRootView.findViewById(R.id.offline_switch);

        mVideoPath = getString(R.string.wowza_vod_hls);
        mLocalPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.dancefloor;


        // Takes care of the video side, defaults to offline
        mLiveVideoView.setVideoPath(mLocalPath);
        mSwitch.setChecked(false);

        mSwitch.setTextOff("Offline");
        mSwitch.setTextOn("Online");

        // Makes sure the switch controls the playback (Server or Local)
        bindSwitchToVideoPlaybackSource();

        // Go online if available
        checkServerAvailability();

        // Sets up the twitter banner
        mTwitterBannerWebView = (WebView) mRootView.findViewById(R.id.twitter_banner);
        retrieveTwitterTickerContent();

        return mRootView;
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
                    mLiveVideoView.setVideoPath(mVideoPath);
                } else {
                    mLiveVideoView.setVideoPath(mLocalPath);
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
