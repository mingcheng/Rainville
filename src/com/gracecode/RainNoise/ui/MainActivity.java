package com.gracecode.RainNoise.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.serivce.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "ent";
    private Button btnPlay;
    private SeekBar seekBar1;
    private AudioManager mAudioManager;
    private SeekBar seekBar2;
    private List<SeekBar> seekBars = new ArrayList<SeekBar>();
    private FrameLayout mMask;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mBound;
    private boolean mOpened = false;
    private float mfirst;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        this.mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        findViewById(R.id.play).setOnClickListener(this);
//        findViewById(R.id.stop).setOnClickListener(this);
//        findViewById(R.id.reset).setOnClickListener(this);
//
//        startService(new Intent(this, PlayerService.class));

//        initMixer();

        Typeface face = Typeface.createFromAsset(getAssets(), "regular.otf");

        TextView t = (TextView) findViewById(R.id.title);
        t.setTypeface(face);

        mMask = (FrameLayout) findViewById(R.id.mask);

        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.blew).setOnClickListener(this);
    }

    private void initMixer() {
        LinearLayout box = (LinearLayout) findViewById(R.id.mixer);

        for (int i = 0; i < 10; i++) {
            SeekBar tmp = new SeekBar(this);

            tmp.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            final int layout = i;
            tmp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int volume, boolean b) {
                    if (mBinder != null) {
                        mBinder.getPlayer().setVolume(layout, volume);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            tmp.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

            seekBars.add(i, tmp);
            box.addView(tmp);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.play:
                mBinder.getPlayer().play();
                break;
            case R.id.stop:
                mBinder.getPlayer().stop();
                break;
            case R.id.reset:
                reSetMixer();
                break;
            case R.id.btn:
                if (mOpened) {
                    closePanel();
                } else {
                    openPanel();
                }
                break;
            case R.id.blew:
                Toast.makeText(this, "Blow!!!", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void openPanel() {
        ValueAnimator animator = ValueAnimator.ofInt(mMask.getScrollY(), (mScreenHeight - mBound));


        Log.e(TAG, "Move " + mMask.getScrollY() + " to " + (mScreenHeight - mBound));
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(250);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                mMask.scrollTo(0, height);
            }
        });
        animator.start();
        mOpened = true;
    }

    private void closePanel() {
        ValueAnimator animator = ValueAnimator.ofInt(mMask.getScrollY(), 0);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                mMask.scrollTo(0, height);
            }
        });
        animator.start();
        mOpened = false;
    }

    private void reSetMixer() {
        int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        for (int i = 0; i < 10; i++) {
            seekBars.get(i).setProgress(volume / 2);
            if (mBinder != null && mBinder.getPlayer().isPlaying()) {
                mBinder.getPlayer().setVolume(i, volume);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        bindService(new Intent(this, PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        reSetMixer();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;
        mScreenWidth = displaymetrics.widthPixels;
        mBound = (int) (mScreenHeight * 0.6);
//        Log.e(TAG, "Screen size is " + width + ", " + height);
//        mMask.setLayoutParams(new FrameLayout.LayoutParams(width, height));

        mMask.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unbindService(mConnection);
    }

    private PlayerService.MyBinder mBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mBinder = (PlayerService.MyBinder) binder;
        }

        public void onServiceDisconnected(ComponentName className) {
            mBinder = null;
        }
    };

    private float mLastY = 0;
    private boolean mDragging = false;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = motionEvent.getAction();

        float motionEventY = motionEvent.getY();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if (mOpened && (mMask.getHeight() - mMask.getScrollY()) < motionEventY) {
                    return false;
                }

                if (mDragging) {
                    float offset = 0;
                    if (mLastY != 0) {
                        offset = mLastY - motionEventY;
                    }


                    // Log.e(TAG, "" + offset);
                    mMask.scrollTo(0, (int) (mMask.getScrollY() + offset));
                    mLastY = motionEventY;
                } else {
                    mLastY = 0;
                }


                mDragging = true;
                break;

            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "action down");
                if (mOpened && (mMask.getHeight() - mMask.getScrollY()) < motionEventY) {
                    return false;
                } else {

                    mfirst = motionEventY;
                }
                break;


            case MotionEvent.ACTION_UP:
                Log.e(TAG, "action up");

                if (mDragging) {
                    if (mfirst > motionEventY) {
                        openPanel();
                    } else {
                        closePanel();
                    }

//                    if (mMask.getScrollY() - (mScreenHeight - mBound * 1.6) > 0) {
//                        openPanel();
//                    } else {
//                        closePanel();
//                    }

                    Log.e(TAG, mMask.getScrollY() + "," + (mScreenHeight - mBound * 1.5));
                    mLastY = 0;
                    mDragging = false;
                } else {

                    return false;
                }
                break;

        }

        return true;
    }
}
