package com.adampiziak.bloktree.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adampiziak.bloktree.Activities.ActProjectEditor;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AdaProjectList extends RecyclerView.Adapter<AdaProjectList.ViewHolder> {
    private ArrayList<Project> projects = new ArrayList<>();

    FirebaseAuth auth;
    DatabaseReference mDataBase;
    final Context context;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mGroupColor;
        LinearLayout mRoot;
        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.item_project_name);
            mGroupColor = (ImageView) v.findViewById(R.id.item_project_color);
            mRoot = (LinearLayout) v.findViewById(R.id.item_project_root);
        }
    }

    public void deleteProject(final int position) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Confirm");
        dialog.setMessage("Are you sure you want to delete ("
                + projects.get(position).getName() +
                ") and all of its tasks?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String projectKey = projects.get(position).getKey();
                        mDataBase.child("projects").child(auth.getCurrentUser().getUid())
                                .child(projectKey).removeValue();
                        notifyItemRemoved(position);
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

    }

    public AdaProjectList(Context context) {
        this.context = context;
        mDataBase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        DatabaseReference ref = mDataBase.child("projects").child(auth.getCurrentUser().getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Project project = dataSnapshot.getValue(Project.class);
                project.setKey(dataSnapshot.getKey());
                projects.add(project);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (int i = 0; i < projects.size(); i++) {
                    if (projects.get(i).getKey().equals(dataSnapshot.getKey()))
                        projects.remove(i);
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public AdaProjectList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_project_view, parent, false);
        final ViewHolder vh = new ViewHolder(groupView);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectKey = projects.get(vh.getAdapterPosition()).getKey();
                Intent intent = new Intent(context, ActProjectEditor.class);
                intent.putExtra("PROJECT_KEY", projectKey);
                context.startActivity(intent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String name = projects.get(position).getName();
        final String color = projects.get(position).getColor();
        holder.mTextView.setText(name);
        holder.mGroupColor.setImageDrawable(backgroundCircle(color));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
    private GradientDrawable backgroundCircle(String color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(color));
        gd.setCornerRadius(10);
        gd.setStroke(2, Color.WHITE);
        gd.setShape(GradientDrawable.OVAL);
        return gd;
    }

}
