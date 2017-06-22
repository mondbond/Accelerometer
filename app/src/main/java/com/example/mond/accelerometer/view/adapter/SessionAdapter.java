package com.example.mond.accelerometer.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.Session;
import com.example.mond.accelerometer.util.DataUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {

    private List<Session> mSessions;
    private OnItemClickListener mListener;

    public SessionAdapter(List<Session> sessions, OnItemClickListener listener, Context context) {
        this.mSessions = sessions;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mSessions.get(position));
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

    public interface OnItemClickListener {
        void onItemClick(Session accelerationDatas);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_session_time)
        TextView sessionItem;
        private Session mSession;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.tv_session_time)
        public void sessionItemClicked() {
            mListener.onItemClick(mSession);
        }

        public void bind(Session session) {
            mSession = session;
            sessionItem.setText(DataUtil.makeTimeStampToDate(session.getSessionId()));
        }
    }
}
