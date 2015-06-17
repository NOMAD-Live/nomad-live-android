package com.thenomads.android.nomadlive.net;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.thenomads.android.nomadlive.R;

/**
 * A wrapper to the Twitter ticker.
 * Wraps around a webview.
 */
public class TwitterTicker {

    private static final String TAG = "TwitterTicker";
    private String onlineURL;
    private String offlineURL;

    private WebView webView;
    private Context context;


    public TwitterTicker(WebView v, Context c) {
        this.webView = v;
        this.context = c;
        onlineURL = c.getString(R.string.twitter_ticker_endpoint);
        offlineURL = c.getString(R.string.twitter_ticker_fallback);
    }

    /**
     * @return false if there is nothing to to.
     */
    public boolean setup() {
        // Do nothing if the page is already loaded.
        if (onlineURL.equals(webView.getUrl()))
            return false;

        new ReachabilityTest(onlineURL, 80, context, new ReachabilityTest.Callback() {
            @Override
            public void onReachabilityTestPassed() {
                Log.i(TAG, onlineURL + " available.");
                webView.loadUrl(onlineURL);
            }

            @Override
            public void onReachabilityTestFailed() {
                Log.e(TAG, onlineURL + " unavailable.");
                webView.loadUrl(offlineURL);
            }
        }).execute();

        return true;
    }

    /**
     * @return false if the view was already visible.
     */
    public boolean show() {
        int old = webView.getVisibility();

        webView.setVisibility(View.VISIBLE);

        return old != View.VISIBLE;
    }

    /**
     * @return false if the view was already visible.
     */
    public boolean hide() {
        int old = webView.getVisibility();

        webView.setVisibility(View.GONE);

        return old != View.GONE;
    }
}
