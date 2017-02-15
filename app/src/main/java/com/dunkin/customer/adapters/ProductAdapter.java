package com.dunkin.customer.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends BaseAdapter {

    public ArrayList<Integer> quantity = new ArrayList<>();
    private List<ProductModel> productList;
    private Context context;
    private LayoutInflater inflater;
    private String currencyCode;
    private int qunatity;
    private int country_id;
    //private ProductModel pm;
    private DBAdapter dbAdapter;

    public ProductAdapter(Context context, List<ProductModel> productList, int country_id, String currencyCode) {
        this.context = context;
        this.productList = productList;
        this.currencyCode = currencyCode;
        this.country_id = country_id;

        for (int i = 0; i < productList.size(); i++)
            quantity.add(1);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        dbAdapter = new DBAdapter(context);
        final ProductModel pm = (ProductModel) getItem(position);

        pm.setCurrency(currencyCode);

        final ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.product_list_page, null);

            viewHolder.productLayout = (RelativeLayout) convertView.findViewById(R.id.productLayout);
            viewHolder.imgProductLogo = (ImageView) convertView.findViewById(R.id.imgLogo);
            viewHolder.btnAddQty = (ImageView) convertView.findViewById(R.id.btnAddQty);
            viewHolder.btnDeletQty = (ImageView) convertView.findViewById(R.id.btnDeletQty);
            viewHolder.txtProductName = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.txtProdutPrice = (TextView) convertView.findViewById(R.id.txtDesc);
            viewHolder.edProQuantity = (EditText) convertView.findViewById(R.id.edProQuantity);

            viewHolder.btnAddToCart = (RelativeLayoutButton) convertView.findViewById(R.id.btnAddToCart);
            //viewHolder.btnAddToCart.imgIcon.setImageResource(R.drawable.ic_cart_icon_57);
            viewHolder.btnAddToCart.imgIcon.setImageResource(R.drawable.ic_nav_my_order);
            viewHolder.btnAddToCart.btnText.setText(context.getString(R.string.txt_add_to_cart));

            viewHolder.btnAddToppings = (RelativeLayoutButton) convertView.findViewById(R.id.btnAddToppings);
            viewHolder.btnAddToppings.btnText.setText(context.getString(R.string.btn_add_toppings2));
            //viewHolder.btnAddToppings.imgIcon.setImageResource(R.drawable.ic_cart_icon_57);
            viewHolder.btnAddToppings.imgIcon.setImageResource(R.drawable.ic_nav_my_order);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            AppUtils.setImage(viewHolder.imgProductLogo, pm.getProductImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.txtProductName.setText(String.format("%s", pm.getProductName()));
        //viewHolder.txtProdutPrice.setText(String.format("%s %s", AppUtils.CurrencyFormat(pm.getProductPrice()), pm.getCurrency()));
        viewHolder.txtProdutPrice.setText(context.getString(R.string.txt_product_amount, AppUtils.currencyFormat(pm.getProductPrice()), pm.getCurrency()));

        //viewHolder.edProQuantity.setText(String.format("%d", pm.getQty()));
        viewHolder.edProQuantity.setText(String.format("%s", quantity.get(position)));

        // Uncomment if required
        /*if (country_id == AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1)) {
            viewHolder.btnAddToCart.setVisibility(View.VISIBLE);
        } else
            viewHolder.btnAddToCart.setVisibility(View.GONE);*/

        /*if (position % 2 == 0) {
            //  viewHolder.imgProductLogo.setBorderColor(context.getResources().getColor(R.color.alternate_image_border));
            viewHolder.productLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        } else {
            //viewHolder.imgProductLogo.setBorderColor(context.getResources().getColor(R.color.alternate_image_border));
            viewHolder.productLayout.setBackgroundColor(context.getResources().getColor(R.color.alternate_listing_bg));
        }*/

        viewHolder.productLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

        // Uncomment if required
        /*if (pm.getToppings() != null && pm.getToppings().size() > 0) {
            viewHolder.btnAddToppings.setVisibility(View.VISIBLE);
        } else {
            viewHolder.btnAddToppings.setVisibility(View.INVISIBLE);
        }*/

        /*viewHolder.btnAddQty.setOnClickListener(new clickListener(pm, viewHolder, position));

        viewHolder.btnDeletQty.setOnClickListener(new clickListener(pm, viewHolder, position));*/

        viewHolder.btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.edProQuantity.setText(String.format("%s", Integer.parseInt(viewHolder.edProQuantity.getText().toString()) + 1));
                //pm.setQty(Integer.parseInt(viewHolder.edProQuantity.getText().toString()));
                quantity.set(position, quantity.get(position) + 1);
                productList.get(position).setQty(quantity.get(position));
            }
        });

        viewHolder.btnDeletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity.get(position) != 1) {
                    //pm.setQty(pm.getQty() - 1);
                    //viewHolder.edProQuantity.setText(String.format("%d", pm.getQty()));

                    viewHolder.edProQuantity.setText(String.format("%s", Integer.parseInt(viewHolder.edProQuantity.getText().toString()) - 1));
                    quantity.set(position, quantity.get(position) - 1);
                    productList.get(position).setQty(quantity.get(position));
                }
            }
        });

        viewHolder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pm.setProductToppings(pm.getToppings());
                int subtotal = pm.getProductPrice() * pm.getQty();
                pm.setSubTotal(subtotal);
                if (pm != null) {
                    openToppingDialog(pm, true, false);
                }
            }
        });

        viewHolder.btnAddToppings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pm.setProductToppings(pm.getToppings());
                openToppingDialog(pm, false, true);
            }
        });

        viewHolder.edProQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pm.setQty(Integer.parseInt(s.toString()));
            }
        });

        return convertView;
    }

    private void openToppingDialog(final ProductModel model, final boolean isFromOrder, final boolean fromAddOnOrCart) {

        if (model.getToppings() != null && model.getToppings().size() > 0) {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(context.getString(R.string.al_warning));
            alert.setMessage(context.getString(R.string.msg_al_add_topping_add_ons));
            alert.setPositiveButton(context.getString(R.string.al_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppUtils.openToppingActivity(context, model, fromAddOnOrCart);
                  /*  Intent i = new Intent(context, ToppingListActivity.class);
                    i.putExtra("product", productList.get(position));
                    context.startActivity(i);*/
                }
            });

            alert.setNegativeButton(context.getString(R.string.al_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (isFromOrder) {
                        dbAdapter.open();
                        dbAdapter.addProductInCart(model, null);
                        dbAdapter.close();
                    }
                    dialog.dismiss();
                }
            });
            alert.create().show();
        } else {
            dbAdapter.open();
            dbAdapter.addProductInCart(model, null);
            dbAdapter.close();
        }
    }

    static class ViewHolder {
        ImageView imgProductLogo;
        ImageView btnAddQty, btnDeletQty;
        EditText edProQuantity;
        TextView txtProductName, txtProdutPrice;

        RelativeLayout productLayout;
        RelativeLayoutButton btnAddToCart, btnAddToppings;
    }
}
