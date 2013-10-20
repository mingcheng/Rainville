package com.gracecode.RainNoise.serivce;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.SendBroadcastHelper;
import com.gracecode.RainNoise.player.PlayManager;
import com.gracecode.RainNoise.receiver.PlayBroadcastReceiver;
import com.gracecode.RainNoise.ui.MainActivity;


public class PlayService extends Service {
    private static final int NOTIFY_ID = 0;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotification;

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
    private BroadcastReceiver mPlayBroadcastReceiver = new PlayBroadcastReceiver() {
        @Override
        public void onPlay() {
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
                .addAction(android.R.color.transparent, getString(R.string.stop), getStopPendingIntent());
    }


    private PendingIntent getStopPendingIntent() {
        return PendingIntent.getBroadcast(PlayService.this,
                NOTIFY_ID,
                SendBroadcastHelper.getNewStopBroadcastIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(mPlayBroadcastReceiver,
                new IntentFilter(PlayBroadcastReceiver.PLAY_BROADCAST_NAME));

        registerReceiver(mPlayBroadcastReceiver,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        if (mPlayManager.isPlaying()) {
            mPlayManager.stop();
            clearNotification();
        }

        unregisterReceiver(mPlayBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
