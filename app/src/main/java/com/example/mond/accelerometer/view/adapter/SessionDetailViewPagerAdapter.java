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

    public SessionDetailViewPagerAdapter (FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return mAccelerometerDataListFragment;
            case 1:
                return mGraphFragment;
            default:return mAccelerometerDataListFragment;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }


    public void initFragments(AccelerometerDataListFragment accelerometerDataListFragment,
                              LineGraphFragment lineGraphFragment){
        mAccelerometerDataListFragment = accelerometerDataListFragment;
        mGraphFragment = lineGraphFragment;
    }
}
