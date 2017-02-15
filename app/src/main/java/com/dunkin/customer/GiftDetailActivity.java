package com.dunkin.customer;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.GiftModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GiftDetailActivity extends BackActivity {
    private TextView txtGiftTitle, txtGiftDescription, txtGiftOrderDate, txtGiftCollectedDate, emptyElement;
    private ImageView imgGiftLogo;
    private ScrollView scrollContainer;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_gift_detail, getString(R.string.lbl_gift_detail));

        int giftOrderId = Integer.parseInt(getIntent().getStringExtra("giftOrderId"));

        initializeViews();

        try {
            getDataFromAPI(giftOrderId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        txtGiftTitle = (TextView) findViewById(R.id.txtGiftTitle);
        txtGiftDescription = (TextView) findViewById(R.id.txtGiftDescription);
        txtGiftOrderDate = (TextView) findViewById(R.id.txtGiftOrderDate);
        txtGiftCollectedDate = (TextView) findViewById(R.id.txtGiftCollectedDate);
        emptyElement = (TextView) findViewById(R.id.emptyElement);
        imgGiftLogo = (ImageView) findViewById(R.id.imgGiftLogo);

        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
    }

    private void getDataFromAPI(int giftOrderId) throws UnsupportedEncodingException, JSONException {
        AppController.getGiftDetail(GiftDetailActivity.this, giftOrderId, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                //Log.i("DataResponse", jsonResponse.toString());
                progressLoading.setVisibility(View.GONE);
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
                    GiftModel giftModel = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("gifts").toString(), new TypeReference<GiftModel>() {
                    });

                    scrollContainer.setVisibility(View.VISIBLE);
                    emptyElement.setVisibility(View.GONE);

                    if (giftModel != null) {
                        AppUtils.setImage(imgGiftLogo, giftModel.getGiftImage());

                        txtGiftTitle.setText(Html.fromHtml(giftModel.getTitle()));
                        txtGiftDescription.setText(Html.fromHtml(giftModel.getGiftDesc()));
                        txtGiftOrderDate.setText(getString(R.string.txt_gift_order_date, AppUtils.getFormattedDate(giftModel.getOrderDate())));
                        txtGiftCollectedDate.setText(getString(R.string.txt_gift_collected_date, AppUtils.getFormattedDate(giftModel.getCollectDate())));
                    }
                }
                else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }
                else {
                    emptyElement.setVisibility(View.VISIBLE);
                    scrollContainer.setVisibility(View.GONE);
                    //AppUtils.showToastMessage(GiftDetailActivity.this, getString(R.string.system_error));
                }
            }
        });
    }
}
