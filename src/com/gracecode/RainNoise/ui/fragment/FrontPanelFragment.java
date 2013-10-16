package com.gracecode.RainNoise.ui.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.helper.TypefaceHelper;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

public class FrontPanelFragment extends Fragment implements SimplePanel.SimplePanelListener, View.OnClickListener {
    private static Typeface mTypefaceMusket2;
    private static Typeface mTypefaceWeather;
    private static Typeface mTypefaceElegant;
    private ToggleButton mToggleButton;
    private SimplePanel mFrontPanel;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setCustomFonts() {
        mTypefaceMusket2 = Typeface.createFromAsset(getActivity().getAssets(), "musket2.otf");
        mTypefaceWeather = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        mTypefaceElegant = Typeface.createFromAsset(getActivity().getAssets(), "elegant.ttf");

        TypefaceHelper.setAllTypeface((ViewGroup) getView(), mTypefaceMusket2);
        ((TextView) getView().findViewById(R.id.icon)).setTypeface(mTypefaceWeather);
        if (mToggleButton != null) {
            mToggleButton.setTypeface(mTypefaceElegant);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mToggleButton = (ToggleButton) getView().findViewById(R.id.toggle_panel);
        mToggleButton.setOnClickListener(this);
        mToggleButton.setChecked(false);

        setCustomFonts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.front_panel, null);

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

    @Override
    public void onClick(View view) {
        if (mFrontPanel != null) {
            if (mFrontPanel.isOpened()) {
                mFrontPanel.close();
            } else {
                mFrontPanel.open();
            }
        }
    }

    public void setFrontPanel(SimplePanel panel) {
        this.mFrontPanel = panel;
    }
}
