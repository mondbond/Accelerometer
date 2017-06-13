package com.example.mond.accelerometer.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import com.example.mond.accelerometer.Constants;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class AccelerometerService extends Service implements SensorEventListener{

    private final String FIREBASE_ROOT = "/";

    public static final int MINIMUM_INTERVAL = 1000;

    public static final String ACCELEROMETER_SERVICE_START_ACTION = "startAction";
    public static final String ACCELEROMETER_SERVICE_STOP_ACTION = "stopAction";

    private BroadcastReceiver mStopBroadcastReciever;

    private String mUID;
    private long mSessionId;

    private long mLastTimeSave;
    private int mSessionTime;
    private int mIntervalTimeInMl;
    private long mActionStartTime;

    private boolean mIsDelayMode;
    private int mStartTime;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private SensorManager mSensorManager;

    private double ax, ay, az;
    private AccelerometerData mAccelerometerData;

    private final IBinder mBinder = new LocalBinder();


    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference(FIREBASE_ROOT);
    }

    // TODO: ? 13/06/17 crash after service restart. Service and Activity should be independent
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if(intent.getAction().equals(ACCELEROMETER_SERVICE_START_ACTION)){


            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.ACCELEROMETER_PARAMETERS_SHARED_PREFERENCE,
                    Context.MODE_PRIVATE);

            mIsDelayMode = sharedPref.getBoolean(Constants.ACCELEROMETER_IS_START_ON_TIME, false);
            mStartTime = sharedPref.getInt(Constants.ACCELEROMETER_TIME_OF_START_IN_ML, 0);
            mIntervalTimeInMl = (int) Math.max(TimeUnit.SECONDS.toMillis(sharedPref.getInt(Constants.ACCELEROMETER_INTERVAL, 1000)), MINIMUM_INTERVAL);
            mSessionTime = (int) TimeUnit.SECONDS.toMillis(sharedPref.getInt(Constants.ACCELEROMETER_SERVICE_WORK_TIME, 0));
            mUID = sharedPref.getString(Constants.UID, "");

            initSensorListener();
            initAccelerometerConfigAndSaveSession();
            initStopBroadcastReceiver();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initStartActionTime() {
        if(mActionStartTime != 0) {
            mActionStartTime = Util.getLocalTimeStamp();
        }
    }

    private void initSensorListener() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initAccelerometerConfigAndSaveSession() {
        mSessionId = Util.getLocalTimeStamp();

        FirebaseUtil.saveSession(mSessionId, mIntervalTimeInMl, mUID);
    }

    private void initStopBroadcastReceiver() {
        // TODO: - 06/06/17 why local BroadcastReceiver?
        // we don't need to give it more opportunities than it need and it's used only in this app
        // TODO: - 13/06/17 service can be bound and terminated directly from activity

        mStopBroadcastReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
            stopSelf();
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACCELEROMETER_SERVICE_STOP_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mStopBroadcastReciever, intentFilter);
    }

    public void stopAccelerometer(){

        Log.d("STOPACCELEROMETER", "-");
        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mIsDelayMode && isPassingPeriodAndTypeFilter(event)) {
            if(Util.isTimeToStart(mStartTime) && !isSessionTimeOver()){
                initStartActionTime();
                saveEventToFirebase(event);
            }
        } else if (isPassingPeriodAndTypeFilter(event) && !isSessionTimeOver()) {
            initStartActionTime();
            saveEventToFirebase(event);
        }else if(isSessionTimeOver()){
            stopSelf();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private boolean isPassingPeriodAndTypeFilter(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                && (Util.getLocalTimeStamp() - mLastTimeSave) >= mIntervalTimeInMl){
            return true;
        }

        return false;
    }

    private boolean isSessionTimeOver(){
//        if session time == 0 -> it should work until user woldn't stop it by himself
        if(mSessionTime == 0){
            return false;
        }else if((Util.getLocalTimeStamp() - mActionStartTime) >= mSessionTime){
            return true;
        }

        return false;
    }

    private void saveEventToFirebase(SensorEvent event){
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        mAccelerometerData = new AccelerometerData(ax, ay, az);
        FirebaseUtil.pushAccelerometerData(mAccelerometerData, mSessionId, mUID);
//        .addOnCompleteListener()
//        .addOnSuccessListener()
//        for what does it need ? can be used to get the result after async operation

        mLastTimeSave = Util.getLocalTimeStamp();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("UNBIND", "-");
//        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }
}
