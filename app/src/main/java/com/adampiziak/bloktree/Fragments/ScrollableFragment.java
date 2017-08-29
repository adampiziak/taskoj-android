package com.adampiziak.bloktree.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adampiziak.bloktree.R;

import it.enricocandino.view.SynchronizedScrollView;

public class ScrollableFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_scroll_view, container, false);

        LinearLayout rowContainer = (LinearLayout) rootView.findViewById(R.id.container);

        int fragmentColor = getArguments().getInt("color", 0xFFF44336);

        int count = 1;
        for (int opacity = 63; opacity < 256; opacity += 16) {

            TextView row = new TextView(getContext());
            row.setLayoutParams(new SynchronizedScrollView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 250
            ));
            row.setGravity(Gravity.CENTER);

            row.setBackgroundColor(fragmentColor);
            row.getBackground().setAlpha(opacity);
            row.setText(String.valueOf(count++));

            rowContainer.addView(row);
        }

        return rootView;
    }

}
