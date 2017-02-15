package com.dunkin.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.models.NavDrawerModel;

import java.util.List;

/**
 * Created by Admin on 9/12/2015.
 */
public class NavDrawerAdapter extends BaseAdapter {

    private List<NavDrawerModel> navDrawerModelList;
    private Context context;

    private LayoutInflater inflater;

    public NavDrawerAdapter(Context context, List<NavDrawerModel> navDrawerModelList) {
        this.navDrawerModelList = navDrawerModelList;
        this.context = context;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return navDrawerModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return navDrawerModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        NavDrawerModel navModel = (NavDrawerModel) getItem(i);
        if (!navModel.isSection()) {
            view = inflater.inflate(R.layout.custom_nav_drawer_layout, null);
            TextView txtView = (TextView) view.findViewById(R.id.txtNavTitle);
            ImageView imgIcon = (ImageView) view.findViewById(R.id.imgNavIcon);

            txtView.setText(navModel.getNavTitle());
            imgIcon.setImageResource(navModel.getImageId());

        } else {
            view = inflater.inflate(R.layout.divider_layout, null);
        }
        return view;
    }
}
