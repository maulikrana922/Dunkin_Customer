package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.OfferHistoryModel;

import java.util.List;

/**
 * Created by Admin on 3/5/2016.
 */
public class OfferHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<OfferHistoryModel> offerHistoryList;

    public OfferHistoryAdapter(Context context, List<OfferHistoryModel> offerHistoryList) {
        this.context = context;
        this.offerHistoryList = offerHistoryList;
    }

    @Override
    public int getCount() {
        return offerHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return offerHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OfferHistoryModel of = (OfferHistoryModel) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_row_layout, null);

            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.itemLayout);
            viewHolder.txtDate.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*if (position % 2 == 0) {
            viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            viewHolder.imgLogo.setBackgroundColor(context.getResources().getColor(R.color.alternate_image_border));
            viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
        }*/
        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        try {
            AppUtils.setImage(viewHolder.imgLogo, of.getOfferImage());

            viewHolder.txtDate.setText(of.getPurchaseConverted_date());
            viewHolder.txtTitle.setText(of.getOfferTitle());
            if (of.getPay_option() >= 0) {
                viewHolder.txtDesc.setVisibility(View.VISIBLE);
                viewHolder.txtDesc.setText(AppUtils.getPaymentMode(context, of.getPay_option()));
            } else
                viewHolder.txtDesc.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout itemLayout;
        ImageView imgLogo;
        TextView txtTitle, txtDesc, txtDate;
    }
}
