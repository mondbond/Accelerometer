package com.example.mond.accelerometer.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mond.accelerometer.view.fragments.AccelerometerDataListFragment;
import com.example.mond.accelerometer.view.fragments.LineGraphFragment;

public class SessionDetailViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_PAGES = 2;

    private LineGraphFragment mGraphFragment;
    private AccelerometerDataListFragment mAccelerometerDataListFragment;

    public SessionDetailViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(mAccelerometerDataListFragment == null) {
                    mAccelerometerDataListFragment = AccelerometerDataListFragment.newInstance();
                }
                return mAccelerometerDataListFragment;
            case 1:
                if(mGraphFragment == null) {
                    mGraphFragment = LineGraphFragment.newInstance();
                }
                return mGraphFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
