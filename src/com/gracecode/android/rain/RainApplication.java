package com.gracecode.android.rain;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gracecode.android.common.CustomApplication;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: mingcheng
 * Date: 14-4-17
 */
public class RainApplication extends CustomApplication {
    private static RainApplication mInstance;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static RainApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
