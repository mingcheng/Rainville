package com.gracecode.RainNoise.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import com.gracecode.RainNoise.ui.fragment.PresetsFragment;


public class ControlCenterAdapter extends FragmentStatePagerAdapter
        implements ViewPager.OnPageChangeListener {

    private Fragment[] fragments = new Fragment[]{
            new PresetsFragment()
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
//
//    @Override
//    public void bindPlayerManager(final PlayManager mPlayManager) {
//        for (int i = 0; i < fragments.length; i++) {
//            Fragment fragment = fragments[i];
//            if (fragment instanceof PlayerBinder) {
//                ((PlayerBinder) fragment).bindPlayerManager(mPlayManager);
//            }
//        }
//    }
//
//    @Override
//    public void unbindPlayerManager() {
//        for (int i = 0; i < fragments.length; i++) {
//            Fragment fragment = fragments[i];
//            if (fragment instanceof PlayerBinder) {
//                ((PlayerBinder) fragment).unbindPlayerManager();
//            }
//        }
//    }
//
//    @Override
//    public void refresh() {
//        for (int i = 0; i < fragments.length; i++) {
//            Fragment fragment = fragments[i];
//            if (fragment instanceof PlayerBinder) {
//                ((PlayerBinder) fragment).refresh();
//            }
//        }
//    }
}
