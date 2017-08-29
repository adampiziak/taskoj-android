package com.adampiziak.bloktree.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.adampiziak.bloktree.R;

public class ActActiveTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_active_task);
        getWindow().setStatusBarColor(Color.parseColor("#263238"));
    }
}
