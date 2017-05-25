package com.example.mond.accelerometer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mond.accelerometer.pojo.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListActivity extends AppCompatActivity {

    private final String TAG = "LIST_ACTIVITY";

    public static final String EMAIL_EXTRA = "email";

    private AccelerationService mAccelerationService;
    private boolean mIsBinded;
    private ServiceConnection mServiceConnection;
    private Intent mServiceIntent;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private Button mStartButton;
    private Button mStopButton;

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle bundle = getIntent().getExtras();
        mEmail = bundle.getString(EMAIL_EXTRA);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(mEmail);

        mStartButton = (Button) findViewById(R.id.activity_list_start_btn);
        mStopButton = (Button) findViewById(R.id.activity_list_stop_btn);

//        mServiceIntent = new Intent("com.example.mond.accelerometer.AccelerationService");
        mServiceIntent = new Intent(this, AccelerationService.class);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "MainActivity onServiceConnected");
                AccelerationService.LocalBinder localBinder = (AccelerationService.LocalBinder) service;
                mAccelerationService = localBinder.getService();
                mIsBinded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "MainActivity onServiceDISnnected");
                mIsBinded = false;
            }
        };

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccelerationService.handleStartAccelerometerAction();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccelerationService.handleStopAccelerometerAction();
            }
        });

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo value = dataSnapshot.getValue(UserInfo.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cenceled is" + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mServiceConnection);
    }
}
