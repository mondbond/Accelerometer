package com.example.mond.accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final String TAG = "MAIN_ACTIVITY";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private TextView mUserEmail;
    private TextView mUserPassword;

    private Button mCreateAccountUserBtn;
    private Button mSignInBtn;

    private SensorManager mSensorManager;
    private double ax, ay, az;

    private AccelerometerData mAccelerometerData;

    private String mEmail;
    private String mSession;

    private boolean mIsAuth;

    private long mLastTimeSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSession = Util.currenTimeStampToDate(null);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference("/");
//
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value != null){
                    Log.d(TAG, "not null  = " + value.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "cenceled is" + databaseError.getMessage());
            }
        });

        mUserEmail = (TextView) findViewById(R.id.field_email);
        mUserPassword = (TextView) findViewById(R.id.field_password);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mCreateAccountUserBtn = (Button) findViewById(R.id.email_create_account_button);
        mCreateAccountUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mUserEmail.getText().toString(), mUserPassword.getText().toString());
            }
        });

        mSignInBtn = (Button) findViewById(R.id.email_sign_in_button);
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mUserEmail.getText().toString(), mUserPassword.getText().toString());

//                 Read from the database
            mDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
//                    AccelerometerData value = dataSnapshot.getValue(AccelerometerData.class);
//                    Log.d(TAG, "Value is: " + value.toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void createAccount(String email, String pswd){
//        TODO: make validate

        mAuth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "good",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String pswd){
//        TODO: make validate

        mAuth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "res is = " + task.getResult() + "/ /" + task.getException().toString());
                        }else {

                            Log.d(TAG, task.getResult().getUser().getEmail());
//                            mEmail = "better";
                            mEmail = Util.clearDots(task.getResult().getUser().getEmail());

                            mIsAuth = true;

                            Toast.makeText(MainActivity.this, "good",
                                    Toast.LENGTH_SHORT).show();

                            AccelerometerData data = new AccelerometerData(4, 4, 4);
                            Map<String, Object> map = data.toMap();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && mIsAuth){
         ax = event.values[0];
         ay = event.values[1];
         az = event.values[2];

            saveDataToFirebase(ax, ay, az);
        }
    }

    private void saveDataToFirebase(double x, double y, double z){
        if((System.currentTimeMillis() - mLastTimeSave) >= 1000) {
            mAccelerometerData = new AccelerometerData(x, y, z);
            Map<String, Object> map = mAccelerometerData.toMap();
            mDbRef.child(mEmail).child(mSession).child(Util.currenTimeStampToDate(null)).setValue(map);
            mLastTimeSave = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
