package com.gracecode.RainNoise.player;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import com.gracecode.RainNoise.BuildConfig;
import com.gracecode.RainNoise.R;

public final class PlayersManager {
    private static final String TAG = PlayersManager.class.getName();


    private final Context mContext;
    private final AudioManager mAudioManager;

    private static final int[] layers = {
            R.raw._0, R.raw._1, R.raw._2, R.raw._3, R.raw._4,
            R.raw._5, R.raw._6, R.raw._7, R.raw._8, R.raw._9
    };
    public static final int MAX_TRACKS_NUM = layers.length;
    private static BufferedPlayer[] mPlayers = new BufferedPlayer[MAX_TRACKS_NUM];

    /**
     * Singleton Mode
     *
     * @param context
     * @return
     */
    private static PlayersManager ourInstance = null;
    private boolean playing = false;


    public static PlayersManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }

        ourInstance = new PlayersManager(context);
        return ourInstance;
    }


    private PlayersManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private void initPlayers() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            mPlayers[i] = new BufferedPlayer(mContext, layers[i]);
            mPlayers[i].setLooping(true);
        }
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
            if (BuildConfig.DEBUG) Log.i(TAG, "Start playing track " + i);
            mPlayers[i].play();
        }

        playing = true;
    }

    public boolean isPlaying() {
        return playing;
    }


    public int getVolume(int track) {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(int track, float volume) {
        try {
            float max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            if (BuildConfig.DEBUG) Log.v(TAG, "Set layout [" + track + "]'s volume " + (volume / max) + ".");
            mPlayers[track].setStereoVolume(volume / max, volume / max);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
