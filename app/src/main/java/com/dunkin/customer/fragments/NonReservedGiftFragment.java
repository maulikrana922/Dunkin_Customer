package com.dunkin.customer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.dunkin.customer.R;
import com.dunkin.customer.adapters.GiftAdapter;
import com.dunkin.customer.models.GiftModel;

import java.util.List;

/**
 * Created by Admin on 1/5/2016.
 */
public class NonReservedGiftFragment extends Fragment {

    public static GiftAdapter giftAdapter;
    GiftFragment parentFragment;
    private View rootView;
    private List<GiftModel> giftList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        giftList = (List<GiftModel>) getArguments().getSerializable("rewards");

        rootView = inflater.inflate(R.layout.cat_gridview, container, false);

        parentFragment = (GiftFragment) getParentFragment();

        GridView listView = (GridView) rootView.findViewById(R.id.grid_view);
        rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);
        giftAdapter = new GiftAdapter(getActivity(), giftList, parentFragment.getUserPoints(), parentFragment, parentFragment);
        listView.setAdapter(giftAdapter);
        listView.setEmptyView(rootView.findViewById(R.id.emptyElement));
        return rootView;
    }
}
