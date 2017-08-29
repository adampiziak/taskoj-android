package com.adampiziak.bloktree;

import android.graphics.RectF;

import com.google.firebase.database.Exclude;

public class Event {
    private String name = "";
    private String projectKey;
    private long year = 2020;
    private long month = 0;
    private long day = 0;
    private long hour = 0;
    private long minute = 0;
    private long duration = 60;
    private long renewType = -1;



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public long getYear() {
        return year;
    }
    public void setYear(long year) {
        this.year = year;
    }

    public long getMonth() {
        return month;
    }
    public void setMonth(long month) {
        this.month = month;
    }

    public long getDay() {
        return day;
    }
    public void setDay(long day) {
        this.day = day;
    }

    public long getHour() {
        return hour;
    }
    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getMinute() {
        return minute;
    }
    public void setMinute(long minute) {
        this.minute = minute;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
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


    @Exclude
    public RectF container = new RectF();











}
