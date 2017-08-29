package com.adampiziak.bloktree.Views.Schedule;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.adampiziak.bloktree.Activities.ActEventEditor;
import com.adampiziak.bloktree.Activities.ActMain;
import com.adampiziak.bloktree.Event;
import com.adampiziak.bloktree.Project;

public class EventView extends View {
    //Debugging
    private final String TAG = "EVENT_VIEW";

    //Data
    Event event = new Event();
    Project project = new Project();

    //Measurements
    private int eventHeight = 0;
    private int eventWidth = 0;

    public EventView(Context context) {
        super(context);
    }

    public EventView(Context context, Event event, Project project) {
        super(context);
        this.event = event;
        this.project = project;
        setTransitionName("EVENT_SHARED");
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP) {
            Intent intent = new Intent(getContext(), ActEventEditor.class);
            intent.putExtra("EVENT_NAME", event.getName());
            intent.putExtra("EVENT_COLOR", project.getColor());
            Pair<View, String> p1 = Pair.create((View) this, "EVENT_SHARED");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((ActMain) getContext(), p1);
            getContext().startActivity(intent, options.toBundle());
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        eventHeight = yNew;
        eventWidth = xNew;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawName(canvas);
    }

    private void drawName(Canvas canvas) {
        TextPaint eventName = new TextPaint();
        eventName.setAntiAlias(true);
        eventName.setTextSize(18 * getResources().getDisplayMetrics().density);
        eventName.setColor(0xFFFFFFFF);

        Rect textBounds = new Rect();
        eventName.getTextBounds("A", 0, 1, textBounds);
        int eventNameHeight =  textBounds.height();

        canvas.drawText(event.getName(), dpToPx(10), dpToPx(10) + eventNameHeight, eventName);
    }

    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(project.getColor()));
        canvas.drawRoundRect(0, 0, eventWidth, eventHeight, 8, 8, paint);
    }

    public Event getEvent() {
        return this.event;
    }

    //Converts dp to px
    public float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


}
