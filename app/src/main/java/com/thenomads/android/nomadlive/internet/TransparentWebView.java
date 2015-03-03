package com.thenomads.android.nomadlive.internet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A custom view with a transparent background for better overlay.
 */
public class TransparentWebView extends WebView {

    private final Context context;

    /**
     * Construct a new TransparentWebView with a Context object.
     *
     * @param context A Context object used to access application assets.
     */
    public TransparentWebView(Context context) {
        super(context);
        this.context = context;
        setup();
    }

    /**
     * Construct a new TransparentWebView with layout parameters.
     *
     * @param context A Context object used to access application assets.
     * @param attrs   An AttributeSet passed to our parent.
     */
    public TransparentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setup();
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
        this.context = context;
        setup();
    }

    private void setup() {
        setBackgroundColor(Color.BLACK);
        makeBackgroundTransparent();

        if (!this.isInEditMode()) {
            WebSettings settings = this.getSettings();
            settings.setJavaScriptEnabled(true);
        }
    }

    private void makeBackgroundTransparent() {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.setVisibility(VISIBLE);
                view.setBackgroundColor(0x00000000);
                if (Build.VERSION.SDK_INT >= 11)
                    view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Launch another Activity that handles URLs
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
        });
    }
}
