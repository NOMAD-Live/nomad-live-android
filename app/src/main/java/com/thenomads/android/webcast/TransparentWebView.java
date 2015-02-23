package com.thenomads.android.webcast;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A custom view with a transparent background for better overlay.
 */
public class TransparentWebView extends WebView {

    /**
     * Construct a new TransparentWebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    public TransparentWebView(Context context) {
        super(context);
        makeBackgroundTransparent();
    }

    /**
     * Construct a new TransparentWebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    public TransparentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        makeBackgroundTransparent();
    }

    /**
     * Construct a new TransparentWebView with layout parameters and a default style.
     *
     * @param context  A Context object used to access application assets.
     * @param attrs    An AttributeSet passed to our parent.
     * @param defStyle The default style resource ID.
     */
    public TransparentWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        makeBackgroundTransparent();
    }

    private void makeBackgroundTransparent() {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.setBackgroundColor(0x00000000);
                if (Build.VERSION.SDK_INT >= 11)
                    view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            }
        });
    }
}
