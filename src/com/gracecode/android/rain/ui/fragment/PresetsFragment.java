package com.gracecode.android.rain.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.gracecode.android.common.Logger;
import com.gracecode.android.rain.BuildConfig;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.adapter.PresetsAdapter;
import com.gracecode.android.rain.helper.MixerPresetsHelper;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;

public class PresetsFragment extends PlayerFragment
        implements MixerPresetsHelper, AdapterView.OnItemClickListener {
    public static final String PREF_SAVED_PRESET = "pref_saved_preset_name";
    private static final int DEFAULT_PRESET_POSITION = 0;

    private PresetsAdapter mAdapter;
    private SharedPreferences mPreferences;
    private ListView mListView;
    private boolean isDisabled = false;
    private String[] mPresets;

    public PresetsFragment() {
        super();
    }

    /**
     * 针对广播的不同逻辑
     */
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
        public void onSetVolume(int track, float volume) {

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

    /**
     * 初始化界面以及逻辑
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPresets = getResources().getStringArray(R.array.presets);
        mAdapter = new PresetsAdapter(getActivity(), mPresets);
        mPreferences = getActivity().getSharedPreferences(PresetsFragment.class.getName(), Context.MODE_PRIVATE);

        // 恢复上次播放的预制
        int preset = mPreferences.getInt(PREF_SAVED_PRESET, DEFAULT_PRESET_POSITION);
        mAdapter.setCurrentPosition(preset);

        if (BuildConfig.DEBUG) {
            Logger.i("Set preset position " + preset + " which name is " + mPresets[preset]);
        }

        SendPresetsBroadcast(preset);
    }

    /**
     * 发送预设的事件广播
     */
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

    /**
     * 根据位置获取预置
     *
     * @param position
     * @return
     */
    private float[] getPresetsFromPosition(int position) {
        try {
            return ALL_PRESETS[position];
        } catch (ArrayIndexOutOfBoundsException e) {
            return ALL_PRESETS[0];
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (isDisabled) {
            Toast.makeText(getActivity(), getString(R.string.headset_needed), Toast.LENGTH_SHORT).show();
            return;
        }

        // 预置的效果名
        String presetName = mAdapter.getItem(i);

        try {
            SendPresetsBroadcast(i);
        } finally {
            if (!isPlaying()) {
                SendBroadcastHelper.sendPlayBroadcast(getActivity());
            }

            // 保存已经选择的预设
            mPreferences.edit().putInt(PREF_SAVED_PRESET, i).apply();

            // 改变高亮条
            if (!presetName.equals(mAdapter.getCurrentPreset())) {
                mAdapter.setCurrentPresetName(presetName);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
