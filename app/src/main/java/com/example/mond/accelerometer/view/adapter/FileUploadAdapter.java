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
import com.example.mond.accelerometer.util.Util;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FileUploadAdapter extends RecyclerView.Adapter<FileUploadAdapter.ViewHolder> {

    private ArrayList<Uri> mFileList;
    private OnPhotoSelected mListener;
    private Context mContext;

    public FileUploadAdapter(ArrayList<Uri> fileList, OnPhotoSelected listener, Context context) {
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

    public void setFile(ArrayList<Uri> files){
        mFileList = files;
        notifyDataSetChanged();
    }

    public void setNewFile(Uri uri){
        if(mFileList == null){
            mFileList = new ArrayList<>();
        }
        mFileList.add(uri);
        notifyDataSetChanged();
    }

    public void removeUploadedFile(Uri uri) {
        for (Uri item : mFileList){
            if(item.equals(uri)){
                mFileList.remove(item);
                break;
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Uri mUri;

        @BindView(R.id.file_upload_button) Button uploadButton;
        @BindView(R.id.upload_image) ImageView photo;
        @BindView(R.id.circle_progress) CircleProgress progress;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.file_upload_button)
        void notificateListener(){
            mListener.onFileSelected(mUri, progress);
            progress.setVisibility(View.VISIBLE);
        }

        public void bind(Uri file) {
            if(mUri != file){
                progress.setVisibility(View.INVISIBLE);
            }

            mUri = file;

            if(Util.getMimeContentType(file, mContext).equals("image")){
                Picasso.with(mContext).load(file).resize(150, 150).into(photo);
            }else if(Util.getMimeContentType(file, mContext).equals("video")){
                photo.setImageBitmap(ThumbnailUtils.createVideoThumbnail(Util.getPath(file, mContext),
                        MediaStore.Video.Thumbnails.MINI_KIND));
            }
        }
    }

    public interface OnPhotoSelected {
        void onFileSelected(Uri uri, CircleProgress progress);
    }
}
