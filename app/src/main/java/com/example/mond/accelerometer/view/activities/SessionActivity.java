package com.example.mond.accelerometer.view.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class SessionActivity extends AppCompatActivity implements SessionFragment.OnSessionFragmentInteractionListener,
        AccelerometerDialogFragment.AccelerometerDialogInteractionListener, GoogleApiClient.OnConnectionFailedListener{

    public static final String UID = "uid";
    public static final String ACCELEROMETER_DIALOG_FRAGMENT_TAG = "accelerometerDialogFragmentTag";
    private static final String RESTORE_SESSIONS = "restoreSessions";
    private static final String RESTORE_IS_RUNNING = "restoreIsRunning";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String mUID;
    private ArrayList<Session> mSessions = new ArrayList<>();
    private AccelerometerDialogFragment mAccelerometerDialogFragment;
    private SessionFragment mSessionFragment;

    private GoogleApiClient mGoogleApiClient;

    private AccelerometerService mService;
    boolean mBound = false;

    private MenuItem mTurnOn;
    private MenuItem mTurnOff;
    private boolean mIsRunning;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        initFirebaseDb();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, AccelerometerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSessions = savedInstanceState.getParcelableArrayList(RESTORE_SESSIONS);
        mIsRunning = savedInstanceState.getBoolean(RESTORE_IS_RUNNING);
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
        switch (item.getItemId()){
            case R.id.log_out:
                if(Util.isNetworkAvailable(this)){
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                    FirebaseAuth.getInstance().signOut();
                    // TODO: ? 13/06/17 google+ logout wanted
                    Intent logOutIntent = new Intent(this, AuthenticationActivity.class);
                    logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(logOutIntent);

                    finish();
                }
                break;
            case R.id.turn_on_accelerometer:
                if(mAccelerometerDialogFragment == null){
                    mAccelerometerDialogFragment = AccelerometerDialogFragment.newInstance(mUID);
                }
                mAccelerometerDialogFragment.show(getSupportFragmentManager(), ACCELEROMETER_DIALOG_FRAGMENT_TAG);
                break;
            case R.id.turn_off_accelerometer:
                mIsRunning = false;
                mTurnOff.setVisible(false);
                mTurnOn.setVisible(true);

                mService.stopAccelerometer();
                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_activity_menu, menu);
        mTurnOn = menu.findItem(R.id.turn_on_accelerometer);
        mTurnOff = menu.findItem(R.id.turn_off_accelerometer);

        if(mIsRunning){
            mTurnOn.setVisible(false);
            mTurnOff.setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onAccelerometerStart() {
        Intent serviceIntent = new Intent(this, AccelerometerService.class);
        serviceIntent.setAction(AccelerometerService.ACCELEROMETER_SERVICE_START_ACTION);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);

        mTurnOn.setVisible(false);
        mTurnOff.setVisible(true);
        mIsRunning = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESTORE_SESSIONS, mSessions);
        outState.putBoolean(RESTORE_IS_RUNNING, mIsRunning);
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            AccelerometerService.LocalBinder binder = (AccelerometerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
