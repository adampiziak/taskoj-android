package com.adampiziak.bloktree;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Task {

    private String name;
    private int priority = 2;
    private String projectKey;
    private List<SubTask> subTasksList = new ArrayList<>();
    private long repeat = NO_REPEAT;
    private long finishedTime = 0;
    private long duration = 0;
    private long postponedUntil = 0;
    private long dueDate = 0;

    @Exclude
    private String key;

    @Exclude
    public static final long NO_REPEAT = 0;
    public static final long REPEAT_DAILY = 1;


    public Task() {

    }

    public Task(String name, int priority, String projectKey) {
        this.name = name;
        if (priority >= 0 && priority <= 4)
            this.priority = priority;
        else
            this.priority = 2;
        this.projectKey = projectKey;
    }

    public Task(String name, int priority, String projectKey, ArrayList<SubTask> subTasks) {
        this.name = name;
        if (priority >= 0 && priority <= 4)
            this.priority = priority;
        else
            this.priority = 2;
        this.projectKey = projectKey;
        this.subTasksList = subTasks;
    }

    public long getDueDate() {
        return dueDate;
    }
    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getPostponedUntil() { return this.postponedUntil; }
    public void setPostponedUntil(long postponedUntil) { this.postponedUntil = postponedUntil; }

    public long getDuration() { return  this.duration; }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getFinishedTime() {
        return this.finishedTime;
    }
    public void setFinishedTime(long time) {
        this.finishedTime = time;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String newName) {
        this.name = newName;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return this.key;
    }

    public String getProjectKey() {
        return this.projectKey;
    }
    public void setProjectKey(String key) {
        this.projectKey = key;
    }

    public int getPriority() { return this.priority;}
    public void setPriority(int priority) { this.priority = priority; }

    public void addSubTask(SubTask subTask) {
        this.subTasksList.add(subTask);
    }

    public long getRepeat() {
        return this.repeat;
    }
    public void setRepeat(long repeat) {
        this.repeat = repeat;
    }

    public void setSubtasks(List<SubTask> subTasks) {
        this.subTasksList = subTasks;
    }
    public List<SubTask> getSubtasks() {
        return this.subTasksList;
    }

    @Exclude
    public static String getPriorityName(int priority) {
        String[] names = {"crucial", "important", "normal", "secondary", "nonessential"};
        if (priority >= 0 && priority <= 4)
            return names[priority];
        else
            return "normal";
    }

    @Exclude
    public static String getPriorityColor(int priority) {
        String[] names = {"#f44336", "#FF9800", "#4CAF50", "#B39DDB", "#BBDEFB"};
        if (priority >= 0 && priority <= 4)
            return names[priority];
        else
            return "normal";
    }



}
