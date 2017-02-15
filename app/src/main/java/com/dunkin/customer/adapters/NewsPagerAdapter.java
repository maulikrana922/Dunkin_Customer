package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dunkin.customer.R;
import com.dunkin.customer.fragments.CelebrationFragment;
import com.dunkin.customer.fragments.EventFragment;
import com.dunkin.customer.fragments.NewsFragment;

import java.util.ArrayList;

/**
 * Created by Admin on 9/28/2015.
 */
public class NewsPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> titles;
    private Context context;

    public NewsPagerAdapter(FragmentManager fm, Context context, ArrayList<String> titles) {
        super(fm);
        this.titles = titles;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (titles.get(position).equals(context.getString(R.string.nav_news))) {
            return new NewsFragment();
        }
        if (titles.get(position).equals(context.getString(R.string.nav_event))) {
            return new EventFragment();
        }
        if (titles.get(position).equals(context.getString(R.string.nav_celebration))) {
            return new CelebrationFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
