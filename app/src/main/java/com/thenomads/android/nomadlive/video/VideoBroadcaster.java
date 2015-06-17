package com.thenomads.android.nomadlive.video;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thenomads.android.nomadlive.SECRETS;

import io.cine.android.BroadcastConfig;
import io.cine.android.CineIoClient;
import io.cine.android.CineIoConfig;

/**
 * A wrapper to the Cine.IO Broadcaster.
 * Wraps around a Button.
 */
public class VideoBroadcaster {

    private static final String TAG = "TwitterTicker";

    private BroadcastConfig mBroadcastConfig;
    private CineIoClient mCineIoClient;
    private Button mBroadcastButton;
    private Context mContext;

    public VideoBroadcaster(Button b, Context c) {
        this.mBroadcastButton = b;
        this.mContext = c;
        startBroadcastActivityOnClick();
    }

    public void setup() {
        CineIoConfig config = new CineIoConfig();
        config.setSecretKey(SECRETS.CINE_SECRET_KEY);
        // config.setMasterKey(SECRETS.MASTER_KEY);
        mCineIoClient = new CineIoClient(config);

        mBroadcastConfig = new BroadcastConfig();
        mBroadcastConfig.selectCamera("back");
        mBroadcastConfig.lockOrientation("landscape");

//        TODO: Make sure the quality gets actually set to 480p.
//        Trying to fix #24
//        mBroadcastConfig.setHeight(480);
//        mBroadcastConfig.setWidth(720);
    }

    private void startBroadcastActivityOnClick() {

        View.OnClickListener mStartBroadcastListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String streamId = "554cf071fc71760b00a78aad";

                Log.i(TAG, "Starting broadcast on stream" + streamId);

                mCineIoClient.broadcast(streamId, mBroadcastConfig, mContext);
            }
        };

        mBroadcastButton.setOnClickListener(mStartBroadcastListener);
    }

    /**
     * @return false if the view was already visible.
     */
    public boolean show() {
        int old = mBroadcastButton.getVisibility();

        mBroadcastButton.setVisibility(View.VISIBLE);

        return old != View.VISIBLE;
    }

    /**
     * @return false if the view was already visible.
     */
    public boolean hide() {
        int old = mBroadcastButton.getVisibility();

        mBroadcastButton.setVisibility(View.GONE);

        return old != View.GONE;
    }
}
