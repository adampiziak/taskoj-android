package com.adampiziak.bloktree.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adampiziak.bloktree.Dialogs.DiaPostpone;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AdaPostpone extends RecyclerView.Adapter<AdaPostpone.ViewHolder> {
    DatabaseReference db;
    FirebaseAuth auth;
    Task task;
    DiaPostpone context;

    int[] postponeTimes = {1, 3, 6};

    public AdaPostpone(DiaPostpone context) {
        db = FirebaseDatabase.getInstance().getReference("tasks");
        auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View v) {
            super(v);
            text = (TextView) v.findViewById(R.id.item_dialog_priority_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar c = Calendar.getInstance();
                    Log.d("TIME_NOW", c.getTime().toString());
                    c.add(Calendar.HOUR, postponeTimes[getAdapterPosition()]);
                    long time = c.getTimeInMillis();
                    Calendar c2 = Calendar.getInstance();
                    c2.setTimeInMillis(time);
                    Log.d("NEXT_TIME", c2.getTime().toString());
                    db.child(auth.getCurrentUser().getUid()).child(task.getKey()).child("postponedUntil").setValue(time);
                    context.dismiss();
                }
            });
        }
    }

    @Override
    public AdaPostpone.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_priority, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdaPostpone.ViewHolder holder, int position) {
        holder.text.setText(String.valueOf(postponeTimes[position]) + " hours");

    }

    @Override
    public int getItemCount() {
        return postponeTimes.length;
    }
}
