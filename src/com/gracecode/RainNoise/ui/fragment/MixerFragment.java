package com.gracecode.RainNoise.ui.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.TypefaceHelper;
import com.gracecode.RainNoise.player.PlayerManager;
import com.gracecode.RainNoise.ui.widget.VerticalSeekBar;

public class MixerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private PlayerManager mPlayerManager;
    private VerticalSeekBar[] mSeekBars = new VerticalSeekBar[PlayerManager.MAX_TRACKS_NUM];
    private int[] mVolumes = new int[PlayerManager.MAX_TRACKS_NUM];

    private int seek = 0;

    public MixerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mixer, null);
        findSeekBars((ViewGroup) view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        TypefaceHelper.setAllTypeface((ViewGroup) getView(),
                Typeface.createFromAsset(getActivity().getAssets(), "elegant.ttf"));

        bindSeekBarsListener();
        getView().findViewById(R.id.reset).setOnClickListener(this);
    }


    private void bindSeekBarsListener() {
        for (int i = 0; i < PlayerManager.MAX_TRACKS_NUM; i++) {
            if (mSeekBars[i] != null)
                mSeekBars[i].setOnSeekBarChangeListener(this);
        }
    }


    private void findSeekBars(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);
            if (v instanceof VerticalSeekBar) {
                int track = seek++;
                if (track < PlayerManager.MAX_TRACKS_NUM) {
                    v.setTag(track);
                    mSeekBars[track] = (VerticalSeekBar) v;
                }
            } else if (v instanceof ViewGroup) {
                findSeekBars((ViewGroup) v);
            }
        }
    }


    private void syncVolume() {
        for (int i = 0; i < PlayerManager.MAX_TRACKS_NUM; i++) {
            int volume = (mVolumes[i] != 0) ? mVolumes[i] : mPlayerManager.getVolume(i);
            if (mSeekBars[i] != null) {
                mSeekBars[i].setMax(mPlayerManager.getMaxVolume());
                mSeekBars[i].setProgress(volume);
            }
        }
    }


    public void setPlayerManager(PlayerManager manager) {
        this.mPlayerManager = manager;
    }


    public void refresh() {
        syncVolume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int track = (Integer) seekBar.getTag();
        if (track < PlayerManager.MAX_TRACKS_NUM && mPlayerManager != null) {
            mPlayerManager.setVolume(track, seekBar.getProgress());
            seekBar.refreshDrawableState();
            seekBar.invalidate();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        syncVolume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                setAllVolume(setDefaultVolumes());
                break;
        }
    }

    public int[] setDefaultVolumes() {
        int volume = mPlayerManager.getDefaultVolume();
        mVolumes = new int[PlayerManager.MAX_TRACKS_NUM];
        for (int i = 0; i < PlayerManager.MAX_TRACKS_NUM; i++) {
            mVolumes[i] = volume;
        }

        return mVolumes;
    }

    public void setAllVolume(int[] volume) {
        for (int i = 0; i < PlayerManager.MAX_TRACKS_NUM; i++) {
            mPlayerManager.setVolumeBySmooth(i, volume[i]);
        }

        syncVolume();
    }


    @Override
    public void onStop() {
        super.onStop();
        syncVolume();
    }
}
