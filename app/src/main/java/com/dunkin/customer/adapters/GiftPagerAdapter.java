package com.dunkin.customer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dunkin.customer.R;
import com.dunkin.customer.fragments.NonReservedGiftFragment;
import com.dunkin.customer.fragments.ReservedGiftFragment;
import com.dunkin.customer.models.GiftModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 9/28/2015.
 */
public class GiftPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> titles;
    private Map<String, List<GiftModel>> data;
    private Context context;

    public GiftPagerAdapter(FragmentManager fm, Context context, List<String> titles, Map<String, List<GiftModel>> data) {
        super(fm);
        this.titles = titles;
        this.context = context;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        if (titles.get(position).equals(context.getString(R.string.tab_wait_for_me))) {
            ReservedGiftFragment reservedGiftFragment = new ReservedGiftFragment();
            Bundle b = new Bundle();
            b.putSerializable("gift", (Serializable) data.get(titles.get(position)));
            reservedGiftFragment.setArguments(b);
            return reservedGiftFragment;
        }

        if (titles.get(position).equals(context.getString(R.string.tab_on_the_go))) {
            NonReservedGiftFragment nonReservedGiftFragment = new NonReservedGiftFragment();
            Bundle b = new Bundle();
            b.putSerializable("gift", (Serializable) data.get(titles.get(position)));
            nonReservedGiftFragment.setArguments(b);
            return nonReservedGiftFragment;
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
