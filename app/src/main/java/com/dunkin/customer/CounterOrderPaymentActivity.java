package com.dunkin.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Admin on 10/23/2015.
 */
public class CounterOrderPaymentActivity extends BaseActivity {

    int counterOrderId;
    private ScrollView scrollContainer;
    private ProgressBar progressLoading;
    private JSONObject jsonObject;
    private TextView txtOrderId, tvShopName, txtOrderAmount, txtPoint, tvPaymentType, tvPaymentDate, emptyElement;
    private RelativeLayoutButton btnPay;
    private LinearLayout lvShopName;
    private String payOption;
    private String restautrantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        counterOrderId = Integer.parseInt(getIntent().getStringExtra("orderId"));
        if (getIntent().hasExtra("restaurant_name"))
            restautrantName = getIntent().getStringExtra("restaurant_name");

        inflateView(R.layout.activity_counter_order_payment, getString(R.string.nav_payment));

        txtOrderId = (TextView) findViewById(R.id.tvOrderId);
        tvShopName = (TextView) findViewById(R.id.tvShopName);
        lvShopName = (LinearLayout) findViewById(R.id.lvShopName);
        txtOrderAmount = (TextView) findViewById(R.id.tvAmount);
        txtPoint = (TextView) findViewById(R.id.tvPoints);
        tvPaymentType = (TextView) findViewById(R.id.tvPaymentType);
        tvPaymentDate = (TextView) findViewById(R.id.tvPaymentDate);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        btnPay = (RelativeLayoutButton) findViewById(R.id.btnPay);
        btnPay.btnText.setText(getString(R.string.btn_submit));
        btnPay.imgIcon.setImageResource(R.drawable.ic_submit);
        btnPay.setVisibility(View.GONE);

        emptyElement = (TextView) findViewById(R.id.emptyElement);

        try {
            AppController.getCounterOrder(CounterOrderPaymentActivity.this, counterOrderId, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());

                    progressLoading.setVisibility(View.GONE);

                    if (jsonResponse.getInt("success") == 1) {
                        scrollContainer.setVisibility(View.VISIBLE);
                        emptyElement.setVisibility(View.GONE);
                        jsonObject = jsonResponse.getJSONObject("orderDetail");
                        txtOrderId.setText(getString(R.string.txt_order_id, String.valueOf(jsonObject.getInt("counter_orderid"))));

                        // Restaurant Name
                        if (AppUtils.isNotNull(restautrantName)) {
                            lvShopName.setVisibility(View.VISIBLE);
                            tvShopName.setText(getString(R.string.lbl_counter_order_shop_name, restautrantName));
                        } else {
                            lvShopName.setVisibility(View.GONE);
                        }

                        if (jsonObject.has("orderStatus")) {
                            if (jsonObject.getString("orderStatus").equalsIgnoreCase("Order Refund")) {
                                txtOrderAmount.setText(getString(R.string.txt_order_refund_amount, "-" + AppUtils.CurrencyFormat(Double.parseDouble(jsonObject.getString("total")))));
                                txtPoint.setText(getString(R.string.txt_number_of_refund_points, AppUtils.CurrencyFormat(Double.parseDouble(jsonObject.getString("points")))));
                            } else {
                                txtOrderAmount.setText(getString(R.string.txt_order_amount, AppUtils.CurrencyFormat(Double.parseDouble(jsonObject.getString("total")))));
                                txtPoint.setText(getString(R.string.txt_number_of_points1, AppUtils.CurrencyFormat(Double.parseDouble(jsonObject.getString("points")))));
                            }
                        } else {
                            txtOrderAmount.setText(getString(R.string.txt_order_amount, AppUtils.CurrencyFormat(Double.parseDouble(jsonObject.getString("total")))));
                            txtPoint.setText(getString(R.string.txt_number_of_points1, AppUtils.CurrencyFormat(Double.parseDouble(jsonObject.getString("points")))));
                        }

                        try {
                            //SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.MM_DD_YYYY_HH_MM_SS_SERVER, Locale.getDefault());
                            //Date orderDate = sdf.parse(jsonObject.getString("orderDate"));
                            tvPaymentDate.setText(AppUtils.getFormattedDateTimeRedeemHistory(jsonObject.getString("orderDate")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        payOption = jsonObject.getString("pay_option");
                        if (jsonObject.getString("pay_option").equalsIgnoreCase("1"))
                            tvPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_cash)));
                        else if (jsonObject.getString("pay_option").equalsIgnoreCase("2"))
                            tvPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_wallet)));
                        else if (jsonObject.getString("pay_option").equalsIgnoreCase("3"))
                            tvPaymentType.setText(getString(R.string.txt_payment_type, getString(R.string.pay_by_points)));

                    } else if (jsonResponse.getInt("success") == 0) {
                        scrollContainer.setVisibility(View.GONE);
                        emptyElement.setVisibility(View.VISIBLE);
                        emptyElement.setText(R.string.msg_no_order_found);
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDialog(getString(R.string.payment_option));
                try {
                    makePayment();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void openDialog(String title) {
        ArrayAdapter<PaymentModel> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, AppUtils.getPaymentList(CounterOrderPaymentActivity.this));

        AlertDialog.Builder alert = new AlertDialog.Builder(CounterOrderPaymentActivity.this);
        alert.setTitle(title);

        alert.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    makePayment(AppUtils.getPaymentList(CounterOrderPaymentActivity.this).get(which));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.create().show();
    }*/

    private void makePayment() throws UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("counter_orderid", jsonObject.getInt("counter_orderid"));
            jsonRequest.put("email", AppUtils.getAppPreference(CounterOrderPaymentActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            jsonRequest.put("points", jsonObject.getString("points"));
            jsonRequest.put("total", jsonObject.getString("total"));
            jsonRequest.put("pay_option", payOption);
            jsonRequest.put("currency_id", jsonObject.getString("currency_id"));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(CounterOrderPaymentActivity.this).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

            AppController.makePaymentForCounterOrder(CounterOrderPaymentActivity.this, jsonRequest, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());

                    if (jsonResponse.getInt("success") == 1) {
                        finish();
                        AppUtils.showToastMessage(CounterOrderPaymentActivity.this, getString(R.string.msg_payment_success));
                    }

                    if (jsonResponse.getInt("success") == 2) {
                        finish();
                        AppUtils.showToastMessage(CounterOrderPaymentActivity.this, getString(R.string.msg_already_payment));
                    }

                    if (jsonResponse.getInt("success") == 0) {
                        //AppUtils.showToastMessage(CounterOrderPaymentActivity.this, getString(R.string.msg_payment_failed));
                        AppUtils.showToastMessage(CounterOrderPaymentActivity.this, jsonResponse.getString("message"));
                    }
                     if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
