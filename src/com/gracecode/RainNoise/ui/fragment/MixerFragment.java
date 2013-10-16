package com.gracecode.RainNoise.ui.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.TypefaceHelper;

public class MixerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mixer, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        TypefaceHelper.setAllTypeface((ViewGroup) getView(),
                Typeface.createFromAsset(getActivity().getAssets(), "elegant.ttf"));

    }
}
