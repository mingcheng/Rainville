package com.gracecode.android.rain.ui.fragment;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.helper.StopPlayTimeoutHelper;
import com.gracecode.android.rain.receiver.PlayBroadcastReceiver;

import java.util.ArrayList;

public class SetTimerFragment extends PlayerFragment {
    private static final int VALUE_STEP = 5;

    private NumberPicker mNumberPicker;

    public SetTimerFragment() {

    }

    private BroadcastReceiver mBroadcastReceiver = new PlayBroadcastReceiver() {

        @Override
        public void onPlay() {
            mNumberPicker.setEnabled(true);
        }

        @Override
        public void onStop() {
            mNumberPicker.setValue(0);
            mNumberPicker.setEnabled(false);
        }

        @Override
        public void onSetVolume(int track, float volume) {

        }

        @Override
        public void onSetPresets(float[] presets) {

        }

        @Override
        public void onHeadsetPlugged() {
            mNumberPicker.setEnabled(true);
        }

        @Override
        public void onHeadsetUnPlugged() {
            mNumberPicker.setEnabled(false);
        }

        @Override
        public void onPlayStopTimeout(long timeout, long remain, boolean byUser) {
            int value = (int) (Math.ceil(timeout / (60 * 1000)) / VALUE_STEP);
            if (value != mNumberPicker.getValue()) {
                mNumberPicker.setValue(value);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_timer, null);
        if (view != null) {
            mNumberPicker = (NumberPicker) view.findViewById(R.id.timeout_picker);

            String[] values = getDisplayedValues();
            mNumberPicker.setMaxValue(values.length - 1);
            mNumberPicker.setWrapSelectorWheel(false);
            mNumberPicker.setDisplayedValues(values);
        }

        return view;
    }


    /**
     * 需要显示的数字
     *
     * @return
     */
    private String[] getDisplayedValues() {
        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i <= StopPlayTimeoutHelper.MAX_TIMEOUT_MINUTES; i += VALUE_STEP) {
            result.add(i == 0 ? getString(android.R.string.cancel) : Integer.toString(i));
        }

        return result.toArray(new String[result.size()]);
    }


    @Override
    BroadcastReceiver getBroadcastReceiver() {
        return mBroadcastReceiver;
    }

    @Override
    public void onStart() {
        super.onStart();

        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                int minutes = i2 * VALUE_STEP;
                SendBroadcastHelper.sendPlayStopTimeoutBroadcast(getActivity(), minutes * (60 * 1000), true);
            }
        });
    }
}
