package com.gracecode.android.rain.ui;

import android.content.*;
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
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.adapter.ControlCenterAdapter;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.helper.TypefaceHelper;
import com.gracecode.android.rain.player.PlayManager;
import com.gracecode.android.rain.serivce.PlayService;
import com.gracecode.android.rain.ui.fragment.FrontPanelFragment;
import com.gracecode.android.rain.ui.widget.SimplePanel;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

public class MainActivity extends FragmentActivity {
    private static final String SAVED_CURRENT_ITEM = "pref_saved_current_item";
    private static final String PREF_IS_FIRST_RUN = "pref_is_first_run";

    private SimplePanel mFrontPanel;
    private FrontPanelFragment mFrontPanelFragment;
    private PlayManager mPlayManager;
    private Intent mServerIntent;
    private ViewPager mControlCenterContainer;
    private ControlCenterAdapter mControlCenterAdapter;
    private RainApplication mRainApplication;
    private SharedPreferences mPreferences;

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFrontPanel = (SimplePanel) findViewById(R.id.front_panel);
        mControlCenterContainer = (ViewPager) findViewById(R.id.control_center);
        mFrontPanelFragment = new FrontPanelFragment();
        mControlCenterAdapter = new ControlCenterAdapter(getSupportFragmentManager());
        mServerIntent = new Intent(this, PlayService.class);

        mRainApplication = RainApplication.getInstance();
        mPreferences = getSharedPreferences(MainActivity.class.getName(), Context.MODE_PRIVATE);

        // 设置界面，通充 Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.front_panel, mFrontPanelFragment)
                .commit();

        // Rain rain rain...
        ((TextView) findViewById(R.id.poem))
                .setTypeface(TypefaceHelper.getTypefaceMusket2(this));

        getActionBar().hide();
        XiaomiUpdateAgent.update(this);
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

        int currentItem = mPreferences.getInt(SAVED_CURRENT_ITEM, 0);
        mControlCenterContainer.setCurrentItem(currentItem);

        // 如果是首次启动，则显示提示信息框
        if (isFirstRun()) {
            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.headset_needed, this))
                    .setContentTitle(getString(R.string.welcome_use_rainville))
                    .setContentText(getString(R.string.welcome_use_rainville_summary))
                    .setStyle(R.style.RainShowcaseView)
                    .hideOnTouchOutside()
                    .build();

            markNotFirstRun(); // 标记下次不再启动
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 当确定其他的 UI 都渲染好了以后，重新放置控制台的位置
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setControlCenterLayout();
            }
        }, 1000);

        startService(mServerIntent);
        bindService(mServerIntent, mConnection, Context.BIND_NOT_FOREGROUND);
        MobclickAgent.onResume(this);
    }

    private void markNotFirstRun() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(PREF_IS_FIRST_RUN, false);
        editor.commit();
    }

    private boolean isFirstRun() {
        return mPreferences.getBoolean(PREF_IS_FIRST_RUN, true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            unbindService(mConnection);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayManager != null && !mPlayManager.isPlaying()) {
            stopService(mServerIntent);
        }

        // 保存上次面板滚动的位置
        int currentItem = mControlCenterContainer.getCurrentItem();
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(SAVED_CURRENT_ITEM, currentItem);
        editor.commit();
    }


    /**
     * 自动设定控制面板的高度, @TODO 需要检查更多机型的兼容性
     */
    private void setControlCenterLayout() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mControlCenterContainer.getLayoutParams();
        params.height = getControlCenterHeight();
        mControlCenterContainer.setLayoutParams(params);
    }


    private int getControlCenterHeight() {
        int height = mFrontPanel.getRootView().getMeasuredHeight();
        return (int) (height * (1 - mFrontPanel.getSlideRatio()) * .9);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mRainApplication.isMeizuDevice()) {
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
                PackageInfo info = mRainApplication.getPackageInfo();
                mRainApplication.sendFeedbackEmail(MainActivity.this,
                        String.format(getString(R.string.feedback_subject), getString(R.string.app_name), info.versionName)
                );
                break;

            case R.id.action_play:
                break;

            case R.id.action_about:
                mRainApplication.showAboutDialog(this, mRainApplication.getPackageInfo());
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

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 发送广播，重新设置UI状态
                        if (mPlayManager.isPlaying()) {
                            SendBroadcastHelper.sendPlayBroadcast(MainActivity.this);
                        } else {
                            SendBroadcastHelper.sendStopBroadcast(MainActivity.this);
                        }
                    }
                }, 200);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mBinder = null;
            mPlayManager = null;
        }
    };
}
