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
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.thenomads.android.nomadlive.R;
import com.thenomads.android.nomadlive.net.ReachabilityTest;
import com.thenomads.android.nomadlive.net.TwitterTicker;


public class LiveScreenFragment extends Fragment {

    private static final String TAG = "LiveScreenFragment";
    private static boolean CONNECTED_TO_INTERNET = false;
    private View mRootView;
    private String mVideoPath;
    private String mLocalPath;
    private VideoView mLiveVideoView;
    private ProgressBar mProgressBar;
    private SharedPreferences SP;

    private TwitterTicker mTwitterTicker;
    private VideoBroadcaster mVideoBroadcaster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        mLiveVideoView = (VideoView) mRootView.findViewById(R.id.fullscreen_content);
        mProgressBar = (ProgressBar) mRootView.findViewById(R.id.my_spinner);

        SP = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getBaseContext());

        Button mBroadcastButton = (Button) mRootView.findViewById(R.id.record_button);
        mVideoBroadcaster = new VideoBroadcaster(mBroadcastButton, this.getActivity());

        WebView twitterTickerWebView = (WebView) mRootView.findViewById(R.id.twitter_banner);
        mTwitterTicker = new TwitterTicker(twitterTickerWebView, this.getActivity());


        mVideoPath = getString(R.string.nomad_live_hls);
        mLocalPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.dancefloor;
//        mIntroPath = "android.resource://" + mRootView.getContext().getPackageName() + "/" + R.raw.nomad720p;

        // Adds a spinner to give loading feedback to the user
        displayLoadingSpinnerIfNeeded();

        return mRootView;
    }

    public void onResume() {
        super.onResume();

        // Makes sure the settings have been applied.
        handleBetaOptions();

        // Go online if available
        checkServerAvailability();

        // Release the current stream on return to the tv.
        mVideoBroadcaster.destroyCurrentStream();
        // If we are back to the activity then not streaming anymore
        mVideoBroadcaster.setStreamingState(false);
    }

    public void onPause() {
        super.onPause();

        // Makes sure we go back to the initial state when we go back to the Activity.
        CONNECTED_TO_INTERNET = false;
    }

    private void handleBetaOptions() {

        handleBroadcastStuff();

        handleTwitterTickerFlag();

    }

    private boolean handleTwitterTickerFlag() {
        boolean twitterTickerFlag = SP.getBoolean("twitter_ticker", true);

        if (twitterTickerFlag) {
            mTwitterTicker.setup();
            mTwitterTicker.show();
            return true;
        }
        mTwitterTicker.hide();
        return false;
    }

    private boolean handleBroadcastStuff() {
        boolean broadcastFlag = SP.getBoolean("broadcast", false);

        if (broadcastFlag) {
            mVideoBroadcaster.setup();
            mVideoBroadcaster.show();
            return true;
        }
        mVideoBroadcaster.hide();
        return false;
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
                    // Progress bar was here even after video started playing.
                    // TODO: Test more.
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        mProgressBar.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        };

        mLiveVideoView.setOnInfoListener(onInfoToPlayStateListener);
    }

    private void checkServerAvailability() {
        new ReachabilityTest(mVideoPath, 1935, mRootView.getContext(), new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                Log.i(TAG, "Internet available.");

                if (!CONNECTED_TO_INTERNET) {
                    mLiveVideoView.setVideoPath(mVideoPath);
//                    mLiveVideoView.start();

                    Log.i(TAG, "Switched Playback to: " + mVideoPath);
                }
                CONNECTED_TO_INTERNET = true;
            }

            @Override
            public void onReachabilityTestFailed() {
                Log.e(TAG, "Internet NOT available.");

                // Switches right away to local if no internet is available
                mLiveVideoView.setVideoPath(mLocalPath);
//                mLiveVideoView.start();

                Log.i(TAG, "Switched Playback to: " + mLocalPath);

                CONNECTED_TO_INTERNET = false;
            }
        }).execute();
    }
}
