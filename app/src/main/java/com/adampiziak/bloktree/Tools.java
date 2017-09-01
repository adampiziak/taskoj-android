package com.adampiziak.bloktree;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Tools {
    public static Task createTaskFromSnapshot(DataSnapshot snap) {
        String name = (String) snap.child("name").getValue();
        Object postponeUntil = snap.child("postponedUntil").getValue();
        Task task = new Task();
        task.setName((name != null) ? name : "");
        task.setPriority(Integer.parseInt(snap.child("priority").getValue().toString()));
        task.setKey(snap.getKey());
        task.setProjectKey(snap.child("projectKey").getValue().toString());
        task.setRepeat((snap.child("repeat").getValue() != null) ? (long) snap.child("repeat").getValue() : 0);
        if (snap.child("finishedTime").getValue() != null)
            task.setFinishedTime((long) snap.child("finishedTime").getValue());
        task.setDuration((long) snap.child("duration").getValue());
        task.setPostponedUntil((postponeUntil != null) ? (long) postponeUntil : 0);
        task.setDueDate((snap.child("dueDate").getValue() != null) ? (long) snap.child("dueDate").getValue() : 0);
        if (snap.child("subTasks") != null) {
            List<SubTask> subTasks = new ArrayList<>();
            for (DataSnapshot child : snap.child("subTasks").getChildren()) {
                subTasks.add(child.getValue(SubTask.class));
                subTasks.get(subTasks.size() - 1).setKey(child.getKey());
            }
            task.setSubtasks(subTasks);
        }
        return task;
    }

    public static Project createProjectFromSnapshot(DataSnapshot snap) {
        Project project = new Project();
        project.setKey(snap.getKey());
        project.setName(snap.child("name").getValue().toString());
        project.setColor(snap.child("color").getValue().toString());
        return project;
    }

    public static Event createEventFromSnapshot(DataSnapshot snap) {
        Event event = new Event();
        event.setKey(snap.getKey());
        event.setName(snap.child("name").getValue().toString());
        event.setTimeStart((long) snap.child("timeStart").getValue());
        event.setTimeEnd((long) snap.child("timeEnd").getValue());
        event.setProjectKey(snap.child("projectKey").getValue().toString());
        event.setRenewType((snap.child("renewType").getValue() != null) ? (long) snap.child("renewType").getValue() : -1);
        event.setRenewDays(snap.child("renewDays").getValue().toString());
        return event;
    }

    public static Zone createZoneFromSnapshot(DataSnapshot snap) {
        Zone zone = new Zone();
        zone.setKey(snap.getKey());
        zone.setName(snap.child("name").getValue().toString());
        zone.setColor(snap.child("color").getValue().toString());
        zone.setTimeStart((long) snap.child("timeStart").getValue());
        zone.setTimeEnd((long) snap.child("timeEnd").getValue());
        if (snap.child("renewType").getValue() != null)
            zone.setRenewType(Integer.valueOf(snap.child("renewType").getValue().toString()));
        return zone;
    }

    public static int createDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static int createBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] *= 0.98;
        hsv[2] *= 1.02;
        return Color.HSVToColor(hsv);
    }

    public static int createSimiliarColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float newHue = hsv[0] + 10;
        hsv[0] = newHue;
        return Color.HSVToColor(hsv);
    }

    public static GradientDrawable createGradientDrawableLR(int color1, int color2) {
        int[] colors;
        if (color1 < color2) //So that gradient is dark to light
            colors = new int[] {color1, color2};
        else
            colors = new int[] {color2, color1};
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        return gd;
    }
    public static GradientDrawable createGradientDrawableTP(int main) {
        int color1 = createBrighterColor(main);
        int color2 = createSimiliarColor(color1);
        int[] colors;
        if (color1 < color2) //So that gradient is dark to light
            colors = new int[] {color1, color2};
        else
            colors = new int[] {color2, color1};
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        return gd;
    }



    public static boolean hasTaskBeenDoneToday(Task task, int resetHour) {
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.HOUR_OF_DAY, resetHour);
        long resetEpoch = c1.getTimeInMillis();
        long taskTime = task.getFinishedTime();
        if (taskTime > resetEpoch)
            return true;
        else
            return false;
    }

    public static void finishTask(Task task) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (task.getRepeat() == Task.NO_REPEAT) {
            db.child("tasks").child(auth.getCurrentUser().getUid()).child(task.getKey()).removeValue();
        } else if (task.getRepeat() == Task.REPEAT_DAILY) {
            long milli = System.currentTimeMillis();
            db.child("tasks").child(auth.getCurrentUser().getUid()).child(task.getKey()).child("finishedTime").setValue(milli);
        }
    }

    public static String capitalizeSentence(String sentence) {
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }
}
