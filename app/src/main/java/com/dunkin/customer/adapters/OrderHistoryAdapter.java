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
import com.dunkin.customer.models.OrderModel;

import java.util.List;


public class OrderHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<OrderModel> orderList;
    private LayoutInflater inflater;

    public OrderHistoryAdapter(Context context, List<OrderModel> orderList) {
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

        OrderModel orderModel = (OrderModel) getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.custom_order_layout, null);

            viewHolder.txtOrderAmount = (TextView) convertView.findViewById(R.id.txtOrderAmount);
            viewHolder.txtOrderDate = (TextView) convertView.findViewById(R.id.txtOrderDate);
            viewHolder.txtOrderNumber = (TextView) convertView.findViewById(R.id.txtOrderNumber);
            viewHolder.txtOrderStatus = (TextView) convertView.findViewById(R.id.txtOrderStatus);
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
        viewHolder.txtOrderDate.setText(AppUtils.getFormattedDate(orderModel.getOrderDate()));
        viewHolder.txtOrderNumber.setText(context.getString(R.string.txt_order_id, orderModel.getOrderId()));
        viewHolder.txtOrderAmount.setText(context.getString(R.string.txt_order_amount_history, AppUtils.CurrencyFormat(Double.parseDouble(orderModel.getOrderAmount())), orderModel.getCurrency()));
        viewHolder.txtOrderStatus.setText(orderModel.getOrderStatus());
        return convertView;
    }

    static class ViewHolder {
        TextView txtOrderNumber, txtOrderDate, txtOrderAmount, txtOrderStatus;
        RelativeLayout itemLayout;
    }
}
