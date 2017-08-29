package com.adampiziak.bloktree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adampiziak.bloktree.R;

public class ActColorPicker extends AppCompatActivity implements View.OnClickListener {

    int color = 0xFF536DFE;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.act_color_picker);
        Button blue = (Button) findViewById(R.id.act_color_picker_blue);
        Button red = (Button) findViewById(R.id.act_color_picker_red);
        blue.setOnClickListener(this);
        red.setOnClickListener(this);
     }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.act_color_picker_blue:
                color = 0xFF536DFE;
                selectAndFinish();
                break;
            case R.id.act_color_picker_red:
                color = 0xFFf44336;
                selectAndFinish();
                break;
        }
    }

    public void selectAndFinish() {
        Intent result = new Intent();
        result.putExtra("COLOR", color);
        setResult(Activity.RESULT_OK, result);
        finish();

    }
}
