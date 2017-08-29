package com.adampiziak.bloktree.Fragments;


import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.R;
import com.adampiziak.bloktree.Schedule;
import com.adampiziak.bloktree.Views.Schedule.ScheduleView;

import java.util.Calendar;
import java.util.Random;

import it.enricocandino.view.SynchronizedScrollView;

import static android.R.attr.duration;

public class FraSchedule extends Fragment {
    String TAG = "FRA_SCHEDULE";

    float scaleFactor = 1.0f;
    ScaleGestureDetector scaleGestureDetector;
    Schedule schedule;

    public SynchronizedScrollView scrollView;
    int dayOffset = 0;

    Taskoj taskoj;

    public FraSchedule() {
        // Required empty public constructor
    }

    public void setDayOffset(int offset) {
        this.dayOffset = offset;
    }

    ScheduleView scheduleView;
    public int getScrollOffset () {
        if (scrollView != null)
            return scrollView.getScrollY();
        else
            return 0;
    }

    public void scrollTo(final int so) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (scrollView != null) {
                    //scrollView.scrollTo(0, 1560);
                } else
                    Log.d(TAG, "run: NULL");
            }
        }, 00);
    }

    public void scrollToCurrentTime (long delay) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int hourHeight = (int) dpToPx(80);
                if (scrollView != null)
                    ObjectAnimator.ofInt(scrollView, "scrollY",  hourHeight*hour).setDuration(270).start();
                else
                    Log.d(TAG, "run: NULL");
            }
        }, delay);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        taskoj = (Taskoj) getContext().getApplicationContext();
        View v = inflater.inflate(R.layout.fra_schedule, container, false);
        scrollView = (SynchronizedScrollView) v.findViewById(R.id.schedule_scrollview);
        scheduleView = new ScheduleView(getContext(), this.dayOffset);
        scheduleView.setLayoutParams(new ScrollView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        scrollView.addView(scheduleView);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getScrollY() != 0)
                    scheduleView.setScrollOffset(scrollView.getScrollY());
            }
        });




        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.7f, Math.min(scaleFactor, 1.3f));
            Log.d("SCALE", String.valueOf(scaleFactor));
            return true;
        }
    }

    //Converts dp to px
    public float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }



}
