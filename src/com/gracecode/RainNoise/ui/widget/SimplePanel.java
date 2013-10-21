package com.gracecode.RainNoise.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
    private float ratio = (float) 0.618;
    private static int DEFAULT_CLOSE_DURATION = 250;
    private static int DEFAULT_OPEN_DURATION = 300;

    private boolean isOpened = false;
    private boolean isDragging = false;
    private float mLastEventY = 0;
    private float mFirstEventY = 0;

    private ValueAnimator mOpenAnimator;
    private ObjectAnimator mFadeOutAnimator;
    private AnimatorSet mAnimatorOpenSet;

    private ValueAnimator mCloseAnimator;
    private AnimatorSet mAnimatorCloseSet;
    private ObjectAnimator mFadeInAnimator;
    private SimplePanelListener mSimplePanelListener;

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

                    setDrawingCacheEnabled(true);
                    scrollTo(0, (int) (getScrollY() + offset));
                    mLastEventY = eventY;
                } else {
                    mLastEventY = 0;
                }

                setAlpha(0.85f);
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
        setDrawingCacheEnabled(false);
    }

    public void open() {
        setAnimationCacheEnabled(true);
        setAnimatorOpenSet().start();
    }

    public void close() {
        setAnimationCacheEnabled(true);
        setAnimatorCloseSet().start();
    }

    private AnimatorSet setAnimatorOpenSet() {
        mOpenAnimator = ValueAnimator.ofInt(getScrollY(),
                (int) (getHeight() - getSlideBound()));

        mOpenAnimator.setInterpolator(new OvershootInterpolator());
        mOpenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                scrollTo(0, height);
            }
        });

        mFadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 0.85f);

        mAnimatorOpenSet = new AnimatorSet();
        mAnimatorOpenSet.setDuration(DEFAULT_OPEN_DURATION);
        mAnimatorOpenSet.play(mOpenAnimator).with(mFadeOutAnimator);
        mAnimatorOpenSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mSimplePanelListener != null) {
                    mSimplePanelListener.onOpened();
                }
                isOpened = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return mAnimatorOpenSet;
    }

    private AnimatorSet setAnimatorCloseSet() {
        mCloseAnimator = ValueAnimator.ofInt(getScrollY(), SCROLL_TOP);
        mCloseAnimator.setInterpolator(new OvershootInterpolator());
        mCloseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                scrollTo(0, height);
            }
        });

        mFadeInAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 1f);

        mAnimatorCloseSet = new AnimatorSet();
        mAnimatorCloseSet.setDuration(DEFAULT_CLOSE_DURATION);
        mAnimatorCloseSet.play(mCloseAnimator).with(mFadeInAnimator);
        mAnimatorCloseSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mSimplePanelListener != null) {
                    mSimplePanelListener.onClosed();
                }
                isOpened = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return mAnimatorCloseSet;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setSlideRatio(float r) {
        this.ratio = r;
    }

    public float getSlideRatio() {
        return ratio;
    }

    public void addSimplePanelListener(SimplePanelListener listener) {
        mSimplePanelListener = listener;
    }

    public interface SimplePanelListener {
        abstract public void onOpened();

        abstract public void onClosed();
    }
}
