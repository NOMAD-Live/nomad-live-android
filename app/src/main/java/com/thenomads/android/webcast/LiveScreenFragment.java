package com.thenomads.android.webcast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

public class LiveScreenFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        final VideoView myVideoView = (VideoView) rootView.findViewById(R.id.fullscreen_content);
        final Button button = (Button) rootView.findViewById(R.id.dummy_button);

        //final String videoPath = getString(R.string.wowza_vod_hls);
        final String videoPath = "nothing";
        button.setText(videoPath);

        if (Toolbox.isWowzaAvailable(videoPath)) {

            // Retrieve video from Wowza stream
            myVideoView.setVideoPath(videoPath);
            button.setText("Wowza seems OK");

        } else {

            String path = "android.resource://" + rootView.getContext().getPackageName() + "/" + R.raw.dancefloor;

            myVideoView.setVideoPath(path);
            button.setText("Server Unavailable");
        }

        return rootView;
    }
}
