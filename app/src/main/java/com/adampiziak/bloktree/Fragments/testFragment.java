package com.adampiziak.bloktree.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.adampiziak.bloktree.R;

public class testFragment extends Fragment {

    private ViewPager mViewPager;
    private ScreenSlidePagerAdapter mPagerAdapter;


    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private int[] fragmentColors = {
                0xFFf44336,
                0xFF9C27B0,
                0xFF3F51B5,
                0xFF03A9F4,
                0xFF4CAF50
        };

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ScrollableFragment();

            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}

