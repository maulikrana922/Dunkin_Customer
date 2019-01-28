package com.dunkin.customer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.WalletNoteModel;

import java.util.List;

/**
 * Created by Admin on 9/9/2015.
 */
public class NewWalletNoteListAdapter extends RecyclerView.Adapter<NewWalletNoteListAdapter.ItemHolder> {
    public List<WalletNoteModel> walletNoteList;
    Context context;
    private LayoutInflater inflter;

    public NewWalletNoteListAdapter(Context context, List<WalletNoteModel> walletNoteList) {
        this.context = context;
        this.walletNoteList = walletNoteList;
        inflter = (LayoutInflater.from(context));
    }


    @Override
    public void onBindViewHolder(ItemHolder viewHolder, int position) {
        WalletNoteModel walletNoteModel = getItem(position);

        viewHolder.tvNoteId.setText(walletNoteModel.getNoteId());
        viewHolder.tvNoteAmount.setText(AppUtils.CurrencyFormat(Double.parseDouble(walletNoteModel.getNoteAmount())));
        viewHolder.tvRechargeBy.setText(walletNoteModel.getRechargeBy());

        viewHolder.lblNoteId.setTextColor(position % 2 == 0 ? context.getResources().getColor(R.color.colorPrimary1) : context.getResources().getColor(R.color.colorPrimary));
        viewHolder.lblAmount.setTextColor(position % 2 == 0 ? context.getResources().getColor(R.color.colorPrimary1) : context.getResources().getColor(R.color.colorPrimary));
        viewHolder.lblAmountAdded.setTextColor(position % 2 == 0 ? context.getResources().getColor(R.color.colorPrimary1) : context.getResources().getColor(R.color.colorPrimary));
        viewHolder.lblPerformedBy.setTextColor(position % 2 == 0 ? context.getResources().getColor(R.color.colorPrimary1) : context.getResources().getColor(R.color.colorPrimary));

        if (walletNoteModel.getEarnType() == 1)
            viewHolder.tvDateTime.setText(AppUtils.getFormattedDateTime(walletNoteModel.getRechargeDate()));
        else
            viewHolder.tvDateTime.setText(AppUtils.getFormattedDateTime(walletNoteModel.getRechargeDate()));

    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public int getItemCount() {
        return walletNoteList.size();
    }

    public WalletNoteModel getItem(int position) {
        return walletNoteList.get(position);
    }

    @Override
    public NewWalletNoteListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewWalletNoteListAdapter.ItemHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_column_layout_new,
                                parent, false));
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvNoteId, tvNoteAmount, tvDateTime, tvRechargeBy;
        TextView lblNoteId,lblAmount,lblAmountAdded,lblPerformedBy;

        ItemHolder(View itemView) {
            super(itemView);
            tvNoteId = (TextView) itemView.findViewById(R.id.tvNoteId);
            tvNoteAmount = (TextView) itemView.findViewById(R.id.tvNoteAmount);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvRechargeBy = (TextView) itemView.findViewById(R.id.tvRechargeBy);
            lblNoteId = itemView.findViewById(R.id.lblNoteId);
            lblAmount = itemView.findViewById(R.id.lblAmount);
            lblAmountAdded= itemView.findViewById(R.id.lblAmountAdded);
            lblPerformedBy= itemView.findViewById(R.id.lblPerformedBy);
        }

    }
}
