package com.adampiziak.bloktree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adampiziak.bloktree.R;

import java.util.ArrayList;
import java.util.List;

public class RenewPicker extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "RENEW_PICKER";

    public static final int NO_RENEW = -1;
    public static final int RENEW_DAILY = 0;
    public static final int RENEW_WEEKLY = 1;

    int renewType = -1;

    boolean dayStates[] = new boolean[7];
    List<TextView> days = new ArrayList<>();

    //Views
    Button actionCreate;

    RadioGroup options;
    LinearLayout fieldDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_renew_picker);

        getWindow().setStatusBarColor(0xFF212121);
        getWindow().setNavigationBarColor(0xFF212121);

        fieldDays = findViewById(R.id.act_renew_picker_field_days);
        options = findViewById(R.id.act_renew_picker_options);
        loadDays();
        actionCreate = findViewById(R.id.act_renew_picker_action_create);
        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                TransitionManager.beginDelayedTransition(fieldDays, new AutoTransition());
                switch (i) {
                    case R.id.act_renew_picker_option_none:
                        renewType = -1;
                        fieldDays.setVisibility(View.GONE);
                        break;
                    case R.id.act_renew_picker_option_daily:
                        renewType = 0;
                        fieldDays.setVisibility(View.GONE);
                        break;
                    case R.id.act_renew_picker_option_weekly:
                        renewType = 1;
                        fieldDays.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        actionCreate.setOnClickListener(this);
        /*

        //Set views
        daily = (Button) findViewById(R.id.act_renew_picker_daily);
        weekly = (Button) findViewById(R.id.act_renew_picker_weekly);
        noRepeat = (Button) findViewById(R.id.act_renew_picker_no_repeat);


        //Set onClickListeners
        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);
        noRepeat.setOnClickListener(this);
        */
    }

    private void loadDays() {
        TextView sun = findViewById(R.id.sunday);
        TextView mon = findViewById(R.id.monday);
        TextView tues = findViewById(R.id.tuesday);
        TextView wed = findViewById(R.id.wednesday);
        TextView thru = findViewById(R.id.thursday);
        TextView fri = findViewById(R.id.friday);
        TextView sat = findViewById(R.id.saturday);
        sun.setOnClickListener(this);
        mon.setOnClickListener(this);
        tues.setOnClickListener(this);
        wed.setOnClickListener(this);
        thru.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        days.add(sun);
        days.add(mon);
        days.add(tues);
        days.add(wed);
        days.add(thru);
        days.add(fri);
        days.add(sat);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        int day = 0;
        switch (id) {
            case R.id.act_renew_picker_action_create:
                selectAndFinish();
                break;
            case R.id.sunday:
                day = 0;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);
                break;
            case R.id.monday:
                day = 1;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);

                break;
            case R.id.tuesday:
                day = 2;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);

                break;
            case R.id.wednesday:
                day = 3;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);

                break;
            case R.id.thursday:
                day = 4;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);

                break;
            case R.id.friday:
                day = 5;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);

                break;
            case R.id.saturday:
                day = 6;
                dayStates[day] = !dayStates[day];
                days.get(day).setTextColor(dayStates[day] ? Color.WHITE : Color.GRAY);
                days.get(day).setBackgroundColor(dayStates[day] ? Color.BLACK : Color.WHITE);

                break;
        }
    }

    public void selectAndFinish() {
        Intent result = new Intent();
        result.putExtra("RENEW_TYPE", renewType);
        String daysActive = "";
        for (int i = 0; i < 7; i++) {
            daysActive += dayStates[i] ? '1' : '0';
        }
        Log.d(TAG, "selectAndFinish: " + daysActive);
        result.putExtra("RENEW_TYPE", renewType);
        result.putExtra("RENEW_DAYS", daysActive);
        setResult(Activity.RESULT_OK, result);
        finish();

    }

}
