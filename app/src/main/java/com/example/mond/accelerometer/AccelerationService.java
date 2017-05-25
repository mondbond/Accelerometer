package com.example.mond.accelerometer;

import android.app.Application;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class AccelerationService extends IntentService implements SensorEventListener {

    private final String TAG ="SERVICE";

    private final IBinder mBinder = new LocalBinder();

    public static final String ACCELEROMETER_ACTION = "accelerometerAction";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String START_ACCELEROMETER = "com.example.mond.accelerometer.action.FOO";
    public static final String STOP_ACCELEROMETER = "com.example.mond.accelerometer.action.BAZ";

    // TODO: Rename parameters
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

    private LocalBinder mLocalBinder = new LocalBinder();

    public AccelerationService() {
        super("AccelerationService");
    }

    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mEmail = "vanya0794@gmailcom";

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference("/");

        mSession = Util.currenTimeStampToDate(null);
    }

    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AccelerationService.class);
        intent.setAction(START_ACCELEROMETER);
        intent.putExtra(ACCELEROMETER_BREAK_IN_SEC, param1);
        intent.putExtra(WORK_TIME_IN_SEC, param2);
        context.startService(intent);
    }

    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AccelerationService.class);
        intent.setAction(STOP_ACCELEROMETER);
        intent.putExtra(ACCELEROMETER_BREAK_IN_SEC, param1);
        intent.putExtra(WORK_TIME_IN_SEC, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (START_ACCELEROMETER.equals(action)) {
                final String param1 = intent.getStringExtra(ACCELEROMETER_BREAK_IN_SEC);
                final String param2 = intent.getStringExtra(WORK_TIME_IN_SEC);
                handleStartAccelerometerAction();
            } else if (STOP_ACCELEROMETER.equals(action)) {
                final String param1 = intent.getStringExtra(ACCELEROMETER_BREAK_IN_SEC);
                final String param2 = intent.getStringExtra(WORK_TIME_IN_SEC);
                handleStopAccelerometerAction();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    public void handleStartAccelerometerAction() {
        setIsDataSaving(true);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    public void handleStopAccelerometerAction() {
        setIsDataSaving(false);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.d(TAG, "boolean = " + String.valueOf(mIsDataSaving));
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsDataSaving){
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];

            saveDataToFirebase(ax, ay, az);
        }
    }

    public void saveDataToFirebase(double x, double y, double z){
        if((System.currentTimeMillis() - mLastTimeSave) >= 3000) {
            mAccelerometerData = new AccelerometerData(x, y, z);
            Map<String, Object> map = mAccelerometerData.toMap();
            mDbRef.child(mEmail).child(mSession).child(Util.currenTimeStampToDate(null)).setValue(map);
            mLastTimeSave = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public IBinder onBind(Intent intent) {
        Log.d(TAG, "MyService onBind");
        return mLocalBinder;
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "MyService onRebind");
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "MyService onUnbind");
        return super.onUnbind(intent);
    }


    public boolean isDataSaving() {
        return mIsDataSaving;
    }

    public void setIsDataSaving(boolean dataSaving) {
        mIsDataSaving = dataSaving;
    }




    public class LocalBinder extends Binder {
        AccelerationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AccelerationService.this;
        }
    }
}
