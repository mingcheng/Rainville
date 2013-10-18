package com.gracecode.RainNoise.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public abstract class PlayBroadcastReceiver extends BroadcastReceiver {
    public static final String PLAY_BROADCAST_NAME = PlayBroadcastReceiver.class.getName();

    public static final String FIELD_CMD = "command";
    public static final String FIELD_PRESETS = "presets";

    public static final int CMD_NOP = 0x00;
    public static final int CMD_STOP = 0x0a;
    public static final int CMD_PLAY = 0x0b;
    public static final int CMD_SET_VOLUME = 0x0c;
    public static final int CMD_SET_PRESETS = 0x0d;

    @Override
    public void onReceive(Context context, Intent intent) {
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
    }


    abstract public void onPlay();

    abstract public void onStop();

    abstract public void onSetVolume(int track, int volume);

    abstract public void onSetPresets(float[] presets);
}
