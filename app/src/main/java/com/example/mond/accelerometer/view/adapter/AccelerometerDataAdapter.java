package com.example.mond.accelerometer.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.util.Util;

public class AccelerometerDataAdapter extends RecyclerView.Adapter<AccelerometerDataAdapter.ViewHolder> {

    private Session mSession;

    public AccelerometerDataAdapter(Session session) {
        mSession = session;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView x;
        TextView y;
        TextView z;
        ViewHolder(View v) {
            super(v);
            time = (TextView) v.findViewById(R.id.accelerometer_data_time);
            x = (TextView) v.findViewById(R.id.accelerometer_data_x);
            y = (TextView) v.findViewById(R.id.accelerometer_data_y);
            z = (TextView) v.findViewById(R.id.accelerometer_data_z);
        }
    }

    @Override
    public AccelerometerDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accelerometer_data_item, parent, false);

        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.time.setText(Util.makeTimeStampToDate(mSession.getData().get(position).getId()));
        holder.x.setText(String.valueOf(mSession.getData().get(position).getX()));
        holder.y.setText(String.valueOf(mSession.getData().get(position).getY()));
        holder.z.setText(String.valueOf(mSession.getData().get(position).getZ()));
    }

    @Override
    public int getItemCount() {
        if (mSession != null) {
            return mSession.getData().size();
        } else {
            return 0;
        }
    }

    public void setNewSessionValue(Session sessionValue){
        mSession = sessionValue;
        notifyDataSetChanged();
    }
}
