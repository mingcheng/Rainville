package com.gracecode.android.rain.helper;


import android.content.Context;
import android.content.Intent;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;

public final class SendBroadcastHelper {

    public static void sendBroadcast(Context context, Intent intent) {
        if (context != null) {
            context.sendBroadcast(intent);
        }
    }


    public static Intent getNewPlayBroadcastIntent(Context context, Class cls) {
        return new Intent(context, cls)
                .setAction(PlayBroadcastReceiver.ACTION_PLAY_BROADCAST)
                .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_PLAY);
    }


    public static Intent getNewPlayBroadcastIntent() {
        return new Intent()
                .setAction(PlayBroadcastReceiver.ACTION_PLAY_BROADCAST)
                .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_PLAY);
    }


    public static Intent getNewStopBroadcastIntent() {
        return new Intent()
                .setAction(PlayBroadcastReceiver.ACTION_PLAY_BROADCAST)
                .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_STOP);
    }


    public static void sendStopBroadcast(Context context) {
        sendBroadcast(context, getNewStopBroadcastIntent());
    }


    public static void sendPlayBroadcast(Context context) {
        sendBroadcast(context, getNewPlayBroadcastIntent());
    }


    /**
     * 设置预设选项
     *
     * @param context
     * @param presets
     */
    public static void sendPresetsBroadcast(Context context, float[] presets) {
        sendBroadcast(context, getNewPlayBroadcastIntent()
                        .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_SET_PRESETS)
                        .putExtra(PlayBroadcastReceiver.FIELD_PRESETS, presets)
        );
    }


    public static void sendPlayStopTimeoutBroadcast(Context context, long timeout, long remain, boolean set) {
        Intent intent = new Intent(StopPlayTimeoutHelper.ACTION_SET_STOP_TIMEOUT);
        intent.putExtra(PlayBroadcastReceiver.FIELD_TIMEOUT, timeout);
        if (set) {
            intent.putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_SET_TIMEOUT);
        }

        if (remain != StopPlayTimeoutHelper.NO_REMAIN) {
            intent.putExtra(StopPlayTimeoutHelper.FIELD_REMAIN, remain);
        }

        sendBroadcast(context, intent);
    }


    public static void sendPlayStopTimeoutBroadcast(Context context, long timeout, boolean set) {
        sendPlayStopTimeoutBroadcast(context, timeout, StopPlayTimeoutHelper.NO_REMAIN, set);
    }
}
