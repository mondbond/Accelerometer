package com.example.mond.accelerometer.events;


import com.example.mond.accelerometer.model.AccelerometerData;

import java.util.ArrayList;

public class AccelerometerDataChangeEvent {

    ArrayList<AccelerometerData> accelerometerDataList;

    public AccelerometerDataChangeEvent(ArrayList<AccelerometerData> accelerometerDataList) {
        this.accelerometerDataList = accelerometerDataList;
    }

    public ArrayList<AccelerometerData> getAccelerometerDataList() {
        return accelerometerDataList;
    }

    public void setAccelerometerDataList(ArrayList<AccelerometerData> accelerometerDataList) {
        this.accelerometerDataList = accelerometerDataList;
    }
}
