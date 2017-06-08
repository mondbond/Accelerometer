package com.example.mond.accelerometer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class StorageFile implements Parcelable {

    String mName;
    String mDownloadUrl;

    public StorageFile() {}

    public StorageFile(String name, String downloadUrl) {
        this.mName = name;
        this.mDownloadUrl = downloadUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mName", mName);
        result.put("mDownloadUrl", mDownloadUrl);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mDownloadUrl);
    }

    protected StorageFile(Parcel in) {
        this.mName = in.readString();
        this.mDownloadUrl = in.readString();
    }

    public static final Parcelable.Creator<StorageFile> CREATOR = new Parcelable.Creator<StorageFile>() {
        @Override
        public StorageFile createFromParcel(Parcel source) {
            return new StorageFile(source);
        }

        @Override
        public StorageFile[] newArray(int size) {
            return new StorageFile[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        mDownloadUrl = downloadUrl;
    }
}
