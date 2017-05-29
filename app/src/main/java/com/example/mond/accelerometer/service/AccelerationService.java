package com.example.mond.accelerometer.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AccelerationService extends IntentService implements SensorEventListener {

    private final String TAG ="SERVICE";

    public static final String START_ACCELEROMETER = "com.example.mond.accelerometer.action.START";
    public static final String STOP_ACCELEROMETER = "com.example.mond.accelerometer.action.STOP";

    private static final String ACCELEROMETER_BREAK_IN_SEC = "accelerometerBreakInSec";
    private static final String WORK_TIME_IN_SEC = "workTimeInSec";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private SensorManager mSensorManager;
    private double ax, ay, az;

    private AccelerometerData mAccelerometerData;

    private String mEmail;
    private String mSession;

    private long mLastTimeSave;

    private boolean mIsDataSaving;

    private int mActionTime;
    private int mIntervalTime;
    private long mActionStartTime;

    private boolean mIsWorkAtTime;
    private int mDayStartTime;

    private LocalBinder mLocalBinder = new LocalBinder();

    public AccelerationService() {
        super("AccelerationService");
    }

    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference("/");

        mSession = Util.currenTimeStampToDate(null);
    }

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AccelerationService.class);
        intent.setAction(START_ACCELEROMETER);
        intent.putExtra(ACCELEROMETER_BREAK_IN_SEC, param1);
        intent.putExtra(WORK_TIME_IN_SEC, param2);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AccelerationService.class);
        intent.setAction(STOP_ACCELEROMETER);
        intent.putExtra(ACCELEROMETER_BREAK_IN_SEC, param1);
        intent.putExtra(WORK_TIME_IN_SEC, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (START_ACCELEROMETER.equals(action)) {
//                final String param1 = intent.getStringExtra(ACCELEROMETER_BREAK_IN_SEC);
//                final String param2 = intent.getStringExtra(WORK_TIME_IN_SEC);
//                handleStartAccelerometerAction();
//            } else if (STOP_ACCELEROMETER.equals(action)) {
//                final String param1 = intent.getStringExtra(ACCELEROMETER_BREAK_IN_SEC);
//                final String param2 = intent.getStringExtra(WORK_TIME_IN_SEC);
//                handleStopAccelerometerAction();
//            }
//        }
    }

    public void handleStartAccelerometerAction(int intervalTime, int actionTime) {
        if(intervalTime == 0 ) {
            mIntervalTime = 1000;
        }else {
            mIntervalTime = intervalTime * 1000;
        }

        if(actionTime == 0 ) {
            mActionTime = 0;
        }else {
            mActionTime = actionTime * 1000;
        }

        mActionStartTime = System.currentTimeMillis();
        Log.d("STARTTTTTTTTTTTTTTTT", "start");
        setIsDataSaving(true);
    }

    public void handleStopAccelerometerAction() {
        setIsDataSaving(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        Log.d("IS ", String.valueOf(mIsWorkAtTime) +String.valueOf(Util.isTimeToStart(mDayStartTime)) +  "/ " + String.valueOf(System.currentTimeMillis() % 86400000) + " / " + String.valueOf(mDayStartTime));

        if (mIsWorkAtTime) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsDataSaving && Util.isTimeToStart(mDayStartTime)) {
                Log.d("1", "-----");
                ax = event.values[0];
                ay = event.values[1];
                az = event.values[2];

                saveDataToFirebase(ax, ay, az);
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsDataSaving) {
            Log.d("2", "-----");
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];

            saveDataToFirebase(ax, ay, az);
        }
    }


    public void saveDataToFirebase(double x, double y, double z){
        if ((System.currentTimeMillis() - mLastTimeSave) >= mIntervalTime) {
            mAccelerometerData = new AccelerometerData(x, y, z);
            Map<String, Object> map = mAccelerometerData.toMap();
            mDbRef.child(mEmail).child(mSession).child(Util.currenTimeStampToDate(null)).setValue(map);
            mLastTimeSave = System.currentTimeMillis();
        }else if(mActionTime != 0 && (System.currentTimeMillis() - mActionStartTime) >= mActionTime) {
            handleStopAccelerometerAction();
        }
    }

    public void setWorkAtTime(boolean isWorkAtTime, int ml){
        mIsWorkAtTime = isWorkAtTime;
        mDayStartTime = ml;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void setIsDataSaving(boolean dataSaving) {
        mIsDataSaving = dataSaving;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public class LocalBinder extends Binder {
        public AccelerationService getService() {
            return AccelerationService.this;
        }
    }
}
