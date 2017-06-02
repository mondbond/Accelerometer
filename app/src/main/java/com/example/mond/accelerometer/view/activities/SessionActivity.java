package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.mond.accelerometer.Constants;
import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.pojo.Session;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionActivity extends AppCompatActivity implements SessionFragment.OnSessionFragmentInteractionListener {

    public static final String UID = "email";
    public static final String ACCELEROMETER_DIALOG_FRAGMENT_TAG = "accelerometerDialogFragmentTag";
    private static final String RESTORE_SESSIONS = "restoreSessions";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String mUID;
    private List<Session> mSessions = new ArrayList<>();
    private AccelerometerDialogFragment mAccelerometerDialogFragment;
    private SessionFragment mSessionFragment;

    @BindView(R.id.fab) FloatingActionButton mFab;

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
        mDbRef = mDatabase.getReference().child(Constants.FIREBASE_SESSIONS_NODE).child(mUID);
        mDbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mSessions != null){
                    mSessions.clear();
                }

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Session session = new Session();

                    if(data.child(Constants.FIREBASE_SESSION_ID).getValue() != null) {
                        session.setSessionId(Long.parseLong(String.valueOf( data.child(Constants.FIREBASE_SESSION_ID).getValue())));
                        session.setSessionInterval(Integer.parseInt(String.valueOf( data.child(Constants.FIREBASE_SESSIONS_INTERVAL_INFO).getValue())));
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
    public void onGetSessionData(Session session) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RESTORE_SESSIONS, (ArrayList<? extends Parcelable>) mSessions);
    }
}
