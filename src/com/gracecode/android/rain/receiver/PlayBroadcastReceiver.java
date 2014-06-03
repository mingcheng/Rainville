package com.gracecode.android.rain.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.helper.StopPlayTimeoutHelper;
import com.gracecode.android.rain.serivce.PlayService;


public abstract class PlayBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PLAY_BROADCAST = "com.gracecode.android.rain.receiver.PlayBroadcastReceiver";

    public static final String FIELD_CMD = "command";
    public static final String FIELD_PRESETS = "presets";
    public static final String FIELD_TRACK = "track";
    public static final String FIELD_TIMEOUT = "timeout";

    public static final int CMD_NOP = 0x00;
    public static final int CMD_STOP = 0x0a;
    public static final int CMD_PLAY = 0x0b;
    public static final int CMD_SET_VOLUME = 0x0c;
    public static final int CMD_SET_PRESETS = 0x0d;
    public static final int CMD_SET_TIMEOUT = 0x0e;

    private final RainApplication mRainApplication;
    private final SharedPreferences mSharedPreferences;

    public PlayBroadcastReceiver() {
        mRainApplication = RainApplication.getInstance();
        mSharedPreferences = mRainApplication.getSharedPreferences();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case PlayBroadcastReceiver.ACTION_PLAY_BROADCAST:
                switch (intent.getIntExtra(FIELD_CMD, CMD_NOP)) {
                    case CMD_PLAY:
                        onPlay();
                        break;
                    case CMD_STOP:
                        onStop();
                        break;
                    case CMD_SET_VOLUME:
                        break;
                    case CMD_SET_PRESETS:
                        onSetPresets(intent.getFloatArrayExtra(FIELD_PRESETS));
                        break;
                }
                break;

            case StopPlayTimeoutHelper.ACTION_SET_STOP_TIMEOUT:
                int command = intent.getIntExtra(FIELD_CMD, CMD_NOP);
                long timeout = intent.getLongExtra(FIELD_TIMEOUT, StopPlayTimeoutHelper.DEFAULT_STOP_TIMEOUT);
                long remain = intent.getLongExtra(StopPlayTimeoutHelper.FIELD_REMAIN, StopPlayTimeoutHelper.NO_REMAIN);
                onPlayStopTimeout(timeout, remain, (command != CMD_NOP));
                break;

            case Intent.ACTION_HEADSET_PLUG:
            case PlayService.ACTION_A2DP_HEADSET_PLUG:
                boolean focus = mSharedPreferences.getBoolean(PlayService.PREF_FOCUS_PLAY_WITHOUT_HEADSET, false)
                        || intent.getBooleanExtra("focus", false);
                if (focus) {
                    onHeadsetPlugged();
                    break;
                }

                int stat = intent.getIntExtra("state", -1);
                switch (stat) {
                    case 0:
                        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        if (manager != null && manager.isBluetoothA2dpOn()) {
                            onHeadsetPlugged();
                        } else {
                            onHeadsetUnPlugged();
                        }
                        break;
                    case 1:
                        onHeadsetPlugged();
                        break;
                }
                break;
        }
    }

    abstract public void onPlay();

    abstract public void onStop();

    abstract public void onSetVolume(int track, int volume);

    abstract public void onSetPresets(float[] presets);

    abstract public void onHeadsetPlugged();

    abstract public void onHeadsetUnPlugged();

    abstract public void onPlayStopTimeout(long timeout, long remain, boolean byUser);
}
