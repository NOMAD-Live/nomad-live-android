package com.thenomads.android.nomadlive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View mRootView = inflater.inflate(R.layout.fragment_fullscreen_second, container, false);

        WebView mAboutPage = (WebView) mRootView.findViewById(R.id.about_page);

        mAboutPage.loadUrl(getString(R.string.about_page_file));

        // Deals with the settings button
        ImageView mImageView = (ImageView) mRootView.findViewById(R.id.settings_button);
        View.OnClickListener mStartBroadcastListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(i);
            }
        };
        mImageView.setOnClickListener(mStartBroadcastListener);

        return mRootView;
    }
}
