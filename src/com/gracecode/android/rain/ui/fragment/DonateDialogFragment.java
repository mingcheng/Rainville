package com.gracecode.android.rain.ui.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.TypefaceHelper;

import java.util.HashMap;

public class DonateDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final int MAX_COUNTS = 5;
    private static final int NEVER_SHOW_COUNTS = -1;
    private static final int INITIAL_COUNTS = 0;

    private static final String COUNT_FIELD = "count";
    private SharedPreferences mSharedPreferences;
    private int mCounts = INITIAL_COUNTS;

    public DonateDialogFragment(Activity activity) {
        mSharedPreferences =
                activity.getSharedPreferences(DonateDialogFragment.class.getName(), Context.MODE_PRIVATE);

        mCounts = mSharedPreferences.getInt(COUNT_FIELD, INITIAL_COUNTS);
        if (mCounts != NEVER_SHOW_COUNTS) {
            setCount(++mCounts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TypefaceHelper.setAllTypeface((ViewGroup) getView(),
                TypefaceHelper.getTypefaceMusket2(getActivity()));

        ((TextView) getView().findViewById(R.id.nexus5))
                .setTypeface(TypefaceHelper.getTypefaceElegant(getActivity()));

        getView().findViewById(R.id.donate).setOnClickListener(this);
        getView().findViewById(R.id.never_show_donate).setOnClickListener(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donate, container);
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void onClick(View view) {
        HashMap<String, String> hashMap = new HashMap<String, String>();

        switch (view.getId()) {
            case R.id.donate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getPaypalUrl())));
                hashMap.put("DonateButton", "DonateButton");
                break;

            case R.id.never_show_donate:
                hashMap.put("DonateButton", "NeverShow");
                break;
        }

        dismiss();
        setCount(NEVER_SHOW_COUNTS);
        FlurryAgent.logEvent(getTag(), hashMap);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        setCount(INITIAL_COUNTS);
    }

    public void setCount(int count) {
        mSharedPreferences.edit()
                .putInt(COUNT_FIELD, count)
                .commit();
    }


    protected String getPaypalUrl() {
        return String.format(getString(R.string.paypal_url),
                Uri.encode(getString(R.string.paypal_account)),
                Uri.encode(getString(R.string.paypal_item_name)),
                Uri.encode(getString(R.string.paypal_amount))
        );
    }

    public boolean isNeedShow() {
        return mCounts != NEVER_SHOW_COUNTS && mCounts > MAX_COUNTS;
    }
}
