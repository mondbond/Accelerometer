package com.example.mond.accelerometer.view.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.StorageFile;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.util.Util;
import com.example.mond.accelerometer.view.adapter.FileStorageAdapter;
import com.example.mond.accelerometer.view.fragments.FileUploadFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadActivity extends AppCompatActivity {

    public static final String UID = "uid";
    private static final int SELECT_PHOTO = 12312;


    private FileUploadFragment mFileUploadFragment;

    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageReference mImagesRef;


    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private Bitmap mBitmap;

    @BindView(R.id.upload_activity_recycler) RecyclerView mRecycler;

    private FileStorageAdapter mAdapter;

    private ArrayList<StorageFile> mStorageFiles;

    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);

        mUid = getIntent().getExtras().getString(UID);
        initStorageRefs();

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FileStorageAdapter(null);
        mRecycler.setAdapter(mAdapter);

        FragmentManager fm = getSupportFragmentManager();

        if(fm.findFragmentByTag(FileUploadFragment.FILE_UPLOAD_FRAGMENT_TAG) == null){
            mFileUploadFragment = FileUploadFragment.newInstance(mUid);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.file_upload_fragment_container, mFileUploadFragment, FileUploadFragment.FILE_UPLOAD_FRAGMENT_TAG);
            ft.commit();
        }else {
            mFileUploadFragment = (FileUploadFragment) fm.findFragmentByTag(FileUploadFragment.FILE_UPLOAD_FRAGMENT_TAG);
        }

        initFirebaseDb();
    }

    public void initFirebaseDb(){
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference().child(FirebaseUtil.FIREBASE_FILE_REF_NODE).child(mUid);
        mDbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mStorageFiles != null){
                    mStorageFiles.clear();
                }

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.getValue() != null) {
                        mStorageFiles.add(data.getValue(StorageFile.class));
                    }
                }
                mAdapter.setStorageFiles(mStorageFiles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void initStorageRefs() {
        mStorageRef = mStorage.getReference();
        mImagesRef = mStorageRef.child("pic").child(mUid);
        mStorageFiles = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.upload && Util.isNetworkAvailable(this)){
            choosePhotoFromGallery();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void choosePhotoFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        photoPickerIntent.setType("image/video/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedFile = imageReturnedIntent.getData();
//                    InputStream imageStream = null;
//                    try {
//                        imageStream = getContentResolver().openInputStream(selectedImage);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

//                    mBitmap = BitmapFactory.decodeStream(imageStream);

                    Util.getMimeType(selectedFile, this);

                    mFileUploadFragment.addFile(selectedFile);
                }
        }
    }
}
