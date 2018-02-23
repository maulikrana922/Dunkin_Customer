package com.dunkin.customer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dunkin.customer.CartDetailActivity;
import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.adapters.CartAdapter;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.widget.RelativeLayoutButton;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements View.OnClickListener {
    public static RelativeLayoutButton btnCheckout;
    CartFragment fragment;
    private Context context;
    private List<ProductModel> cartList;
    private DBAdapter dbAdapter;
    private ListView lvCartList;
    private ProgressBar progressLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.cart_item_list_layout, container, false);
        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        dbAdapter = new DBAdapter(context);

        fragment = this;

        lvCartList = (ListView) rootView.findViewById(R.id.lvCartList);

        btnCheckout = (RelativeLayoutButton) rootView.findViewById(R.id.btnCheckout);

        btnCheckout.btnText.setText(getString(R.string.lbl_checkout));
        btnCheckout.imgIcon.setImageResource(R.drawable.ic_nav_my_order);

        btnCheckout.setOnClickListener(this);
        cartList = new ArrayList<>();

        getDataFromAPI();

        if (cartList.size() == 0) {
            btnCheckout.setVisibility(View.GONE);
        }

        CartAdapter cartAdapter = new CartAdapter(context, cartList);
        lvCartList.setAdapter(cartAdapter);

        lvCartList.setEmptyView(rootView.findViewById(R.id.emptyElement));
        return rootView;
    }

    @Override
    public void onResume() {
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
        fragment.startActivityForResult(i, 1);
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
            ((NewHomeActivity) getActivity()).addFragment(new CategoryFragment(), "Category");
        }
    }
}
