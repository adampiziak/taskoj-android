package com.adampiziak.bloktree.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Tools;

public class ActEventEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_event_editor);

        TextView name = (TextView) findViewById(R.id.act_event_editor_name);
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        String eventColor = getIntent().getStringExtra("EVENT_COLOR");
        View toolbar = findViewById(R.id.event_background);
        getWindow().setStatusBarColor(Tools.createDarkerColor(Color.parseColor(eventColor)));
        toolbar.setBackgroundColor(Color.parseColor(eventColor));
        name.setText(eventName);
        getWindow().setSharedElementEnterTransition(enterTransition());
        getWindow().setSharedElementReturnTransition(returnTransition());


    }

    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(250);

        return bounds;
    }

    private Transition returnTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(250);

        return bounds;
    }
}
