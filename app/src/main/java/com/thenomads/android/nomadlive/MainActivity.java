package com.thenomads.android.nomadlive;
/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.thenomads.android.nomadlive.video.Util;

import java.io.File;

import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.api.KickflipCallback;
import io.kickflip.sdk.api.json.Response;
import io.kickflip.sdk.api.json.Stream;
import io.kickflip.sdk.av.BroadcastListener;
import io.kickflip.sdk.av.SessionConfig;
import io.kickflip.sdk.exception.KickflipException;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private BroadcastListener mBroadcastListener = new BroadcastListener() {
        @Override
        public void onBroadcastStart() {
            Log.i(TAG, "onBroadcastStart");
        }

        @Override
        public void onBroadcastLive(Stream stream) {
            Log.i(TAG, "onBroadcastLive @ " + stream.getKickflipUrl());
        }

        @Override
        public void onBroadcastStop() {
            Log.i(TAG, "onBroadcastStop");

            // If you're manually injecting the BroadcastFragment,
            // you'll want to remove/replace BroadcastFragment
            // when the Broadcast is over.

            //getFragmentManager().beginTransaction()
            //    .replace(R.id.container, MainFragment.getInstance())
            //    .commit();
        }

        @Override
        public void onBroadcastError(KickflipException error) {
            Log.i(TAG, "onBroadcastError " + error.getMessage());
        }
    };
    private boolean mKickflipReady = false;
    private View.OnClickListener mStartBroadcastListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Hey, he clicked on the record button !");
            if (mKickflipReady) {
                startBroadcastingActivity();
            } else {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle(getString(R.string.dialog_title_not_ready))
                        .setMessage(getString(R.string.dialog_msg_not_ready))
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    };
    // By default, Kickflip stores video in a "Kickflip" directory on external storage
    private String mRecordingOutputPath = new File(Environment.getExternalStorageDirectory(), "NOMADLive/index.m3u8").getAbsolutePath();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private FragmentPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_views);

        // Create the adapter that will return a fragment for each of the two primary sections
        // of the app.
        mAppSectionsPagerAdapter = new FragmentPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        Button mBroadcastButton = (Button) findViewById(R.id.record_button);

        if (mBroadcastButton != null) {
            Log.e(TAG, "Dude, I could not find the button...");
            mBroadcastButton.setOnClickListener(mStartBroadcastListener);
        }

        // This must happen before any other Kickflip interactions
        Kickflip.setup(this, SECRETS.CLIENT_KEY, SECRETS.CLIENT_SECRET, new KickflipCallback() {
            @Override
            public void onSuccess(Response response) {
                mKickflipReady = true;
            }

            @Override
            public void onError(KickflipException error) {

            }
        });
    }

    private void startBroadcastingActivity() {
        configureNewBroadcast();
        Log.i(TAG, "Broadcast activity starting...");
        Kickflip.startBroadcastActivity(this, mBroadcastListener);
        Log.i(TAG, "Broadcast activity started.");
    }

    private void configureNewBroadcast() {
        // Should reset mRecordingOutputPath between recordings
//        SessionConfig config = Util.create720pSessionConfig(mRecordingOutputPath);

        Log.i(TAG, "Broadcast configuration in process...");
        SessionConfig config = Util.create420pSessionConfig(mRecordingOutputPath);
        Kickflip.setSessionConfig(config);
        Log.i(TAG, "Broadcast configuration done.");
    }
}
