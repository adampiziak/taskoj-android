package com.adampiziak.bloktree.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adampiziak.bloktree.Activities.LanguagePicker;
import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;

public class FraAuth extends Fragment implements View.OnClickListener {

    EditText mFieldEmail;
    EditText mFieldPassword;
    Button   mButtonSignIn;
    Button   mButtonSignUp;
    Button   buttonLanguagePicker;
    Button   buttonSignIn;
    RelativeLayout layoutStartOptionsRoot;
    LinearLayout layoutSignInInputs;
    LinearLayout layoutStartOptions;
    Button buttonSignInStandard;
    EditText fieldEmailStandard;
    EditText fieldPasswordStandard;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_auth, container, false);
        mAuth = FirebaseAuth.getInstance();

        mFieldEmail    = (EditText)  v.findViewById(R.id.actvity_signin_field_email);
        mFieldPassword = (EditText)  v.findViewById(R.id.activity_signin_field_password);
        mButtonSignIn  = (Button)    v.findViewById(R.id.activity_signin_button_signin);
        mButtonSignUp  = (Button)    v.findViewById(R.id.activity_signin_button_signup);
        buttonLanguagePicker = (Button) v.findViewById(R.id.fra_auth_language_picker);
        layoutStartOptionsRoot = (RelativeLayout) v.findViewById(R.id.start_options_root);
        layoutStartOptions = (LinearLayout) v.findViewById(R.id.start_options);
        layoutSignInInputs = (LinearLayout) v.findViewById(R.id.sign_in_inputs);
        buttonSignIn = (Button) v.findViewById(R.id.action_sign_in);
        buttonSignInStandard = (Button) v.findViewById(R.id.action_signin_standard);
        fieldEmailStandard = (EditText) v.findViewById(R.id.input_email_option_standard);
        fieldPasswordStandard = (EditText) v.findViewById(R.id.input_password_option_standard);


        mButtonSignIn.setOnClickListener(this);
        mButtonSignUp.setOnClickListener(this);
        buttonLanguagePicker.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
        buttonSignInStandard.setOnClickListener(this);

        getActivity().getWindow().setStatusBarColor(0xFF263238);

        return v;
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password);
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.activity_signin_button_signin) {
            signInUser(mFieldEmail.getText().toString(), mFieldPassword.getText().toString());
        } else if (id == R.id.activity_signin_button_signup) {
            createUser(mFieldEmail.getText().toString(), mFieldPassword.getText().toString());
        } else if (id == R.id.fra_auth_language_picker) {
            Intent intent = new Intent(getActivity(), LanguagePicker.class);
            startActivity(intent);
        }

        switch (id) {
            case R.id.action_sign_in:
                final Fade fade = new Fade();
                fade.setDuration(300);
                TransitionManager.beginDelayedTransition(layoutStartOptionsRoot, fade);
                layoutStartOptions.setVisibility(View.INVISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TransitionManager.beginDelayedTransition(layoutStartOptionsRoot, fade);
                        layoutSignInInputs.setVisibility(View.VISIBLE);
                    }
                }, 300);
                break;
            case R.id.action_signin_standard:
                String email = fieldEmailStandard.getText().toString();
                String password = fieldPasswordStandard.getText().toString();
                signInUser(email, password);

        }
    }
}

