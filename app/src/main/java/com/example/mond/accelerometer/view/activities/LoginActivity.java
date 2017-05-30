package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "MAIN_ACTIVITY";

    private FirebaseAuth mAuth;

    private TextView mUserEmail;
    private TextView mUserPassword;

    private Button mCreateAccountUserBtn;
    private Button mSignInBtn;

    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        // TODO: 30/05/17 user butterknife library
        mUserEmail = (TextView) findViewById(R.id.field_email);
        mUserPassword = (TextView) findViewById(R.id.field_password);


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
            }
        });
        // TODO: 30/05/17  mAuthListener is not connected to lifecycle
        // TODO: 30/05/17 implement Splash Screen logic: SplashActivity launcher with auth state check, then start Login or Main Screens
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
        // TODO: 30/05/17 use TextUtils class to handle null pointer exception
//        TextUtils.isEmpty(email);
//        TextUtils.equals("str1", null);
        if(!email.equals("") && !pswd.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, pswd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // TODO: 30/05/17 TEXT, VISIBLE TO USER SHOULD BE IN STRINGS
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "failed",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "good",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(LoginActivity.this, "Fields shouldn't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(final String email, String pswd) {
        // TODO: 30/05/17 check createAccount
            if (!email.equals("") && !pswd.equals("")) {
                mAuth.signInWithEmailAndPassword(email, pswd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                // TODO: 30/05/17 DRY!!!
                                Toast.makeText(LoginActivity.this, "failed",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "good",
                                        Toast.LENGTH_SHORT).show();
                                // TODO: 30/05/17 better to use user UID as key.
                                // TODO: 30/05/17 starter pattern
                                Intent intent = new Intent(LoginActivity.this, ListActivity.class);
                                intent.putExtra(ListActivity.EMAIL_EXTRA, Util.clearDots(email));
                                startActivity(intent);
                            }
                        }
                    });
        }else {
            Toast.makeText(LoginActivity.this, "Fields shouldn't be empty", Toast.LENGTH_SHORT).show();
        }
    }
}
