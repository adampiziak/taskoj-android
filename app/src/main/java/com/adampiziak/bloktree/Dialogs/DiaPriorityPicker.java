package com.adampiziak.bloktree.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.adampiziak.bloktree.Adapters.AdaPriorityDialog;
import com.adampiziak.bloktree.R;

public class DiaPriorityPicker extends DialogFragment {
    private RecyclerView mRecyclerView;
    private AdaPriorityDialog adapter;
    View v;
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dia_priority_selector, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.dialog_priority_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AdaPriorityDialog(getActivity());
        mRecyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog = this.getDialog();


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = Math.round((metrics.widthPixels * 95)/100);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
    }


}
