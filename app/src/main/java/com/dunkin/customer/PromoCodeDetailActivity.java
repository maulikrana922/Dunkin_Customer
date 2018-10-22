package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.PromoDetailModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Created by qtm-c-android on 30/6/17.
 */

public class PromoCodeDetailActivity extends BaseActivity {
    private TextView txtOfferCode, txtNumberUser, txtStartTime, txtEndTime,
            txtUserPoint, txtMyPoint;
    private ImageView  imgProductQrCode;
    private PromoDetailModel of;
    private int country_id;
    private ScrollView scrollContainer;
    private ProgressBar progressLoading;
    private Boolean isBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_promp_detail, "PROMOCODE DETAILS");

        isBackHome=getIntent().getBooleanExtra("isBackHome",false);

        int promoId = Integer.parseInt(getIntent().getStringExtra("promoId"));
        if (getIntent().hasExtra("country_id"))
            country_id = getIntent().getIntExtra("country_id", -1);
        else
            country_id = AppUtils.getAppPreference(PromoCodeDetailActivity.this).getInt(AppConstants.USER_COUNTRY, -1);

        initializeViews();

        try {
            getDataFromAPI(promoId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
    }

    private void initializeViews() {
        txtOfferCode = (TextView) findViewById(R.id.txtOfferCode);
        txtNumberUser = (TextView) findViewById(R.id.txtNumberUser);
        txtStartTime = (TextView) findViewById(R.id.txtStartTime);
        txtEndTime = (TextView) findViewById(R.id.txtEndTime);
        txtUserPoint = (TextView) findViewById(R.id.txtUserPoint);
        txtMyPoint = (TextView) findViewById(R.id.txtMyPoint);
        imgProductQrCode = (ImageView) findViewById(R.id.imgProductQrCode);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
    }

    private void getDataFromAPI(int promoId) throws UnsupportedEncodingException, JSONException {
        AppController.getPromoDetail(PromoCodeDetailActivity.this, promoId, country_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                progressLoading.setVisibility(View.GONE);
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
                    of = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("data").toString(),
                            new TypeReference<PromoDetailModel>() {
                    });
                    if (of != null) {
                        AppUtils.setImage(imgProductQrCode, of.getPromoQRCode());

                        String offerCode = Html.fromHtml(of.getPromoCode()).toString();
                        txtOfferCode.setText(offerCode);

                        String maxUse = Html.fromHtml(of.getMaxUse()).toString();
                        txtNumberUser.setText(maxUse);

                        String start_time = Html.fromHtml(of.getStartTime()).toString();
                        txtStartTime.setText(start_time);

                        String user_point = Html.fromHtml(of.getUserPoint()).toString();
                        txtUserPoint.setText(user_point);

                        String my_point = Html.fromHtml(of.getMyPoint()).toString();
                        txtMyPoint.setText(my_point);

                        String end_time = Html.fromHtml(of.getEndTime()).toString();
                        txtEndTime.setText(end_time);
                    }
                } else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }else if(jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }else {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return super.onOptionsItemSelected(item);
    }

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

