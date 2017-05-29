package com.example.mond.accelerometer.view.activities;

import android.app.TimePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mond.accelerometer.service.AccelerometerService;
import com.example.mond.accelerometer.util.Util;
import com.example.mond.accelerometer.view.fragments.ListFragment;
import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.view.fragments.LineGraphFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener, TimePickerDialog.OnTimeSetListener {

    public static final String EMAIL_EXTRA = "email";

    private AccelerometerService mAccelerometerService;
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
    private Button mTimeExecutionSetterBtn;
    private TextView mTimeExecutionValue;

    private RadioButton mIsExecutingOnTime;
    private int mDayTimeExecuting;

    private TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mIntervalValue = (EditText) findViewById(R.id.activity_list_interval_value);
        mActionTimeValue = (EditText) findViewById(R.id.activity_list_time_value);

        mIsExecutingOnTime = (RadioButton) findViewById(R.id.activity_list_is_time_execution);
        mIsExecutingOnTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mTimeExecutionValue.getText().toString().equals("")){
                    buttonView.setChecked(false);
                    Toast.makeText(ListActivity.this, "Set your time first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTimeExecutionSetterBtn = (Button) findViewById(R.id.activity_list_time_execution_btn);
        mTimeExecutionSetterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimePickerDialog == null){
                    mTimePickerDialog = new TimePickerDialog(ListActivity.this, ListActivity.this, 0, 0, true);
                }
                mTimePickerDialog.show();
            }
        });

        mTimeExecutionValue = (TextView) findViewById(R.id.activity_list_time_execution_value);

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

        mServiceIntent = new Intent(this, AccelerometerService.class);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AccelerometerService.LocalBinder localBinder = (AccelerometerService.LocalBinder) service;
                mAccelerometerService = localBinder.getService();
                mAccelerometerService.setEmail(mEmail);
                mIsBinded = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIsBinded = false;
            }
        };

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int interval = 0;
                int sessionTime = 0;

                if(!mIntervalValue.getText().toString().equals("")){
                    interval = Integer.parseInt(mIntervalValue.getText().toString());
                }

                if(!mActionTimeValue.getText().toString().equals("")){
                    sessionTime = Integer.parseInt(mActionTimeValue.getText().toString());
                }

                if(mIsExecutingOnTime.isChecked()){
                    mAccelerometerService.setWorkAtTime(true, mDayTimeExecuting);
                }else {
                    mAccelerometerService.setWorkAtTime(false, 0);
                }

                mAccelerometerService.startAccelerometerAction(interval, sessionTime);
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
            public void onCancelled(DatabaseError databaseError) {}
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(Util.isOutOfTime(hourOfDay, minute)) {
            Toast.makeText(this, "Your time is out of time", Toast.LENGTH_SHORT).show();
        }else {
            mDayTimeExecuting = Util.getTimeOfDayInMl(hourOfDay, minute);
            mTimeExecutionValue.setText(String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
        }
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
