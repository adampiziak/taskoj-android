package com.adampiziak.bloktree.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.adampiziak.bloktree.Adapters.AdaProjectPicker;
import com.adampiziak.bloktree.R;

public class ActProjectPicker extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_project_picker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_choose_group);
        EditText filterText = (EditText) findViewById(R.id.act_proj_filter);
        filterText.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        getWindow().setStatusBarColor(Color.parseColor("#263238"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        RecyclerView projectsList = (RecyclerView) findViewById(R.id.rv_group_selector);
        projectsList.setLayoutManager(new LinearLayoutManager(this));
        final AdaProjectPicker adapter = new AdaProjectPicker(this);
        projectsList.setAdapter(adapter);

        filterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    adapter.setFilterText("");
                    Log.d("PROJ_PICKER", "EMPTY");
                } else {
                    adapter.setFilterText(s.toString());
                    Log.d("PROJ_PICKER", s.toString());
                }

                }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void selectAndFinish(String projectKey, String projectName) {
        Intent result = new Intent();
        result.putExtra("PROJECT_KEY", projectKey);
        result.putExtra("PROJECT_NAME", projectName);
        setResult(Activity.RESULT_OK, result);
        finish();

    }
}
