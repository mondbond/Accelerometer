package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mond.accelerometer.Constants;
import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.Session;
import com.example.mond.accelerometer.service.AccelerometerService;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.util.Util;
import com.example.mond.accelerometer.view.fragments.AccelerometerDialogFragment;
import com.example.mond.accelerometer.view.fragments.SessionFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SessionActivity extends AppCompatActivity implements SessionFragment.OnSessionFragmentInteractionListener {

    public static final String UID = "email";
    public static final String ACCELEROMETER_DIALOG_FRAGMENT_TAG = "accelerometerDialogFragmentTag";
    private static final String RESTORE_SESSIONS = "restoreSessions";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String mUID;
    private ArrayList<Session> mSessions = new ArrayList<>();
    private AccelerometerDialogFragment mAccelerometerDialogFragment;
    private SessionFragment mSessionFragment;

//    @BindView(R.id.fab) FloatingActionButton mFab;
    MenuItem mTurnOn;
    MenuItem mTurnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);

        FragmentManager fm = getSupportFragmentManager();

        if(fm.findFragmentByTag(SessionFragment.SESSION_FRAGMENT_TAG) == null){
            mSessionFragment = SessionFragment.newInstance();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.sessionListContainer, mSessionFragment, SessionFragment.SESSION_FRAGMENT_TAG);
            ft.commit();
        }else {
            mSessionFragment = (SessionFragment) fm.findFragmentByTag(SessionFragment.SESSION_FRAGMENT_TAG);
        }

        Bundle bundle = getIntent().getExtras();
        mUID = bundle.getString(UID);

        initFirebaseDb();
    }

//    @OnClick(R.id.fab)
//    public void showAccelerometerDialog() {
//    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSessions = savedInstanceState.getParcelableArrayList(RESTORE_SESSIONS);
        mSessionFragment.setNewAccelerometerValues(mSessions);
    }

    public void initFirebaseDb(){
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(FirebaseUtil.FIREBASE_SESSIONS_NODE).child(mUID);
        mDbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mSessions != null){
                    mSessions.clear();
                }

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.child(FirebaseUtil.FIREBASE_SESSION_ID).getValue() != null) {
                        mSessions.add(data.getValue(Session.class));
                    }
                }
                mSessionFragment.setNewAccelerometerValues(mSessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onSessionItemSelected(Session session) {
        Intent detailSessionIntent = new Intent(this, DetailSessionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailSessionActivity.SESSION_DATA, session);
        bundle.putString(DetailSessionActivity.UID, mUID);
        detailSessionIntent.putExtras(bundle);

        startActivity(detailSessionIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.log_out && Util.isNetworkAvailable(this)){
            FirebaseAuth.getInstance().signOut();
            Intent logOutIntent = new Intent(this, LoginActivity.class);
            logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logOutIntent);

            finish();
        } else if(item.getItemId() == R.id.turn_on_accelerometer) {
            item.setVisible(false);
            mTurnOff.setVisible(true);
            if(mAccelerometerDialogFragment == null){
                mAccelerometerDialogFragment = AccelerometerDialogFragment.newInstance(mUID);
            }
            mAccelerometerDialogFragment.show(getSupportFragmentManager(), ACCELEROMETER_DIALOG_FRAGMENT_TAG);
        }else if(item.getItemId() == R.id.turn_off_accelerometer) {
            item.setVisible(false);
            mTurnOn.setVisible(true);
            Intent intent = new Intent(AccelerometerService.ACCELEROMETER_SERVICE_STOP_ACTION);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_activity_menu, menu);
        mTurnOn = menu.findItem(R.id.turn_on_accelerometer);
        mTurnOff = menu.findItem(R.id.turn_off_accelerometer);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESTORE_SESSIONS, mSessions);
    }
}
