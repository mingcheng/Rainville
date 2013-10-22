package com.gracecode.android.rain.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.gracecode.android.rain.ui.fragment.AboutFragment;
import com.gracecode.android.rain.ui.fragment.DonateFragment;
import com.gracecode.android.rain.ui.fragment.PresetsFragment;


public class ControlCenterAdapter extends FragmentStatePagerAdapter
        implements ViewPager.OnPageChangeListener {

    private Fragment[] fragments = new Fragment[]{
            new PresetsFragment(), new AboutFragment(), new DonateFragment()
    };

    public ControlCenterAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        return fragments.length;
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
