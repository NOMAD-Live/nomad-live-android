package com.thenomads.android.nomadlive;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.thenomads.android.nomadlive.video.LiveScreenFragment;

class FragmentPageAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 2;
    private static final String TAG = "FragmentPageAdapter";

    public FragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
            case 0:
                return new LiveScreenFragment();
            case 1:
                return new SecondFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}