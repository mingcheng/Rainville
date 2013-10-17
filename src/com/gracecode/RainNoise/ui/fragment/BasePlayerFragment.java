package com.gracecode.RainNoise.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import com.gracecode.RainNoise.helper.PlayBroadcastReceiver;


abstract class BasePlayerFragment extends Fragment {
    private boolean mPlaying = false;

    public void setPlaying() {
        mPlaying = true;
    }

    public void setStopped() {
        mPlaying = false;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void sendStopBroadcast() {
        Intent intent = new Intent(PlayBroadcastReceiver.PLAY_BROADCAST_NAME);
        intent.putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_STOP);
        getActivity().sendBroadcast(intent);
    }

    public void sendPlayBroadcast() {
        Intent intent = new Intent(PlayBroadcastReceiver.PLAY_BROADCAST_NAME);
        intent.putExtra(PlayBroadcastReceiver.FIELD_CMD, PlayBroadcastReceiver.CMD_PLAY);
        getActivity().sendBroadcast(intent);
    }
}
