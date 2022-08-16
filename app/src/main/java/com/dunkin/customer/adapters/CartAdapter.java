package com.dunkin.customer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dunkin.customer.CartActivity;
import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.fragments.CartFragment;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.models.ToppingModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<ProductModel> cartModelList;
    private DBAdapter dbAdapter;

    public CartAdapter(Context context, List<ProductModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
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

        final ProductModel cart = (ProductModel) getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.cart_item_item_list, null);
            dbAdapter = new DBAdapter(context);

            viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.imgLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
            viewHolder.edQuantity = (EditText) convertView.findViewById(R.id.edQuantity);
            viewHolder.btnDelete = (ImageView) convertView.findViewById(R.id.btnRemove);
            viewHolder.tvTotalAmount = (TextView) convertView.findViewById(R.id.tvTotalAmount);
            viewHolder.cartLayout = (RelativeLayout) convertView.findViewById(R.id.cartLayout);
            viewHolder.btnToppings = (RelativeLayoutButton) convertView.findViewById(R.id.btnToppings);
            //viewHolder.btnToppings.imgIcon.setImageResource(R.drawable.ic_cart_icon_57);
            viewHolder.btnToppings.imgIcon.setImageResource(R.drawable.ic_nav_my_order);
            viewHolder.btnAddQty = (ImageView) convertView.findViewById(R.id.btnAddQty);
            viewHolder.btnDeleteQty = (ImageView) convertView.findViewById(R.id.btnDeletQty);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnToppings.btnText.setText(context.getString(R.string.btn_add_toppings_value, cart.getToppings().size()));
        viewHolder.btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.setQty(cart.getQty() + 1);
                viewHolder.edQuantity.setText(String.format("%s", cart.getQty()));

                dbAdapter.open();
                dbAdapter.updateCartQuantity(cart);
                dbAdapter.close();
                cart.setSubTotal(cart.getProductPrice() * cart.getQty());
                viewHolder.total = cart.getSubTotal();
                if (cart.getToppings() != null && cart.getToppings().size() > 0) {
                    for (ToppingModel tm : cart.getToppings()) {
                        viewHolder.total = viewHolder.total + (Double.parseDouble(tm.getToppingPrice()) * cart.getQty());
                    }
                }
                //viewHolder.tvTotalAmount.setText(String.format("%.2f", viewHolder.total));
                viewHolder.tvTotalAmount.setText(AppUtils.CurrencyFormat(viewHolder.total));
            }
        });

        viewHolder.btnDeleteQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.getQty() != 1) {
                    cart.setQty(cart.getQty() - 1);
                    viewHolder.edQuantity.setText(String.format("%s", cart.getQty()));

                    dbAdapter.open();
                    dbAdapter.updateCartQuantity(cart);
                    dbAdapter.close();
                    cart.setSubTotal(cart.getProductPrice() * cart.getQty());
                    viewHolder.total = cart.getSubTotal();
                    if (cart.getToppings() != null && cart.getToppings().size() > 0) {
                        for (ToppingModel tm : cart.getToppings()) {
                            viewHolder.total = viewHolder.total + (Double.parseDouble(tm.getToppingPrice()) * cart.getQty());
                        }
                    }
                    //viewHolder.tvTotalAmount.setText(String.format("%.2f", viewHolder.total));
                    viewHolder.tvTotalAmount.setText(AppUtils.CurrencyFormat(viewHolder.total));
                }
            }
        });

        cart.setSubTotal(cart.getProductPrice() * cart.getQty());
        viewHolder.total = cart.getSubTotal();

        for (ToppingModel tm : cart.getToppings()) {
            viewHolder.total = viewHolder.total + (Double.parseDouble(tm.getToppingPrice()) * cart.getQty());
        }

        try {
            /*if (position % 2 == 0) {
                viewHolder.cartLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            } else {
                viewHolder.cartLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
            }*/
            viewHolder.cartLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            viewHolder.tvItemName.setText(cart.getProductName());

            viewHolder.tvPrice.setText(String.format("%s %s", AppUtils.CurrencyFormat(cart.getProductPrice()), cart.getCurrency()));

            viewHolder.edQuantity.setText(String.format("%s", cart.getQty()));
            AppUtils.setImage(viewHolder.imgLogo, cart.getProductImage());

            viewHolder.tvTotalAmount.setText(AppUtils.CurrencyFormat(viewHolder.total));

            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage(context.getString(R.string.msg_product_remove_confirmation));
                    alert.setTitle(context.getString(R.string.al_warning));
                    alert.setNegativeButton(context.getString(R.string.al_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbAdapter.open();
                            dbAdapter.removeProduct(cart.getRowId());
                            dbAdapter.close();
                            cartModelList.remove(position);

                            dialog.dismiss();
                            if (cartModelList.size() == 0 && context instanceof CartActivity) {
                                CartActivity.btnCheckout.setVisibility(View.GONE);
                            } else if (cartModelList.size() == 0) {
                                CartFragment.btnCheckout.setVisibility(View.GONE);
                            }
                            notifyDataSetChanged();
                        }
                    });

                    alert.setPositiveButton(context.getString(R.string.al_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.create().show();
                }
            });

            viewHolder.btnToppings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.openToppingActivity(context, cart, true);
                 /*   Intent  i = new Intent(context, ToppingListActivity.class);
                        i.putExtra("product",cart);
                    context.startActivity(i);*/
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvItemName, tvPrice, tvTotalAmount;
        EditText edQuantity;
        ImageView btnDelete, imgLogo;
        RelativeLayout cartLayout;
        RelativeLayoutButton btnToppings;
        ImageView btnAddQty, btnDeleteQty;
        double total = 0;
    }
}
