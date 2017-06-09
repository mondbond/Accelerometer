package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mond.accelerometer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooserActivity extends AppCompatActivity {

    public static final String UID = "uid";

    @BindView(R.id.chooser_accelerometer_btn) Button mChooseAccelerometer;
    @BindView(R.id.chooser_file_storage_btn) Button mChooseStorage;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        ButterKnife.bind(this);

        final String uid = getIntent().getExtras().getString(UID);

        mBundle = new Bundle();
        mBundle.putString(SessionActivity.UID, uid);
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
}
