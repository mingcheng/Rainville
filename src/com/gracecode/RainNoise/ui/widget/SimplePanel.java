package com.gracecode.RainNoise.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;


public class SimplePanel extends FrameLayout {
    private static final int SCROLL_TOP = 0;
    private static final float DEFAULT_SLIDE_DISTANCE = 200;
    private float ratio = (float) 0.6;
    private static int DEFAULT_CLOSE_DURATION = 250;
    private static int DEFAULT_OPEN_DURATION = 300;

    private boolean isOpened = false;
    private boolean isDragging = false;
    private float mLastEventY = 0;
    private float mFirstEventY = 0;


    public SimplePanel(Context context) {
        super(context);
    }

    public SimplePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimplePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float getSlideBound() {
        return getHeight() * ratio;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float eventY = event.getY();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if (isOpened && (getHeight() - getScrollY()) < eventY) {
                    return false;
                }

                if (isDragging) {
                    float offset = 0;
                    if (mLastEventY != 0) offset = mLastEventY - eventY;

                    scrollTo(0, (int) (getScrollY() + offset));
                    mLastEventY = eventY;
                } else {
                    mLastEventY = 0;
                }

                isDragging = true;
                break;

            case MotionEvent.ACTION_DOWN:
                if (isOpened && (getHeight() - getScrollY()) < eventY) {
                    return false;
                } else {
                    mFirstEventY = eventY;
                }
                break;


            case MotionEvent.ACTION_UP:
                if (!isDragging) return false;

                if (mFirstEventY > eventY && Math.abs(mFirstEventY - eventY) > DEFAULT_SLIDE_DISTANCE) {
                    open();
                } else {
                    close();
                }

                clearDragging();
                break;
        }

        return true;
    }

    private void clearDragging() {
        mLastEventY = 0;
        mFirstEventY = 0;
        isDragging = false;
    }

    public void open() {
        ValueAnimator animator = ValueAnimator.ofInt(getScrollY(), (int) (getHeight() - getSlideBound()));
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(DEFAULT_OPEN_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                scrollTo(0, height);
            }
        });
        animator.start();
        isOpened = true;
    }


    public void close() {
        ValueAnimator animator = ValueAnimator.ofInt(getScrollY(), SCROLL_TOP);
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(DEFAULT_CLOSE_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                scrollTo(0, height);
            }
        });
        animator.start();
        isOpened = false;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setSlideRatio(float r) {
        this.ratio = r;
    }
}
