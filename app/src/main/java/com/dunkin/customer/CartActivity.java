package com.dunkin.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.adapters.CartAdapter;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 10/2/2015.
 */

/*
* This activity will be only used if we are redirecting from product page.
 */
public class CartActivity extends BaseActivity implements View.OnClickListener {
    public static RelativeLayoutButton btnCheckout;
    private Context context;
    private List<ProductModel> cartList;
    private DBAdapter dbAdapter;
    private ListView lvCartList;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = CartActivity.this;

        inflateView(R.layout.cart_item_list_layout, getString(R.string.nav_cart));

        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        dbAdapter = new DBAdapter(context);

        lvCartList = (ListView) findViewById(R.id.lvCartList);

        btnCheckout = (RelativeLayoutButton) findViewById(R.id.btnCheckout);
        btnCheckout.btnText.setText(getString(R.string.lbl_checkout));
        btnCheckout.imgIcon.setImageResource(R.drawable.ic_nav_my_order);

        btnCheckout.setOnClickListener(this);
        cartList = new ArrayList<>();

        if (cartList.size() == 0) {
            btnCheckout.setVisibility(View.GONE);
        }

        CartAdapter cartAdapter = new CartAdapter(context, cartList);
        lvCartList.setAdapter(cartAdapter);

        lvCartList.setEmptyView(findViewById(R.id.emptyElement));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromAPI();
    }

    public void getDataFromAPI() {

        progressLoading.setVisibility(View.GONE);
        dbAdapter.open();

        cartList = dbAdapter.getCartList();
        dbAdapter.close();
        if (cartList.size() == 0) {
            btnCheckout.setVisibility(View.GONE);
        } else {
            btnCheckout.setVisibility(View.VISIBLE);
        }
        CartAdapter cartAdapter = new CartAdapter(context, cartList);
        lvCartList.setAdapter(cartAdapter);
    }

    private void checkOut() {
        Intent i = new Intent(context, CartDetailActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCheckout) {
            if (cartList != null && cartList.size() > 0)
                checkOut();
            else
                AppUtils.showError(btnCheckout, getString(R.string.empty_cart));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
