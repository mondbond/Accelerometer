package com.example.mond.accelerometer.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mond.accelerometer.R;
import com.example.mond.accelerometer.model.StorageFile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FileStorageAdapter extends RecyclerView.Adapter<FileStorageAdapter.ViewHolder> {

    private ArrayList<StorageFile> mStorageFiles;
    private FileUploadAdapter.OnPhotoSelected mListener;

    public FileStorageAdapter(ArrayList<StorageFile> storageFiles) {
        mStorageFiles = storageFiles;
    }

    @Override
    public FileStorageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.storage_file_item, parent, false);

        return new FileStorageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FileStorageAdapter.ViewHolder holder, int position) {
        holder.bind(mStorageFiles.get(position));
    }

    @Override
    public int getItemCount() {
        if (mStorageFiles != null) {
            return mStorageFiles.size();
        } else {
            return 0;
        }
    }

    public void setStorageFiles(ArrayList<StorageFile> storageFiles) {
        if (mStorageFiles == null) {
            mStorageFiles = new ArrayList<>();
        }
        mStorageFiles = storageFiles;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.storage_file_name) TextView name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(StorageFile storageFile) {
            name.setText(storageFile.getName());
        }
    }
}
