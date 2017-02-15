package com.dunkin.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.OfferProductDetailAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OfferProductModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Admin on 3/4/2016.
 */
public class OfferPaymentActivity extends BackActivity {

    int offerId, country_id, currency_id;
    double points;
    private TextView txtTotalAmount, txtPoints, txtOfferTitle, txtPaymentType, emptyElement, txtOfferTotalPoints;
    private ListView lvProductDetail;
    private TableLayout tlHeader;
    private String referenceId, total;
    private String payOption;
    private Menu mMenu;
    private TableRow trPaymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_pay_offer, getString(R.string.nav_offer));
        initializeViews();
        getDataFromAPI();
    }

    private void initializeViews() {
        tlHeader = (TableLayout) findViewById(R.id.tlHeader);
        txtTotalAmount = (TextView) findViewById(R.id.txtTotalAmount);
        txtPoints = (TextView) findViewById(R.id.txtOfferPoints);
        txtOfferTotalPoints = (TextView) findViewById(R.id.txtOfferTotalPoints);
        txtOfferTitle = (TextView) findViewById(R.id.txtOfferTitle);
        txtPaymentType = (TextView) findViewById(R.id.txtPaymentType);
        lvProductDetail = (ListView) findViewById(R.id.lvOfferProductsDetail);
        emptyElement = (TextView) findViewById(R.id.emptyElement);
        trPaymentType = (TableRow) findViewById(R.id.trPaymentType);

        if (getIntent().hasExtra("reference_id"))
            referenceId = getIntent().getStringExtra("reference_id");
        if (getIntent().hasExtra("offerId"))
            offerId = Integer.parseInt(getIntent().getStringExtra("offerId"));
        if (getIntent().hasExtra("country_id"))
            country_id = Integer.parseInt(getIntent().getStringExtra("country_id"));

        Log.i("DATA:", referenceId + " " + offerId + " " + country_id);
    }

    private void getDataFromAPI() {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("offerId", offerId);
            jsonRequest.put("reference_id", referenceId);
            jsonRequest.put("country_id", country_id);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(OfferPaymentActivity.this).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            jsonRequest.put("email", AppUtils.getAppPreference(OfferPaymentActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""));

            //Log.i("REQUEST ", jsonRequest.toString());

            AppController.offerPurchaseDetail(OfferPaymentActivity.this, jsonRequest, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    //Log.i("DataResponse", (String) result);
                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        mMenu.findItem(R.id.menu_submit).setVisible(true);

                        tlHeader.setVisibility(View.VISIBLE);
                        emptyElement.setVisibility(View.GONE);
                        currency_id = jsonResponse.getInt("currency_id");
                        total = AppUtils.CurrencyFormat(Double.parseDouble(String.valueOf(jsonResponse.getInt("Total"))));
                        points = jsonResponse.getDouble("Point");
                        double total_point = jsonResponse.getDouble("totalPoints");
                        txtOfferTotalPoints.setText(AppUtils.CurrencyFormat(total_point));
                        txtOfferTitle.setText(jsonResponse.getString("offerTitle"));

                        if (jsonResponse.getString("offerFree").equalsIgnoreCase("Yes")) {
                            txtTotalAmount.setText(String.format("0.00 %s", jsonResponse.getString("currency_code")));
                            txtPoints.setText("0.00");
                        } else {
                            txtTotalAmount.setText(String.format("%s %s", total, jsonResponse.getString("currency_code")));
                            txtPoints.setText(AppUtils.CurrencyFormat(points));
                        }

                        payOption = jsonResponse.getString("pay_option");
                        trPaymentType.setVisibility(View.GONE);

                        /*if (jsonResponse.getString("offerFree").equals("No")) {
                            //mMenu.findItem(R.id.menu_submit).setVisible(true);
                            trPaymentType.setVisibility(View.VISIBLE);
                            //payOption = jsonResponse.getString("pay_option");
                            if (jsonResponse.getString("pay_option").equalsIgnoreCase("1"))
                                txtPaymentType.setText(getString(R.string.pay_by_cash));
                            else if (jsonResponse.getString("pay_option").equalsIgnoreCase("2"))
                                txtPaymentType.setText(getString(R.string.pay_by_wallet));
                            else if (jsonResponse.getString("pay_option").equalsIgnoreCase("3"))
                                txtPaymentType.setText(getString(R.string.pay_by_points));
                        } else {
                            //mMenu.findItem(R.id.menu_submit).setVisible(false);
                            trPaymentType.setVisibility(View.GONE);
                        }*/

                        /*if (jsonResponse.getString("pay_option").equalsIgnoreCase("1"))
                            txtPaymentType.setText(getString(R.string.pay_by_cash));
                        else if (jsonResponse.getString("pay_option").equalsIgnoreCase("2"))
                            txtPaymentType.setText(getString(R.string.pay_by_wallet));
                        else if (jsonResponse.getString("pay_option").equalsIgnoreCase("3"))
                            txtPaymentType.setText(getString(R.string.pay_by_points));*/

                        List<OfferProductModel> of = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("offerproduct").toString(), new TypeReference<List<OfferProductModel>>() {
                        });

                        OfferProductDetailAdapter offerProductDetailAdapter = new OfferProductDetailAdapter(OfferPaymentActivity.this, of, jsonResponse.getString("offerFree"), jsonResponse.getString("currency_code"), false);
                        lvProductDetail.setAdapter(offerProductDetailAdapter);
                    } else if (jsonResponse.getInt("success") == 0) {
                        mMenu.findItem(R.id.menu_submit).setVisible(false);

                        tlHeader.setVisibility(View.GONE);
                        lvProductDetail.setVisibility(View.GONE);
                        emptyElement.setVisibility(View.VISIBLE);
                    }
                    else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mMenu = menu;
        if (!getIntent().getBooleanExtra("isFromHistory", false))
            getMenuInflater().inflate(R.menu.submit_btn_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_submit) {
            //openDialog(getString(R.string.payment_option));
            makePayment();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    /*private void openDialog(String title) {
        ArrayAdapter<PaymentModel> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, AppUtils.getPaymentList(OfferPaymentActivity.this));

        AlertDialog.Builder alert = new AlertDialog.Builder(OfferPaymentActivity.this);
        alert.setTitle(title);

        alert.setAdapter(dataAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makePayment(AppUtils.getPaymentList(OfferPaymentActivity.this).get(which));
            }
        });
        alert.create().show();
    }*/

    private void makePayment() {
        final JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("offerId", offerId);
            jsonRequest.put("reference_id", referenceId);
            jsonRequest.put("country_id", country_id);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(OfferPaymentActivity.this).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            jsonRequest.put("email", AppUtils.getAppPreference(OfferPaymentActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            String tempTotal;
            if(AppUtils.isNotNull(total)) {
                if (total.contains(",")) {
                    tempTotal = total.replaceAll(",", "");
                } else
                    tempTotal = total;
                jsonRequest.put("total", tempTotal);
            } else {
                jsonRequest.put("total", "");
            }
            jsonRequest.put("pay_option", payOption);
            jsonRequest.put("pointsRequired", points);
            jsonRequest.put("currency_id", currency_id);

            AppController.payOffer(OfferPaymentActivity.this, jsonRequest, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    //Log.i("DataResponse", jsonResponse.toString());

                    if (jsonResponse.getInt("success") == 1) {
                        AppUtils.showToastMessage(OfferPaymentActivity.this, getString(R.string.msg_payment_success));
                        finish();
                    }

                    if (jsonResponse.getInt("success") == 2) {
                        AppUtils.showToastMessage(OfferPaymentActivity.this, getString(R.string.msg_already_payment));
                        finish();
                    }

                    if (jsonResponse.getInt("success") == 0) {
                        AppUtils.showToastMessage(OfferPaymentActivity.this, getString(R.string.msg_payment_failed));
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