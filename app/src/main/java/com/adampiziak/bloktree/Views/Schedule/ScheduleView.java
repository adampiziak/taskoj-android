package com.adampiziak.bloktree.Views.Schedule;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.adampiziak.bloktree.Activities.ActEventCreator;
import com.adampiziak.bloktree.Activities.ActMain;
import com.adampiziak.bloktree.Event;
import com.adampiziak.bloktree.Project;
import com.adampiziak.bloktree.Task;
import com.adampiziak.bloktree.Taskoj;
import com.adampiziak.bloktree.Tools;
import com.adampiziak.bloktree.Zone;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ScheduleView extends ViewGroup {
    //Debugging
    final private String TAG = "ScheduleView";

    //Global data
    Taskoj taskoj;

    //Measurements
    private int rootHeight = 1000;
    private int rootWidth = 0;
    private int rootPadding = 0;
    private int hourHeight = 0;
    private int eventContainerLeft = 0;
    private int hourTextHeight = 14;
    private int scrollOffset = 0;

    //Views
    EventView eventView;

    //Data

    private List<Task> tasks = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Zone> zones = new ArrayList<>();

    private List<Event> eventsToday = new ArrayList<>();
    private int dayOffset = 0;

    //UI data
    private int newEventHour = -1;

    //UI config
    private int eventBorderRadius = 8;

    public ScheduleView(Context context) {
        super(context);
        init();
    }

    public ScheduleView(Context context, int dayOffset) {
        super(context);
        this.dayOffset = dayOffset;
        init();
    }

    private void init() {
        //Initial measurements
        hourHeight = (int) dpToPx(80);
        rootPadding = (int) dpToPx(20);
        rootHeight = hourHeight*24; //rootWidth is set in onMeasure()
        eventContainerLeft = (int) dpToPx(80);

        //Get global data reference
        taskoj = (Taskoj) getContext().getApplicationContext();

        //Initialize views
        eventView = new EventView(getContext());
        //sync();

        //Allowing drawing, Android disables this by default for layouts
        setWillNotDraw(false);

        //Get projects first before syncing all data
        //syncProjects();

    }

    public void setData(List<Task> tasks, List<Project> projects, List<Event> events, List<Zone> zones) {
        this.tasks = tasks;
        this.projects = projects;
        this.events = events;
        this.zones = zones;
        Log.d(TAG, "tasks " + tasks.size());
        Log.d(TAG, "projects " + projects.size());
        Log.d(TAG, "events " + events.size());
        Log.d(TAG, "zones " + zones.size());
        filterEvents();
        Log.d(TAG, "eventsToday " + eventsToday.size());
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getRawY();
        float x = e.getRawX();
        taskoj.setScrollOffset(scrollOffset);
        if (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP) {
            int hourSelected = (int) Math.floor((y + scrollOffset - (rootPadding + dpToPx(70) + getStatusBarHeight())) / hourHeight);
            if (hourSelected == newEventHour) {
                Intent intent = new Intent(getContext(), ActEventCreator.class);
                intent.putExtra("HOUR_OF_DAY", hourSelected);
                getContext().startActivity(intent);
            } else {
                newEventHour = hourSelected;
            }
            Log.d(TAG, "onTouchEvent: " + newEventHour);
            invalidate();
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rootWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        super.setMeasuredDimension(rootWidth, rootHeight);
        filterEvents();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child instanceof EventView) {
                Event event = ((EventView) child).getEvent();
                Log.d(TAG, "onLayout: " + event.container.top + " " + event.container.bottom + " " + event.container.left + " " + event.container.right);
                child.layout((int) event.container.left, (int) event.container.top, (int) event.container.right, (int)  event.container.bottom);
            }

            if (child instanceof TextView) {
                Log.d(TAG, "TEXTVIEW");
                child.layout(0, 0, rootWidth, 1000);
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTimeLabels(canvas); //hour labels and dividers

        if (dayOffset == 0) //If this page represents the current day
            drawCurrentTime(canvas); //blue line indicating current time

        drawZones(canvas);

        //if user has pressed on an empty hour, create a "new event" visual
        if (newEventHour >= 0 && newEventHour < 24)
            drawNewEvent(canvas);
    }

    private void drawZones(Canvas canvas) {
        float strokeWidthDp = 1.5f;

        //set paint instances
        Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(dpToPx(strokeWidthDp));
        rectPaint.setStyle(Paint.Style.STROKE);

        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
        //textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        Paint line = new Paint();
        line.setStrokeWidth(dpToPx(strokeWidthDp));

        Paint fillPaint = new Paint();
        float width = (rootWidth - rootPadding) - (eventContainerLeft);
        //Draw Zones
        for (Zone zone : zones) {
            //Set Color
            int color = Color.parseColor(zone.getColor());
            rectPaint.setColor(color);
            rectPaint.setAlpha(40);
            line.setColor(color);
            line.setAlpha(30);
            fillPaint.setColor(color);

            //Get times
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(zone.getTimeStart());
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(zone.getTimeEnd());
            int hourStart = start.get(Calendar.HOUR_OF_DAY);
            int hourEnd = end.get(Calendar.HOUR_OF_DAY);
            int duration = hourEnd - hourStart;
            float height = duration*hourHeight;
            float separation = dpToPx(10);
            float remainder = width - height;
            if (remainder < 0) remainder *= -1;
            if (width > height) {
                for (int i = 1; i <= Math.floor(height/separation); i++) {
                    canvas.drawLine(eventContainerLeft, rootPadding+hourStart*hourHeight + separation*i, separation*i + eventContainerLeft, rootPadding + hourStart*hourHeight, line);
                }
                for (int i = 1; i <= Math.floor(remainder/separation); i++) {
                    canvas.drawLine(eventContainerLeft+separation*i, rootPadding + hourStart*hourHeight + height, eventContainerLeft + (width - remainder) + (separation*i), rootPadding + hourStart*hourHeight, line);
                }

                for (int i = 0; i <= Math.floor((height)/separation); i++) {
                    canvas.drawLine(eventContainerLeft+remainder+separation*i, rootPadding+height+hourStart*hourHeight, rootWidth-rootPadding, rootPadding+hourStart*hourHeight + separation*i, line);
                }
            } else {
                for (int i = 1; i <= Math.floor(width/separation); i++) {
                    canvas.drawLine(eventContainerLeft, rootPadding+hourStart*hourHeight + separation*i, separation*i + eventContainerLeft, rootPadding + hourStart*hourHeight, line);
                }
                for (int i = 1; i <= Math.floor(remainder/separation); i++) {
                    canvas.drawLine(eventContainerLeft, rootPadding + hourStart*hourHeight + (height-remainder) + separation*i, rootWidth-rootPadding, rootPadding + hourStart*hourHeight + separation*i, line);
                }
                for (int i = 0; i <= Math.floor((width)/separation); i++) {
                    canvas.drawLine(eventContainerLeft+separation*i, rootPadding+height+hourStart*hourHeight, rootWidth-rootPadding, rootPadding+hourHeight*hourStart + remainder + separation*i, line);
                }

            }

            Rect textBounds = new Rect();
            textPaint.getTextBounds(zone.getName(), 0, zone.getName().length(), textBounds);
            int textLength =  textBounds.width();

            //Draw
            canvas.drawRoundRect(eventContainerLeft, rootPadding + hourStart*hourHeight, rootWidth - rootPadding,  rootPadding + hourStart*hourHeight + duration*hourHeight, 5, 5, rectPaint);

            //Draw Text
            canvas.drawRect(rootWidth - rootPadding, rootPadding + hourStart*hourHeight - dpToPx(1), rootWidth, rootPadding + hourStart*hourHeight + textLength + dpToPx(21), fillPaint);
            canvas.save();
            canvas.rotate(90, rootWidth-rootPadding + 20, rootPadding+hourStart*hourHeight + dpToPx(10));
            canvas.drawText(zone.getName(), rootWidth-rootPadding + 20, rootPadding+hourStart*hourHeight + dpToPx(10), textPaint);
            canvas.restore();
        }
    }

    private void drawTimeLabels(Canvas canvas) {
        Paint line = new Paint();
        line.setColor(0xFFEEEEEE);
        line.setStrokeWidth(4);

        TextPaint hour = new TextPaint();
        hour.setAntiAlias(true);
        hour.setTextSize(hourTextHeight * getResources().getDisplayMetrics().density);
        hour.setColor(0xFF555555);

        int textHeight = getTimeLabelTextHeight();

        for (int i = 0; i < 24; i++) {
            int y = i*hourHeight + rootPadding;
            canvas.drawLine(eventContainerLeft, y, rootWidth - rootPadding, y, line);
            canvas.drawText(i+":00", rootPadding, y + textHeight/2, hour);
        }
    }

    private int getTimeLabelTextHeight() {
        TextPaint paint = new TextPaint();
        paint.setTextSize(hourTextHeight * getResources().getDisplayMetrics().density);
        Rect textBounds = new Rect();
        paint.getTextBounds("A", 0, 1, textBounds);
        return textBounds.height();
    }

    private void drawNewEvent(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xFF2979FF);

        TextPaint text = new TextPaint();
        text.setAntiAlias(true);
        text.setTextSize(hourTextHeight * getResources().getDisplayMetrics().density);
        text.setColor(0xFFFFFFFF);

        Rect textBounds = new Rect();
        text.getTextBounds("A", 0, 1, textBounds);
        int newEventTextHeight =  textBounds.height();

        int top = newEventHour*hourHeight+rootPadding;
        int radius = 8;
        canvas.drawRoundRect(eventContainerLeft, top, rootWidth - rootPadding, top + hourHeight, radius, radius, paint);
        canvas.drawText("new event", eventContainerLeft + dpToPx(10), top + newEventTextHeight + dpToPx(10), text);
    }

    private void drawCurrentTime(Canvas canvas) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        float minuteHeight = ((float) minute / 60f) * hourHeight;
        Paint currentLine = new Paint();
        currentLine.setColor(Color.parseColor("#2979FF"));
        currentLine.setStrokeWidth(dpToPx(2));

        canvas.drawLine(0, hour*hourHeight + rootPadding + minuteHeight, rootWidth, hour*hourHeight + rootPadding + minuteHeight, currentLine);

    }

    //Parent scrollview calls this. scroll height is used to calculated touch origin on custom view
    public void setScrollOffset(int offset) {
        /*
        this.scrollOffset = offset;
        newEventHour = -1;
        invalidate();
        */
    }

    //Gets status bar height
    int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = ((ActMain) getContext()).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    //Converts dp to px
    public float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private void reset() {
        Log.d(TAG, "reset: ");
        removeAllViews();
        //filterEvents();
        invalidate();
    }

    void filterEvents() {
        eventsToday = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        Calendar pageDate = now; //initialize to current time
        pageDate.add(Calendar.DAY_OF_MONTH, dayOffset); //add offset

        int pageWeekDay = pageDate.get(Calendar.DAY_OF_WEEK);
        for (Event event : events) {
            Calendar eventDate = Calendar.getInstance();
            eventDate.setTimeInMillis(event.getTimeStart());
            boolean sameDayOfWeek = false;
            if (event.getRenewType() == 1)
                sameDayOfWeek = isOnSameWeekDay(pageWeekDay, event);

            if (onSameDay(pageDate, eventDate) || event.getRenewType() == 0 || sameDayOfWeek) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(event.getTimeStart());
                int hourStart = cal.get(Calendar.HOUR_OF_DAY);
                int minuteStart = cal.get(Calendar.MINUTE);
                float durationHours = ((float) (event.getTimeEnd() - event.getTimeStart())) / (1000f*60f*60f);
                float eventTop = rootPadding + hourStart*hourHeight + (minuteStart/60)*hourHeight;
                Log.d(TAG, "rootWidth" + rootWidth);
                Log.d(TAG, "rootPadding" + rootPadding);
                event.container = new RectF(eventContainerLeft, eventTop, rootWidth-rootPadding, eventTop + durationHours*hourHeight);
                Project project = getProject(event.getProjectKey());
                EventView eventView = new EventView(getContext(), event, project);
                final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, MATCH_PARENT);
                eventView.setLayoutParams(lp);
                eventsToday.add(event);
                addView(eventView);
                TextView test = new TextView(getContext());
                test.setText("Hello there");
                final LayoutParams testLP = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
                test.setLayoutParams(testLP);
                addView(test);
            }
        }
    }

    private boolean isOnSameWeekDay(int pageWeekDay, Event event) {
        boolean sameWeekDay = false;
        String days = event.getRenewDays();
        if (days.charAt(pageWeekDay -1) == '1')
            sameWeekDay = true;
        return sameWeekDay;

    }

    private boolean onSameDay(Calendar page, Calendar event) {
        int pageDay = page.get(Calendar.DAY_OF_MONTH);
        int pageMonth = page.get(Calendar.MONTH);
        int pageYear = page.get(Calendar.YEAR);
        int eventDay = event.get(Calendar.DAY_OF_MONTH);
        int eventMonth = event.get(Calendar.MONTH);
        int eventYear = event.get(Calendar.YEAR);
        return pageDay == eventDay && pageMonth == eventMonth && pageYear == eventYear;


    }

    private Project getProject(String key) {
        Project project = new Project();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getKey().equals(key)) {
                project = projects.get(i);
                break;
            }
        }
        return project;
    }
}


