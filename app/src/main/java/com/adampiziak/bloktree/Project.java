package com.adampiziak.bloktree;

import com.google.firebase.database.Exclude;

public class Project {
    @Exclude
    public final String NO_KEY = "NO_PROJECT_KEY";

    private String name = "General";
    private String color = "#2196F3";
    private String key = NO_KEY;

    public Project() {}

    public Project(String name, String color) {
        this.name  = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }
    public String getColor() {
        return this.color;
    }
    public void setKey (String key) {
        this.key = key;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setColor(String color) {
        this.color = color;
    }

    @Exclude
    public String getKey() {
        return this.key;
    }
    @Exclude
    public boolean hasKey() {
        if (!key.equals(NO_KEY))
            return true;
        else
            return false;

    }
}
