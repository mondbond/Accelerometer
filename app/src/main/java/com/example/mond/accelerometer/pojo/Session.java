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

    private String mTime;
    private List<AccelerometerData> mData;

    public Session(String time, List<AccelerometerData> data) {
        mTime = time;
        mData = data;
    }

    public Session() {
        mData = new ArrayList<>();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("time", mTime);
        result.put("data", mData);

        return result;
    }

    public void addAccelerometerData(AccelerometerData accelerometerData){
        mData.add(accelerometerData);
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public List<AccelerometerData> getData() {
        return mData;
    }

    public void setData(List<AccelerometerData> data) {
        mData = data;
    }

    public void addData(AccelerometerData accelerometerData){
        mData.add(accelerometerData);
    }

    protected Session(Parcel in) {
        mTime = in.readString();
        if (in.readByte() == 0x01) {
            mData = new ArrayList<AccelerometerData>();
            in.readList(mData, AccelerometerData.class.getClassLoader());
        } else {
            mData = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTime);
        if (mData == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mData);
        }
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
}