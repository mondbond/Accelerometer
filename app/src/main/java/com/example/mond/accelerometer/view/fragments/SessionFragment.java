package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.Session;
import com.example.mond.accelerometer.view.adapter.SessionAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionFragment extends Fragment implements SessionAdapter.OnItemClickListener {
    public static final String SESSION_FRAGMENT_TAG = "sessionFragmentTag";

    private OnSessionFragmentInteractionListener mListener;

    @BindView(R.id.accelerometer_data_recycler) RecyclerView mRecycler;
    private SessionAdapter mAdapter;

    public static SessionFragment newInstance() {
        return new SessionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        if (context instanceof OnSessionFragmentInteractionListener) {
            mListener = (OnSessionFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSessionFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(Session session) {
        mListener.onSessionItemSelected(session);
    }

    public void setNewAccelerometerValues(List<Session> sessions){
        if(mAdapter!= null) {
            mAdapter.setSessions(sessions);
        }
    }

    public interface OnSessionFragmentInteractionListener {

        void onSessionItemSelected(Session session);
    }
}
