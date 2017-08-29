package com.adampiziak.bloktree.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.R;

public class FraMain extends Fragment {
    final String TAG = "FRA_MAIN";
    Taskoj taskoj;
    private FrameLayout fragmentContainer;
    private FragmentManager fm;
    private BottomNavigationView nav;
    private int currentPosition = 3;
    ViewPager vp;
    TextView title;
    ImageView currentDay;
    FraSchedulePager pager;
    boolean resumed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fra_main, container, false);

        taskoj = (Taskoj) getActivity().getApplication();
        fm = getActivity().getSupportFragmentManager();


        getActivity().getWindow().setStatusBarColor(0xFFECEFF1);
        v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        currentDay = (ImageView) v.findViewById(R.id.fra_main_current_day);

        fragmentContainer = (FrameLayout) v.findViewById(R.id.fra_main_container);
        final DrawerLayout drawerLayout = (DrawerLayout) v.findViewById(R.id.main_navigation_drawer);
        final NavigationView navigationView = (NavigationView) v.findViewById(R.id.main_navigation_view);
        ImageView menu = (ImageView) v.findViewById(R.id.fra_main_menu);
        final TextView toolbarTitle = (TextView) v.findViewById(R.id.fra_main_title);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (pager != null && !resumed) {
                    pager.resumeState();
                    resumed = true;
                }
            }
        });

        switch (taskoj.getMainFragmentPage()) {
            case Taskoj.HOME_PAGE:
                replaceFragment(new FraHome());
                toolbarTitle.setText("Tasks");
                currentDay.setVisibility(View.GONE);
                taskoj.setMainFragmentPage(Taskoj.HOME_PAGE);
                break;
            case Taskoj.PROJECTS_PAGE:
                replaceFragment(new FraProjects());
                toolbarTitle.setText("Projects");
                currentDay.setVisibility(View.GONE);
                taskoj.setMainFragmentPage(Taskoj.PROJECTS_PAGE);
                break;
            case Taskoj.SCHEDULE_PAGE:
                pager = new FraSchedulePager();
                replaceFragment(pager);
                toolbarTitle.setText("Schedule");
                currentDay.setVisibility(View.VISIBLE);
                taskoj.setMainFragmentPage(Taskoj.SCHEDULE_PAGE);
                break;
            case Taskoj.ZONES_PAGE:
                replaceFragment(new FraZones());
                toolbarTitle.setText("Zones");
                currentDay.setVisibility(View.VISIBLE);
                taskoj.setMainFragmentPage(Taskoj.ZONES_PAGE);
            default:
                pager = new FraSchedulePager();
                replaceFragment(pager);
                toolbarTitle.setText("Schedule");
                currentDay.setVisibility(View.VISIBLE);
                taskoj.setMainFragmentPage(Taskoj.SCHEDULE_PAGE);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_item_tasks:
                        toolbarTitle.setText("Tasks");
                        replaceFragment(new FraHome());
                        currentDay.setVisibility(View.GONE);
                        taskoj.setMainFragmentPage(Taskoj.HOME_PAGE);
                        break;
                    case R.id.nav_item_projects:
                        toolbarTitle.setText("Projects");
                        replaceFragment(new FraProjects());
                        currentDay.setVisibility(View.GONE);
                        taskoj.setMainFragmentPage(Taskoj.PROJECTS_PAGE);
                        break;
                    case R.id.nav_item_schedule:
                        toolbarTitle.setText("Schedule");
                        pager = new FraSchedulePager();
                        replaceFragment(pager);
                        currentDay.setVisibility(View.VISIBLE);
                        taskoj.setMainFragmentPage(Taskoj.SCHEDULE_PAGE);
                        break;
                    case R.id.nav_item_zones:
                        toolbarTitle.setText("Zones");
                        replaceFragment(new FraZones());
                        currentDay.setVisibility(View.VISIBLE);
                        taskoj.setMainFragmentPage(Taskoj.ZONES_PAGE);
                        break;

                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

         currentDay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FraSchedulePager schedulePager;
                 Fragment fragment = getFragmentManager().findFragmentById(R.id.fra_main_container);
                 if (fragment instanceof FraSchedulePager)
                    schedulePager = (FraSchedulePager) fragment;
                 else return;
                    schedulePager.setCurrentDay();
             }
         });

        return v;

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fra_main_container, fragment);
        transaction.commit();
    }
}
