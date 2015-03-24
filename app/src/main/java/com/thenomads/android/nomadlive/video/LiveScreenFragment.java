package com.thenomads.android.nomadlive.video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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
import com.thenomads.android.nomadlive.internet.ReachabilityTest;

import java.io.File;

import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.api.json.Stream;
import io.kickflip.sdk.av.BroadcastListener;
import io.kickflip.sdk.av.SessionConfig;
import io.kickflip.sdk.exception.KickflipException;

public class LiveScreenFragment extends Fragment {

    private static final String TAG = "LiveScreenFragment";
    // By default, Kickflip stores video in a "Kickflip" directory on external storage
    private static String mRecordingOutputPath = new File(Environment.getExternalStorageDirectory(), "NOMADLive/index.m3u8").getAbsolutePath();
    private View mRootView;
    private Switch mSwitch;
    private String mVideoPath;
//    private String mIntroPath;
private String mLocalPath;
    private WebView mTwitterBannerWebView;
    private VideoView mLiveVideoView;
    private ProgressBar mProgressBar;
    private Button mBroadcastButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        mLiveVideoView = (VideoView) mRootView.findViewById(R.id.fullscreen_content);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.my_spinner);

        mSwitch = (Switch) mRootView.findViewById(R.id.offline_switch);

        mBroadcastButton = (Button) mRootView.findViewById(R.id.record_button);

        mVideoPath = getString(R.string.wowza_vod_hls);
        mLocalPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.dancefloor;
//        mIntroPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.nomad720p;


        // Takes care of the video side, defaults to offline
        mSwitch.setChecked(false);
        mSwitch.setTextOff("Offline");
        mSwitch.setTextOn("Online");

        // Binds an action to the record button
        startBroadcastActivityOnClick();

        // Makes sure the switch controls the playback (Server or Local)
        bindSwitchToVideoPlaybackSource();

        // Adds a spinner to give loading feedback to the user
        displayLoadingSpinnerIfNeeded();

        // Sets up the twitter banner
        mTwitterBannerWebView = (WebView) mRootView.findViewById(R.id.twitter_banner);
        retrieveTwitterTickerContent();

        return mRootView;
    }

    public void onStart() {
        super.onStart();

        // Go online if available
        checkServerAvailability();
        
        mLiveVideoView.start();

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
