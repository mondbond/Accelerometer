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

import com.example.mond.accelerometer.Constants;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AccelerometerService extends Service implements SensorEventListener{

    private final String FIREBASE_ROOT = "/";

    public static final int MINIMUM_INTERVAL = 1000;

    public static final String START_ACTION = "start";
    public static final String BROADCAST_ACTION = "broadcastAction";
    public static final String INTERVAL = "interval";
    public static final String SESSION_TIME = "sessionTime";
    public static final String TIME_OF_START = "timeOfStart";
    public static final String IS_DELAY_STARTING = "isDelayStarting";
    public static final String UID = "uid";

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

    private SensorEvent mLastEvent;

    private ExecutorService mExecutorService;
    private AsyncSave mAsyncSave;

    private LocalBinder mLocalBinder = new LocalBinder();

    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference(FIREBASE_ROOT);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if(intent.getAction().equals(START_ACTION)){
            Bundle bundle = intent.getExtras();
            mIsDelayMode = bundle.getBoolean(IS_DELAY_STARTING);
            mStartTime = bundle.getInt(TIME_OF_START);
            mIntervalTimeInMl = (int) Math.max(TimeUnit.SECONDS.toMillis(bundle.getInt(INTERVAL)), MINIMUM_INTERVAL);
            mSessionTime = (int) TimeUnit.SECONDS.toMillis(bundle.getInt(SESSION_TIME));
            mUID = bundle.getString(UID);

            initSensorListener();
            initAccelerometerConfig();
            initStopBroadcastReceiver();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initStartActionTime(){
        if(mActionStartTime != 0) {
            mActionStartTime = Util.getLocalTimeStamp();
        }
    }

    private void initSensorListener(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initAccelerometerConfig() {
        mSessionId = Util.getLocalTimeStamp();
        saveSessionToFirebase();
        mExecutorService = Executors.newFixedThreadPool(2);
        mAsyncSave = new AsyncSave();
    }

    private void saveSessionToFirebase(){
        mDbRef.child(Constants.FIREBASE_SESSIONS_NODE).child(mUID).child(String.valueOf(mSessionId)).child(Constants.FIREBASE_SESSIONS_INTERVAL_INFO)
                .setValue(String.valueOf(mIntervalTimeInMl));
        mDbRef.child(Constants.FIREBASE_SESSIONS_NODE).child(mUID).child(String.valueOf(mSessionId)).child(Constants.FIREBASE_SESSION_ID)
                .setValue(String.valueOf(mSessionId));
    }

    private void initStopBroadcastReceiver(){
        mStopBroadcastReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                stopSelf();
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mStopBroadcastReciever, intentFilter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mIsDelayMode && isPassingPeriodAndTypeFilter(event)) {
            if(Util.isTimeToStart(mStartTime) && !isSessionTimeOver()){
                initStartActionTime();
                startAsyncSave(event);
            }
        } else if (isPassingPeriodAndTypeFilter(event) && !isSessionTimeOver()) {
            initStartActionTime();
            startAsyncSave(event);
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

    private void startAsyncSave(SensorEvent event){
        mLastEvent = event;
        mExecutorService.submit(mAsyncSave);
    }

    private void saveEventToFirebase(SensorEvent event){
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        mAccelerometerData = new AccelerometerData(ax, ay, az);
        Map<String, Object> map = mAccelerometerData.toMap();
        mDbRef.child(Constants.FIREBASE_ACCELEROMETER_DATAS_NODE).child(mUID)
                .child(String.valueOf(mSessionId)).child(Util.makeCurrentTimeStampToDate()).setValue(map);

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

    public class LocalBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }

    private class AsyncSave implements Runnable {

        @Override
        public void run() {
            saveEventToFirebase(mLastEvent);
        }
    }
}
