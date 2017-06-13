package com.example.mond.accelerometer.view.adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.util.FileUploadItem;
import com.example.mond.accelerometer.util.Util;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileUploadAdapter extends RecyclerView.Adapter<FileUploadAdapter.ViewHolder> {

    private ArrayList<FileUploadItem> mFileList;
    private OnPhotoSelected mListener;
    private Context mContext;

    public FileUploadAdapter(ArrayList<FileUploadItem> fileList, OnPhotoSelected listener, Context context) {
        mFileList = fileList;
        mListener = listener;
        mContext = context;
    }

    @Override
    public FileUploadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_upload_item, parent, false);

        return new FileUploadAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileUploadAdapter.ViewHolder holder, int position) {
        holder.bind(mFileList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mFileList != null) {
            return mFileList.size();
        } else {
            return 0;
        }
    }

    public void setFile(ArrayList<FileUploadItem> files){
        mFileList = files;
        notifyDataSetChanged();
    }

    public void setNewFile(FileUploadItem uri){
        if(mFileList == null){
            mFileList = new ArrayList<>();
        }
        mFileList.add(uri);
        notifyDataSetChanged();
    }

    public void removeUploadedFile(Uri uri) {
        for (FileUploadItem item : mFileList){
            if(item.getmUri().equals(uri)){
                mFileList.remove(item);
                break;
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //        private Uri mUri;
        private FileUploadItem mUri;

        @BindView(R.id.file_upload_button) Button uploadButton;
        @BindView(R.id.upload_image) ImageView photo;
        @BindView(R.id.circle_progress) CircleProgress circleProgress;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.file_upload_button)
        void notificateListener(){
            mUri.setmUploadTask(mListener.createUploadTask(mUri));
            mUri.getmUploadTask().addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred())
                            / taskSnapshot.getTotalByteCount();
                    circleProgress.setProgress((int) progress);
                }
            });

            mListener.onFileSelected(mUri);
            circleProgress.setVisibility(View.VISIBLE);
        }

        public void bind(FileUploadItem file) {

            if(mUri.getmUploadTask() == null){
                circleProgress.setVisibility(View.INVISIBLE);
            }

            mUri = file;

            if(mUri.getmUploadTask() != null){

                circleProgress.setVisibility(View.VISIBLE);


                mUri.getmUploadTask().addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred())
                                / taskSnapshot.getTotalByteCount();
                        circleProgress.setProgress((int) progress);
                    }
                });
            }

            if(Util.getMimeContentType(file.getmUri(), mContext).equals("image")) {
                Picasso.with(mContext).load(file.getmUri()).resize(150, 150).into(photo);
            }else if(Util.getMimeContentType(file.getmUri(), mContext).equals("video")){
                photo.setImageBitmap(ThumbnailUtils.createVideoThumbnail(Util.getPath(file.getmUri(), mContext),
                        MediaStore.Video.Thumbnails.MINI_KIND));
            }
        }
    }

    public interface OnPhotoSelected {
        void onFileSelected(FileUploadItem item);
        UploadTask createUploadTask(FileUploadItem item);
    }
}