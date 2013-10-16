package com.gracecode.RainNoise.ui.fragment;

import android.animation.Animator;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.gracecode.RainNoise.BuildConfig;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.TypefaceHelper;
import com.gracecode.RainNoise.player.PlayerManager;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

public class FrontPanelFragment extends Fragment implements SimplePanel.SimplePanelListener, View.OnClickListener {
    private static final String TAG = FrontPanelFragment.class.getName();

    private ToggleButton mToggleButton;
    private SimplePanel mFrontPanel;
    private PlayerManager mPlayerManager;
    private ToggleButton mPlayButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setCustomFonts() {
        Typeface typefaceMusket2 = Typeface.createFromAsset(getActivity().getAssets(), "musket2.otf");
        Typeface typefaceWeather = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        Typeface typefaceElegant = Typeface.createFromAsset(getActivity().getAssets(), "elegant.ttf");

        TypefaceHelper.setAllTypeface((ViewGroup) getView(), typefaceMusket2);
        ((TextView) getView().findViewById(R.id.icon)).setTypeface(typefaceWeather);
        if (mToggleButton != null) {
            mToggleButton.setTypeface(typefaceElegant);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mToggleButton = (ToggleButton) getView().findViewById(R.id.toggle_panel);
        mToggleButton.setOnClickListener(this);
        mToggleButton.setChecked(false);

        mPlayButton = (ToggleButton) getView().findViewById(R.id.toggle_play);
        mPlayButton.setOnClickListener(this);
        mPlayButton.setChecked(false);

        setCustomFonts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_front_panel, null);
    }


    @Override
    public void onOpened() {
        if (mToggleButton != null)
            mToggleButton.setChecked(true);
    }

    @Override
    public void onClosed() {
        if (mToggleButton != null) {
            mToggleButton.setChecked(false);
        }
    }

    public void togglePlay() throws RuntimeException {
        if (mPlayerManager.isPlaying()) {
            mPlayerManager.muteSmoothly(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mPlayButton.setChecked(false);
                    mPlayButton.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mPlayerManager.stop();
                    mPlayButton.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

        } else {
            mPlayerManager.play();
            mPlayButton.setChecked(true);
        }
    }

    public void setFrontPanel(SimplePanel panel) {
        this.mFrontPanel = panel;
    }

    public void setPlayerManager(PlayerManager manager) {
        this.mPlayerManager = manager;
    }

    public void refresh() {
        if (mPlayerManager != null && mPlayerManager.isPlaying()) {
            mPlayButton.setChecked(true);
        } else {
            mPlayButton.setChecked(false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggle_panel:
                if (mFrontPanel.isOpened()) {
                    mFrontPanel.close();
                } else {
                    mFrontPanel.open();
                }
                break;

            case R.id.toggle_play:
                try {
                    togglePlay();
                } catch (RuntimeException e) {
                    if (BuildConfig.DEBUG)
                        Log.e(TAG, "Toggle play status is failed. Maybe player not finished?");
                }
                break;
        }
    }
}
