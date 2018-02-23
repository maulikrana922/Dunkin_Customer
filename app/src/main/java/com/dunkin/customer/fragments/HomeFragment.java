package com.dunkin.customer.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dunkin.customer.HomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ImageDialog;
import com.dunkin.customer.dialogs.ScanAndWinDialog;
import com.dunkin.customer.models.DashbordModel;
import com.dunkin.customer.models.HomeCatModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.dunkin.customer.R.id.imgBag;

public class HomeFragment extends Fragment {
    private Context context;
    private TextView txtLandingPageTitle, txtLandingPageDescription;
    private List<HomeCatModel> homeList;
    private View rootView;
    private View v1, v2, v3, v4;
    private LinearLayout llScan;
    private ImageView ivScan;
    private String path = "";
    private int rotationCount;
    DashbordModel dashbordModel=new DashbordModel();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        v1 = rootView.findViewById(R.id.iv1);
        v2 = rootView.findViewById(R.id.iv2);
        v3 = rootView.findViewById(R.id.iv3);
        v4 = rootView.findViewById(R.id.iv4);

        llScan = (LinearLayout) rootView.findViewById(R.id.llScan);
        ivScan = (ImageView) rootView.findViewById(R.id.ivScan);

        llScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
//                editor.putBoolean(AppConstants.USER_SCAN_RESULT, false);
//                editor.apply();
               checkScanAndWin1();
            }
        });

        final View[] viewArray = new View[]{v1, v2, v3, v4};

        ImageView imgQR = (ImageView) rootView.findViewById(R.id.imgProfileQR);
        AppUtils.setImage(imgQR, AppUtils.getAppPreference(context).getString(AppConstants.USER_PROFILE_QR, ""));

        txtLandingPageTitle = (TextView) rootView.findViewById(R.id.txtLandingPageTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            txtLandingPageTitle.setAllCaps(true);
        }
        txtLandingPageDescription = (TextView) rootView.findViewById(R.id.txtLandingPageDescription);
        final ImageView inside_image = (ImageView) rootView.findViewById(R.id.inside_image);

