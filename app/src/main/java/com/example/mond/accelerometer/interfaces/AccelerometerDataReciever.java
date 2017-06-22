package com.example.mond.accelerometer.interfaces;

import com.example.mond.accelerometer.model.AccelerometerData;

import java.util.ArrayList;

public interface AccelerometerDataReciever {
    void onAccelerometerDataChange(ArrayList<AccelerometerData> accelerometerDatas);
}
