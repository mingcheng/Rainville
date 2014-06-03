package com.gracecode.android.rain.ui.fragment;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.helper.TypefaceHelper;

public class AboutFragment extends Fragment {
    private TextView mVersionTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);
        if (view != null) {
            mVersionTextView = (TextView) view.findViewById(R.id.version);
        }
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TypefaceHelper.setAllTypeface((ViewGroup) getView(), TypefaceHelper.getTypefaceMusket2(getActivity()));
    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            if (mVersionTextView != null) {
                PackageInfo packageInfo = RainApplication.getInstance().getPackageInfo();
                mVersionTextView.setText(String.format(getString(R.string.version), packageInfo.versionName, packageInfo.versionCode));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
