package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// TODO: 06/06/17  REPEATED Login & Create Account are different thing, don't try to combine them
// TODO: 06/06/17 add google+ sing in
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @BindView(R.id.field_email) TextView mUserEmail;
    @BindView(R.id.field_password) TextView mUserPassword;
    @BindView(R.id.email_create_account_button) Button mCreateAccountUserBtn;
    @BindView(R.id.email_sign_in_button) Button mSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.email_create_account_button)
    public void onCreateAccountClicked(View v) {
        if(Util.isNetworkAvailable(LoginActivity.this)) {
            createAccount(mUserEmail.getText().toString(), mUserPassword.getText().toString());
        }
    }

    @OnClick(R.id.email_sign_in_button)
    public void onSignInClicked(View v) {
        if(Util.isNetworkAvailable(LoginActivity.this)) {
            signIn(mUserEmail.getText().toString(), mUserPassword.getText().toString());
        }
    }

    public void createAccount(final String email, final String pswd){
        if(isFieldsNotNullAndEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_create_account_filed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        signIn(email, pswd);
                    }
                    }
                });
        }else {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_empty_fields),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void signIn(final String email, String pswd) {
        if (isFieldsNotNullAndEmpty()) {
            mAuth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_log_in_filed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.message_logged),
                                Toast.LENGTH_SHORT).show();
                        startSessionActivity();
                    }
                    }
                });
        }else {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_empty_fields),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void startSessionActivity(){
        Intent intent = new Intent(LoginActivity.this, SessionActivity.class);
        intent.putExtra(SessionActivity.UID, mAuth.getCurrentUser().getUid());
        startActivity(intent);

        finish();
    }

    public boolean isFieldsNotNullAndEmpty(){
        if (!TextUtils.equals(mUserEmail.getText().toString(), null)
                && !TextUtils.equals(mUserPassword.getText().toString(), null)
                &&!TextUtils.isEmpty(mUserEmail.getText().toString())
                && !TextUtils.isEmpty(mUserPassword.getText().toString())) {
            return true;
        }else{
            return false;
        }
    }
}
