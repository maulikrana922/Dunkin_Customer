package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.RedeemHistoryModel;

import java.util.List;

/**
 * Created by Admin on 3/8/2016.
 */
public class RedeemPointHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<RedeemHistoryModel> redeemHistoryList;

    public RedeemPointHistoryAdapter(Context context, List<RedeemHistoryModel> redeemHistoryList) {
        this.redeemHistoryList = redeemHistoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return redeemHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return redeemHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RedeemHistoryModel redeemHistoryModel = (RedeemHistoryModel) getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.custom_redeem_history_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        /*if(position%2 == 0){
            viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }else{
              viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
        }*/
        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        //viewHolder.txtTitle.setText(redeemHistoryModel.getMessage(context));
        if (AppUtils.isNotNull(redeemHistoryModel.getSource()))
            viewHolder.txtTitle.setText(String.format("%s %s", AppUtils.CurrencyFormat(redeemHistoryModel.getRedeem_point()), redeemHistoryModel.getSource()));
        else
            viewHolder.txtTitle.setText(String.format("%s -", AppUtils.CurrencyFormat(redeemHistoryModel.getRedeem_point())));

        viewHolder.txtDate.setText(AppUtils.getFormattedDateTimeRedeemHistory(redeemHistoryModel.getRedeem_date()));

        return convertView;
    }

    static class ViewHolder {
        LinearLayout itemLayout;
        TextView txtTitle, txtDate;
    }
}
