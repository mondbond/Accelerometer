package com.example.mond.accelerometer.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.view.adapter.AccelerometerDataAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccelerometerDataListFragment extends Fragment {
    public static final String SESSION = "session";

    // TODO: 19.06.17 variable name code style. (Datas? Mb it would be better to call it mAccelerometerDataList)?
    private ArrayList<AccelerometerData> accelerometerDatas;
    private AccelerometerDataAdapter mAdapter;

    @BindView(R.id.accelerometer_data_fragment_recycler) RecyclerView mRecycler;

    public static AccelerometerDataListFragment newInstance() {
        return new AccelerometerDataListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            accelerometerDatas = getArguments().getParcelable(SESSION);
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

    public void setNewSessionValue(ArrayList<AccelerometerData> accelerometerDatas){
        this.accelerometerDatas = accelerometerDatas;
        mAdapter.setNewSessionValue(this.accelerometerDatas);
    }
}
