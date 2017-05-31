package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;
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
            Log.d("VERIFICATION", " is = " + mAuth.getCurrentUser().getEmail());
            Intent sessionListIntent = new Intent(this, ListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ListActivity.UID, mAuth.getCurrentUser().getUid());
            sessionListIntent.putExtras(bundle);
            startActivity(sessionListIntent);
        }

        finish();
    }
}
