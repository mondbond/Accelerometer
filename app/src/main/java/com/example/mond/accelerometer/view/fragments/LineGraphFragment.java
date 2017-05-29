package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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

    private LineChart mGraph;
    private List<Entry> mXEntries;
    private List<Entry> mYEntries;
    private List<Entry> mZEntries;

    private LineDataSet mXLine;
    private LineDataSet mYLine;
    private LineDataSet mZLine;
    private LineData mLineData;

    private OnFragmentInteractionListener mListener;

    public static LineGraphFragment newInstance() {
        LineGraphFragment fragment = new LineGraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_line_graph, container, false);
        mGraph = (LineChart) v.findViewById(R.id.graph_fragment_graph);

        return  v;
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void setAccelerometerDatas(List<AccelerometerData> accelerometerDatas) {
        setNewAccelerometerData(accelerometerDatas);
    }

    private void setNewAccelerometerData(List<AccelerometerData> accelerometerDatas){

        if(mXEntries == null || mZEntries == null || mYEntries == null  ){
            mXEntries = new ArrayList<>();
            mYEntries = new ArrayList<>();
            mZEntries = new ArrayList<>();
        }else {
            mXEntries.clear();
            mYEntries.clear();
            mZEntries.clear();
        }


        for(int i = 0; i != accelerometerDatas.size(); i++){
            mXEntries.add(new Entry(i, ((float) accelerometerDatas.get(i).getX())));
            mYEntries.add(new Entry(i, ((float) accelerometerDatas.get(i).getY())));
            mZEntries.add(new Entry(i, ((float) accelerometerDatas.get(i).getZ())));
        }

        mXLine = new LineDataSet(mXEntries, "X");
        mXLine.setColor(Color.RED);

        mYLine = new LineDataSet(mYEntries, "Y");
        mYLine.setColor(Color.BLUE);
        mYLine.setAxisDependency(YAxis.AxisDependency.LEFT);

        mZLine = new LineDataSet(mZEntries, "Z");
        mZLine.setColor(Color.GREEN);
        mZLine.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(mXLine);
        dataSets.add(mYLine);
        dataSets.add(mZLine);

        mLineData = new LineData(dataSets);

        mGraph.setData(mLineData);
        mGraph.invalidate();
    }
}
