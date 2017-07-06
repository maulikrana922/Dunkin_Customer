package com.dunkin.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.GPSTracker;
import com.dunkin.customer.Utils.LoadingDialog;
import com.dunkin.customer.adapters.NavDrawerAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ImageDialog;
import com.dunkin.customer.dialogs.ScanAndWinDialog;
import com.dunkin.customer.dialogs.WinStatusDialog;
import com.dunkin.customer.fragments.AboutUsFragment;
import com.dunkin.customer.fragments.CartFragment;
import com.dunkin.customer.fragments.CategoryFragment;
import com.dunkin.customer.fragments.ContactUsFragment;
import com.dunkin.customer.fragments.FeedbackFragment;
import com.dunkin.customer.fragments.GetBillFragment;
import com.dunkin.customer.fragments.HomeFragment;
import com.dunkin.customer.fragments.MyCreditCardFragment;
import com.dunkin.customer.fragments.MyProfileFragment;
import com.dunkin.customer.fragments.MyWalletFragment;
import com.dunkin.customer.fragments.NearRestaurantFragment;
import com.dunkin.customer.fragments.NewsPagerFragment;
import com.dunkin.customer.fragments.NotificationFragment;
import com.dunkin.customer.fragments.OfferFragment;
import com.dunkin.customer.fragments.OrderHistoryFragment;
import com.dunkin.customer.fragments.RecurrentOrderFragment;
import com.dunkin.customer.fragments.RedeemFragment;
import com.dunkin.customer.fragments.RotatingBannerFragment;
import com.dunkin.customer.fragments.ScanFragment;
import com.dunkin.customer.fragments.ScratchCardFragment;
import com.dunkin.customer.listener.FileDownloadListener;
import com.dunkin.customer.models.NavDrawerModel;
import com.facebook.CallbackManager;
import com.fasterxml.jackson.core.type.TypeReference;

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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.dunkin.customer.constants.AppConstants.context;

public class HomeActivity extends AppCompatActivity {
    private static final String NAV_ITEM_ID = "navItemId";
    private static Context mContext;
    Toolbar toolbar;
    CallbackManager callbackManager;
    private int mNavItemId;
    private ListView navigationView;
    private NavDrawerAdapter navDrawerAdapter;
    private DrawerLayout mDrawerLayout;
    private MyActionBarDrawerToggle mDrawerToggle;
    private DBAdapter dbAdapter;
    private List<NavDrawerModel> navigationList;
    private ArrayList<String> newsFragmentTitles;
    private boolean isFromGCM = false;
    private int msgType;

