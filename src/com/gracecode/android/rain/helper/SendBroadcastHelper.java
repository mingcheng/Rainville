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


    public static Intent getNewPlayBroadcastIntent() {
        return new Intent(PlayBroadcastReceiver.PLAY_BROADCAST_NAME);
    }


    public static Intent getNewStopBroadcastIntent() {
        return getNewPlayBroadcastIntent()
                .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_STOP);
    }


    public static void sendStopBroadcast(Context context) {
        sendBroadcast(context, getNewStopBroadcastIntent());
    }


    public static void sendPlayBroadcast(Context context) {
        sendBroadcast(context, getNewPlayBroadcastIntent()
                .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_PLAY)
        );
    }


    public static void sendPresetsBroadcast(Context context, float[] presets) {
        sendBroadcast(context, getNewPlayBroadcastIntent()
                .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_SET_PRESETS)
                .putExtra(PlayBroadcastReceiver.FIELD_PRESETS, presets)
        );
    }
}
