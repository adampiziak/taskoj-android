package com.adampiziak.bloktree.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.Dialogs.DiaPriorityPicker;
import com.adampiziak.bloktree.Dialogs.DiaRepeatPicker;
import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActTaskEditor extends AppCompatActivity {
    final int REQUEST_PROJECT = 10;
    EditText taskNameEdit;
    TextView projectText;
    LinearLayout viewPriority;
    LinearLayout viewRepeat;
    DiaPriorityPicker priorityDialog;
    DiaRepeatPicker repeatDialog;
    TextView priorityText;
    FirebaseAuth auth;
    DatabaseReference db;
    FloatingActionButton fab;
    Button delete;

    String name = "";
    long priority = 1;
    String projectKey = "default";
    String projectName = "general";
    String projectColor = "#424242";
    long repeat = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_task_editor);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        taskNameEdit = (EditText) findViewById(R.id.task_name_edit);
        priorityText = (TextView) findViewById(R.id.act_task_editor_priority_text);
        viewPriority = (LinearLayout) findViewById(R.id.act_task_editor_priority);
        projectText = (TextView) findViewById(R.id.task_edit_proj_text);
        viewRepeat = (LinearLayout) findViewById(R.id.task_edit_repeat);
        delete = (Button) findViewById(R.id.act_task_editor_delete);

        viewPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityDialog = new DiaPriorityPicker();
                priorityDialog.show(getSupportFragmentManager(), "priority_dialog");
            }
        });
        viewRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatDialog = new DiaRepeatPicker();
                repeatDialog.show(getSupportFragmentManager(), "dia_repeat");
            }
        });
        fab = (FloatingActionButton) findViewById(R.id.task_edit_fab);
        final LinearLayout projectField = (LinearLayout) findViewById(R.id.task_edit_proj);
        final Context context = this;
        projectField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActProjectPicker.class);
                startActivityForResult(i, REQUEST_PROJECT);
            }
        });
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String taskKey = intent.getStringExtra("TASK_KEY");
        final DatabaseReference taskRef = db
                .child("tasks")
                .child(auth.getCurrentUser().getUid())
                .child(taskKey);
        //Priority Dialog is used by more than one activity this lets the global state know which one it should respond to
        Taskoj taskoj = (Taskoj) getApplication();
        taskoj.setPriorityCase(Taskoj.CASE_TASK_EDITOR);
        taskoj.setRepeatCase(Taskoj.CASE_TASK_EDITOR);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskRef.child("name").setValue(name);
                taskRef.child("priority").setValue(priority);
                taskRef.child("projectKey").setValue(projectKey);
                finish();
            }
        });
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Confirm");
        dialog.setMessage("Are you sure you want to delete this task forever");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskRef.removeValue();
                        finish();
                    }
                });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        taskNameEdit.clearFocus();
        taskNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        getWindow().setStatusBarColor(Color.parseColor("#ECEFF1"));
        taskNameEdit.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("name").getValue();
                priority = (long) dataSnapshot.child("priority").getValue();
                projectKey = (String) dataSnapshot.child("projectKey").getValue();
                repeat = (long) dataSnapshot.child("repeat").getValue();
                Log.d("ACT_TASK_EDITOR", name + priority + projectKey + repeat);
                getProjectFromDB();
                updateUI();
                fab.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PROJECT && data != null) {
            this.projectKey = data.getStringExtra("PROJECT_KEY");
            this.projectName = data.getStringExtra("PROJECT_NAME");
            getProjectFromDB();
        }
    }
    void getProjectFromDB() {
        DatabaseReference projectsRef = db
                .child("projects")
                .child(auth.getCurrentUser().getUid())
                .child(this.projectKey);
        projectsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                projectKey = dataSnapshot.getKey();
                projectName = dataSnapshot.child("name").getValue().toString();
                projectColor = dataSnapshot.child("color").getValue().toString();
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    void updateUI() {
        taskNameEdit.setText(this.name);
        taskNameEdit.setSelection(this.name.length());
        priorityText.setText(getPriorityName());
        projectText.setText(this.projectName);
        Log.d("ActTaskEditor", this.projectColor);
        projectText.setTextColor(Color.parseColor(this.projectColor));
    }

    public void updateRepeat(int i) {
        this.repeat = i;
        updateUI();
        repeatDialog.dismiss();

    }

    public void updatePriority(int i) {
        this.priority = i;
        updateUI();
        priorityDialog.dismiss();
    }

    String getPriorityName() {
        String name;
        switch ((int) priority) {
            case 0:
                name = "max";
                break;
            case 1:
                name = "high";
                break;
            case 2:
                name = "normal";
                break;
            case 3:
                name = "low";
                break;
            case 4:
                name = "min";
                break;
            default:
                name = "normal";
        }
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }


}
