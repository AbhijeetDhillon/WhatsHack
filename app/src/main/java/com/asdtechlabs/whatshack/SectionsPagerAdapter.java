package com.asdtechlabs.whatshack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PhotosFragment();
            case 1:
                return new VideosFragment();
            case 2:
                return new SavedFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}