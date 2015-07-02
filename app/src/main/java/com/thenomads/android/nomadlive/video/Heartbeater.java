package com.thenomads.android.nomadlive.video;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import io.swagger.client.ApiException;
import io.swagger.client.api.HeartbeatApi;
import io.swagger.client.model.Stream;

/**
 * Maintains the mStream alive sending a heartbeat every 5s.
 */
public class Heartbeater {

    private final String TAG = "Heartbeater";
    private final int INTERVAL = 5000; // In ms

    private final Context mContext;
    private HeartbeatApi mHeartbeatAPI;
    private Timer mTimer;

    // Stream to keep alive
    private Stream mStream;

    public Heartbeater(Stream stream, Context context) {
        this.mHeartbeatAPI = new HeartbeatApi();
        this.mTimer = new Timer();

        this.mContext = context;
        this.mStream = stream;
    }

    public void start() {

        Log.v(TAG, "Keeping alive stream: " + mStream.getId());

        final Handler handler = new Handler();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                boolean post = handler.post(new Runnable() {
                    public void run() {
                        try {

                            Log.v(TAG, "Beating for: " + mStream.getId());
                            oneBeat().execute(mStream);

                            // REALLY BIG HACK TO CLEAN CINE.IO HEAP
                            // TODO: GET RID OF IT ASAP
                            Log.d(TAG, "HUGE HACK: FORCE GARBAGE COLLECTOR. GET RID OF IT ASAP!!!");

                            System.gc();

                        } catch (Exception e) {
                            Log.e(TAG, "Could not execute one beat for " + mStream);
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        mTimer.schedule(doAsynchronousTask, 0, INTERVAL); //execute in every 50000 ms
    }

    public void stop() {
        Log.v(TAG, "Letting die stream: " + mStream.getId());
        mTimer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
        mTimer.purge();
    }

    private AsyncTask<Stream, Void, ApiException> oneBeat() {
        return new AsyncTask<Stream, Void, ApiException>() {

            @Override
            protected ApiException doInBackground(Stream... params) {
                String id = mStream.getId();
                String password = mStream.getPassword();
                try {
                    mHeartbeatAPI.streamStreamIdPost(id, password, password);
                } catch (ApiException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(ApiException result) {
                super.onPostExecute(result);

                if (result != null) {
                    VideoBroadcaster.showAPIErrorIfAny(result, mContext);
                }
            }
        };
    }
}
