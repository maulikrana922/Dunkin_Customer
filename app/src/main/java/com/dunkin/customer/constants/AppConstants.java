package com.dunkin.customer.constants;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Admin on 7/6/2015.
 */
public class AppConstants {

    public static Context context;
    public static Activity activity;

    public static final String contentType = "text/json";
    public static final String encodeType = "UTF-8";

    public static final String POST = "post";
    public static final String GET = "get";
    public static final String GCM_SENDER_ID = "61784081159"; // OLD
//    public static final String GCM_SENDER_ID = "604439655068"; // OLD
    //public static final String GCM_SENDER_ID = "863509219024"; // NEW
    public static final String USER_EMAIL_ADDRESS = "email";
    public static final String USER_NAME = "name";
    public static final String USER_FIRST_NAME = "firstname";
    public static final String USER_LANGUAGE = "user_language";
    public static final String GCM_TOKEN_ID = "token";
    public static final String USER_COUNTRY = "country";
    public static final String USER_ADDRESS = "address";
    public static final String USER_PHONE = "phone_number";
    public static final String USER_CASEID = "lele";
    public static final String USER_SCAN_RESULT = "scan_result";

    public static final String USER_LAST_NAME = "lastname";
    public static final String USER_FB_ID = "fb_id";
    public static final String USER_DOB = "birthday";

    public static final String USER_PROFILE_QR = "profileqr";
    public static final String USER_BANNER = "payBannerImage";
    public static final String USER_SHIPPING_ADDRESS = "shippingaddress";
    public static final String USER_NAME_VALIDATION = "^([a-zA-Z]+(?:\\.)?(?:(?:'| )[a-zA-Z]+(?:\\.)?)*)$";

    public static final String YYYY_MM = "yyyy-MM";
    public static final String DD_MM_YYYY_SLASH = "dd/MM/yyyy";
    public static final String DD_MM_YYYY_DASH = "dd-MM-yyyy";
    public static final String MM_DD_YYYY_SERVER = "MM-dd-yyyy";
    public static final String MM_DD_YYYY_HH_MM_SS_SERVER = "MM-dd-yyyy HH:mm:ss"; // 05-21-2016 11:30:26
    public static final String YYYY_MM_DD_HH_MM_SS_SERVER = "yyyy-MM-dd HH:mm:ss"; // 2016-05-21 16:25:17
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss"; // 21/03/2016 18:14:47
    public static final String MM_YY = "MM/yyyy";
    public static final String NOTIFICATION_COUNTER = "1";


    public static final int REQUEST_TIME_OUT = 1200000;


    // MENU ITEM POSITION AND ID
    public static final int MENU_HOME = 1;
    public static final int MENU_PROFILE = 2;
    public static final int MENU_NOTIFICATIONS = 3;
    public static final int MENU_WALLET = 4;
    public static final int MENU_CREDIT_CARD = 6;
    public static final int MENU_PRODUCTS = 8;
    public static final int MENU_ORDER_HISTORY = 9;
    public static final int MENU_GET_BILL = 10;
    public static final int MENU_CART = 11;
    public static final int MENU_OFFER = 13;
    public static final int MENU_GIFT_STORE = 14;
    public static final int MENU_NEARBY_RESTAURANT = 15;
    public static final int MENU_SCRATCH_CARD = 17;
    public static final int MENU_NEWS_EVENT = 19;
    public static final int MENU_SETTINGS = 21;
    public static final int MENU_FEEDBACK = 22;
    public static final int MENU_CONTACT_US = 23;
    public static final int MENU_ABOUT_US = 24;
    public static final int MENU_LOGOUT = 25;
    public static final int MENU_RECURRENT_ORDER = 27;
    public static final int MENU_SCAN_AND_WIN = 28;
    public static final int MENU_GIFT_APPINESS = 29;
    public static final int MENU_RATE_STAFF = 30;

    // OFFLINE DATA IDS
    public static final int OF_SPLASH = 1;
    public static final int OF_MENU = 2;
    public static final int OF_PROFILE = 3;
    public static final int OF_WALLET = 4;
    public static final int OF_POINTS = 5;

    // LANGUAGE CODES
    public static final String LANG_EN = "en";
    public static final String LANG_AR = "ar";
    public static final String TIME_OUT = "TIME_OUT";

}
