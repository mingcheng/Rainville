package com.gracecode.android.rain.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.Rainville;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.helper.TypefaceHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;
import com.gracecode.android.rain.ui.widget.SimplePanel;

public class FrontPanelFragment extends PlayerFragment
        implements SimplePanel.SimplePanelListener, View.OnClickListener, MenuItem.OnMenuItemClickListener {

    private ToggleButton mToggleButton;
    private SimplePanel mFrontPanel;
    private ToggleButton mPlayButton;
    private TextView mHeadsetNeeded;

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
    };
    private Rainville mRainville;
    private MenuItem mPlayMenuItem;

    public void setPlayMenuItem(MenuItem item) {
        this.mPlayMenuItem = item;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRainville = Rainville.getInstance();
    }


    private void setCustomFonts() {
        TypefaceHelper.setAllTypeface((ViewGroup) getView(),
                TypefaceHelper.getTypefaceMusket2(getActivity()));
        ((TextView) getView().findViewById(R.id.icon))
                .setTypeface(TypefaceHelper.getTypefaceWeather(getActivity()));

        if (mToggleButton != null) {
            mToggleButton.setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));
        }

        if (mHeadsetNeeded != null) {
            mHeadsetNeeded.setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));
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

        if (mRainville.isMeizuDevice()) {
            mPlayButton.setVisibility(View.INVISIBLE);
        }

        mHeadsetNeeded = (TextView) getView().findViewById(R.id.headset_needed);
        mHeadsetNeeded.setOnClickListener(this);

        setCustomFonts();
        getActivity().registerReceiver(mBroadcastReceiver,
                new IntentFilter(PlayBroadcastReceiver.PLAY_BROADCAST_NAME));

        getActivity().registerReceiver(mBroadcastReceiver,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

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
        if (!mRainville.isMeizuDevice()) {
            mPlayButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_front_panel, null);
    }


    @Override
    public void onOpened() {
        if (mToggleButton != null)
            mToggleButton.setChecked(true);
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
                Toast.makeText(getActivity(),
                        getString(R.string.headset_needed), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        mPlayMenuItem = menuItem;
        togglePlay();
        return true;
    }


    private void togglePlay() {
        if (isPlaying()) {
            SendBroadcastHelper.sendStopBroadcast(getActivity());
        } else {
            SendBroadcastHelper.sendPlayBroadcast(getActivity());
        }
    }
}
