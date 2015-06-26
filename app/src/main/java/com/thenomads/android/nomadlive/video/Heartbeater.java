package com.thenomads.android.nomadlive.video;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import io.swagger.client.ApiException;
import io.swagger.client.api.HeartbeatApi;
import io.swagger.client.model.Stream;

/**
 * Maintains the stream alive sending a heartbeat every 5s.
 */
public class Heartbeater {

    private final String TAG = "Heartbeater";

    private final int INTERVAL = 5000; // In ms
    private HeartbeatApi api;
    private Timer timer;

    // Stream to keep alive
    private Stream stream;

    public Heartbeater(Stream stream) {
        this.api = new HeartbeatApi();
        this.timer = new Timer();

        this.stream = stream;
    }

    public void start() {

        Log.v(TAG, "Keeping alive stream: " + stream.getId());

        final Handler handler = new Handler();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                boolean post = handler.post(new Runnable() {
                    public void run() {
                        try {

                            Log.v(TAG, "Beating for: " + stream.getId());
                            oneBeat().execute(stream);
                        } catch (Exception e) {
                            Log.e(TAG, "Could not execute one beat for " + stream);
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, INTERVAL); //execute in every 50000 ms
    }

    public void stop() {
        Log.v(TAG, "Letting die stream: " + stream.getId());
        timer.cancel();  // Terminates this timer, discarding any currently scheduled tasks.
        timer.purge();
    }

    private AsyncTask<Stream, Void, Void> oneBeat() {
        return new AsyncTask<Stream, Void, Void>() {

            @Override
            protected Void doInBackground(Stream... params) {
                String id = stream.getId();
                String password = stream.getPassword();
                try {
                    api.streamStreamIdPost(id, password, password);
                } catch (ApiException e) {
                    showAPIError(e);
                }
                return null;
            }
        };
    }

    private void showAPIError(ApiException e) {

        // TODO: Display a message to the user
        Log.e(TAG, "Code: " + e.getCode());
        Log.e(TAG, "Message: " + e.getMessage());

//        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
//        builder.setMessage(message);
//
//        // 3. Get the AlertDialog from create()
//        AlertDialog dialog = builder.create();
//        dialog.show();

    }
}
