package com.adampiziak.bloktree.Activities;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adampiziak.bloktree.Adapters.AdaCreatorSubTasks;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.Dialogs.DiaPriorityPicker;
import com.adampiziak.bloktree.Dialogs.DiaRepeatPicker;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.SubTask;
import com.adampiziak.bloktree.Task;
import com.adampiziak.bloktree.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActTaskCreator extends AppCompatActivity implements View.OnClickListener {

    final int REQUEST_PROJECT = 10;
    final int REQUEST_DURATION = 20;

    private Context                mContext;

    private int priority = 2;
    private String projectKey = "default";
    private String projectName = "general";
    private long repeat = Task.NO_REPEAT;
    private int duration = 0;
    private long dueDate = 0;

    FirebaseDatabase db;
    FirebaseAuth auth;

    DiaPriorityPicker mDialogPriority;
    DiaRepeatPicker repeatDialog;

    //Task data<<
    private ArrayList<SubTask> subTasks = new ArrayList<>();

    //Project data
    private List<Project> projects = new ArrayList<>();
    //Views
    private TextView               mActionCreateTask;
    private Button                 mActionCancelTask;
    private EditText               mInputTaskName;
    private LinearLayout           mViewFieldGroup;
    private ImageView              mImageProject;
    private TextView               mTextProject;
    private Toolbar                mToolbar;
    private TextView               mTextPriority;
    private TextView               textRepeat;
    private LinearLayout           mViewFieldPriority;
    private EditText               mInputSubTask;
    private RecyclerView           mViewSubTaskRV;
    private LinearLayout           mViewFieldRepeat;
    private LinearLayout           fieldDuration;
    private TextView               textDuration;
    private LinearLayout           fieldDate;
    private TextView               textDate;



    //Lifecycle events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_task_creator);

        //Get Application State
        final Taskoj taskoj = ((Taskoj) getApplication());
        taskoj.setPriorityCase(0);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        mContext = this;

        //Assign Views
        mToolbar = (Toolbar) findViewById(R.id.task_creator_toolbar);
        mViewFieldGroup = (LinearLayout) findViewById(R.id.creator_field_project);
        mViewFieldRepeat = (LinearLayout) findViewById(R.id.activity_task_creator_repeat);

        mImageProject = (ImageView) findViewById(R.id.group_field_icon);
        mInputTaskName = (EditText) findViewById(R.id.activity_task_creator_toolbar_task_name);
        mInputSubTask = (EditText) findViewById(R.id.task_creator_subtask_input);
        mTextProject = (TextView) findViewById(R.id.project_field_text);
        mTextPriority = (TextView) findViewById(R.id.activity_task_creator_priority_text);
        mViewFieldPriority = (LinearLayout) findViewById(R.id.activity_task_creator_priority);
        mActionCreateTask = (TextView) findViewById(R.id.a_task_creator_action_create);
        mActionCancelTask = (Button) findViewById(R.id.task_creator_app_bar_cancel);
        mViewSubTaskRV = (RecyclerView) findViewById(R.id.task_creator_subtask_rv);
        textRepeat = (TextView) findViewById(R.id.activity_task_creator_repeat_text);
        fieldDuration = (LinearLayout) findViewById(R.id.activity_task_creator_duration);
        textDuration = (TextView) findViewById(R.id.activity_task_creator_duration_text);
        fieldDate = (LinearLayout) findViewById(R.id.activity_task_creator_date);
        textDate = (TextView) findViewById(R.id.activity_task_creator_date_text);


        //Set RecyclerView
        mViewSubTaskRV.setLayoutManager(new LinearLayoutManager(getApplication()));
        mViewSubTaskRV.setAdapter(new AdaCreatorSubTasks());
        mViewSubTaskRV.setNestedScrollingEnabled(false);


        //Transition
        Window window = getWindow();
        window.setEnterTransition(new Slide());
        window.setStatusBarColor(Color.parseColor("#455A64"));
                //(ResourcesCompat.getColor(getResources(), R.color.colorMainDark, null));


        //Set Toolbar
        setSupportActionBar(mToolbar);

        mActionCreateTask.setOnClickListener(this);
        mViewFieldPriority.setOnClickListener(this);
        mViewFieldGroup.setOnClickListener(this);
        mViewFieldRepeat.setOnClickListener(this);

        final Context context = this;

        fieldDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new DatePickerFragment();
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        fieldDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActDurationPicker.class);
                intent.putExtra("VALUE", duration);
                startActivityForResult(intent, REQUEST_DURATION);
            }
        });

        mInputSubTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SubTask subTask = new SubTask();
                    subTask.setName(mInputSubTask.getText().toString());
                    subTasks.add(subTask);
                    ((AdaCreatorSubTasks) mViewSubTaskRV.getAdapter()).add(subTask);
                    mInputSubTask.setText("");
                }
                return true;
            }
        });

        db.getReference().child("projects").child(auth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Project project = dataSnapshot.getValue(Project.class);
                project.setKey(dataSnapshot.getKey());
                projects.add(project);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_PROJECT:
                    this.projectKey = data.getStringExtra("PROJECT_KEY");
                    this.projectName = data.getStringExtra("PROJECT_NAME");
                    this.mTextProject.setText(this.projectName.substring(0, 1).toUpperCase() + this.projectName.substring(1));
                    int projectColor = Color.BLUE;
                    for (Project project : projects) {
                        if (project.getKey().equals(this.projectKey)) {
                            projectColor = Color.parseColor(project.getColor());
                            break;
                        }
                    }
                    setToolBarColor(projectColor);
                    break;
                case REQUEST_DURATION: {
                    this.duration = data.getIntExtra("MINUTES", -1);
                    if (this.duration == 0)
                        textDuration.setText("");
                    else
                        textDuration.setText(String.valueOf(this.duration) + " minutes");
                }
            }
        }
    }


    private void setToolBarColor(final int color) {
        int cx = mToolbar.getWidth() / 2;
        int cy = mToolbar.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(mToolbar, cx, cy, 0, finalRadius);
        mToolbar.setBackgroundColor(color);
        anim.start();
        Window window = getWindow();
        window.setStatusBarColor(Tools.createDarkerColor(color));
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.a_task_creator_action_create:
                String name = mInputTaskName.getText().toString();
                Task task;
                if (subTasks.size() > 0)
                    task = new Task(name, this.priority, this.projectKey, this.subTasks);
                else
                    task = new Task(name, this.priority, this.projectKey);
                Log.d("TASK_SCAN", task.getName() + task.getPriority() + task.getProjectKey());
                createTask(task);
                break;
            case R.id.activity_task_creator_priority:
                mDialogPriority = new DiaPriorityPicker();
                mDialogPriority.show(getSupportFragmentManager(), "priority_dialog");
                break;
            case R.id.activity_task_creator_repeat:
                repeatDialog = new DiaRepeatPicker();
                repeatDialog.show(getSupportFragmentManager(), "repeat_dialog");
                break;
            case R.id.creator_field_project:
                Intent i = new Intent(this, ActProjectPicker.class);
                startActivityForResult(i, REQUEST_PROJECT);
        }

    }

    public void updatePriority(int priority) {
        this.priority = priority;
        mTextPriority.setText(getPriorityName(priority));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialogPriority.dismiss();
            }
        }, 150);
    }

    public void updateRepeat(long value) {
        this.repeat = value;
        String text;
        switch ((int) repeat) {
            case (int) Task.NO_REPEAT:
                text = "Do not repeat";
                break;
            case (int) Task.REPEAT_DAILY:
                text = "Repeat daily";
                break;
            default:
                text = "ERROR_UPDATE_REPEAT";
        }
        textRepeat.setText(text);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                repeatDialog.dismiss();
            }
        }, 150);


    }

    private void createTask(Task task) {
        task.setRepeat(this.repeat);
        task.setDuration(this.duration);
        task.setDueDate(this.dueDate);
        DatabaseReference pushRef = db
                .getReference()
                .child("tasks")
                .child(auth.getCurrentUser().getUid());
        String pushKey = pushRef.push().getKey();
        pushRef.child(pushKey).setValue(task);
        for (int i = 0; i < subTasks.size(); i++) {
            pushRef.child(pushKey).child("subTasks").push().setValue(subTasks.get(i));
        }
        finish();
    }


    String getPriorityName(int priority) {
        String name;
        switch (priority) {
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

    public int cYear = 0;
    public int cMonth = 0;
    public int cDay = 0;
    public int cHour = 0;
    public int cMinute = 0;

    void setDueDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, cYear);
        c.set(Calendar.MONTH, cMonth);
        c.set(Calendar.DAY_OF_MONTH, cDay);
        c.set(Calendar.HOUR_OF_DAY, cHour);
        c.set(Calendar.MINUTE, cMinute);

        this.dueDate = c.getTimeInMillis();
        textDate.setText(c.getTime().toString());

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            ActTaskCreator creator = ((ActTaskCreator) getContext());
            creator.cYear = year;
            creator.cMonth = month;
            creator.cDay = day;
            DialogFragment time = new TimePickerFragment();
            time.show(getActivity().getSupportFragmentManager(), "timePicker");
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            ActTaskCreator creator = ((ActTaskCreator) getContext());
            creator.cHour = hourOfDay;
            creator.cMinute = minute;
            creator.setDueDate();
        }
    }
}
