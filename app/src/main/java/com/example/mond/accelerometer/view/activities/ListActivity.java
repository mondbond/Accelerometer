package com.example.mond.accelerometer.view.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mond.accelerometer.view.fragments.ListFragment;
import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.service.AccelerationService;
import com.example.mond.accelerometer.view.fragments.LineGraphFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private final String LIST_FRAGMENT_NAME = "LIST";
    private final String GRAPH_FRAGMENT_NAME = "GRAPH";

    private final String TAG = "LIST_ACTIVITY";

    public static final String EMAIL_EXTRA = "email";

    private AccelerationService mAccelerometerService;
    private boolean mIsBinded;
    private ServiceConnection mServiceConnection;
    private Intent mServiceIntent;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private Button mStartButton;
    private Button mStopButton;

    private String mEmail;

    private ListFragment mListFragment;
    private LineGraphFragment mGraphFragment;


    private List<Session> mSessions = new ArrayList<>();

    private ViewPager mPager;
    private SectionsPagerAdapter mPagerAdapter;

    private EditText mIntervalValue;
    private EditText mActionTimeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mIntervalValue = (EditText) findViewById(R.id.activity_list_interval_value);
        mActionTimeValue = (EditText) findViewById(R.id.activity_list_time_value);

        mPager = (ViewPager) findViewById(R.id.list_activity_view_pager);
        mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        Bundle bundle = getIntent().getExtras();
        mEmail = bundle.getString(EMAIL_EXTRA);

        Log.d("EMAIL", mEmail);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(mEmail);

        mStartButton = (Button) findViewById(R.id.activity_list_start_btn);
        mStopButton = (Button) findViewById(R.id.activity_list_stop_btn);

        mServiceIntent = new Intent(this, AccelerationService.class);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "MainActivity onServiceConnected");
                AccelerationService.LocalBinder localBinder = (AccelerationService.LocalBinder) service;
                mAccelerometerService = localBinder.getService();
                mAccelerometerService.setEmail(mEmail);
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

                mAccelerometerService.handleStartAccelerometerAction(
                        Integer.parseInt(mIntervalValue.getText().toString()),
                        Integer.parseInt(mActionTimeValue.getText().toString()));
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccelerometerService.handleStopAccelerometerAction();
            }
        });

        mDbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(mSessions != null){
                    mSessions.clear();
                }

                for(DataSnapshot data : dataSnapshot.getChildren()){

                    Session session = new Session();
                    session.setTime(data.getKey());

                    for(DataSnapshot data1 : data.getChildren()){
                        AccelerometerData accelerometerData = data1.getValue(AccelerometerData.class);
                        session.addData(accelerometerData);
                    }

                    mSessions.add(session);
                }
                setAccelerometerDataToFragment(mSessions);
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

    private void setAccelerometerDataToFragment(List<Session> sessions){
        mListFragment.setNewAccelerometerValues(sessions);
    }

    @Override
    public void setSessionAcccelerometerData(List<AccelerometerData> accelerometerDatas) {
        mGraphFragment.setAccelerometerDatas(accelerometerDatas);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(mListFragment == null || mGraphFragment == null) {
                mListFragment = ListFragment.newInstance();
                mGraphFragment = LineGraphFragment.newInstance();
            }

            switch (position) {
                case 0:
                    return mListFragment;
                case 1:
                    return mGraphFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
