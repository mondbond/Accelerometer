package com.example.mond.accelerometer.util;

import android.net.Uri;
import com.google.firebase.storage.UploadTask;

public class FileUploadItem {

    private Uri mUri;
    private UploadTask mUploadTask;

    public FileUploadItem(Uri mUri) {
        this.mUri = mUri;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    public UploadTask getUploadTask() {
        return mUploadTask;
    }

    public void setUploadTask(UploadTask mUploadTask) {
        this.mUploadTask = mUploadTask;
    }
}
