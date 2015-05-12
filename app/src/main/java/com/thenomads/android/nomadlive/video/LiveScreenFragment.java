package com.thenomads.android.nomadlive.video;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.VideoView;

import com.thenomads.android.nomadlive.R;
import com.thenomads.android.nomadlive.SECRETS;
import com.thenomads.android.nomadlive.internet.ReachabilityTest;

import io.cine.android.BroadcastConfig;
import io.cine.android.CineIoClient;
import io.cine.android.CineIoConfig;


public class LiveScreenFragment extends Fragment {

    private static final String TAG = "LiveScreenFragment";
    private View mRootView;
    private Switch mSwitch;
    private String mVideoPath;
    // private String mIntroPath;
    private String mLocalPath;
    private WebView mTwitterBannerWebView;
    private VideoView mLiveVideoView;
    private ProgressBar mProgressBar;
    private Button mBroadcastButton;

    private SharedPreferences SP;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        mLiveVideoView = (VideoView) mRootView.findViewById(R.id.fullscreen_content);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.my_spinner);

        SP = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getBaseContext());

        mSwitch = (Switch) mRootView.findViewById(R.id.offline_switch);

        mBroadcastButton = (Button) mRootView.findViewById(R.id.record_button);

        mTwitterBannerWebView = (WebView) mRootView.findViewById(R.id.twitter_banner);

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

        handleBetaOptions();

        return mRootView;
    }

    public void onStart() {
        super.onStart();

        // Go online if available
        checkServerAvailability();

        mLiveVideoView.start();

    }

    private void handleBetaOptions() {

        handleKickflipFlag();

        handleTwitterTickerFlag();

    }

    private boolean handleKickflipFlag() {

        boolean betaBroadcastFlag = SP.getBoolean("kickflip_broadcast", false);

        if (betaBroadcastFlag) {

            setUpKickflip();

            // Shows the button
            mBroadcastButton.setVisibility(View.VISIBLE);

            // Binds an action to the record button
            startBroadcastActivityOnClick();
            return true;
        }

        // Hides the broadcast button
        mBroadcastButton.setVisibility(View.GONE);

        return false;
    }

    private void setUpKickflip() {

    }

    private boolean handleTwitterTickerFlag() {

        boolean twitterTickerFlag = SP.getBoolean("twitter_ticker", true);

        if (twitterTickerFlag) {

            // Sets up the twitter banner
            retrieveTwitterTickerContent();
            return true;
        }

        // Hides the twitter banner
        mTwitterBannerWebView.setVisibility(View.GONE);

        return false;
    }

    private void startBroadcastActivityOnClick() {

        View.OnClickListener mStartBroadcastListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CineIoConfig config = new CineIoConfig();
                config.setSecretKey(SECRETS.CINE_SECRET_KEY);
                // config.setMasterKey(MASTER_KEY);
                CineIoClient client = new CineIoClient(config);

                BroadcastConfig bConfig = new BroadcastConfig();
                bConfig.selectCamera("back");

                String streamId = "554cf071fc71760b00a78aad";

                client.broadcast(streamId, bConfig, getActivity());
            }
        };

        mBroadcastButton.setOnClickListener(mStartBroadcastListener);
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

        // Do nothing if the page is already loaded.
        if (mTwitterBannerWebView.getUrl() == getString(R.string.twitter_ticker_endpoint))
            return;

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
