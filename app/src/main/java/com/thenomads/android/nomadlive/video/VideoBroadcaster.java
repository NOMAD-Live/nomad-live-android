package com.thenomads.android.nomadlive.video;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thenomads.android.nomadlive.SECRETS;

import io.cine.android.BroadcastConfig;
import io.cine.android.CineIoClient;
import io.cine.android.CineIoConfig;
import io.swagger.client.ApiException;
import io.swagger.client.api.HeartbeatApi;
import io.swagger.client.api.StreamsApi;
import io.swagger.client.model.Stream;

/**
 * A wrapper to the Cine.IO Broadcaster.
 * Wraps around a Button.
 */
public class VideoBroadcaster {

    private static final String TAG = "VideoBroadcaster";

    private BroadcastConfig mBroadcastConfig;
    private CineIoClient mCineIoClient;
    private Button mBroadcastButton;
    private Context mContext;

    private HeartbeatApi heartbeatApi;
    private StreamsApi streamsApi;
    private Stream currentStream;
    private boolean isStreaming = false;

    public VideoBroadcaster(Button b, Context c) {
        this.mBroadcastButton = b;
        this.mContext = c;

        this.streamsApi = new StreamsApi();

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

                // Uses the current stream or get a new one
                if (currentStream == null) {
                    new AsyncTask<Void, Void, Stream>() {
                        protected Stream doInBackground(Void... params) {
                            try {
                                return streamsApi.streamsPost();

                            } catch (ApiException e) {
                                showAPIError("" + e.getCode(), e.getMessage());
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        public void onPostExecute(Stream result) {

                            super.onPostExecute(result);

                            if (result != null)
                                broadcast(result);
                            else
                                Log.e(TAG, "Unable to get a new stream.");
                        }
                    }.execute();
                } else {
                    broadcast(currentStream);
                }



            }
        };

        mBroadcastButton.setOnClickListener(mStartBroadcastListener);
    }

    public void setStreamingState(boolean isStreaming) {
        this.isStreaming = isStreaming;
    }

    public boolean isStreaming() {
        return this.isStreaming;
    }

    private void broadcast(Stream stream) {

        this.currentStream = stream;

        if (stream != null && !this.isStreaming()) {

            Log.i(TAG, "Starting broadcast on stream " + stream);
            Log.i(TAG, "Starting broadcast on stream " + stream.getId());
            mCineIoClient.broadcast(stream.getId(), mBroadcastConfig, mContext);
            this.setStreamingState(true);
        }
    }

    public void destroyCurrentStream() {

        if (!this.isStreaming()) {
            return;
        }
        // Makes sure there is an actual stream.
        if (currentStream != null) {
            new AsyncTask<Void, Void, Stream>() {
                protected Stream doInBackground(Void... params) {
                    try {
                        String id = currentStream.getId();
                        String password = currentStream.getPassword();
                        Log.i(TAG, "DELETING " + id + "?p=" + password);

                        return streamsApi.streamStreamIdDelete(id, password, password);

                    } catch (ApiException e) {
                        showAPIError("" + e.getCode(), e.getMessage());
                        // e.printStackTrace();
                    }
                    return null;
                }

                @Override
                public void onPostExecute(Stream result) {
                    Log.i(TAG, "Stream deleted (" + result.getId() + ")");
                    setStreamingState(false);
                }
            }.execute();
        }
    }

    private void showAPIError(String code, String message) {


        // TODO: Display a message to the user
        Log.e(TAG, "Code: " + code);
        Log.e(TAG, "Message: " + message);

//        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
//        builder.setMessage(message);
//
//        // 3. Get the AlertDialog from create()
//        AlertDialog dialog = builder.create();
//        dialog.show();

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
