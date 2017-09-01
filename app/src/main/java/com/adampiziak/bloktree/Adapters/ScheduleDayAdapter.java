package com.adampiziak.bloktree.Adapters;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.adampiziak.bloktree.Event;
import com.adampiziak.bloktree.Fragments.FraSchedule;
import com.adampiziak.bloktree.Fragments.FraSchedulePager;
import com.adampiziak.bloktree.Fragments.FraSplash;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.Task;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.Tools;
import com.adampiziak.bloktree.Zone;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleDayAdapter extends FragmentStatePagerAdapter {
    //debug
    final String TAG = "SCHEDULE_DAY_ADAPTER";

    //Parent Fragment with viewpager
    FraSchedulePager parent;

    //Adapter data
    final int PAGE_COUNT = 11;
    int offset = 0;
    int currentPosition = 5;
    boolean firstCall = true;

    //Once all data has been fetched, start updating UI
    boolean fetchedTasks = false;
    boolean fetchedProjects = false;
    boolean fetchedEvents = false;
    boolean fetchedZones = false;

    //App state
    Taskoj taskoj;

    //Firebase
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    //All data
    List<Task> tasks = new ArrayList<>();
    List<Project> projects = new ArrayList<>();
    List<Event> events = new ArrayList<>();
    List<Zone> zones = new ArrayList<>();

    public ScheduleDayAdapter(FragmentManager fm, FraSchedulePager context) {
        super(fm);
        this.parent = context;
        taskoj = (Taskoj) context.getActivity().getApplication();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 10) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    offset += 4;
                    parent.setPage(5);
                    notifyDataSetChanged();
                }
            }, 0);
        }
        if (position == 0) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    offset -= 4;
                    parent.setPage(5);
                    notifyDataSetChanged();
                }
            }, 0);
        }
        FraSchedule fragment = new FraSchedule();
        if (firstCall) {
            fragment.scrollToCurrentTime(0);
            firstCall = false;
        }
        fragment.setDayOffset(offset + (position - 5));
        currentPosition = position;
        return fragment;
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    private void fetchInitial() {

        //Queries
        String userId = auth.getCurrentUser().getUid();
        Query tasksRef = ref.child("tasks").child(userId);
        Query projectsRef = ref.child("projects").child(userId);
        Query eventsRef = ref.child("events").child(userId);
        Query zonesRef = ref.child("zones").child(userId);

        //Single value events, these are always called after onChildChanged listeners
        tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchedTasks = true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        projectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchedProjects = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchedEvents = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        zonesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchedZones = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //child event listeners
        tasksRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = Tools.createTaskFromSnapshot(dataSnapshot);
                tasks.add(task);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Task task = Tools.createTaskFromSnapshot(dataSnapshot);
                boolean found = false;
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getKey().equals(task.getKey())) {
                        tasks.set(i, task);
                        found = true;
                        break;
                    }
                }
                if (!found)
                    tasks.add(task);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getKey().equals(dataSnapshot.getKey())) {
                        tasks.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        projectsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Project project = Tools.createProjectFromSnapshot(dataSnapshot);
                projects.add(project);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Project project = Tools.createProjectFromSnapshot(dataSnapshot);
                boolean found = false;
                for (int i = 0; i < projects.size(); i++) {
                    if (projects.get(i).getKey().equals(project.getKey())) {
                        projects.set(i, project);
                        found = true;
                        break;
                    }
                }
                if (!found)
                    projects.add(project);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (int i = 0; i < projects.size(); i++) {
                    if (projects.get(i).getKey().equals(dataSnapshot.getKey())) {
                        projects.remove(i);
                        break;
                    }
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        eventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = Tools.createEventFromSnapshot(dataSnapshot);
                events.add(event);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event event = Tools.createEventFromSnapshot(dataSnapshot);
                boolean found = false;
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getKey().equals(event.getKey())) {
                        events.set(i, event);
                        found = true;
                        break;
                    }
                }

                if (!found)
                    events.add(event);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getKey().equals(dataSnapshot.getKey())) {
                        events.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        zonesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Zone zone = Tools.createZoneFromSnapshot(dataSnapshot);
                zones.add(zone);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Zone zone = Tools.createZoneFromSnapshot(dataSnapshot);
                boolean found = false;
                for (int i = 0; i < zones.size(); i++) {
                    if (zones.get(i).getKey().equals(zone.getKey())) {
                        zones.set(i, zone);
                        found = true;
                        break;
                    }
                }
                if (!found)
                    zones.add(zone);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (int i = 0; i < zones.size(); i++) {
                    if (zones.get(i).getKey().equals(dataSnapshot.getKey())) {
                        zones.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
    //git
    private void sync() {

    }

    public void setCurrentDay() {
        long delay = (parent.getViewPager().getCurrentItem() == 5 && offset == 0) ? 0 : 250;
        if (offset > 0)
            parent.setPage(8);
        else if (offset < 0)
            parent.setPage(2);
        offset = 0;
        parent.setPage(5, true);

        getFragmentInView().scrollToCurrentTime(delay);
    }

    //By Nepster, stackoverflow
    public FraSchedule getFragmentInView()
    {
        return ((FraSchedule) (instantiateItem(parent.getViewPager(), parent.getViewPager().getCurrentItem())));
    }


    public void setOffset(int offset) {
        this.offset = offset;
        notifyDataSetChanged();
    }

    public int getOffset() {
        return this.offset;
    }


}
