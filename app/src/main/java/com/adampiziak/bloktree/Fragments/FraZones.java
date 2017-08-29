package com.adampiziak.bloktree.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adampiziak.bloktree.Activities.ActEventCreator;
import com.adampiziak.bloktree.Activities.ActZoneCreator;
import com.adampiziak.bloktree.R;

public class FraZones extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_zones, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fra_zones_fab);
        fab.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fra_zones_fab:
                Intent intent = new Intent(getActivity(), ActZoneCreator.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
        }
    }
}
