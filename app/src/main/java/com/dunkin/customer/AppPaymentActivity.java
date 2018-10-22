package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.adapters.OrderItemAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OrderModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AppPaymentActivity extends BaseActivity {

    private JSONObject jsonOrderDetail;
    private ListView lvProductDetails;
    private OrderModel om;
    private ProgressBar progressLoading;
    private TextView tvTotalAmount, tvCustRequirePoints, emptyElement, tvCustPaymentType;
    private TextView tvCustOrderId;
    private LinearLayout llHeader;
    private Menu mMenu;
    private Boolean isBackHome;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        getMenuInflater().inflate(R.menu.submit_btn_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {
            //openDialog(getString(R.string.payment_option));
            try {
                makePayment();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            if(isBackHome){// if app not in foreground then
                Intent intent=new Intent(this,NewHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{// if app in foreground then
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (jsonOrderDetail == null || jsonOrderDetail.length() == 0) {
            progressLoading.setVisibility(View.GONE);
            menu.findItem(R.id.menu_submit).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_pay, getString(R.string.lbl_activity_pay));

        isBackHome=getIntent().getBooleanExtra("isBackHome",false);

        lvProductDetails = (ListView) findViewById(R.id.lvProductDetails);

        tvCustOrderId = (TextView) findViewById(R.id.tvCustOrderId);
        TextView tvCustTableId = (TextView) findViewById(R.id.tvCustTableId);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        tvCustRequirePoints = (TextView) findViewById(R.id.tvCustRequirePoints);
        tvCustPaymentType = (TextView) findViewById(R.id.tvCustPaymentType);
        emptyElement = (TextView) findViewById(R.id.emptyElement);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        llHeader = (LinearLayout) findViewById(R.id.llHeader);

        try {
            jsonOrderDetail = new JSONObject();
            jsonOrderDetail.put("order_id", getIntent().getStringExtra("order_id"));

            if (getIntent().hasExtra("table_id"))
                if (AppUtils.isNotNull(getIntent().getStringExtra("table_id")))
                    jsonOrderDetail.put("table_id", getIntent().getStringExtra("table_id"));
                else
                    jsonOrderDetail.put("table_id", "");
            else
                jsonOrderDetail.put("table_id", "");

            jsonOrderDetail.put("restaurant_id", getIntent().getStringExtra("restaurant_id"));

            Dunkin_Log.d("jsonOrderDetail", jsonOrderDetail.toString());

            if (getIntent().hasExtra("table_id"))
                if (AppUtils.isNotNull(jsonOrderDetail.getString("table_id")))
                    tvCustTableId.setText(getString(R.string.txt_table_id, jsonOrderDetail.getString("table_id")));
                else
                    tvCustTableId.setVisibility(View.GONE);
            else
                tvCustTableId.setVisibility(View.GONE);

            // GET ORDER DETAIL BY ORDER ID
            getDataFromAPI(jsonOrderDetail.getString("order_id"));
        } catch (JSONException e) {
            e.printStackTrace();
            lvProductDetails.setVisibility(View.GONE);
            progressLoading.setVisibility(View.GONE);
            emptyElement.setVisibility(View.VISIBLE);
            emptyElement.setText(R.string.msg_no_order_found);
            llHeader.setVisibility(View.GONE);
        }
    }

    private void getDataFromAPI(String orderId) {

        try {
            AppController.getOrderDetail(AppPaymentActivity.this, orderId, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());

                    progressLoading.setVisibility(View.GONE);

                    if (jsonResponse.getInt("success") == 1) {
                        llHeader.setVisibility(View.VISIBLE);
                        emptyElement.setVisibility(View.GONE);
                        om = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("orderDetail").getJSONObject(0).toString(), new TypeReference<OrderModel>() {
                        });
                        tvCustOrderId.setText(getString(R.string.txt_order_id, om.getOrderId()));
                        tvTotalAmount.setText(String.format("%s %s", getString(R.string.txt_order_amount, AppUtils.CurrencyFormat(Double.parseDouble(om.getOrderAmount()))), om.getCurrency()));
                        tvCustRequirePoints.setText(getString(R.string.txt_points_require, AppUtils.CurrencyFormat(Double.parseDouble(om.getPointneed()))));
                        if (om.getPay_option().equalsIgnoreCase("1"))
                            tvCustPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_cash)));
                        else if (om.getPay_option().equalsIgnoreCase("2"))
                            tvCustPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_wallet)));
                        else if (om.getPay_option().equalsIgnoreCase("3"))
                            tvCustPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_points)));

                        lvProductDetails.setVisibility(View.VISIBLE);
                        mMenu.findItem(R.id.menu_submit).setVisible(true);
                        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(AppPaymentActivity.this, om.getOrderItems(), om.getCurrency(), false);
                        lvProductDetails.setAdapter(orderItemAdapter);
                    } else if (jsonResponse.getInt("success") == 0) {
                        mMenu.findItem(R.id.menu_submit).setVisible(false);
                        llHeader.setVisibility(View.GONE);
                        lvProductDetails.setVisibility(View.GONE);
                        emptyElement.setVisibility(View.VISIBLE);
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void makePayment() throws UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", AppUtils.getAppPreference(AppPaymentActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            jsonRequest.put("order_id", jsonOrderDetail.getString("order_id"));

            if (AppUtils.isNotNull(jsonOrderDetail.getString("table_id")))
                jsonRequest.put("table_id", jsonOrderDetail.getString("table_id"));
            else
                jsonRequest.put("table_id", "");

            jsonRequest.put("restaurant_id", jsonOrderDetail.getString("restaurant_id"));
            jsonRequest.put("total", om.getOrderAmount());
            jsonRequest.put("currency_id", om.getCurrency_id());
            jsonRequest.put("pointsRequired", om.getPointneed());
            jsonRequest.put("pay_option", om.getPay_option());
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(AppPaymentActivity.this).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

            AppController.payAmountByCustomer(AppPaymentActivity.this, jsonRequest, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    //Dunkin_Log.d("DataResponse", (String) result);

                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        AppUtils.showToastMessage(AppPaymentActivity.this, getString(R.string.msg_payment_success));
                        finish();
                    }

                    if (jsonResponse.getInt("success") == 2) {
                        AppUtils.showToastMessage(AppPaymentActivity.this, getString(R.string.msg_already_payment));
                        finish();
                    }

                    if (jsonResponse.getInt("success") == 0) {
                        AppUtils.showToastMessage(AppPaymentActivity.this, getString(R.string.msg_payment_failed));
                    }
                     if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                     if (jsonResponse.getInt("success") == 99) {
                         displayDialog(jsonResponse.getString("message"));
                     }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*private void openDialog(String title) {
        ArrayAdapter<PaymentModel> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, AppUtils.getPaymentList(AppPaymentActivity.this));

        AlertDialog.Builder alert = new AlertDialog.Builder(AppPaymentActivity.this);
        alert.setTitle(title);

        alert.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    makePayment(AppUtils.getPaymentList(AppPaymentActivity.this).get(which));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.create().show();
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isBackHome){
            Intent intent=new Intent(this,NewHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            finish();
        }
    }
}
