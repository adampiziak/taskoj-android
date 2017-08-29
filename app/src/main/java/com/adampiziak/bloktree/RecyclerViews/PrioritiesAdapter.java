package com.adampiziak.bloktree.RecyclerViews;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Task;
import com.adampiziak.bloktree.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PrioritiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private List<List<Task>> priorities = new ArrayList<>();
    private List<TasksAdapter> adapters = new ArrayList<>();

    public PrioritiesAdapter() {
        initializeTaskAdapters();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_priority, parent, false);
        return new PriorityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return priorities.size();
    }

    private class PriorityViewHolder extends RecyclerView.ViewHolder {
        TextView priorityLevelText;
        RecyclerView rv;

        public PriorityViewHolder(View v) {
            super(v);
            priorityLevelText = (TextView) v.findViewById(R.id.item_priority_title);
            rv = (RecyclerView) v.findViewById(R.id.item_priority_rv);
        }
    }

    private void initializeTaskAdapters() {
        for (int i = 0; i < 5; i++)
            adapters.add(i, new TasksAdapter());
    }

    //User
    private int resetHour = 5;
    ////

    //Server sync
    private void syncWithServer() {

        for (int i = 0; i < 5; i++) { //One for each priority
            priorities.add(new ArrayList<Task>());
        }

        Query tasksRef = db
                .child("tasks")
                .child(auth.getCurrentUser().getUid())
                .orderByChild("priority");

        Query userRef = db
                .child("users")
                .child(auth.getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resetHour = Integer.valueOf(dataSnapshot.child("resetHour").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tasksRef.addChildEventListener(new ChildEventListener() {

            //onChildAdded is initially called for every item in list, then for any item added later
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task task = Tools.createTaskFromSnapshot(dataSnapshot); //Create task from database
                int priority = task.getPriority();
                if (task.getRepeat() == Task.NO_REPEAT) {
                    priorities.get(priority).add(task);
                    adapters.get(priority).notifyItemInserted(priorities.get(priority).size() - 1);
                } else if (task.getRepeat() == Task.REPEAT_DAILY) {
                    if (!Tools.hasTaskBeenDoneToday(task, resetHour)) {
                        priorities.get(priority).add(task);
                        adapters.get(priority).notifyItemInserted(priorities.get(priority).size() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Task task = Tools.createTaskFromSnapshot(dataSnapshot);
                boolean remove = false;
                if (task.getRepeat() == Task.REPEAT_DAILY && Tools.hasTaskBeenDoneToday(task, resetHour))
                    remove = true;
                boolean found = false;
                int priority = task.getPriority();
                for (int i = 0; i < priorities.get(priority).size(); i++) {
                    if (priorities.get(priority).get(i).getKey().equals(task.getKey())) {
                        if (remove) {
                            priorities.get(task.getPriority()).remove(i);
                        } else {
                            priorities.get(task.getPriority()).set(i, task);
                        }
                        if (adapters.get(task.getPriority()) != null)
                            adapters.get(task.getPriority()).notifyDataSetChanged();
                        found = true;
                        adapters.get(priority).notifyItemChanged(i);
                    }
                }
                if (!found && !remove) {
                    priorities.get(priority).add(task);
                    adapters.get(priority).notifyItemInserted(priorities.get(priority).size() - 1);
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

}
