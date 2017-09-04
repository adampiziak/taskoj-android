package com.adampiziak.bloktree.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adampiziak.bloktree.Activities.ActProjectPicker;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AdaProjectPicker extends RecyclerView.Adapter<AdaProjectPicker.ViewHolder> {
    private ArrayList<Project> projects = new ArrayList<>();
    private ArrayList<Project> filteredProjects = new ArrayList<>();
    String filterText = "";
    DatabaseReference mDataBase;
    Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mGroupColor;
        public LinearLayout mRoot;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.rv_group_name);
            mGroupColor = (ImageView) v.findViewById(R.id.rv_group_icon_background);
            mRoot = (LinearLayout) v.findViewById(R.id.rv_group_root);
        }
    }

    public AdaProjectPicker(Context context) {
        mContext = context;
        mDataBase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = mDataBase.child("projects").child(mAuth.getCurrentUser().getUid());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Project project = dataSnapshot.getValue(Project.class);
                project.setKey(dataSnapshot.getKey());
                projects.add(project);
                notifyItemInserted(projects.size());
                notifyFilter();
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View groupView = inflater.inflate(R.layout.item_project_selection, parent, false);
        ViewHolder vh = new ViewHolder(groupView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Project project = filteredProjects.get(position);
        final String name = project.getName();
        if (filterText == "" || name.contains(filterText)) {
            holder.mRoot.setVisibility(View.VISIBLE);
            final String color = project.getColor();
            holder.mTextView.setText(capitalizeSentence(name));
            holder.mGroupColor.setImageDrawable(backgroundCircle(color));
            final int i = position;
            holder.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (project.hasKey())
                        ((ActProjectPicker) mContext).selectAndFinish(project.getKey(), project.getName());
                }
            });
        } else {
            holder.mRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredProjects.size();
    }
    private GradientDrawable backgroundCircle(String color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(color));
        gd.setCornerRadius(10);
        gd.setStroke(2, Color.WHITE);
        gd.setShape(GradientDrawable.OVAL);
        return gd;
    }

    public void setFilterText(String text) {
        this.filterText = text;
        notifyFilter();
    }

    private void notifyFilter() {
        if (filterText == "") {
            filteredProjects = projects;
            notifyDataSetChanged();
        } else {
            filteredProjects = new ArrayList<>();
            for (Project project : projects) {
                if (project.getName().contains(filterText)) {
                    filteredProjects.add(project);
                }
            }
            notifyDataSetChanged();
        }
    }

    private String capitalizeSentence(String sentence) {
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }
}
