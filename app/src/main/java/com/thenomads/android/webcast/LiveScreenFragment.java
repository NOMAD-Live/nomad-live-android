package com.thenomads.android.webcast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

public class LiveScreenFragment extends Fragment {

    private View rootView;
    private Button button;
    private String videoPath;
    private VideoView myVideoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_fullscreen_video, container, false);
        myVideoView = (VideoView) rootView.findViewById(R.id.fullscreen_content);
        button = (Button) rootView.findViewById(R.id.dummy_button);
        videoPath = getString(R.string.wowza_vod_hls);

        button.setText("Playing from Wowza.");
        myVideoView.setVideoPath(videoPath);

        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (button.getText().toString().startsWith("Playing Local")) {

                                              // Retrieve video from Wowza stream
                                              myVideoView.setVideoPath(videoPath);
                                              button.setText("Playing from Wowza.");

                                          } else {

                                              String path = "android.resource://" + rootView.getContext().getPackageName() + "/" + R.raw.dancefloor;

                                              myVideoView.setVideoPath(path);
                                              button.setText("Playing Local File.");
                                          }
                                      }
                                  }
        );

        return rootView;
    }
}
