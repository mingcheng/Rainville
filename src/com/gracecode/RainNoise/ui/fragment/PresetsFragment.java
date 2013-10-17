package com.gracecode.RainNoise.ui.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.gracecode.RainNoise.adapter.PresetsAdapter;
import com.gracecode.RainNoise.helper.MixerPresetsHelper;
import com.gracecode.RainNoise.player.PlayManager;
import com.gracecode.RainNoise.player.PlayerBinder;

public class PresetsFragment extends ListFragment implements PlayerBinder, MixerPresetsHelper {
    private PresetsAdapter mAdapter;
    private PlayManager mPlayManager;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new PresetsAdapter(getActivity(), PRESET_TITLES);
        mSharedPreferences = getActivity().getSharedPreferences(PresetsFragment.class.getName(), Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        setPresets(ALL_PRESETS[position]);

    }

    public void setPresets(float[] presets) {
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            int volume = (int) (mPlayManager.getMaxVolume() * presets[i]);
            mPlayManager.setVolume(i, volume);
            mSharedPreferences.edit().putFloat("_" + i, presets[i]).commit();
        }
    }

    public float[] getPresets() {
        float[] result = new float[PlayManager.MAX_TRACKS_NUM];
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            result[i] = mSharedPreferences.getFloat("_" + i, mPlayManager.getDefaultVolume());
        }

        return result;
    }

    @Override
    public void bindPlayerManager(PlayManager manager) {
        mPlayManager = manager;
    }

    @Override
    public void unbindPlayerManager() {
        mPlayManager = null;
    }

    @Override
    public void refresh() {
        setPresets(getPresets());
    }
}
