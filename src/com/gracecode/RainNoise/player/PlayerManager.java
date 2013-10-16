package com.gracecode.RainNoise.player;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import com.gracecode.RainNoise.BuildConfig;
import com.gracecode.RainNoise.R;

import java.util.ArrayList;

public final class PlayerManager {
    private static final String TAG = PlayerManager.class.getName();
    private static final long VOLUME_CHANGE_DURATION = 1600;

    private final Context mContext;
    private final AudioManager mAudioManager;

    private static final int[] mTrackers = {
            R.raw._0, R.raw._1, R.raw._2, R.raw._3, R.raw._4,
            R.raw._5, R.raw._6, R.raw._7, R.raw._8, R.raw._9
    };
    public static final int MAX_TRACKS_NUM = mTrackers.length;
    private static BufferedPlayer[] mPlayers = new BufferedPlayer[MAX_TRACKS_NUM];
    private static int[] mTrackerVolumes = new int[MAX_TRACKS_NUM];

    /**
     * Singleton Mode
     *
     * @param context
     * @return playerManager
     */
    private static PlayerManager ourInstance = null;
    private boolean playing = false;


    public static PlayerManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }

        ourInstance = new PlayerManager(context);
        return ourInstance;
    }


    private PlayerManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private void initPlayers() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            mPlayers[i] = new BufferedPlayer(mContext, mTrackers[i]);
            mPlayers[i].setLooping(true);
        }
    }


    private ValueAnimator getVolumeChangeAnimator(final int track, final int volume) {
        ValueAnimator animator = ValueAnimator.ofInt(getVolume(track), volume);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setVolume(track, (Integer) valueAnimator.getAnimatedValue(), true);
            }
        });

        return animator;
    }


    public void muteSmoothly(Animator.AnimatorListener listener) {
        ArrayList<Animator> animators = new ArrayList<Animator>();
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            if (getVolume(i) > 0)
                animators.add(getSmoothlyMuteAnimator(i));
        }

        if (animators.size() == 0) {
            stop();
            return;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(VOLUME_CHANGE_DURATION);
        if (listener != null)
            animatorSet.addListener(listener);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }


    public void muteSmoothly() {
        muteSmoothly(null);
    }


    private Animator getSmoothlyMuteAnimator(int tracker) {
        return getVolumeChangeAnimator(tracker, 0);
    }


    synchronized public void stop() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            if (mPlayers[i] != null) {
                mPlayers[i].shutdown();
            }
        }

        playing = false;
    }

    synchronized public void play() {
        if (isPlaying()) return;

        initPlayers();
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            if (BuildConfig.DEBUG)
                Log.v(TAG, "Start playing track " + i + ".");
            mPlayers[i].play();
            setVolume(i, getVolume(i));
        }

        playing = true;
    }

    public boolean isPlaying() {
        return playing;
    }


    public int getVolume(int track) {
        return mTrackerVolumes[track];
    }


    public int getDefaultVolume() {
        return (int) (BufferedPlayer.DEFAULT_VOLUME_PERCENT * getMaxVolume());
    }


    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }


    public void setVolumeBySmooth(int track, int volume) {
        setVolumeBySmooth(track, volume, null);
    }


    public void setVolumeBySmooth(int track, int volume, Animator.AnimatorListener listener) {
        Animator animator = getVolumeChangeAnimator(track, volume);
        animator.setDuration(VOLUME_CHANGE_DURATION);
        if (listener != null)
            animator.addListener(listener);
        animator.start();
    }


    public void setVolume(int track, int volume) {
        setVolume(track, volume, false);
    }


    public void setVolume(int track, int volume, boolean temporary) {
        try {
            if (BuildConfig.DEBUG)
                Log.v(TAG, "Set layout [" + track + "]'s volume " + volume + " / " + getMaxVolume());

            float percent = volume / (float) getMaxVolume();
            if (mPlayers[track] != null) {
                mPlayers[track].setStereoVolume(percent, percent);
            }

            if (!temporary)
                mTrackerVolumes[track] = volume;
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
