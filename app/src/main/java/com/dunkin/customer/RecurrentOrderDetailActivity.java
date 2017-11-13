package com.dunkin.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.RecurrentOrderItemAdapter;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.RecurrentOrderModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class RecurrentOrderDetailActivity extends BackActivity {

    private RecurrentOrderModel recurrentOrderModel;
    private ListView recurrentOrderItemList;
    private TextView emptyElement, txtRecurrentOrderDate, txtRecurrentDate, txtRecurrentDays, txtShippingAddress, txtBillingAddress, txtRecurrentOrderAmount;
    private ProgressBar progressLoading;
    private ScrollView scrollContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_recurrent_order_detail, getString(R.string.lbl_recurrent_order_detail));

        String recurrentOrderId = getIntent().getStringExtra("recurrentOrderId");

        initializeViews();

        getDataFromAPI(recurrentOrderId);
    }

    private void initializeViews() {
        emptyElement = (TextView) findViewById(R.id.emptyElement);
        txtBillingAddress = (TextView) findViewById(R.id.txtBillingAddress);
        txtShippingAddress = (TextView) findViewById(R.id.txtShippingAddress);
        txtRecurrentOrderDate = (TextView) findViewById(R.id.txtRecurrentOrderDate);
        txtRecurrentOrderAmount = (TextView) findViewById(R.id.txtRecurrentOrderAmount);
        txtRecurrentDate = (TextView) findViewById(R.id.txtRecurrentDate);
        txtRecurrentDays = (TextView) findViewById(R.id.txtRecurrentDays);
        recurrentOrderItemList = (ListView) findViewById(R.id.recurrentOrderItemList);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
    }

    private void getDataFromAPI(String recurrentOrderId) {

        try {
            AppController.getRecurrentOrderDetail(RecurrentOrderDetailActivity.this, recurrentOrderId, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Dunkin_Log.i("RESPONSE", jsonResponse.toString());

                    progressLoading.setVisibility(View.GONE);
                    if (jsonResponse.getInt("success") == 1) {
                        scrollContainer.setVisibility(View.VISIBLE);
                        emptyElement.setVisibility(View.GONE);
                        recurrentOrderModel = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("orderList").toString(), new TypeReference<RecurrentOrderModel>() {
                        });

                        if (recurrentOrderModel.getBillingAddress().equals("")) {
                            txtBillingAddress.setText(getString(R.string.txt_not_applicable));
                        } else {
                            txtBillingAddress.setText(recurrentOrderModel.getBillingAddress());
                        }
                        if (recurrentOrderModel.getShippingAddress().equals("")) {
                            txtShippingAddress.setText(getString(R.string.txt_not_applicable));
                        } else {
                            txtShippingAddress.setText(recurrentOrderModel.getShippingAddress());
                        }

                        txtRecurrentOrderAmount.setText(getString(R.string.txt_order_amount, AppUtils.CurrencyFormat(Double.parseDouble(recurrentOrderModel.getOrderAmount())) + " " + recurrentOrderModel.getCurrency()));

                        txtRecurrentOrderDate.setText(getString(R.string.txt_order_date, AppUtils.getFormattedDate(recurrentOrderModel.getCreated_at())));
                        txtRecurrentDate.setText(getString(R.string.txt_recurrent_order_date, AppUtils.getFormattedDate(recurrentOrderModel.getOrder_start_date()), AppUtils.getFormattedDate(recurrentOrderModel.getOrder_end_date())));
                        txtRecurrentDays.setText(getString(R.string.txt_recurrent_order_days, recurrentOrderModel.getSelected_day()));

                        RecurrentOrderItemAdapter recurrentOrderItemAdapter = new RecurrentOrderItemAdapter(RecurrentOrderDetailActivity.this, recurrentOrderModel.getOrderItems(), recurrentOrderModel.getCurrency());
                        recurrentOrderItemList.setAdapter(recurrentOrderItemAdapter);
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    } else {
                        emptyElement.setVisibility(View.VISIBLE);
                        scrollContainer.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void cancelRecurrentOrder(String title, String message, final String recurrentOrderId) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(RecurrentOrderDetailActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(getString(R.string.al_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                try {
                    AppController.cancelRecurrentOrder(RecurrentOrderDetailActivity.this, recurrentOrderId, new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);

                            //Dunkin_Log.i("RESPONSE", jsonResponse.toString());

                            progressLoading.setVisibility(View.GONE);
                            if (jsonResponse.getInt("success") == 1) {
                                AppUtils.showToastMessage(RecurrentOrderDetailActivity.this, jsonResponse.getString("message"));
                                setResult(RESULT_OK);
                                finish();
                            }else if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                            }
                            else {
                                if(jsonResponse.getInt("success") != 99) {
                                    AppUtils.showToastMessage(RecurrentOrderDetailActivity.this, getString(R.string.system_error));
                                }
                            }
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
//                    AppUtils.showToastMessage(RecurrentOrderDetailActivity.this, getString(R.string.system_error));
                }
            }
        });

        dialog.setNegativeButton(getString(R.string.al_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_recurrent_order_btn_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_cancel_recurrent_order) {
            cancelRecurrentOrder(getString(R.string.lbl_recurrent_order), getString(R.string.al_cancel_recurrent_order_confirmation), recurrentOrderModel.getReference_id());
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }
}
