package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        verificate();
    }

    private void verificate(){
        if(mAuth.getCurrentUser() == null){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }else {
            Intent sessionListIntent = new Intent(this, SessionActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(SessionActivity.UID, mAuth.getCurrentUser().getUid());
            sessionListIntent.putExtras(bundle);
            startActivity(sessionListIntent);
        }

        finish();
    }
}
