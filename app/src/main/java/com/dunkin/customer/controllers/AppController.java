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
        //  Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_BANNERS, jsonRequest.toString(), false, callback);
    }

    //POST CUSTOMER DATA
    public static void RegisterCustomer(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //  Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.REGISTER_CUSTOMER_URL, requestString, true, callback);
    }

    //UPDATE UDUD FOR USER
    public static void updateUDIDForUser(String requestString, Callback callback) throws UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(URLConstant.UPDATE_UDID_URL, requestString, callback);
    }

    //NEAR RESTAURANTS ON MAP
    public static void nearByRestaurantsOnMap(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.NEAR_BY_RESTAURANTS, requestString, false, callback);
    }

    //CHECK SCAN AND WIN
    public static void getScan(Context context, Callback callback) throws UnsupportedEncodingException, JSONException {
        // Dunkin_Log.e("DataRequest", requestString);
//        JSONObject jsonRequest = new JSONObject();
//        jsonRequest.put("user_id", "34772");
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity("", AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.SCAN_AND_WIN, "", false, callback);
    }

    //NEAR RESTAURANTS ON MAP
    public static void checkAndScanWin(Context context, String requestString, int country_id,
                                       String email, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", email);
        jsonRequest.put("qrscan", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.CHECK_AND_WIN, jsonRequest.toString(), false, callback);
    }

    // LOGIN CUSTOMER
    public static void loginUser(Context context, String email, String password, Callback callback) throws JSONException, UnsupportedEncodingException {

        ApiParamloginUser apiParamloginUser =new ApiParamloginUser();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(apiParamloginUser.getEmail(), email);
        jsonRequest.put(apiParamloginUser.getPassword(), password);
        jsonRequest.put(apiParamloginUser.is_device_android, 1);
        jsonRequest.put(apiParamloginUser.lang_flag, AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        jsonRequest.put(apiParamloginUser.udid, AppUtils.getAppPreference(context).getString(AppConstants.GCM_TOKEN_ID, ""));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, new URLConstant().getLOGIN(), jsonRequest.toString(), true, callback);
    }

    // SHARE POINT
    public static void sharepoint(Context context, String email, String country_id,String point, String userscan,String type,String message, Callback callback) throws JSONException, UnsupportedEncodingException {
        ApiParamloginUser apiParamloginUser =new ApiParamloginUser();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(apiParamloginUser.getEmail(), email);
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("point", point);
        jsonRequest.put("userscan", userscan);
        jsonRequest.put("type", type);
        jsonRequest.put("message", message);

        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.POINT_SHARE, jsonRequest.toString(), true, callback);
    }

    // FORGOT PASSWORD
    public static void forgotPassword(Context context, String email, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", email);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.FORGOT_PASSWORD_URL, jsonRequest.toString(), true, callback);
    }

    //GET USER PROFILE
    public static void getUserProfile(Context context, String email, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            //Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_USER_PROFILE, jsonRequest.toString(), isLoading, callback);
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
            //Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(AppConstants.OF_PROFILE, context, URLConstant.GET_USER_PROFILE, jsonRequest.toString(), isLoading, callback);
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
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.LOGOUT_USER_URL, jsonRequest.toString(), isLoading, callback);
    }

    // LOGOUT USER PROFILE
    public static void logoutUser(Context context, int isDirectExit, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("udid", "");
        jsonRequest.put("type", 1);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        jsonRequest.put("isDirectExit", isDirectExit);
        //  Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(URLConstant.LOGOUT_USER_URL, jsonRequest.toString(), callback);
    }

    //POST UPDATED USER PROFILE
    public static void postUpdatedData(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //  Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.UPDATE_USER_PROFILE, requestString, true, callback);
    }

    //GET EVENTS LIST
    public static void getEventsList(Context context, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_EVENTS_LIST, jsonRequest.toString(), false, callback);
    }

    //GET EVENT DETAIL
    public static void getEventDetail(Context context, int eventId, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("eventId", eventId);
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_EVENT_DETAIL, jsonRequest.toString(), false, callback);
    }

    //GET GIFTS LIST
    public static void getGiftsList(Context context, RestaurantModel restaurantModel, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("restaurant_id", restaurantModel.getRestaurantId());
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_GIFTS_LIST, jsonRequest.toString(), true, callback);
    }

    //GET ALL GIFTS LIST
    public static void getAllGiftsList(Context context, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_ALL_GIFTS_LIST, jsonRequest.toString(), true, callback);
    }

    public static void getWeatherInfo(Context context, Callback callback) throws UnsupportedEncodingException, JSONException {
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity("", AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.SCAN_AND_WIN, "", false, callback);
    }

    //GET OFFERS LIST
    public static void getOffersList(Context context, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_OFFER_LIST, jsonRequest.toString(), false, callback);
    }

    //GET OFFER DETAIL
    public static void getOfferDetail(Context context, int offerId, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("offerId", offerId);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_OFFER_DETAIL, jsonRequest.toString(), false, callback);
    }

    //GET RATING LIST
    public static void getRatingList(Context context, int restaurantId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("restaurantId", restaurantId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_RATING_LIST, jsonRequest.toString(), false, callback);
    }

    //GET RATING LIST
    public static void getCatalogQuestionList(Context context, int restaurantId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CATALOG_QUESTION_LIST, jsonRequest.toString(), false, callback);
    }

    public static void postComments(Context context, String requestString, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.POST_COMMENTS, requestString, true, callback);
    }

    public static void putStaffAssessment(Context context, String requestString, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.PUT_STAFF_ASSESSMENT, requestString, true, callback);
    }

    //POST RESERVE DATA TO SERVER
    public static void postGiftData(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.POST_RESERVATION, requestString, true, callback);
    }

    //GET RESTAURENT LIST
    public static void getRestaurantList(Context context, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_RESTAURANT_LIST, jsonRequest.toString(), isLoading, callback);
    }

    //GET RESTAURENT LIST
    public static void getRestaurantListRegister(Context context, int countryId, String langFlag, boolean isLoading, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", langFlag);
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_RESTAURANT_LIST, jsonRequest.toString(), isLoading, callback);
    }

    //GET NEWS LIST
    public static void getNewsList(Context context, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_NEWS_LIST, jsonRequest.toString(), false, callback);
    }

    //GET NEWS DETAIL
    public static void getnewsDetail(Context context, int newsId, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("newsId", newsId);
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_NEWS_DETAIL, jsonRequest.toString(), false, callback);
    }

    //GET BILL
    public static void getBill(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //   Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_BILL, jsonRequest.toString(), false, callback);
    }

    //PAY AMOUNT BY CUSTOMER
    public static void payAmountByCustomer(Context context, JSONObject jsonRequest, Callback callback) throws UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.PAY_AMOUNT_BT_CUSTOMER, jsonRequest.toString(), true, callback);
    }

    // GET CELEBRATION LIST
    public static void getCelebrationList(Context context, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CELEBRATION_LIST, jsonRequest.toString(), false, callback);
    }

    //GET CELEBRATION DETAIL

    public static void getCelebrationDetail(Context context, int celebrationId, int countryId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("celebrationId", celebrationId);
        jsonRequest.put("country_id", countryId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //  Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CELEBRATION_DETAIL, jsonRequest.toString(), false, callback);
    }

    //GET CATAGORY LIST
    public static void getCategoryList(Context context, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("parent", 0);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_CATEGORY_LIST, jsonRequest.toString(), false, callback);
    }

    //GET PRODUCTS FROM CATAGORYID
    public static void getProductsFromCatagory(Context context, int categoryId, int country_id, int child_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("categoryId", categoryId);
        jsonRequest.put("child_id", child_id);
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PRODUCT_FROM_CATEGORY, jsonRequest.toString(), false, callback);
    }

    //GET PRODUCT DETAIL
    public static void getProductDetail(Context context, int productid, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("productId", productid);
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PRODUCT_DETAIL, jsonRequest.toString(), false, callback);
    }

    //GET REEDEEM POINTS
    public static void getRedeemPoints(Context context, String email, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", email);
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_REDEEM_PONTS, jsonRequest.toString(), false, callback);
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
            // Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.CONTACT_US, jsonObject.toString(), true, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.REGISTER_NEW_CARD, jsonObject.toString(), true, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_CARD_LIST, jsonObject.toString(), false, callback);
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
                //   Dunkin_Log.e("DataRequest", jsonObject.toString());
                org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
                AppUtils.requestCallAsyncTask(context, URLConstant.GET_MY_WALLET, jsonObject.toString(), false, callback);
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
            //Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_ORDER_HISTORY, jsonObject.toString(), false, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_ORDER_DETAIL, jsonObject.toString(), false, callback);
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
            // Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_RECURRENT_ORDER_DETAIL_URL, jsonObject.toString(), false, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // CANCEL RECURRENT ORDER
    public static void cancelRecurrentOrder(Context context, String recurrentOrderId, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("reference_id", recurrentOrderId);
            // Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.CANCEL_RECURRENT_ORDER_URL, jsonObject.toString(), true, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.EDIT_CARD_DETAIL, jsonObject.toString(), true, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.REMOVE_CREDIT_CARD, jsonObject.toString(), true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //INSERT COUNTRY
    public static void getCountryList(Context context, Callback callback) throws UnsupportedEncodingException {
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_COUNTRY_LIST, "", false, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.MAKE_ONLINE_ORDER, jsonRequest.toString(), true, callback);
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
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_SCRATCH_CARD, jsonRequest.toString(), false, callback);
    }

    // ADD REDEEMING POINT IN CUSTOMER ACCOUNT.
    public static void addRedeemPoints(Context context, String points, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("putConsumePoint", points);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, new URLConstant().getConsume(), jsonRequest.toString(), true, callback);
    }

    // GET SCRATCH FIRST CHANCE
    public static void getScratFirstchCard(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_FIRST_SCRATCH_CARD, jsonRequest.toString(), false, callback);
    }

    // GET MENU LIST
    public static void getMenuList(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        if (AppUtils.checkNetwork(context) != 0) {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
//            jsonRequest.put("userid", "34772");
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_MENU_LIST_URL, jsonRequest.toString(), true, callback);
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
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.ABOUT_US_URL, jsonRequest.toString(), true, callback);
    }

    //SOCIAL URLS
    public static void getSocialUrls(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.SOCIAL_URLS, jsonRequest.toString(), true, callback);
    }

    // GET COUNTER ORDER
    public static void getCounterOrder(Context context, int orderId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("counter_orderid", orderId);
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.COUNTER_ORDER_DETAIL, jsonRequest.toString(), false, callback);
    }

    // MAKE PAYMENT FOR COUNTER ORDER
    public static void makePaymentForCounterOrder(Context context, JSONObject jsonRequest, Callback callback) throws UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.MAKE_PAYMENT_FOR_COUNTER_ORDER, jsonRequest.toString(), true, callback);
    }

    // GET Dashbord PAGE DATA
    public static void getDashbordPageData(Context context,JSONObject jsonRequest, Callback callback) throws JSONException, UnsupportedEncodingException {
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());

        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.DASHBORD_PAGE_URL, jsonRequest.toString(), true, callback);

