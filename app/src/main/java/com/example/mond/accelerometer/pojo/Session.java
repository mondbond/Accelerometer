package com.example.mond.accelerometer.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Session {

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
}
