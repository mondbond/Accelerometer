package com.example.mond.accelerometer.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.model.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {

    public static final String FIREBASE_SESSIONS_NODE = "sessions";
    public static final String FIREBASE_SESSION_ID = "sessionId";
    public static final String FIREBASE_ACCELEROMETER_DATA_NODE = "sessionData";

    private static FirebaseDatabase mDb = FirebaseDatabase.getInstance();

    public static void pushAccelerometerData(AccelerometerData accelerometerData, long sessionId,
                                             String uid){
        mDb.getReference().child(FirebaseUtil.FIREBASE_ACCELEROMETER_DATA_NODE).child(uid)
                .child(String.valueOf(sessionId)).child(Util.makeCurrentTimeStampToDate())
                .setValue(accelerometerData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("FIREBASE_UPLOAD", "Done");
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("FIREBASE_UPLOAD", "Upload success");
            }
        });

//        .addOnCompleteListener()
//        .addOnSuccessListener()
//        - for what does it need ?
//        - can be used to get the result after async operation
//        - done. I understand, but for what we need this results in this case ?
    }

    public static void saveSession(long sessionId, int intervalTime, String uid) {
        mDb.getReference().child(FirebaseUtil.FIREBASE_SESSIONS_NODE).child(uid)
                .child(String.valueOf(sessionId)).setValue(new Session(sessionId, intervalTime));
    }
}
