package com.adampiziak.bloktree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.adampiziak.bloktree.R;

public class ActDurationPicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_duration_picker);

        Button button = (Button) findViewById(R.id.act_dur_button);
        Button noDuration = (Button) findViewById(R.id.act_dur_no_duration);
        int value = getIntent().getIntExtra("VALUE", 0) / 5;


        String[] nums = new String[144];
        for (int i = 0; i < 144; i++) {
            int a = i * 5;
            nums[i] = Integer.toString(a);
        }

        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.act_dur_number_picker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(143);
        numberPicker.setDisplayedValues(nums);
        numberPicker.setValue(value);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minutes = numberPicker.getValue() * 5;
                Intent result = new Intent();
                result.putExtra("MINUTES", minutes);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
        noDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("MINUTES", 0);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
    }
}
