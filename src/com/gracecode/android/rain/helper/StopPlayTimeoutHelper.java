package com.gracecode.android.rain.helper;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

public class StopPlayTimeoutHelper {
    public static final int MAX_TIMEOUT_MINUTES = 60;
    public static final String ACTION_SET_STOP_TIMEOUT = "com.gracecode.android.rain.ActionStopPlayTimeout";

    // 默认定时停止时间，0 为不设定或者清除
    public static final long DEFAULT_STOP_TIMEOUT = 0 * 60 * 1000;
    public static final String FIELD_REMAIN = "extra_timeout_remain";
    public static final long NO_REMAIN = -1;

    private final Context mContext;
    private long mFinishTime;

    public StopPlayTimeoutHelper(Context context) {
        mContext = context;
    }

    private Runnable mStopPlayRunnable = new Runnable() {
        @Override
        public void run() {
            SendBroadcastHelper.sendStopBroadcast(mContext);
        }
    };

    private Handler mHandler = new Handler();
    private long mTimeout = DEFAULT_STOP_TIMEOUT;

    /**
     * 清除定时停止
     */
    public void clearStopPlayTimeout() {
        mHandler.removeCallbacks(mStopPlayRunnable);
        mTimeout = DEFAULT_STOP_TIMEOUT;
    }

    /**
     * 增加定时定制播放
     *
     * @param timeout
     */
    public void setStopPlayTimeout(long timeout) {
        clearStopPlayTimeout();
        if (timeout > 0) {
            mTimeout = timeout;
            mFinishTime = mTimeout + SystemClock.uptimeMillis();
            mHandler.postAtTime(mStopPlayRunnable, mFinishTime);
        }
    }

    public void setStopPlayTimeout() {
        setStopPlayTimeout(DEFAULT_STOP_TIMEOUT);
    }

    public long getTimeout() {
        return mTimeout;
    }

    public long getTimeoutRemain() {
        long remain = mFinishTime - SystemClock.uptimeMillis();
        return remain > 0 ? remain : NO_REMAIN;
    }

    public void sendStateBroadcast() {
        SendBroadcastHelper.sendPlayStopTimeoutBroadcast(mContext, getTimeout(), getTimeoutRemain(), false);
    }
}
