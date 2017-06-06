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

    // TODO: 06/06/17 better to set ViewHolder in the bottom of the main class to separate ViewHolder methods & adapter methods
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView session;

        ViewHolder(View view) {
            super(view);
            // TODO: 06/06/17 butterknife
            session = (TextView) view.findViewById(R.id.session_time);
        }

        public void bind(Session session) {
            // TODO: 06/06/17 implement this
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
        // TODO: 06/06/17 here you create listener each time, don't do this
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

    // TODO: 06/06/17
    public void setSessions(List<Session> mSessions) {
        this.mSessions = mSessions;
        notifyDataSetChanged();
    }

    // TODO: 06/06/17 meaningful names
    public interface AdapterListener {
        void onItemClick(Session accelerationDatas);
    }
}
