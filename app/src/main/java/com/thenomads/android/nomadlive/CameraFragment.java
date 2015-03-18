package com.thenomads.android.nomadlive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.api.KickflipCallback;
import io.kickflip.sdk.api.json.Response;
import io.kickflip.sdk.exception.KickflipException;

public class CameraFragment extends Fragment {

    boolean mKickflipReady = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View mRootView = inflater.inflate(R.layout.camera_activity, container, false);

        // This must happen before any other Kickflip interactions
        Kickflip.setup(mRootView.getContext(), SECRETS.CLIENT_KEY, SECRETS.CLIENT_SECRET, new KickflipCallback() {
            @Override
            public void onSuccess(Response response) {
                mKickflipReady = true;
                Toast.makeText(mRootView.getContext(), "KICKASS", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(KickflipException error) {

            }
        });

        return mRootView;
    }
}
