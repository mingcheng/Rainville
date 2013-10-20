package com.gracecode.RainNoise.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.SendBroadcastHelper;
import com.gracecode.RainNoise.helper.TypefaceHelper;
import com.gracecode.RainNoise.receiver.PlayBroadcastReceiver;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

public class FrontPanelFragment extends PlayerFragment
        implements SimplePanel.SimplePanelListener, View.OnClickListener {

    private ToggleButton mToggleButton;
    private SimplePanel mFrontPanel;
    private ToggleButton mPlayButton;

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
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setCustomFonts() {
        TypefaceHelper.setAllTypeface((ViewGroup) getView(), TypefaceHelper.getTypefaceMusket2(getActivity()));
        ((TextView) getView().findViewById(R.id.icon))
                .setTypeface(TypefaceHelper.getTypefaceWeather(getActivity()));

        if (mToggleButton != null) {
            mToggleButton.setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));
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

        setCustomFonts();
        getActivity().registerReceiver(mBroadcastReceiver,
                new IntentFilter(PlayBroadcastReceiver.PLAY_BROADCAST_NAME));
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
    }

    @Override
    public void setStopped() {
        super.setStopped();
        mPlayButton.setChecked(false);
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
                if (isPlaying()) {
                    SendBroadcastHelper.sendStopBroadcast(getActivity());
                } else {
                    SendBroadcastHelper.sendPlayBroadcast(getActivity());
                }
                break;
        }
    }
}
