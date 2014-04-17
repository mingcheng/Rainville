package com.gracecode.android.rain.ui.fragment;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.TypefaceHelper;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, null);
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
            PackageInfo packageInfo =
                    getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            TextView textView = (TextView) getView().findViewById(R.id.version);
            textView.setText(
                    String.format(getString(R.string.version),
                            packageInfo.versionName, packageInfo.versionCode));

            setTouchListener((ViewGroup) getView(), new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setTouchListener(ViewGroup group, View.OnTouchListener listener) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
                v.setOnTouchListener(listener);
            } else if (v instanceof ViewGroup)
                setTouchListener((ViewGroup) v, listener);
        }
    }
}
