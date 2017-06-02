package com.dunkin.customer.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.TokenModel;

import java.util.List;

/**
 * Created by qtm-c-android on 2/6/17.
 */

public class TokenListAdapter extends BaseAdapter {

    public List<TokenModel> tokenModelList;
    private Context context;
    private LayoutInflater inflater;

    public TokenListAdapter(Context context, List<TokenModel> tokenModelList) {
        this.tokenModelList = tokenModelList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return tokenModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return tokenModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TokenModel tokenModel = (TokenModel) getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_token_layout, null);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.nameLayout = (LinearLayout) convertView.findViewById(R.id.nameLayout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtTitle.setText(tokenModel.getTicketId());
        viewHolder.txtDate.setText(AppUtils.getFormattedDate(tokenModel.getPurchaseDate()));
        if(!TextUtils.isEmpty(tokenModel.getUserName())) {
            viewHolder.txtName.setText(tokenModel.getUserName());
        }
        else
        {
            viewHolder.nameLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        TextView txtTitle, txtDate, txtName;
        LinearLayout nameLayout;
    }
}

