package com.gracecode.android.rain.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.gracecode.android.rain.Rainville;
import com.gracecode.android.rain.ui.fragment.AboutFragment;
import com.gracecode.android.rain.ui.fragment.DonateFragment;
import com.gracecode.android.rain.ui.fragment.PresetsFragment;
import com.gracecode.android.rain.ui.fragment.SetTimerFragment;


public class ControlCenterAdapter extends FragmentPagerAdapter
        implements ViewPager.OnPageChangeListener {

    private final Rainville mRainville;
    private Fragment[] fragments = new Fragment[]{
            new PresetsFragment(), new SetTimerFragment(), new AboutFragment(), new DonateFragment()
    };

    public ControlCenterAdapter(FragmentManager fm) {
        super(fm);
        mRainville = Rainville.getInstance();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        if (mRainville.isMeizuDevice()) {
            return fragments.length - 1;
        }
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
