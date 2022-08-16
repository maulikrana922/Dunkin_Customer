package com.dunkin.customer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.adapters.CartItemsAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.constants.DBConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.fragments.DatePickerDialogFragment;
import com.dunkin.customer.models.ProductModel;
import com.dunkin.customer.models.RestaurantModel;
import com.dunkin.customer.models.ToppingModel;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CartDetailActivity extends BaseActivity {

    public static int restaurantId = -1;
    private int restId = 0;
    private double total = 0;
    private Context context;
    private List<ProductModel> cartList;
    private TextView tvTotalAmount, edRecStartDate, edRecEndDate, edRecTime, edRecDate;
    private TextView spSelectRestaurant;
    private EditText edShippingAddress, edBillingAddress;
    private ListView lvCartItems;
    private CartItemsAdapter cartItemAdapter;
    private CheckBox cbDelivery, cbRecurring, cbOrderForToday;
    private DBAdapter dbAdapter;
    private Calendar calender;
    private JSONArray checkedBox;
    private JSONObject jsonRecurring = null;
    private JSONObject jsonTakeout = null;
    private List<RestaurantModel> restaurantList;
    private ArrayAdapter<RestaurantModel> restaurantAdapter;
    private RestaurantModel restaurantModel;
    private int[] daysCheckBox = {R.id.cb0, R.id.cb1, R.id.cb2, R.id.cb3, R.id.cb4, R.id.cb5, R.id.cb6};
    private int hour, minute;
    //private String strOrderDate = null;
    private String strOrderTime = null;
    private boolean isOrderToday;

    /*private DatePickerDialog.OnDateSetListener startDatePickerRecurring = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calender.set(Calendar.YEAR, year);
            calender.set(Calendar.MONTH, monthOfYear);
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            edRecStartDate.setText((AppUtils.getFormattedDate(calender.getTime())));
            edRecEndDate.setText(AppUtils.getFormattedDate(calender.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerRecurring = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            edRecEndDate.setText(new StringBuilder().append(checkDigit(dayOfMonth)).append("/").append(checkDigit(monthOfYear + 1))
                    .append("/").append(year));
        }
    };

    private DatePickerDialog.OnDateSetListener datePickerTakeout = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calender.set(Calendar.YEAR, year);
            calender.set(Calendar.MONTH, monthOfYear);
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            edRecDate.setText((AppUtils.getFormattedDate(calender.getTime())));
        }
    };*/

    private TimePickerDialog.OnTimeSetListener timePickerTakeout =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    edRecTime.setText(String.format("%s:%s", pad(hour), pad(minute)));
                }
            };

    private Handler mHandlerStartDate = new Handler() {
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            edRecStartDate.setText(b.getString(getResources().getString(R.string.cal_set_date)));
        }
    };

    private Handler mHandlerEndDate = new Handler() {
        public void handleMessage(Message m) {
            Bundle b = m.getData();
            edRecEndDate.setText(b.getString(getResources().getString(R.string.cal_set_date)));
        }
    };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.cart_list_items_layout, getString(R.string.lbl_checkout));
        context = CartDetailActivity.this;

        lvCartItems = (ListView) findViewById(R.id.lvCartItems);
        cartList = new ArrayList<>();

        dbAdapter = new DBAdapter(context);

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        edShippingAddress = (EditText) findViewById(R.id.edShippingAddress);
        edBillingAddress = (EditText) findViewById(R.id.edBillingAddress);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        cbDelivery = (CheckBox) findViewById(R.id.cbDelivery);
        cbRecurring = (CheckBox) findViewById(R.id.cbReccuring);
        ImageView imgEnableShipAddress = (ImageView) findViewById(R.id.imgEnableShipAddress);
        ImageView imgEnableBillAddress = (ImageView) findViewById(R.id.imgEnableBillAddress);

        edShippingAddress.setEnabled(false);
        edBillingAddress.setEnabled(false);

        //tvTotalAmount.setText(getString(R.string.lbl_cart_amount, 0));
        edShippingAddress.setText(AppUtils.getAppPreference(context).getString(AppConstants.USER_SHIPPING_ADDRESS, ""));
        edBillingAddress.setText(AppUtils.getAppPreference(context).getString(AppConstants.USER_ADDRESS, ""));

        cartItemAdapter = new CartItemsAdapter(context, cartList);
        lvCartItems.setAdapter(cartItemAdapter);

        lvCartItems.setEmptyView(findViewById(R.id.emptyElement));
        getDataFromAPI();

        /*imgEnableBillAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edBillingAddress.setEnabled(true);
                edBillingAddress.requestFocus();
                edBillingAddress.setSelection(edBillingAddress.length());
            }
        });*/

        /*imgEnableShipAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edShippingAddress.setEnabled(true);
                edShippingAddress.requestFocus();
                edShippingAddress.setSelection(edShippingAddress.length());
            }
        });*/

        cbRecurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        cbDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeOutBottomSheet();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_btn_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.menu_submit) {
            placeOrder();
        }
        return super.onOptionsItemSelected(item);
    }

    private void placeOrder() {
        int isDelivery;
        if (cbDelivery != null) {
            if (cbDelivery.isChecked()) {
                isDelivery = 0;
            } else {
                isDelivery = 1;
            }
        } else {
            isDelivery = 1;
        }

        /*if(cbDelivery != null) {
            if (cbDelivery.isChecked()) {
                isDelivery = 1;
            } else {
                isDelivery = 0;
            }
        } else {
            isDelivery = 0;
        }*/

        int isTodayOrder;
        /*if(cbOrderForToday != null) {
            if (cbOrderForToday.isChecked()) {
                isOrderToday = true;
                isTodayOrder = 0;
            } else {
                isOrderToday = false;
                isTodayOrder = 1;
            }
        } else {
            isOrderToday = false;
            isTodayOrder = 1;
        }*/

        if (cbOrderForToday != null) {
            if (cbOrderForToday.isChecked()) {
                isOrderToday = true;
                isTodayOrder = 1;
            } else {
                isOrderToday = false;
                isTodayOrder = 0;
            }
        } else {
            isOrderToday = false;
            isTodayOrder = 0;
        }

        AppController.postOrder(CartDetailActivity.this, jsonRecurring, strOrderTime, restId, edBillingAddress.getText().toString(), edShippingAddress.getText().toString(), total, isDelivery, isTodayOrder, cartList, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);
                AppUtils.showToastMessage(CartDetailActivity.this, jsonResponse.getString("message"));
                if (jsonResponse.getInt("success") == 1) {
                    dbAdapter.open();
                    dbAdapter.truncateTables(DBConstants.CART_ITEM_TABLE);
                    dbAdapter.truncateTables(DBConstants.TOPPING_TABLE);
                    dbAdapter.close();
                    setResult(RESULT_OK);
                    finish();
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }else if(jsonResponse.getInt("success") == 99) {
                    displayDialog(jsonResponse.getString("message"));
                }
            }
        });
    }

    private void getDataFromAPI() {

        dbAdapter.open();
        cartList = dbAdapter.getCartList();
        dbAdapter.close();
        if (cartList != null && cartList.size() > 0) {
            cartItemAdapter = new CartItemsAdapter(context, cartList);
            lvCartItems.setAdapter(cartItemAdapter);
            for (ProductModel cm : cartList) {
                total += (cm.getProductPrice() * cm.getQty());
                for (ToppingModel tm : cm.getToppings()) {
                    total += Double.parseDouble(tm.getToppingPrice()) * cm.getQty();
                }
            }
            tvTotalAmount.setText(AppUtils.CurrencyFormat(total));
        }
    }

    public void openBottomSheet() {
        try {
            final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

            final DateFormat dateFormat = new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH, Locale.US);

            calender = Calendar.getInstance();
            Calendar calMinDate = Calendar.getInstance();
            calMinDate.setTime(dateFormat.parse(AppUtils.getFormattedDate(calender.getTime())));
            calMinDate.add(Calendar.DATE, 1);

            edRecStartDate = (TextView) view.findViewById(R.id.edRecStartDate);
            edRecEndDate = (TextView) view.findViewById(R.id.edRecEnddate);

            int mYear = calMinDate.get(Calendar.YEAR);
            int mMonth = calMinDate.get(Calendar.MONTH);
            int mDay = calMinDate.get(Calendar.DAY_OF_MONTH);

            final Bundle b = new Bundle();
            b.putInt(getResources().getString(R.string.cal_set_day), mDay);
            b.putInt(getResources().getString(R.string.cal_set_month), mMonth);
            b.putInt(getResources().getString(R.string.cal_set_year), mYear);

            edRecStartDate.setText((AppUtils.getFormattedDate(calMinDate.getTime())));
            edRecEndDate.setText((AppUtils.getFormattedDate(calMinDate.getTime())));

            cbOrderForToday = (CheckBox) view.findViewById(R.id.cbOrderForToday);

            cbOrderForToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    isOrderToday = isChecked;
                }
            });

            if (isOrderToday) {
                cbOrderForToday.setChecked(true);
            } else {
                cbOrderForToday.setChecked(false);
            }

            if (edRecTime != null && edRecTime.getText().toString().length() > 0) {
                String takeoutTime = edRecTime.getText().toString();
                if (compareTime(takeoutTime).equalsIgnoreCase("1") || compareTime(takeoutTime).equalsIgnoreCase("3")) {
                    cbOrderForToday.setEnabled(false);
                }
            }

            if (jsonRecurring != null) {
                try {

                    DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                    Date startDate = null;
                    Date endDate = null;
                    try {
                        startDate = dateFormat2.parse(jsonRecurring.getString("startdate"));
                        endDate = dateFormat2.parse(jsonRecurring.getString("enddate"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    edRecStartDate.setText(AppUtils.getFormattedDate(startDate));
                    edRecEndDate.setText(AppUtils.getFormattedDate(endDate));

                    for (int i = 0; i < checkedBox.length(); i++) {
                        int pos = (int) checkedBox.get(i);
                        ((CheckBox) view.findViewById(daysCheckBox[pos])).setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            RelativeLayoutButton btnSetup = (RelativeLayoutButton) view.findViewById(R.id.btnSetup);
            btnSetup.imgIcon.setImageResource(R.drawable.ic_white_submit);
            btnSetup.btnText.setText(getString(R.string.txt_setup_reccuring));
            final RelativeLayoutButton btnReset = (RelativeLayoutButton) view.findViewById(R.id.btnReset);
            btnReset.imgIcon.setImageResource(R.drawable.ic_white_submit);
            btnReset.btnText.setText(getString(R.string.txt_reset_reccuring));
            if (jsonRecurring != null) {
                btnReset.setVisibility(View.VISIBLE);
            }

            final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();

            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (jsonRecurring == null) {
                        cbRecurring.setChecked(false);
                    } else {
                        cbRecurring.setChecked(true);
                    }
                }
            });

            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //     mBottomSheetDialog.dismiss();
                    checkedBox = new JSONArray();

                    for (int i = 0; i < daysCheckBox.length; i++) {
                        CheckBox cb = (CheckBox) view.findViewById(daysCheckBox[i]);
                        if (cb.isChecked()) {
                            checkedBox.put(i);
                        }
                    }

                    if (checkedBox != null && checkedBox.length() > 0) {

                        cbRecurring.setChecked(true);
                        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH);

                        try {
                            Date dtRectStart = dateFormat.parse(edRecStartDate.getText().toString());
                            Date dtRectEnd = dateFormat.parse(edRecEndDate.getText().toString());

                            if (AppUtils.isValidDate(edRecStartDate.getText().toString(), AppConstants.DD_MM_YYYY_SLASH) == 0) {
                                AppUtils.showToastMessage(context, getString(R.string.txt_order_rect_start_date));
                            } else if (AppUtils.isValidDate(edRecEndDate.getText().toString(), AppConstants.DD_MM_YYYY_SLASH) == 0) {
                                AppUtils.showToastMessage(context, getString(R.string.txt_order_rect_end_date));
                            } else if (dtRectStart.compareTo(dtRectEnd) > 0) {
                                AppUtils.showToastMessage(context, getString(R.string.txt_order_rect_date_before));
                            } else if (!AppUtils.isDaysBetweenDates(edRecStartDate.getText().toString(), edRecEndDate.getText().toString(), checkedBox)) {
                                AppUtils.showToastMessage(context, getString(R.string.txt_order_days_not_in_date));
                            } else {
                                jsonRecurring = new JSONObject();
                                try {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

                                        jsonRecurring.put("startdate", sdf.format(new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(edRecStartDate.getText().toString().trim())));
                                        jsonRecurring.put("enddate", sdf.format(new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(edRecEndDate.getText().toString().trim())));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    jsonRecurring.put("selectedday", checkedBox);
                                    Dunkin_Log.i("JSON RECURRING", jsonRecurring.toString());
                                    mBottomSheetDialog.dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (ParseException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AppUtils.showToastMessage(context, getString(R.string.txt_checkbox_empty));
                    }
                }
            });

            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        edRecStartDate.setText(" ");
                        edRecEndDate.setText(" ");

                        for (int i = 0; i < checkedBox.length(); i++) {
                            int pos = (int) checkedBox.get(i);
                            ((CheckBox) view.findViewById(daysCheckBox[pos])).setChecked(false);
                        }
                        cbOrderForToday.setChecked(false);
                        jsonRecurring = null;
                        //  mBottomSheetDialog.dismiss();
                        btnReset.setVisibility(View.GONE);
                        cbRecurring.setChecked(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            edRecStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new DatePickerDialog(context, startDatePickerRecurring, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show();
                    Calendar calMinDate = Calendar.getInstance();
                    calMinDate.add(Calendar.DATE, 1);
                    String strCalMinDate = dateFormat.format(calMinDate.getTime());

                    b.putString("MinDate", strCalMinDate);

                    AppUtils.setHandler(mHandlerStartDate);
                    DatePickerDialogFragment datePicker = new DatePickerDialogFragment();

                    datePicker.setArguments(b);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(datePicker, "date_picker");
                    ft.commit();
                }
            });

            edRecEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new DatePickerDialog(context, endDatePickerRecurring, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show();
                    Calendar calMinDate = Calendar.getInstance();
                    calMinDate.add(Calendar.DATE, 1);
                    String strCalMinDate = dateFormat.format(calMinDate.getTime());

                    b.putString("MinDate", strCalMinDate);

                    AppUtils.setHandler(mHandlerEndDate);
                    DatePickerDialogFragment datePicker = new DatePickerDialogFragment();

                    datePicker.setArguments(b);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(datePicker, "date_picker");
                    ft.commit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takeOutBottomSheet() {
        try {
            final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_takeout, null);
            calender = Calendar.getInstance();

            edRecDate = (TextView) view.findViewById(R.id.edRecDate);
            edRecTime = (TextView) view.findViewById(R.id.edRecTime);
            spSelectRestaurant = (TextView) view.findViewById(R.id.spSelectRestaurant);

            final StringBuilder tempSB = new StringBuilder();
            tempSB.append(pad(hour)).append(":").append(pad(minute));
            edRecTime.setText(tempSB);

            Calendar cal = Calendar.getInstance();
            calender.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            calender.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            calender.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
            edRecDate.setText((AppUtils.getFormattedDate(calender.getTime())));

            if (restaurantModel != null) {
                restaurantId = restaurantModel.getRestaurantId();
                spSelectRestaurant.setText(restaurantModel.getRestaurantName());
            } else {
                restaurantId = -1;
                spSelectRestaurant.setText(R.string.txt_select_restaurant);
            }

            spSelectRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AppController.getRestaurantList(context, true, new Callback() {
                            @Override
                            public void run(Object result) throws JSONException, IOException {

                                JSONObject jsonResponse = new JSONObject((String) result);
                                if (jsonResponse.getInt("success") == 1) {

                                    restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                                    });

                                    restaurantAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, restaurantList);

                                    openDialog(getString(R.string.txt_select_restaurant));
                                }else if (jsonResponse.getInt("success") == 100) {
                                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                } else {
                                    if(jsonResponse.getInt("success") == 99) {
                                        displayDialog(jsonResponse.getString("message"));
                                    }else{
                                        AppUtils.showToastMessage(context, getString(R.string.system_error));
                                    }
                                }
                            }
                        });
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });

            RelativeLayoutButton btnSetup = (RelativeLayoutButton) view.findViewById(R.id.btnSetup);
            btnSetup.imgIcon.setImageResource(R.drawable.ic_white_submit);
            btnSetup.btnText.setText(getString(R.string.txt_setup_reccuring));
            final RelativeLayoutButton btnReset = (RelativeLayoutButton) view.findViewById(R.id.btnReset);
            btnReset.imgIcon.setImageResource(R.drawable.ic_white_submit);
            btnReset.btnText.setText(getString(R.string.txt_reset_reccuring));
            if (jsonTakeout != null) {
                btnReset.setVisibility(View.VISIBLE);
            }

            final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();

            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (jsonTakeout == null) {
                        cbDelivery.setChecked(false);
                    } else {
                        cbDelivery.setChecked(true);
                    }
                }
            });

            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cbDelivery.setChecked(true);
                    try {
                        /*if (edRecDate.getText().toString().length() == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.txt_please_select_date));
                        } else*//*
                        if (edRecTime.getText().toString().length() == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.txt_please_select_time));
                        } *//*else if (AppUtils.isValidDate(edRecDate.getText().toString(), AppConstants.DD_MM_YYYY_SLASH) == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.txt_order_rect_start_date));
                        }*//* else if (restaurantId == -1) {
                            AppUtils.showToastMessage(context, getString(R.string.err_select_restaurant));
                        } else {*/
                        /*if (edRecDate.getText().toString().length() == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.txt_please_select_date));
                        } else*/

                        if (edRecTime.getText().toString().length() == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.txt_please_select_time));
                        } else if (restaurantId == -1) {
                            AppUtils.showToastMessage(context, getString(R.string.err_select_restaurant));
                        } else {
                            String takeoutTime = edRecTime.getText().toString();
                            Dunkin_Log.i("Time", "" + compareTime(takeoutTime));
                            Dunkin_Log.i("IsTodayOrder", "" + isOrderToday);

                            if (isOrderToday) {
                                if (compareTime(takeoutTime).equalsIgnoreCase("1") || compareTime(takeoutTime).equalsIgnoreCase("3")) {
                                    AppUtils.showToastMessage(context, "Please select time after current time!!!");
                                } else {
                                    jsonTakeout = new JSONObject();
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                        //strOrderDate = sdf.format(new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(edRecDate.getText().toString().trim()));
                                        strOrderTime = edRecTime.getText().toString().trim();
                                        restId = restaurantId;
                                        mBottomSheetDialog.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                jsonTakeout = new JSONObject();
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                                    //strOrderDate = sdf.format(new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(edRecDate.getText().toString().trim()));
                                    strOrderTime = edRecTime.getText().toString().trim();
                                    restId = restaurantId;
                                    mBottomSheetDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //edRecDate.setText("");
                        edRecTime.setText("");
                        spSelectRestaurant.setText(R.string.txt_select_restaurant);
                        restaurantId = -1;

                        jsonTakeout = null;
                        btnReset.setVisibility(View.GONE);
                        cbDelivery.setChecked(false);
                        restaurantList.clear();
                        restaurantModel = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            /*edRecDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(context, datePickerTakeout, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show();
                }
            });*/

            edRecTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new TimePickerDialog(CartDetailActivity.this, timePickerTakeout, hour, minute, true).show();
                    //new RangeTimePickerDialog(CartDetailActivity.this, timePickerTakeout, hour, minute, true).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String compareTime(String strTimeToCompare) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        int dtHour;
        int dtMin;

        Date dtCurrentDate;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date TimeToCompare = sdf.parse(strTimeToCompare);
            dtMin = cal.get(Calendar.MINUTE);
            dtHour = cal.get(Calendar.HOUR_OF_DAY);

            dtCurrentDate = sdf.parse(dtHour + ":" + dtMin);

            Dunkin_Log.i("CurrentTime", "" + sdf.format(dtCurrentDate));
            Dunkin_Log.i("TakeoutTime", "" + sdf.format(TimeToCompare));

            if (dtCurrentDate.after(TimeToCompare)) {
                return "1";
            }

            if (dtCurrentDate.before(TimeToCompare)) {
                return "2";
            }

            if (dtCurrentDate.equals(TimeToCompare)) {
                return "3";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "4";
    }

    private void openDialog(String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        alert.setAdapter(restaurantAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restaurantModel = restaurantAdapter.getItem(which);
                restaurantId = restaurantModel.getRestaurantId();
                spSelectRestaurant.setText(restaurantModel.getRestaurantName());
            }
        });
        alert.create().show();
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
