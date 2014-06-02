package com.gracecode.android.rain.serivce;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.Rainville;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.player.BufferedPlayer;
import com.gracecode.android.rain.player.PlayManager;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;
import com.gracecode.android.rain.ui.MainActivity;

import java.util.Timer;
import java.util.TimerTask;


public class PlayService extends Service {
    private static final int NOTIFY_ID = 0;
    public static final String ACTION_A2DP_HEADSET_PLUG = "action_d2dp_headset_plugin";
    public static final String PREF_FOCUS_PLAY_WITHOUT_HEADSET = "pref_foucs_play_without_headset";
    public static final int MAX_TIMEOUT_MINUTES = 60;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;
    private SharedPreferences mPreferences;
    private AudioManager mAudioManager;
    private Timer mTimer;
    private SharedPreferences mSharedPreferences;

    public void notifyRunning() {
        mNotificationManager.notify(NOTIFY_ID, mNotification.build());
    }

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
    private static PlayManager mPlayManager;
    private boolean isDisabled = true;
    private BroadcastReceiver mPlayBroadcastReceiver = new PlayBroadcastReceiver() {
        @Override
        public void onPlay() {
            if (isDisabled) {
                Toast.makeText(PlayService.this,
                        getString(R.string.headset_needed), Toast.LENGTH_SHORT).show();
                onHeadsetUnPlugged();
                return;
            }

            mPlayManager.play();
            notifyRunning();
        }

        @Override
        public void onStop() {
            mPlayManager.stop();
            clearNotification();
        }

        @Override
        public void onSetVolume(int track, int volume) {
            mPlayManager.setVolume(track, volume);
        }


        @Override
        public void onSetPresets(float[] presets) {
            for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
                int volume = (int) (mPlayManager.getMaxVolume() * presets[i]);
                mPlayManager.setVolume(i, volume);
            }

            savePresets(presets);
        }


        @Override
        public void onHeadsetPlugged() {
            setDisabled(false);
        }

        @Override
        public void onHeadsetUnPlugged() {
            SendBroadcastHelper.sendStopBroadcast(PlayService.this);
            setDisabled(true);
        }
    };


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

        mNotification = new NotificationCompat.Builder(PlayService.this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.keep_running))
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(intent)
                .addAction(R.drawable.ic_stop, getString(R.string.stop), getStopPendingIntent());

        mPreferences = getSharedPreferences(PlayService.class.getName(), Context.MODE_PRIVATE);
        mSharedPreferences = Rainville.getInstance().getSharedPreferences();
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
                ACTION_A2DP_HEADSET_PLUG,
                Intent.ACTION_HEADSET_PLUG,
                PlayBroadcastReceiver.PLAY_BROADCAST_NAME
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
            mTimer.schedule(new DetectA2dpTimerTask(), 0, 500);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    class DetectA2dpTimerTask extends TimerTask {
        private int lastA2dpState = -1;
        private boolean lastFocusState = false;

        @Override
        public void run() {
            // https://developer.android.com/reference/android/media/AudioManager.html#isWiredHeadsetOn()
            int state = (mAudioManager.isBluetoothA2dpOn() || mAudioManager.isWiredHeadsetOn()) ? 1 : 0;
            boolean focus = mSharedPreferences.getBoolean(PREF_FOCUS_PLAY_WITHOUT_HEADSET, false);

            if (lastFocusState != focus || state != lastA2dpState) {
                Intent intent = new Intent(ACTION_A2DP_HEADSET_PLUG);
                intent.putExtra("state", state);
                intent.putExtra("focus", focus);
                sendBroadcast(intent);

                lastA2dpState = state;
                lastFocusState = focus;
            }
        }
    }


    public void setDisabled(boolean flag) {
        this.isDisabled = flag;
    }


    public void savePresets(float[] presets) {
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            mPreferences.edit().putFloat("_" + i, presets[i]).commit();
        }
    }


    public float[] getPresets() {
        float[] result = new float[PlayManager.MAX_TRACKS_NUM];
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            result[i] = mPreferences.getFloat("_" + i, BufferedPlayer.DEFAULT_VOLUME_PERCENT);
        }

        return result;
    }

    @Override
    public void onDestroy() {
        if (mPlayManager.isPlaying()) {
            mPlayManager.stop();
            clearNotification();
        }

        unregisterReceiver(mPlayBroadcastReceiver);

        try {
            mTimer.cancel();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
