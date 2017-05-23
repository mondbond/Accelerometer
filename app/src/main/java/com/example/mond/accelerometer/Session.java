package com.example.mond.accelerometer;

import java.util.List;

public class Session {

    private String mTime;
    private List<AccelerometerData> mData;

    public Session(String time, List<AccelerometerData> data) {
        mTime = time;
        mData = data;
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
}
