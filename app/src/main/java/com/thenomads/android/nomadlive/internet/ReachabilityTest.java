package com.thenomads.android.nomadlive.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * From https://gist.github.com/cocoahero/01a24c4fcccf40dcdd99
 */
public class ReachabilityTest extends AsyncTask<Void, Void, Boolean> {

    private final Context mContext;
    private final Callback mCallback;
    private String mHostname;
    private int mServicePort;

    public ReachabilityTest(Context context, String url, Callback callback) {
        this(context, "", 80, callback);
        mHostname = getHostnameFromURL(url);
        mServicePort = getPortFromURL(url);
    }

    public ReachabilityTest(Context context, String hostname, int port, Callback callback) {
        mContext = context.getApplicationContext(); // Avoid leaking the Activity!
        mHostname = hostname;
        mServicePort = port;
        mCallback = callback;
    }

    private int getPortFromURL(String url) {

        int port = -1;

        try {
            URL mURL = new URL(url);
            port = mURL.getPort();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // If no port is found, use 80
        return port == -1 ? 80 : port;
    }

    private String getHostnameFromURL(String url) {

        String hostname = url;

        try {
            URL mURL = new URL(url);
            hostname = mURL.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return hostname;
    }

    @Override
    protected Boolean doInBackground(Void... args) {
        if (isConnected(mContext)) {
            InetAddress address = isResolvable(mHostname);
            if (address != null) {
                if (canConnect(address, mServicePort)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (mCallback != null) {
            if (result) {
                mCallback.onReachabilityTestPassed();
            } else {
                mCallback.onReachabilityTestFailed();
            }
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();

    }

    private InetAddress isResolvable(String hostname) {
        try {
            return InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private boolean canConnect(InetAddress address, int port) {
        Socket socket = new Socket();

        SocketAddress socketAddress = new InetSocketAddress(address, port);

        try {
            socket.connect(socketAddress, 2000);
        } catch (IOException e) {
            return false;
        } finally {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public interface Callback {
        void onReachabilityTestPassed();

        void onReachabilityTestFailed();
    }

}