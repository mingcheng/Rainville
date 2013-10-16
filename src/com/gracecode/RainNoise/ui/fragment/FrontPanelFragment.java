package com.gracecode.RainNoise.ui.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

public class FrontPanelFragment extends Fragment implements SimplePanel.SimplePanelListener, View.OnClickListener {
    private RelativeLayout mView;
    private static Typeface mTypefaceMusket2;
    private static Typeface mTypefaceWeather;
    private static Typeface mTypefaceElegant;
    private ToggleButton mToggleButton;
    private SimplePanel mFrontPanel;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();

        mToggleButton = (ToggleButton) mView.findViewById(R.id.toggle_panel);
        mToggleButton.setOnClickListener(this);
        mToggleButton.setChecked(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = (RelativeLayout) inflater.inflate(R.layout.front_panel, null);
        createTypeface();
        setCustomFonts(mView);
        return mView;
    }


    private void createTypeface() {
        mTypefaceMusket2 = Typeface.createFromAsset(getActivity().getAssets(), "musket2.otf");
        mTypefaceWeather = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
        mTypefaceElegant = Typeface.createFromAsset(getActivity().getAssets(), "elegant.ttf");
    }

    private void setCustomFonts(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);
            if (v instanceof ViewGroup) {
                setCustomFonts((ViewGroup) v);
            } else if (view.getId() == R.id.icon) {
                ((TextView) v).setTypeface(mTypefaceWeather);
            } else if (v instanceof ToggleButton) {
                ((ToggleButton) v).setTypeface(mTypefaceElegant);
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(mTypefaceMusket2);
            }
        }
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
