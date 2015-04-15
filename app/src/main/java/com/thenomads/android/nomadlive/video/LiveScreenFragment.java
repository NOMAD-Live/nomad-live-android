package com.thenomads.android.nomadlive.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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

import com.thenomads.android.nomadlive.MainActivity;
import com.thenomads.android.nomadlive.R;
import com.thenomads.android.nomadlive.SECRETS;
import com.thenomads.android.nomadlive.internet.ReachabilityTest;

import java.io.File;

import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.api.KickflipCallback;
import io.kickflip.sdk.api.json.Response;
import io.kickflip.sdk.api.json.Stream;
import io.kickflip.sdk.av.BroadcastListener;
import io.kickflip.sdk.av.SessionConfig;
import io.kickflip.sdk.exception.KickflipException;

public class LiveScreenFragment extends Fragment {

    private static final String TAG = "LiveScreenFragment";
    // By default, Kickflip stores video in a "Kickflip" directory on external storage
    private final String mRecordingOutputPath = new File(Environment.getExternalStorageDirectory(), "NOMADLive/index.m3u8").getAbsolutePath();
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

        // Do nothing if kickflip is already setup
        if (MainActivity.mKickflipReady)
            return;

        // This must happen before any other Kickflip interactions
        Kickflip.setup(getActivity().getBaseContext(), SECRETS.CLIENT_KEY, SECRETS.CLIENT_SECRET, new KickflipCallback() {
            @Override
            public void onSuccess(Response response) {
                MainActivity.mKickflipReady = true;
                Log.d(TAG, "Kickflip setup done; " + response.toString());
            }

            @Override
            public void onError(KickflipException error) {
                Log.e(TAG, "Kickflip setup failed.");
                error.printStackTrace();
            }
        });
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

                if (MainActivity.mKickflipReady) {
                    startBroadcastingActivity();
                } else {
                    new AlertDialog.Builder(mRootView.getContext())
                            .setTitle(getString(R.string.dialog_title_not_ready))
                            .setMessage(getString(R.string.dialog_msg_not_ready))
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
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

    private void startBroadcastingActivity() {
        configureNewBroadcast();

        BroadcastListener mBroadcastListener = new BroadcastListener() {
            @Override
            public void onBroadcastStart() {
                Log.i(TAG, "onBroadcastStart");
            }

            @Override
            public void onBroadcastLive(Stream stream) {
                Log.i(TAG, "onBroadcastLive @ " + stream.getKickflipUrl());
            }

            @Override
            public void onBroadcastStop() {
                Log.i(TAG, "onBroadcastStop");

                // If you're manually injecting the BroadcastFragment,
                // you'll want to remove/replace BroadcastFragment
                // when the Broadcast is over.

                //getFragmentManager().beginTransaction()
                //    .replace(R.id.container, MainFragment.getInstance())
                //    .commit();
            }

            @Override
            public void onBroadcastError(KickflipException error) {
                Log.i(TAG, "onBroadcastError " + error.getMessage());
            }
        };
        Kickflip.startBroadcastActivity(getActivity(), mBroadcastListener);
    }

    private void configureNewBroadcast() {
        // Should reset mRecordingOutputPath between recordings
//        SessionConfig config = Util.create720pSessionConfig(mRecordingOutputPath);
        SessionConfig config = Util.create420pSessionConfig(mRecordingOutputPath);
        Kickflip.setSessionConfig(config);
    }
}
