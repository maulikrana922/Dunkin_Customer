package com.dunkin.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.OrderHistoryDetailActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.ToppingModel;

import java.util.List;

/**
 * Created by Admin on 10/30/2015.
 */
public class ToppingAdapter extends BaseAdapter {

    private Context context;
    private List<ToppingModel> toppingList;
    private String currencyCode;
    private DBAdapter dbAdapter;

    public ToppingAdapter(Context context, List<ToppingModel> toppingList, String currencyCode) {
        this.toppingList = toppingList;
        this.context = context;
        dbAdapter = new DBAdapter(context);
        this.currencyCode = currencyCode;
    }

    @Override
    public int getCount() {
        return toppingList.size();
    }

    @Override
    public Object getItem(int position) {
        return toppingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ToppingModel tm = toppingList.get(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_topping_layout, null);

            viewHolder.txtToppingName = (TextView) convertView.findViewById(R.id.toppingName);
            viewHolder.txtToppingPrice = (TextView) convertView.findViewById(R.id.toppingPrice);
            viewHolder.cbTopping = (CheckBox) convertView.findViewById(R.id.cbTopping);

            if (context instanceof OrderHistoryDetailActivity) {
                viewHolder.cbTopping.setChecked(true);
                viewHolder.cbTopping.setEnabled(false);
            }

            viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.rl);
            convertView.setTag(viewHolder);


        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtToppingName.setText(tm.getToppingTitle());
        viewHolder.txtToppingPrice.setText(AppUtils.CurrencyFormat(Double.parseDouble(tm.getToppingPrice())) + " " + currencyCode);
        if (toppingList.get(position).isSelected()) {
            viewHolder.cbTopping.setChecked(true);
        }

        if (context instanceof OrderHistoryDetailActivity) {
            viewHolder.cbTopping.setChecked(true);
            viewHolder.cbTopping.setEnabled(false);
        }


        viewHolder.cbTopping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toppingList.get(position).setSelected(true);
                } else {
                    toppingList.get(position).setSelected(false);
                }
            }
        });

        viewHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.cbTopping.isChecked()) {
                    viewHolder.cbTopping.setChecked(false);
                } else {
                    viewHolder.cbTopping.setChecked(true);
                }
            }
        });


        return convertView;
    }

    public List<ToppingModel> getUpdatedToppingList() {
       /* dbAdapter.open();
            dbAdapter.updateToppingList(toppingList);
        dbAdapter.close();*/
        return toppingList;
    }

    class ViewHolder {
        TextView txtToppingName, txtToppingPrice;
        CheckBox cbTopping;
        RelativeLayout rl;
    }
}
