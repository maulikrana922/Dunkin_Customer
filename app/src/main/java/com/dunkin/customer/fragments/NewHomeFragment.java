package com.dunkin.customer.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ScanAndWinDialog;
import com.dunkin.customer.models.DashbordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.dunkin.customer.R.id.imgBag;

public class NewHomeFragment extends Fragment {
    private NewHomeActivity homeActivity;
    private TextView txtLandingPageTitle, txtLandingPageDescription;
    private View rootView;
    private LinearLayout llScan;
    private ImageView ivScan, ivWeather;
    private DashbordModel dashbordModel;
    private ImageView inside_image;
    private TextView tvWelcome;
    private String city = "", country = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = (NewHomeActivity) getActivity();
        dashbordModel = homeActivity.dashbordModel;
        homeActivity.latitude = homeActivity.gps.getLatitude();
        homeActivity.longitude = homeActivity.gps.getLongitude();
        getCityNCountry();
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setToolbarView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_new, container, false);
        tvWelcome = (TextView) rootView.findViewById(R.id.tvWelcome);
        llScan = (LinearLayout) rootView.findViewById(R.id.llScan);
        ivScan = (ImageView) rootView.findViewById(R.id.ivScan);
        ivWeather = (ImageView) rootView.findViewById(R.id.ivWeather);

        llScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
