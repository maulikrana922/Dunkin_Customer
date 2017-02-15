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
import com.dunkin.customer.models.NewsModel;

import java.util.List;


public class NewsAdapter extends BaseAdapter {


    private List<NewsModel> newsList;
    private LayoutInflater inflater;
    private Context context;

    public NewsAdapter(Context context, List<NewsModel> newsList) {

        this.context = context;
        this.newsList = newsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsModel nm = (NewsModel) getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_row_layout, null);

            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.txtDate.setVisibility(View.VISIBLE);
            viewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.itemLayout);
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

            if (nm.getNewsImage() != null) {
                AppUtils.setImage(viewHolder.imgLogo, nm.getNewsImage());
            } else {
                AppUtils.setImage(viewHolder.imgLogo, nm.getNewsVideoThumb());
            }

            viewHolder.txtTitle.setText(nm.getTitle());
            viewHolder.txtDesc.setText(Html.fromHtml(nm.getDescription()));
            viewHolder.txtDate.setText(AppUtils.getFormattedDate(nm.getNewsdate()));
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
