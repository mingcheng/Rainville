package com.gracecode.android.rain.player;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import com.gracecode.android.rain.BuildConfig;
import com.gracecode.android.rain.R;

import java.util.ArrayList;

public final class PlayManager {
    public static final String TAG = PlayManager.class.getName();
    private static final long VOLUME_CHANGE_DURATION = 500;
    private static final int LEFT_TRACK = 0;
    private static final int RIGHT_TRACK = 1;

    private final Context mContext;
    private final AudioManager mAudioManager;

    private static final int[][] mTrackers = {
            {R.raw._0a, R.raw._0b},
            {R.raw._1a, R.raw._1b},
            {R.raw._2a, R.raw._2b},
            {R.raw._3a, R.raw._3b},
            {R.raw._4a, R.raw._4b},
            {R.raw._5a, R.raw._5b},
            {R.raw._6a, R.raw._6b},
            {R.raw._7a, R.raw._7b},
            {R.raw._8a, R.raw._8b},
            {R.raw._9a, R.raw._9b}
    };

    public static final int MAX_TRACKS_NUM = mTrackers.length;
    private static BufferedPlayer[][] mPlayers = new BufferedPlayer[MAX_TRACKS_NUM][2];
    private static int[] mTrackerVolumes = new int[MAX_TRACKS_NUM];

    /**
     * Singleton Mode
     *
     * @param context
     * @return playerManager
     */
    private static PlayManager ourInstance = null;
    private boolean playing = false;


    public static PlayManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }

        ourInstance = new PlayManager(context);
        return ourInstance;
    }


    private PlayManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private void initPlayers() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            setPlayer(i, LEFT_TRACK);
            setPlayer(i, RIGHT_TRACK);
        }
    }

    private void setPlayer(int i, int track) {
        mPlayers[i][track] = new BufferedPlayer(mContext, mTrackers[i][track]);
        mPlayers[i][track].setLooping(true);
    }


    private BufferedPlayer getPlayer(int i, int track) {
        return mPlayers[i][track];
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


    public void stop() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            if (mPlayers[i] != null) {
                getPlayer(i, LEFT_TRACK).shutdown();
                getPlayer(i, RIGHT_TRACK).shutdown();
            }
        }

        playing = false;
    }

    public void play() {
        if (isPlaying()) return;

        initPlayers();
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "Start playing track " + i + ".");
            }
            getPlayer(i, LEFT_TRACK).play();
            getPlayer(i, RIGHT_TRACK).play();
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


    synchronized public void setVolumeBySmooth(int track, int volume, Animator.AnimatorListener listener) {
        mTrackerVolumes[track] = volume;

        Animator animator = getVolumeChangeAnimator(track, volume);
        animator.setDuration(VOLUME_CHANGE_DURATION);
        if (listener != null)
            animator.addListener(listener);
        animator.start();

    }


    public void setVolume(int track, int volume) {
        setVolume(track, volume, false);
    }


    synchronized public void setVolume(final int track, int volume, boolean temporary) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Set layout [" + track + "]'s volume " + volume + " / " + getMaxVolume());
        }

        try {
            final float percent = volume / (float) getMaxVolume();
            getPlayer(track, LEFT_TRACK).setStereoVolume(percent, percent);
            getPlayer(track, RIGHT_TRACK).setStereoVolume(percent, percent);
        } catch (RuntimeException e) {
            Log.e(TAG, "Can not set volume, maybe player is not ready.");
        } finally {
            if (!temporary) {
                mTrackerVolumes[track] = volume;
            }
        }
    }
}
