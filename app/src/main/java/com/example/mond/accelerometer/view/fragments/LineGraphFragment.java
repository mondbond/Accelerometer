package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LineGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LineGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LineGraphFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private LineChart mGraph;
    private List<Entry> mXEntries;

    private LineDataSet mXLine;
    private LineData mLineData;
    private OnFragmentInteractionListener mListener;

    public LineGraphFragment() {
        // Required empty public constructor
    }

    public static LineGraphFragment newInstance() {
        LineGraphFragment fragment = new LineGraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_line_graph, container, false);
        mGraph = (LineChart) v.findViewById(R.id.graph_fragment_graph);
        mXEntries = new ArrayList<>();
        mXEntries.add(new Entry(0, 1));
        mXEntries.add(new Entry(1, 5));
        mXEntries.add(new Entry(2, 7));
        mXEntries.add(new Entry(3, 10));
        mXEntries.add(new Entry(4, 15));
        mXEntries.add(new Entry(5, 1));
        mXEntries.add(new Entry(6, 5));
        mXEntries.add(new Entry(7, 8));
        mXEntries.add(new Entry(8, 3));
        mXEntries.add(new Entry(9, 20));

        mXLine = new LineDataSet(mXEntries, "X");
        mXLine.setColor(Color.RED);

        mLineData = new LineData(mXLine);
        mGraph.setData(mLineData);

        return  v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGraph.invalidate();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
