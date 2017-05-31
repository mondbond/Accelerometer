package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.AccelerometerData;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.view.fragments.AccelerometerDialogFragment;
import com.example.mond.accelerometer.view.fragments.SessionFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements SessionFragment.OnFragmentInteractionListener {

    public static final String UID = "email";
    public static final String ACCELEROMETER_DIALOG_FRAGMENT_TAG = "accelerometerDialogFragmentTag";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String mUID;
    private List<Session> mSessions = new ArrayList<>();
    private AccelerometerDialogFragment mAccelerometerDialogFragment;
    private SessionFragment mSessionFragment;

    private FirebaseAuth mAuth;

    @BindView(R.id.fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 30/05/17 single method should fit screen height!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);


        mAuth = FirebaseAuth.getInstance();

        if(mSessionFragment == null){
            mSessionFragment = SessionFragment.newInstance();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.sessionListContainer, mSessionFragment);
        ft.commit();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAccelerometerDialogFragment == null){
                    mAccelerometerDialogFragment = AccelerometerDialogFragment.newInstance(mUID);
                }

                mAccelerometerDialogFragment.show(getSupportFragmentManager(), ACCELEROMETER_DIALOG_FRAGMENT_TAG);
            }
        });

        Bundle bundle = getIntent().getExtras();
        mUID = bundle.getString(UID);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(mUID);

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

                mSessionFragment.setNewAccelerometerValues(mSessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void setSessionAcccelerometerData(Session accelerometerDatas) {

        Intent detailSessionIntent = new Intent(this, DetailSessionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailSessionActivity.SESSION_DATA, accelerometerDatas);
        bundle.putString(DetailSessionActivity.UID, mUID);
        detailSessionIntent.putExtras(bundle);

        startActivity(detailSessionIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.log_out){

            FirebaseAuth.getInstance().signOut();

            Intent logOutIntent = new Intent(this, LoginActivity.class);
            logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logOutIntent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
