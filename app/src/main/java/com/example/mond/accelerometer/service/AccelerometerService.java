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

import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final String START_ACTION = "start";
    public static final String STOP_ACTION = "stop";

    public static final String BROADCAST_ACTION = "broadcastAction";

    public static final String INTERVAL = "interval";
    public static final String SESSION_TIME = "sessionTime";
    public static final String TIME_OF_START = "timeOfStart";
    public static final String IS_DELAY_STARTING = "isDelayStarting";
    public static final String UID = "uid";

    private BroadcastReceiver mBroadcastReciever;

    private final String FIREBASE_ROOT = "/";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private SensorManager mSensorManager;
    private double ax, ay, az;

    private AccelerometerData mAccelerometerData;

    private String mUID;
    private String mSession;

    private long mLastTimeSave;

    private boolean mIsDataSaving;

    private int mSessionTime;
    private int mIntervalTime;
    private long mActionStartTime;

    private boolean mIsWorkAtTime;
    private int mDayStartTime;

    private LocalBinder mLocalBinder = new LocalBinder();

    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference(FIREBASE_ROOT);

        mBroadcastReciever = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                mIsDataSaving = false;
                stopSelf();
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        intFilt.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReciever, intFilt);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if(intent.getAction().equals(START_ACTION) && !mIsDataSaving){
            Bundle bundle = intent.getExtras();

            mIsWorkAtTime = bundle.getBoolean(IS_DELAY_STARTING);
            mDayStartTime = bundle.getInt(TIME_OF_START);
            mUID = bundle.getString(UID);
            startAccelerometerAction(bundle.getInt(INTERVAL), bundle.getInt(SESSION_TIME));
        }

        return super.onStartCommand(intent, flags, startId);
    }

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
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                    && mIsDataSaving && Util.isTimeToStart(mDayStartTime)) {
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
        } else if(mSessionTime != 0
                && (Util.getLocalTimeStamp() - mActionStartTime) >= mSessionTime) {
            handleStopAccelerometerAction();
        }
    }

    private void saveEventToFirebase(SensorEvent event){
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        mAccelerometerData = new AccelerometerData(ax, ay, az);
        Map<String, Object> map = mAccelerometerData.toMap();
        mDbRef.child(Util.clearDots(mUID)).child(mSession)
                .child(Util.makeTimeStampToDate(Util.getLocalTimeStamp())).setValue(map);

        mLastTimeSave = Util.getLocalTimeStamp();
    }

    public void setWorkAtTime (boolean isWorkAtTime, int ml) {
        mIsWorkAtTime = isWorkAtTime;
        mDayStartTime = ml;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(mBroadcastReciever);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void setIsDataSaving(boolean dataSaving) {
        mIsDataSaving = dataSaving;
    }

    public class LocalBinder extends Binder {
        public AccelerometerService getService() {
            return AccelerometerService.this;
        }
    }
}
