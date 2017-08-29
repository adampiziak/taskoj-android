package com.adampiziak.bloktree.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.adampiziak.bloktree.R;

public class LanguagePicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_language_picker);
        getWindow().setStatusBarColor(0xFF263238);
    }
}
