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
import com.dunkin.customer.models.PromoModel;

import java.util.List;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class PlayItemAdapter extends BaseAdapter {


    private List<PromoModel> playModelList;
    private LayoutInflater inflater;
    private Context context;

    public PlayItemAdapter(Context context, List<PromoModel> playModelList) {
        this.context = context;
        this.playModelList = playModelList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return playModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return playModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PromoModel playModel = (PromoModel) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.item_play_layout, null);

            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtDesc = (TextView) convertView.findViewById(R.id.txtDesc);
            viewHolder.txtPoint = (TextView) convertView.findViewById(R.id.txtPoint);
            viewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.itemLayout);
            viewHolder.txtNoOfDays = (TextView) convertView.findViewById(R.id.txtNoOfDays);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        try {
            AppUtils.setImage(viewHolder.imgLogo, playModel.getPromoImage());

            viewHolder.txtPoint.setText(playModel.getTicketPoint() + " Points");

            viewHolder.txtTitle.setText(Html.fromHtml(playModel.getName()));
            viewHolder.txtDesc.setText(Html.fromHtml(playModel.getDescription()));
            if(!playModel.getDays().equals("0")) {
                viewHolder.txtNoOfDays.setText(playModel.getDays() + " Day(s) Left");
            }
            // viewHolder.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout itemLayout;
        ImageView imgLogo;
        TextView txtTitle, txtDesc, txtPoint, txtNoOfDays;
    }

}

