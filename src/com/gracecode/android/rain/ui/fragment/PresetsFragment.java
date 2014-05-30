package com.gracecode.android.rain.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.Rainville;
import com.gracecode.android.rain.adapter.PresetsAdapter;
import com.gracecode.android.rain.helper.MixerPresetsHelper;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;
import com.gracecode.android.rain.serivce.PlayService;
import com.umeng.analytics.MobclickAgent;

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
    };


    public void setDisabled(boolean flag) {
        this.isDisabled = flag;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPresets = getResources().getStringArray(R.array.presets);
        mAdapter = new PresetsAdapter(getActivity(), mPresets);
        mSharedPreferences = Rainville.getInstance().getSharedPreferences();

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
    public void onStart() {
        super.onStart();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        // 注册响应的广播，根据广播判断状态
        IntentFilter filter = new IntentFilter();
        for (String action : new String[]{
                Intent.ACTION_HEADSET_PLUG,
                PlayBroadcastReceiver.PLAY_BROADCAST_NAME,
                PlayService.ACTION_A2DP_HEADSET_PLUG
        }) {
            filter.addAction(action);
        }
        getActivity().registerReceiver(mBroadcastReceiver, filter);

        setDisabled(true);
    }


    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mBroadcastReceiver);
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

        //UIHelper.showShortToast(getActivity(), presetName);
        MobclickAgent.onEvent(getActivity(), presetName);
    }
}
