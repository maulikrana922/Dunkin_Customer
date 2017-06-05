package com.dunkin.customer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dunkin.customer.R;
import com.dunkin.customer.fragments.TokenListFragment;
import com.dunkin.customer.fragments.WinnerListFragment;
import com.dunkin.customer.models.TokenModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qtm-c-android on 2/6/17.
 */

public class TokenAdapter extends FragmentStatePagerAdapter {
    private List<TokenModel> tokenModelList;
    private String[] pagerTitle;
    private Context context;
    private String winnerImage;

    public TokenAdapter(Context c, FragmentManager fm, List<TokenModel> tokenModelList,
                        String winnerImage) {
        super(fm);
        this.context = c;
        this.tokenModelList = tokenModelList;
        this.winnerImage = winnerImage;
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
            WinnerListFragment fragment = new WinnerListFragment();
            Bundle b = new Bundle();
            b.putString("winnerImage", winnerImage);
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

