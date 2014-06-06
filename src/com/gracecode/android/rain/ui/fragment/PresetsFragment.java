package com.gracecode.android.rain.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.adapter.PresetsAdapter;
import com.gracecode.android.rain.helper.MixerPresetsHelper;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;

public class PresetsFragment extends PlayerFragment
        implements MixerPresetsHelper, AdapterView.OnItemClickListener {
    public static final String PREF_SAVED_PRESET_NAME = "pref_saved_preset_name";

    private PresetsAdapter mAdapter;
    private SharedPreferences mSharedPreferences;
    private ListView mListView;
    private boolean isDisabled = false;
    private String[] mPresets;

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
//            savePresets(presets);
        }

        @Override
        public void onHeadsetPlugged() {
            setDisabled(false);
        }

        @Override
        public void onHeadsetUnPlugged() {
            setDisabled(true);
        }

        @Override
        public void onPlayStopTimeout(long timeout, long remain, boolean byUser) {

        }
    };

    public void setDisabled(boolean flag) {
        this.isDisabled = flag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPresets = getResources().getStringArray(R.array.presets);
        mAdapter = new PresetsAdapter(getActivity(), mPresets);
        mSharedPreferences = RainApplication.getInstance().getSharedPreferences();

        // 恢复上次播放的预制
        String preset = mSharedPreferences.getString(PREF_SAVED_PRESET_NAME, mPresets[0]);
        mAdapter.setCurrentPresetName(preset);

        SendPresetsBroadcast(preset);
    }

    // 发送预设的事件广播
    private void SendPresetsBroadcast(String name) {
        int position = mAdapter.getPositionFromName(name);
        SendPresetsBroadcast(position);
    }

    private void SendPresetsBroadcast(int position) {
        try {
            SendBroadcastHelper.sendPresetsBroadcast(getActivity(), getPresetsFromPosition(position));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_persents, null);
        mListView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }


    @Override
    BroadcastReceiver getBroadcastReceiver() {
        return mBroadcastReceiver;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }


    private float[] getPresetsFromPosition(int position) {
        return ALL_PRESETS[position];
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isDisabled) {
            Toast.makeText(getActivity(), getString(R.string.headset_needed), Toast.LENGTH_SHORT).show();
            return;
        }

        String presetName = mAdapter.getItem(i);

        SendPresetsBroadcast(i);
        if (!isPlaying()) {
            SendBroadcastHelper.sendPlayBroadcast(getActivity());
        }

        // 保存已经选择的预设
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREF_SAVED_PRESET_NAME, presetName);
        editor.commit();

        if (!presetName.equals(mAdapter.getCurrentPreset())) {
            mAdapter.setCurrentPresetName(presetName);
            mAdapter.notifyDataSetChanged();
        }

        //MobclickAgent.onEvent(getActivity(), presetName);
    }
}
