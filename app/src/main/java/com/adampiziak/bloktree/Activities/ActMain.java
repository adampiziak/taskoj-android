package com.adampiziak.bloktree.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.adampiziak.bloktree.Fragments.FraAuth;
import com.adampiziak.bloktree.Fragments.FraMain;
import com.adampiziak.bloktree.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActMain extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FragmentManager fm;

    boolean active = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        fm = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FraMain main = new FraMain();
                    replaceFragment(main);
                } else {
                    FraAuth auth = new FraAuth();
                    replaceFragment(auth);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    private void replaceFragment(Fragment fragment) {
        //Prevents this activity from committing transactions (and crashing) when it isn't active
        if (active) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.act_main_container, fragment);
            transaction.commit();
        }
    }

}
