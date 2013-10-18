package com.gracecode.RainNoise.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import com.gracecode.RainNoise.receiver.PlayBroadcastReceiver;


abstract class PlayerFragment extends Fragment {
    private static boolean mPlaying = false;

    public void setPlaying() {
        mPlaying = true;
    }

    public void setStopped() {
        mPlaying = false;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void sendBroadcast(Intent intent) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.sendBroadcast(intent);
        }
    }


    private Intent getNewBroadcastIntent() {
        return new Intent(PlayBroadcastReceiver.PLAY_BROADCAST_NAME);
    }


    public void sendStopBroadcast() {
        sendBroadcast(
                getNewBroadcastIntent()
                        .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_STOP)
        );
    }


    public void sendPlayBroadcast() {
        sendBroadcast(
                getNewBroadcastIntent()
                        .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_PLAY)
        );
    }


    public void sendPresetsBroadcast(float[] presets) {
        sendBroadcast(
                getNewBroadcastIntent()
                        .putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_SET_PRESETS)
                        .putExtra(PlayBroadcastReceiver.FIELD_PRESETS, presets)
        );
    }
}
