package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.Util;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @BindView(R.id.try_again) Button mTryAgainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        if(Util.isNetworkAvailable(this)) {
            verificate();
        }else {
            mTryAgainBtn.setVisibility(View.VISIBLE);
        }

        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.isNetworkAvailable(SplashActivity.this)) {
                    verificate();
                }
            }
        });
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
