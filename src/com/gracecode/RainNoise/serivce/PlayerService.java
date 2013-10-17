package com.gracecode.RainNoise.serivce;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.gracecode.RainNoise.player.PlayManager;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 13-10-11
 */
public class PlayerService extends Service {
    private static PlayManager mPlayManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayManager = PlayManager.getInstance(this);
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

        public PlayManager getPlayersManager() {
            return mPlayManager;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPlayManager.isPlaying()) {
            mPlayManager.stop();
        }
    }
}
