package com.adampiziak.bloktree;

import android.graphics.RectF;

import com.google.firebase.database.Exclude;

public class Event {
    private String name = "";
    private String projectKey;
    private long timeStart = 0;
    private long timeEnd = 0;
    private long renewType = -1;
    private String renewDays = "0000000";

    @Exclude
    String key = "";



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getProjectKey() {
        return projectKey;
    }
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public long getRenewType() {
        return renewType;
    }
    public void setRenewType(long renewType) {
        this.renewType = renewType;
    }

    public String getRenewDays() {
        return renewDays;
    }

    public void setRenewDays(String renewDays) {
        this.renewDays = renewDays;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    @Exclude
    public RectF container = new RectF();











}
