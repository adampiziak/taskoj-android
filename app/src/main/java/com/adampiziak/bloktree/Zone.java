package com.adampiziak.bloktree;

public class Zone {

    private String name = "ZONE_PLACEHOLDER_NAME";
    private String color = "#000000";
    private long timeStart = 0;
    private long timeEnd = 0;
    private int renewType = -1;

    public Zone() {}

    public Zone(String name, String color, long start, long end, int renewType) {
        this.name = name;
        this.color = color;
        this.timeStart = start;
        this.timeEnd = end;
        this.renewType = renewType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
    public int getRenewType() {
        return renewType;
    }

    public void setRenewType(int renewType) {
        this.renewType = renewType;
    }

}
