package com.dunkin.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.Utils.GPSTracker;
import com.dunkin.customer.adapters.TabAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ImageDialog;
import com.dunkin.customer.dialogs.ScanAndWinDialog;
import com.dunkin.customer.dialogs.WinStatusDialog;
import com.dunkin.customer.fragments.FeedbackFragment;
import com.dunkin.customer.fragments.MoreFragment;
import com.dunkin.customer.fragments.MyWalletFragment;
import com.dunkin.customer.fragments.NewHomeFragment;
import com.dunkin.customer.fragments.NotificationFragment;
import com.dunkin.customer.fragments.OfferFragment;
import com.dunkin.customer.fragments.RedeemFragment;
import com.dunkin.customer.fragments.RotatingBannerFragment;
import com.dunkin.customer.listener.FileDownloadListener;
import com.dunkin.customer.listener.OnGetTabTextColor;
import com.dunkin.customer.listener.OnTabClick;
import com.dunkin.customer.models.DashbordModel;
import com.dunkin.customer.models.HomeCatModel;
import com.facebook.CallbackManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NewHomeActivity extends AppCompatActivity implements OnTabClick, View.OnClickListener {
    private static final int SCANNER_REQUEST_CODE = 0x11;
    private static final int SCANNER_PROMOTION_REQUEST_CODE = 0x111;
    private static final int PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE = 0x21;
    private static final int CAMERA_PERMISSION_REQUEST = 0x31;
    private static final int REQUEST_LOCATION_SETTINGS = 101;
    private static final int REQUEST_LOCATION = 102;
    public static String strUrl = "", strOfferUrl = "", strGetScanImage = "", scanOfferImagePath = "",
            isScanWinEnable = "", isOfferEnable = "";
    public static String TempPath = "Dunkin Leb" + File.separator + "Temp" + File.separator;
    private static Context mContext;
    final String[] permsReadWrite = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    public DashbordModel dashbordModel = new DashbordModel();
    public NewHomeFragment newHomeFragment = new NewHomeFragment();
    public MyWalletFragment myWalletFragment = new MyWalletFragment();
    public RedeemFragment redeemFragment = new RedeemFragment();
    public OfferFragment offerFragment = new OfferFragment();
    public MoreFragment moreFragment = new MoreFragment();
    public DBAdapter dbAdapter;
    public boolean isFromGCM = false;
    public int msgType;
    public ImageView ivDone, ivClose;
    public GPSTracker gps;
    public double latitude, longitude;
    public TabAdapter tabAdapter;
    CallbackManager callbackManager;
    private ImageView ivWeather;
    private List<HomeCatModel> homeList = new ArrayList<>();
    private GridView rvTabs;
    private OnGetTabTextColor onGetTabTextColor;
    private String lat, log;
    private LinearLayout llTabs;
    private Toolbar toolbar;
    private ImageView ivBack;
    private String res;
    private File mFileTemp;
    private Animation animHyperSpace, animRotate, animZoomIn, animZoomOut, animFlip,
            animFadeOut;
    private Boolean isForeground;

    public static Context getCustomContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        initUI();

        gps = new GPSTracker(NewHomeActivity.this);

        animHyperSpace = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.hyperspace);

        animRotate = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.rotate);

        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_in);

        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoom_out);

        animFlip = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.flip);

        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);


    }

    private void initUI() {

        dbAdapter = new DBAdapter(this);
        mContext = NewHomeActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
        ivDone = (ImageView) findViewById(R.id.ivDone);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (getIntent().hasExtra("FromGCM")) {
            isFromGCM = getIntent().getBooleanExtra("FromGCM", false);
        } else {
            isFromGCM = false;
        }

        if (getIntent().hasExtra("isForeground")) {
            isForeground = getIntent().getBooleanExtra("isForeground", true);
        } else {
            isForeground = getIntent().getBooleanExtra("isForeground", false);
        }

        if (getIntent().hasExtra("MsgType")) {
            msgType = getIntent().getIntExtra("MsgType", 0);
        }

        callbackManager = CallbackManager.Factory.create();
        llTabs = (LinearLayout) findViewById(R.id.llTabs);

        ivWeather = (ImageView) findViewById(R.id.ivWeather);
        rvTabs = (GridView) findViewById(R.id.rvTabs);
//        rvTabs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tabAdapter = new TabAdapter(homeList, this, this);
        rvTabs.setAdapter(tabAdapter);
        saveUserData();
        getHomeData();

        rvTabs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                homeList.get(position).setSelect(!homeList.get(position).isSelect());
                for (int i = 0; i < homeList.size(); i++) {
                    if (position != i) {
                        homeList.get(i).setSelect(false);
                    }
                }
                if (position == 0) {
                    view.startAnimation(animHyperSpace);
                    if (!newHomeFragment.isVisible()) {
                        newHomeFragment = new NewHomeFragment();
                        addFragment(newHomeFragment, homeList.get(position).getTitle());
                    }
                    animHyperSpace.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }
                    });
                }
                if (position == 1) {
                    view.startAnimation(animZoomIn);
                    if (!redeemFragment.isVisible()) {
                        redeemFragment = new RedeemFragment();
                        addFragment(redeemFragment, homeList.get(position).getTitle());
                    }
                    animZoomIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.clearAnimation();
                            view.startAnimation(animZoomOut);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            view.clearAnimation();
                        }
                    });
                    animZoomOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }
                    });
                }
                if (position == 2) {
                    view.startAnimation(animRotate);
                    if (!offerFragment.isVisible()) {
                        offerFragment = new OfferFragment();
                        addFragment(offerFragment, homeList.get(position).getTitle());
                    }
                    animRotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }
                    });
                }
                if (position == 3) {
                    view.startAnimation(animFlip);
                    if (!myWalletFragment.isVisible()) {
                        myWalletFragment = new MyWalletFragment();
                        addFragment(myWalletFragment, homeList.get(position).getTitle());
                    }
                    animFlip.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }
                    });
                }
                if (position == 4) {
                    if (!moreFragment.isVisible()) {
                        addFragment(moreFragment, homeList.get(position).getTitle());
                    }
                    view.startAnimation(animFadeOut);
                    animFadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            view.clearAnimation();
                            tabAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    public void setOnGetTabTextColor(OnGetTabTextColor onGetTabTextColor) {
        this.onGetTabTextColor = onGetTabTextColor;
    }

    private void saveUserData() {
        try {
            //2
            AppController.getUserProfile(NewHomeActivity.this, AppUtils.getAppPreference(NewHomeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""), true
                    , new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 1) {
                                dbAdapter.open();
                                dbAdapter.addOfflineData(AppConstants.OF_PROFILE, (String) result);
                                dbAdapter.close();
                            } else if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                            } else {
                                AppUtils.showToastMessage(NewHomeActivity.this, getString(R.string.system_error));
                            }
                            Dunkin_Log.e("res", (String) result);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBanner() {
        try {

            final FrameLayout advertisement_banner = (FrameLayout) findViewById(R.id.advertisement_banner);
            if (advertisement_banner != null) {
                advertisement_banner.setVisibility(View.GONE);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.advertisement_banner, RotatingBannerFragment.newInstance()).commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getHomeData() {
        JSONObject jsonRequest = new JSONObject();
        String version = "";
        PackageInfo pInfo;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            version = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            jsonRequest.put("email", AppUtils.getAppPreference(mContext)
                    .getString(AppConstants.USER_EMAIL_ADDRESS, ""));

            jsonRequest.put("country_id", AppUtils.getAppPreference(mContext)
                    .getInt(AppConstants.USER_COUNTRY, -1));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(mContext)
                    .getString(AppConstants.USER_LANGUAGE, "en"));
            jsonRequest.put("loginUser", "1");
            jsonRequest.put("settingType", "2");

            AppController.getDashbordPageData(mContext, jsonRequest, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    JSONObject jsonResponse = new JSONObject((String) result);

                    if (jsonResponse.getInt("success") == 1) {
                        dashbordModel = new Gson().fromJson(jsonResponse.toString(), DashbordModel.class);
                        if (onGetTabTextColor != null) {
                            onGetTabTextColor.onGetTabTextColor(dashbordModel.tabColor, dashbordModel.tabHighlightColor);
                        }

                        dbAdapter.open();
                        dbAdapter.addOfflineData(AppConstants.OF_PROFILE, new Gson().toJson(dashbordModel.userDetail));
                        dbAdapter.close();

                        homeList.addAll(dashbordModel.landingpage);
                        if (isFromGCM) {
                            int isNotMsgType = 0;
                            if (getIntent().hasExtra("isNotMsgType")) {
                                isNotMsgType = getIntent().getIntExtra("isNotMsgType", 0);
                            }
                            if (isNotMsgType == 1) {
                                if (isForeground) {//if app is foreground and msgType is null
                                    homeList.get(4).setSelect(true);
                                    addFragment(moreFragment, homeList.get(4).getTitle());
                                    addFragment(new NotificationFragment(), "Notification");
                                } else {////if app is not in foreground and msgType is null
                                    homeList.get(0).setSelect(true);
                                    addFragment(newHomeFragment, homeList.get(0).getTitle());
                                    addFragment(new NotificationFragment(), "Notification");
                                }
                            } else {//if app msgType is not null
                                if (msgType == 8 || msgType == 14) {// for giftappiness
                                    homeList.get(3).setSelect(true);
                                    addFragment(myWalletFragment, homeList.get(3).getTitle());
                                } else if (msgType == AppConstants.MENU_FEEDBACK) {
                                    homeList.get(4).setSelect(true);
                                    addFragment(moreFragment, homeList.get(4).getTitle());
                                    addFragment(new FeedbackFragment(), "FeedBack");
                                } else if (msgType == 12) {
                                    homeList.get(4).setSelect(true);
                                    addFragment(moreFragment, homeList.get(4).getTitle());
                                    addFragment(new NotificationFragment(), "Notification");
                                } else {
                                    homeList.get(4).setSelect(true);
                                    addFragment(moreFragment, homeList.get(4).getTitle());
                                }
                            }
                        } else {
                            homeList.get(0).setSelect(true);
                            addFragment(newHomeFragment, homeList.get(0).getTitle());
                        }
                        tabAdapter.notifyDataSetChanged();
//                        addFragment(newHomeFragment, homeList.get(0).getTitle());

                        NewHomeActivity.strUrl = dashbordModel.scanwinImage;
                        NewHomeActivity.strOfferUrl = dashbordModel.offerImage;
                        NewHomeActivity.isScanWinEnable = dashbordModel.isScanWinEnable;
                        NewHomeActivity.isOfferEnable = dashbordModel.isOfferEnable;
//                                ((HomeActivity1)context).fileDownload();

                        if (NewHomeActivity.isScanWinEnable.equalsIgnoreCase("1")) {
                            //all setting api

                            if (!AppUtils.getAppPreference(mContext).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
                                if (NewHomeActivity.isScanWinEnable.equalsIgnoreCase("1"))
                                    ImageDialog.newInstance(mContext, NewHomeActivity.strUrl, false).show();

                            }
                        }
                    } else if (jsonResponse.getInt("success") == 99) {
                        AppUtils.showToastMessage(getApplicationContext(), getString(R.string.system_error));
                        SharedPreferences.Editor editor = AppUtils.getAppPreference(NewHomeActivity.this).edit();
                        String gcm = AppUtils.getAppPreference(NewHomeActivity.this).getString(AppConstants.GCM_TOKEN_ID, "");
                        editor.clear();
                        editor.putString(AppConstants.GCM_TOKEN_ID, gcm);
                        editor.apply();
                        CustomerApplication.setLocale(new Locale(AppConstants.LANG_EN));
                        NewHomeActivity.this.finish();
                        startActivity(new Intent(NewHomeActivity.this, com.dunkin.customer.RegisterActivity.class));
                    } else if (jsonResponse.getInt("success") == 100) {

                        AppUtils.showToastMessage(mContext, jsonResponse.getString("message"));
                    }

                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabClick(final int position, String title) {
        switch (position) {

            case 1:
                if (!newHomeFragment.isVisible()) {
                    newHomeFragment = new NewHomeFragment();
                    addFragment(newHomeFragment, title);
                }
                break;

            case 2:
                if (!redeemFragment.isVisible()) {
                    redeemFragment = new RedeemFragment();
                    addFragment(redeemFragment, title);
                }
                break;

            case 3:
                if (!offerFragment.isVisible()) {
                    offerFragment = new OfferFragment();
                    addFragment(offerFragment, title);
                }
                break;

            case 4:
                if (!myWalletFragment.isVisible()) {
                    myWalletFragment = new MyWalletFragment();
                    addFragment(myWalletFragment, title);
                }
                break;

            case 5:
                if (!moreFragment.isVisible()) {
                    addFragment(moreFragment, title);
                }
                break;

        }
    }

    public void addFragment(Fragment fragment, String fragmentTag) {
        if (fragment instanceof NewHomeFragment || fragment instanceof RedeemFragment ||
                fragment instanceof OfferFragment || fragment instanceof MyWalletFragment ||
                fragment instanceof MoreFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frFragmentContainer, fragment, fragmentTag).commit();
        } else {
            toolbar.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
            llTabs.setVisibility(View.GONE);
            ivDone.setVisibility(View.GONE);
            ivClose.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().addToBackStack(fragmentTag).replace(R.id.frFragmentContainer, fragment, fragmentTag).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (redeemFragment.isAdded()) {
            addFragment(newHomeFragment, "Home");
            for (int i = 0; i < homeList.size(); i++) {
                homeList.get(i).setSelect(false);
            }
            homeList.get(0).setSelect(true);
            tabAdapter.notifyDataSetChanged();
        } else if (offerFragment.isAdded()) {
            addFragment(newHomeFragment, "Home");
            for (int i = 0; i < homeList.size(); i++) {
                homeList.get(i).setSelect(false);
            }
            homeList.get(0).setSelect(true);
            tabAdapter.notifyDataSetChanged();
        } else if (myWalletFragment.isAdded()) {
            addFragment(newHomeFragment, "Home");
            for (int i = 0; i < homeList.size(); i++) {
                homeList.get(i).setSelect(false);
            }
            homeList.get(0).setSelect(true);
            tabAdapter.notifyDataSetChanged();
        } else if (moreFragment.isAdded()) {
            addFragment(newHomeFragment, "Home");
            for (int i = 0; i < homeList.size(); i++) {
                homeList.get(i).setSelect(false);
            }
            homeList.get(0).setSelect(true);
            tabAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    public void setToolbarView(Fragment fragment) {
        if (fragment instanceof NewHomeFragment || fragment instanceof RedeemFragment || fragment instanceof OfferFragment || fragment instanceof MyWalletFragment || fragment instanceof MoreFragment) {
            llTabs.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);
            if (fragment instanceof NewHomeFragment || fragment instanceof MoreFragment) {
                toolbar.setVisibility(View.GONE);
            } else if (fragment instanceof RedeemFragment) {
                toolbar.setVisibility(View.VISIBLE);
                ivDone.setVisibility(View.GONE);
                ivClose.setVisibility(View.GONE);
            } else {
                toolbar.setVisibility(View.VISIBLE);
                ivDone.setVisibility(View.GONE);
                ivClose.setVisibility(View.GONE);
            }
        } else {
            toolbar.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);
            llTabs.setVisibility(View.GONE);
            ivDone.setVisibility(View.GONE);
            ivClose.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCANNER_REQUEST_CODE && resultCode == RESULT_OK) {
            String code = !TextUtils.isEmpty(data.getStringExtra("scanner")) ? data.getStringExtra("scanner") : "";
            if (!TextUtils.isEmpty(code))
                getCheckAndWinResult(code);
        } else if (requestCode == SCANNER_PROMOTION_REQUEST_CODE && resultCode == RESULT_OK) {
            String code = !TextUtils.isEmpty(data.getStringExtra("scanner")) ? data.getStringExtra("scanner") : "";
            if (!TextUtils.isEmpty(code))
                redeemPromoCode(code);
        } else if (requestCode == 301 && resultCode == RESULT_OK) {
            finish();
            Intent i = new Intent(NewHomeActivity.this, NewHomeActivity.class);
            i.putExtra("navigateflag", AppConstants.MENU_HOME);
            startActivity(i);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getCheckAndWinResult(String code) {
        try {
            AppController.checkAndScanWin(NewHomeActivity.this, code, AppUtils.getAppPreference(NewHomeActivity.this).getInt(AppConstants.USER_COUNTRY, -1),
                    AppUtils.getAppPreference(NewHomeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""), new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            String res = (String) result;
                            JSONObject jsonResponse = new JSONObject(res);
                            if (jsonResponse.getString("success").equalsIgnoreCase("1")) {

                                WinStatusDialog fragment = new WinStatusDialog();

                                FragmentTransaction ft = getSupportFragmentManager()
                                        .beginTransaction();
                                Bundle bundle = new Bundle();
                                bundle.putString("res", res);
                                fragment.setArguments(bundle);
                                fragment.show(ft, "loading");

                            } else {
                                if (!newHomeFragment.isAdded()) {
                                    addFragment(newHomeFragment, "Home");
                                }
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScanImage() {
        if (getSupportFragmentManager().findFragmentById(R.id.content) instanceof NewHomeFragment) {
            ((NewHomeFragment) getSupportFragmentManager().findFragmentById(R.id.content)).loadingAnimation(strGetScanImage);
        }
    }

    public void addHomeFragment() {
        if (!newHomeFragment.isAdded()) {
            addFragment(newHomeFragment, "Home");
        }
    }

    public void checkScanAndWin() {
        try {
//            showProgressDialog();
            if (TextUtils.isEmpty(res)) {

                //1
                AppController.getScan(mContext, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
//                        dismissProgressDialog();
                        res = (String) result;
                        processResponse(res);
                    }
                });
            } else {
                processResponse(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processResponse(String res) throws JSONException {
        JSONObject jsonResponse = new JSONObject(res);
        if (jsonResponse.getString("success").equalsIgnoreCase("1")) {
            strUrl = jsonResponse.getString("scanwinImage");
            strOfferUrl = jsonResponse.getString("offerImage");
            isScanWinEnable = jsonResponse.getString("isScanWinEnable");
            isOfferEnable = jsonResponse.getString("isOfferEnable");
            if (ActivityCompat.checkSelfPermission(mContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(NewHomeActivity.this, permsReadWrite, PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE);
            } else if (ActivityCompat.checkSelfPermission(mContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fileDownload();
            }
        }
    }

    public void fileDownload() {
        /**
         * Download scan image
         */

//        showProgressDialog();
        new NewHomeActivity.GetFileTask(mContext, new FileDownloadListener() {
            @Override
            public void onFileDownload(String path) {
//                dismissProgressDialog();
                strGetScanImage = path;
                /**
                 * If app is open and user already get scan result then don't display intro screen
                 */

                if (!AppUtils.getAppPreference(mContext).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
                    if (isScanWinEnable.equalsIgnoreCase("1") && (getSupportFragmentManager().findFragmentById(R.id.content) instanceof NewHomeFragment))
                        ScanAndWinDialog.newInstance(NewHomeActivity.this, path, false).show();
                } else {
                    if (isScanWinEnable.equalsIgnoreCase("1") && (getSupportFragmentManager().findFragmentById(R.id.content) instanceof NewHomeFragment))
                        setScanImage();
                }

                /**
                 * Download offer image
                 */

//                new GetFileTask(mContext, new FileDownloadListener() {
//                    @Override
//                    public void onFileDownload(String path) {
//                        scanOfferImagePath = path;
//                    }
//                }).execute(strOfferUrl);

            }
        }).execute(strUrl);
    }

    public void opendisalogofShare(final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewHomeActivity.this);
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.view), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!myWalletFragment.isAdded()) {
                                    addFragment(myWalletFragment, "Pay");
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.al_close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.app_name));
                alert.show();
            }
        });

    }

    public void opendisalogofSharePointReceive(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewHomeActivity.this);
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.hurray), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!myWalletFragment.isAdded()) {
                                    addFragment(myWalletFragment, "Pay");
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.al_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle(getResources().getString(R.string.app_name));
                alert.show();
            }
        });
    }

    public void redeemPromoCode(String code) {
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        lat = String.valueOf(latitude);
        log = String.valueOf(longitude);
        try {
            AppController.redeemPromoCode(NewHomeActivity.this, code,
                    AppUtils.getAppPreference(NewHomeActivity.this).getInt(AppConstants.USER_COUNTRY, -1),
                    AppUtils.getAppPreference(NewHomeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""),
                    lat, log, new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 1) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewHomeActivity.this);

                                // Setting Dialog Message
                                alertDialog.setMessage(jsonResponse.getString("message"));

                                // On pressing Settings button
                                alertDialog.setPositiveButton("View", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        getSupportFragmentManager().beginTransaction().replace(R.id.content, new NotificationFragment()).commitAllowingStateLoss();
                                    }
                                });

                                alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                // Showing Alert Message
                                alertDialog.show();
                            } else if (jsonResponse.getInt("success") == 0) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewHomeActivity.this);

                                // Setting Dialog Message
                                alertDialog.setMessage(jsonResponse.getString("message"));

                                // On pressing Settings button
                                alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                // Showing Alert Message
                                alertDialog.show();
                            } else {
                                if (jsonResponse.getInt("success") != 99) {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewHomeActivity.this);

                                    // Setting Dialog Message
                                    alertDialog.setMessage(getString(R.string.system_error));

                                    // On pressing Settings button
                                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    // Showing Alert Message
                                    alertDialog.show();
                                }
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBack) {
            onBackPressed();
        }
    }

    public class GetFileTask extends AsyncTask<String, Void, String> {
        private FileDownloadListener downloadListner;

        public GetFileTask(Context mContext, FileDownloadListener downloadListner) {
            this.downloadListner = downloadListner;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (path != null) {
                File f = new File(path);
                if (f.exists()) {
                    downloadListner.onFileDownload(path);
                }
            }
        }

        /*
         * param 0 : url
         */
        @Override
        protected String doInBackground(String... params) {
            try {
                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        TempPath);

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    boolean success = mediaStorageDir.mkdirs();
                    if (!success) {
                    }
                }

                URL ulrn = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
                InputStream is = con.getInputStream();

                try {
                    OutputStream fOut = null;
                    String ImageName = params[0].substring(params[0].lastIndexOf("/") + 1);//, params[0].lastIndexOf("."));

                    mFileTemp = new File(mediaStorageDir, ImageName);
                    mFileTemp.createNewFile();
//                    if (!mFileTemp.exists()) {

                    fOut = new FileOutputStream(mFileTemp);

                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = is.read(bytes)) != -1) {
                        fOut.write(bytes, 0, read);
                    }
                    is.close();
                    fOut.flush();
                    fOut.close();
//                    }
                    return mFileTemp.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
