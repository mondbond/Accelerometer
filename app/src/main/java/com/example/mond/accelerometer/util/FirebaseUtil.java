package com.example.mond.accelerometer.util;


import android.net.Uri;

import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.model.Session;
import com.example.mond.accelerometer.model.StorageFile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {

    public static final String FIREBASE_SESSIONS_NODE = "sessions";
    public static final String FIREBASE_SESSION_ID = "sessionId";
    public static final String FIREBASE_ACCELEROMETER_DATA_NODE = "sessionData";
    public static final String FIREBASE_FILE_REF_NODE = "references";

    private static FirebaseDatabase mDb = FirebaseDatabase.getInstance();

    public static void pushAccelerometerData(AccelerometerData accelerometerData, long sessionId,  String uid){
        mDb.getReference().child(FirebaseUtil.FIREBASE_ACCELEROMETER_DATA_NODE).child(uid)
                .child(String.valueOf(sessionId)).child(Util.makeCurrentTimeStampToDate()).setValue(accelerometerData);
    }

    public static void saveSession(long sessionId, int intervalTime, String uid) {
        mDb.getReference().child(FirebaseUtil.FIREBASE_SESSIONS_NODE).child(uid).child(String.valueOf(sessionId)).setValue(new Session(sessionId, intervalTime));
    }

    public static void saveImageRef(StorageReference ref, final String name, final String uid){
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                mDb.getReference().child(FirebaseUtil.FIREBASE_FILE_REF_NODE).child(uid)
                        .child(String.valueOf(Util.getLocalTimeStamp())).setValue(new StorageFile(name, uri.toString()));
            }
        });
    }
}
