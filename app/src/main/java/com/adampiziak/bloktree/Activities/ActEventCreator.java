package com.adampiziak.bloktree.Activities;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adampiziak.bloktree.Event;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
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
import java.util.Locale;

public class ActEventCreator extends AppCompatActivity implements View.OnClickListener {

    //Request codes
    final int REQUEST_PROJECT = 10;
    final int REQUEST_RENEW_TYPE = 11;

    //Context
    Context context = this;

    //Event data
    String projectKey = "default";
    String projectName;

    long timeStart = 0;
    long timeEnd = 0;
    int startHour = 0;
    int startMinute = 0;
    int endHour = 0;
    int endMinute = 0;
    int renewType = -1;
    String renewDays = "0000000";

    //Database
    DatabaseReference ref;
    FirebaseAuth auth;

    //Views
    LinearLayout fieldProject;
    LinearLayout fieldRenew;
    TextView date;

    TextView actionSelectStartTime;
    TextView actionSelectEndTime;
    TextView actionSelectStartDate;
    TextView actionSelectEndDate;

    TextView textProject;
    TextView textRenew;
    EditText eventName;
    FloatingActionButton fab;
    Toolbar toolbar;
    FrameLayout toolbarColor;

    //Data
    List<Project> projects = new ArrayList<>();

    //Lifecycle events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_event_creator);

        syncProjects();

        //Get intent extras
        startHour = getIntent().getIntExtra("HOUR_OF_DAY", 8);
        endHour = startHour + 1;
        Calendar cal = Calendar.getInstance();
        String displayDayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        String displaymonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int displayDay = cal.get(Calendar.DAY_OF_MONTH);

        //Get database reference
        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        //Date and time views
        actionSelectStartDate = findViewById(R.id.act_event_creator_action_select_start_date);
        actionSelectEndDate = findViewById(R.id.act_event_creator_action_select_end_date);
        actionSelectStartTime = findViewById(R.id.act_event_creator_action_select_start_time);
        actionSelectEndTime = findViewById(R.id.act_event_creator_action_select_end_time);

        //Get views
        eventName = (EditText) findViewById(R.id.act_event_creator_name);
        fieldProject = (LinearLayout) findViewById(R.id.act_event_creator_project);
        textProject = (TextView) findViewById(R.id.act_event_creator_project_text);
        fab = (FloatingActionButton) findViewById(R.id.act_event_creator_fab);
        fieldRenew = (LinearLayout) findViewById(R.id.act_event_creator_repeat);
        textRenew = (TextView) findViewById(R.id.act_event_creator_repeat_text);
        toolbar = findViewById(R.id.act_event_creator_toolbar);
        toolbarColor = findViewById(R.id.toolbar_color_placeholder);

        //Set times for timepickers
        actionSelectStartTime.setText(startHour + ":00");
        actionSelectEndTime.setText((startHour+1) + ":00");
        String dateDisplay = displayDayOfWeek + ", " + displaymonth + " " + displayDay;
        actionSelectStartDate.setText(dateDisplay);
        actionSelectEndDate.setText(dateDisplay);

        //Set status bar background color
        getWindow().setStatusBarColor(0xFF1976D2);

        //Set listeners
        fieldProject.setOnClickListener(this);
        fab.setOnClickListener(this);
        actionSelectStartDate.setOnClickListener(this);
        actionSelectEndDate.setOnClickListener(this);
        actionSelectStartTime.setOnClickListener(this);
        actionSelectEndTime.setOnClickListener(this);
        fieldRenew.setOnClickListener(this);

