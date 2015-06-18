package com.gracecode.android.rain.player;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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

    private static int[] mStreamID = new int[MAX_TRACKS_NUM];
    private static int[] mSoundID = new int[MAX_TRACKS_NUM];
    private static float[] mVolume = new float[MAX_TRACKS_NUM];

    private static SoundPool mSoundPool;
    private static boolean playing = false;

    /**
     * Singleton Mode
     *
     * @param context
     * @return playerManager
     */
    private static PlayManager ourInstance = null;
    private AnimatorSet mPresetsChangeAnimatorSet;

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


    /**
     * 根据新的 SDK 文档说明，尽可能使用新的 Build 声明方式
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_TRACKS_NUM)
                .setAudioAttributes(attributes)
                .build();
    }

    /**
     * @see "https://developer.android.com/reference/android/media/SoundPool.html"
     */
    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        mSoundPool = new SoundPool(MAX_TRACKS_NUM, AudioManager.STREAM_MUSIC, 0);
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
    }

    /**
     * 初始化 SoundPool，载入所有的资源
     */
    public void load() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            mSoundID[i] = mSoundPool.load(mContext, mTrackers[i], 0);
        }
    }

    /**
     * 卸载所有资源
     */
    public void unload() {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            mSoundPool.unload(mSoundID[i]);
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        try {
            for (int i = 0; i < MAX_TRACKS_NUM; i++) {
                mSoundPool.stop(mStreamID[i]);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            playing = false;
        }
    }

    /**
     * 开始播放
     */
    public void play() {
        try {
            if (mPresetsChangeAnimatorSet != null) {
                mPresetsChangeAnimatorSet.cancel();
            }
            mPresetsChangeAnimatorSet = new AnimatorSet();

            for (int i = 0; i < MAX_TRACKS_NUM; i++) {
                final int track = i;

                mStreamID[track] = mSoundPool.play(mSoundID[track], 0f, 0f, 0, -1, 1.0f);

                ValueAnimator animator = ValueAnimator.ofFloat(0f, getVolume(i));
                animator.setDuration(1000);
                animator.setStartDelay((long) (3000 * Math.random()));
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float value = (float) valueAnimator.getAnimatedValue();
                        setVolume(track, value);
                    }
                });

                mPresetsChangeAnimatorSet.playTogether(animator);
            }

            mPresetsChangeAnimatorSet.start();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            playing = true;
        }
    }


    /**
     * 判断是否在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return playing;
    }

    public float getVolume(int track) {
        return mVolume[track];
    }


    /**
     * 设置音轨的音量
     * <p/>
     * 采用设置不同音轨的音量，从而改变不同的声音效果
     *
     * @param track
     * @param volume
     */
    public void setVolume(int track, float volume) {
        setVolume(track, volume, false);
    }

    public void setVolume(final int track, final float volume, boolean temporary) {
        try {
            mSoundPool.setVolume(mStreamID[track], volume, volume);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            if (!temporary) {
                mVolume[track] = volume;
            }
        }
    }

    public void setPresets(float[] presets) {
        for (int i = 0; i < MAX_TRACKS_NUM; i++) {
            setVolume(i, presets[i]);
        }
    }
}
