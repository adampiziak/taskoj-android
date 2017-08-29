package com.adampiziak.bloktree.Adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.SubTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdaSubtasks extends RecyclerView.Adapter<AdaSubtasks.ViewHolder> {

    List<SubTask> subTasks;
    String parentKey = "";

    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;

    public AdaSubtasks(List<SubTask> tasks, String key) {
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        this.subTasks = tasks;
        this.parentKey = key;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox checkBox;
        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.creator_subtask_name);
            checkBox = (CheckBox) v.findViewById(R.id.creator_subtask_checkbox);
        }
    }

    @Override
    public AdaSubtasks.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View subtask = inflater.inflate(R.layout.item_creator_subtask, parent, false);
        return new ViewHolder(subtask);
    }

    @Override
    public void onBindViewHolder(final AdaSubtasks.ViewHolder holder, int position) {
        holder.name.setText(subTasks.get(position).getName());
        if (subTasks.get(position).getStatus())
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        boolean status = subTasks.get(position).getStatus();
        holder.checkBox.setChecked(status);
        holder.itemView.setBackgroundColor((status) ? Color.parseColor("#EEEEEE") : Color.WHITE);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                mDatabase.getReference()
                        .child("tasks")
                        .child(mAuth.getCurrentUser().getUid())
                        .child(parentKey)
                        .child("subTasks")
                        .child(subTasks.get(holder.getAdapterPosition()).getKey())
                        .child("status")
                        .setValue(isChecked);
            }
        });
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        notifyItemInserted(subTasks.size() - 1);
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }
}
