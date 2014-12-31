package com.gracecode.android.rain.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import com.gracecode.android.common.Logger;
import com.gracecode.android.rain.BuildConfig;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.MixerPresetsHelper;

public final class PlayManager {
    public static final int DEFAULT_VOLUME_PERCENT = (int) MixerPresetsHelper.DEFAULT_PRESET[0];
    private static final double TOTAL_DELAY_TIME = 2000;

    private final Context mContext;
    private final AudioManager mAudioManager;

    private static final int[] mTrackers = {
            R.raw._0, R.raw._1, R.raw._2,
            R.raw._3, R.raw._4, R.raw._5,
            R.raw._6, R.raw._7, R.raw._8,
            R.raw._9,
    };
    public static final int MAX_TRACKS_NUM = mTrackers.length;

    private static int[] mStreamID = new int[MAX_TRACKS_NUM];
    private static int[] mSoundID = new int[MAX_TRACKS_NUM];
    private static int[] mVolume = new int[MAX_TRACKS_NUM];

    /**
     * Singleton Mode
     *
     * @param context
     * @return playerManager
     */
    private static PlayManager ourInstance = null;
    private SoundPool mSoundPool;
    private boolean isPlaying = false;

    /**
     * 播放控制器，使用单例模式
     *
     * @param context
     * @return
     */
    public static PlayManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        ourInstance = new PlayManager(context);

        return ourInstance;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        this.mSoundPool =
                new SoundPool.Builder()
                        .setAudioAttributes(attributes)
                        .build();
    }

    /**
     * @see "https://developer.android.com/reference/android/media/SoundPool.html"
     */
    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        this.mSoundPool = new SoundPool(MAX_TRACKS_NUM, AudioManager.STREAM_MUSIC, 0);
    }

    /**
     * 使用单例模式，此构造函数不直接被调用
     *
     * @param context
     */
    private PlayManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // 生成 SoundPool 对象，不同的版本有不同的构造方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

        initSoundPool();
    }


    /**
     * 初始化 SoundPool，载入所有的资源
     */
    private void initSoundPool() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            mSoundID[i] = mSoundPool.load(mContext, mTrackers[i], 0);
        }
    }

    public synchronized void stop() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            mSoundPool.stop(mStreamID[i]);
        }

        isPlaying = false;
    }


    Handler mHandler = new Handler();

    public synchronized void play() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
//            final int track = i;
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Logger.i("Now play track [" + track + "]");
//                    mStreamID[track] = mSoundPool.play(mSoundID[track], getVolume(track), getVolume(track), 0, -1, 1.0f);  // loop
//                }
//            }, getRandomDelayTime());
            mStreamID[i] = mSoundPool.play(mSoundID[i], getVolume(i), getVolume(i), 0, -1, 1.0f);  // loop
        }

        isPlaying = true;
    }


    /**
     * 播放的时候随机暂停时间
     *
     * @return
     */
    private long getRandomDelayTime() {
        return (long) (TOTAL_DELAY_TIME * Math.random());
    }

    /**
     * 判断是否在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    public int getVolume(int track) {
        return mVolume[track];
    }

    public int getDefaultVolume() {
        return DEFAULT_VOLUME_PERCENT * getMaxVolume();
    }

    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public synchronized void setVolume(int track, int volume) {
        setVolume(track, volume, false);
    }

    public synchronized void setVolume(final int track, final int volume, boolean temporary) {
        if (BuildConfig.DEBUG) {
            Logger.v("Set track[" + track + "]'s volume " + volume + " / " + getMaxVolume());
        }

        try {
            final float percent = volume / (float) getMaxVolume();
            mSoundPool.setVolume(mStreamID[track], percent, percent);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (!temporary) {
                mVolume[track] = volume;
            }
        }
    }
}
