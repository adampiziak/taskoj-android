package com.adampiziak.bloktree.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adampiziak.bloktree.R;

public class FraSplash extends Fragment {
    TextView title;
    String titleText = "PLACEHOLDER";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_splash, container, false);
        title = (TextView) v.findViewById(R.id.splash_title);
        return v;
    }

    public void setTitle(String text) {
        this.titleText = text;
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText(titleText);
    }
}
