package com.example.mond.accelerometer.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.events.AccelerometerDataChangeEvent;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.view.adapter.AccelerometerDataAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccelerometerDataListFragment extends Fragment {
    public static final String SESSION = "session";

    private ArrayList<AccelerometerData> accelerometerDataList;
    private AccelerometerDataAdapter mAdapter;

    @BindView(R.id.rv_accelerometer_items)
    RecyclerView mRecycler;

    public static AccelerometerDataListFragment newInstance() {
        return new AccelerometerDataListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            accelerometerDataList = getArguments().getParcelable(SESSION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accelerometer_data_list, container, false);
        ButterKnife.bind(this, v);

        mAdapter = new AccelerometerDataAdapter(null);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void setNewSessionValue(ArrayList<AccelerometerData> accelerometerDatas) {
        this.accelerometerDataList = accelerometerDatas;
        mAdapter.setNewSessionValue(this.accelerometerDataList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setNewSessionValue(AccelerometerDataChangeEvent event) {
        this.accelerometerDataList = event.getAccelerometerDataList();
        mAdapter.setNewSessionValue(this.accelerometerDataList);
    }
}
