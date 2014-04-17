package com.gracecode.android.rain.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;


public class NerdTextView extends TextView {
    public NerdTextView(Context context) {
        super(context);
    }

    public NerdTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NerdTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            // ignore any exception
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
