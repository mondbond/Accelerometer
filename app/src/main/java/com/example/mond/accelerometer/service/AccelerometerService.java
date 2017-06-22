package com.example.mond.accelerometer.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.mond.accelerometer.Constants;
import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.util.DataUtil;
import com.example.mond.accelerometer.util.FirebaseUtil;

import java.util.concurrent.TimeUnit;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final int MINIMUM_INTERVAL = 1000;

    public static final String ACCELEROMETER_SERVICE_START_ACTION = "startAction";
    public static final int ACCELEROMETER_START_NOTIFICATION_ID = 34;

    private String mUID;
    private long mSessionId;

    private long mLastTimeSave;
    private int mSessionTime;
    private int mIntervalTimeInMl;
    private long mActionStartTime;

    private boolean mIsDelayMode;
    private int mStartTime;

    private SensorManager mSensorManager;

    private double ax, ay, az;
    private AccelerometerData mAccelerometerData;

    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(ACCELEROMETER_SERVICE_START_ACTION)) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(Constants.ACCELEROMETER_PARAMETERS_SHARED_PREFERENCE,
                    Context.MODE_PRIVATE);

            mIsDelayMode = sharedPref.getBoolean(Constants.ACCELEROMETER_IS_START_ON_TIME, false);
            mStartTime = sharedPref.getInt(Constants.ACCELEROMETER_TIME_OF_START_IN_ML, 0);
            mIntervalTimeInMl = (int) Math.max(TimeUnit.SECONDS.toMillis(sharedPref.getInt(Constants.ACCELEROMETER_INTERVAL, 1000)), MINIMUM_INTERVAL);
            mSessionTime = (int) TimeUnit.SECONDS.toMillis(sharedPref.getInt(Constants.ACCELEROMETER_SERVICE_WORK_TIME, 0));
            mUID = sharedPref.getString(Constants.UID, "");

            initSensorListener();
            initAccelerometerConfigAndSaveSession();
            buildNotification();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildNotification() {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle(getResources().getString(R.string.text_accelerometer_start))
                        .setContentText(getResources().getString(R.string.text_accelerometer_start_session)
                                + DataUtil.makeTimeStampToDate(mSessionId));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ACCELEROMETER_START_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void initStartActionTime() {
        if (mActionStartTime != 0) {
            mActionStartTime = DataUtil.getLocalTimeStamp();
        }
    }

    private void initSensorListener() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void initAccelerometerConfigAndSaveSession() {
        mSessionId = DataUtil.getLocalTimeStamp();

        FirebaseUtil.saveSession(mSessionId, mIntervalTimeInMl, mUID);
    }

    public void stopAccelerometer() {
        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mIsDelayMode && isPassingPeriodAndTypeFilter(event)) {
            if (DataUtil.isTimeToStart(mStartTime) && !isSessionTimeOver()) {
                initStartActionTime();
                saveEventToFirebase(event);
            }
        } else if (isPassingPeriodAndTypeFilter(event) && !isSessionTimeOver()) {
            initStartActionTime();
            saveEventToFirebase(event);
        } else if (isSessionTimeOver()) {
            stopSelf();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private boolean isPassingPeriodAndTypeFilter(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
                && (DataUtil.getLocalTimeStamp() - mLastTimeSave) >= mIntervalTimeInMl) {
            return true;
        }

        return false;
    }

    private boolean isSessionTimeOver() {
//        if session time == 0 -> it should work until user woldn't stop it by himself
        if (mSessionTime == 0) {
            return false;
        } else if ((DataUtil.getLocalTimeStamp() - mActionStartTime) >= mSessionTime) {
            return true;
        }

        return false;
    }

    private void saveEventToFirebase(SensorEvent event) {
        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];

        mAccelerometerData = new AccelerometerData(ax, ay, az);
        FirebaseUtil.pushAccelerometerData(mAccelerometerData, mSessionId, mUID);
        mLastTimeSave = DataUtil.getLocalTimeStamp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null) {
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
