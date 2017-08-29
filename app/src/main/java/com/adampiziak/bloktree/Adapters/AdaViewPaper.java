package com.adampiziak.bloktree.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.adampiziak.bloktree.Fragments.FraHome;
import com.adampiziak.bloktree.Fragments.FraProjects;
import com.adampiziak.bloktree.Fragments.FraSchedule;
import com.adampiziak.bloktree.Fragments.FraSplash;
import com.adampiziak.bloktree.Fragments.FraTasks;


public class AdaViewPaper extends FragmentPagerAdapter {
    private static int NUM_FRAGMENTS = 4;

    public AdaViewPaper(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new FraTasks();
                break;
            case 1:
                fragment = new FraHome();
                break;
            case 2:
                fragment = new FraProjects();
                break;
            case 3:
                fragment = new FraSchedule();
                break;
            default:
                fragment = new FraSchedule();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_FRAGMENTS;
    }
}
