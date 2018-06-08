package com.dunkin.customer.constants;

import com.dunkin.customer.Utils.CO123QA;

import java.io.IOException;

public class URLConstant {

    String add =  "H4sIAAAAAAAAACvOTM/zTc1NSi3yzAMAIvewSAwAAAA=";
    String consume =  "H4sIAAAAAAAAACsoLXHOzysuzU0NyM/MKwEAWdlxoQ8AAAA=";



    public String getLOGIN() {
        try {
            return CO123QA.getData(add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getConsume() {
        try {
            return CO123QA.getData(consume);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final String ABOUT_US_URL =  "aboutUs";
    public static final String SOCIAL_URLS =  "gainSocialLink";
    public static final String GET_SPLASH_SCREEN_URL =  "gainSplash";
    public static final String GET_BANNERS =  "rotatingBanner";
    public static final String GET_COUNTRY_LIST =  "pickCountry";
    public static final String GET_RESTAURANT_LIST =  "catalogRestaurant";
    public static final String NEAR_BY_RESTAURANTS =  "nearCatalogRestaurant";
    public static final String SCAN_AND_WIN =  "gainScanWin";
    public static final String CHECK_AND_WIN =  "inspectScanWin";
    public static final String UPDATE_UDID_URL =  "improveDetail";
    public static final String PAY_AMOUNT_BT_CUSTOMER =  "billClearance";
    public static final String GET_RATING_LIST =  "gainCommentRating";
    public static final String POST_COMMENTS =  "putComment";
    public static final String POST_RESERVATION =  "putGiftOrder";
    public static final String GET_BILL =  "gainCustomerBill";
    public static final String REGISTER_CUSTOMER_URL =  "regMember";
    public static final String FORGOT_PASSWORD_URL =  "improvePass";
    public static final String POINT_SHARE =  "putPointShift";
    public static final String UPDATE_USER_PROFILE =  "improveMember";
    public static final String LOGOUT_USER_URL =  "signMemberOut";
    public static final String GET_REDEEM_PONTS =  "pickConsumePoint";
    public static final String GET_EVENTS_LIST =  "catalogEvent";
    public static final String GET_EVENT_DETAIL =  "specificEvent";
    public static final String GET_OFFER_LIST =  "catalogOffer";
    public static final String GET_OFFER_DETAIL =  "specificOffer";
    public static final String GET_NEWS_LIST =  "catalogNews";
    public static final String GET_NEWS_DETAIL =  "specificNews";
    public static final String GET_CELEBRATION_LIST =  "catalogCelebration";
    public static final String GET_CELEBRATION_DETAIL =  "specificCelebration";
    public static final String GET_GIFTS_LIST =  "catalogGift";
    public static final String GET_ALL_GIFTS_LIST =  "catalogAllGift";
    public static final String GET_FIRST_SCRATCH_CARD =  "pickGameAttend";
    public static final String GET_CATEGORY_LIST =  "gainCategoryChild";
    public static final String GET_PRODUCT_FROM_CATEGORY =  "gainCategoryByProduct";
    public static final String GET_PRODUCT_DETAIL =  "specificProduct";
    public static final String GET_ORDER_HISTORY =  "pickCatalogOrder";
    public static final String GET_ORDER_DETAIL =  "pickSpecificOrder";
    public static final String GET_RECURRENT_ORDER_DETAIL_URL =  "specificRepeatOrder";
    public static final String CANCEL_RECURRENT_ORDER_URL =  "wipeRepeatOrder";
    public static final String GET_MY_WALLET =  "catalogWallet";
    public static final String GET_USER_PROFILE =  "pickMember";
    public static final String CONTACT_US =  "contactUs";
    public static final String REGISTER_NEW_CARD =  "regCreditCatd";
    public static final String GET_CARD_LIST =  "catalogCard";
    public static final String REMOVE_CREDIT_CARD =  "wipeCard";
    public static final String EDIT_CARD_DETAIL =  "improveCardInfo";
    public static final String MAKE_ONLINE_ORDER =  "putOrder";
    public static final String GET_SCRATCH_CARD =  "pickGame";
//    public static final String GET_MENU_LIST_URL =  "catalogMenuTest";
    public static final String GET_MENU_LIST_URL =  "catalogNewMenu";
    public static final String COUNTER_ORDER_DETAIL =  "specificOrderCounter";
    public static final String MAKE_PAYMENT_FOR_COUNTER_ORDER =  "orderCounterClearance";
    public static final String HOME_PAGE_URL =  "gainDashboard";
    public static final String DASHBORD_PAGE_URL =  "fethAppMessage";
    public static final String OFFER_PURCHASE_DETAIL_URL =  "pickOfferClearance";
    public static final String MAKE_OFFER_PAYMENT_URL =  "offerClearance";
    public static final String OFFER_HISTORY_URL =  "catalogOfferClearance";
    public static final String GET_NOTIFICATION_URL =  "catalogPush";
    public static final String REMOVE_ALL_NOTIFICATION_URL =  "wipePush";
    public static final String UPDATE_IS_NOTIFICATION_READ_URL =  "improvePush";
    public static final String GET_REDEEM_POINTS_HISTORY_URL =  "catalogConsumePoint";
    public static final String GET_GIFT_DETAIL_URL =  "specificGiftOrder";
    public static final String GET_RECURRENT_ORDER_LIST_URL =  "catalogRepeatOrder";
    public static final String GET_PLAY_LIST =  "catalogPromo";
    public static final String GET_PROMO_TICKET =  "catalogPromoTicket";
    public static final String GET_MY_PROMO_TICKET =  "catalogUserPromoTicket";
    public static final String FETCH_PROMO_IMAGE =  "gainPromoImage";
    public static final String REDEEM_PROMO =  "putConsumePromo";
    public static final String GET_PROMO_DETAIL =  "gainPromoCode";
    public static final String GET_SETTING =  "catalogSetting";
    public static final String GET_KNOW_LIST =  "catalogKnowUs";
    public static final String PUT_STAFF_ASSESSMENT =  "putStaffAssessment";
    public static final String GET_CATALOG_QUESTION_LIST =  "catalogQuestion";
    public static final String GAIN_POINT_SHIFT="gainPointShift";
}
