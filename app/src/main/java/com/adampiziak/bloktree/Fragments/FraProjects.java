package com.adampiziak.bloktree.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adampiziak.bloktree.Adapters.AdaProjectList;
import com.adampiziak.bloktree.R;

public class FraProjects extends Fragment {
    AdaProjectList adapter;
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fra_projects, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.f_projects_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AdaProjectList(getActivity());
        mRecyclerView.setAdapter(adapter);

        setOnSwipeListener();
        return v;
    }

    public void setOnSwipeListener() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                adapter.deleteProject(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(simpleItemTouchCallback);
        helper.attachToRecyclerView(mRecyclerView);
    }



}
