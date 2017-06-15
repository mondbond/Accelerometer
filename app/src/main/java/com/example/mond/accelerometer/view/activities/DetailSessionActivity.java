package com.example.mond.accelerometer.view.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.Session;
import com.example.mond.accelerometer.view.fragments.DetailFragment;

public class DetailSessionActivity extends AppCompatActivity {

    public final static String SESSION_DATA = "sessionData";
    public final static String UID = "uid";

    private String mUID;
    private Session mSession;

    DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_session);

        Bundle bundle = getIntent().getExtras();
        mUID = bundle.getString(UID);
        mSession = bundle.getParcelable(SESSION_DATA);

        FragmentManager fm = getSupportFragmentManager();

        if(fm.findFragmentByTag(DetailFragment.DETAIL_FRAGMENT_TAG) == null){
            mDetailFragment = DetailFragment.newInstance(mUID, mSession);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.detail_fragment_container, mDetailFragment, DetailFragment.DETAIL_FRAGMENT_TAG);
            ft.commit();
        }else {
            mDetailFragment = (DetailFragment) fm.findFragmentByTag(DetailFragment.DETAIL_FRAGMENT_TAG);
        }
    }
}
