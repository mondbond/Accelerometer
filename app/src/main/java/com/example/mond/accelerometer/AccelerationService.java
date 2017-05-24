package com.example.mond.accelerometer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AccelerationService extends IntentService implements SensorEventListener {

    private final String TAG ="SERVICE";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String START_ACCELEROMETER = "com.example.mond.accelerometer.action.FOO";
    private static final String STOP_ACCELEROMETER = "com.example.mond.accelerometer.action.BAZ";

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


        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value != null){
                    Log.d(TAG, "not null  = " + value.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cenceled is" + databaseError.getMessage());
            }
        });
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
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleStartAccelerometerAction() {
        // TODO: Handle action Foo

    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];

            saveDataToFirebase(ax, ay, az);
        }
    }

    private void saveDataToFirebase(double x, double y, double z){
        if((System.currentTimeMillis() - mLastTimeSave) >= 1000) {
            mAccelerometerData = new AccelerometerData(x, y, z);
            Map<String, Object> map = mAccelerometerData.toMap();
            mDbRef.child(mEmail).child(mSession).child(Util.currenTimeStampToDate(null)).setValue(map);
            mLastTimeSave = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
