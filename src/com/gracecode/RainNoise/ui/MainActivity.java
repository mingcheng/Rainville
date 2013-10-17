package com.gracecode.RainNoise.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.adapter.ControlCenterAdapter;
import com.gracecode.RainNoise.player.PlayerManager;
import com.gracecode.RainNoise.serivce.PlayerService;
import com.gracecode.RainNoise.ui.fragment.FrontPanelFragment;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

public class MainActivity extends Activity {
    private SimplePanel mFrontPanel;
    private FrontPanelFragment mFrontPanelFragment;

    private PlayerService.MyBinder mBinder;
    private PlayerManager mPlayerManager;
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
        mServerIntent = new Intent(this, PlayerService.class);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.front_panel, mFrontPanelFragment)
                .commit();

        startService(mServerIntent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService(mServerIntent, mConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        setControlCenterLayout();

        mControlCenterContainer.setAdapter(mControlCenterAdapter);
        mControlCenterContainer.setOnPageChangeListener(mControlCenterAdapter);

        mFrontPanelFragment.setFrontPanel(mFrontPanel);
        mFrontPanel.addSimplePanelListener(mFrontPanelFragment);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }


    private void setControlCenterLayout() {
        int height = getControlCenterHeight();
        mControlCenterContainer.setLayoutParams(
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
    }


    private int getControlCenterHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return (int) (displaymetrics.heightPixels * (1 - mFrontPanel.getSlideRatio() * 1.03));
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            if (binder instanceof PlayerService.MyBinder) {
                mBinder = (PlayerService.MyBinder) binder;
                mPlayerManager = mBinder.getPlayersManager();

                bindPlayerManager();
                refresh();
            }
        }

        private void refresh() {
            mControlCenterAdapter.refresh();
            mFrontPanelFragment.refresh();
        }

        private void bindPlayerManager() {
            mFrontPanelFragment.bindPlayerManager(mPlayerManager);
            mControlCenterAdapter.bindPlayerManager(mPlayerManager);
        }

        public void onServiceDisconnected(ComponentName className) {
            mControlCenterAdapter.unbindPlayerManager();
            mBinder = null;
        }
    };
}
