package com.adampiziak.bloktree.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.SubTask;

import java.util.ArrayList;

public class AdaCreatorSubTasks extends RecyclerView.Adapter<AdaCreatorSubTasks.ViewHolder> {
    private ArrayList<SubTask> subTasks = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.creator_subtask_name);
        }
    }

    public AdaCreatorSubTasks() {
        this.subTasks = new ArrayList<>();
    }

    @Override
    public AdaCreatorSubTasks.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_creator_subtask, parent, false);
        ViewHolder vh = new ViewHolder(groupView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SubTask subTask = subTasks.get(position);
        holder.name.setText(subTask.getName());
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public void add(SubTask subTask){
        this.subTasks.add(subTask);
        notifyDataSetChanged();
    }
}
