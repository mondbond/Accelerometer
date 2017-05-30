package com.example.mond.accelerometer.service;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AccelerometerService extends IntentService implements SensorEventListener {

    private final String FIREBASE_ROOT = "/";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private SensorManager mSensorManager;
    private double ax, ay, az;

    private AccelerometerData mAccelerometerData;

    private String mEmail;
    private String mSession;

    private long mLastTimeSave;

    private boolean mIsDataSaving;

    private int mSessionTime;
    private int mIntervalTime;
    private long mActionStartTime;

    private boolean mIsWorkAtTime;
    private int mDayStartTime;

    private LocalBinder mLocalBinder = new LocalBinder();

    public AccelerometerService() {
        super("AccelerometerService");
    }

    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference(FIREBASE_ROOT);
    }

    @Override
    protected void onHandleIntent(Intent intent) {}

    public void startAccelerometerAction(int intervalTime, int actionTime) {
        // TODO: 30/05/17 don't understand what you are doing here. The code should be understandable for other people
        if(intervalTime == 0 ) {
            mIntervalTime = 1000;
        }else {
            mIntervalTime = intervalTime * 1000;
        }

        if(actionTime == 0 ) {
            mSessionTime = 0;
        }else {
            mSessionTime = actionTime * 1000;
        }

        mActionStartTime = Util.getLocalTimeStamp();
        mSession = Util.makeTimeStampToDate(Util.getLocalTimeStamp());

        setIsDataSaving(true);
    }

    public void handleStopAccelerometerAction() {
        setIsDataSaving(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mIsWorkAtTime) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsDataSaving && Util.isTimeToStart(mDayStartTime)) {
                checkEventBeforeSave(event);
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsDataSaving) {
            checkEventBeforeSave(event);
        }
    }

    // TODO: 30/05/17 check what? Check or check and do some actions?
    public void checkEventBeforeSave(SensorEvent event){
        if ((Util.getLocalTimeStamp() - mLastTimeSave) >= mIntervalTime) {
            saveEventToFirebase(event);
        } else if(mSessionTime != 0 && (Util.getLocalTimeStamp() - mActionStartTime) >= mSessionTime) {
            handleStopAccelerometerAction();
        }
    }

    private void saveEventToFirebase(SensorEvent event){
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        mAccelerometerData = new AccelerometerData(ax, ay, az);
        Map<String, Object> map = mAccelerometerData.toMap();
        mDbRef.child(mEmail).child(mSession).child(Util.makeTimeStampToDate(Util.getLocalTimeStamp())).setValue(map);

        mLastTimeSave = Util.getLocalTimeStamp();
    }

    public void setWorkAtTime (boolean isWorkAtTime, int ml) {
        mIsWorkAtTime = isWorkAtTime;
        mDayStartTime = ml;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void setIsDataSaving(boolean dataSaving) {
        mIsDataSaving = dataSaving;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public class LocalBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }
}
