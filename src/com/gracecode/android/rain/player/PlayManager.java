package com.gracecode.android.rain.player;

import android.content.Context;
import android.media.AudioManager;
import com.gracecode.android.common.Logger;
import com.gracecode.android.rain.BuildConfig;
import com.gracecode.android.rain.R;

public final class PlayManager {
    private final Context mContext;
    private final AudioManager mAudioManager;

    private static final int[] mTrackers = {
            R.raw._0, R.raw._1, R.raw._2,
            R.raw._3, R.raw._4, R.raw._5,
            R.raw._6, R.raw._7, R.raw._8,
            R.raw._9,
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
            setPlayer(i);
        }
    }

    private void setPlayer(int track) {
        mPlayers[track] = new BufferedPlayer(mContext, mTrackers[track]);
        mPlayers[track].setLooping(true);
    }


    private BufferedPlayer getPlayer(int track) {
        return mPlayers[track];
    }


    public void stop() {
        try {
            for (int i = 0; i < MAX_TRACKS_NUM; i++) {
                if (mPlayers[i] != null) {
                    getPlayer(i).shutdown();
                }
            }
        } catch (RuntimeException e) {
            if (BuildConfig.DEBUG) Logger.w("Can not shutdown, maybe player is not ready?");
        }
        playing = false;
    }

    public void play() {
        if (isPlaying()) return;

        initPlayers();
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            if (BuildConfig.DEBUG) {
                Logger.v("Start playing track " + i + ".");
            }
            getPlayer(i).play();
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


    public void setVolume(int track, int volume) {
        setVolume(track, volume, false);
    }


    public synchronized void setVolume(final int track, final int volume, boolean temporary) {
        if (BuildConfig.DEBUG) {
            Logger.v("Set track[" + track + "]'s volume " + volume + " / " + getMaxVolume());
        }

        try {
            final float percent = volume / (float) getMaxVolume();
            getPlayer(track).setStereoVolume(percent, percent);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (!temporary) {
                mTrackerVolumes[track] = volume;
            }
        }
    }
}
