package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.view.adapter.FileUploadAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileUploadFragment extends Fragment implements FileUploadAdapter.OnPhotoSelected{

    public static final String FILE_UPLOAD_FRAGMENT_TAG = "fileUploadFragment";
    public static final String UID = "uid";

    private String mUID;

    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageReference mImagesRef;
    private String mCurrentRandom;

    private FileUploadAdapter mAdapter;

    @BindView(R.id.upload_file_recycler) RecyclerView mRecycler;

    public static FileUploadFragment newInstance(String uID) {

        FileUploadFragment fragment = new FileUploadFragment();
        Bundle args = new Bundle();
        args.putString(UID, uID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUID = getArguments().getString(UID);
        }

        mStorageRef = mStorage.getReference();
        mImagesRef = mStorageRef.child("pic").child(mUID);

        mAdapter = new FileUploadAdapter(null, this, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_upload, container, false);
        setRetainInstance(true);
        ButterKnife.bind(this, v);

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        mRecycler.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onPhotoSelected(Uri uri) {
        Log.d("TO UPLOAD", "-");
        uploadImage(uri);
    }

    private void uploadImage(final Uri uri){
        mCurrentRandom = UUID.randomUUID() + ".jpg";
        final StorageReference picRef = mImagesRef.child(mCurrentRandom);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        UploadTask uploadTask = picRef.putFile(uri);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") long b =  taskSnapshot.getBytesTransferred();
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {}
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseUtil.saveImageRef(picRef, mCurrentRandom, mUID);
                mAdapter.removeUploadedFile(uri);
            }
        });
    }

    public void addFile(Uri file) {
        mAdapter.setNewFile(file);
    }
}
