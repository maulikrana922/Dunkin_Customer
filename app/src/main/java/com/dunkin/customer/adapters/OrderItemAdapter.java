package com.dunkin.customer.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.OrderItemModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import java.util.List;

public class OrderItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<OrderItemModel> cartModelList;
    private String currency;
    private boolean isFromHistory = false;

    public OrderItemAdapter(Context context, List<OrderItemModel> cartModelList, String currency, boolean fromHistory) {
        this.context = context;
        this.cartModelList = cartModelList;
        this.currency = currency;
        this.isFromHistory = fromHistory;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cartModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final OrderItemModel cart = (OrderItemModel) getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.cart_item_item_list, null);

            viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.edQuantity = (EditText) convertView.findViewById(R.id.edQuantity);
            viewHolder.btnDelete = (ImageView) convertView.findViewById(R.id.btnRemove);
            viewHolder.tvTotalAmount = (TextView) convertView.findViewById(R.id.tvTotalAmount);
            viewHolder.cartLayout = (RelativeLayout) convertView.findViewById(R.id.cartLayout);
            viewHolder.btnToppings = (RelativeLayoutButton) convertView.findViewById(R.id.btnToppings);
            viewHolder.btnAddQty = (ImageView) convertView.findViewById(R.id.btnAddQty);
            viewHolder.btnDeletQty = (ImageView) convertView.findViewById(R.id.btnDeletQty);

            viewHolder.btnDelete.setVisibility(View.INVISIBLE);
            viewHolder.btnAddQty.setVisibility(View.INVISIBLE);
            viewHolder.btnDeletQty.setVisibility(View.INVISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.btnToppings.imgIcon.setImageResource(R.drawable.ic_cart_icon_57);
        viewHolder.btnToppings.imgIcon.setImageResource(R.drawable.ic_nav_my_order);

        viewHolder.btnToppings.btnText.setText(context.getString(R.string.btn_add_toppings_value, cart.getTopping().size()));
        if (cart.getTopping().size() == 0) {
            viewHolder.btnToppings.setEnabled(false);
        } else {
            if (isFromHistory)
                viewHolder.btnToppings.setEnabled(false);
            else
                viewHolder.btnToppings.setEnabled(true);
        }
        try {
            /*if (position % 2 == 0) {
                viewHolder.cartLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            } else {
                viewHolder.cartLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
            }*/
            viewHolder.cartLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            viewHolder.tvItemName.setText(cart.getProductName());
            //viewHolder.tvPrice.setText(String.format("%s %s", AppUtils.CurrencyFormat(cart.getUnitPrice()), currency));
            viewHolder.tvPrice.setText(String.format("%s %s\n%s %s", AppUtils.CurrencyFormat(cart.getUnitPrice()), currency, cart.getItemPoints(), context.getResources().getString(R.string.lbl_order_point)));
            viewHolder.edQuantity.setText(String.format("%d", cart.getQuantity()));
            AppUtils.setImage(viewHolder.imgLogo, cart.getProductImage());
            viewHolder.tvTotalAmount.setText(AppUtils.CurrencyFormat(cart.getSubTotal()));

            viewHolder.btnToppings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.openOrderToppings(context, currency, cart);
                   /* Intent i = new Intent(context, OrderToppingsActivity.class);
                    i.putExtra("currency",currency);
                    i.putExtra("topping",cart);
                    context.startActivity(i);*/
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }

    static class ViewHolder {
        TextView tvItemName, tvPrice, tvTotalAmount;
        EditText edQuantity;
        ImageView btnDelete, imgLogo;
        RelativeLayout cartLayout;
        RelativeLayoutButton btnToppings;
        ImageView btnAddQty, btnDeletQty;
    }
}
