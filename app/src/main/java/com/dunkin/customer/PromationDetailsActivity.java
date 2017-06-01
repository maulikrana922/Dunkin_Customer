package com.dunkin.customer;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.models.PromoModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by qtm-c-android on 1/6/17.
 */

public class PromationDetailsActivity extends BackActivity{
    private TextView txtOfferName, txtOfferDescription, txtPoint, txtUserPoint, txtPurchase;
    private ImageView imgLogo;
    private ScrollView scrollContainer;
    private PromoModel promo_data;
    private ImageView btnAddQty, btnDeletQty;
    private EditText edProQuantity;
    private String user_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_promo_detail, getString(R.string.header_promo));

        promo_data = (PromoModel) getIntent().getSerializableExtra("promo_data");
        user_point = getIntent().getStringExtra("user_point");

        initializeViews();
    }

    private void initializeViews() {
        txtOfferName = (TextView) findViewById(R.id.txtTitle);
        txtOfferDescription = (TextView) findViewById(R.id.txtDesc);
        txtPoint = (TextView) findViewById(R.id.txtPoint);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        scrollContainer = (ScrollView) findViewById(R.id.scrollContainer);
        btnAddQty = (ImageView) findViewById(R.id.btnAddQty);
        btnDeletQty = (ImageView) findViewById(R.id.btnDeletQty);
        edProQuantity = (EditText) findViewById(R.id.edProQuantity);
        txtUserPoint = (TextView) findViewById(R.id.txtUserPoint);
        txtPurchase = (TextView) findViewById(R.id.txtPurchase); 

        txtOfferName.setText(Html.fromHtml(promo_data.getName()));
        txtOfferDescription.setText(Html.fromHtml(promo_data.getDescription()));
        txtPoint.setText(promo_data.getTicketPoint() + " Point");
        AppUtils.setImage(imgLogo, promo_data.getPromoImage());

        txtUserPoint.setText("Current Point : " + user_point);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = df.format(c.getTime());
        Date current_date = null;
        try {
            current_date = df.parse(formattedDate);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        promo_data.setQty(1);
        edProQuantity.setText("" + promo_data.getQty());

        btnAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promo_data.setQty(promo_data.getQty() + 1);
                edProQuantity.setText("" + promo_data.getQty());
            }
        });

        btnDeletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (promo_data.getQty() != 1) {
                    promo_data.setQty(promo_data.getQty() - 1);
                    edProQuantity.setText("" + promo_data.getQty());
                }
            }

        });

        if(current_date!=null) {
            if (current_date.after(promo_data.getStartDate()) && current_date.before(promo_data.getEndDate())) {
                Toast.makeText(getApplicationContext(), "Data Show", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Data Not Show", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void getDataFromAPI(int offerId) throws UnsupportedEncodingException, JSONException {
//        AppController.getOfferDetail(OfferDetailActivity.this, offerId, country_id, new Callback() {
//            @Override
//            public void run(Object result) throws JSONException, IOException {
//                JSONObject jsonResponse = new JSONObject((String) result);
//                //Log.i("DataResponse", jsonResponse.toString());
//                progressLoading.setVisibility(View.GONE);
//                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
//                    of = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("offerDetail").toString(), new TypeReference<OfferModel>() {
//                    });
//
//                    offerProductDetailAdapter = new OfferProductDetailAdapter(OfferDetailActivity.this, of.getOfferproduct(), of.getOfferFree(), of.getCurrency(), true);
//                    lvOfferProductDetails.setAdapter(offerProductDetailAdapter);
//
//                    scrollContainer.setVisibility(View.VISIBLE);
//                    lvOfferProductDetails.setVisibility(View.VISIBLE);
//
//                    try {
//                        AppUtils.setListViewHeightBasedOnChildren(offerProductDetailAdapter, lvOfferProductDetails);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (of != null) {
//                        if (of.getOfferproduct().size() == 0) {
//                            productInfo.setVisibility(View.GONE);
//                        }
//                        AppUtils.setImage(imgLogo, of.getOfferImage());
//                        AppUtils.setImage(imgProductQrCode, of.getOfferqrCode());
//
//                        AppUtils.setImage(imgQrCode, AppUtils.getAppPreference(OfferDetailActivity.this).getString(AppConstants.USER_PROFILE_QR, ""));
//
//                        String offerName = Html.fromHtml(of.getOfferTitle()).toString();
//                        txtOfferName.setText(offerName);
//
//                        txtOfferDescription.setText(Html.fromHtml(of.getOfferDescription()));
//                        txtOfferDescription.setMovementMethod(LinkMovementMethod.getInstance());
//
//                        String offerCode = Html.fromHtml(of.getOfferCode()).toString();
//                        txtOfferCode.setText(offerCode);
//
//                        String offerStartDate = Html.fromHtml(getString(R.string.txt_to, AppUtils.getFormattedDate(of.getOfferStartDate()), AppUtils.getFormattedDate(of.getOfferEndDate()))).toString();
//                        txtStartDate.setText(offerStartDate);
//
//                        /*String offerEndDate = Html.fromHtml("<br/><b>" + getString(R.string.txt_offer_end_date) + " : </b><br/>" + of.getOfferEndDate()).toString();
//                        txtEndDate.setText(offerEndDate);*/
//
//                        String numberOfDays = Html.fromHtml(of.getOfferNoofdaysvalidate()).toString();
//                        txtOfferValidateDays.setText(numberOfDays);
//
//                        String offerItemPerDay = Html.fromHtml(of.getOfferItemperday()).toString();
//                        txtOfferItemPerDays.setText(offerItemPerDay);
//
//                        String redeemTimes = Html.fromHtml(of.getTimesRedeem()).toString();
//                        txtTimesRedeem.setText(redeemTimes);
//
//                        String remainingRedeem = Html.fromHtml(of.getRemainingtimesRedeem()).toString();
//                        txtRemainingTimeRedeem.setText(remainingRedeem);
//
//                        if (Integer.parseInt(of.getTimesRedeem()) == 0) {
//                            Spanned remainingRedeem2 = Html.fromHtml(of.getRemainingtimesRedeem());
//                            txtTotalTimesRedeem.setText(String.format(" %s", remainingRedeem2));
//                        } else {
//                            Spanned totalRedeem = Html.fromHtml(of.getTotalRedeem());
//                            txtTotalTimesRedeem.setText(String.format(" %s", totalRedeem));
//                        }
//
//                        if (of.getOfferFree().equals("Yes")) {
//                            String offerFreeType = Html.fromHtml(getString(R.string.txt_free_type)).toString();
//                            txtOfferFreeType.setText(offerFreeType);
//                        } else {
//                            String offerFreeType = Html.fromHtml(getString(R.string.txt_not_free_type)).toString();
//                            txtOfferFreeType.setText(offerFreeType);
//                        }
//
//                        if (AppUtils.isNotNull(of.getOffer_video_url())) {
//                            imgPlayVideo.setVisibility(View.VISIBLE);
//                        } else {
//                            imgPlayVideo.setVisibility(View.GONE);
//                        }
//                    }
//                } else if (jsonResponse.getInt("success") == 100) {
//                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
//                }else {
//                    scrollContainer.setVisibility(View.GONE);
//                    lvOfferProductDetails.setVisibility(View.GONE);
//                    AppUtils.showToastMessage(OfferDetailActivity.this, jsonResponse.getString("offerDetail"));
//                    //AppUtils.showToastMessage(OfferDetailActivity.this, getString(R.string.system_error));
//                    finish();
//                }
//            }
//        });
//    }
}

