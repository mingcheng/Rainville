package com.gracecode.RainNoise.ui.fragment;

import android.content.*;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.flurry.android.FlurryAgent;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.adapter.PresetsAdapter;
import com.gracecode.RainNoise.helper.MixerPresetsHelper;
import com.gracecode.RainNoise.helper.SendBroadcastHelper;
import com.gracecode.RainNoise.player.BufferedPlayer;
import com.gracecode.RainNoise.player.PlayManager;
import com.gracecode.RainNoise.receiver.PlayBroadcastReceiver;

import java.util.HashMap;

public class PresetsFragment extends PlayerFragment implements MixerPresetsHelper, AdapterView.OnItemClickListener {
    private PresetsAdapter mAdapter;
    private SharedPreferences mSharedPreferences;
    private ListView mListView;
    private boolean isDisabled = false;

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
            savePresets(presets);
        }

        @Override
        public void onHeadsetPlugged() {
            setDisabled(false);
        }

        @Override
        public void onHeadsetUnPlugged() {
            setDisabled(true);
        }
    };


    public void setDisabled(boolean flag) {
        this.isDisabled = flag;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new PresetsAdapter(getActivity(), PRESET_TITLES);
        mSharedPreferences = getActivity()
                .getSharedPreferences(PresetsFragment.class.getName(), Context.MODE_PRIVATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_persents, null);
        mListView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        SendBroadcastHelper.sendPresetsBroadcast(getActivity(), getPresets());

        getActivity().registerReceiver(mBroadcastReceiver,
                new IntentFilter(PlayBroadcastReceiver.PLAY_BROADCAST_NAME));
        getActivity().registerReceiver(mBroadcastReceiver,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        setDisabled(true);
    }


    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


    public void savePresets(float[] presets) {
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            mSharedPreferences.edit().putFloat("_" + i, presets[i]).commit();
        }
    }


    public float[] getPresets() {
        float[] result = new float[PlayManager.MAX_TRACKS_NUM];
        for (int i = 0; i < PlayManager.MAX_TRACKS_NUM; i++) {
            result[i] = mSharedPreferences.getFloat("_" + i, BufferedPlayer.DEFAULT_VOLUME_PERCENT);
        }

        return result;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isDisabled) {
            Toast.makeText(getActivity(), getString(R.string.headset_needed), Toast.LENGTH_SHORT).show();
            return;
        }

        SendBroadcastHelper.sendPresetsBroadcast(getActivity(), ALL_PRESETS[i]);
        if (!isPlaying()) {
            SendBroadcastHelper.sendPlayBroadcast(getActivity());

            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("SelectedPreset", PRESET_TITLES[i]);
            FlurryAgent.logEvent(getTag(), hashMap);
        }
    }
}
