package com.example.mond.accelerometer.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class AccelerometerData {

    private long id;

    private double x;
    private double y;
    private double z;

    public AccelerometerData() {}

    public AccelerometerData(double x, double y, double z) {
        this.id = System.currentTimeMillis();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("x", x);
        result.put("y", y);
        result.put("z", z);

        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
