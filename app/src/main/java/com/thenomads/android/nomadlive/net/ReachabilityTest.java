package com.thenomads.android.nomadlive.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

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

    private static final String TAG = "ReachabilityTest";
    private final Context mContext;
    private final Callback mCallback;
    private String mHostname;
    private int mPort;

    public ReachabilityTest(String url, int defaultPort, Callback callback) {
        this(null, "", -1, callback);

        mPort = getPortFromURL(url, defaultPort);
        mHostname = getHostnameFromURL(url);
    }

    /**
     * This constructor should be avoided as it does not specify the default port.
     *
     * @param url Web url of the form 'http://google.com:80'.
     * @param context Context of the application.
     * @param callback What to do if the connection succeeds/fails.
     */
    @Deprecated
    public ReachabilityTest(String url, Context context, Callback callback) {
        this(url, 80, context, callback);
    }

    public ReachabilityTest(String url, int defaultPort, Context context, Callback callback) {
        this(context, "", -1, callback);

        mPort = getPortFromURL(url, defaultPort);
        mHostname = getHostnameFromURL(url);
    }

    public ReachabilityTest(Context context, String hostname, int port, Callback callback) {
        // Avoid leaking the Activity!
        mContext = context == null ? null : context.getApplicationContext();

        mHostname = hostname;
        mCallback = callback;
        mPort = port;
    }

    /**
     * Maintains compatibility with older API.
     * Defaults to port 80 if no port is found.
     *
     * @param url Web url of the form 'http://google.com:443'.
     * @return Would return 443 in this case.
     */
    @Deprecated
    private int getPortFromURL(String url) {
        return getPortFromURL(url, 80);
    }

    private int getPortFromURL(String url, int defaultPort) {

        int port = -1;

        try {
            URL mURL = new URL(url);
            port = mURL.getPort();

        } catch (MalformedURLException e) {
        }

        // If no port is found, use the provided default one.
        return port == -1 ? defaultPort : port;
    }

    /**
     * If no host can be found in the given URL, it returns the URL itself.
     *
     * @param url Web url of the form 'http://google.com:80'.
     * @return Would return google.com:80 in this case.
     */
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
        if (mContext == null || isConnected(mContext)) {
            InetAddress address = isResolvable(mHostname);

            if (address != null) {
                if (canConnect(address, mPort)) {
                    return true;
                } else {
                    Log.d(TAG, "The port does not appear accessible: " + mPort);
                }
            } else {
                Log.d(TAG, "The hostname does not appear resolvable: " + mHostname);
            }
        } else {
            Log.d(TAG, "Device does not have any connectivity service.");
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

    /**
     * Uses the Android {@code ConnectivityManager}
     * to determine if there is any connection available.
     *
     * @param context of the application.
     * @return false if no connection is possible.
     */
    private boolean isConnected(Context context) {

        // Allows to continue without context.
        if (context == null) {
            Log.d(TAG, "No Context specified.");
            return true;
        }

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();

    }

    /**
     * Tries and resolve the hostname to an InetAddress
     *
     * @param hostname Of the form 'google.com:80'.
     * @return null if the hostname cannot be resolved.
     */
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