//        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
//        AppUtils.requestCallAsyncTask(context, URLConstant.DASHBORD_PAGE_URL, se, true, callback);
    }

    // GET HOME PAGE DATA
    public static void getHomePageData(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.HOME_PAGE_URL, jsonRequest.toString(), true, callback);
    }

    // OFFER PURCHASE DETAIL
    public static void offerPurchaseDetail(Context context, JSONObject jsonRequest, Callback callback) {
        try {
            //   Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.OFFER_PURCHASE_DETAIL_URL, jsonRequest.toString(), true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MAKE OFFER PAYMENT
    public static void payOffer(Context context, JSONObject jsonRequest, Callback callback) {
        try {
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.MAKE_OFFER_PAYMENT_URL, jsonRequest.toString(), true, callback);
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
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.OFFER_HISTORY_URL, jsonRequest.toString(), false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET ALL NOTIFICATION OF USER
    public static void getNotifications(Context context, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_NOTIFICATION_URL, jsonRequest.toString(), false, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // REMOVE ALL NOTIFICATION
    public static void removeAllNotifications(Context context, JSONObject jsonObject, Callback callback) {
        try {
            // Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.REMOVE_ALL_NOTIFICATION_URL, jsonObject.toString(), true, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UPDATE NOTIFICATION IS READ
    public static void updateIsReadNotification(Context context, int notificationId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("notification_id", notificationId);
        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.UPDATE_IS_NOTIFICATION_READ_URL, jsonRequest.toString(), false, callback);
    }

    // GET REDEEM POINT HISTORY
    public static void getRedeemPointHistory(Context context, Callback callback) throws JSONException {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
            //  Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_REDEEM_POINTS_HISTORY_URL, jsonRequest.toString(), false, callback);
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
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_GIFT_DETAIL_URL, jsonRequest.toString(), false, callback);
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
            // Dunkin_Log.e("DataRequest", jsonRequest.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_RECURRENT_ORDER_LIST_URL, jsonRequest.toString(), false, callback);
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
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_PLAY_LIST, jsonObject.toString(), true, callback);
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
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PROMO_TICKET, jsonRequest.toString(), true, callback);
    }


    public static void fetchMyPromoTicket(Context context, String promo_id, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
//            jsonObject.put("page", "1");
            jsonObject.put("promo_id", promo_id);
            //   Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_MY_PROMO_TICKET, jsonObject.toString(), true, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void fetchPromoImage(Context context, Callback callback) throws JSONException, UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0));
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, null));
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.FETCH_PROMO_IMAGE, jsonRequest.toString(), true, callback);
    }

    public static void redeemPromoCode(Context context, String promo_id, int country_id,
                                       String email, String latitude, String longitude, Callback callback)
            throws JSONException, UnsupportedEncodingException {
        // Dunkin_Log.e("DataRequest", requestString);
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", email);
        jsonRequest.put("promo_id", promo_id);
        jsonRequest.put("latitude", latitude);
        jsonRequest.put("longitude", longitude);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.REDEEM_PROMO, jsonRequest.toString(), true, callback);
    }

    //GET OFFER DETAIL
    public static void getPromoDetail(Context context, int promoId, int country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("promo_id", promoId);
        jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", country_id);
        //Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_PROMO_DETAIL, jsonRequest.toString(), true, callback);
    }

    public static void fetchAllSetting(Context context, String requestString, Callback callback) throws UnsupportedEncodingException {
        //  Dunkin_Log.e("DataRequest", requestString);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(requestString, AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_SETTING, requestString, true, callback);
    }

    public static void getAboutUsDetail(Context context, String listId, Callback callback) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("listId", listId);
        jsonRequest.put("type", "detail");
        jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_KNOW_LIST, jsonRequest.toString(), true, callback);
    }

    public static void aboutUSNew(Context context, Callback callback) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "list");
            jsonObject.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
            jsonObject.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
