package com.gracecode.android.rain.serivce;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.helper.MixerPresetsHelper;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.helper.StopPlayTimeoutHelper;
import com.gracecode.android.rain.player.PlayManager;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;
import com.gracecode.android.rain.ui.MainActivity;

import java.util.Timer;
import java.util.TimerTask;


public class PlayService extends Service {
    private static final int NOTIFY_ID = 0;
//    public static final String ACTION_A2DP_HEADSET_PLUG = "action_d2dp_headset_plugin";
//    public static final String PREF_FOCUS_PLAY_WITHOUT_HEADSET = "pref_foucs_play_without_headset";

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotification;
    private SharedPreferences mPreferences;
    private AudioManager mAudioManager;
    private Timer mTimer;
    private SharedPreferences mSharedPreferences;
    private StopPlayTimeoutHelper mStopPlayTimeoutHelper;

    /**
     * 定时检查系统状态
     */
    class RepeatStateTimerTask extends TimerTask {
        private boolean lastA2dpState = false;
        private boolean lastFocusState = false;
        private long lastTimeoutRemain;

        @Override
        public void run() {
            // https://developer.android.com/reference/android/media/AudioManager.html#isWiredHeadsetOn()
//            detectA2dpOrHeadset();

            // 定时停止状态，避免同个重复发送
            long timeoutRemain = mStopPlayTimeoutHelper.getTimeoutRemain();
            if (timeoutRemain != StopPlayTimeoutHelper.NO_REMAIN && lastTimeoutRemain != timeoutRemain) {
                mStopPlayTimeoutHelper.sendStateBroadcast();
                lastTimeoutRemain = timeoutRemain;
            }
        }

//        @SuppressWarnings("deprecation")
//        private boolean detectA2dpOrHeadset() {
//            boolean state = mAudioManager.isBluetoothA2dpOn() || mAudioManager.isWiredHeadsetOn();
//            boolean focus = mSharedPreferences.getBoolean(PREF_FOCUS_PLAY_WITHOUT_HEADSET, false);
//            if (lastFocusState != focus || state != lastA2dpState) {
//                Intent intent = new Intent(ACTION_A2DP_HEADSET_PLUG);
//                intent.putExtra("state", state ? 1 : 0);
//                intent.putExtra("focus", focus);
//                sendBroadcast(intent);
//
//                lastA2dpState = state;
//                lastFocusState = focus;
//            }
//
//            return state || focus;
//        }
    }


    /**
     * 显示通知信息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void notifyRunning() {
        mNotificationManager.notify(NOTIFY_ID, mNotification.build());
    }


    /**
     * 清除通知信息
     */
    public void clearNotification() {
        mNotificationManager.cancel(NOTIFY_ID);
    }

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }

        public PlayManager getPlayManager() {
            return mPlayManager;
        }
    }

    private final IBinder mBinder = new PlayBinder();
    private PlayManager mPlayManager;
    private boolean isDisabled = true;
    private BroadcastReceiver mPlayBroadcastReceiver = new PlayBroadcastReceiver() {
        @Override
        public void onPlay() {
            if (isDisabled) {
                Toast.makeText(PlayService.this,
                        getString(R.string.headset_needed), Toast.LENGTH_SHORT).show();
//                onHeadsetUnPlugged();
                return;
            }

            if (!mPlayManager.isPlaying()) {
                mPlayManager.play();
                notifyRunning();
            }
        }

        @Override
        public void onStop() {
            clearNotification();
            mStopPlayTimeoutHelper.clearStopPlayTimeout();
            mPlayManager.stop();
        }

        @Override
        public void onSetVolume(int track, float volume) {
            mPlayManager.setVolume(track, volume);
        }

        @Override
        public void onSetPresets(float[] presets) {
            mPlayManager.setPresets(presets);
            savePresets(presets);
        }


//        @Override
//        public void onHeadsetPlugged() {
//            setDisabled(false);
//        }
//
//        @Override
//        public void onHeadsetUnPlugged() {
//            SendBroadcastHelper.sendStopBroadcast(PlayService.this);
//            setDisabled(true);
//        }

        @Override
        public void onPlayStopTimeout(long timeout, long remain, boolean byUser) {
            if (byUser && mPlayManager.isPlaying()) {
                mStopPlayTimeoutHelper.setStopPlayTimeout(timeout);
            }
        }
    };


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        mPlayManager = PlayManager.getInstance(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent intent = PendingIntent.getActivity(
                PlayService.this,
                NOTIFY_ID,
                new Intent(PlayService.this, MainActivity.class),
                NOTIFY_ID);

        mNotification = new Notification.Builder(PlayService.this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.keep_running))
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(intent)
                .addAction(R.drawable.ic_stop, getString(R.string.stop), getStopPendingIntent());

        mPreferences = getSharedPreferences(PlayService.class.getName(), Context.MODE_PRIVATE);
        mSharedPreferences = RainApplication.getInstance().getSharedPreferences();
        mStopPlayTimeoutHelper = new StopPlayTimeoutHelper(this);
    }


    private PendingIntent getStopPendingIntent() {
        return PendingIntent.getBroadcast(PlayService.this,
                NOTIFY_ID,
                SendBroadcastHelper.getNewStopBroadcastIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter();
        for (String action : new String[]{
//                ACTION_A2DP_HEADSET_PLUG,
                StopPlayTimeoutHelper.ACTION_SET_STOP_TIMEOUT,
//                Intent.ACTION_HEADSET_PLUG,
                PlayBroadcastReceiver.ACTION_PLAY_BROADCAST
        }) {
            filter.addAction(action);
        }
        registerReceiver(mPlayBroadcastReceiver, filter);

        SendBroadcastHelper.sendPresetsBroadcast(PlayService.this, getPresets());

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (mTimer != null) {
            try {
                mTimer.cancel();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        mTimer = new Timer();
        try {
            mTimer.schedule(new RepeatStateTimerTask(), 0, 1000);  // 一秒检查一次
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        // 载入音频资源
        mPlayManager.load();
        setDisabled(false);

        return super.onStartCommand(intent, flags, startId);
    }


    public void setDisabled(boolean flag) {
        this.isDisabled = flag;
    }


    /**
     * 保存声音配置到本地
     *
     * @param presets
     */
    public void savePresets(float[] presets) {
        SharedPreferences.Editor editor = mPreferences.edit();
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            editor.putFloat("_" + i, presets[i]);
        }
        editor.apply();
    }


    /**
     * 获取声音配置
     *
     * @return
     */
    public float[] getPresets() {
        float[] result = new float[PlayManager.MAX_TRACKS_NUM];
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            result[i] = mPreferences.getFloat("_" + i, MixerPresetsHelper.DEFAULT_PRESET[i]);
        }

        return result;
    }

    @Override
    public void onDestroy() {
        try {
            mTimer.cancel();
            if (mPlayManager.isPlaying()) {
                mPlayManager.stop();
                mPlayManager.unload();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            mTimer = null;
            clearNotification();
            unregisterReceiver(mPlayBroadcastReceiver);
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
