package com.dunkin.customer.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.RecurrentOrderModel;

import java.util.List;


public class RecurrentOrderAdapter extends BaseAdapter {

    private Context context;
    private List<RecurrentOrderModel> orderList;
    private LayoutInflater inflater;

    public RecurrentOrderAdapter(Context context, List<RecurrentOrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecurrentOrderModel orderModel = (RecurrentOrderModel) getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.custom_recurrent_order_layout, null);

            viewHolder.txtOrderAmount = (TextView) convertView.findViewById(R.id.txtOrderAmount);
            viewHolder.txtOrderDate = (TextView) convertView.findViewById(R.id.txtOrderDate);
            viewHolder.txtOrderNumber = (TextView) convertView.findViewById(R.id.txtOrderNumber);
            viewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.itemLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*if (position % 2 == 0) {
            viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            viewHolder.itemLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
        }*/
        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        viewHolder.txtOrderDate.setText(context.getString(R.string.txt_recurrent_date, AppUtils.getFormattedDate(orderModel.getRecurrent_startdate()), AppUtils.getFormattedDate(orderModel.getRecurrent_enddate())));
        viewHolder.txtOrderNumber.setText(context.getString(R.string.txt_order_id, orderModel.getReference_id()));
        //viewHolder.txtOrderAmount.setText(context.getString(R.string.txt_order_amount, AppUtils.CurrencyFormat(Double.parseDouble(orderModel.getOrderAmount())) + " " + orderModel.getCurrency()));
        return convertView;
    }

    static class ViewHolder {
        TextView txtOrderNumber, txtOrderDate, txtOrderAmount;
        RelativeLayout itemLayout;
    }
}
