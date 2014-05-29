package com.gracecode.android.rain.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.DonateHelper;
import com.gracecode.android.rain.helper.TypefaceHelper;

public class DonateFragment extends Fragment implements View.OnClickListener {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TypefaceHelper.setAllTypeface((ViewGroup) getView(),
                TypefaceHelper.getTypefaceMusket2(getActivity()));

        ((TextView) getView().findViewById(R.id.nexus5))
                .setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));

        getView().findViewById(R.id.donate).setOnClickListener(this);
//        getView().findViewById(R.id.never_show_donate).setOnClickListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate, null);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.donate:
                DonateHelper.gotoPaypalPage(getActivity());
                break;

            case R.id.never_show_donate:
                break;
        }
    }
}
