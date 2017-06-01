package com.example.mond.accelerometer.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Session implements Parcelable {

    private int mSessionInterval;
    private long mSessionId;

    public Session(int sessionInterval, long sessionId) {
        mSessionInterval = sessionInterval;
        mSessionId = sessionId;
    }

    public Session() {}

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("interval", mSessionInterval);
        result.put("id", mSessionId);

        return result;
    }

    protected Session(Parcel in) {
        mSessionInterval = in.readInt();
        mSessionId = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mSessionInterval);
        dest.writeLong(mSessionId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public int getSessionInterval() {
        return mSessionInterval;
    }

    public void setSessionInterval(int sessionInterval) {
        mSessionInterval = sessionInterval;
    }

    public long getSessionId() {
        return mSessionId;
    }

    public void setSessionId(long sessionId) {
        mSessionId = sessionId;
    }
}
