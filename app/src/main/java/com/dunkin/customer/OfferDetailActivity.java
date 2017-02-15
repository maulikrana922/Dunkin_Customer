package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.OfferProductDetailAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.OfferModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class OfferDetailActivity extends BackActivity implements View.OnClickListener {
    private TextView txtOfferName, txtOfferDescription, txtStartDate, txtOfferCode,
            txtOfferValidateDays, txtOfferItemPerDays, txtOfferFreeType, productInfo,
            txtTimesRedeem, txtRemainingTimeRedeem, txtTotalTimesRedeem;
    private ImageView imgLogo, imgProductQrCode, imgQrCode, imgPlayVideo;
    private ListView lvOfferProductDetails;
    private OfferProductDetailAdapter offerProductDetailAdapter;
    private OfferModel of;
    private int country_id;
    private ScrollView scrollContainer;
    private ProgressBar progressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_offer_detail, getString(R.string.nav_offer2));

        int offerId = Integer.parseInt(getIntent().getStringExtra("offerId"));
        if (getIntent().hasExtra("country_id"))
            country_id = getIntent().getIntExtra("country_id", -1);
        else
            country_id = AppUtils.getAppPreference(OfferDetailActivity.this).getInt(AppConstants.USER_COUNTRY, -1);

        initializeViews();

        try {
            getDataFromAPI(offerId);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        txtOfferName = (TextView) findViewById(R.id.txtTitle);
        txtOfferDescription = (TextView) findViewById(R.id.txtOfferDescription);
        //   txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        productInfo = (TextView) findViewById(R.id.productInfo);
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtOfferCode = (TextView) findViewById(R.id.txtOfferCode);
        txtOfferValidateDays = (TextView) findViewById(R.id.txtOfferValidateDays);
        txtOfferItemPerDays = (TextView) findViewById(R.id.txtOfferItemPerDays);
        txtOfferFreeType = (TextView) findViewById(R.id.txtOfferFreeType);
        txtTimesRedeem = (TextView) findViewById(R.id.txtTimesRedeem);
        txtRemainingTimeRedeem = (TextView) findViewById(R.id.txtRemainingTimeRedeem);
        txtTotalTimesRedeem = (TextView) findViewById(R.id.txtTotalTimesRedeem);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        imgPlayVideo = (ImageView) findViewById(R.id.imgPlayVideo);
        imgProductQrCode = (ImageView) findViewById(R.id.imgProductQrCode);

        imgQrCode = (ImageView) findViewById(R.id.imgQrCode);
        imgPlayVideo.setOnClickListener(this);

        lvOfferProductDetails = (ListView) findViewById(R.id.lvOfferProductsDetail);
        /*lvOfferProductDetails.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                (v.getParent()).getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/

        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
    }

    private void getDataFromAPI(int offerId) throws UnsupportedEncodingException, JSONException {
        AppController.getOfferDetail(OfferDetailActivity.this, offerId, country_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                //Log.i("DataResponse", jsonResponse.toString());
                progressLoading.setVisibility(View.GONE);
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
                    of = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("offerDetail").toString(), new TypeReference<OfferModel>() {
                    });

                    offerProductDetailAdapter = new OfferProductDetailAdapter(OfferDetailActivity.this, of.getOfferproduct(), of.getOfferFree(), of.getCurrency(), true);
                    lvOfferProductDetails.setAdapter(offerProductDetailAdapter);

                    scrollContainer.setVisibility(View.VISIBLE);
                    lvOfferProductDetails.setVisibility(View.VISIBLE);

                    try {
                        AppUtils.setListViewHeightBasedOnChildren(offerProductDetailAdapter, lvOfferProductDetails);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (of != null) {
                        if (of.getOfferproduct().size() == 0) {
                            productInfo.setVisibility(View.GONE);
                        }
                        AppUtils.setImage(imgLogo, of.getOfferImage());
                        AppUtils.setImage(imgProductQrCode, of.getOfferqrCode());

                        AppUtils.setImage(imgQrCode, AppUtils.getAppPreference(OfferDetailActivity.this).getString(AppConstants.USER_PROFILE_QR, ""));

                        String offerName = Html.fromHtml(of.getOfferTitle()).toString();
                        txtOfferName.setText(offerName);

                        txtOfferDescription.setText(Html.fromHtml(of.getOfferDescription()));
                        txtOfferDescription.setMovementMethod(LinkMovementMethod.getInstance());

                        String offerCode = Html.fromHtml(of.getOfferCode()).toString();
                        txtOfferCode.setText(offerCode);

                        String offerStartDate = Html.fromHtml(getString(R.string.txt_to, AppUtils.getFormattedDate(of.getOfferStartDate()), AppUtils.getFormattedDate(of.getOfferEndDate()))).toString();
                        txtStartDate.setText(offerStartDate);

                        /*String offerEndDate = Html.fromHtml("<br/><b>" + getString(R.string.txt_offer_end_date) + " : </b><br/>" + of.getOfferEndDate()).toString();
                        txtEndDate.setText(offerEndDate);*/

                        String numberOfDays = Html.fromHtml(of.getOfferNoofdaysvalidate()).toString();
                        txtOfferValidateDays.setText(numberOfDays);

                        String offerItemPerDay = Html.fromHtml(of.getOfferItemperday()).toString();
                        txtOfferItemPerDays.setText(offerItemPerDay);

                        String redeemTimes = Html.fromHtml(of.getTimesRedeem()).toString();
                        txtTimesRedeem.setText(redeemTimes);

                        String remainingRedeem = Html.fromHtml(of.getRemainingtimesRedeem()).toString();
                        txtRemainingTimeRedeem.setText(remainingRedeem);

                        if (Integer.parseInt(of.getTimesRedeem()) == 0) {
                            Spanned remainingRedeem2 = Html.fromHtml(of.getRemainingtimesRedeem());
                            txtTotalTimesRedeem.setText(String.format(" %s", remainingRedeem2));
                        } else {
                            Spanned totalRedeem = Html.fromHtml(of.getTotalRedeem());
                            txtTotalTimesRedeem.setText(String.format(" %s", totalRedeem));
                        }

                        if (of.getOfferFree().equals("Yes")) {
                            String offerFreeType = Html.fromHtml(getString(R.string.txt_free_type)).toString();
                            txtOfferFreeType.setText(offerFreeType);
                        } else {
                            String offerFreeType = Html.fromHtml(getString(R.string.txt_not_free_type)).toString();
                            txtOfferFreeType.setText(offerFreeType);
                        }

                        if (AppUtils.isNotNull(of.getOffer_video_url())) {
                            imgPlayVideo.setVisibility(View.VISIBLE);
                        } else {
                            imgPlayVideo.setVisibility(View.GONE);
                        }
                    }
                } else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }else {
                    scrollContainer.setVisibility(View.GONE);
                    lvOfferProductDetails.setVisibility(View.GONE);
                    AppUtils.showToastMessage(OfferDetailActivity.this, jsonResponse.getString("offerDetail"));
                    //AppUtils.showToastMessage(OfferDetailActivity.this, getString(R.string.system_error));
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgPlayVideo) {
            if (AppUtils.isNotNull(of.getOffer_video_url()))
                startActivity(new Intent(OfferDetailActivity.this, AppWebViewActivity.class).putExtra("url", of.getOffer_video_url()));
        }
    }
}
