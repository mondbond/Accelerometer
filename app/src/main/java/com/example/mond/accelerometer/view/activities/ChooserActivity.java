package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.Util;
import com.example.mond.accelerometer.view.fragments.AccelerometerDialogFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooserActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String UID = "uid";

    @BindView(R.id.chooser_accelerometer_btn) Button mChooseAccelerometer;
    @BindView(R.id.chooser_file_storage_btn) Button mChooseStorage;

    private Bundle mBundle;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        ButterKnife.bind(this);

        final String uid = getIntent().getExtras().getString(UID);

        mBundle = new Bundle();
        mBundle.putString(SessionActivity.UID, uid);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @OnClick(R.id.chooser_accelerometer_btn)
    public void startSessionActivity(){
        Intent sessionListIntent = new Intent(ChooserActivity.this, SessionActivity.class);
        sessionListIntent.putExtras(mBundle);
        startActivity(sessionListIntent);
    }
    
    @OnClick(R.id.chooser_file_storage_btn)
    public void startFileStorageActivity(){
        Intent uploadIntent = new Intent(ChooserActivity.this, UploadActivity.class);
        uploadIntent.putExtras(mBundle);
        startActivity(uploadIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chooser_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