//                editor.putBoolean(AppConstants.USER_SCAN_RESULT, false);
//                editor.apply();
                checkScanAndWin1();
            }
        });

        ImageView imgQR = (ImageView) rootView.findViewById(R.id.imgProfileQR);
        AppUtils.setImage(imgQR, AppUtils.getAppPreference(homeActivity).getString(AppConstants.USER_PROFILE_QR, ""));

        txtLandingPageTitle = (TextView) rootView.findViewById(R.id.txtLandingPageTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            txtLandingPageTitle.setAllCaps(true);
        }
        txtLandingPageDescription = (TextView) rootView.findViewById(R.id.txtLandingPageDescription);
        inside_image = (ImageView) rootView.findViewById(R.id.inside_image);

        setData();
        return rootView;
    }

    private void setData() {
        if (dashbordModel.appMessage != null) {
            if (!TextUtils.isEmpty(dashbordModel.appMessage.getAppImage()))
                AppUtils.setImage(inside_image, dashbordModel.appMessage.getAppImage());
            tvWelcome.setText(Html.fromHtml(dashbordModel.appMessage.getAppMessage()));
        }

        if (AppUtils.isNotNull(dashbordModel.landingpageTitle)) {
            txtLandingPageTitle.setText(Html.fromHtml(dashbordModel.landingpageTitle).toString().trim());
        } else {
            txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());
        }
        // Landing Page Description
        if (AppUtils.isNotNull(dashbordModel.landingpageDescription)) {
            txtLandingPageDescription.setText(Html.fromHtml(dashbordModel.landingpageDescription).toString().trim());
        } else {
            txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
        }

        homeActivity.tabAdapter.notifyDataSetChanged();
    }

    private void getCityNCountry() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(homeActivity, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            homeActivity.latitude, homeActivity.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        city = addressList.get(0).getLocality();
                        country = address.getCountryName();
                        getWeatherData();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void getWeatherData() {
        if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(country)) {
            final String weatherUrl = String.format("http://api.openweathermap.org/data/2.5/weather?lat="
                    + homeActivity.latitude + "&lon=" + homeActivity.longitude
                    + "&appid=bc6b2e359ff981aab1494d4166588ea6");
            new WeatherDataAsyncTask().execute(weatherUrl);
        }
    }

    public void checkScanAndWin() {
        try {
            //1
            AppController.getScan(homeActivity, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
//                        dismissProgressDialog();
                    String res = (String) result;
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.getString("success").equalsIgnoreCase("1")) {
//                        HomeActivity.strGetScanImage = jsonResponse.getString("scanwinImage");
//                        HomeActivity.strOfferUrl = jsonResponse.getString("offerImage");
                        NewHomeActivity.isScanWinEnable = jsonResponse.getString("isScanWinEnable");
//                        HomeActivity.isOfferEnable = jsonResponse.getString("isOfferEnable");
//                        if (!AppUtils.getAppPreference(homeActivity).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
//                            if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") )
//                                ScanAndWinDialog.newInstance(homeActivity, HomeActivity.strGetScanImage, false).show();
//                        } else {
                        if (NewHomeActivity.isScanWinEnable.equalsIgnoreCase("1")) {
                            loadingAnimation(dashbordModel.BottomImage);
                        }
//                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkScanAndWin1() {
        try {
            //1
            AppController.getScan(homeActivity, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
//                        dismissProgressDialog();
                    String res = (String) result;
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.getString("success").equalsIgnoreCase("1")) {
//                        HomeActivity.strGetScanImage = jsonResponse.getString("scanwinImage");
//                        HomeActivity.strOfferUrl = jsonResponse.getString("offerImage");
                        NewHomeActivity.isScanWinEnable = jsonResponse.getString("isScanWinEnable");
//                        HomeActivity.isOfferEnable = jsonResponse.getString("isOfferEnable");
//                        if (!AppUtils.getAppPreference(homeActivity).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
                        if (NewHomeActivity.isScanWinEnable.equalsIgnoreCase("1"))
                            ScanAndWinDialog.newInstance(homeActivity, NewHomeActivity.strUrl, false).show();
//                        } else {
//                        if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") ) {
//                            loadingAnimation(dashbordModel.BottomImage);
//                        }
//                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadingAnimation(String str) {

        if (!TextUtils.isEmpty(str)) {

            if (str.contains(".gif")) {
                llScan.setVisibility(View.VISIBLE);
                Glide.with(this).load(str).into(ivScan);
            } else {
                Glide.with(this).load(str).into(ivScan);
//                ivScan.setImageBitmap(BitmapFactory.decodeFile(str));
                if (llScan.getVisibility() != View.VISIBLE) {
                    llScan.setVisibility(View.VISIBLE);

//        TranslateAnimation animation = new TranslateAnimation(0.0f, 200.0f,
//                0.0f, 0.0f);
//        animation.setDuration(2000);
//        animation.setRepeatCount(-1);
//        animation.setRepeatMode(Animation.REVERSE);
//        animation.setFillAfter(true);
//        ivScan.startAnimation(animation);

                    final AnimatorSet loading_first = (AnimatorSet) AnimatorInflater
                            .loadAnimator(homeActivity, R.animator.myanimation_first);
                    final AnimatorSet loading_out_in_1 = (AnimatorSet) AnimatorInflater
                            .loadAnimator(homeActivity, R.animator.myanimation_two);

                    loading_first.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
//                imgBag.setImageResource(R.drawable.bags2_reverse);
//                if (rotationCount < 2) {
                            loading_out_in_1.setTarget(ivScan);
                            loading_out_in_1.start();
                            ivScan.refreshDrawableState();
                            ivScan.invalidate();
//                    rotationCount++;
//                } else {
//                    rotationCount = 0;
//                }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }
                    });

                    loading_out_in_1.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            loading_first.setTarget(ivScan);
                            loading_first.start();
                            ivScan.refreshDrawableState();
                            ivScan.invalidate();

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }
                    });

                    loading_first.setTarget(imgBag);
                    loading_first.start();
                }
            }
        }
    }

    private class WeatherDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(url[0])
                    .build();
            String res = "";
            try {
                Response response = client.newCall(request).execute();
                if (response != null && response.body() != null) {
                    res = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject jsonObj = jsonArray.getJSONObject(0);
                        Glide.with(homeActivity).load(String.format("http://openweathermap.org/img/w/%s.png", jsonObj.getString("icon"))).error(R.drawable.clouds).into(ivWeather);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