        setInitialTime();
    }

    private void setInitialTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, startHour);
        cal.set(Calendar.HOUR_OF_DAY, startMinute);
        timeStart = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, 1);
        timeEnd = cal.getTimeInMillis();
        updateDatesAndTimes();
    }

    public void updateDatesAndTimes() {
        //configure calendar instances
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(timeStart);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(timeEnd);

        ////start date
        String startDayOfWeek = start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        String startMonth = start.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        int startDay = start.get(Calendar.DAY_OF_MONTH);
        int startYear = start.get(Calendar.YEAR);
        String startDate = startDayOfWeek + ", " + startMonth + " " + startDay + ", " + startYear;
        ////end date
        String endDayOfWeek = end.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        String endMonth = end.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        int endDay = end.get(Calendar.DAY_OF_MONTH);
        int endYear = end.get(Calendar.YEAR);
        String endDate = endDayOfWeek + ", " + endMonth + " " + endDay + ", " + endYear;

        //start time
        int startHour = start.get(Calendar.HOUR);
        int startMinute = start.get(Calendar.MINUTE);
        String startMinuteDisplay = (startMinute > 9) ? String.valueOf(startMinute) : "0" + startMinute;
        String startAMPM = start.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());

        //end time
        int endHour = end.get(Calendar.HOUR);
        int endMinute = start.get(Calendar.MINUTE);
        String endMinuteDisplay = (endMinute > 9) ? String.valueOf(endMinute) : "0" + endMinute;
        String endAMPM = end.getDisplayName(Calendar.AM_PM, Calendar.SHORT, Locale.getDefault());

        //update views
        actionSelectStartTime.setText(startHour + ":" + startMinuteDisplay + " " + startAMPM);
        actionSelectStartDate.setText(startDate);
        actionSelectEndDate.setText(endDate);
        actionSelectEndTime.setText(endHour + ":" + endMinuteDisplay + " " + endAMPM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_PROJECT:
                    String projectKeyTemp = data.getStringExtra("PROJECT_KEY");
                    int tempColor = Color.parseColor(getProject(this.projectKey).getColor());
                    this.projectKey = projectKeyTemp;
                    this.projectName = data.getStringExtra("PROJECT_NAME");
                    Project project = getProject(projectKeyTemp);
                    toolbarColor.setBackgroundColor(tempColor);
                    setToolBarColor(Color.parseColor(project.getColor()));
                    textProject.setText(Tools.capitalizeSentence(projectName));
                    break;
                case REQUEST_RENEW_TYPE:
                    this.renewType = data.getIntExtra("RENEW_TYPE", -1);
                    this.renewDays = data.getStringExtra("RENEW_DAYS");
                    switch (renewType) {
                        case -1:
                            textRenew.setText("Do not repeat");
                            break;
                        case 0:
                            textRenew.setText("Repeat daily");
                            break;
                        case 1:
                            textRenew.setText("Repeat weekly " + getRenewDaysDisplay());
                            break;
                    }
                    break;
            }
        }
    }

    private String getRenewDaysDisplay() {
        String[] days = {"sun", "mon", "tues", "wed", "thru", "fri", "sat"};
        String displayText = "  ( ";
        for (int i = 0; i < 7; i++) {
            if (renewDays.charAt(i) == '1') {
                displayText += days[i] + " ";
            }
        }
        return displayText + ")";
    }

    private void setToolBarColor(final int color) {
        int cx = toolbar.getWidth() / 2;
        int cy = toolbar.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        toolbar.setBackgroundColor(Color.BLACK);
        Animator anim = ViewAnimationUtils.createCircularReveal(toolbar, cx, cy, 0, finalRadius);
        toolbar.setBackgroundColor(color);
        anim.setDuration(360);
        anim.start();
        Window window = getWindow();
        window.setStatusBarColor(Tools.createDarkerColor(color));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.act_event_creator_project:
                Intent intent = new Intent(context, ActProjectPicker.class);
                startActivityForResult(intent, REQUEST_PROJECT);
                break;
            case R.id.act_event_creator_fab:
                int duration = ((endHour - startHour) * 60) + (endMinute - startMinute);

                Calendar c = Calendar.getInstance();

                Event event = new Event();
                event.setName(eventName.getText().toString());
                event.setTimeStart(timeStart);
                event.setTimeEnd(timeEnd);
                event.setProjectKey(projectKey);
                event.setRenewType(renewType);
                event.setRenewDays(renewDays);
                ref.child("events").child(auth.getCurrentUser().getUid()).push().setValue(event);
                finish();
                break;
            case R.id.act_event_creator_action_select_start_date:
                DatePickerFragment selectStartDate = new DatePickerFragment();
                selectStartDate.updateAction(DatePickerFragment.ACTION_UPDATE_START_DATE);
                selectStartDate.show(getSupportFragmentManager(), "DatePicker");
                break;
            case R.id.act_event_creator_action_select_end_date:
                DatePickerFragment selectEndDate = new DatePickerFragment();
                selectEndDate.updateAction(DatePickerFragment.ACTION_UPDATE_END_DATE);
                selectEndDate.show(getSupportFragmentManager(), "DatePicker");
                break;
            case R.id.act_event_creator_action_select_start_time:
                TimePickerFragment selectStartTime = new TimePickerFragment();
                selectStartTime.updateAction(TimePickerFragment.ACTION_UPDATE_START_TIME);
                selectStartTime.show(getSupportFragmentManager(), "TimePicker");
                break;
            case R.id.act_event_creator_action_select_end_time:
                TimePickerFragment selectEndTime = new TimePickerFragment();
                selectEndTime.updateAction(TimePickerFragment.ACTION_UPDATE_END_TIME);
                selectEndTime.show(getSupportFragmentManager(), "TimePicker");
                break;
            /*
            case R.id.act_event_creator_date:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "DatePicker");
                break;
            case R.id.act_event_creator_timeStart:
                DialogFragment startTimePicker = new TimePickerFragment();
                startTimePicker.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.act_event_creator_timeEnd:
                TimePickerFragment fragment = new TimePickerFragment();
                fragment.timeStart = false;
                fragment.show(getSupportFragmentManager(), "timePicker");
                break;
                */
            case R.id.act_event_creator_repeat:
                Intent intentRenew = new Intent(context, RenewPicker.class);
                startActivityForResult(intentRenew, REQUEST_RENEW_TYPE);
                break;

        }
    }



    //Date and time pickers

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public static final int NO_ACTION = -1;
        public static final int ACTION_UPDATE_START_DATE = 0;
        public static final int ACTION_UPDATE_END_DATE = 1;

        private int action = NO_ACTION;

        public void updateAction(int action) {
            this.action = action;
        }

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

        public void onDateSet(DatePicker view, int y, int m, int d) {
            // Do something with the date chosen by the user
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, y);
            c.set(Calendar.MONTH, m);
            c.set(Calendar.DAY_OF_MONTH, d);


            ActEventCreator eventCreator = (ActEventCreator) getContext();
            switch (action) {
                case ACTION_UPDATE_START_DATE:
                    Calendar startTimes = Calendar.getInstance();
                    startTimes.setTimeInMillis(eventCreator.timeStart);
                    c.set(Calendar.HOUR_OF_DAY, startTimes.get(Calendar.HOUR_OF_DAY));
                    c.set(Calendar.MINUTE, startTimes.get(Calendar.MINUTE));
                    eventCreator.timeStart = c.getTimeInMillis();
                    eventCreator.updateDatesAndTimes();
                    break;
                case ACTION_UPDATE_END_DATE:
                    Calendar endTimes = Calendar.getInstance();
                    endTimes.setTimeInMillis(eventCreator.timeEnd);
                    c.set(Calendar.HOUR_OF_DAY, endTimes.get(Calendar.HOUR_OF_DAY));
                    c.set(Calendar.MINUTE, endTimes.get(Calendar.MINUTE));
                    eventCreator.timeEnd = c.getTimeInMillis();
                    eventCreator.updateDatesAndTimes();
                    break;
            }

        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public static final int NO_ACTION = -1;
        public static final int ACTION_UPDATE_START_TIME = 0;
        public static final int ACTION_UPDATE_END_TIME = 1;

        public int action = NO_ACTION;

        public void updateAction(int action) {
            this.action = action;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            int hour = ((ActEventCreator) getContext()).startHour;
            return new TimePickerDialog(getActivity(), this, hour, 0,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            ActEventCreator eventCreator = (ActEventCreator) getContext();
            Calendar date = Calendar.getInstance();
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            switch (action) {
                case ACTION_UPDATE_START_TIME:
                    date.setTimeInMillis(eventCreator.timeStart);
                    time.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    time.set(Calendar.MONTH, date.get(Calendar.MONTH));
                    time.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                    eventCreator.timeStart = time.getTimeInMillis();
                    eventCreator.updateDatesAndTimes();
                    break;
                case ACTION_UPDATE_END_TIME:
                    date.setTimeInMillis(eventCreator.timeEnd);
                    time.set(Calendar.YEAR, date.get(Calendar.YEAR));
                    time.set(Calendar.MONTH, date.get(Calendar.MONTH));
                    time.set(Calendar.DAY_OF_MONTH, date.get(Calendar.DAY_OF_MONTH));
                    eventCreator.timeEnd = time.getTimeInMillis();
                    eventCreator.updateDatesAndTimes();
                    break;
            }




        }
    }

    private void syncProjects() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        ref.child("projects").child(auth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Project project = Tools.createProjectFromSnapshot(dataSnapshot);
                projects.add(project);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Project project = Tools.createProjectFromSnapshot(dataSnapshot);
                boolean found = false;
                for (int i = 0; i < projects.size(); i++) {
                    if (projects.get(i).getKey().equals(dataSnapshot.getKey())) {
                        projects.set(i, project);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    projects.add(project);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (int i = 0; i < projects.size(); i++) {
                    if (projects.get(i).getKey().equals(dataSnapshot.getKey())) {
                        projects.remove(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private Project getProject(String key) {
        Project project = new Project();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getKey().equals(key)) {
                project = projects.get(i);
                break;
            }
        }
        return project;
    }
}
