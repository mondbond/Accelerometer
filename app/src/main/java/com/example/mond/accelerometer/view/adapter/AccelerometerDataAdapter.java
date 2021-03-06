package com.example.mond.accelerometer.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.util.DataUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccelerometerDataAdapter extends RecyclerView.Adapter<AccelerometerDataAdapter.ViewHolder> {

    private ArrayList<AccelerometerData> mAccelerometerData;

    public AccelerometerDataAdapter(ArrayList<AccelerometerData> accelerometerData) {
        mAccelerometerData = accelerometerData;
    }

    @Override
    public AccelerometerDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accelerometer_data_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mAccelerometerData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mAccelerometerData != null) {
            return mAccelerometerData.size();
        } else {
            return 0;
        }
    }

    public void setNewSessionValue(ArrayList<AccelerometerData> accelerometerDatas) {
        mAccelerometerData = accelerometerDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_accelerometer_item_time)
        TextView time;
        @BindView(R.id.tv_accelerometer_item_x)
        TextView x;
        @BindView(R.id.tv_accelerometer_item_y)
        TextView y;
        @BindView(R.id.tv_accelerometer_item_z)
        TextView z;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(AccelerometerData accelerometerData) {
            time.setText(DataUtil.makeTimeStampToDate(accelerometerData.getId()));
            x.setText(String.valueOf(accelerometerData.getX()));
            y.setText(String.valueOf(accelerometerData.getY()));
            z.setText(String.valueOf(accelerometerData.getZ()));
        }
    }
}
