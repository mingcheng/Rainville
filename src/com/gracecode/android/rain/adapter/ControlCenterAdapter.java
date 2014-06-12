package com.gracecode.android.rain.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.gracecode.android.common.ui.fragment.AboutFragment;
import com.gracecode.android.rain.RainApplication;
import com.gracecode.android.rain.ui.fragment.DonateFragment;
import com.gracecode.android.rain.ui.fragment.PresetsFragment;
import com.gracecode.android.rain.ui.fragment.SetTimerFragment;

import java.util.ArrayList;


public class ControlCenterAdapter extends FragmentPagerAdapter
        implements ViewPager.OnPageChangeListener {

    private final RainApplication mRainApplication;
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    public ControlCenterAdapter(FragmentManager fm) {
        super(fm);
        mRainApplication = RainApplication.getInstance();

        mFragments.add(new PresetsFragment());
        mFragments.add(new SetTimerFragment());

        // Meizu's device can call about dialog though menu item.
        if (!mRainApplication.isMeizuDevice()) {
            mFragments.add(new AboutFragment(mRainApplication.getPackageInfo()));
        }

        if (!mRainApplication.isZhLanguage()) {
            mFragments.add(new DonateFragment());
        }
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
