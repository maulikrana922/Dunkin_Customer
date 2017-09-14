package com.dunkin.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.AboutUsModel;

import java.util.List;

/**
 * Created by latitude on 1/8/17.
 */

public class AboutUsItemAdapter extends BaseAdapter {

    public List<AboutUsModel> aboutUsModelList;
    private Context context;

    public AboutUsItemAdapter(Context context, List<AboutUsModel> aboutUsModelList) {
        this.context = context;
        this.aboutUsModelList = aboutUsModelList;
    }

    @Override
    public int getCount() {
        return aboutUsModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return aboutUsModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        AboutUsModel nm = (AboutUsModel) getItem(position);
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

        h.image.setAdjustViewBounds(true);
        AppUtils.setImage(h.image, aboutUsModelList.get(position).getListImage());

        return view;
    }

    static class ViewHolder {
        ImageView image;
    }
}
