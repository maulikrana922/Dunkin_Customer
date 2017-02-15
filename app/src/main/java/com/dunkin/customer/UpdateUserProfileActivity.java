package com.dunkin.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.FavoriteRestaurantAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CountriesModel;
import com.dunkin.customer.models.RestaurantModel;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateUserProfileActivity extends BackActivity implements View.OnClickListener {
    private ArrayAdapter<CountriesModel> countryAdapter;
    private CountriesModel countryModel;
    private Context context;
    private EditText edFirstName, edLastName, edPhoneNumber, edAddress, edShippingAddress, edEmail, edPassword, edConfPassword;
    private CheckBox cbAddress;
    private LinearLayout llAddress;
    private DBAdapter db;
    private TextView spSelectCountry, edDateOfBirth, spSetFavoriteRestaurant;
    private Calendar calendar;
    private JSONObject jsonUser;
    private String[] restaurantIds;
    private JSONArray jsonArray;
    private List<RestaurantModel> restaurantList;
    private ListView lvRestaurantList;
    private FavoriteRestaurantAdapter restaurantAdapter;

    private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            // dd/MM/yyyy
            edDateOfBirth.setText(String.format("%d/%d/%d", dayOfMonth, (monthOfYear + 1), year));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_update_user_profile, getString(R.string.lbl_edit_profile));
        context = UpdateUserProfileActivity.this;

        try {
            initializeViews();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getData() throws JSONException {

        jsonUser = new JSONObject(getIntent().getStringExtra("user"));

        try {
            if (AppUtils.isNotNull(jsonUser.getString("restaurant_id"))) {

                restaurantIds = jsonUser.getString("restaurant_id").split(",");
                jsonArray = new JSONArray();
                for (String restaurantId : restaurantIds)
                    jsonArray.put(Integer.parseInt(restaurantId));
            } else
                spSetFavoriteRestaurant.setText(getString(R.string.txt_set_fav_restaurant));

        } catch (Exception e) {
            spSetFavoriteRestaurant.setText(getString(R.string.txt_set_fav_restaurant));
            e.printStackTrace();
        }

        edFirstName.setText(jsonUser.getString("firstName"));
        edLastName.setText(jsonUser.getString("lastName"));
        edEmail.setText(jsonUser.getString("email"));
        edPhoneNumber.setText(jsonUser.getString("phone"));
        edAddress.setText(jsonUser.getString("address"));
//        edPassword.setText(jsonUser.getString("password"));
//        edConfPassword.setText(jsonUser.getString("password"));
        edPassword.setText("");
        edConfPassword.setText("");
        edShippingAddress.setText(jsonUser.getString("shippingAddress"));
        try {
            if (jsonUser.getString("dob").equalsIgnoreCase("00-00-0000")) {
                edDateOfBirth.setOnClickListener(this);
                edDateOfBirth.setText(R.string.str_default_date);
            } else {
                edDateOfBirth.setEnabled(false);
                String dob = AppUtils.getFormattedDate(new SimpleDateFormat(AppConstants.MM_DD_YYYY_SERVER).parse(jsonUser.getString("dob")));
                edDateOfBirth.setText(dob);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (jsonUser.getString("address").equals(jsonUser.getString("shippingAddress"))) {
            cbAddress.setChecked(true);
        } else {
            cbAddress.setChecked(false);
        }

        edEmail.setEnabled(false);

        db.open();
        List<CountriesModel> countriesModelList = db.getAllCountries();

        countryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, countriesModelList);
        db.close();

        for (CountriesModel cm : countriesModelList) {
            if (cm.getCountry_id() == AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, 0)) {
                this.countryModel = cm;
                spSelectCountry.setText(cm.getName());
                break;
            }
        }

        try {
            if (countryModel != null) {
                AppController.getRestaurantListRegister(UpdateUserProfileActivity.this, countryModel.getCountry_id(), AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN), false, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {

                        JSONObject jsonResponse = new JSONObject((String) result);
                        if (jsonResponse.getInt("success") == 1) {
                            restaurantList = new ArrayList<>();
                            restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                            });

                            StringBuilder sbRestaurantName = new StringBuilder();
                            boolean firstTime = true;

                            try {
                                if (restaurantIds != null && restaurantIds.length != 0) {
                                    for (int j = 0; j < restaurantList.size(); j++) {
                                        for (String restaurantId : restaurantIds) {
                                            if (restaurantId.equalsIgnoreCase(String.valueOf(restaurantList.get(j).getRestaurantId()))) {
                                                if (firstTime) {
                                                    firstTime = false;
                                                } else {
                                                    sbRestaurantName.append(", ");
                                                }
                                                sbRestaurantName.append(restaurantList.get(j).getRestaurantName());
                                            }
                                        }
                                    }
                                    spSetFavoriteRestaurant.setText(sbRestaurantName);
                                } else
                                    spSetFavoriteRestaurant.setText(getString(R.string.txt_set_fav_restaurant));

                            } catch (Exception e) {
                                e.printStackTrace();
                                spSetFavoriteRestaurant.setText(getString(R.string.txt_set_fav_restaurant));
                            }
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                        } else {
                            AppUtils.showToastMessage(UpdateUserProfileActivity.this, getString(R.string.system_error));
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        spSetFavoriteRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (countryModel != null) {
                        AppController.getRestaurantListRegister(context, countryModel.getCountry_id(), AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN), false, new Callback() {
                            @Override
                            public void run(Object result) throws JSONException, IOException {

                                JSONObject jsonResponse = new JSONObject((String) result);
                                if (jsonResponse.getInt("success") == 1) {
                                    restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                                    });
                                    bottomSheetFavRestaurant(restaurantList);
                                } else if (jsonResponse.getInt("success") == 100) {
                                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                } else {
                                    AppUtils.showToastMessage(context, getString(R.string.system_error));
                                }
                            }
                        });
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bottomSheetFavRestaurant(final List<RestaurantModel> list) {
        try {
            final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_fav_restaurant, null);

            lvRestaurantList = (ListView) view.findViewById(R.id.lvRestaurantList);

            if (jsonArray != null && jsonArray.length() > 0) {
                List<RestaurantModel> dummyList = new ArrayList<>();
                dummyList.addAll(list);
                for (int j = 0; j < list.size(); j++) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.getInt(i) == dummyList.get(j).getRestaurantId()) {
                            dummyList.get(j).setSelected(true);
                        }
                    }
                }
                restaurantAdapter = new FavoriteRestaurantAdapter(context, dummyList);
                lvRestaurantList.setAdapter(restaurantAdapter);
            } else {
                restaurantAdapter = new FavoriteRestaurantAdapter(context, list);
                lvRestaurantList.setAdapter(restaurantAdapter);
            }

            RelativeLayoutButton btnSetup = (RelativeLayoutButton) view.findViewById(R.id.btnSetup);
            btnSetup.imgIcon.setImageResource(R.drawable.ic_white_submit);
            btnSetup.btnText.setText(getString(R.string.btn_done));

            final Dialog mBottomSheetDialog = new Dialog(context, R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();

            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (jsonArray == null || jsonArray.length() == 0) {
                        jsonArray = new JSONArray();
                        spSetFavoriteRestaurant.setText(context.getString(R.string.txt_set_fav_restaurant));
                    } else {
                        boolean isAnySelect = false;
                        StringBuilder sbRestaurantName = new StringBuilder();
                        boolean firstTime = true;
                        for (RestaurantModel rm : list) {
                            if (rm.isSelected()) {
                                isAnySelect = true;
                                if (firstTime) {
                                    firstTime = false;
                                } else {
                                    sbRestaurantName.append(", ");
                                }
                                sbRestaurantName.append(rm.getRestaurantName());
                            }
                        }
                        if (isAnySelect)
                            spSetFavoriteRestaurant.setText(sbRestaurantName);
                        else
                            spSetFavoriteRestaurant.setText(context.getString(R.string.txt_set_fav_restaurant));
                    }
                }
            });

            btnSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsonArray = new JSONArray();
                    StringBuilder sbRestaurantName = new StringBuilder();
                    boolean firstTime = true;
                    for (RestaurantModel rm : list) {
                        if (rm.isSelected()) {
                            jsonArray.put(rm.getRestaurantId());

                            if (firstTime) {
                                firstTime = false;
                            } else {
                                sbRestaurantName.append(", ");
                            }
                            sbRestaurantName.append(rm.getRestaurantName());
                        }
                    }
                    if (jsonArray != null && jsonArray.length() > 0)
                        spSetFavoriteRestaurant.setText(sbRestaurantName);
                    else
                        spSetFavoriteRestaurant.setText(context.getString(R.string.txt_set_fav_restaurant));
                    mBottomSheetDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeViews() throws JSONException {

        db = new DBAdapter(context);

        edFirstName = (EditText) findViewById(R.id.edFirstName);
        edLastName = (EditText) findViewById(R.id.edLastName);
        edDateOfBirth = (TextView) findViewById(R.id.edDateOfBirth);
        edPhoneNumber = (EditText) findViewById(R.id.edPhoneNumber);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edAddress = (EditText) findViewById(R.id.edAddress);
        edShippingAddress = (EditText) findViewById(R.id.edShippingAddress);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edConfPassword = (EditText) findViewById(R.id.edConfirmPassword);

        spSelectCountry = (TextView) findViewById(R.id.spSelectCountry);
        spSetFavoriteRestaurant = (TextView) findViewById(R.id.spSetFavoriteRestaurant);
        spSetFavoriteRestaurant.setSelected(true);

        calendar = Calendar.getInstance();
        cbAddress = (CheckBox) findViewById(R.id.cbAddress);

        llAddress = (LinearLayout) findViewById(R.id.llAddress);

        llAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbAddress.isChecked()) {
                    cbAddress.setChecked(false);
                } else {
                    cbAddress.setChecked(true);
                }
            }
        });

        getData();


        // Checkbox for shipping address logic start.............................................
        cbAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edShippingAddress.setText(edAddress.getText().toString());
                } else {
                    try {
                        edShippingAddress.setText("");
                        //edShippingAddress.setText(jsonUser.getString("shippingAddress"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edAddress.getText().length() > 0) {
                    cbAddress.setEnabled(true);
                } else {
                    cbAddress.setEnabled(false);
                }
                cbAddress.setChecked(false);
            }
        });

        edShippingAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edShippingAddress.getText().toString().equals(edAddress.getText().toString())) {
                    cbAddress.setChecked(false);
                }
            }
        });

        // Checkbox shipping address end................................................................
        spSelectCountry.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.submit_btn_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {
            try {
                updateCustomerInformation();
            } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCustomerInformation() throws UnsupportedEncodingException, JSONException, ParseException {
        if (edFirstName.getText().length() == 0) {
            AppUtils.showError(edFirstName, getString(R.string.empty_first_name));
        } else if (edLastName.getText().length() == 0) {
            AppUtils.showError(edLastName, getString(R.string.empty_last_name));
        } else if (edDateOfBirth.getText().length() == 0) {
            AppUtils.showError(edDateOfBirth, getString(R.string.empty_date_of_birth));
        } else if (AppUtils.isValidDate(edDateOfBirth.getText().toString(), AppConstants.DD_MM_YYYY_SLASH) == 1) {
            AppUtils.showError(edDateOfBirth, getString(R.string.min_val_date_of_birth));
        }
//        else if (edPassword.getText().length() == 0) {
//            AppUtils.showError(edPassword, getString(R.string.empty_password));
//        }
        else if (edPassword.getText().toString().trim().length() > 0 && edPassword.getText().length() < 6) {
            AppUtils.showError(edPassword, getString(R.string.min_length_password));
        }
//        else if (edConfPassword.getText().length() == 0) {
//            AppUtils.showError(edConfPassword, getString(R.string.empty_conform_password));
//        }
        else if (edConfPassword.getText().toString().trim().length() > 0 && edConfPassword.getText().length() < 6) {
            AppUtils.showError(edConfPassword, getString(R.string.min_length_password));
        } else if ((edPassword.getText().toString().trim().length() > 0 || edPassword.getText().toString().trim().length() > 0)
                && !edConfPassword.getText().toString().equals(edPassword.getText().toString())) {
            AppUtils.showError(edConfPassword, getString(R.string.password_is_not_match));
        } else if (edPhoneNumber.getText().length() == 0) {
            AppUtils.showError(edPhoneNumber, getString(R.string.empty_phone_number));
        } else if (edPhoneNumber.getText().length() < 8) {
            AppUtils.showError(edPhoneNumber, getString(R.string.min_length_phone_number));
        } else if (edAddress.getText().length() == 0) {
            AppUtils.showError(edAddress, getString(R.string.empty_address));
        } else if (edShippingAddress.getText().length() == 0) {
            AppUtils.showError(edAddress, getString(R.string.empty_shipping_address));
        } else if (countryModel == null) {
            AppUtils.showError(spSelectCountry, getString(R.string.txt_select_country));
        } else {
            final JSONObject jsonRequest = new JSONObject();
            try {
                jsonRequest.put("firstName", edFirstName.getText().toString());
                jsonRequest.put("lastName", edLastName.getText().toString());
                jsonRequest.put("email", edEmail.getText().toString());

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    String dob = sdf.format(new SimpleDateFormat(AppConstants.DD_MM_YYYY_SLASH).parse(edDateOfBirth.getText().toString()));
                    jsonRequest.put("dob", dob);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                jsonRequest.put("phoneNumber", edPhoneNumber.getText().toString());
                jsonRequest.put("address", edAddress.getText().toString());
                jsonRequest.put("shippingAddress", edShippingAddress.getText().toString());
                jsonRequest.put("country_id", countryModel.getCountry_id());
                jsonRequest.put("password", edPassword.getText().toString());
                jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
                jsonRequest.put("restaurant_id", jsonArray);

                try {
                    AppController.postUpdatedData(context, jsonRequest.toString(), new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);

                            if (jsonResponse.getInt("success") == 1) {
                                AppUtils.showToastMessage(context, getString(R.string.msg_profile_edit_success));

                                SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
                                editor.putInt(AppConstants.USER_COUNTRY, countryModel.getCountry_id());
                                editor.putString(AppConstants.USER_EMAIL_ADDRESS, edEmail.getText().toString());
                                editor.putString(AppConstants.USER_NAME, edFirstName.getText().toString() + " " + edLastName.getText().toString());
                                editor.putString(AppConstants.USER_ADDRESS, edAddress.getText().toString());
                                editor.putString(AppConstants.USER_SHIPPING_ADDRESS, edShippingAddress.getText().toString());
                                editor.putString(AppConstants.USER_FIRST_NAME, edFirstName.getText().toString());
                                editor.putString(AppConstants.USER_PHONE, edPhoneNumber.getText().toString());

                                editor.apply();

                                Intent i = new Intent();
                                setResult(Activity.RESULT_OK, i);
                                finish();
                            } else if (jsonResponse.getInt("success") == 3) {
                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                            } else if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                            } else if (jsonResponse.getInt("success") == 0) {
                                AppUtils.showToastMessage(context, getString(R.string.msg_profile_edit_failed));
                            }
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == edDateOfBirth) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            try {
                View focus = getCurrentFocus();
                if (focus != null)
                    inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            new DatePickerDialog(context, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        if (v == spSelectCountry) {
            final AlertDialog.Builder al = new AlertDialog.Builder(context);
            al.setTitle(getString(R.string.txt_select_country));
            al.setAdapter(countryAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    countryModel = countryAdapter.getItem(which);
                    spSelectCountry.setText(countryModel.getName());
                }
            });
            al.create().show();
        }
    }
}
