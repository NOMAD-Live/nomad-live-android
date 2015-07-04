package com.thenomads.android.nomadlive.net;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.VideoView;

import java.util.List;

/**
 * Manages the playback of a video view,
 * playing the sources one by one until one is available.
 *
 * Has not been tested yet, but should replace the useless IntuitiveFullScreenVideoView
 */
public class PlaybackManager {

    private final String TAG = "PlaybackManager";
    private final List<String> mSources;
    private final VideoView mVideoView;

    public PlaybackManager(List<String> videoPaths, VideoView videoView) {
        mSources = videoPaths;
        mVideoView = videoView;
    }

    public void playFirstAvailablePath() {
        AsyncTask task;

        // Try paths starting from the end.
        for (int i = mSources.size() - 1; i <= 0; i--) {

            task = tryPathAtIndex(i);

            if (task == null) break;

            task.execute();

        }

    }

    /**
     *
     * @param i
     * @return null if all sources have been exhausted.
     */
    AsyncTask<Void, Void, Boolean> tryPathAtIndex(final int i) {

        if (i < 0 || mSources.size() <= i) {
            mVideoView.setVideoPath(mSources.get(0));
            mVideoView.start();
            return null;
        }

        return new ReachabilityTest(mSources.get(i), 1935, new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                Log.i(TAG, "Path is accessible: " + mSources.get(i));
                mVideoView.setVideoPath(mSources.get(i));
                mVideoView.start();
            }

            @Override
            public void onReachabilityTestFailed() {
                Log.e(TAG, "Unable to play: " + mSources.get(i));
            }
        });
    }
}
