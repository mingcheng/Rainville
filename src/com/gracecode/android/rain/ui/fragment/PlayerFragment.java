package com.gracecode.android.rain.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import com.gracecode.android.rain.helper.StopPlayTimeoutHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;

/**
 * 和播放有关联的 Fragment
 */
abstract class PlayerFragment extends Fragment {
    private static boolean mPlaying = false;

    abstract BroadcastReceiver getBroadcastReceiver();

    public PlayerFragment() {
        super();
    }

    /**
     * 设置正在播放的状态
     */
    public void setPlaying() {
        mPlaying = true;
    }

    /**
     * 设置停止的状态
     */
    public void setStopped() {
        mPlaying = false;
    }

    /**
     * 判断是否在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mPlaying;
    }

    /**
     * 注册默认的广播
     *
     * @param receiver
     */
    public void registerReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        for (String action : new String[]{
//                Intent.ACTION_HEADSET_PLUG,
//                PlayService.ACTION_A2DP_HEADSET_PLUG,
                PlayBroadcastReceiver.ACTION_PLAY_BROADCAST,
                StopPlayTimeoutHelper.ACTION_SET_STOP_TIMEOUT
        }) {
            filter.addAction(action);
        }

        getActivity().registerReceiver(receiver, filter);
    }

    /**
     * 注销广播
     *
     * @param receiver
     */
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
