package com.thenomads.android.nomadlive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View mRootView = inflater.inflate(R.layout.fragment_fullscreen_second, container, false);

        WebView mAboutPage = (WebView) mRootView.findViewById(R.id.about_page);

        mAboutPage.loadUrl(getString(R.string.about_page_file));

        return mRootView;
    }
}
