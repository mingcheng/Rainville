package com.gracecode.android.rain.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.Rainville;
import com.gracecode.android.rain.adapter.ControlCenterAdapter;
import com.gracecode.android.rain.helper.TypefaceHelper;
import com.gracecode.android.rain.player.PlayManager;
import com.gracecode.android.rain.serivce.PlayService;
import com.gracecode.android.rain.ui.fragment.FrontPanelFragment;
import com.gracecode.android.rain.ui.widget.SimplePanel;

public class MainActivity extends FragmentActivity {
    private SimplePanel mFrontPanel;
    private FrontPanelFragment mFrontPanelFragment;
    private PlayManager mPlayManager;
    private Intent mServerIntent;
    private ViewPager mControlCenterContainer;
    private ControlCenterAdapter mControlCenterAdapter;
    private Rainville mRainville;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrontPanel = (SimplePanel) findViewById(R.id.front_panel);
        mControlCenterContainer = (ViewPager) findViewById(R.id.control_center);
        mFrontPanelFragment = new FrontPanelFragment();
        mControlCenterAdapter = new ControlCenterAdapter(getSupportFragmentManager());
        mServerIntent = new Intent(this, PlayService.class);

        mRainville = Rainville.getInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.front_panel, mFrontPanelFragment)
                .commit();

        // Rain rain rain...
        ((TextView) findViewById(R.id.poem))
                .setTypeface(TypefaceHelper.getTypefaceMusket2(this));

        getActionBar().hide();
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

        mControlCenterContainer.setAdapter(mControlCenterAdapter);
        mControlCenterContainer.setOnPageChangeListener(mControlCenterAdapter);

        mFrontPanelFragment.setFrontPanel(mFrontPanel);
        mFrontPanel.addSimplePanelListener(mFrontPanelFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setControlCenterLayout();

                startService(mServerIntent);
                bindService(mServerIntent, mConnection, Context.BIND_NOT_FOREGROUND);

                FlurryAgent.setUseHttps(true);
                FlurryAgent.onStartSession(MainActivity.this, getString(R.string.app_key));
            }
        }, 100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayManager != null && !mPlayManager.isPlaying()) {
            stopService(mServerIntent);
        }

        try {
            unbindService(mConnection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        FlurryAgent.onEndSession(this);
    }


    private void setControlCenterLayout() {
        int height = getControlCenterHeight();

        RelativeLayout parentView = (RelativeLayout) mControlCenterContainer.getParent();
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);

        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.height = height;
        parentView.setLayoutParams(params);
    }


    private int getControlCenterHeight() {
        int height = mFrontPanel.getRootView().getMeasuredHeight();
        return (int) (height * (1 - mFrontPanel.getSlideRatio()) * .9);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mRainville.isMeizuDevice()) {
            getMenuInflater().inflate(R.menu.main, menu);
            MenuItem menuItem = menu.findItem(R.id.action_play);
            if (menuItem != null) {
                menuItem.setOnMenuItemClickListener(mFrontPanelFragment);
                mFrontPanelFragment.setPlayMenuItem(menuItem);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_feedback:
                PackageInfo info = mRainville.getPackageInfo();
                mRainville.sendFeedbackEmail(MainActivity.this,
                        String.format(getString(R.string.feedback_subject), getString(R.string.app_name), info.versionName)
                );
                break;

            case R.id.action_play:
                break;

            case R.id.action_about:
                mRainville.showAboutDialog(this, mRainville.getPackageInfo());
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                mFrontPanelFragment.togglePlay();
                return true;
        }

        return super.onKeyUp(keyCode, event);
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
