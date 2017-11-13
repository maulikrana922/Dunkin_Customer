package com.dunkin.customer.DBAdaters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.Utils.Encryptor;
import com.dunkin.customer.constants.DBConstants;
import com.dunkin.customer.models.CountriesModel;
import com.dunkin.customer.models.NotificationModel;
import com.dunkin.customer.models.NotificationResponseModel;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.models.ToppingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DBAdapter {

    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = DBConstants.DATABASE_NAME;
    private static final int DATABASE_VERSION = 1;
    // Todo Notification create and alter table statement
    private static final String CREATE_TABLE_NOTIFICATION =
            "CREATE TABLE " + DBConstants.NOTIFICATION_TABLE + "(" +
                    DBConstants.CL_NOTIFICATION_ID + " INTEGER PRIMARY KEY," +
                    DBConstants.CL_NOTIFICATION_MSG_TYPE + " INTEGER," +
                    DBConstants.CL_NOTIFICATION_MESSAGE + " TEXT," +
                    DBConstants.CL_NOTIFICATION_ORDER_ID + " TEXT," +
                    DBConstants.CL_NOTIFICATION_TABLE_ID + " TEXT," +
                    DBConstants.CL_NOTIFICATION_RESTAURANT_ID + " TEXT," +
                    DBConstants.CL_NOTIFICATION_REFERENCE_ID + " TEXT," +
                    DBConstants.CL_NOTIFICATION_COUNTRY_ID + " TEXT," +
                    DBConstants.CL_NOTIFICATION_OFFER_ID + " TEXT," +
                    DBConstants.CL_NOTIFICATION_IS_VIEW + " INTEGER)";
    public static SQLiteDatabase db;

    private final DatabaseHelper DBHelper;
    public Cursor cr;
    private Context ctx;

    public DBAdapter(Context ctx) {
        this.DBHelper = new DatabaseHelper(ctx);
        this.ctx = ctx;
    }

    // ---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ---closes the database---
    public void close() {
        DBHelper.close();
    }

    public void truncateTables(String tableName) {
        db.execSQL("delete from  " + tableName);
    }

    //ADD JSON DATA INTO DATABASE
    public void addOfflineData(int id, String jsonString) {
        try {
            Cursor cr = db.rawQuery(
                    "select " + DBConstants.CL_offline_id +
                            " from " + DBConstants.OFFLINE_DATA +
                            " where " + DBConstants.CL_offline_id +
                            " = ?", new String[]{String.valueOf(id)});
            ContentValues cv = new ContentValues();
            Dunkin_Log.e("Encrypted Data", "" + Encryptor.encrypt(ctx, jsonString));
            cv.put(DBConstants.CL_json_string, Encryptor.encrypt(ctx, jsonString));
            cv.put(DBConstants.CL_offline_id, id);

            if (cr != null && cr.getCount() > 0 && cr.moveToFirst()) {
                db.update(DBConstants.OFFLINE_DATA, cv, DBConstants.CL_offline_id + " = ?",
                        new String[]{String.valueOf(id)});

            } else {
                db.insert(DBConstants.OFFLINE_DATA, null, cv);
            }
            cr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GET JSON OF OFFLINE DATA BY ID
    public String getOfflineData(int id) {
        try {
            Cursor cr = db.rawQuery(
                    "select * from " + DBConstants.OFFLINE_DATA +
                            " where " + DBConstants.CL_offline_id +
                            " = ?", new String[]{String.valueOf(id)});
            if (cr != null && cr.getCount() > 0 && cr.moveToFirst()) {
                Dunkin_Log.e("Decrypted Data", "" + Encryptor.decrypt(ctx, cr.getString(cr.getColumnIndex(DBConstants.CL_json_string))));
                return Encryptor.decrypt(ctx, cr.getString(cr.getColumnIndex(DBConstants.CL_json_string)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //ADD COUNTRIE INTO DATABASE
    public void addCountries(List<CountriesModel> countryList) {
        truncateTables(DBConstants.COUNTRY_TABLE);
        for (CountriesModel cm : countryList) {
            ContentValues cv = new ContentValues();
            cv.put(DBConstants.CL_country_name, cm.getName());
            cv.put(DBConstants.CL_country_id, cm.getCountry_id());
            db.insert(DBConstants.COUNTRY_TABLE, null, cv);
        }
    }

    //GET ALL COUNTRIES FROM DATABASE
    public List<CountriesModel> getAllCountries() {

        List<CountriesModel> getCountiList = new ArrayList<>();
        cr = db.rawQuery("select * from " + DBConstants.COUNTRY_TABLE, null);
        if (cr.getCount() > 0) {
            if (cr.moveToFirst()) {
                do {
                    CountriesModel cm = new CountriesModel();
                    cm.setCountry_id(cr.getInt(cr.getColumnIndex(DBConstants.CL_country_id)));
                    cm.setName(cr.getString(cr.getColumnIndex(DBConstants.CL_country_name)));
                    getCountiList.add(cm);
                } while (cr.moveToNext());
            }
        }

        return getCountiList;
    }

    //ADD PRODUCT INTO CART
    public void addProductInCart(ProductModel pm, List<ToppingModel> toppingList) {

        // FIRST CHECK WITH SAME TOPPING AND SAME PRODUCTS WE DO HAVE OR NOT IF YES THEN INCREEMENT QTY
        String query;
        if (toppingList != null && toppingList.size() > 0) {

            Integer[] sb = new Integer[toppingList.size()];
            for (int i = 0; i < sb.length; i++) {
                sb[i] = toppingList.get(i).getToppingId();
            }

            query = "select " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_ID +
                    "," + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_quantity +
                    ", group_concat(" + DBConstants.TOPPING_TABLE +
                    "." + DBConstants.CL_topping_id +
                    ") as topps  from " + DBConstants.CART_ITEM_TABLE +
                    " inner join " + DBConstants.TOPPING_TABLE +
                    " on " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_ID +
                    " = " + DBConstants.TOPPING_TABLE +
                    "." + DBConstants.CL_topping_relation_id +
                    " where " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_product_id +
                    "= " + pm.getProductId() +
                    " group by " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_ID;
            cr = db.rawQuery(query, null);

            if (cr != null && cr.moveToFirst()) {
                boolean isSame;
                do {
                    String[] y = cr.getString(cr.getColumnIndex("topps")).split(",");
                    Integer[] sb1 = new Integer[y.length];
                    for (int j = 0; j < y.length; j++) {
                        sb1[j] = Integer.parseInt(y[j]);
                    }
                    HashSet<Integer> set1 = new HashSet<>(Arrays.asList(sb1));
                    HashSet<Integer> set2 = new HashSet<>(Arrays.asList(sb));
                    if (set1.equals(set2)) {
                        isSame = true;
                        break;
                    } else {
                        isSame = false;
                    }
                } while (cr.moveToNext());

                if (isSame) {
                    ContentValues cv = new ContentValues();
                    cv.put(DBConstants.CL_quantity, cr.getInt(cr.getColumnIndex(DBConstants.CL_quantity)) + pm.getQty());
                    cv.put(DBConstants.CL_sub_total, pm.getSubTotal());
                    db.update(DBConstants.CART_ITEM_TABLE, cv, DBConstants.CL_ID +
                            "=?", new String[]{String.valueOf(cr.getInt(cr.getColumnIndex(DBConstants.CL_ID)))});
                } else {
                    putData(pm, toppingList);
                }
            } else {
                putData(pm, toppingList);
            }
        } else {
            query = "select " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_ID +
                    "," + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_quantity +
                    "," + DBConstants.TOPPING_TABLE +
                    "." + DBConstants.CL_topping_id +
                    "  from " + DBConstants.CART_ITEM_TABLE +
                    " left join " + DBConstants.TOPPING_TABLE +
                    " on " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_ID +
                    " = " + DBConstants.TOPPING_TABLE +
                    "." + DBConstants.CL_topping_relation_id +
                    " where " + DBConstants.CART_ITEM_TABLE +
                    "." + DBConstants.CL_product_id +
                    "= " + pm.getProductId();

            cr = db.rawQuery(query, null);

            if (cr != null && cr.getCount() > 0 && cr.moveToFirst()) {
                boolean isExist;
                do {
                    if (cr.getInt(cr.getColumnIndex(DBConstants.CL_topping_id)) == 0) {
                        isExist = false;
                        break;
                    } else if (cr.getInt(cr.getColumnIndex(DBConstants.CL_topping_id)) != 0) {
                        if (cr.getInt(cr.getColumnIndex(DBConstants.CL_topping_id)) == 0) {
                            isExist = false;
                        } else {
                            isExist = true;
                        }
                    } else {
                        isExist = false;
                    }
                } while (cr.moveToNext());

                if (isExist) {
                    putData(pm, toppingList);
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(DBConstants.CL_quantity, cr.getInt(cr.getColumnIndex(DBConstants.CL_quantity)) + pm.getQty());
                    cv.put(DBConstants.CL_sub_total, pm.getSubTotal());
                    db.update(DBConstants.CART_ITEM_TABLE, cv, DBConstants.CL_ID +
                            "=?", new String[]{String.valueOf(cr.getInt(cr.getColumnIndex(DBConstants.CL_ID)))});
                }
            } else {
                putData(pm, toppingList);
            }
        }
        AppUtils.showToastMessage(ctx, ctx.getString(R.string.msg_add_to_cart));
    }

    private void putData(ProductModel pm, List<ToppingModel> toppingList) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.CL_product_id, pm.getProductId());
        cv.put(DBConstants.CL_product_name, pm.getProductName());
        cv.put(DBConstants.CL_quantity, pm.getQty());
        cv.put(DBConstants.CL_currency, pm.getCurrency());
        cv.put(DBConstants.CL_single_product_price, pm.getProductPrice());
        cv.put(DBConstants.CL_image_url, pm.getProductImage());
        cv.put(DBConstants.CL_sub_total, pm.getSubTotal());
        try {
            cv.put(DBConstants.CL_product_toppings, AppUtils.getJsonMapper().writeValueAsString(pm.getProductToppings()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        db.insert(DBConstants.CART_ITEM_TABLE, null, cv);

        if (toppingList != null && toppingList.size() > 0) {
            cr = db.rawQuery(
                    "select " + DBConstants.CL_ID +
                            " from " + DBConstants.CART_ITEM_TABLE +
                            " order by " + DBConstants.CL_ID +
                            " desc limit 1", null);
            if (cr.moveToFirst()) {
                int relationId = cr.getInt(cr.getColumnIndex(DBConstants.CL_ID));
                cr.close();

                for (ToppingModel tm : toppingList) {

                    ContentValues cv1 = new ContentValues();
                    cv1.put(DBConstants.CL_topping_relation_id, relationId);
                    cv1.put(DBConstants.CL_product_id, pm.getProductId());
                    cv1.put(DBConstants.CL_topping_id, tm.getToppingId());
                    cv1.put(DBConstants.CL_topping_name, tm.getToppingTitle());
                    cv1.put(DBConstants.CL_topping_price, tm.getToppingPrice());
                    cv1.put(DBConstants.CL_topping_image, tm.getToppingImage());

                    db.insert(DBConstants.TOPPING_TABLE, null, cv1);
                }
            }
        }
    }

    // GET ALL CART ITEMS
    public List<ProductModel> getCartList() {
        List<ProductModel> productList = new ArrayList<>();

        cr = db.rawQuery("select * from " + DBConstants.CART_ITEM_TABLE, null);
        if (cr.moveToFirst()) {
            do {
                ProductModel pm = new ProductModel();
                pm.setProductId(cr.getInt(cr.getColumnIndex(DBConstants.CL_product_id)));
                pm.setQty(cr.getInt(cr.getColumnIndex(DBConstants.CL_quantity)));
                pm.setProductName(cr.getString(cr.getColumnIndex(DBConstants.CL_product_name)));
                pm.setCurrency(cr.getString(cr.getColumnIndex(DBConstants.CL_currency)));
                pm.setProductPrice(Integer.parseInt(cr.getString(cr.getColumnIndex(DBConstants.CL_single_product_price))));
                pm.setProductImage(cr.getString(cr.getColumnIndex(DBConstants.CL_image_url)));
                pm.setSubTotal(Integer.parseInt(cr.getString(cr.getColumnIndex(DBConstants.CL_sub_total))));
                pm.setRowId(cr.getInt(cr.getColumnIndex(DBConstants.CL_ID)));

                // Now get toppings of this product whatever we have added...
                List<ToppingModel> toppingList = new ArrayList<>();
                Cursor c = db.rawQuery(
                        "select * from " + DBConstants.TOPPING_TABLE +
                                " where " + DBConstants.CL_topping_relation_id +
                                " = ?", new String[]{String.valueOf(pm.getRowId())});
                if (c.moveToFirst()) {
                    do {
                        ToppingModel tm = new ToppingModel();
                        tm.setRelationId(c.getInt(c.getColumnIndex(DBConstants.CL_topping_relation_id)));
                        tm.setId(c.getInt(c.getColumnIndex(DBConstants.CL_ID)));
                        tm.setToppingId(c.getInt(c.getColumnIndex(DBConstants.CL_topping_id)));
                        tm.setToppingTitle(c.getString(c.getColumnIndex(DBConstants.CL_topping_name)));
                        tm.setToppingImage(c.getString(c.getColumnIndex(DBConstants.CL_topping_image)));
                        tm.setToppingPrice(c.getString(c.getColumnIndex(DBConstants.CL_topping_price)));
                        tm.setSelected(true);
                        toppingList.add(tm);
                    } while (c.moveToNext());
                }
                pm.setToppings(toppingList);

                try {
                    List<ToppingModel> productToppings = AppUtils.getJsonMapper().readValue(cr.getString(cr.getColumnIndex(DBConstants.CL_product_toppings)), new TypeReference<List<ToppingModel>>() {
                    });
                    pm.setProductToppings(productToppings);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                productList.add(pm);
            } while (cr.moveToNext());
        }
        return productList;
    }

    // REMOVE PRODUCT FROM CART
    public void removeProduct(int rowId) {
        db.delete(DBConstants.CART_ITEM_TABLE, DBConstants.CL_ID +
                " = ?", new String[]{String.valueOf(rowId)});
        db.delete(DBConstants.TOPPING_TABLE, DBConstants.CL_topping_relation_id +
                " = ?", new String[]{String.valueOf(rowId)});
    }

    //REMOVE TOPPING FROM CART
    public void removeToppingsFromCart(ProductModel product, ToppingModel tm) {
        db.delete(DBConstants.TOPPING_TABLE, DBConstants.CL_topping_relation_id +
                "=? and " + DBConstants.CL_topping_id +
                "=?", new String[]{String.valueOf(product.getRowId()), String.valueOf(tm.getToppingId())});
    }

    // ADD TOPPINGS IN EXISTING PRODUCT
    public void addToppingOnExistingProduct(ProductModel product, ToppingModel topping) {
        try {
            db.delete(DBConstants.TOPPING_TABLE, DBConstants.CL_product_id +
                            " = ? AND " + DBConstants.CL_topping_relation_id +
                            " = ? AND " + DBConstants.CL_topping_id +
                            " = ? ",
                    new String[]{String.valueOf(product.getProductId()),
                            String.valueOf(product.getRowId()),
                            String.valueOf(topping.getToppingId())});
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues cv = new ContentValues();
        cv.put(DBConstants.CL_topping_relation_id, product.getRowId());
        cv.put(DBConstants.CL_topping_id, topping.getToppingId());
        cv.put(DBConstants.CL_product_id, product.getProductId());
        cv.put(DBConstants.CL_topping_name, topping.getToppingTitle());
        cv.put(DBConstants.CL_topping_price, topping.getToppingPrice());
        cv.put(DBConstants.CL_topping_image, topping.getToppingImage());
        db.insert(DBConstants.TOPPING_TABLE, null, cv);
    }

    // UPDATE CART QUANTITY
    public void updateCartQuantity(ProductModel product) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.CL_quantity, product.getQty());
        db.update(DBConstants.CART_ITEM_TABLE, cv, DBConstants.CL_ID +
                " = ?", new String[]{String.valueOf(product.getRowId())});
    }

    // Todo Add new notification
    public void addNotification(NotificationModel notificationModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.CL_NOTIFICATION_ID, notificationModel.getNotification_id());
        contentValues.put(DBConstants.CL_NOTIFICATION_MSG_TYPE, notificationModel.getResponse().getMsgtype());
        contentValues.put(DBConstants.CL_NOTIFICATION_MESSAGE, notificationModel.getResponse().getMessage());
        contentValues.put(DBConstants.CL_NOTIFICATION_ORDER_ID, notificationModel.getResponse().getOrderId());
        contentValues.put(DBConstants.CL_NOTIFICATION_TABLE_ID, notificationModel.getResponse().getTable_id());
        contentValues.put(DBConstants.CL_NOTIFICATION_RESTAURANT_ID, notificationModel.getResponse().getRestaurant_id());
        contentValues.put(DBConstants.CL_NOTIFICATION_REFERENCE_ID, notificationModel.getResponse().getReference_id());
        contentValues.put(DBConstants.CL_NOTIFICATION_COUNTRY_ID, notificationModel.getResponse().getCountry_id());
        contentValues.put(DBConstants.CL_NOTIFICATION_OFFER_ID, notificationModel.getResponse().getOfferId());
        contentValues.put(DBConstants.CL_NOTIFICATION_IS_VIEW, notificationModel.getIs_read());
        db.insert(DBConstants.NOTIFICATION_TABLE, null, contentValues);
    }

    // Todo Update the notification IsRead
    public int updateNotificationRead(int mNotificationID, int mIsRead) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.CL_NOTIFICATION_IS_VIEW, mIsRead);

        // updating row
        return db.update(DBConstants.NOTIFICATION_TABLE, values, DBConstants.CL_NOTIFICATION_ID + " = ?",
                new String[]{String.valueOf(mNotificationID)});
    }

    // Todo truncate the table from the database
    public Integer truncateTable(String tableName) {
        return db.delete(tableName, null, null);
    }

    // Todo Count total number of rows
    public int totalRowsInTable(String tableName) {
        return (int) DatabaseUtils.queryNumEntries(db, tableName, null, null);
    }

    // Todo get all the notifications
    public ArrayList<NotificationModel> getAllNotification() {
        ArrayList<NotificationModel> data = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + DBConstants.NOTIFICATION_TABLE + " ORDER BY " + DBConstants.CL_NOTIFICATION_ID + " DESC";
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                NotificationModel onceData = new NotificationModel();
                NotificationResponseModel onceData2 = new NotificationResponseModel();
                onceData.setNotification_id(c.getInt((c.getColumnIndex(DBConstants.CL_NOTIFICATION_ID))));
                onceData2.setMsgtype(c.getInt((c.getColumnIndex(DBConstants.CL_NOTIFICATION_MSG_TYPE))));
                onceData2.setMessage((c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_MESSAGE))));
                onceData2.setOrderId(c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_ORDER_ID)));
                onceData2.setTable_id(c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_TABLE_ID)));
                onceData2.setRestaurant_id(c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_RESTAURANT_ID)));
                onceData2.setReference_id(c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_REFERENCE_ID)));
                onceData2.setCountry_id(c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_COUNTRY_ID)));
                onceData2.setOfferId(c.getString(c.getColumnIndex(DBConstants.CL_NOTIFICATION_OFFER_ID)));
                onceData.setIs_read(c.getInt(c.getColumnIndex(DBConstants.CL_NOTIFICATION_IS_VIEW)));
                onceData.setResponse(onceData2);

                // adding to notification list
                data.add(onceData);
            } while (c.moveToNext());
        }
        c.close();

        return data;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                String cart_items =
                        "CREATE TABLE " +
                                DBConstants.CART_ITEM_TABLE + "( " +
                                DBConstants.CL_ID + " integer primary key," +
                                DBConstants.CL_product_id + " integer," +
                                DBConstants.CL_product_name + " text," +
                                DBConstants.CL_currency + " text," +
                                DBConstants.CL_quantity + " integer," +
                                DBConstants.CL_single_product_price + " text, " +
                                DBConstants.CL_sub_total + " text, " +
                                DBConstants.CL_image_url + " text, " +
                                DBConstants.CL_product_toppings + " text)";

                String country_table =
                        "CREATE TABLE " +
                                DBConstants.COUNTRY_TABLE + "( " +
                                DBConstants.CL_ID + " integer primary key," +
                                DBConstants.CL_country_id + " integer," +
                                DBConstants.CL_country_name + " text)";

                String json_data_table =
                        "CREATE TABLE " +
                                DBConstants.OFFLINE_DATA + "( " +
                                DBConstants.CL_ID + " integer primary key," +
                                DBConstants.CL_offline_id + " integer, " +
                                DBConstants.CL_json_string + " text)";

                String topping_table =
                        "CREATE TABLE " +
                                DBConstants.TOPPING_TABLE + " ( " +
                                DBConstants.CL_ID + " integer primary key, " +
                                DBConstants.CL_topping_relation_id + " integer, " +
                                DBConstants.CL_topping_id + " integer, " +
                                DBConstants.CL_product_id + " integer, " +
                                DBConstants.CL_topping_name + " text," +
                                DBConstants.CL_topping_price + " text," +
                                DBConstants.CL_topping_image + " text)";

                db.execSQL(cart_items);
                db.execSQL(country_table);
                db.execSQL(json_data_table);
                db.execSQL(topping_table);
                db.execSQL(CREATE_TABLE_NOTIFICATION);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            onCreate(db);
        }
    }
}