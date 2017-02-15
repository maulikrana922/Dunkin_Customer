package com.dunkin.customer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dunkin.customer.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by Admin on 2/19/2016.
 */
public class RedeemFragment extends Fragment {

    private View rootView;
    private SmartTabLayout tabs;
    private ViewPager viewPager;
    private LinearLayout scrollContainer;
    private ProgressBar progressLoad;

    private RedeemFragmentPagerAdapter pagerAdapter;
    private String[] pagerTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_page_taber, container, false);

        progressLoad = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        scrollContainer = (LinearLayout) rootView.findViewById(R.id.scrollContainer);
        tabs = (SmartTabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        tabs.setDistributeEvenly(true);
        progressLoad.setVisibility(View.GONE);
        scrollContainer.setVisibility(View.VISIBLE);
        pagerTitle = new String[]{getContext().getString(R.string.nav_coupons2), getContext().getString(R.string.nav_my_point)};

        pagerAdapter = new RedeemFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setViewPager(viewPager);

        return rootView;
    }

    private class RedeemFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public RedeemFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new GiftFragment();
            } else if (position == 1) {
                return new RedeemPointFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return pagerTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitle[position];
        }
    }
}
