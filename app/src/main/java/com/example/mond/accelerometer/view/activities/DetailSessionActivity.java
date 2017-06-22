package com.example.mond.accelerometer.view.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.events.AccelerometerDataChangeEvent;
import com.example.mond.accelerometer.model.AccelerometerData;
import com.example.mond.accelerometer.model.Session;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.view.fragments.DetailFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class DetailSessionActivity extends AppCompatActivity {

    public final static String SESSION_DATA = "sessionData";
    public final static String UID = "uid";

    private String mUID;
    private Session mSession;

    DetailFragment mDetailFragment;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private ArrayList<AccelerometerData> mAccelerometerDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_session);

        Bundle bundle = getIntent().getExtras();
        mUID = bundle.getString(UID);
        mSession = bundle.getParcelable(SESSION_DATA);

        FragmentManager fm = getSupportFragmentManager();

        if (fm.findFragmentByTag(DetailFragment.DETAIL_FRAGMENT_TAG) == null) {
            mDetailFragment = DetailFragment.newInstance(mUID, mSession);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_detail_fragment_container, mDetailFragment, DetailFragment.DETAIL_FRAGMENT_TAG);
            ft.commit();
        } else {
            mDetailFragment = (DetailFragment) fm.findFragmentByTag(DetailFragment.DETAIL_FRAGMENT_TAG);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initFirebaseDb();
    }

    private void initFirebaseDb() {
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(FirebaseUtil.FIREBASE_ACCELEROMETER_DATA_NODE).child(mUID)
                .child(String.valueOf(mSession.getSessionId()));
        mDbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    AccelerometerData accelerometerData = data.getValue(AccelerometerData.class);
                    mAccelerometerDataList.add(accelerometerData);
                }
                EventBus.getDefault().post(new AccelerometerDataChangeEvent(mAccelerometerDataList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
