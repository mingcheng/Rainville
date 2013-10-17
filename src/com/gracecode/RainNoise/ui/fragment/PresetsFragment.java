package com.gracecode.RainNoise.ui.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.gracecode.RainNoise.adapter.PresetsAdapter;
import com.gracecode.RainNoise.helper.MixerPresetsHelper;
import com.gracecode.RainNoise.player.PlayerBinder;
import com.gracecode.RainNoise.player.PlayerManager;

public class PresetsFragment extends ListFragment implements PlayerBinder, MixerPresetsHelper {
    private PresetsAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new PresetsAdapter(getActivity(), PRESET_TITLES);
        setListAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getActivity(), ALL_PRESETS[position].toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bindPlayerManager(PlayerManager manager) {
    }

    @Override
    public void unbindPlayerManager() {
    }

    @Override
    public void refresh() {

    }
}
