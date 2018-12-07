package com.dunkin.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.WalletNoteModel;

import java.util.List;

/**
 * Created by Admin on 9/9/2015.
 */
public class WalletNoteListAdapter extends BaseAdapter {

    public List<WalletNoteModel> walletNoteList;
    private Context context;
    private LayoutInflater inflater;

    public WalletNoteListAdapter(Context context, List<WalletNoteModel> walletNoteList) {
        this.walletNoteList = walletNoteList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return walletNoteList.size();
    }

    @Override
    public Object getItem(int position) {
        return walletNoteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WalletNoteModel walletNoteModel = (WalletNoteModel) getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_column_layout_new, null);
            viewHolder.tvNoteId = (TextView) convertView.findViewById(R.id.tvNoteId);
            viewHolder.tvNoteAmount = (TextView) convertView.findViewById(R.id.tvNoteAmount);
            viewHolder.tvDateTime = (TextView) convertView.findViewById(R.id.tvDateTime);
            viewHolder.tvRechargeBy = (TextView) convertView.findViewById(R.id.tvRechargeBy);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvNoteId.setText(walletNoteModel.getNoteId());
        viewHolder.tvNoteAmount.setText(AppUtils.CurrencyFormat(Double.parseDouble(walletNoteModel.getNoteAmount())));
        viewHolder.tvRechargeBy.setText( walletNoteModel.getRechargeBy());

        if (walletNoteModel.getEarnType() == 1)
            viewHolder.tvDateTime.setText(AppUtils.getFormattedDateTime(walletNoteModel.getRechargeDate()));
        else
            viewHolder.tvDateTime.setText(AppUtils.getFormattedDateTime(walletNoteModel.getRechargeDate()));

        return convertView;
    }

    class ViewHolder {
        TextView tvNoteId, tvNoteAmount, tvDateTime, tvRechargeBy;
    }
}
