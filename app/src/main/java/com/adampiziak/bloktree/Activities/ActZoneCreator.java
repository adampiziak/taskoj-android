package com.adampiziak.bloktree.Activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adampiziak.bloktree.DialogTimeEnum;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Zone;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ActZoneCreator extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_COLOR = 142;
    private final int REQUEST_RENEW = 134;

    //Zone data
    String name = "";
    String color = "#000000";
    boolean exclude = false;
    long startTime = 0;
    long endTime = 0;
    int renew = -1;

    //Views
    LinearLayout fieldColor;
    LinearLayout fieldRenew;
    TextView textStartTime;
    TextView textEndTime;
    Button actionCreate;
    EditText inputName;

    //Database
    DatabaseReference ref;
    FirebaseAuth auth;

    @Override
    public void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.act_zone_creator);
        getWindow().setStatusBarColor(0xFF2E7D32);

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        inputName = (EditText) findViewById(R.id.act_zone_creator_input_name);
        textStartTime = (TextView) findViewById(R.id.act_zone_creator_field_time_start);
        textEndTime = (TextView) findViewById(R.id.act_zone_creator_field_time_end);
        fieldColor = (LinearLayout) findViewById(R.id.act_zone_creator_field_color);
        actionCreate = (Button) findViewById(R.id.act_zone_creator_action_create);
        fieldRenew = (LinearLayout) findViewById(R.id.act_zone_creator_field_renew);

        textStartTime.setOnClickListener(this);
        textEndTime.setOnClickListener(this);
        fieldColor.setOnClickListener(this);
        actionCreate.setOnClickListener(this);
        fieldRenew.setOnClickListener(this);

        init();
    }

    private void createZone() {
        name = inputName.getText().toString();
        Zone zone = new Zone(name, color, startTime, endTime, renew);
        ref.child("zones").child(auth.getCurrentUser().getUid()).push().setValue(zone);
    }

    private void init() {
        Calendar cal = Calendar.getInstance();
        startTime = cal.getTimeInMillis();
        textStartTime.setText(cal.getTime().toString());
        cal.add(Calendar.HOUR_OF_DAY, 1);
        endTime = cal.getTimeInMillis();
        textEndTime.setText(cal.getTime().toString());

    }

    void updateViews() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        textStartTime.setText(cal.getTime().toString());
        cal.setTimeInMillis(endTime);
        textEndTime.setText(cal.getTime().toString());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.act_zone_creator_field_color:
                Intent intent = new Intent(this, ActColorPicker.class);
                startActivityForResult(intent, REQUEST_COLOR);
                break;
            case R.id.act_zone_creator_field_time_start:
                TimePickerFragment timeStart = new TimePickerFragment();
                timeStart.setTimeType(DialogTimeEnum.START);
                timeStart.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.act_zone_creator_field_time_end:
                TimePickerFragment timeEnd = new TimePickerFragment();
                timeEnd.setTimeType(DialogTimeEnum.END);
                timeEnd.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.act_zone_creator_action_create:
                createZone();
                finish();
                break;
            case R.id.act_zone_creator_field_renew:
                Intent renewPicker = new Intent(this, RenewPicker.class);
                startActivityForResult(renewPicker, REQUEST_RENEW);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case REQUEST_COLOR:
                    int colorHex = data.getIntExtra("COLOR", 0xFF000000);
                    color = String.format("#%06X", 0xFFFFFF & colorHex);
                    fieldColor.setBackgroundColor(colorHex);
                    break;
                case REQUEST_RENEW:
                    int renew = data.getIntExtra("RENEW_TYPE", -1);
                    break;
            }
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {
        private int timeType = 0;

        public void setTimeType(DialogTimeEnum time) {
            switch (time) {
                case START:
                    timeType = 0;
                    break;
                case END:
                    timeType = 1;
                    break;
            }
        }

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
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, minute);
            ActZoneCreator zoneCreator = ((ActZoneCreator) getContext());
            if (timeType == 0)
                zoneCreator.startTime = cal.getTimeInMillis();
            else
                zoneCreator.endTime = cal.getTimeInMillis();
            zoneCreator.updateViews();

        }
    }
}
