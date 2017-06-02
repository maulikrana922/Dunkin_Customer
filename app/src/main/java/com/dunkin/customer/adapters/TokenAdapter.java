package com.dunkin.customer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dunkin.customer.R;
import com.dunkin.customer.fragments.TokenListFragment;
import com.dunkin.customer.models.TokenModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qtm-c-android on 2/6/17.
 */

public class TokenAdapter extends FragmentStatePagerAdapter {
    private List<TokenModel> tokenModelList;
    private List<TokenModel> winnerList;
    private String[] pagerTitle;
    private Context context;

    public TokenAdapter(Context c, FragmentManager fm, List<TokenModel> tokenModelList,
                        List<TokenModel> winnerList) {
        super(fm);
        this.context = c;
        this.tokenModelList = tokenModelList;
        this.winnerList = winnerList;
        pagerTitle = new String[]{c.getString(R.string.lbl_tab_token), c.getString(R.string.lbl_tab_winning_token)};
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            TokenListFragment fragment = new TokenListFragment();
            Bundle b = new Bundle();
            b.putSerializable("token_list", (Serializable) tokenModelList);
            fragment.setArguments(b);
            return fragment;
        } else {
            TokenListFragment fragment = new TokenListFragment();
            Bundle b = new Bundle();
            b.putSerializable("winner_list", (Serializable) winnerList);
            fragment.setArguments(b);
            return fragment;
        }
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

