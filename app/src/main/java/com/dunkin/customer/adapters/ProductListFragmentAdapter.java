package com.dunkin.customer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dunkin.customer.fragments.ProductListFragment;
import com.dunkin.customer.models.ChildCatModel;

import java.util.List;


public class ProductListFragmentAdapter extends FragmentPagerAdapter {

    private List<ChildCatModel> categoryList;
    private int country_id;

    public ProductListFragmentAdapter(FragmentManager fm, int country_id, List<ChildCatModel> childCatList) {
        super(fm);
        this.categoryList = childCatList;
        this.country_id = country_id;
    }

    @Override
    public Fragment getItem(int position) {
        return ProductListFragment.newInstance(position, categoryList.get(position), country_id);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getCategoryName();
    }
}