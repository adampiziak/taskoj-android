package com.adampiziak.bloktree.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adampiziak.bloktree.Event;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
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
    int year = 0;
    int month = 0;
    int day = 0;
    int hour = 0;
    int minute = 0;
    int startHour = 0;
    int startMinute = 0;
    int endHour = 0;
    int endMinute = 0;
    int renewType = -1;

    //Database
    DatabaseReference ref;
    FirebaseAuth auth;

    //Views
    LinearLayout fieldProject;
    LinearLayout fieldRenew;
    TextView date;
    TextView timeStart;
    TextView timeEnd;
    TextView textProject;
    TextView textRenew;
    EditText eventName;
    FloatingActionButton fab;

    //Lifecycle events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_event_creator);

        //Get intent extras
        hour = getIntent().getIntExtra("HOUR_OF_DAY", 0);
        startHour = hour;
        endHour = hour + 1;

        //Get database reference
        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        //Get views
        eventName = (EditText) findViewById(R.id.act_event_creator_name);
        fieldProject = (LinearLayout) findViewById(R.id.act_event_creator_project);
        textProject = (TextView) findViewById(R.id.act_event_creator_project_text);
        timeStart = (TextView) findViewById(R.id.act_event_creator_timeStart);
        timeEnd = (TextView) findViewById(R.id.act_event_creator_timeEnd);
        fab = (FloatingActionButton) findViewById(R.id.act_event_creator_fab);
        date = (TextView) findViewById(R.id.act_event_creator_date);
        fieldRenew = (LinearLayout) findViewById(R.id.act_event_creator_repeat);
        textRenew = (TextView) findViewById(R.id.act_event_creator_repeat_text);

        //Set times for timepickers
        timeStart.setText(hour + ":00");
        timeEnd.setText((hour+1) + ":00");

        //Set status bar background color
        getWindow().setStatusBarColor(0xFF1976D2);

        //Set listeners
        fieldProject.setOnClickListener(this);
        fab.setOnClickListener(this);
        date.setOnClickListener(this);
        timeStart.setOnClickListener(this);
        timeEnd.setOnClickListener(this);
        fieldRenew.setOnClickListener(this);

        //
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_PROJECT:
                    this.projectKey = data.getStringExtra("PROJECT_KEY");
                    this.projectName = data.getStringExtra("PROJECT_NAME");
                    textProject.setText(Tools.capitalizeSentence(projectName));
                    break;
                case REQUEST_RENEW_TYPE:
                    this.renewType = data.getIntExtra("RENEW_TYPE", -1);
                    switch (renewType) {
                        case -1:
                            textRenew.setText("Do not repeat");
                            break;
                        case 0:
                            textRenew.setText("Repeat daily");
                            break;
                        case 1:
                            textRenew.setText("Repeat weekly");
                            break;
                    }
                    break;
            }
        }
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
                event.setYear(year);
                event.setMonth(month);
                event.setDay(day);
                event.setHour(hour);
                event.setDuration(duration);
                event.setMinute(minute);
                event.setProjectKey(projectKey);
                event.setRenewType(renewType);
                ref.child("events").child(auth.getCurrentUser().getUid()).push().setValue(event);
                finish();
                break;
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
            case R.id.act_event_creator_repeat:
                Intent intentRenew = new Intent(context, RenewPicker.class);
                startActivityForResult(intentRenew, REQUEST_RENEW_TYPE);
                break;
        }
    }



    //Date and time pickers

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

        public void onDateSet(DatePicker view, int y, int m, int d) {
            // Do something with the date chosen by the user
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, y);
            c.set(Calendar.MONTH, m);
            c.set(Calendar.DAY_OF_MONTH, d);

            String dayOfWeek = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            String monthOfYear = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            ActEventCreator eventCreator = (ActEventCreator) getContext();
            eventCreator.year = y;
            eventCreator.month = m;
            eventCreator.day = d;
            eventCreator.date.setText(dayOfWeek + ", " + monthOfYear + " " + d + ", " + y);

        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        public boolean timeStart = true;


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of TimePickerDialog and return it
            int hour = ((ActEventCreator) getContext()).hour;
            if (!timeStart)
                hour += 1;
            return new TimePickerDialog(getActivity(), this, hour, 0,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            ActEventCreator eventCreator = (ActEventCreator) getContext();
            String period = (hourOfDay < 12) ? "AM" : "PM";
            eventCreator.hour = hourOfDay;
            eventCreator.minute = minute;
            if (timeStart) {
                eventCreator.startHour = hourOfDay;
                eventCreator.startMinute = minute;
            } else {
                eventCreator.endHour = hourOfDay;
                eventCreator.endMinute = minute;
            }

            int tempHour = (hourOfDay > 12) ? (hourOfDay - 12) : hourOfDay;
            String min = (minute > 9) ? String.valueOf(minute) : "0" + minute;
            if (timeStart)
                eventCreator.timeStart.setText(tempHour + ":" + min + " " + period);
            else
                eventCreator.timeEnd.setText(tempHour + ":" + min + " " + period);

        }
    }

}
