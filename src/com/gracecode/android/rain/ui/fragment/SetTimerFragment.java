package com.gracecode.android.rain.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.serivce.PlayService;

public class SetTimerFragment extends Fragment {
    private NumberPicker mNumberPicker;

    public SetTimerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_timer, null);
        if (view != null) {
            mNumberPicker = (NumberPicker) view.findViewById(R.id.timeout_picker);
            mNumberPicker.setMaxValue(PlayService.MAX_TIMEOUT_MINUTES);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
