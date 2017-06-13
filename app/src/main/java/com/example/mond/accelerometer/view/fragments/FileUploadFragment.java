package com.example.mond.accelerometer.view.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.FileUploadItem;
import com.example.mond.accelerometer.util.FirebaseUtil;
import com.example.mond.accelerometer.util.Util;
import com.example.mond.accelerometer.view.adapter.FileUploadAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    private OnFilesCountChangeListener mListener;

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
        mImagesRef = mStorageRef.child("data").child(mUID);


        mAdapter = new FileUploadAdapter(null, this, getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFilesCountChangeListener) {
            mListener = (OnFilesCountChangeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFilesCountChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void onFileSelected(FileUploadItem item) {
        uploadFile(item);
    }

    @Override
    public UploadTask createUploadTask(FileUploadItem item) {
        mCurrentRandom = Util.makeCurrentTimeStampToDate() + Util.getExtension(item.getmUri(), getActivity());
        final StorageReference picRef = mImagesRef.child(mCurrentRandom);
        UploadTask uploadTask = picRef.putFile(item.getmUri());
        return uploadTask;
    }


    private void uploadFile(final FileUploadItem item){
        mCurrentRandom = Util.makeCurrentTimeStampToDate() + Util.getExtension(item.getmUri(), getActivity());
        final StorageReference picRef = mImagesRef.child(mCurrentRandom);

        UploadTask uploadTask = picRef.putFile(item.getmUri());

        item.setmUploadTask(uploadTask);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseUtil.saveImageRef(picRef, mCurrentRandom, mUID);
                mAdapter.removeUploadedFile(item.getmUri());
                mListener.onFilesCountChange(mAdapter.getItemCount());
            }
        });
    }

    public void addFile(Uri uri) {
        mAdapter.setNewFile(new FileUploadItem(uri));
        mListener.onFilesCountChange(mAdapter.getItemCount());
    }

    public interface OnFilesCountChangeListener{
        void onFilesCountChange(int count);
    }
}