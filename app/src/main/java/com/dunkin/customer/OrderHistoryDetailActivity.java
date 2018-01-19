package com.dunkin.customer;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.OrderItemAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OrderModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class OrderHistoryDetailActivity extends BaseActivity {

    private OrderModel om;
    private ListView lvOrderItemList;
    private TextView txtOrderDate, txtOrderStatus, txtShippingAddress, txtBillingAddress, txtOrderAmount, txtOrderPoint, txtOrderPaymentType;
    private ProgressBar progressLoading;
    private ScrollView scrollContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_order_history_detail, getString(R.string.lbl_order_detail));

        String orderId = getIntent().getStringExtra("orderId");

        initializeViews();

        getDataFromAPI(orderId);
    }

    private void initializeViews() {
        txtBillingAddress = (TextView) findViewById(R.id.txtBillingAddress);
        txtShippingAddress = (TextView) findViewById(R.id.txtShippingAddress);
        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);
        txtOrderAmount = (TextView) findViewById(R.id.txtOrderAmount);
        txtOrderPoint = (TextView) findViewById(R.id.txtOrderPoint);
        txtOrderPaymentType = (TextView) findViewById(R.id.txtOrderPaymentType);
        txtOrderStatus = (TextView) findViewById(R.id.txtOrderStatus);
        lvOrderItemList = (ListView) findViewById(R.id.orderItemList);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
    }

    private void getDataFromAPI(String orderId) {

        try {
            AppController.getOrderDetail(OrderHistoryDetailActivity.this, orderId, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Dunkin_Log.i("RESPONSE", jsonResponse.toString());

                    progressLoading.setVisibility(View.GONE);
                    if (jsonResponse.getInt("success") == 1) {
                        scrollContainer.setVisibility(View.VISIBLE);
                        om = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("orderDetail").getJSONObject(0).toString(), new TypeReference<OrderModel>() {
                        });

                        if (om.getBillingAddress().equals("")) {
                            txtBillingAddress.setText(getString(R.string.txt_not_applicable));
                        } else {
                            txtBillingAddress.setText(om.getBillingAddress());
                        }
                        if (om.getShippingAddress().equals("")) {
                            txtShippingAddress.setText(getString(R.string.txt_not_applicable));
                        } else {
                            txtShippingAddress.setText(om.getShippingAddress());
                        }

                        if (AppUtils.isNotNull(om.getOrderAmount()))
                            txtOrderAmount.setText(getString(R.string.txt_order_amount, AppUtils.CurrencyFormat(Double.parseDouble(om.getOrderAmount())) + " " + om.getCurrency()));
                        else
                            txtOrderAmount.setText(getString(R.string.txt_order_amount, "-"));
                        if (AppUtils.isNotNull(om.getPointneed()))
                            txtOrderPoint.setText(getString(R.string.txt_order_point, AppUtils.CurrencyFormat(Double.parseDouble(om.getPointneed()))));
                        else
                            txtOrderPoint.setText(getString(R.string.txt_order_point, "-"));

                        // Product Pay Option
                        if (AppUtils.isNotNull(om.getPay_option())) {
                            if (om.getPay_option().equalsIgnoreCase("-1"))
                                txtOrderPaymentType.setVisibility(View.GONE);
                            else if (om.getPay_option().equalsIgnoreCase("0"))
                                txtOrderPaymentType.setVisibility(View.GONE);
                            else
                                txtOrderPaymentType.setVisibility(View.VISIBLE);

                            if (om.getPay_option().equalsIgnoreCase("1"))
                                txtOrderPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_cash)));
                            else if (om.getPay_option().equalsIgnoreCase("2"))
                                txtOrderPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_wallet)));
                            else if (om.getPay_option().equalsIgnoreCase("3"))
                                txtOrderPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_points)));
                        } else
                            txtOrderPaymentType.setVisibility(View.GONE);

                        txtOrderDate.setText(Html.fromHtml(getString(R.string.txt_order_date, AppUtils.getFormattedDate(om.getOrderDate()))));
                        txtOrderStatus.setText(om.getOrderStatus());

                        if (om.getOrderItems() != null && om.getOrderItems().size() > 0) {
                            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(OrderHistoryDetailActivity.this, om.getOrderItems(), om.getCurrency(), true);
                            lvOrderItemList.setAdapter(orderItemAdapter);
                        }
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
