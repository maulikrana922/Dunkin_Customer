package com.dunkin.customer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.fragments.PayPointHistoryFragment;
import com.dunkin.customer.fragments.WalletNoteListDummyFragment;
import com.dunkin.customer.fragments.WalletNoteListFragment;
import com.dunkin.customer.models.WalletModel;

import java.util.List;

public class WalletAdapter extends FragmentStatePagerAdapter {
    private List<WalletModel> walletList;
    private String remainingPoints;
    private String[] pagerTitle;
    private Context context;

    public WalletAdapter(Context c, FragmentManager fm, List<WalletModel> walletList, String point) {
        super(fm);
        this.context = c;
        this.walletList = walletList;
        this.remainingPoints = point;
        //pagerTitle = new String[]{context.getString(R.string.tab_point_1), context.getString(R.string.tab_point_2)};
        pagerTitle = new String[]{c.getString(R.string.lbl_tab_wallet_history), c.getString(R.string.lbl_tab_wallet_point_history)};
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (walletList != null && walletList.size() > 0) {
                Dunkin_Log.i("WalletAdapter", "WalletNoteListFragment");
                WalletNoteListFragment fragment = new WalletNoteListFragment();
                Bundle b = new Bundle();
                b.putSerializable("notes", walletList.get(position));
                b.putString("points", remainingPoints);
                fragment.setArguments(b);
                return fragment;
            } else {
                Dunkin_Log.i("WalletAdapter", "WalletNoteListDummyFragment");
                WalletNoteListDummyFragment fragment = new WalletNoteListDummyFragment();
                Bundle b = new Bundle();
                b.putString("points", remainingPoints);
                fragment.setArguments(b);
                return fragment;
            }

        } else if (position == 1) {
            return new PayPointHistoryFragment();
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
