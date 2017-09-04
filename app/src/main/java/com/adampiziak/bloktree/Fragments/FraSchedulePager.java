package com.adampiziak.bloktree.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adampiziak.bloktree.Activities.ActEventCreator;
import com.adampiziak.bloktree.Adapters.ScheduleDayAdapter;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Taskoj;

public class FraSchedulePager extends Fragment {
    final String TAG = "SCHEDULE_VIEW_PAGER";

    ViewPager viewPager;
    ScheduleDayAdapter adapter;
    Taskoj taskoj;
    int resumePosition = 5;
    int resumeOffset = 0;
    int resumeScrollOffset = 0;
    boolean initialized = false;

    FloatingActionButton actionCreateEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRetainInstance(true);
        adapter = new ScheduleDayAdapter(getActivity().getSupportFragmentManager(), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fra_schedule_pager, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.fra_schedule_view_pager);

        taskoj = (Taskoj) getActivity().getApplication();
        resumeScrollOffset = taskoj.getScrollOffset();
        resumePosition = taskoj.getScheduleViewPagerPosition();
        resumeOffset = taskoj.getDayOffset();

        actionCreateEvent = v.findViewById(R.id.fra_schedule_fab);
        actionCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActEventCreator.class);
                startActivity(intent);
            }
        });

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                taskoj.setDayOffset(adapter.getOffset());
                taskoj.setScheduleViewPagerPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (!initialized) {
            setPage(5);
            initialized = true;
        }

        return v;
    }

    public void resumeState() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(resumePosition, false);
                Log.d(TAG, "resumeOffset: " + resumeOffset);
                Log.d(TAG, "resumePosition: " + resumePosition);
                adapter.setOffset(resumeOffset);
                //adapter.resumeScrollState(resumeScrollOffset);
            }
        }, 0);

    }

    public ViewPager getViewPager() {
        return this.viewPager;
    }



    public void setPage(int page) {
        viewPager.setCurrentItem(page, false);
    }

    public void setPage(int page, boolean animate) {
        viewPager.setCurrentItem(page, animate);
    }

    public void setCurrentDay() {
        adapter.setCurrentDay();
    }


}
