package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.Session;
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

import butterknife.ButterKnife;

public class SessionActivity extends AppCompatActivity implements SessionFragment.OnSessionFragmentInteractionListener,
        AccelerometerDialogFragment.AccelerometerDialogInteractionListener{

    public static final String UID = "uid";
    public static final String ACCELEROMETER_DIALOG_FRAGMENT_TAG = "accelerometerDialogFragmentTag";
    private static final String RESTORE_SESSIONS = "restoreSessions";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String mUID;
    private ArrayList<Session> mSessions = new ArrayList<>();
    private AccelerometerDialogFragment mAccelerometerDialogFragment;
    private SessionFragment mSessionFragment;

    private MenuItem mTurnOn;
    private MenuItem mTurnOff;

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
        // TODO: 13/06/17 switch item.getItemId()?
        if(item.getItemId() == R.id.log_out && Util.isNetworkAvailable(this)){
            FirebaseAuth.getInstance().signOut();
            // TODO: 13/06/17 google+ logout wanted
            Intent logOutIntent = new Intent(this, AuthenticationActivity.class);
            logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logOutIntent);

            finish();
        } else if(item.getItemId() == R.id.turn_on_accelerometer) {
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
    public void onAccelerometerStart() {
        mTurnOn.setVisible(false);
        mTurnOff.setVisible(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESTORE_SESSIONS, mSessions);
    }
}
