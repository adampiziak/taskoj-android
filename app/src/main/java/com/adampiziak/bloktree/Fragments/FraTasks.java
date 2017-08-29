package com.adampiziak.bloktree.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adampiziak.bloktree.Activities.ActGroupFullscreen;
import com.adampiziak.bloktree.Activities.ActTaskEditor;
import com.adampiziak.bloktree.Adapters.AdaSubtasks;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.SubTask;
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
import java.util.Calendar;
import java.util.List;

public class FraTasks extends Fragment {

    //global state
    private Taskoj taskoj;

    //primary recyclerview
    private RecyclerView taskList;
    private TaskAnimator primaryTaskAnimator;
    private PriorityAdapter priorityAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_tasks, container, false);

        //get global state
        taskoj = (Taskoj) getActivity().getApplicationContext();

        setupUI(v);

        return v;
    }

    private void setupUI(View v) {
        priorityAdapter = new PriorityAdapter();
        primaryTaskAnimator = new TaskAnimator();

        taskList = (RecyclerView) v.findViewById(R.id.f_tasks_rv);
        taskList.setLayoutManager(new LinearLayoutManager(getActivity()));
        taskList.setAdapter(priorityAdapter);
        taskList.setItemAnimator(primaryTaskAnimator);
    }

    private class PriorityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //Initialization
        private final List<Project> projects = new ArrayList<>();
        private final List<List<Task>> priorities = new ArrayList<>();
        private final List<TasksAdapter> adapters = new ArrayList<>();
        private final List<TaskAnimator> animators = new ArrayList<>();
        private int resetHour = 5;

        private static final int EXPAND = 1;
        private static final int COLLAPSE = 2;

        DatabaseReference db;
        FirebaseAuth auth;

        private void initializeLists() {
            for (int i = 0; i < 5; i++) {
                priorities.add(i, new ArrayList<Task>());
                adapters.add(i, new TasksAdapter(i));
                animators.add(i, new TaskAnimator());
            }
        }


        PriorityAdapter() {
            db = FirebaseDatabase.getInstance().getReference();
            auth = FirebaseAuth.getInstance();

            syncWithServer();
        }

        private void syncWithServer() {
            initializeLists();

            Query tasksRef = db
                    .child("tasks")
                    .child(auth.getCurrentUser().getUid())
                    .orderByChild("priority");
            Query projectsRef = db
                    .child("projects")
                    .child(auth.getCurrentUser().getUid());
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

            tasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //After initial data has been loaded
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            tasksRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Task task = Tools.createTaskFromSnapshot(dataSnapshot);
                    int priority = task.getPriority();
                    int insertPosition = priorities.get(priority).size() - 1;
                    if (task.getRepeat() == Task.NO_REPEAT) {
                        priorities.get(priority).add(task);
                        adapters.get(priority).notifyItemInserted(insertPosition);
                    } else if (task.getRepeat() == Task.REPEAT_DAILY) {
                        if (!hasTaskBeenDoneToday(task)) {
                            priorities.get(priority).add(task);
                            adapters.get(priority).notifyItemInserted(insertPosition);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Task task = Tools.createTaskFromSnapshot(dataSnapshot);
                    int priority = task.getPriority();
                    boolean remove = false;
                    if (task.getRepeat() == Task.REPEAT_DAILY && hasTaskBeenDoneToday(task))
                        remove = true;
                    boolean found = false;
                    List<Task> priorityTasks = priorities.get(priority);
                    for (int i = 0; i < priorities.get(priority).size(); i++) {
                        //If a match is found
                        if (priorities.get(priority).get(i).getKey().equals(task.getKey())) {
                            if (remove) {
                                priorities.get(priority).remove(i);
                                adapters.get(priority).notifyItemRemoved(i);
                            } else {
                                priorities.get(priority).set(i, task);
                                adapters.get(priority).notifyItemChanged(i);
                            }
                            found = true;
                        }
                    }
                    if (!found && !remove) {
                        priorities.get(priority).add(task);
                        adapters.get(priority).notifyItemInserted(priorities.get(priority).size() - 1);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Task task = Tools.createTaskFromSnapshot(dataSnapshot);
                    int priority = task.getPriority();
                    for (int i = 0; i < priorities.get(priority).size(); i++) {
                        //if a match is found
                        if (priorities.get(priority).get(i).getKey().equals(task.getKey())) {
                            priorities.get(priority).remove(i);
                            adapters.get(priority).notifyItemRemoved(i);
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
                    notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Project project = Tools.createProjectFromSnapshot(dataSnapshot);
                    for (int i = 0; i < projects.size(); i++) {
                        //if a match is found
                        if (projects.get(i).getKey().equals(project.getKey())) {
                            projects.set(i, project);
                            notifyDataSetChanged();
                        }
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
        ////




        //PriorityAdapter bindings
        class PriorityViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            RecyclerView rv;

            PriorityViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.item_priority_title);
                rv = (RecyclerView) v.findViewById(R.id.item_priority_rv);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new PriorityViewHolder(inflater.inflate(R.layout.item_priority, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int priority) {
            PriorityViewHolder h = ((PriorityViewHolder) holder);
            if (priorities.get(priority).size() > 0) {
                h.itemView.setVisibility(View.VISIBLE);
                h.title.setVisibility(View.VISIBLE);
                h.title.setText(Task.getPriorityName(priority));
                h.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                TasksAdapter adapter = new TasksAdapter(priority);
                adapters.add(priority, adapter);
                h.rv.setAdapter(adapters.get(priority));
                h.rv.setItemAnimator(animators.get(priority));

                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        Task task = priorities.get(priority).get(viewHolder.getAdapterPosition());
                        if (task.getSubtasks().size() > 0) {
                            Dialog dialog = createConfirmationDialog(task);
                            dialog.show();
                        } else {
                            Tools.finishTask(task);
                        }
                        notifyDataSetChanged();
                    }
                };

                ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallback);
                helper.attachToRecyclerView(h.rv);
            } else {
                h.itemView.setVisibility(View.GONE);
                h.title.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return priorities.size();
        }



        AlertDialog createConfirmationDialog(final Task task) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
            dialog.setTitle("Confirm");
            String action = (task.getRepeat() == Task.NO_REPEAT) ? "complete forever" : "complete";
            String note = (task.getRepeat() == Task.NO_REPEAT) ? "" : " This task will become active tomorrow at " + resetHour + ".";
            dialog.setMessage("Are you sure you want to " + action + " ("
                    + task.getName() +
                    ") and all of its subtasks?" + note);
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Tools.finishTask(task);
                        }
                    });
            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                    });
            return dialog;
        }




        //// end priority bindings




        //Tasks Adapter
        class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            int priority;

            TasksAdapter(int priority) {
                this.priority = priority;
            }

            class TaskViewHolder extends RecyclerView.ViewHolder {
                TextView name;
                TextView project;
                LinearLayout root;
                View top;
                View bottom;
                LinearLayout options;
                ImageView renew;
                ImageView edit;

                TaskViewHolder(View v) {
                    super(v);
                    name = (TextView) v.findViewById(R.id.item_task_name);
                    project = (TextView) v.findViewById(R.id.item_task_project);
                    root = (LinearLayout) v.findViewById(R.id.item_task_root);
                    top = v.findViewById(R.id.item_task_top);
                    bottom = v.findViewById(R.id.item_task_bottom);
                    options = (LinearLayout) v.findViewById(R.id.item_task_options);
                    renew = (ImageView) v.findViewById(R.id.item_task_renew_icon);
                    edit = (ImageView) v.findViewById(R.id.item_task_option_edit);
                }
            }

            class GroupViewHolder extends RecyclerView.ViewHolder {
                TextView name;
                TextView project;
                TextView preview;
                ImageView fullscreen;
                RecyclerView subTasks;
                View divider;
                LinearLayout subtaskContainer;
                LinearLayout options;
                ImageView edit;
                ImageView start;

                GroupViewHolder(View v) {
                    super(v);
                    name = (TextView) v.findViewById(R.id.item_group_name);
                    project = (TextView) v.findViewById(R.id.item_group_project);
                    preview = (TextView) v.findViewById(R.id.item_group_preview);
                    subTasks = (RecyclerView) v.findViewById(R.id.item_group_subtasks_rv);
                    divider = v.findViewById(R.id.item_group_divider);
                    subtaskContainer = (LinearLayout) v.findViewById(R.id.item_group_subtasks_parent);
                    fullscreen = (ImageView) v.findViewById(R.id.item_group_option_fullscreen);
                    options = (LinearLayout) v.findViewById(R.id.item_group_options);
                    edit = (ImageView) v.findViewById(R.id.item_group_option_edit);
                    start = (ImageView) v.findViewById(R.id.item_group_option_start);
                }

            }



            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case (R.layout.item_task_view):
                        return createTaskHolder(parent, viewType);
                    case (R.layout.item_group_view):
                        return createGroupHolder(parent, viewType);
                }
                throw new IllegalArgumentException();
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                         int position, List<Object> partialChangePayloads) {
                if (holder instanceof GroupViewHolder) {
                    bindPartialGroupChange((GroupViewHolder) holder, position, partialChangePayloads);
                } else if (holder instanceof TaskViewHolder) {
                    bindPartialTaskChange((TaskViewHolder) holder, position, partialChangePayloads);
                }

                onBindViewHolder(holder, position);

            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                switch (getItemViewType(position)) {
                    case (R.layout.item_task_view):
                        bindTask((TaskViewHolder) holder, priorities.get(priority).get(position));
                        break;
                    case (R.layout.item_group_view):
                        bindGroup((GroupViewHolder) holder, priorities.get(priority).get(position));
                        break;
                }
            }

            @Override
            public int getItemViewType(int position) {
                if (priorities.get(priority).get(position).getSubtasks().size() > 0)
                    return R.layout.item_group_view;
                else
                    return R.layout.item_task_view;
            }

            @Override
            public int getItemCount() {
                return priorities.get(priority).size();
            }

            private TaskViewHolder createTaskHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                final TaskViewHolder holder = new TaskViewHolder(inflater.inflate(viewType, parent, false));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int expandedTaskPosition = taskoj.getTaskPosition();
                        int expandedPriorityPosition = taskoj.getPriorityPosition();
                        final int position = holder.getAdapterPosition();
                        boolean expand = expandedPriorityPosition != priority || expandedTaskPosition != position;


                        primaryTaskAnimator.setAnimateMoves(false);
                        animators.get(priority).setAnimateMoves(false);
                        TransitionManager.beginDelayedTransition(taskList, createGroupTransition(expand, false));

                        //If view no longer exists in adapter. Not sure if this would ever
                        //be called but this checks and returns if true.
                        if (position == RecyclerView.NO_POSITION)
                            return;

                        if (expand) {
                            if (expandedPriorityPosition >= 0 || expandedTaskPosition >= 0)
                                adapters.get(expandedPriorityPosition).notifyItemChanged(expandedTaskPosition, COLLAPSE); //Collapse current active task
                            notifyItemChanged(position, EXPAND);
                            holder.itemView.requestFocus();
                            taskoj.setPriorityPosition(priority);
                            taskoj.setTaskPosition(position);
                        } else {
                            notifyItemChanged(position, COLLAPSE);
                            taskoj.setPriorityPosition(RecyclerView.NO_POSITION);
                            taskoj.setTaskPosition(RecyclerView.NO_POSITION);
                        }
                    }
                });
                return holder;
            }

            private GroupViewHolder createGroupHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                final GroupViewHolder holder = new GroupViewHolder(inflater.inflate(viewType, parent, false));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int expandedTaskPosition = taskoj.getTaskPosition();
                        int expandedPriorityPosition = taskoj.getPriorityPosition();
                        final int position = holder.getAdapterPosition();
                        boolean expand = expandedPriorityPosition != priority || expandedTaskPosition != position;

                        //If view no longer exists in adapter. Not sure if this would ever
                        //be called but this checks and returns if true.
                        if (position == RecyclerView.NO_POSITION)
                            return;

                        //animators.get(priority).setAnimateMoves(false);
                        //taskAnimator.setAnimateMoves(false); //Prevent recyclerview from moving around items while the group expands/collapses

                        boolean wait = expand && priority == expandedPriorityPosition;
                        primaryTaskAnimator.setAnimateMoves(false);
                        animators.get(priority).setAnimateMoves(false);
                        TransitionManager.beginDelayedTransition(taskList, createGroupTransition(expand, wait));


                        //If this group is currently expanded, close it and set the expanded position in the global state to none
                        if (!expand) {
                            notifyItemChanged(position, COLLAPSE);
                            taskoj.setPriorityPosition(RecyclerView.NO_POSITION);
                            taskoj.setTaskPosition(RecyclerView.NO_POSITION);
                        } else { // If its not expanded, expand it and set global state
                            if (expandedPriorityPosition >= 0 || expandedTaskPosition >= 0)
                                adapters.get(expandedPriorityPosition).notifyItemChanged(expandedTaskPosition, COLLAPSE); //Collapse current active task
                            notifyItemChanged(position, EXPAND);
                            holder.itemView.requestFocus();
                            taskoj.setPriorityPosition(priority);
                            taskoj.setTaskPosition(position);
                        }

                    }
                });
                return holder;
            }

            private TransitionSet createGroupTransition(boolean expand, boolean wait) {
                TransitionSet set = new TransitionSet();
                long duration = 300;
                ChangeBounds changeBounds = new ChangeBounds();
                Fade fade = new Fade();
                fade.setDuration(duration);
                if (wait) {
                    changeBounds.setStartDelay(duration / 4);
                }

                if (expand) {
                    changeBounds.setDuration(duration);
                } else {
                    changeBounds.setDuration(200);
                }



                set.setOrdering(TransitionSet.ORDERING_TOGETHER);
                set.addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
                        primaryTaskAnimator.setAnimateMoves(true);
                        animators.get(priority).setAnimateMoves(true);
                    }

                    @Override
                    public void onTransitionCancel(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(@NonNull Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(@NonNull Transition transition) {

                    }
                });
                set.addTransition(changeBounds);
                set.addTransition(fade);
                return set;
            }

            private void bindTask(TaskViewHolder holder, final Task task) {
                holder.name.setText(task.getName());
                Project project = getProject(task.getProjectKey());
                int color = Color.parseColor(project.getColor());
                holder.project.setText(capitalizeSentence(project.getName()));
                holder.project.setTextColor(color);
                if (task.getRepeat() != Task.NO_REPEAT) {
                    holder.renew.setVisibility(View.VISIBLE);
                    holder.renew.setColorFilter(color);
                }
                final String taskKey = task.getKey();
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActTaskEditor.class);
                        intent.putExtra("TASK_KEY", taskKey);
                        startActivity(intent);

                    }
                });


            }

            private void bindGroup(GroupViewHolder holder, Task task) {
                holder.name.setText(task.getName());
                Project project = getProject(task.getProjectKey());
                holder.project.setText(capitalizeSentence(project.getName()));
                if (taskoj.getTaskPosition() == holder.getAdapterPosition() && taskoj.getPriorityPosition() == priority)
                    setGroupExpandState(holder, true);
                else
                    holder.project.setTextColor(Color.parseColor(project.getColor()));

                final String key = task.getKey();
                holder.fullscreen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActGroupFullscreen.class);
                        intent.putExtra("TASK_KEY", key);
                        startActivity(intent);
                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ActTaskEditor.class);
                        intent.putExtra("TASK_KEY", key);
                        startActivity(intent);
                    }
                });

                holder.start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("FRA_TASKS", "HEY_THERE");
                    }
                });

                String preview = "";
                List<SubTask> subTasks = task.getSubtasks();
                for (int i = 0; i < subTasks.size(); i++) {
                    preview += subTasks.get(i).getName();
                    if (i < subTasks.size() - 1)
                        preview += ", ";
                }
                holder.preview.setText(preview);
                holder.subTasks.setLayoutManager(new LinearLayoutManager(getContext()));
                holder.subTasks.setAdapter(new AdaSubtasks(task.getSubtasks(), task.getKey()));

            }

            private void bindPartialTaskChange(TaskViewHolder holder, int position, List<Object> partialTaskPayload) {
                if (partialTaskPayload.contains(EXPAND) || partialTaskPayload.contains(COLLAPSE))
                    setTaskExpandState(holder, position == taskoj.getTaskPosition() && priority == taskoj.getPriorityPosition());
            }

            private void bindPartialGroupChange(GroupViewHolder holder,
                                                int position,
                                                List<Object> partialGroupPayload) {
                if (partialGroupPayload.contains(EXPAND) || partialGroupPayload.contains(COLLAPSE))
                    setGroupExpandState(holder, position == taskoj.getTaskPosition() && priority == taskoj.getPriorityPosition());
                else
                    onBindViewHolder(holder, position);
            }

            private void setTaskExpandState(TaskViewHolder holder, boolean expandState) {
                holder.itemView.setActivated(expandState);
                holder.top.setVisibility((expandState) ? View.VISIBLE : View.GONE);
                holder.bottom.setVisibility((expandState) ? View.VISIBLE : View.GONE);
                holder.options.setVisibility((expandState) ? View.VISIBLE : View.GONE);

            }

            private void setGroupExpandState(GroupViewHolder holder, boolean expandState) {
                holder.subtaskContainer.setVisibility((expandState) ? View.VISIBLE : View.GONE);
                holder.preview.setVisibility((expandState) ? View.GONE : View.VISIBLE);

                Project project = getProject(priorities.get(priority).get(holder.getAdapterPosition()).getProjectKey());
                holder.project.setTextColor((expandState) ? Color.parseColor("#FAFAFA") : Color.parseColor(project.getColor()));
                int projectColor = Tools.createBrighterColor(Color.parseColor(project.getColor()));
                holder.divider.setBackgroundColor((expandState)
                        ? projectColor : Color.parseColor("#EEEEEE"));
                holder.itemView.setActivated(expandState);
                holder.options.setVisibility((expandState) ? View.VISIBLE : View.GONE);
                if (expandState) {
                    holder.itemView.setBackground(Tools.createGradientDrawableLR(projectColor, Tools.createSimiliarColor(projectColor)));
                } else {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FAFAFA"));
                }
                holder.name.setTextColor((expandState) ? Color.parseColor("#F5F5F5") : Color.parseColor("#212121"));

            }

        }

        private String capitalizeSentence(String sentence) {
            return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
        }
        ////




        //Methods
        boolean hasTaskBeenDoneToday(Task task) {
            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.HOUR_OF_DAY, resetHour);
            long resetEpoch = c1.getTimeInMillis();
            long taskTime = task.getFinishedTime();
            if (taskTime > resetEpoch)
                return true;
            else
                return false;
        }

        private Project getProject(String key) {
            Project project = new Project();
            for (int i = 0; i < projects.size(); i++) {
                if (projects.get(i).getKey().equals(key)) {
                    project = projects.get(i);
                    break;
                }
            }
            return project;
        }
    }




    //RecyclerView gets confused upon an item height change and moves items around. This
    //animator allows disabling item rearrangement. With this, moves are prohibited during a height change
    // Recommended at Google I/O

    private static class TaskAnimator extends DefaultItemAnimator {

        private boolean animateMoves = false;

        public TaskAnimator() {
        }

        void setAnimateMoves(boolean animateMoves) {
            this.animateMoves = animateMoves;
        }
        @Override
        public boolean animateMove(
                RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            if (!animateMoves) {
                dispatchMoveFinished(holder);
                return false;
            }
            return super.animateMove(holder, fromX, fromY, toX, toY);
        }
    }
}


