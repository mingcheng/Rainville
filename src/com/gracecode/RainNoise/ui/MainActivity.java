package com.gracecode.RainNoise.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.player.PlayerManager;
import com.gracecode.RainNoise.serivce.PlayerService;
import com.gracecode.RainNoise.ui.fragment.FrontPanelFragment;
import com.gracecode.RainNoise.ui.fragment.MixerFragment;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

public class MainActivity extends Activity {
    private SimplePanel mFrontPanel;
    private MixerFragment mMixerFragment;
    private FrontPanelFragment mFrontPanelFragment;

    private PlayerService.MyBinder mBinder;
    private PlayerManager mPlayerManager;
    private Intent mServerIntent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrontPanel = (SimplePanel) findViewById(R.id.front_panel);
        mFrontPanelFragment = new FrontPanelFragment();
        mMixerFragment = new MixerFragment();

        mFrontPanelFragment.setFrontPanel(mFrontPanel);
        mFrontPanel.addSimplePanelListener(mFrontPanelFragment);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.front_panel, mFrontPanelFragment)
                .replace(R.id.control_center, mMixerFragment)
                .commit();

        mServerIntent = new Intent(this, PlayerService.class);
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
    }


    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }


    private void setControlCenterLayout() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float v = displaymetrics.heightPixels * (1 - mFrontPanel.getSlideRatio());
        mMixerFragment.getView()
                .setLayoutParams(
                        new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, (int) v));
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
            mMixerFragment.refresh();
            mFrontPanelFragment.refresh();
        }

        private void bindPlayerManager() {
            mMixerFragment.setPlayerManager(mPlayerManager);
            mFrontPanelFragment.setPlayerManager(mPlayerManager);
        }

        public void onServiceDisconnected(ComponentName className) {
            mBinder = null;
        }
    };
}
