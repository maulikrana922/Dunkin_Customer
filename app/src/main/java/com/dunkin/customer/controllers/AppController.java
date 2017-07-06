package com.dunkin.customer.controllers;

import android.content.Context;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.constants.URLConstant;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.models.RestaurantModel;
import com.dunkin.customer.models.ToppingModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SuppressWarnings("ALL")
public class AppController {

    //GET SPLASH SCREEN IMAGE
    public static void getSplashImage(Context context, Callback callback) throws IOException, JSONException {
        if (AppUtils.checkNetwork(context) != 0) {
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_SPLASH_SCREEN_URL, null, true, callback);
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            String result = dbAdapter.getOfflineData(AppConstants.OF_SPLASH);
            dbAdapter.close();
            callback.run(result);
        }
    }

    //GET ROTATING BANNER
    public static void getRotatingBanner(Context context, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //  Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_BANNERS, se, false, callback);
    }

    //POST CUSTOMER DATA
    public static void RegisterCustomer(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //  Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.REGISTER_CUSTOMER_URL, se, true, callback);
    }

    //UPDATE UDUD FOR USER
    public static void updateUDIDForUser(String requestString, Callback callback) throws UnsupportedEncodingException {
        // Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(URLConstant.UPDATE_UDID_URL, se, callback);
    }

    //NEAR RESTAURANTS ON MAP
    public static void nearByRestaurantsOnMap(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        // Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.NEAR_BY_RESTAURANTS, se, false, callback);
    }

    //CHECK SCAN AND WIN
    public static void getScan(Context context, Callback callback) throws UnsupportedEncodingException, JSONException {
        // Log.e("DataRequest", requestString);
//        JSONObject jsonRequest = new JSONObject();
//        jsonRequest.put("user_id", "34772");
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity("", AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.SCAN_AND_WIN, se, false, callback);
    }

    //NEAR RESTAURANTS ON MAP
    public static void checkAndScanWin(Context context, String requestString, int country_id,
                                       String email, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Log.e("DataRequest", requestString);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", email);
        jsonRequest.put("qrscan", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.CHECK_AND_WIN, se, false, callback);
    }

    // LOGIN CUSTOMER
    public static void loginUser(Context context, String email, String password, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", email);
        jsonRequest.put("password", password);
        jsonRequest.put("is_device_android", 1);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        jsonRequest.put("udid", AppUtils.getAppPreference(context).getString(AppConstants.GCM_TOKEN_ID, ""));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.LOGIN_CUSTOMER_URL, se, true, callback);
    }

    // FORGOT PASSWORD
    public static void forgotPassword(Context context, String email, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", email);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.FORGOT_PASSWORD_URL, se, true, callback);
    }

    //GET USER PROFILE
    public static void getUserProfile(Context context, String email, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_USER_PROFILE, se, isLoading, callback);
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            String result = dbAdapter.getOfflineData(AppConstants.OF_PROFILE);
            try {
                callback.run(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //GET USER PROFILE
    public static void getUserProfileFragment(Context context, String email, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(AppConstants.OF_PROFILE, context, URLConstant.GET_USER_PROFILE, se, isLoading, callback);
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            String result = dbAdapter.getOfflineData(AppConstants.OF_PROFILE);
            try {
                callback.run(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // LOGOUT USER PROFILE
    public static void logoutUser(Context context, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("udid", "");
        jsonRequest.put("type", 1);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.LOGOUT_USER_URL, se, isLoading, callback);
    }

    // LOGOUT USER PROFILE
    public static void logoutUser(Context context, int isDirectExit, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("udid", "");
        jsonRequest.put("type", 1);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        jsonRequest.put("isDirectExit", isDirectExit);
        //  Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(URLConstant.LOGOUT_USER_URL, se, callback);
    }

    //POST UPDATED USER PROFILE
    public static void postUpdatedData(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //  Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.UPDATE_USER_PROFILE, se, true, callback);
    }

    //GET EVENTS LIST
    public static void getEventsList(Context context, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_EVENTS_LIST, se, false, callback);
    }

    //GET EVENT DETAIL
    public static void getEventDetail(Context context, int eventId, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("eventId", eventId);
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_EVENT_DETAIL, se, false, callback);
    }

    //GET GIFTS LIST
    public static void getGiftsList(Context context, RestaurantModel restaurantModel, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("restaurant_id", restaurantModel.getRestaurantId());
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_GIFTS_LIST, se, true, callback);
    }

    //GET OFFERS LIST
    public static void getOffersList(Context context, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_OFFER_LIST, se, false, callback);
    }

    //GET OFFER DETAIL
    public static void getOfferDetail(Context context, int offerId, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("offerId", offerId);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_OFFER_DETAIL, se, false, callback);
    }

    //GET RATING LIST
    public static void getRatingList(Context context, int restaurantId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("restaurantId", restaurantId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_RATING_LIST, se, false, callback);
    }

    public static void postComments(Context context, String requestString, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.POST_COMMENTS, se, true, callback);
    }

    //POST RESERVE DATA TO SERVER
    public static void postGiftData(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.POST_RESERVATION, se, true, callback);
    }

    //GET RESTAURENT LIST
    public static void getRestaurantList(Context context, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_RESTAURANT_LIST, se, isLoading, callback);
    }

    //GET RESTAURENT LIST
    public static void getRestaurantListRegister(Context context, int countryId, String langFlag, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", langFlag);
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_RESTAURANT_LIST, se, isLoading, callback);
    }

    //GET NEWS LIST
    public static void getNewsList(Context context, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_NEWS_LIST, se, false, callback);
    }

    //GET NEWS DETAIL
    public static void getnewsDetail(Context context, int newsId, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("newsId", newsId);
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_NEWS_DETAIL, se, false, callback);
    }

    //GET BILL
    public static void getBill(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //   Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_BILL, se, false, callback);
    }

    //PAY AMOUNT BY CUSTOMER
    public static void payAmountByCustomer(Context context, JSONObject jsonRequest, Callback callback) throws UnsupportedEncodingException {
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.PAY_AMOUNT_BT_CUSTOMER, se, true, callback);
    }

    // GET CELEBRATION LIST
    public static void getCelebrationList(Context context, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CELEBRATION_LIST, se, false, callback);
    }

    //GET CELEBRATION DETAIL

    public static void getCelebrationDetail(Context context, int celebrationId, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("celebrationId", celebrationId);
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //  Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CELEBRATION_DETAIL, se, false, callback);
    }

    //GET CATAGORY LIST
    public static void getCategoryList(Context context, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("parent", 0);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CATEGORY_LIST, se, false, callback);
    }

    //GET PRODUCTS FROM CATAGORYID
    public static void getProductsFromCatagory(Context context, int categoryId, int country_id, int child_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("categoryId", categoryId);
        jsonRequest.put("child_id", child_id);
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PRODUCT_FROM_CATEGORY, se, false, callback);
    }

    //GET PRODUCT DETAIL
    public static void getProductDetail(Context context, int productid, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("productId", productid);
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PRODUCT_DETAIL, se, false, callback);
    }

    //GET REEDEEM POINTS
    public static void getRedeemPoints(Context context, String email, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_REDEEM_PONTS, se, false, callback);
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            String result = dbAdapter.getOfflineData(AppConstants.OF_POINTS);
            dbAdapter.close();
            try {
                callback.run(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // POST CONTACT FORM DATA
    public static void postContactUs(Context context, String email, String phone, String name, String description, Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
            jsonObject.put("message", description);
            jsonObject.put("name", name);
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            // Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.CONTACT_US, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ADD NEW CREDIT CARD
    public static void registerNewCard(Context context, String cardNumber, String expiry, String cardHolderName, Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cardNumber", cardNumber);
            jsonObject.put("cardHolderName", cardHolderName);
            jsonObject.put("expiry", expiry);
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //  Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.REGISTER_NEW_CARD, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CARD LISTING
    public static void getCardList(Context context, Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //  Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_CARD_LIST, se, false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //GET MY WALLET
    public static void getMyWallet(Context context, Callback callback) throws UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
                jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
                jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
                //   Log.e("DataRequest", jsonObject.toString());
                org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
                AppUtils.requestCallAsyncTask(context, URLConstant.GET_MY_WALLET, se, false, callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            String result = dbAdapter.getOfflineData(AppConstants.OF_WALLET);
            dbAdapter.close();
            try {
                callback.run(result);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    //GET ORDER LIST
    public static void getOrderList(Context context, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            //Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_ORDER_HISTORY, se, false, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // GET ORDER DETAIL
    public static void getOrderDetail(Context context, String orderId, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId", orderId);
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //  Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_ORDER_DETAIL, se, false, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // GET RECURRENT ORDER DETAIL
    public static void getRecurrentOrderDetail(Context context, String recurrentOrderId, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reference_id", recurrentOrderId);
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            // Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_RECURRENT_ORDER_DETAIL_URL, se, false, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // CANCEL RECURRENT ORDER
    public static void cancelRecurrentOrder(Context context, String recurrentOrderId, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reference_id", recurrentOrderId);
            // Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.CANCEL_RECURRENT_ORDER_URL, se, true, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // EDIT CARD DETAIL
    public static void editCardDetail(Context context, int cardId, String cardNumber, String expiry, String cardHolderName, Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", cardId);
            jsonObject.put("cardNumber", cardNumber);
            jsonObject.put("cardHolderName", cardHolderName);
            jsonObject.put("expiry", expiry);
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //  Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.EDIT_CARD_DETAIL, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // REMOVE CARD
    public static void removeCard(Context context, int id, Callback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //  Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.REMOVE_CREDIT_CARD, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //INSERT COUNTRY
    public static void getCountryList(Context context, Callback callback) throws UnsupportedEncodingException {
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_COUNTRY_LIST, null, false, callback);
    }

    // POST ORDER
    public static void postOrder(Context context, JSONObject jsonRecurring, String time, int restaurantId, String billingAddress, String shippingAddress, double total, int isDelivery, int isTodayOrder, List<ProductModel> cartList, Callback callback) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonRequest.put("shippingAddress", shippingAddress);
            jsonRequest.put("billingAddress", billingAddress);
            jsonRequest.put("orderAmount", total);
            jsonRequest.put("is_delivery", isDelivery);
            jsonRequest.put("is_online", 1);
            jsonRequest.put("is_device_android", 1);
            jsonRequest.put("orderToday", isTodayOrder);

            if (restaurantId == 0 || restaurantId == -1)
                jsonRequest.put("restaurant_id", 0);
            else
                jsonRequest.put("restaurant_id", restaurantId);

            /*if (date == null)
                jsonRequest.put("orderDate", "");
            else
                jsonRequest.put("orderDate", date);*/

            if (time == null)
                jsonRequest.put("orderTime", "");
            else
                jsonRequest.put("orderTime", time);

            jsonRequest.put("recurring", jsonRecurring);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

            JSONArray jArray = new JSONArray();
            for (ProductModel cart : cartList) {
                JSONObject json = new JSONObject();
                json.put("productId", cart.getProductId());
                json.put("qty", cart.getQty());
                json.put("productPrice", cart.getProductPrice());
                json.put("subTotal", cart.getQty() * cart.getProductPrice());

                JSONArray jsonArray = new JSONArray();
                for (ToppingModel tm : cart.getToppings()) {
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("toppingId", tm.getToppingId());
                    jsonObj.put("toppingPrice", tm.getToppingPrice());
                    jsonArray.put(jsonObj);
                }
                json.put("topping", jsonArray);

                jArray.put(json);
            }
            jsonRequest.put("orderItems", jArray);
            //  Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.MAKE_ONLINE_ORDER, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET SCRATCH CARD
    public static void getScratchCard(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_SCRATCH_CARD, se, false, callback);
    }

    // ADD REDEEMING POINT IN CUSTOMER ACCOUNT.
    public static void addRedeemPoints(Context context, String points, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("points", points);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.ADD_REDEEM_POINT, se, true, callback);
    }

    // GET SCRATCH FIRST CHANCE
    public static void getScratFirstchCard(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_FIRST_SCRATCH_CARD, se, false, callback);
    }

    // GET MENU LIST
    public static void getMenuList(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
//            jsonRequest.put("user_id", "34772");
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_MENU_LIST_URL, se, true, callback);
        } else {
            DBAdapter dbAdapter = new DBAdapter(context);
            dbAdapter.open();
            String result = dbAdapter.getOfflineData(AppConstants.OF_MENU);
            dbAdapter.close();
            try {
                callback.run(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //ABOUT US
    public static void aboutUSWS(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.ABOUT_US_URL, se, true, callback);
    }

    //SOCIAL URLS
    public static void getSocialUrls(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.SOCIAL_URLS, se, true, callback);
    }

    // GET COUNTER ORDER
    public static void getCounterOrder(Context context, int orderId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("counter_orderid", orderId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.COUNTER_ORDER_DETAIL, se, false, callback);
    }

    // MAKE PAYMENT FOR COUNTER ORDER
    public static void makePaymentForCounterOrder(Context context, JSONObject jsonRequest, Callback callback) throws UnsupportedEncodingException {
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.MAKE_PAYMENT_FOR_COUNTER_ORDER, se, true, callback);
    }

    // GET HOME PAGE DATA
    public static void getHomePageData(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.HOME_PAGE_URL, se, true, callback);
    }

    // OFFER PURCHASE DETAIL
    public static void offerPurchaseDetail(Context context, JSONObject jsonRequest, Callback callback) {
        try {
            //   Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.OFFER_PURCHASE_DETAIL_URL, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MAKE OFFER PAYMENT
    public static void payOffer(Context context, JSONObject jsonRequest, Callback callback) {
        try {
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.MAKE_OFFER_PAYMENT_URL, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET OFFER HISTORY
    public static void getOfferHistory(Context context, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.OFFER_HISTORY_URL, se, false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET ALL NOTIFICATION OF USER
    public static void getNotifications(Context context, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_NOTIFICATION_URL, se, false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // REMOVE ALL NOTIFICATION
    public static void removeAllNotifications(Context context, JSONObject jsonObject, Callback callback) {
        try {
            // Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.REMOVE_ALL_NOTIFICATION_URL, se, true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE NOTIFICATION IS READ
    public static void updateIsReadNotification(Context context, int notificationId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("notification_id", notificationId);
        // Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.UPDATE_IS_NOTIFICATION_READ_URL, se, false, callback);
    }

    // GET REDEEM POINT HISTORY
    public static void getRedeemPointHistory(Context context, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            //  Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_REDEEM_POINTS_HISTORY_URL, se, false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET GIFT DETAIL
    public static void getGiftDetail(Context context, int giftOrderId, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("giftorderId", giftOrderId);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_GIFT_DETAIL_URL, se, false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET RECURRENT ORDER LIST
    public static void getRecurrentOrderList(Context context, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
            // Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_RECURRENT_ORDER_LIST_URL, se, false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getPlayList(Context context, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
//            jsonObject.put("page", "1");
            //  Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_PLAY_LIST, se, true, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getPromoTicket(Context context, String promo_id, int qty, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        jsonRequest.put("promo_id", promo_id);
        jsonRequest.put("qty", qty);
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PROMO_TICKET, se, true, callback);
    }


    public static void fetchMyPromoTicket(Context context, String promo_id, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
//            jsonObject.put("page", "1");
            jsonObject.put("promo_id", promo_id);
            //   Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_MY_PROMO_TICKET, se, true, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fetchPromoImage(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Log.e("DataRequest", requestString);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.FETCH_PROMO_IMAGE, se, true, callback);
    }

    public static void redeemPromoCode(Context context, String promo_id, int country_id,
                                       String email, String latitude, String longitude, Callback callback)
            throws JSONException, UnsupportedEncodingException {
        // Log.e("DataRequest", requestString);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", email);
        jsonRequest.put("promo_id", promo_id);
        jsonRequest.put("latitude", latitude);
        jsonRequest.put("longitude", longitude);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.REDEEM_PROMO, se, true, callback);
    }

    //GET OFFER DETAIL
    public static void getPromoDetail(Context context, int promoId, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("promo_id", promoId);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", country_id);
        //Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PROMO_DETAIL, se, true, callback);
    }

    public static void fetchAllSetting(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //  Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_SETTING, se, true, callback);
    }
}
