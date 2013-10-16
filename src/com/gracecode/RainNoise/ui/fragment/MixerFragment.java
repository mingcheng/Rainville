package com.gracecode.RainNoise.ui.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.TypefaceHelper;
import com.gracecode.RainNoise.player.PlayerManager;

public class MixerFragment extends Fragment {

    private PlayerManager mPlayerManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mixer, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        TypefaceHelper.setAllTypeface((ViewGroup) getView(),
                Typeface.createFromAsset(getActivity().getAssets(), "elegant.ttf"));

    }

    public void setPlayerManager(PlayerManager manager) {
        this.mPlayerManager = manager;
    }

    public void refresh() {

    }
}
