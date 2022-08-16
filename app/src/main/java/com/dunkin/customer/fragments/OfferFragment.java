package com.dunkin.customer.fragments;

import android.os.Bundle;
import javax.annotation.Nullable;


import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Admin on 2/19/2016.
 */
public class OfferFragment extends Fragment {

    private View rootView;
//    private SmartTabLayout tabs;
    private TabLayout tabs;
    private ViewPager viewPager;
    private LinearLayout scrollContainer;
    private ProgressBar progressLoad;
    private RelativeLayout mainLayout;
    private OfferFragmentPagerAdapter pagerAdapter;
    private String[] pagerTitle;
    Animation animFadein;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_offer, container, false);

        progressLoad = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        scrollContainer = (LinearLayout) rootView.findViewById(R.id.scrollContainer);
//        tabs = (SmartTabLayout) rootView.findViewById(R.id.tabs);
        tabs = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        mainLayout.startAnimation(animFadein);


//        tabs.setDistributeEvenly(true);
        progressLoad.setVisibility(View.GONE);
        scrollContainer.setVisibility(View.VISIBLE);
        //pagerTitle = new String[]{getContext().getString(R.string.tab_offer_1), getContext().getString(R.string.tab_offer_2)};
        pagerTitle = new String[]{getContext().getString(R.string.tab_offer_1)};

        pagerAdapter = new OfferFragmentPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
//        tabs.setViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NewHomeActivity)getActivity()).setToolbarView(this);
    }

    private class OfferFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public OfferFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new OfferListFragment();
            } /*else if (position == 1) {
                return new OfferHistoryFragment();
            }*/
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
