package com.adampiziak.bloktree.Activities;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActProjectEditor extends AppCompatActivity {
    String color = "#f44336";
    TextView actionSave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_project_editor);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        ((Taskoj) getApplication()).setProjectCase(1);

        final EditText projectName = (EditText) findViewById(R.id.project_editor_text);
        actionSave = (TextView) findViewById(R.id.action_save_edit_project);
        RecyclerView rv = (RecyclerView) findViewById(R.id.project_editor_rv);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        final AdaColorList adapter = new AdaColorList(this);
        rv.setAdapter(adapter);
        getWindow().setStatusBarColor(Color.parseColor("#ECEFF1"));
        rv.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Intent intent = getIntent();
        final String projectKey = intent.getStringExtra("PROJECT_KEY");
        db.child("projects").child(auth.getCurrentUser().getUid()).child(projectKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projectName.setText(dataSnapshot.child("name").getValue().toString());
                color = dataSnapshot.child("color").getValue().toString();
                actionSave.setBackgroundColor(Color.parseColor(color));
                adapter.setColorPosition(color);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        projectName.clearFocus();

        actionSave.setBackgroundColor(Color.parseColor(color));
        actionSave.setTextColor(Color.WHITE);
        actionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = projectName.getText().toString().toLowerCase();
                Project project = new Project(name, color);
                db.child("projects")
                        .child(auth.getCurrentUser().getUid()).child(projectKey).setValue(project);
                finish();
            }
        });
    }

    public void setColor(String color) {
        this.color = color;
        actionSave.setBackgroundColor(Color.parseColor(color));

    }
}