    private static final int SCANNER_REQUEST_CODE = 0x11;
    private static final int SCANNER_PROMOTION_REQUEST_CODE = 0x111;
    public static String strUrl = "", strOfferUrl = "", strGetScanImage = "", scanOfferImagePath = "",
            isScanWinEnable = "", isOfferEnable = "";
        final String[] permsReadWrite = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};
    private static final int PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE = 0x21;
    private static final int CAMERA_PERMISSION_REQUEST = 0x31;
    public static String TempPath = Environment.getExternalStorageDirectory()
            + File.separator + "Dunkin Leb" + File.separator + "Temp" + File.separator;
    private File mFileTemp;

    private LoadingDialog progressDialog;
    private String res;
    GPSTracker gps;
    private double latitude, longitude;
    private String lat, log;

    public static Context getCustomContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mContext = HomeActivity.this;

        if (getIntent().hasExtra("FromGCM")) {
            isFromGCM = getIntent().getBooleanExtra("FromGCM", false);
        } else {
            isFromGCM = false;
        }

        if (getIntent().hasExtra("MsgType")) {
            msgType = getIntent().getIntExtra("MsgType", 0);
        }

        callbackManager = CallbackManager.Factory.create();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        SharedPreferences myPrefs = AppUtils.getAppPreference(HomeActivity.this);
        dbAdapter = new DBAdapter(HomeActivity.this);
        setSupportActionBar(toolbar);

        SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
        editor.putBoolean(AppConstants.USER_SCAN_RESULT, false);
        editor.apply();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // listen for navigation events
        navigationView = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        View HeaderView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.navigation_header_layout, null);
        View FooterView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.navigation_footer_layout, null);
        TextView FirstName = (TextView) HeaderView.findViewById(R.id.FirstName);

        navigationView.addHeaderView(HeaderView, null, false);
        navigationView.addFooterView(FooterView, null, false);

        navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavDrawerModel navModel = (NavDrawerModel) parent.getAdapter().getItem(position);

                navigationView.setItemChecked(position, true);
                mNavItemId = navModel.getNavId();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                navigate(mNavItemId, 0);
            }
        });

        FirstName.setText(getString(R.string.txt_hello, myPrefs.getString(AppConstants.USER_FIRST_NAME, "")));

        try {
            getNavigationMenuList(getIntent().getIntExtra("navigateflag", AppConstants.MENU_HOME));
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        mDrawerToggle = new MyActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(false);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(AppConstants.MENU_HOME);

        gps = new GPSTracker(HomeActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getNavigationMenuList(final int switchPosition) throws UnsupportedEncodingException, JSONException {
        setNavigationMenu(switchPosition, false);
    }

    private void setNavigationMenu(final int switchPosition, final boolean isDynamic) {
        try {
            AppController.getMenuList(HomeActivity.this, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {

                        dbAdapter.open();
                        dbAdapter.addOfflineData(AppConstants.OF_MENU, jsonResponse.toString());
                        dbAdapter.close();

                        newsFragmentTitles = new ArrayList<>();
                        navigationList = new ArrayList<>();
                        LinkedHashMap<String, LinkedHashMap<Integer, String>> navigationMap;
                        navigationMap = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONObject("message").toString(), new TypeReference<LinkedHashMap<String, LinkedHashMap<Integer, String>>>() {
                        }); // get all vlaues

                        Set<String> navigationKey = navigationMap.keySet(); // get only key

                        LinkedHashMap<Integer, String> valueSet;

                        // FOR My Home
                        if (navigationKey.contains("My Home") || navigationKey.contains("Home Sweet Home")) {
                            navigationList.add(new NavDrawerModel(AppConstants.MENU_HOME, R.drawable.ic_nav_home, getString(R.string.nav_home), false));
                        }

                        // FOR LABEL 1
                        if (navigationKey.contains("Section 1")) {
                            navigationList.add(new NavDrawerModel(-1, -1, null, true));

                            valueSet = navigationMap.get("Section 1");

                            // "It's Me!"
                            if (valueSet.containsKey(3)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_PROFILE, R.drawable.ic_nav_its_me, valueSet.get(3), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_PROFILE, R.drawable.ic_nav_its_me, getString(R.string.nav_my_profile), false));
                            }

                            // "Scan And Win"
                            if (valueSet.containsKey(28)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_SCAN_AND_WIN, R.drawable.ic_scan_win, valueSet.get(28), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_SCAN_AND_WIN, R.drawable.ic_scan_win, getString(R.string.nav_scan_and_win), false));
                            }

                            // "My Notification"
                            if (valueSet.containsKey(4)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_NOTIFICATIONS, R.drawable.ic_nav_notifications, valueSet.get(4), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_NOTIFICATIONS, R.drawable.ic_nav_notifications, getString(R.string.nav_notification), false));
                            }

                            // "Pay"
                            if (valueSet.containsKey(5)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_WALLET, R.drawable.ic_nav_pay, valueSet.get(5), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_WALLET, R.drawable.ic_nav_pay, getString(R.string.nav_wallet), false));
                            }

                            // "Refill"
                            if (valueSet.containsKey(6)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_CREDIT_CARD, R.drawable.ic_nav_refill, valueSet.get(6), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_CREDIT_CARD, R.drawable.ic_nav_refill, getString(R.string.nav_manage_card), false));
                            }


                        }

                        // FOR LABEL 2
                        if (navigationKey.contains("Section 2")) {
                            navigationList.add(new NavDrawerModel(-1, -1, null, true));

                            valueSet = navigationMap.get("Section 2");

                            // Products
                            if (valueSet.containsKey(8)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_PRODUCTS, R.drawable.ic_nav_products, valueSet.get(8), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_PRODUCTS, R.drawable.ic_nav_products, getString(R.string.nav_products), false));
                            }

                            // Order History
                            if (valueSet.containsKey(9)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_ORDER_HISTORY, R.drawable.ic_nav_my_favorites, valueSet.get(9), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_ORDER_HISTORY, R.drawable.ic_nav_my_favorites, getString(R.string.nav_order_history2), false));
                            }

                            // Get Bill
                            if (valueSet.containsKey(10)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_GET_BILL, R.drawable.ic_nav_get_bill, valueSet.get(10), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_GET_BILL, R.drawable.ic_nav_get_bill, getString(R.string.nav_get_bill), false));
                            }

                            // My Order
                            if (valueSet.containsKey(11)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_CART, R.drawable.ic_nav_my_order, valueSet.get(11), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_CART, R.drawable.ic_nav_my_order, getString(R.string.nav_cart), false));
                            }

                            // Recurrent Order
                            if (valueSet.containsKey(27)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_RECURRENT_ORDER, R.drawable.ic_nav_my_recurrent_order, valueSet.get(27), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_RECURRENT_ORDER, R.drawable.ic_nav_my_recurrent_order, getString(R.string.nav_recurrent_order), false));
                            }
                        }

                        // FOR LABEL 3
                        if (navigationKey.contains("Section 3")) {
                            navigationList.add(new NavDrawerModel(-1, -1, null, true));

                            valueSet = navigationMap.get("Section 3");

                            // Offers
                            if (valueSet.containsKey(13)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_OFFER, R.drawable.ic_nav_deal, valueSet.get(13), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_OFFER, R.drawable.ic_nav_deal, getString(R.string.nav_offer2), false));
                            }

                            // Rewards
                            if (valueSet.containsKey(14)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_GIFT_STORE, R.drawable.ic_nav_redeem, valueSet.get(14), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_GIFT_STORE, R.drawable.ic_nav_redeem, getString(R.string.nav_coupons2), false));
                            }

                            // Find us
                            if (valueSet.containsKey(15)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_NEARBY_RESTAURANT, R.drawable.ic_nav_find_me, valueSet.get(15), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_NEARBY_RESTAURANT, R.drawable.ic_nav_find_me, getString(R.string.nav_near_restaurant), false));
                            }
                        }

                        // FOR Fun n Games
                        if (navigationKey.contains("Fun n Games")) {
                            navigationList.add(new NavDrawerModel(-1, -1, null, true));
                            navigationList.add(new NavDrawerModel(AppConstants.MENU_SCRATCH_CARD, R.drawable.ic_nav_fun_n_games, getString(R.string.nav_scratch_card), false));
                        }

                        // FOR Happening
                        if (navigationKey.contains("Section 4")) {
                            navigationList.add(new NavDrawerModel(-1, -1, null, true));

                            valueSet = navigationMap.get("Section 4");

                            // News
                            if (valueSet.containsKey(18)) {
                                if (isDynamic)
                                    newsFragmentTitles.add(valueSet.get(18));
                                else
                                    newsFragmentTitles.add(getString(R.string.nav_news));

                                NavDrawerModel navNewsModel = new NavDrawerModel(AppConstants.MENU_NEWS_EVENT, R.drawable.ic_nav_happenings, getString(R.string.nav_news_event), false);
                                if (!navigationList.contains(navNewsModel))
                                    navigationList.add(navNewsModel);
                            }

                            // Events
                            if (valueSet.containsKey(19)) {
                                if (isDynamic)
                                    newsFragmentTitles.add(valueSet.get(19));
                                else
                                    newsFragmentTitles.add(getString(R.string.nav_event));

                                NavDrawerModel navNewsModel = new NavDrawerModel(AppConstants.MENU_NEWS_EVENT, R.drawable.ic_nav_happenings, getString(R.string.nav_news_event), false);
                                if (!navigationList.contains(navNewsModel))
                                    navigationList.add(navNewsModel);
                            }

                            // Celebration
                            if (valueSet.containsKey(20)) {
                                if (isDynamic)
                                    newsFragmentTitles.add(valueSet.get(20));
                                else
                                    newsFragmentTitles.add(getString(R.string.nav_celebration));

                                NavDrawerModel navNewsModel = new NavDrawerModel(AppConstants.MENU_NEWS_EVENT, R.drawable.ic_nav_happenings, getString(R.string.nav_news_event), false);
                                if (!navigationList.contains(navNewsModel))
                                    navigationList.add(navNewsModel);
                            }
                        }

                        // FOR LABEL 5
                        if (navigationKey.contains("Section 5")) {
                            navigationList.add(new NavDrawerModel(-1, -1, null, true));

                            valueSet = navigationMap.get("Section 5");

                            // Settings
                            if (valueSet.containsKey(22)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_SETTINGS, R.drawable.ic_nav_setting, valueSet.get(22), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_SETTINGS, R.drawable.ic_nav_setting, getString(R.string.nav_settings), false));
                            }

                            // Give us 5 Stars
                            if (valueSet.containsKey(23)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_FEEDBACK, R.drawable.ic_nav_give_me_stars, valueSet.get(23), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_FEEDBACK, R.drawable.ic_nav_give_me_stars, getString(R.string.nav_rating), false));
                            }

                            // Talk to us
                            if (valueSet.containsKey(24)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_CONTACT_US, R.drawable.ic_nav_talk_to_me, valueSet.get(24), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_CONTACT_US, R.drawable.ic_nav_talk_to_me, getString(R.string.nav_contact_us), false));
                            }

                            // Get to know us
                            if (valueSet.containsKey(25)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_ABOUT_US, R.drawable.ic_nav_get_to_know_us, valueSet.get(25), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_ABOUT_US, R.drawable.ic_nav_get_to_know_us, getString(R.string.nav_about_us), false));
                            }

                            // Log out
                            if (valueSet.containsKey(26)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_LOGOUT, R.drawable.ic_nav_logout, valueSet.get(26), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_LOGOUT, R.drawable.ic_nav_logout, getString(R.string.nav_exit), false));
                            }
                        }

                        navDrawerAdapter = new NavDrawerAdapter(HomeActivity.this, navigationList);
                        navigationView.setAdapter(navDrawerAdapter);

                        if (isFromGCM) {
                            Log.e("msgtype", "" + msgType);
                            if (msgType == 8) {
                                navigateAndCheckItem(AppConstants.MENU_WALLET);
                            } else if (msgType == AppConstants.MENU_FEEDBACK) {
                                navigateAndCheckItem(AppConstants.MENU_FEEDBACK);
//                            } else if (msgType == 12)
//                            {
//                                getSupportFragmentManager().beginTransaction().replace(R.id.content, new NotificationFragment()).commitAllowingStateLoss();
                            }else
                                navigateAndCheckItem(AppConstants.MENU_NOTIFICATIONS);
                        } else
                            navigateAndCheckItem(switchPosition);
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            AppController.getUserProfile(HomeActivity.this, AppUtils.getAppPreference(HomeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""), true
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
                                AppUtils.showToastMessage(HomeActivity.this, getString(R.string.system_error));
                            }
                            Log.e("res", (String) result);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCANNER_REQUEST_CODE && resultCode == RESULT_OK) {
            String code = !TextUtils.isEmpty(data.getStringExtra("scanner")) ? data.getStringExtra("scanner") : "";
            if (!TextUtils.isEmpty(code))
                getCheckAndWinResult(code);
        }

        if(requestCode == SCANNER_PROMOTION_REQUEST_CODE && resultCode == RESULT_OK) {
            String code = !TextUtils.isEmpty(data.getStringExtra("scanner")) ? data.getStringExtra("scanner") : "";
            if (!TextUtils.isEmpty(code))
                redeemPromoCode(code);
        }

        if (requestCode == 301 && resultCode == RESULT_OK) {
            finish();
            Intent i = new Intent(HomeActivity.this, HomeActivity.class);
            i.putExtra("navigateflag", AppConstants.MENU_HOME);
            startActivity(i);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getCheckAndWinResult(String code) {
        try {
            AppController.checkAndScanWin(HomeActivity.this, code, AppUtils.getAppPreference(HomeActivity.this).getInt(AppConstants.USER_COUNTRY, -1),
                    AppUtils.getAppPreference(HomeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""), new Callback() {
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
                                ((HomeActivity) mContext).navigate(AppConstants.MENU_HOME);
                            }
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redeemPromoCode(String code) {
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        lat = String.valueOf(latitude);
        log = String.valueOf(longitude);
        try {
            AppController.redeemPromoCode(HomeActivity.this, code,
                    AppUtils.getAppPreference(HomeActivity.this).getInt(AppConstants.USER_COUNTRY, -1),
                    AppUtils.getAppPreference(HomeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""),
                    lat, log, new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 1) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

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
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

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
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);

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
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean navigate(final int itemId, int... clickVal) {
        if (RotatingBannerFragment.timer != null) {
            try {
                RotatingBannerFragment.timer.cancel();
                RotatingBannerFragment.timer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {

            final FrameLayout advertisement_banner = (FrameLayout) findViewById(R.id.advertisement_banner);
            if (advertisement_banner != null) {
                advertisement_banner.setVisibility(View.GONE);
            }

            AppController.getRotatingBanner(HomeActivity.this, AppUtils.getAppPreference(HomeActivity.this).getInt(AppConstants.USER_COUNTRY, -1), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    if (jsonResponse.getInt("success") == 1) {
                        if (advertisement_banner != null) {
                            advertisement_banner.setVisibility(View.VISIBLE);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.advertisement_banner, RotatingBannerFragment.newInstance()).commit();
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), getString(R.string.msg_register_error));
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                    } else {
                        if (advertisement_banner != null) {
                            advertisement_banner.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        switch (itemId) {
            case AppConstants.MENU_HOME:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_OFFER:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new OfferFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_NEWS_EVENT:
                NewsPagerFragment newsPagerFragment = new NewsPagerFragment();
                Bundle b = new Bundle();
                b.putStringArrayList("titles", newsFragmentTitles);
                b.putInt("pos", clickVal[0]);
                newsPagerFragment.setArguments(b);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, newsPagerFragment).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_PRODUCTS:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new CategoryFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_ORDER_HISTORY:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new OrderHistoryFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_RECURRENT_ORDER:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new RecurrentOrderFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_NOTIFICATIONS:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new NotificationFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_GIFT_STORE:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new RedeemFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_SCRATCH_CARD:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new ScratchCardFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_NEARBY_RESTAURANT:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new NearRestaurantFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_FEEDBACK:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new FeedbackFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_GET_BILL:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new GetBillFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_WALLET:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new MyWalletFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_CREDIT_CARD:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new MyCreditCardFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_PROFILE:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new MyProfileFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_CART:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new CartFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_SETTINGS:
                startActivityForResult(new Intent(HomeActivity.this, com.dunkin.customer.SettingActivity.class), 301);
                return true;

            case AppConstants.MENU_CONTACT_US:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new ContactUsFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_ABOUT_US:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new AboutUsFragment()).commitAllowingStateLoss();
                return true;

            case AppConstants.MENU_LOGOUT:
                showDialogBox(getString(R.string.al_exit_message), getString(R.string.al_warning));
                return true;

            case AppConstants.MENU_SCAN_AND_WIN:
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

//                if (isOfferEnable.equalsIgnoreCase("1")) {
//                    ScanAndWinFragment scanAndWinFragment = new ScanAndWinFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("isForScan", false);
//                    scanAndWinFragment.setArguments(bundle);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.content, scanAndWinFragment).commitAllowingStateLoss();
//                }

                /**
                 * Open Dialog instead of Fragment and perform same operation
                 */
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new ScanFragment()).commitAllowingStateLoss();
//                if (isOfferEnable.equalsIgnoreCase("1"))
//                    ScanAndWinDialog.newInstance(HomeActivity.this, scanOfferImagePath, true).show();
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        //outState.putInt(NAV_ITEM_ID, mNavItemId);
        //super.onSaveInstanceState(outState);

        //No call for super(). Bug on API Level > 11.

        // If you need to save the instance, and add something to your outState Bundle you can use the following :
        //outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        //super.onSaveInstanceState(outState);
    }

    private void showDialogBox(String message, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton(getString(R.string.al_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                try {
                    AppController.logoutUser(HomeActivity.this, true, new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 1) {
                                AppUtils.showToastMessage(HomeActivity.this, getString(R.string.msg_logout_success));
                                SharedPreferences.Editor editor = AppUtils.getAppPreference(HomeActivity.this).edit();
                                String gcm = AppUtils.getAppPreference(HomeActivity.this).getString(AppConstants.GCM_TOKEN_ID, "");
                                editor.clear();
                                editor.putString(AppConstants.GCM_TOKEN_ID, gcm);
                                editor.apply();
                                CustomerApplication.setLocale(new Locale(AppConstants.LANG_EN));
                                finish();
                                startActivity(new Intent(HomeActivity.this, com.dunkin.customer.RegisterActivity.class));
                            } else if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                            } else {
                                AppUtils.showToastMessage(HomeActivity.this, getString(R.string.msg_logout_failure));
                            }
                        }
                    });
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    AppUtils.showToastMessage(HomeActivity.this, getString(R.string.system_error));
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

    public void navigateAndCheckItem(int position, final int... currentVal) {
        for (int i = 0; i < navigationList.size(); i++) {
            NavDrawerModel nav = navigationList.get(i);
            if (position == AppConstants.MENU_HOME) {
                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_HOME, null, null),
                        AppConstants.MENU_HOME,
                        navigationView.getAdapter().getItemId(AppConstants.MENU_HOME));
                break;
            } else if (position == AppConstants.MENU_SCRATCH_CARD) {
                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_SCRATCH_CARD, null, null),
                        AppConstants.MENU_SCRATCH_CARD,
                        navigationView.getAdapter().getItemId(AppConstants.MENU_SCRATCH_CARD));
                break;
            } else if (position == AppConstants.MENU_NOTIFICATIONS) {
//                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_NOTIFICATIONS, null, null),
//                        AppConstants.MENU_NOTIFICATIONS,
//                        navigationView.getAdapter().getItemId(AppConstants.MENU_NOTIFICATIONS));

                if(msgType == 12)
                {
                    position = position;
                }
                else
                {
                    position = position + 1;
                }

                NavDrawerModel navModel = (NavDrawerModel) navigationView.getAdapter().getItem(position);

                navigationView.setItemChecked(position, true);
                mNavItemId = navModel.getNavId();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                navigate(mNavItemId, currentVal);
                break;
            } else if (position == AppConstants.MENU_WALLET) {
                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_WALLET, null, null),
                        AppConstants.MENU_WALLET,
                        navigationView.getAdapter().getItemId(AppConstants.MENU_WALLET));
                break;
            } else if (position == AppConstants.MENU_CONTACT_US) {
                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_CONTACT_US, null, null),
                        AppConstants.MENU_CONTACT_US,
                        navigationView.getAdapter().getItemId(AppConstants.MENU_CONTACT_US));
                break;
            } else if (position == AppConstants.MENU_FEEDBACK) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                FeedbackFragment feedbackFragment = new FeedbackFragment();
                fragmentTransaction.replace(R.id.content, feedbackFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            } else if (position == AppConstants.MENU_NEWS_EVENT && nav.getNavId() == position) {
                NavDrawerModel navModel = (NavDrawerModel) navigationView.getAdapter().getItem(i + 1);

                navigationView.setItemChecked(i + 1, true);
                mNavItemId = navModel.getNavId();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                navigate(mNavItemId, currentVal);
                break;
            } else {
                if (nav.getNavId() == position) {
                    Log.i("DATA", "" + i);
                    navigationView.performItemClick(navigationView.getAdapter().getView(i + 1, null, null),
                            i + 1,
                            navigationView.getAdapter().getItemId(i + 1));
                    break;
                }
            }
        }
    }

    public void setScanImage() {
        if (getSupportFragmentManager().findFragmentById(R.id.content) instanceof HomeFragment) {
            ((HomeFragment) getSupportFragmentManager().findFragmentById(R.id.content)).loadingAnimation(strGetScanImage);
        }
    }

    public class MyActionBarDrawerToggle extends android.support.v7.app.ActionBarDrawerToggle {
        MyActionBarDrawerToggle(Activity activity, final DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);

            setHomeAsUpIndicator(R.drawable.ic_menu_icon);
            setDrawerIndicatorEnabled(false);

            setToolbarNavigationClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new LoadingDialog(this);
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    public void checkScanAndWin() {
        try {
            showProgressDialog();
            if (TextUtils.isEmpty(res)) {
                AppController.getScan(mContext, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
                        dismissProgressDialog();
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

                ActivityCompat.requestPermissions(HomeActivity.this, permsReadWrite, PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE);
            } else if (ActivityCompat.checkSelfPermission(mContext,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fileDownload();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        switch (permsRequestCode) {

            case PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE:

                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeAccepted && readAccepted) {
                    if (ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(mContext,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        fileDownload();
                    }
                }
                break;

            case CAMERA_PERMISSION_REQUEST:

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    ((Activity) mContext).startActivityForResult(new Intent(AppConstants.context, SimpleScannerActivity.class), SCANNER_REQUEST_CODE);
                }
                break;
        }
    }

    private void fileDownload() {
        /**
         * Download scan image
         */

        showProgressDialog();
        new GetFileTask(mContext, new FileDownloadListener() {
            @Override
            public void onFileDownload(String path) {
                dismissProgressDialog();
                strGetScanImage = path;
                /**
                 * If app is open and user already get scan result then don't display intro screen
                 */

                if (!AppUtils.getAppPreference(context).getBoolean(AppConstants.USER_SCAN_RESULT, false)) {
                    if (isScanWinEnable.equalsIgnoreCase("1") && (getSupportFragmentManager().findFragmentById(R.id.content) instanceof HomeFragment))
                        ScanAndWinDialog.newInstance(HomeActivity.this, path, false).show();
                } else {
                    if (isScanWinEnable.equalsIgnoreCase("1") && (getSupportFragmentManager().findFragmentById(R.id.content) instanceof HomeFragment))
                        setScanImage();
                }

                /**
                 * Download offer image
                 */

                new GetFileTask(mContext, new FileDownloadListener() {
                    @Override
                    public void onFileDownload(String path) {
                        scanOfferImagePath = path;
                    }
                }).execute(strOfferUrl);

            }
        }).execute(strUrl);
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

                    mFileTemp = new File(
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            TempPath + ImageName);

                    if (!mFileTemp.exists()) {

                        fOut = new FileOutputStream(mFileTemp);

                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while ((read = is.read(bytes)) != -1) {
                            fOut.write(bytes, 0, read);
                        }
                        is.close();
                        fOut.flush();
                        fOut.close();
                    }
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

//    public class CheckVersionOnPlayStore extends AsyncTask<String, String, String>
//    {
//        Activity mContext;
//        String urlToGetIP;
//
//        public CheckVersionOnPlayStore(Activity mContext)
//
//        {
//
//            this.mContext = mContext;
//
//        }
//
//        @Override
//        protected void onPreExecute()
//        {
//
//        }
//
//        @Override
//        protected String doInBackground(String... params)
//        {
//            HttpClient httpclient;
//            String result = null;
//            try
//            {
////                history appointment list data
//                httpclient = new DefaultHttpClient();
//                HttpGet httppost = new HttpGet("http://carreto.pt/tools/android-store-version/?package=com.dunkin.customer");
//
////                Execute HTTP Get Request
//
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity entity = response.getEntity();
//                InputStream instream = entity.getContent();
//                result = convertStreamToString(instream);
//                return result;
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        public String convertStreamToString(InputStream is) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            try {
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return sb.toString();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            try {
//                if (result != null) {
//                    try {
//                        Gson gson = new Gson();
//                        VersionPlayStore versionInfo = gson.fromJson(result, VersionPlayStore.class);
//                        if (Double.parseDouble(versionInfo.version) > versionCode) {
//                            try {
//                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
//                                alertDialogBuilder.setTitle("Adractive");
//                                alertDialogBuilder.setMessage("Download the latest updated version of app from Playstore to get the maximum videos with maximum points and our latest entertainment shows")
//                                        .setCancelable(false)
//                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//                                                final String appPackageName = getPackageName();
////                                                getPackageName() from Context or Activity object
//                                                try {
//                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                                                } catch (android.content.ActivityNotFoundException anfe) {
//                                                    anfe.printStackTrace();
//                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                                                }
//                                                dialog.cancel();
//                                            }
//                                        });
//                                AlertDialog alertDialog = alertDialogBuilder.create();
//                                if (!HomeActivity.this.isFinishing()) {
//                                    alertDialog.show();
//                                }
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        } else {
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public class VersionPlayStore
//    {
//        public String package_name;
//        public boolean status;
//        public String author;
//        public String app_name;
//        public boolean locale;
//        public String publish_date;
//        public String version;
//        public  String message;
//        public String last_version_description;
//    }

}