//            jsonObject.put("page", "1");
            //  Dunkin_Log.e("DataRequest", jsonObject.toString());
            org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonObject.toString(), AppConstants.encodeType);
            AppUtils.requestCallAsyncTask(context, URLConstant.GET_KNOW_LIST, jsonObject.toString(), true, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // SHARE POINT
    public static void gainsharepoint(Context context, String email, String country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        ApiParamloginUser apiParamloginUser =new ApiParamloginUser();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put(apiParamloginUser.getEmail(), email);
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", email);

        // Dunkin_Log.e("DataRequest", jsonRequest.toString());
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GAIN_POINT_SHIFT, jsonRequest.toString(), true, callback);
    }

    // GET WALLET REDEEM POINT
    public static void getWalletRedeempoint(Context context, String email, String country_id, Callback callback) throws JSONException, UnsupportedEncodingException {
        ApiParamloginUser apiParamloginUser =new ApiParamloginUser();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("country_id", country_id);
        jsonRequest.put("email", email);
        org.apache.http.entity.StringEntity se = new org.apache.http.entity.StringEntity(jsonRequest.toString(), AppConstants.encodeType);
        AppUtils.requestCallAsyncTask(context, URLConstant.GET_WALLET_REDEEM_POINT, jsonRequest.toString(), true, callback);
    }
}
