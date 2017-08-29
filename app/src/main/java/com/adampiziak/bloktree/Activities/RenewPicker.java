package com.adampiziak.bloktree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adampiziak.bloktree.R;

public class RenewPicker extends AppCompatActivity implements View.OnClickListener {

    public static final int NO_RENEW = -1;
    public static final int RENEW_DAILY = 0;
    public static final int RENEW_WEEKLY = 1;

    //Views
    Button daily;
    Button weekly;
    Button noRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_renew_picker);

        //Set views
        daily = (Button) findViewById(R.id.act_renew_picker_daily);
        weekly = (Button) findViewById(R.id.act_renew_picker_weekly);
        noRepeat = (Button) findViewById(R.id.act_renew_picker_no_repeat);

        //Set onClickListeners
        daily.setOnClickListener(this);
        weekly.setOnClickListener(this);
        noRepeat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.act_renew_picker_daily:
                selectAndFinish(RENEW_DAILY);
                break;
            case R.id.act_renew_picker_weekly:
                selectAndFinish(RENEW_WEEKLY);
                break;
            case R.id.act_renew_picker_no_repeat:
                selectAndFinish(NO_RENEW);
                break;
        }
    }

    public void selectAndFinish(int type) {
        Intent result = new Intent();
        result.putExtra("RENEW_TYPE", type);
        setResult(Activity.RESULT_OK, result);
        finish();

    }
}
