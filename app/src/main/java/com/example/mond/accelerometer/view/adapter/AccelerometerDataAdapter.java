package com.example.mond.accelerometer.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.util.Util;

import java.util.ArrayList;

// TODO: 06/06/17 check SessionAdapter
public class AccelerometerDataAdapter extends RecyclerView.Adapter<AccelerometerDataAdapter.ViewHolder> {

    private ArrayList<AccelerometerData> mAccelerometerDatas;

    public AccelerometerDataAdapter(ArrayList<AccelerometerData> accelerometerDatas) {
        mAccelerometerDatas = accelerometerDatas;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, x, y, z;

        ViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.accelerometer_data_time);
            x = (TextView) view.findViewById(R.id.accelerometer_data_x);
            y = (TextView) view.findViewById(R.id.accelerometer_data_y);
            z = (TextView) view.findViewById(R.id.accelerometer_data_z);
        }
    }

    @Override
    public AccelerometerDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accelerometer_data_item, parent, false);

        return  new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.time.setText(Util.makeTimeStampToDate(mAccelerometerDatas.get(position).getId()));
        holder.x.setText(String.valueOf(mAccelerometerDatas.get(position).getX()));
        holder.y.setText(String.valueOf(mAccelerometerDatas.get(position).getY()));
        holder.z.setText(String.valueOf(mAccelerometerDatas.get(position).getZ()));
    }

    @Override
    public int getItemCount() {
        if (mAccelerometerDatas != null) {
            return mAccelerometerDatas.size();
        } else {
            return 0;
        }
    }

    public void setNewSessionValue(ArrayList<AccelerometerData> accelerometerDatas){
        mAccelerometerDatas = accelerometerDatas;
        notifyDataSetChanged();
    }
}
