package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.models.RestaurantModel;

import java.util.List;

/**
 * Created by Admin on 3/7/2016.
 */
public class FavoriteRestaurantAdapter extends BaseAdapter {

    public List<RestaurantModel> restaurantList;
    private Context context;

    public FavoriteRestaurantAdapter(Context context, List<RestaurantModel> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @Override
    public int getCount() {
        return restaurantList.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RestaurantModel rm = (RestaurantModel) getItem(position);
        View view;

        if (convertView == null) {
            final ViewHolder viewHolder = new ViewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.custom_fav_restaurant_layout, null);

            viewHolder.itemLayout = (RelativeLayout) view.findViewById(R.id.itemLayout);
            viewHolder.txtRestaurantName = (TextView) view.findViewById(R.id.txtRestaurantName);
            viewHolder.cbCheckRestaurant = (CheckBox) view.findViewById(R.id.cbCheckRestaurant);

            view.setTag(viewHolder);
            viewHolder.cbCheckRestaurant.setTag(restaurantList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).cbCheckRestaurant.setTag(restaurantList.get(position));
        }

        final ViewHolder h = (ViewHolder) view.getTag();

        h.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        h.txtRestaurantName.setText(rm.getRestaurantName());

        h.cbCheckRestaurant.setFocusable(false);
        h.cbCheckRestaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RestaurantModel element = (RestaurantModel) h.cbCheckRestaurant.getTag();
                element.setSelected(isChecked);
            }
        });

        h.cbCheckRestaurant.setChecked(restaurantList.get(position).isSelected());

        return view;
    }

    static class ViewHolder {
        RelativeLayout itemLayout;
        TextView txtRestaurantName;
        CheckBox cbCheckRestaurant;
    }
}
