package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.view.adapter.AccelerometerDataAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccelerometerDataListFragment extends Fragment {
    public static final String SESSION = "session";

    private Session mSession;
    private AccelerometerDataAdapter mAdapter;

    @BindView(R.id.accelerometer_data_fragment_recycler) RecyclerView mRecycler;

    public static AccelerometerDataListFragment newInstance(Session session) {
        AccelerometerDataListFragment fragment = new AccelerometerDataListFragment();
        Bundle args = new Bundle();
        args.putParcelable(SESSION, session);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSession = getArguments().getParcelable(SESSION);
        }
        mAdapter = new AccelerometerDataAdapter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accelerometer_data_list, container, false);
        ButterKnife.bind(this, v);

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);

        return v;
    }

    public void setNewSessionValue(Session newSession){
        mSession = newSession;
        mAdapter.setNewSessionValue(mSession);
    }
}
