package com.dunkin.customer.fragments;

/**
 * Created by kinal on 20/2/18.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dunkin.customer.CustomerApplication;
import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.adapters.NavDrawerAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.NavDrawerModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MoreFragment extends Fragment {

    private NewHomeActivity homeActivity;
    private List<NavDrawerModel> navigationList;
    private ArrayList<String> newsFragmentTitles;
    private NavDrawerAdapter navDrawerAdapter;
    private ListView navigationView;
    private int mNavItemId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = (NewHomeActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        initUI(view);
        try {
            getNavigationMenuList(homeActivity.getIntent().getIntExtra("navigateflag", AppConstants.MENU_HOME));
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeActivity.setToolbarView(this);
    }

    private void initUI(View view){
        navigationView = (ListView) view.findViewById(R.id.navList);
        View HeaderView = LayoutInflater.from(homeActivity).inflate(R.layout.navigation_header_layout, null);
        ImageView ivMenuHeader = (ImageView) HeaderView.findViewById(R.id.ivMenuHeader);
        if (homeActivity.dashbordModel != null && !TextUtils.isEmpty(homeActivity.dashbordModel.MenuImage))
            AppUtils.setImage(ivMenuHeader, homeActivity.dashbordModel.MenuImage);
        View FooterView = LayoutInflater.from(homeActivity).inflate(R.layout.navigation_footer_layout, null);
        TextView FirstName = (TextView) HeaderView.findViewById(R.id.FirstName);
        SharedPreferences myPrefs = AppUtils.getAppPreference(homeActivity);
        FirstName.setText(getString(R.string.txt_hello, myPrefs.getString(AppConstants.USER_FIRST_NAME, "")));
        navigationView.addHeaderView(HeaderView, null, false);
        navigationView.addFooterView(FooterView, null, false);

        navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavDrawerModel navModel =(NavDrawerModel) parent.getAdapter().getItem(position);

                navigationView.setItemChecked(position, true);
                mNavItemId = navModel.getNavId();
                navigate(mNavItemId, 0);
            }
        });
    }

    public void getNavigationMenuList(final int switchPosition) throws UnsupportedEncodingException, JSONException {
        setNavigationMenu(switchPosition, false);
    }

    private void setNavigationMenu(final int switchPosition, final boolean isDynamic) {
        try {
            AppController.getMenuList(homeActivity, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {

                        homeActivity.dbAdapter.open();
                        homeActivity.dbAdapter.addOfflineData(AppConstants.OF_MENU, jsonResponse.toString());
                        homeActivity.dbAdapter.close();

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

                            // "rewards appiness"
                            if (valueSet.containsKey(29)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_GIFT_APPINESS, R.drawable.ic_gift_appiness, valueSet.get(29), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_GIFT_APPINESS, R.drawable.ic_gift_appiness, getString(R.string.nav_gift_appiness), false));
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

                            // Rate Staff
                            if (valueSet.containsKey(30)) {
                                if (isDynamic)
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_RATE_STAFF, R.drawable.ic_nav_give_me_stars, valueSet.get(30), false));
                                else
                                    navigationList.add(new NavDrawerModel(AppConstants.MENU_RATE_STAFF, R.drawable.ic_nav_give_me_stars, getString(R.string.nav_staff_rating), false));
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

                        navDrawerAdapter = new NavDrawerAdapter(homeActivity, navigationList);
                        navigationView.setAdapter(navDrawerAdapter);
                        navigationView.setItemChecked(2, true);

                        if (homeActivity.isFromGCM) {
                            Dunkin_Log.e("msgtype", "" + homeActivity.msgType);
                            if (homeActivity.msgType == 8 || homeActivity.msgType == 14) {
                                navigateAndCheckItem(AppConstants.MENU_WALLET);
                            } else if (homeActivity.msgType == AppConstants.MENU_FEEDBACK) {
                                navigateAndCheckItem(AppConstants.MENU_FEEDBACK);
//                            } else if (msgType == 12)
//                            {
//                                getSupportFragmentManager().beginTransaction().replace(R.id.content, new NotificationFragment()).commitAllowingStateLoss();
                            } else
                                navigateAndCheckItem(AppConstants.MENU_NOTIFICATIONS);
                        } else
                            navigateAndCheckItem(switchPosition);
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(homeActivity, jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            //2
            AppController.getUserProfile(homeActivity, AppUtils.getAppPreference(homeActivity).getString(AppConstants.USER_EMAIL_ADDRESS, ""), true
                    , new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 1) {
                                homeActivity.dbAdapter.open();
                                homeActivity.dbAdapter.addOfflineData(AppConstants.OF_PROFILE, (String) result);
                                homeActivity.dbAdapter.close();
                            } else if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(homeActivity, jsonResponse.getString("message"));
                            } else {
                                AppUtils.showToastMessage(homeActivity, getString(R.string.system_error));
                            }
                            Dunkin_Log.e("res", (String) result);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateAndCheckItem(int position, final int... currentVal) {
        for (int i = 0; i < navigationList.size(); i++) {
            NavDrawerModel nav = navigationList.get(i);
            if (position == AppConstants.MENU_HOME) {
//                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_HOME, null, null),
//                        AppConstants.MENU_HOME,
//                        navigationView.getAdapter().getItemId(AppConstants.MENU_HOME));
//                break;
            } else if (position == AppConstants.MENU_SCRATCH_CARD) {
                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_SCRATCH_CARD, null, null),
                        AppConstants.MENU_SCRATCH_CARD,
                        navigationView.getAdapter().getItemId(AppConstants.MENU_SCRATCH_CARD));
                break;
            } else if (position == AppConstants.MENU_NOTIFICATIONS) {
//                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_NOTIFICATIONS, null, null),
//                        AppConstants.MENU_NOTIFICATIONS,
//                        navigationView.getAdapter().getItemId(AppConstants.MENU_NOTIFICATIONS));

                if (homeActivity.msgType == 12) {
                    position = position;
                } else {
                    position = position + 1;
                }

                NavDrawerModel navModel = (NavDrawerModel) navigationView.getAdapter().getItem(position);

                navigationView.setItemChecked(position, true);
                mNavItemId = navModel.getNavId();
                navigate(mNavItemId, currentVal);
                break;
            } else if (position == AppConstants.MENU_WALLET) {
//                NavDrawerModel navModel =navigationList.get(AppConstants.MENU_WALLET);
//
//                navigationView.setItemChecked(position, true);
//                mNavItemId = AppConstants.MENU_WALLET;
                navigate(AppConstants.MENU_WALLET, 0);

//                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_WALLET, null, null),
//                        AppConstants.MENU_WALLET,
//                        navigationView.getAdapter().getItemId(AppConstants.MENU_WALLET));
                break;
            } else if (position == AppConstants.MENU_CONTACT_US) {
                navigationView.performItemClick(navigationView.getAdapter().getView(AppConstants.MENU_CONTACT_US, null, null),
                        AppConstants.MENU_CONTACT_US,
                        navigationView.getAdapter().getItemId(AppConstants.MENU_CONTACT_US));
                break;
            } else if (position == AppConstants.MENU_FEEDBACK) {
                homeActivity.addFragment(new FeedbackFragment(), "FeedBack");
                break;
            } else if (position == AppConstants.MENU_RATE_STAFF) {
                homeActivity.addFragment(new RateStaffFragment(), "RateStaff");
                break;
            } else if (position == AppConstants.MENU_NEWS_EVENT && nav.getNavId() == position) {
                NavDrawerModel navModel = (NavDrawerModel) navigationView.getAdapter().getItem(i + 1);

                navigationView.setItemChecked(i + 1, true);
                mNavItemId = navModel.getNavId();
                navigate(mNavItemId, currentVal);
                break;
            } else {
                if (nav.getNavId() == position) {
                    Dunkin_Log.i("DATA", "" + i);
                    navigationView.performItemClick(navigationView.getAdapter().getView(i + 1, null, null),
                            i + 1,
                            navigationView.getAdapter().getItemId(i + 1));
                    break;
                }
            }
        }
    }

    public void navigate(final int itemId, int... clickVal) {
        if (RotatingBannerFragment.timer != null) {
            try {
                RotatingBannerFragment.timer.cancel();
                RotatingBannerFragment.timer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        homeActivity.loadBanner();

        switch (itemId) {
            case AppConstants.MENU_HOME:
                homeActivity.addFragment(homeActivity.newHomeFragment, "Home");
                break;
            case AppConstants.MENU_GIFT_APPINESS:
                homeActivity.addFragment(new GiftAppinessFragment(), "Gift");
                break;
            case AppConstants.MENU_OFFER:
                homeActivity.addFragment(homeActivity.offerFragment, "Offer");
                break;
            case AppConstants.MENU_NEWS_EVENT:
                NewsPagerFragment newsPagerFragment = new NewsPagerFragment();
                Bundle b = new Bundle();
                b.putStringArrayList("titles", newsFragmentTitles);
                b.putInt("pos", clickVal[0]);
                newsPagerFragment.setArguments(b);
                homeActivity.addFragment(newsPagerFragment, "News");
                break;

            case AppConstants.MENU_PRODUCTS:
                homeActivity.addFragment(new CategoryFragment(), "Category");
                break;

            case AppConstants.MENU_ORDER_HISTORY:
                homeActivity.addFragment(new OrderHistoryFragment(), "OrderHistory");
                break;

            case AppConstants.MENU_RECURRENT_ORDER:
                homeActivity.addFragment(new RecurrentOrderFragment(), "RecurrentOrder");
                break;

            case AppConstants.MENU_NOTIFICATIONS:
                homeActivity.addFragment(new NotificationFragment(), "Notification");
                break;

            case AppConstants.MENU_GIFT_STORE:
                homeActivity.addFragment(new RedeemFragment(), "Redeem");
                break;

            case AppConstants.MENU_SCRATCH_CARD:
                homeActivity.addFragment(new ScratchCardFragment(), "ScratchCard");
                break;

            case AppConstants.MENU_NEARBY_RESTAURANT:
                homeActivity.addFragment(new NearRestaurantFragment(), "NearRestaurant");
                break;

            case AppConstants.MENU_FEEDBACK:
                homeActivity.addFragment(new FeedbackFragment(), "Feedback");
                break;

            case AppConstants.MENU_RATE_STAFF:
                homeActivity.addFragment(new RateStaffFragment(), "RateStaff");
                break;

            case AppConstants.MENU_GET_BILL:
                homeActivity.addFragment(new GetBillFragment(), "GetBill");
                break;

            case AppConstants.MENU_WALLET:
                homeActivity.addFragment(new MyWalletFragment(), "MyWallet");
                break;

            case AppConstants.MENU_CREDIT_CARD:
                homeActivity.addFragment(new MyCreditCardFragment(), "MyCredit");
                break;

            case AppConstants.MENU_PROFILE:
                homeActivity.addFragment(new MyProfileFragment(), "MyProfile");
                break;

            case AppConstants.MENU_CART:
                homeActivity.addFragment(new CartFragment(), "Cart");
                break;

            case AppConstants.MENU_SETTINGS:
                startActivityForResult(new Intent(homeActivity, com.dunkin.customer.SettingActivity.class), 301);
                break;

            case AppConstants.MENU_CONTACT_US:
                homeActivity.addFragment(new ContactUsFragment(), "ContactUs");
                break;

            case AppConstants.MENU_ABOUT_US:
                homeActivity.addFragment(new AboutUsFragmentNew(), "AboutUs");
                break;

            case AppConstants.MENU_LOGOUT:
                showDialogBox(getString(R.string.al_exit_message), getString(R.string.al_warning));
                break;

            case AppConstants.MENU_SCAN_AND_WIN:
                homeActivity.addFragment(new ScanFragment(), "Scan");
                break;

            default:
                break;
        }
    }

    private void showDialogBox(String message, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(homeActivity);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setPositiveButton(getString(R.string.al_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                try {
                    AppController.logoutUser(homeActivity, true, new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            if (jsonResponse.getInt("success") == 1) {
                                AppUtils.showToastMessage(homeActivity, getString(R.string.msg_logout_success));
                                SharedPreferences.Editor editor = AppUtils.getAppPreference(homeActivity).edit();
                                String gcm = AppUtils.getAppPreference(homeActivity).getString(AppConstants.GCM_TOKEN_ID, "");
                                editor.clear();
                                editor.putString(AppConstants.GCM_TOKEN_ID, gcm);
                                editor.apply();
                                CustomerApplication.setLocale(new Locale(AppConstants.LANG_EN));
                                homeActivity.finish();
                                startActivity(new Intent(homeActivity, com.dunkin.customer.RegisterActivity.class));
                            } else if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(homeActivity, jsonResponse.getString("message"));
                            } else {
                                AppUtils.showToastMessage(homeActivity, getString(R.string.msg_logout_failure));
                            }
                        }
                    });
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
//                    AppUtils.showToastMessage(homeActivity, getString(R.string.system_error));
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
}
