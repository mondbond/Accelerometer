package com.example.mond.accelerometer;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.pojo.Session;

import java.util.ArrayList;
import java.util.List;

public class AccelerationDataAdapter extends RecyclerView.Adapter<AccelerationDataAdapter.ViewHolder> {

    private List<Session> mSessions;
    private Context mContext;
    private OnInteractionListener mListener;


    public AccelerationDataAdapter(List<Session> sessions, OnInteractionListener listener, Context context) {
        this.mSessions = sessions;
        this.mListener = listener;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView session;
        LinearLayout dataContainer;

        ViewHolder(View v) {
            super(v);

            session = (TextView) v.findViewById(R.id.session_time);
            dataContainer = (LinearLayout) v.findViewById(R.id.accelerometer_data_container);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accelerometer_data_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        View view = holder.itemView;

        holder.session.setText(mSessions.get(position).getTime());

        TextView x = new TextView(mContext);
        x.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        TextView y = new TextView(mContext);
        y.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        TextView z = new TextView(mContext);
        z.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        for (int i = 0; i != mSessions.get(position).getData().size(); ++i) {
            x.setText(String.valueOf(mSessions.get(position).getData().get(i).getX()));
            y.setText(String.valueOf(mSessions.get(position).getData().get(i).getY()));
            z.setText(String.valueOf(mSessions.get(position).getData().get(i).getZ()));
            holder.dataContainer.addView(x);
            holder.dataContainer.addView(y);
            holder.dataContainer.addView(z);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mListener.onItemClick(mStudents.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mSessions != null) {
            return mSessions.size();
        } else {
            return 0;
        }
    }

    public interface OnInteractionListener {
        void onItemClick(long studentId);
    }

    public List<Session> getSessions() {
        return mSessions;
    }

    public void setSessions(List<Session> mSessions) {
        this.mSessions = mSessions;
    }

    public void setNewSessionValue(List<Session> sessions){
        if(mSessions == null){
            mSessions = new ArrayList<>();
        }
        mSessions.addAll(sessions);
        notifyDataSetChanged();
    }
}
