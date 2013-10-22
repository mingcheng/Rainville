package com.gracecode.android.rain.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.adapter.ControlCenterAdapter;
import com.gracecode.android.rain.helper.TypefaceHelper;
import com.gracecode.android.rain.player.PlayManager;
import com.gracecode.android.rain.serivce.PlayService;
import com.gracecode.android.rain.ui.fragment.FrontPanelFragment;
import com.gracecode.android.rain.ui.widget.SimplePanel;

public class MainActivity extends Activity {
    private SimplePanel mFrontPanel;
    private FrontPanelFragment mFrontPanelFragment;
    private PlayManager mPlayManager;
    private Intent mServerIntent;
    private ViewPager mControlCenterContainer;
    private ControlCenterAdapter mControlCenterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrontPanel = (SimplePanel) findViewById(R.id.front_panel);
        mControlCenterContainer = (ViewPager) findViewById(R.id.control_center);
        mFrontPanelFragment = new FrontPanelFragment();
        mControlCenterAdapter = new ControlCenterAdapter(getFragmentManager());
        mServerIntent = new Intent(this, PlayService.class);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.front_panel, mFrontPanelFragment)
                .commit();

        // Rain rain rain...
        ((TextView) findViewById(R.id.poem))
                .setTypeface(TypefaceHelper.getTypefaceMusket2(this));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mFrontPanel.isOpened()) {
            mFrontPanel.close();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStart() {
        super.onStart();
        setControlCenterLayout();

        mControlCenterContainer.setAdapter(mControlCenterAdapter);
        mControlCenterContainer.setOnPageChangeListener(mControlCenterAdapter);

        mFrontPanelFragment.setFrontPanel(mFrontPanel);
        mFrontPanel.addSimplePanelListener(mFrontPanelFragment);

        startService(mServerIntent);
        bindService(mServerIntent, mConnection, Context.BIND_NOT_FOREGROUND);

        FlurryAgent.setUseHttps(true);
        FlurryAgent.onStartSession(this, getString(R.string.app_key));
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayManager != null && !mPlayManager.isPlaying()) {
            stopService(mServerIntent);
        }

        unbindService(mConnection);
        FlurryAgent.onEndSession(this);
    }


    private void setControlCenterLayout() {
        int height = getControlCenterHeight();
        mControlCenterContainer.setLayoutParams(
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
    }


    private int getControlCenterHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return (int) (displaymetrics.heightPixels * (1 - mFrontPanel.getSlideRatio() * 1.024));
    }


    private PlayService.PlayBinder mBinder;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            if (binder instanceof PlayService.PlayBinder) {
                mBinder = (PlayService.PlayBinder) binder;
                mPlayManager = mBinder.getPlayManager();

                if (mPlayManager.isPlaying()) {
                    mFrontPanelFragment.setPlaying();
                } else {
                    mFrontPanelFragment.setStopped();
                }
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mBinder = null;
            mPlayManager = null;
        }
    };
}
