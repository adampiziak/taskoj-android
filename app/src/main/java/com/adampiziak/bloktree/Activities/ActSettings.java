package com.adampiziak.bloktree.Activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.TimeZone;

public class ActSettings extends AppCompatActivity {

    static int hour = 5;
    static int minute = 0;
    TextView timeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_settings);
        getWindow().setStatusBarColor(Color.parseColor("#424242"));
        timeField = (TextView) findViewById(R.id.settings_time_reset);
        LinearLayout dateField = (LinearLayout) findViewById(R.id.settings_date_picker);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        db.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hour = Integer.valueOf(dataSnapshot.child("resetHour").getValue().toString());
                minute = Integer.valueOf(dataSnapshot.child("resetMinute").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment fragment = new SettingsTimePicker();
                fragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
        updateTimeUI();
    }

    public void updateTimeUI() {
        String period = (hour > 12) ? " PM" : " AM";
        String time = (hour > 12) ? String.valueOf(hour - 12) : String.valueOf(hour);
        String timeText = time + period + "   " + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT).toString();
        timeField.setText(timeText);
    }

    public static class SettingsTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            /*
            final Calendar c = Calendar.getInstance();
            int fragmentHour = c.get(Calendar.HOUR_OF_DAY);
            int fragmentMinute = c.get(Calendar.MINUTE);
            */

            return new TimePickerDialog(getActivity(), this, hour, minute,  DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
            hour = hourOfDay;
            minute = min;
            ((ActSettings) getActivity()).updateTimeUI();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            db.getReference().child("users").child(auth.getCurrentUser().getUid()).child("resetHour").setValue(hour);
            db.getReference().child("users").child(auth.getCurrentUser().getUid()).child("resetMinute").setValue(minute);
        }


    }
}
