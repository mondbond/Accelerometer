package com.example.mond.accelerometer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final int MINIMUM_INTERVAL = 1000;

    public static final String START_ACTION = "start";
    public static final String STOP_ACTION = "stop";
    public static final String BROADCAST_ACTION = "broadcastAction";
    public static final String INTERVAL = "interval";
    public static final String SESSION_TIME = "sessionTime";
    public static final String TIME_OF_START = "timeOfStart";
    public static final String IS_DELAY_STARTING = "isDelayStarting";
    public static final String UID = "uid";

    private BroadcastReceiver mStopBroadcastReciever;

    private final String FIREBASE_ROOT = "/";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private SensorManager mSensorManager;
    private double ax, ay, az;

    private AccelerometerData mAccelerometerData;

    private String mUID;
    private String mSessionId;

    private long mLastTimeSave;

    private boolean mIsRunning;

    private int mSessionTime;
    private int mIntervalTimeInMl;
    private long mActionStartTime;

    private boolean mIsDelayMode;
    private int mStartTime;

    private LocalBinder mLocalBinder = new LocalBinder();

    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference(FIREBASE_ROOT);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if(intent.getAction().equals(START_ACTION) && !mIsRunning){
            Bundle bundle = intent.getExtras();
            mIsDelayMode = bundle.getBoolean(IS_DELAY_STARTING);
            mStartTime = bundle.getInt(TIME_OF_START);
            mIntervalTimeInMl = bundle.getInt(INTERVAL);
            mSessionTime = bundle.getInt(SESSION_TIME);
            mUID = bundle.getString(UID);

            initAccelerometerConfig();
            initSensorListener();
            initStopBroadcastReceiver();
            setIsDataSaving(true);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void initAccelerometerConfig() {
        // TODO: 30/05/17 don't understand what you are doing here. The code should be understandable for other people
        mIntervalTimeInMl = Math.max(Util.secToMl(mIntervalTimeInMl), MINIMUM_INTERVAL);
        mSessionTime = Util.secToMl(mSessionTime);
        mActionStartTime = Util.getLocalTimeStamp();
        mSessionId = Util.makeTimeStampToDate(Util.getLocalTimeStamp());
    }

    public void initSensorListener(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initStopBroadcastReceiver(){
        mStopBroadcastReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                mIsRunning = false;
                stopSelf();
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mStopBroadcastReciever, intentFilter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("SENSOR CHANGE", "-");

        if (mIsDelayMode && isPassingPeriodAndTypeFilter(event) && Util.isTimeToStart(mStartTime)
                && isSessionTimeOver()) {
            saveEventToFirebase(event);
        } else if (isPassingPeriodAndTypeFilter(event) && !isSessionTimeOver()) {
            saveEventToFirebase(event);
        }else if(isSessionTimeOver()){
            stopSelf();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public boolean isPassingPeriodAndTypeFilter(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsRunning
                && (Util.getLocalTimeStamp() - mLastTimeSave) >= mIntervalTimeInMl){
            return true;
        }

        return false;
    }

    public boolean isSessionTimeOver(){
        if(mSessionTime != 0 && (Util.getLocalTimeStamp() - mActionStartTime) >= mSessionTime){
            return true;
        }

        return false;
    }

    private void saveEventToFirebase(SensorEvent event){
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        mAccelerometerData = new AccelerometerData(ax, ay, az);
        Map<String, Object> map = mAccelerometerData.toMap();
        mDbRef.child(Util.clearDots(mUID)).child(mSessionId)
                .child(Util.makeTimeStampToDate(Util.getLocalTimeStamp())).setValue(map);

        mLastTimeSave = Util.getLocalTimeStamp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(mStopBroadcastReciever);
    }

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void setIsDataSaving(boolean dataSaving) {
        mIsRunning = dataSaving;
    }

    public class LocalBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }
}
