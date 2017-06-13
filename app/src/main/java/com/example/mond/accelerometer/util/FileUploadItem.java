package com.example.mond.accelerometer.util;

import android.net.Uri;
import com.google.firebase.storage.UploadTask;

public class FileUploadItem {

    private Uri mUri;
    private UploadTask mUploadTask;

    public FileUploadItem(Uri mUri) {
        this.mUri = mUri;
    }

    public Uri getmUri() {
        return mUri;
    }

    public void setmUri(Uri mUri) {
        this.mUri = mUri;
    }

    public UploadTask getmUploadTask() {
        return mUploadTask;
    }

    public void setmUploadTask(UploadTask mUploadTask) {
        this.mUploadTask = mUploadTask;
    }
}
