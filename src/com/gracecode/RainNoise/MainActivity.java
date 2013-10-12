package com.gracecode.RainNoise;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button btnPlay;
    private SeekBar seekBar1;
    private AudioManager mAudioManager;
    private SeekBar seekBar2;
    private List<SeekBar> seekBars = new ArrayList<SeekBar>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        findViewById(R.id.play).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);

        startService(new Intent(this, PlayerService.class));

        initMixer();
    }

    private void initMixer() {
        LinearLayout box = (LinearLayout) findViewById(R.id.mixer);

        for (int i = 0; i < 10; i++) {
            SeekBar tmp = new SeekBar(this);

            tmp.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            final int layout = i;
            tmp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int volume, boolean b) {
                    if (mBinder != null) {
                        mBinder.getPlayer().setVolume(layout, volume);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            tmp.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

            seekBars.add(i, tmp);
            box.addView(tmp);
        }

    }

    @Override
    public void onClick(View view) {
        if (mBinder != null) {
            switch (view.getId()) {
                case R.id.play:
                    mBinder.getPlayer().play();
                    break;
                case R.id.stop:
                    mBinder.getPlayer().stop();
                    break;
                case R.id.reset:
                    reSetMixer();
                    break;
            }
        }
    }

    private void reSetMixer() {
        int volume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        for (int i = 0; i < 10; i++) {
            seekBars.get(i).setProgress(volume / 2);
            if (mBinder != null && mBinder.getPlayer().isPlaying()) {
                mBinder.getPlayer().setVolume(i, volume);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(new Intent(this, PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        reSetMixer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private PlayerService.MyBinder mBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mBinder = (PlayerService.MyBinder) binder;
        }

        public void onServiceDisconnected(ComponentName className) {
            mBinder = null;
        }
    };
}
