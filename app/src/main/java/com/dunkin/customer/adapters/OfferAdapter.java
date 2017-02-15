package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.OfferModel;

import java.util.List;


public class OfferAdapter extends BaseAdapter {


    private List<OfferModel> offerList;
    private LayoutInflater inflater;
    private Context context;

    public OfferAdapter(Context context, List<OfferModel> offerList) {
        this.context = context;
        this.offerList = offerList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return offerList.size();
    }

    @Override
    public Object getItem(int position) {
        return offerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OfferModel of = (OfferModel) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_row_layout, null);

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

            viewHolder.txtDate.setText(context.getString(R.string.txt_to, AppUtils.getFormattedDate(of.getOfferStartDate()), AppUtils.getFormattedDate(of.getOfferEndDate())));

            viewHolder.txtTitle.setText(of.getOfferTitle());
            viewHolder.txtDesc.setText(Html.fromHtml(of.getOfferDescription()));
            // viewHolder.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
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
