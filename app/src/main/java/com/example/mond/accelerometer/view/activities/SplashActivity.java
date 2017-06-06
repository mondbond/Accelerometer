package com.example.mond.accelerometer.view.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private BroadcastReceiver mNetworkConnectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Util.isNetworkAvailable(SplashActivity.this)) {
                verificate();
            }
        }
    };

    private IntentFilter mIntentFilter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        // TODO: 06/06/17 do you need network check here?
        if(Util.isNetworkAvailable(this)) {
            verificate();
        }else {
            Toast.makeText(this, getString(R.string.network_is_not_available), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkConnectionChangeReceiver, mIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mNetworkConnectionChangeReceiver);
    }

    private void verificate() {
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
