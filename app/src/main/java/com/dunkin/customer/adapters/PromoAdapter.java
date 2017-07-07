package com.dunkin.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.PromoData;

import java.util.List;

/**
 * Created by qtm-c-android on 7/7/17.
 */

public class PromoAdapter extends BaseAdapter {

    public List<PromoData> promoDataList;
    private Context context;

    public PromoAdapter(Context context, List<PromoData> promoDataList) {
        this.context = context;
        this.promoDataList = promoDataList;
    }

    @Override
    public int getCount() {
        return promoDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return promoDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        PromoData nm = (PromoData) getItem(position);
        View view;

        if (convertView == null) {
            final ViewHolder viewHolder = new ViewHolder();

            view = LayoutInflater.from(context).inflate(R.layout.custom_promo_layout, null);

            viewHolder.image = (ImageView) view.findViewById(R.id.image);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        final ViewHolder h = (ViewHolder) view.getTag();

//        Glide.with(context.getApplicationContext()).load(promoDataList.get(position).getImage())
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.drawable.app_bg)
//                .into(h.image);

        AppUtils.setImage(h.image, promoDataList.get(position).getImage());

        return view;
    }

    static class ViewHolder {
        ImageView image;
    }
}

