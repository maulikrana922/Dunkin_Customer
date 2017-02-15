package com.dunkin.customer.constants;

/**
 * Created by Android2 on 7/27/2015.
 */
public class DBConstants {

    public static final String DATABASE_NAME = "CustomerAppDB";

    //TABLES NAME
    public static final String CART_ITEM_TABLE = "cartitems";
    public static final String COUNTRY_TABLE = "countries";
    public static final String TOPPING_TABLE = "toppings";
    public static final String NOTIFICATION_TABLE = "notification";

    public static final String OFFLINE_DATA = "offline_data";

    // COLUMN NAMES PER TABLE........
    public static final String CL_ID = "id";

    //COLUMANS FOR CART ITEMS
    public static final String CL_product_id = "product_id";
    public static final String CL_product_name = "product_name";
    public static final String CL_image_url = "image_url";
    public static final String CL_quantity = "quantity";
    public static final String CL_currency = "currency_code";
    public static final String CL_single_product_price = "unit_price";
    public static final String CL_sub_total = "sub_total";
    public static final String CL_product_toppings = "product_topping";

    //COLUMNS FOR PROFILE DATA JSON
    public static final String CL_offline_id = "offline_id";
    public static final String CL_json_string = "json_string";

    //COLUMANS FOR COUNTRY TABLE
    public static final String CL_country_id = "country_id";
    public static final String CL_country_name = "name";

    // COLUMNS FOR TOPPING TABLE
    public static final String CL_topping_image = "topping_image";
    public static final String CL_topping_name = "topping_name";
    public static final String CL_topping_price = "topping_price";
    public static final String CL_topping_id = "topping_id";
    public static final String CL_topping_relation_id = "relation_id";

    // COLUMN FOR NOTIFICATION TABLE
    public static final String CL_NOTIFICATION_ID = "nt_id";
    public static final String CL_NOTIFICATION_MSG_TYPE = "nt_msg_type";
    public static final String CL_NOTIFICATION_MESSAGE = "nt_msg";
    public static final String CL_NOTIFICATION_ORDER_ID = "nt_order_id";
    public static final String CL_NOTIFICATION_TABLE_ID = "nt_table_id";
    public static final String CL_NOTIFICATION_RESTAURANT_ID = "nt_restaurant_id";
    public static final String CL_NOTIFICATION_REFERENCE_ID = "nt_reference_id";
    public static final String CL_NOTIFICATION_COUNTRY_ID = "nt_country_id";
    public static final String CL_NOTIFICATION_OFFER_ID = "nt_offer_id";
    public static final String CL_NOTIFICATION_IS_VIEW = "nt_is_view";
}
