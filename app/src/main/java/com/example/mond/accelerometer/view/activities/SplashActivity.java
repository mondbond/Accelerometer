package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.mond.accelerometer.R;
import com.google.firebase.auth.FirebaseAuth;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        verificate();
    }

    private void verificate() {
        if(mAuth.getCurrentUser() == null){
            Intent loginIntent = new Intent(this, AuthenticationActivity.class);
            startActivity(loginIntent);
        }else {
            Intent sessionListIntent = new Intent(this, ChooserActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(ChooserActivity.UID, mAuth.getCurrentUser().getUid());
            sessionListIntent.putExtras(bundle);
            startActivity(sessionListIntent);
        }

        finish();
    }
}
