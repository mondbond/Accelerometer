package com.example.mond.accelerometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mond.accelerometer.pojo.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListActivity extends AppCompatActivity {

    private final String TAG = "LIST_ACTIVITY";

    public static final String EMAIL_EXTRA = "email";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private Button mStartButton;
    private Button mStopButton;

    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Bundle bundle = getIntent().getExtras();
        mEmail = bundle.getString(EMAIL_EXTRA);

        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(mEmail);

        mStartButton = (Button) findViewById(R.id.activity_list_start_btn);
        mStopButton = (Button) findViewById(R.id.activity_list_stop_btn);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                to service
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                to service
            }
        });

        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo value = dataSnapshot.getValue(UserInfo.class);
                if(value != null){
                    Log.d(TAG, "not null  = " + value.toString());
                }else {
                    Log.d(TAG, " null ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "cenceled is" + databaseError.getMessage());
            }
        });
    }
}
