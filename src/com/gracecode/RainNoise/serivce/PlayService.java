package com.gracecode.RainNoise.serivce;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import com.gracecode.RainNoise.helper.PlayBroadcastReceiver;
import com.gracecode.RainNoise.player.PlayManager;


public class PlayService extends Service {
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
    private BroadcastReceiver mBroadcastReceiver = new PlayBroadcastReceiver() {
        @Override
        public void onPlay() {
            mPlayManager.play();
        }

        @Override
        public void onStop() {
            mPlayManager.stop();
        }

        @Override
        public void onSetVolume(int track, int volume) {
            mPlayManager.setVolume(track, volume);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mPlayManager = PlayManager.getInstance(this);
        registerReceiver(mBroadcastReceiver, new IntentFilter(PlayBroadcastReceiver.PLAY_BROADCAST_NAME));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        if (mPlayManager.isPlaying()) {
            mPlayManager.stop();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
