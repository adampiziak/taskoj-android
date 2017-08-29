package com.adampiziak.bloktree.RecyclerViews;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.Task;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Task> tasks = new ArrayList<>();
    List<Project> projects = new ArrayList<>();

    public TasksAdapter() {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