/*
                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        final int position = viewHolder.getAdapterPosition();
                        final Task task = pTasks.get(position);
                        final int i = position;
                        if (task.getSubtasks().size() > 0 && task.getRepeat() == Task.NO_REPEAT) {
                            AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
                            dialog.setTitle("Confirm");
                            dialog.setMessage("Are you sure you want to delete ("
                                    + task.getName() +
                                    ") and all of its subtasks?");
                            dialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.child("tasks").child(auth.getCurrentUser().getUid()).child(task.getKey()).removeValue();
                                            pTasks.remove(position);
                                            dialog.dismiss();
                                            notifyDataSetChanged();
                                        }
                                    });
                            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            notifyDataSetChanged();
                                        }
                                    });
                            dialog.show();
                        } else if (task.getRepeat() == Task.REPEAT_DAILY) {
                            long time = System.currentTimeMillis();
                            db.child("tasks").child(auth.getCurrentUser().getUid()).child(task.getKey()).child("finishedTime").setValue(time);
                            notifyDataSetChanged();
                        } else {
                            db.child("tasks").child(auth.getCurrentUser().getUid()).child(task.getKey()).removeValue();
                        }

                        notifyItemRemoved(position);
                    }
                };

                ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallback);
                helper.attachToRecyclerView(parentRV);
                */