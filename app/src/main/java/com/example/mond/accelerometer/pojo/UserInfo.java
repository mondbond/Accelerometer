package com.example.mond.accelerometer.pojo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class UserInfo {

    private List<Session> mSessionList;

    public UserInfo() {
    }

    public UserInfo(List<Session> sessionList) {
        mSessionList = sessionList;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sessionList", mSessionList);

        return result;
    }

    public List<Session> getSessionList() {
        return mSessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        mSessionList = sessionList;
    }
}
