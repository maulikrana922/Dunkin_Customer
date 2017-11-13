package com.dunkin.customer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Dunkin_CustomerApp created by Rahul Kanani on 06-06-2016 11:14.
 **/

public class VerticalScrollView extends ScrollView {

    public VerticalScrollView(Context context) {
        super(context);
    }

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            /*    Dunkin_Log.i("VerticalScrollview", "onInterceptTouchEvent: DOWN super false");*/
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                return false; // redirect MotionEvents to ourself

            case MotionEvent.ACTION_CANCEL:
             //   Dunkin_Log.i("VerticalScrollview", "onInterceptTouchEvent: CANCEL super false");
                super.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_UP:
               // Dunkin_Log.i("VerticalScrollview", "onInterceptTouchEvent: UP super false");
                return false;

            default:
               // Dunkin_Log.i("VerticalScrollview", "onInterceptTouchEvent: " + action);
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
       // Dunkin_Log.i("VerticalScrollview", "onTouchEvent. action: " + ev.getAction());
        return true;
    }
}