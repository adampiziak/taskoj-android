package com.adampiziak.bloktree;

public class SubTask {
    private String name = "NO_NAME";
    private boolean status = false; //True: task is done    False: task is not done
    private String key;

    public SubTask() {}

    public SubTask(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getKey() {
        return this.key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
