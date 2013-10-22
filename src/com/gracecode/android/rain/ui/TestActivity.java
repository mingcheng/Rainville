package com.gracecode.android.rain.ui;

import android.app.Activity;
import android.os.Bundle;
import com.gracecode.android.rain.ui.fragment.DonateDialogFragment;

public class TestActivity extends Activity {
    private DonateDialogFragment mDonationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDonationFragment = new DonateDialogFragment(TestActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDonationFragment.isNeedShow()) {
            mDonationFragment.show(getFragmentManager(), "DonateDialog");
        }
    }
}
