package com.gracecode.RainNoise;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-10-11
 */
public class PlayerService extends Service {
    private static PlayersManager mRainNoisePlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mRainNoisePlayer = PlayersManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }

        public PlayersManager getPlayer() {
            return mRainNoisePlayer;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mRainNoisePlayer.isPlaying()) {
            mRainNoisePlayer.stop();
        }

    }


    public void play() {
        if (!mRainNoisePlayer.isPlaying()) {
            mRainNoisePlayer.play();
        }
    }
}
