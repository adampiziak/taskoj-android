package com.adampiziak.bloktree.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.adampiziak.bloktree.Adapters.AdaColorList;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ActProjectCreator extends AppCompatActivity {

    String color = "#f44336";
    TextView actionCreate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_project_creator);

        final EditText text = (EditText) findViewById(R.id.act_project_creator_text);
        actionCreate = (TextView) findViewById(R.id.action_create_project);
        RecyclerView rv = (RecyclerView) findViewById(R.id.project_creator_rv);
        ((Taskoj) getApplication()).setProjectCase(0);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setAdapter(new AdaColorList(this));
        getWindow().setStatusBarColor(Color.parseColor("#ECEFF1"));
        rv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        text.clearFocus();

        actionCreate.setBackgroundColor(Color.parseColor(color));
        actionCreate.setTextColor(Color.WHITE);
        actionCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = text.getText().toString().toLowerCase();
                Project project = new Project(name, color);

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                mDatabase.getReference().child("projects")
                        .child(mAuth.getCurrentUser().getUid()).push().setValue(project);
                finish();
            }
        });
    }

    public void setColor(String color) {
        this.color = color;
        actionCreate.setBackgroundColor(Color.parseColor(color));

    }
}
