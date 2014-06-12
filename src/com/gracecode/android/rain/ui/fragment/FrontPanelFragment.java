package com.gracecode.android.rain.ui.fragment;

import android.content.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.gracecode.android.common.helper.DateHelper;
import com.gracecode.android.common.helper.UIHelper;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.helper.StopPlayTimeoutHelper;
import com.gracecode.android.rain.helper.TypefaceHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;
import com.gracecode.android.rain.serivce.PlayService;
import com.gracecode.android.rain.ui.widget.SimplePanel;

public class FrontPanelFragment extends PlayerFragment
        implements SimplePanel.SimplePanelListener, View.OnClickListener, MenuItem.OnMenuItemClickListener {

    private static final String PREF_IS_FIRST_OPEN_PANEL = "PREF_IS_FIRST_OPEN_PANEL";

    private ToggleButton mToggleButton;
    private SimplePanel mFrontPanel;
    private ToggleButton mPlayButton;
    private TextView mHeadsetNeeded;
    private TextView mCountDownTextView;

    private int mFocusPlayTime = 0;
    static private final int MAX_FOCUS_PLAY_TIMES = 12;

    private RainApplication mRainApplication;
    private MenuItem mPlayMenuItem;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences mPreferences;

    private BroadcastReceiver mBroadcastReceiver = new PlayBroadcastReceiver() {
        @Override
        public void onPlay() {
            setPlaying();
        }

        @Override
        public void onStop() {
            setStopped();
        }

        @Override
        public void onSetVolume(int track, int volume) {

        }

        @Override
        public void onSetPresets(float[] presets) {

        }

        @Override
        public void onHeadsetPlugged() {
            setAsNormal();
        }

        @Override
        public void onHeadsetUnPlugged() {
            setHeadsetNeeded();
            setStopped();
        }

        @Override
        public void onPlayStopTimeout(long timeout, long remain, boolean byUser) {
            if (!byUser && remain != StopPlayTimeoutHelper.NO_REMAIN) {
                String countdown = DateHelper.getCountDownString(remain);
                if (mCountDownTextView.getVisibility() != View.VISIBLE) {
                    mCountDownTextView.setVisibility(View.VISIBLE);
                }
                mCountDownTextView.setText(countdown);
            } else {
                mCountDownTextView.setVisibility(View.INVISIBLE);
            }
        }
    };


    public void setPlayMenuItem(MenuItem item) {
        this.mPlayMenuItem = item;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRainApplication = RainApplication.getInstance();
        mSharedPreferences = mRainApplication.getSharedPreferences();
        mPreferences = getActivity().getSharedPreferences(FrontPanelFragment.class.getName(), Context.MODE_PRIVATE);
    }


    /**
     * 自定义字体样式
     */
    private void setCustomFonts() {
        UIHelper.setCustomTypeface((ViewGroup) getView(), TypefaceHelper.getTypefaceMusket2(getActivity()));

        ((TextView) getView().findViewById(R.id.icon))
                .setTypeface(TypefaceHelper.getTypefaceWeather(getActivity()));

        if (mToggleButton != null) {
            mToggleButton.setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));
        }

        if (mHeadsetNeeded != null) {
            mHeadsetNeeded.setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));
        }

        if (mCountDownTextView != null) {
            mCountDownTextView.setTypeface(TypefaceHelper.getTypefaceRoboto(getActivity()));
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        mToggleButton = (ToggleButton) getView().findViewById(R.id.toggle_panel);
        mToggleButton.setOnClickListener(this);
        onClosed(); // The Panel is closed when initial launched.

        mPlayButton = (ToggleButton) getView().findViewById(R.id.toggle_play);
        mPlayButton.setOnClickListener(this);

        if (mRainApplication.isMeizuDevice()) {
            mPlayButton.setVisibility(View.INVISIBLE);
        }

        mHeadsetNeeded = (TextView) getView().findViewById(R.id.headset_needed);
        mHeadsetNeeded.setOnClickListener(this);

        mCountDownTextView = (TextView) getView().findViewById(R.id.countdown);

        // 设置自定义的字体
        setCustomFonts();

        // 初始化界面
        setHeadsetNeeded();
    }


    public void setHeadsetNeeded() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.headset_needed);
        mHeadsetNeeded.startAnimation(animation);

        mPlayButton.setVisibility(View.INVISIBLE);
        mHeadsetNeeded.setVisibility(View.VISIBLE);
    }


    public void setAsNormal() {
        mHeadsetNeeded.clearAnimation();
        mHeadsetNeeded.setVisibility(View.INVISIBLE);
        if (!mRainApplication.isMeizuDevice()) {
            mPlayButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_front_panel, null);
    }


    @Override
    public void onOpened() {
        if (mToggleButton != null) {
            mToggleButton.setChecked(true);
        }

        // 第一次打开面板的时候，显示功能介绍
        if (isFirstOpenPanel()) {
            try {
                new ShowcaseView.Builder(getActivity())
                        .setTarget(new ViewTarget(android.R.id.list, getActivity()))
                        .setContentTitle(getString(R.string.panel_intro))
                        .setContentText(getString(R.string.panel_intro_summary))
                        .setStyle(R.style.RainShowcaseView)
                        .hideOnTouchOutside()
                        .build();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            markPanelOpened();  // 设定下次不再打开
        }
    }

    private boolean isFirstOpenPanel() {
        return mPreferences.getBoolean(PREF_IS_FIRST_OPEN_PANEL, true);
    }

    private void markPanelOpened() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(PREF_IS_FIRST_OPEN_PANEL, false);
        editor.commit();
    }

    @Override
    public void onClosed() {
        if (mToggleButton != null) {
            mToggleButton.setChecked(false);
        }
    }


    public void setFrontPanel(SimplePanel panel) {
        this.mFrontPanel = panel;
    }


    @Override
    BroadcastReceiver getBroadcastReceiver() {
        return mBroadcastReceiver;
    }

    @Override
    public void setPlaying() {
        super.setPlaying();
        mPlayButton.setChecked(true);
        if (mPlayMenuItem != null) {
            mPlayMenuItem.setIcon(android.R.drawable.ic_media_pause);
        }
    }


    @Override
    public void setStopped() {
        super.setStopped();
        mPlayButton.setChecked(false);
        mCountDownTextView.setVisibility(View.INVISIBLE);
        if (mPlayMenuItem != null) {
            mPlayMenuItem.setIcon(android.R.drawable.ic_media_play);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_panel:
                if (mFrontPanel.isOpened()) {
                    mFrontPanel.close();
                } else {
                    mFrontPanel.open();
                }
                break;

            case R.id.toggle_play:
                togglePlay();
                break;

            case R.id.headset_needed:

                try {
                    String message = getString(R.string.headset_needed);

                    // 这里有个小的彩蛋，多点击耳机图标多次就可以解锁直接使用耳机外放播放
                    if (mFocusPlayTime >= MAX_FOCUS_PLAY_TIMES) {
                        markAsPlayWithoutHeadset();
                        message = getString(R.string.play_wihout_headset);
                    }

                    UIHelper.showShortToast(getActivity(), message);
                } finally {
                    mFocusPlayTime++;
                    setStopped();
                }

                break;
        }
    }

    private void markAsPlayWithoutHeadset() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(PlayService.PREF_FOCUS_PLAY_WITHOUT_HEADSET, true);
        editor.commit();
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        mPlayMenuItem = menuItem;
        togglePlay();
        return true;
    }


    public void togglePlay() {
        if (isPlaying()) {
            SendBroadcastHelper.sendStopBroadcast(getActivity());
        } else {
            SendBroadcastHelper.sendPlayBroadcast(getActivity());
        }
    }
}
