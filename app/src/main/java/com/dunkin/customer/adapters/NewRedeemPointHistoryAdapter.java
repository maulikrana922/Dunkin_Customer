package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.RedeemHistoryModel;

import java.util.List;

/**
 * Created by Admin on 3/8/2016.
 */
public class NewRedeemPointHistoryAdapter extends RecyclerView.Adapter<NewRedeemPointHistoryAdapter.ItemHolder> {

    private Context context;
    private List<RedeemHistoryModel> redeemHistoryList;

    public NewRedeemPointHistoryAdapter(Context context, List<RedeemHistoryModel> redeemHistoryList) {
        this.redeemHistoryList = redeemHistoryList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(NewRedeemPointHistoryAdapter.ItemHolder viewHolder, int position) {
        RedeemHistoryModel redeemHistoryModel = getItem(position);

        viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        viewHolder.txtTitle.setTextColor(position % 2 == 0 ? context.getResources().getColor(R.color.colorPrimary1) : context.getResources().getColor(R.color.colorPrimary));

        if (AppUtils.isNotNull(redeemHistoryModel.getSource()))
            viewHolder.txtTitle.setText(String.format("%s %s", AppUtils.CurrencyFormat(redeemHistoryModel.getRedeem_point()), redeemHistoryModel.getSource()));
        else
            viewHolder.txtTitle.setText(String.format("%s -", AppUtils.CurrencyFormat(redeemHistoryModel.getRedeem_point())));

        viewHolder.txtDate.setText(AppUtils.getFormattedDateTimeRedeemHistory(redeemHistoryModel.getRedeem_date()));

    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public int getItemCount() {
        return redeemHistoryList.size();
    }

    public RedeemHistoryModel getItem(int position) {
        return redeemHistoryList.get(position);
    }

    @Override
    public NewRedeemPointHistoryAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewRedeemPointHistoryAdapter.ItemHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_redeem_history_layout,
                                parent, false));
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        LinearLayout itemLayout;
        TextView txtTitle, txtDate;

        ItemHolder(View itemView) {
            super(itemView);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.itemLayout);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
        }

    }
}
