package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.view.adapter.SessionAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionFragment extends Fragment implements SessionAdapter.AdapterListener {

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.accelerometer_data_recycler) RecyclerView mRecycler;
    private SessionAdapter mAdapter;

    public static SessionFragment newInstance() {
        SessionFragment fragment = new SessionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        ButterKnife.bind(this, v);

        mAdapter = new SessionAdapter(null, this, getActivity());
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);

        return  v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(Session session) {
        mListener.onGetSessionData(session);
    }

    public void setNewAccelerometerValues(List<Session> sessions){
        if(mAdapter!= null) {
            mAdapter.setSessions(sessions);
        }
    }

    public interface OnFragmentInteractionListener {
        void onGetSessionData(Session session);
    }
}
