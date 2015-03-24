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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.io.File;

import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.api.KickflipCallback;
import io.kickflip.sdk.api.json.Response;
import io.kickflip.sdk.exception.KickflipException;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    public static boolean mKickflipReady = false;

    // By default, Kickflip stores video in a "Kickflip" directory on external storage
    private static String mRecordingOutputPath = new File(Environment.getExternalStorageDirectory(), "NOMADLive/index.m3u8").getAbsolutePath();

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

        // This must happen before any other Kickflip interactions
        Kickflip.setup(this, SECRETS.CLIENT_KEY, SECRETS.CLIENT_SECRET, new KickflipCallback() {
            @Override
            public void onSuccess(Response response) {
                mKickflipReady = true;
                Log.d(TAG, "Kickflip setup done; " + response.toString());
            }

            @Override
            public void onError(KickflipException error) {
                Log.e(TAG, "Kickflip setup failed.");
                error.printStackTrace();
            }
        });
    }
}