//        try {
//            AppController.getHomePageData(context, new Callback() {
//                @Override
//                public void run(Object result) throws JSONException, IOException {
//
//                    JSONObject jsonResponse = new JSONObject((String) result);
//
//
//                    if (jsonResponse.getInt("success") == 1) {
//                        homeList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("landingpage").toString(), new TypeReference<List<HomeCatModel>>() {
//                        });
//
//
//                        for (int i = 0; i < homeList.size(); i++) {
//                            final HomeCatModel hm = homeList.get(i);
//
//                            final View view = viewArray[i];
//
//                            Log.v("TTT","viewArray[i] = "+hm.getId()+" "+hm.getImage()+" "+hm.getTitle());
//                            view.setTag(hm);
//                            AppUtils.setImage(((ImageView) view.findViewById(R.id.imgContentView)), hm.getImage());
//
//                            ((TextView) view.findViewById(R.id.txtLabel)).setText(hm.getTitle().replaceAll("!", ""));
//
//                            view.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(final View v) {
//                                    Interpolator accelerator = new AccelerateInterpolator();
//                                    Interpolator decelerator = new DecelerateInterpolator();
//
//                                    ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view.findViewById(R.id.imgContentView), "rotationY", 0f, 90f);
//                                    visToInvis.setDuration(200);
//                                    visToInvis.setInterpolator(accelerator);
//
//                                    final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(view.findViewById(R.id.imgContentView), "rotationY", -90f, 0f);
//                                    invisToVis.setDuration(200);
//                                    invisToVis.setInterpolator(decelerator);
//
//                                    visToInvis.addListener(new AnimatorListenerAdapter() {
//                                        @Override
//                                        public void onAnimationEnd(Animator anim) {
//                                            invisToVis.addListener(new AnimatorListenerAdapter() {
//                                                @Override
//                                                public void onAnimationEnd(Animator anim) {
//                                                    HomeCatModel homeContentModel = (HomeCatModel) v.getTag();
//
//                                                    if (homeContentModel.getId() == 1) {
//                                                        ((AppCompatActivity) context).getSupportFragmentManager()
//                                                                .beginTransaction()
//                                                                .replace(R.id.content, new MyWalletFragment())
//                                                                .commitAllowingStateLoss();
//
//                                                    } else if (homeContentModel.getId() == 2) {
//                                                        ((AppCompatActivity) context).getSupportFragmentManager()
//                                                                .beginTransaction()
//                                                                .replace(R.id.content, new RedeemFragment())
//                                                                .commitAllowingStateLoss();
//
//                                                    } else if (homeContentModel.getId() == 3) {
//                                                        ((AppCompatActivity) context).getSupportFragmentManager()
//                                                                .beginTransaction()
//                                                                .replace(R.id.content, new OfferFragment())
//                                                                .commitAllowingStateLoss();
//
//                                                    } else if (homeContentModel.getId() == 4) {
////                                                        ((AppCompatActivity) context).getSupportFragmentManager()
////                                                                .beginTransaction()
////                                                                .replace(R.id.content, new MyProfileFragment())
////                                                                .commitAllowingStateLoss();
//
//                                                        /**
//                                                         * Purvesh
//                                                         *
//                                                         * Check if isOfferEnable or Not
//                                                         *
//                                                         * Open fragment and display first scan image and then display
//                                                         * offer image then open simple scanner activity to scan barcode then open
//                                                         * WinStatusDialog
//                                                         *
//                                                         * All operation are on Image click
//                                                         */
//
////                                                        if (isOfferEnable.equalsIgnoreCase("1"))
//                                                            ((AppCompatActivity) context).getSupportFragmentManager()
//                                                                    .beginTransaction()
//                                                                    .replace(R.id.content, new ScanFragment())
//                                                                    .commitAllowingStateLoss();
//
//                                                        /**
//                                                         * Open Dialog instead of Fragment and perform same operation
//                                                         */
////                                                        if (isOfferEnable.equalsIgnoreCase("1"))
////                                                            ScanAndWinDialog.newInstance((HomeActivity) context, scanOfferImagePath, true).show();
//
//                                                    }
//                                                }
//                                            });
//                                            invisToVis.start();
//                                        }
//                                    });
//                                    visToInvis.start();
//                                }
//                            });
//                        }
//
//
//                        AppUtils.setImage(inside_image, jsonResponse.getString("BackGroundImage"));
//
//
//                        // Landing Page Welcome Title
//                        if (jsonResponse.has("landingpageTitle")) {
//                            if (AppUtils.isNotNull(jsonResponse.getString("landingpageTitle"))) {
//                                txtLandingPageTitle.setText(Html.fromHtml(jsonResponse.getString("landingpageTitle")).toString().trim());
//                            } else {
//                                txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());
//                            }
//
//
//                        } else {
//                            txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());
//
//                        }
//
//                        // Landing Page Description
//                        if (jsonResponse.has("landingpageDescription")) {
//
//                            if (AppUtils.isNotNull(jsonResponse.getString("landingpageDescription"))) {
//                                txtLandingPageDescription.setText(Html.fromHtml(jsonResponse.getString("landingpageDescription")).toString().trim());
//                            } else {
//                                txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
//                            }
//                        } else {
//                            txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
//                        }
//                    } else if (jsonResponse.getInt("success") == 100) {
//
//                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
//                    }
//
//                }
//
//
//            });
//
//
//        } catch (JSONException | UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        try {
//            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            String version = String.valueOf(pInfo.versionCode);
//            fetchAllSetting(version);
//        } catch (UnsupportedEncodingException | JSONException | ParseException e) {
//            e.printStackTrace();
//
//        } catch(Exception e)
//        {
//            e.printStackTrace();
//        }


        try {
            //all
            JSONObject jsonRequest = new JSONObject();
            String version = "";
            PackageInfo pInfo = null;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                version = String.valueOf(pInfo.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            jsonRequest.put("email", AppUtils.getAppPreference(context)
                    .getString(AppConstants.USER_EMAIL_ADDRESS, ""));

            jsonRequest.put("country_id", AppUtils.getAppPreference(context)
                    .getInt(AppConstants.USER_COUNTRY, -1));
            jsonRequest.put("type", "2");
            jsonRequest.put("version", version);
            jsonRequest.put("settingType", "1");

            try {
                AppController.getDashbordPageData(context, jsonRequest, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {

                        JSONObject jsonResponse = new JSONObject((String) result);

                        if (jsonResponse.getInt("success") == 1) {
                            dashbordModel=new DashbordModel();
                            try {
                                dashbordModel.landingpage=new ArrayList<>();
                                dashbordModel.landingpage = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("landingpage").toString(), new TypeReference<List<HomeCatModel>>() {
                                });
                                homeList=new ArrayList<>();
                                homeList.addAll(dashbordModel.landingpage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                dashbordModel.BottomImage = jsonResponse.getString("BottomImage").toString();
                                dashbordModel.BackGroundImage= jsonResponse.getString("BackGroundImage").toString();
                                dashbordModel.landingpageTitle= jsonResponse.getString("landingpageTitle").toString();
                                dashbordModel.landingpageDescription= jsonResponse.getString("landingpageDescription").toString();
                                dashbordModel.isScanWinEnable= jsonResponse.getString("isScanWinEnable").toString();
                                dashbordModel.isOfferEnable= jsonResponse.getString("isOfferEnable").toString();
                                dashbordModel.scanwinImage= jsonResponse.getString("scanwinImage").toString();
                                dashbordModel.offerImage= jsonResponse.getString("offerImage").toString();

                                HomeActivity.strUrl = dashbordModel.scanwinImage;
                                HomeActivity.strOfferUrl = dashbordModel.offerImage;
                                HomeActivity.isScanWinEnable = dashbordModel.isScanWinEnable;
                                HomeActivity.isOfferEnable = dashbordModel.isOfferEnable;
//                                ((HomeActivity)context).fileDownload();

                                ((HomeActivity)context).dbAdapter.open();
                                ((HomeActivity)context).dbAdapter.addOfflineData(AppConstants.OF_PROFILE,  jsonResponse.getString("userDetail").toString());
                                ((HomeActivity)context).dbAdapter.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") ) {
                                    //all setting api
                                    JSONObject jsonObject = jsonResponse.getJSONObject("updateApp");
                                    String image_url = jsonObject.getString("Image");

                                    if (!AppUtils.getAppPreference(context).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
                                        if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1"))
                                            ImageDialog.newInstance(context, HomeActivity.strUrl, false).show();

                                    }
                                    else
                                    {
                                        if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") ) {
                                            loadingAnimation(dashbordModel.BottomImage);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < homeList.size(); i++) {
                                final HomeCatModel hm = homeList.get(i);

                                final View view = viewArray[i];

                                Log.v("TTT","viewArray[i] = "+hm.getId()+" "+hm.getImage()+" "+hm.getTitle());
                                view.setTag(hm);
                                AppUtils.setImage(((ImageView) view.findViewById(R.id.imgContentView)), hm.getImage());

                                ((TextView) view.findViewById(R.id.txtLabel)).setText(hm.getTitle().replaceAll("!", ""));

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        Interpolator accelerator = new AccelerateInterpolator();
                                        Interpolator decelerator = new DecelerateInterpolator();

                                        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view.findViewById(R.id.imgContentView), "rotationY", 0f, 90f);
                                        visToInvis.setDuration(200);
                                        visToInvis.setInterpolator(accelerator);

                                        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(view.findViewById(R.id.imgContentView), "rotationY", -90f, 0f);
                                        invisToVis.setDuration(200);
                                        invisToVis.setInterpolator(decelerator);

                                        visToInvis.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator anim) {
                                                invisToVis.addListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator anim) {
                                                        HomeCatModel homeContentModel = (HomeCatModel) v.getTag();

                                                        if (homeContentModel.getId() == 1) {
                                                            ((AppCompatActivity) context).getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.content, new MyWalletFragment())
                                                                    .commitAllowingStateLoss();

                                                        } else if (homeContentModel.getId() == 2) {
                                                            ((AppCompatActivity) context).getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.content, new RedeemFragment())
                                                                    .commitAllowingStateLoss();

                                                        } else if (homeContentModel.getId() == 3) {
                                                            ((AppCompatActivity) context).getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.content, new OfferFragment())
                                                                    .commitAllowingStateLoss();

                                                        } else if (homeContentModel.getId() == 4) {
//                                                        ((AppCompatActivity) context).getSupportFragmentManager()
//                                                                .beginTransaction()
//                                                                .replace(R.id.content, new MyProfileFragment())
//                                                                .commitAllowingStateLoss();

                                                            /**
                                                             * Purvesh
                                                             *
                                                             * Check if isOfferEnable or Not
                                                             *
                                                             * Open fragment and display first scan image and then display
                                                             * offer image then open simple scanner activity to scan barcode then open
                                                             * WinStatusDialog
                                                             *
                                                             * All operation are on Image click
                                                             */

//                                                        if (isOfferEnable.equalsIgnoreCase("1"))
                                                            ((AppCompatActivity) context).getSupportFragmentManager()
                                                                    .beginTransaction()
                                                                    .replace(R.id.content, new ScanFragment())
                                                                    .commitAllowingStateLoss();

                                                            /**
                                                             * Open Dialog instead of Fragment and perform same operation
                                                             */
//                                                        if (isOfferEnable.equalsIgnoreCase("1"))
//                                                            ScanAndWinDialog.newInstance((HomeActivity) context, scanOfferImagePath, true).show();

                                                        }
                                                    }
                                                });
                                                invisToVis.start();
                                            }
                                        });
                                        visToInvis.start();
                                    }
                                });
                            }


                            AppUtils.setImage(inside_image, dashbordModel.BackGroundImage);




                            // Landing Page Welcome Title
                            if (jsonResponse.has("landingpageTitle")) {
                                if (AppUtils.isNotNull(dashbordModel.landingpageTitle)) {
                                    txtLandingPageTitle.setText(Html.fromHtml(dashbordModel.landingpageTitle).toString().trim());
                                } else {
                                    txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());
                                }


                            } else {
                                txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());

                            }

                            // Landing Page Description
                            if (jsonResponse.has("landingpageDescription")) {

                                if (AppUtils.isNotNull(dashbordModel.landingpageDescription)) {
                                    txtLandingPageDescription.setText(Html.fromHtml(dashbordModel.landingpageDescription).toString().trim());
                                } else {
                                    txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
                                }
                            } else {
                                txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
                            }
                        } else if (jsonResponse.getInt("success") == 100) {

                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }

                    }

                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void checkScanAndWin() {
        try {
            //1
            AppController.getScan(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
//                        dismissProgressDialog();
                    String res = (String) result;
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.getString("success").equalsIgnoreCase("1")) {
//                        HomeActivity.strGetScanImage = jsonResponse.getString("scanwinImage");
//                        HomeActivity.strOfferUrl = jsonResponse.getString("offerImage");
                        HomeActivity.isScanWinEnable = jsonResponse.getString("isScanWinEnable");
//                        HomeActivity.isOfferEnable = jsonResponse.getString("isOfferEnable");
//                        if (!AppUtils.getAppPreference(context).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
//                            if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") )
//                                ScanAndWinDialog.newInstance(context, HomeActivity.strGetScanImage, false).show();
//                        } else {
                            if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") ) {
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
            AppController.getScan(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
//                        dismissProgressDialog();
                    String res = (String) result;
                    JSONObject jsonResponse = new JSONObject(res);
                    if (jsonResponse.getString("success").equalsIgnoreCase("1")) {
//                        HomeActivity.strGetScanImage = jsonResponse.getString("scanwinImage");
//                        HomeActivity.strOfferUrl = jsonResponse.getString("offerImage");
                        HomeActivity.isScanWinEnable = jsonResponse.getString("isScanWinEnable");
//                        HomeActivity.isOfferEnable = jsonResponse.getString("isOfferEnable");
//                        if (!AppUtils.getAppPreference(context).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
                            if (HomeActivity.isScanWinEnable.equalsIgnoreCase("1") )
                                ScanAndWinDialog.newInstance(context, HomeActivity.strUrl, false).show();
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

        if(!TextUtils.isEmpty(str))
        {

            if(str.contains(".gif"))
            {
                llScan.setVisibility(View.VISIBLE);
                Glide.with(this).load(str).into(ivScan);
            }
            else
            {
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
                            .loadAnimator(context, R.animator.myanimation_first);
                    final AnimatorSet loading_out_in_1 = (AnimatorSet) AnimatorInflater
                            .loadAnimator(context, R.animator.myanimation_two);

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

//    private void fetchAllSetting(String version) throws UnsupportedEncodingException, JSONException, ParseException {
//
//        JSONObject jsonRequest = new JSONObject();
//
//        jsonRequest.put("email", AppUtils.getAppPreference(context)
//                .getString(AppConstants.USER_EMAIL_ADDRESS, ""));
//        jsonRequest.put("country_id", AppUtils.getAppPreference(context)
//                .getInt(AppConstants.USER_COUNTRY, -1));
//        jsonRequest.put("type", "2");
//        jsonRequest.put("version", version);
//        AppController.fetchAllSetting(context, jsonRequest.toString(), new Callback() {
//            @Override
//            public void run(Object result) throws JSONException, IOException {
//                try {
//                    JSONObject jsonResponse = new JSONObject((String) result);
//                    Dunkin_Log.e("response_setting", "response : " + jsonResponse);
//
//                    if (jsonResponse.getString("success").equals("1")) {
//                        JSONObject jsonObject = jsonResponse.getJSONObject("data");
//                        String image_url = jsonObject.getString("Image");
//                        ImageDialog.newInstance(context, image_url, false).show();
//                    } else {
//                        ((HomeActivity) context).checkScanAndWin();
//                    }
//
////                    } else if (jsonResponse.getString("success").equals("0")) {
////                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
////                    } else {
////                        AppUtils.showToastMessage(context, getString(R.string.system_error));
////                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
////                    AppUtils.showToastMessage(context, getString(R.string.system_error));
//                }
//            }
//        });
//    }
}


//try {
//        //all
//        JSONObject jsonRequest = new JSONObject();
//        String version="";
//        PackageInfo pInfo = null;
//        try {
//        pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//        version = String.valueOf(pInfo.versionCode);
//        } catch (PackageManager.NameNotFoundException e) {
//        e.printStackTrace();
//        }
//
//
//
//        jsonRequest.put("email", AppUtils.getAppPreference(context)
//        .getString(AppConstants.USER_EMAIL_ADDRESS, ""));
//
//        jsonRequest.put("country_id", AppUtils.getAppPreference(context)
//        .getInt(AppConstants.USER_COUNTRY, -1));
//        jsonRequest.put("type", "2");
//        jsonRequest.put("version", version);
//
//        try {
//        AppController.getDashbordPageData(context,jsonRequest, new Callback() {
//@Override
//public void run(Object result) throws JSONException, IOException {
//
//        JSONObject jsonResponse = new JSONObject((String) result);
//
//        if (jsonResponse.getInt("success") == 1) {
//
//        try {
//        homeList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("landingpage").toString(), new TypeReference<List<HomeCatModel>>() {
//        });
//        }
//        catch (Exception e)
//        {
//        e.printStackTrace();
//        }
//
//        try {
//        //all setting api
//        JSONObject jsonObject = jsonResponse.getJSONObject("updateApp");
//        String image_url = jsonObject.getString("Image");
//        ImageDialog.newInstance(context, image_url, false).show();
//        }
//        catch (Exception e)
//        {
//        e.printStackTrace();
//        }
//
//        for (int i = 0; i < homeList.size(); i++) {
//final HomeCatModel hm = homeList.get(i);
//
//final View view = viewArray[i];
//
//        Log.v("TTT", "viewArray[i] = " + hm.getId() + " " + hm.getImage() + " " + hm.getTitle());
//        view.setTag(hm);
//        AppUtils.setImage(((ImageView) view.findViewById(R.id.imgContentView)), hm.getImage());
//
//
//        ((TextView) view.findViewById(R.id.txtLabel)).setText(hm.getTitle().replaceAll("!", ""));
//
//
//        view.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(final View v) {
//        Interpolator accelerator = new AccelerateInterpolator();
//        Interpolator decelerator = new DecelerateInterpolator();
//
//        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(view.findViewById(R.id.imgContentView), "rotationY", 0f, 90f);
//        visToInvis.setDuration(200);
//        visToInvis.setInterpolator(accelerator);
//
//final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(view.findViewById(R.id.imgContentView), "rotationY", -90f, 0f);
//        invisToVis.setDuration(200);
//        invisToVis.setInterpolator(decelerator);
//
//        visToInvis.addListener(new AnimatorListenerAdapter() {
//@Override
//public void onAnimationEnd(Animator anim) {
//        invisToVis.addListener(new AnimatorListenerAdapter() {
//@Override
//public void onAnimationEnd(Animator anim) {
//        HomeCatModel homeContentModel = (HomeCatModel) v.getTag();
//
//        if (homeContentModel.getId() == 1) {
//        ((AppCompatActivity) context).getSupportFragmentManager()
//        .beginTransaction()
//        .replace(R.id.content, new MyWalletFragment())
//        .commitAllowingStateLoss();
//
//        } else if (homeContentModel.getId() == 2) {
//        ((AppCompatActivity) context).getSupportFragmentManager()
//        .beginTransaction()
//        .replace(R.id.content, new RedeemFragment())
//        .commitAllowingStateLoss();
//
//        } else if (homeContentModel.getId() == 3) {
//        ((AppCompatActivity) context).getSupportFragmentManager()
//        .beginTransaction()
//        .replace(R.id.content, new OfferFragment())
//        .commitAllowingStateLoss();
//
//        } else if (homeContentModel.getId() == 4) {
//        //                                                        ((AppCompatActivity) context).getSupportFragmentManager()
//        //                                                                .beginTransaction()
//        //                                                                .replace(R.id.content, new MyProfileFragment())
//        //                                                                .commitAllowingStateLoss();
//
//        /**
//         * Purvesh
//         *
//         * Check if isOfferEnable or Not
//         *
//         * Open fragment and display first scan image and then display
//         * offer image then open simple scanner activity to scan barcode then open
//         * WinStatusDialog
//         *
//         * All operation are on Image click
//         */
//
//        //                                                        if (isOfferEnable.equalsIgnoreCase("1"))
//        ((AppCompatActivity) context).getSupportFragmentManager()
//        .beginTransaction()
//        .replace(R.id.content, new ScanFragment())
//        .commitAllowingStateLoss();
//
//        /**
//         * Open Dialog instead of Fragment and perform same operation
//         */
//        //                                                        if (isOfferEnable.equalsIgnoreCase("1"))
//        //                                                            ScanAndWinDialog.newInstance((HomeActivity) context, scanOfferImagePath, true).show();
//
//        }
//        }
//        });
//        invisToVis.start();
//        }
//        });
//        visToInvis.start();
//        }
//        });
//        }
//
//
//        AppUtils.setImage(inside_image, jsonResponse.getString("BackGroundImage"));
//
//
//        // Landing Page Welcome Title
//        if (jsonResponse.has("landingpageTitle")) {
//        if (AppUtils.isNotNull(jsonResponse.getString("landingpageTitle"))) {
//        txtLandingPageTitle.setText(Html.fromHtml(jsonResponse.getString("landingpageTitle")).toString().trim());
//        } else {
//        txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());
//        }
//
//
//        } else {
//        txtLandingPageTitle.setText(getString(R.string.home_welcome_text).trim());
//
//        }
//
//        // Landing Page Description
//        if (jsonResponse.has("landingpageDescription")) {
//
//        if (AppUtils.isNotNull(jsonResponse.getString("landingpageDescription"))) {
//        txtLandingPageDescription.setText(Html.fromHtml(jsonResponse.getString("landingpageDescription")).toString().trim());
//        } else {
//        txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
//        }
//        } else {
//        txtLandingPageDescription.setText("Pamper yourself in a full table service setting with premium cuisine. Savor our food and vibrant surroundings while enjoying your friends and family.".trim());
//        }
//        } else if (jsonResponse.getInt("success") == 100) {
//
//        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
//        }
//
//        }
//
//        });
//        } catch (UnsupportedEncodingException e) {
//        e.printStackTrace();
//        }
//        } catch (JSONException e) {
//        e.printStackTrace();
//        }
