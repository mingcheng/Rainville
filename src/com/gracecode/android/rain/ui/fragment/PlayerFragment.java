package com.gracecode.android.rain.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.gracecode.android.rain.helper.StopPlayTimeoutHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;
import com.gracecode.android.rain.serivce.PlayService;

abstract class PlayerFragment extends Fragment {
    private static boolean mPlaying = false;
    protected Handler mHandler = new Handler();

    abstract BroadcastReceiver getBroadcastReceiver();

    public void setPlaying() {
        mPlaying = true;
    }

    public void setStopped() {
        mPlaying = false;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        for (String action : new String[]{
                Intent.ACTION_HEADSET_PLUG,
                PlayService.ACTION_A2DP_HEADSET_PLUG,
                PlayBroadcastReceiver.ACTION_PLAY_BROADCAST,
                StopPlayTimeoutHelper.ACTION_SET_STOP_TIMEOUT
        }) {
            filter.addAction(action);
        }

        getActivity().registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(getBroadcastReceiver());
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(getBroadcastReceiver());
    }
}
