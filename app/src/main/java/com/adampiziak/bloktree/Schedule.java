package com.adampiziak.bloktree;

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
import android.support.v4.widget.NestedScrollView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Window;

import com.adampiziak.bloktree.Activities.ActEventCreator;
import com.adampiziak.bloktree.Activities.ActEventEditor;
import com.adampiziak.bloktree.Activities.ActMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Schedule extends NestedScrollView {

    List<Event> events = new ArrayList<>();
    List<Event> eventsToday = new ArrayList<>();

    List<Project> projects = new ArrayList<>();

    private final String TAG = "Schedule";
    float scaleFactor = 1.0f;
    int hourHeightDp = 100;
    int hourHeight = (int) dpToPx(hourHeightDp);
    int mainOffset = (int) dpToPx(80);
    int rootPadding = (int) dpToPx(30);
    int rootHeight = (24*hourHeight) + (int) (2* rootPadding);
    int rootWidth;
    float scrollOffset = 0;
    float textHeightG = 10f;
    float newEventStart = 0;
    int newEventHour = -1;

    boolean activeState;
    ScaleGestureDetector scaleGestureDetector;
    GestureListener gestureListener;

    //this is used to prevent touch events from creating more than one intent
    public void setState(boolean state) {
        activeState = state;
    }

    public Schedule(Context context) {
        super(context);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureListener = new GestureListener();
        init();
    }

    public Schedule(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureListener = new GestureListener();
        init();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        boolean handled= scaleGestureDetector.onTouchEvent(ev);
        return handled;
    }

    void filterEvents() {
        eventsToday = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int currentYear = c.get(Calendar.YEAR);
        /*
        for (Event event : events) {
            if (event.getDay() == currentDay
                && event.getMonth() == currentMonth
                && event.getYear() == currentYear) {
                eventsToday.add(event);
            }
        }
        */
    }

    void init() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ref.child("events").child(auth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /*
                Event event = Tools.createEventFromSnapshot(dataSnapshot);

                event.container = new RectF(mainOffset,
                        (float) event.getHour() * hourHeight + ((float) event.getMinute()/60)*hourHeight + rootPadding,
                        rootWidth - rootPadding,
                        (float) event.getHour() * hourHeight + rootPadding + ((float) event.getDuration() / 60)*hourHeight);
                events.add(event);
                */
                filterEvents();
                invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                invalidate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                invalidate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.child("projects").child(auth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Project project = Tools.createProjectFromSnapshot(dataSnapshot);
                projects.add(project);
                invalidate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                invalidate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                invalidate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleGestureDetector.onTouchEvent(e);
        float y = e.getRawY();
        float x = e.getRawX();
        if (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP) {
            Log.d("State", String.valueOf(activeState));
            for (Event event : eventsToday) {
                if (event.container.contains(x,((y + scrollOffset) - (dpToPx(70) + getStatusBarHeight()))) && activeState) {
                    Log.d("TOP", event.container.top + " ");
                    Intent intent = new Intent(getContext(), ActEventEditor.class);
                    intent.putExtra("EVENT_NAME", event.getName());
                    getContext().startActivity(intent);
                    activeState = false;
                    return true;
                }
            }
            newEventStart = e.getRawY() + scrollOffset;
            int newHour = (int) Math.floor((newEventStart - (rootPadding + dpToPx(70) + getStatusBarHeight())) / hourHeight);
            if (newHour == newEventHour) {
                Intent intent = new Intent(getContext(), ActEventCreator.class);
                intent.putExtra("HOUR_OF_DAY", newHour);
                getContext().startActivity(intent);
            } else {
                newEventHour = newHour;
            }
            invalidate();
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        rootWidth = parentWidth;


        this.setMeasuredDimension(parentWidth, rootHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        hourHeight = (int) dpToPx(hourHeightDp);
        hourHeight *= scaleFactor;
        rootHeight = (24*hourHeight) + (int) (2* rootPadding);
        rootHeight *= scaleFactor;
        measure(rootWidth, rootHeight);
        TextPaint textPaint = getTextPaint();
        Paint linePaint = getLinePaint();

        int textHeight = getTextHeight(textPaint);
        this.textHeightG = textHeight;
        drawCurrentTime(canvas, textHeight);


        for (int i = 0; i <= 24; i++) {
            canvas.drawText(String.valueOf(i) + ":00", rootPadding, hourHeight *i + textHeight/2 + rootPadding, textPaint);
            canvas.drawLine(mainOffset, (i*hourHeight + rootPadding), (rootWidth - rootPadding), (i*hourHeight + rootPadding ), linePaint);
        }

        Paint rect = new Paint();
        rect.setColor(Color.parseColor("#536DFE"));
        if (newEventHour >= 0) {
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setTextSize(14 * getResources().getDisplayMetrics().density);
            canvas.drawRoundRect(mainOffset, (float) newEventHour * hourHeight + rootPadding, rootWidth - rootPadding, (float) newEventHour * hourHeight + rootPadding + hourHeight, 10, 10, rect);
            canvas.drawText("add event/task", mainOffset + 30, (float) newEventHour * hourHeight + rootPadding + 60, textPaint);
        }
        Paint eventPaint = getEventPaint();
        for (Event event : eventsToday) {
            drawEvent(canvas, event);
        }
    }

    public void setScrollOffset(int offset) {
        this.scrollOffset = offset;
        newEventHour = -1;
        invalidate();
    }

    private void drawEvent(Canvas canvas, Event event) {
        Project project = getProject(event.getProjectKey());
        //Draw background
        canvas.drawRoundRect(event.container, 8, 8, getEventPaint(project.getColor()));

        //Draw project text
        //canvas.drawText(project.getName() + " · (" + event.getDuration()/60 + " hr)", mainOffset + dpToPx(10), (float) event.getHour() * hourHeight + ((float) event.getMinute()/60)*hourHeight + textHeightG/2 + rootPadding + dpToPx(15), getEventProjectTextPaint());
        //Draw event name text
        //canvas.drawText(event.getName(), mainOffset + dpToPx(10), (float) event.getHour() * hourHeight + ((float) event.getMinute()/60)*hourHeight + textHeightG/2 + rootPadding + dpToPx(40), getEventNameTextPaint());
    }

    private TextPaint getEventProjectTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(13 * getResources().getDisplayMetrics().density);
        textPaint.setColor(0xBBFFFFFF);
        return textPaint;
    }

    private TextPaint getEventNameTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(19 * getResources().getDisplayMetrics().density);
        //textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setColor(0xFFFFFFFF);
        return textPaint;
    }

    private int getTextHeight(TextPaint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds("0:00", 0, 4, bounds);
        return  bounds.height();
    }

    private Paint getEventPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#7C4DFF"));
        return paint;
    }

    private Paint getEventPaint(String color) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        return paint;
    }

    private TextPaint getTextPaint() {
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(12 * getResources().getDisplayMetrics().density);
        textPaint.setColor(0xFA424242);
        return textPaint;
    }

    private Paint getLinePaint() {
        Paint line = new Paint();
        line.setStrokeWidth(3);
        line.setColor(Color.parseColor("#424242"));
        line.setAlpha(20);
        return line;
    }

    private void drawCurrentTime(Canvas canvas, int textHeight) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        float minuteHeight = ((float) minute / 60f) * hourHeight;
        Paint currentLine = new Paint();
        currentLine.setColor(Color.parseColor("#2979FF"));
        currentLine.setStrokeWidth(6);
        canvas.drawLine(0, hour*hourHeight + rootPadding + (textHeight / 2) + minuteHeight, rootWidth, hour*hourHeight + textHeight/2 + rootPadding + minuteHeight, currentLine);

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.7f, Math.min(scaleFactor, 1.3f));
            Log.d("SCALE", String.valueOf(scaleFactor));
            invalidate();
            return true;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("TAP_UP", "TRUE");
            return true;
        }
    }

    public float dpToPx(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = ((ActMain) getContext()).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
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
