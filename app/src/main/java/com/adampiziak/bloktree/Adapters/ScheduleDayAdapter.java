package com.adampiziak.bloktree.Adapters;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.adampiziak.bloktree.Fragments.FraSchedule;
import com.adampiziak.bloktree.Fragments.FraSchedulePager;
import com.adampiziak.bloktree.Fragments.FraSplash;
import com.adampiziak.bloktree.Taskoj;

import java.util.Calendar;

public class ScheduleDayAdapter extends FragmentStatePagerAdapter {
    final String TAG = "VIEW_PAGER";
    final int PAGE_COUNT = 11;
    int offset = 0;
    FraSchedulePager parent;
    Taskoj taskoj;

    int year = 0;
    int month = 0;
    int day = 0;
    boolean firstCall = true;
    int currentPosition = 5;

    public void setOffset(int offset) {
        this.offset = offset;
        notifyDataSetChanged();
    }

    public int getOffset() {
        return this.offset;
    }


    public ScheduleDayAdapter(FragmentManager fm, FraSchedulePager context) {
        super(fm);
        this.parent = context;
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        taskoj = (Taskoj) context.getActivity().getApplication();
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 10) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    offset += 4;
                    parent.setPage(5);
                    notifyDataSetChanged();
                }
            }, 0);
        }
        if (position == 0) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    offset -= 4;
                    parent.setPage(5);
                    notifyDataSetChanged();
                }
            }, 0);
        }
        FraSchedule fragment = new FraSchedule();
        if (firstCall) {
            //fragment.scrollToCurrentTime(0);
            firstCall = false;
        }
        fragment.setDayOffset(offset + (position - 5));
        currentPosition = position;
        return fragment;
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public void setCurrentDay() {
        long delay = (parent.getViewPager().getCurrentItem() == 5 && offset == 0) ? 0 : 250;
        if (offset > 0)
            parent.setPage(8);
        else if (offset < 0)
            parent.setPage(2);
        offset = 0;
        parent.setPage(5, true);

        ((FraSchedule) getFragmentInView()).scrollToCurrentTime(delay);
    }

    public void resumeScrollState(int scrollOffset) {
        ((FraSchedule) getFragmentInView()).scrollTo(scrollOffset);
    }

    //By Nepster
    public Fragment getFragmentInView()
    {
        return ((Fragment) (instantiateItem(parent.getViewPager(), parent.getViewPager().getCurrentItem())));
    }
}
