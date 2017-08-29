package com.adampiziak.bloktree;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Taskoj extends Application {

    List<Event> events = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // allows app to use database while offline and then sync when online
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private void sync() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.child("events").child(auth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = Tools.createEventFromSnapshot(dataSnapshot);
                events.add(event);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event event = Tools.createEventFromSnapshot(dataSnapshot);
                for (int i = 0; i < events.size(); i++) {

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //Priority tasks position for toggle expand/collapse
    private int priorityPosition = RecyclerView.NO_POSITION;
    private int taskPosition = RecyclerView.NO_POSITION;
    ////


    // State of main ViewPager position
    private int mainFragmentPage = SCHEDULE_PAGE;
    public static final int HOME_PAGE = 0;
    public static final int PROJECTS_PAGE = 1;
    public static final int SCHEDULE_PAGE = 2;
    public static final int ZONES_PAGE = 3;

    public void setMainFragmentPage (int page) {
        this.mainFragmentPage = page;
    }

    public int getMainFragmentPage () {
        return this.mainFragmentPage;
    }
    ////

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    //Schedule Fragment
    public int scrollOffset = 0;

    private int dayOffset = 0;

    private int scheduleViewPagerPosition = 5;

    public int getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(int dayOffset) {
        this.dayOffset = dayOffset;
    }

    public int getScheduleViewPagerPosition() {
        return scheduleViewPagerPosition;
    }

    public void setScheduleViewPagerPosition(int scheduleViewPagerPosition) {
        this.scheduleViewPagerPosition = scheduleViewPagerPosition;
        Log.d("TASKOJ", "setScheduleViewPagerPosition: " + scheduleViewPagerPosition);
    }




    //Creators and editors
    public static final int CASE_TASK_CREATOR = 0;
    public static final int CASE_TASK_EDITOR = 1;

    private int projectCase = CASE_TASK_CREATOR;
    private int priorityCase = CASE_TASK_CREATOR;
    private int repeatCase = CASE_TASK_CREATOR;
    ////


    //Getters and Setters
    public int getRepeatCase() {
        return repeatCase;
    }
    public void setRepeatCase(int repeatCase) {
        this.repeatCase = repeatCase;
    }

    public int getProjectCase() {
        return projectCase;
    }
    public void setProjectCase(int projectCase) {
        this.projectCase = projectCase;
    }
    public int getPriorityCase() {
        return this.priorityCase;
    }
    public void setPriorityCase(int i) {
        this.priorityCase = i;
    }

    public int getPriorityPosition() {
        return this.priorityPosition;
    }
    public void setPriorityPosition(int position) {
        this.priorityPosition = position;
    }

    public int getTaskPosition() {
        return this.taskPosition;
    }
    public void setTaskPosition(int position) {
        this.taskPosition = position;
    }
    ////



}
