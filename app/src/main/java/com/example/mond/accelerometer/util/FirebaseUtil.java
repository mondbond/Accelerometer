package com.example.mond.accelerometer.util;


import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.model.Session;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {

    public static final String FIREBASE_SESSIONS_NODE = "sessions";
    public static final String FIREBASE_SESSION_ID = "sessionId";
    public static final String FIREBASE_ACCELEROMETER_DATA_NODE = "sessionData";

    private static FirebaseDatabase mDb = FirebaseDatabase.getInstance();

    public static void pushAccelerometerData(AccelerometerData accelerometerData, long sessionId,  String uid){
        mDb.getReference().child(FirebaseUtil.FIREBASE_ACCELEROMETER_DATA_NODE).child(uid)
                .child(String.valueOf(sessionId)).child(Util.makeCurrentTimeStampToDate()).setValue(accelerometerData);
    }

    public static void saveSession(long sessionId, int intervalTime, String uid) {
        mDb.getReference().child(FirebaseUtil.FIREBASE_SESSIONS_NODE).child(uid).child(String.valueOf(sessionId)).setValue(new Session(sessionId, intervalTime));
    }
}
