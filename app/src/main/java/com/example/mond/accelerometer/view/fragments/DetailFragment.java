package com.example.mond.accelerometer.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.model.Session;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.view.adapter.SessionDetailViewPagerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {
    public static String DETAIL_FRAGMENT_TAG = "detailFragmentTag";

    public final static String UID = "uid";
    public final static String SESSION = "session";

    private String mUid;
    private Session mSession;

    private LineGraphFragment mGraphFragment;
    private AccelerometerDataListFragment mAccelerometerDataListFragment;



    @BindView(R.id.vp_fragment_detail)
    ViewPager mPager;
    SessionDetailViewPagerAdapter mAdapter;

    public static DetailFragment newInstance(String uid, Session session) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(UID, uid);
        bundle.putParcelable(SESSION, session);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUid = getArguments().getString(UID);
            mSession = getArguments().getParcelable(SESSION);
        }

        mGraphFragment = LineGraphFragment.newInstance();
        mAccelerometerDataListFragment = AccelerometerDataListFragment.newInstance();

        mAdapter = new SessionDetailViewPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        setRetainInstance(true);
        ButterKnife.bind(this, v);
        mPager.setAdapter(mAdapter);

        return v;
    }
}
