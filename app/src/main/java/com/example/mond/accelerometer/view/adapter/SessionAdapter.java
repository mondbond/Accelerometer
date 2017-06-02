package com.example.mond.accelerometer.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.util.Util;

import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<Session> mSessions;
    private AdapterListener mListener;

    public SessionAdapter(List<Session> sessions, AdapterListener listener, Context context) {
        this.mSessions = sessions;
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView session;

        ViewHolder(View view) {
            super(view);
            session = (TextView) view.findViewById(R.id.session_time);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int finalPosition = position;
        holder.session.setText(Util.makeTimeStampToDate(mSessions.get(position).getSessionId()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mSessions.get(finalPosition));
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

    public void setSessions(List<Session> mSessions) {
        this.mSessions = mSessions;
        notifyDataSetChanged();
    }

    public interface AdapterListener {
        void onItemClick(Session accelerationDatas);
    }
}
