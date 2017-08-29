package com.adampiziak.bloktree.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adampiziak.bloktree.Adapters.AdaSubtasks;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.SubTask;
import com.adampiziak.bloktree.Task;
import com.adampiziak.bloktree.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActGroupFullscreen extends AppCompatActivity {

    //Firebase
    DatabaseReference db;
    FirebaseAuth auth;

    //Task
    String key;
    Task task;

    //Project
    Project project;

    //Views
    LinearLayout root;
    TextView name;
    TextView projectText;
    RecyclerView subtasks;
    EditText subTaskInput;
    ImageView edit;
    ImageView start;
    ImageView exit;
    LinearLayout timerContainer;
    ImageView timerToggle;
    ProgressBar progressBar;

    boolean timerActive = false;
    boolean timerToggled = false;
    int time = 0;
    CountDownTimer countDownTimer;

    //Adapter
    AdaSubtasks adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_group_fullscreen);
        syncTask();
        bindUI();
        goFullScreen();


        getWindow().setStatusBarColor(Color.parseColor("#303F9F"));
    }

    void syncTask() {
        String taskKey = getIntent().getStringExtra("TASK_KEY");
        if (taskKey != null) {
            key = taskKey;
        } else {
            executeError();
            return;
        }
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        db.child("tasks")
          .child(auth.getCurrentUser().getUid())
          .child(taskKey)
          .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  task = Tools.createTaskFromSnapshot(dataSnapshot);
                  updateUI();
                  syncProject();
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });
    }

    void syncProject() {
        db.child("projects")
          .child(auth.getCurrentUser().getUid())
          .child(task.getProjectKey())
          .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  project = Tools.createProjectFromSnapshot(dataSnapshot);
                  updateUI();
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });
    }

    void updateUI() {
        name.setText(task.getName());
        adapter = new AdaSubtasks(task.getSubtasks(), task.getKey());
        subtasks.setLayoutManager(new LinearLayoutManager(this));
        subtasks.setAdapter(adapter);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (task != null) {
            subTaskInput.setOnEditorActionListener(createTextActionListener());
            final Context context = this;
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActTaskEditor.class);
                    intent.putExtra("TASK_KEY", task.getKey());
                    startActivity(intent);
                }
            });

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionManager.beginDelayedTransition(root, (new AutoTransition()).setOrdering(TransitionSet.ORDERING_TOGETHER));
                    timerActive = !timerActive;
                    timerContainer.setVisibility((timerActive) ? View.VISIBLE : View.GONE);
                    start.setImageDrawable((timerActive) ? getDrawable(R.drawable.ic_keyboard_arrow_up_white_24px) : getDrawable(R.drawable.ic_hourglass_empty_white_24px));

                }
            });

            progressBar.setProgress(time);
            countDownTimer = new CountDownTimer(10000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    time++;
                    progressBar.setProgress((int) time*100/(10000/1));
                }

                @Override
                public void onFinish() {
                    progressBar.setProgress(100);
                }
            };

            timerToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timerToggled = !timerToggled;
                    timerToggle.setImageDrawable((timerToggled) ? getDrawable(R.drawable.ic_play_arrow_white_24px) : getDrawable(R.drawable.ic_pause_white_24px));
                    if (timerToggled) {
                        countDownTimer.start();
                    }
                }
            });



        }


        if (project != null) {
            projectText.setText(project.getName());
            int color = Color.parseColor(project.getColor());
            projectText.setTextColor(color);
            root.setBackground(Tools.createGradientDrawableTP(color));

            int color1 = Tools.createBrighterColor(color);
            int color2 = Tools.createSimiliarColor(color1);

            getWindow().setStatusBarColor((color2 > color1) ? color1 : color2);
        }

    }
    private void exitFullScreen() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
    }
    private void goFullScreen() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    void bindUI() {
        root = (LinearLayout) findViewById(R.id.group_editor_root);
        name = (TextView) findViewById(R.id.group_editor_name);
        projectText = (TextView) findViewById(R.id.group_editor_project);
        subtasks = (RecyclerView) findViewById(R.id.group_editor_rv);
        subTaskInput = (EditText) findViewById(R.id.group_editor_subtask_input);
        edit = (ImageView) findViewById(R.id.group_fullscreen_edit);
        start = (ImageView) findViewById(R.id.group_fullscreen_start);
        exit = (ImageView) findViewById(R.id.group_fullscreen_exit);
        timerContainer = (LinearLayout) findViewById(R.id.timer_container);
        timerToggle = (ImageView) findViewById(R.id.toggle_timer);
        progressBar = (ProgressBar) findViewById(R.id.group_fullscreen_progress);
    }

    TextView.OnEditorActionListener createTextActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SubTask subTask = new SubTask();
                    subTask.setName(subTaskInput.getText().toString());
                    db.child("tasks").child(auth.getCurrentUser().getUid()).child(key).child("subTasks").push().setValue(subTask);
                    subTaskInput.setText("");
                }
                return true;
            }
        };
    }

    void executeError() {
        Toast.makeText(this, "Could not retrieve task key", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

}
