package com.dunkin.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dunkin.customer.R;
import com.dunkin.customer.adapters.NewsPagerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

public class NewsPagerFragment extends Fragment {

    private Context context;
    private View rootView;

    private SmartTabLayout tabs;
    private ViewPager viewPager;

    private NewsPagerAdapter pagerAdapter;
    private ArrayList<String> titles;

    private int pos = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        titles = getArguments().getStringArrayList("titles");

        pos = getArguments().getInt("pos", 0);

        rootView = inflater.inflate(R.layout.fragment_page_taber, container, false);

        tabs = (SmartTabLayout) rootView.findViewById(R.id.tabs);
        //  tabs.setDistributeEvenly(true);
        //tabs.setTextColor(getResources().getColor(android.R.color.white));

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        rootView.findViewById(R.id.scrollContainer).setVisibility(View.VISIBLE);

        rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);

        pagerAdapter = new NewsPagerAdapter(getChildFragmentManager(), context, titles);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(titles.size());
        viewPager.setCurrentItem(pos, true);
        tabs.setViewPager(viewPager);

        return rootView;
    }
}
