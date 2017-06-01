package com.example.mond.accelerometer.view.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.view.fragments.AccelerometerDataListFragment;
import com.example.mond.accelerometer.view.fragments.LineGraphFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailSessionActivity extends AppCompatActivity {

    public final static String SESSION_DATA = "sessionData";
    public final static String UID = "email";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String mUID;
    private Session mSession;

    private LineGraphFragment mGraphFragment;
    private AccelerometerDataListFragment mAccelerometerDataListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_session);

        Bundle bundle = getIntent().getExtras();
        mUID = bundle.getString(UID);
        mSession = bundle.getParcelable(SESSION_DATA);

        if(mGraphFragment == null|| mAccelerometerDataListFragment == null){
            mGraphFragment = LineGraphFragment.newInstance(mSession);
            mAccelerometerDataListFragment = AccelerometerDataListFragment.newInstance(mSession);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.graph_fragment_container, mGraphFragment);
        ft.replace(R.id.accelerometer_data_fragment_container, mAccelerometerDataListFragment);
        ft.commit();

        initFirebaseDb();
    }

    public void initFirebaseDb(){
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(mUID).child(mSession.getTime());

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mSession = new Session();
                mSession.setTime(dataSnapshot.getKey());
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    AccelerometerData accelerometerData = data.getValue(AccelerometerData.class);
                    mSession.addData(accelerometerData);
                }

                mAccelerometerDataListFragment.setNewSessionValue(mSession);
                mGraphFragment.setNewSessionValue(mSession);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
