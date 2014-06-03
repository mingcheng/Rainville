package com.gracecode.android.rain;

import com.gracecode.android.common.CustomApplication;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 14-4-17
 */
public class RainApplication extends CustomApplication {
    private static RainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static RainApplication getInstance() {
        return mInstance;
    }
}
