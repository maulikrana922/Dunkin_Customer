package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.FavoriteRestaurantAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ImageDialog;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    SharedPreferences myPrefs;
    private View rootView;
    private Context context;
    private RelativeLayoutButton btnRegister, btnLogin;

    private TextView spSelectCountry, edDateOfBirth, spSetFavoriteRestaurant;
    private EditText edFirstName, edLastName, edEmail, edPassword, edConfirmPassword, edPhoneNumber, edAddress, edShippingAddress;
    private CountriesModel countryModel;
    private ArrayAdapter<CountriesModel> countryAdapter;
    private Calendar calender;
    private CheckBox cbAddress;
    private List<RestaurantModel> restaurantList;
    private ListView lvRestaurantList;
    private JSONArray jsonArray;
    private FavoriteRestaurantAdapter restaurantAdapter;

    private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calender.set(Calendar.YEAR, year);
            calender.set(Calendar.MONTH, monthOfYear);
            calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //edDateOfBirth.setText((AppUtils.getFormattedDate(calender.getTime())));
            edDateOfBirth.setText(String.format("%d/%d/%d", dayOfMonth, (monthOfYear + 1), year));
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        calender = Calendar.getInstance();
        initializeViews();
        return rootView;
    }

    // INITIALIZATION OF VIEWS
    private void initializeViews() {
        myPrefs = AppUtils.getAppPreference(context);
        DBAdapter db = new DBAdapter(context);
        db.open();
        countryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, db.getAllCountries());
        db.close();
        edFirstName = (EditText) rootView.findViewById(R.id.edFirstName);
        edLastName = (EditText) rootView.findViewById(R.id.edLastName);
        edDateOfBirth = (TextView) rootView.findViewById(R.id.edDateOfBirth);
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edPassword = (EditText) rootView.findViewById(R.id.edPassword);
        edConfirmPassword = (EditText) rootView.findViewById(R.id.edConfirmPassword);
        edPhoneNumber = (EditText) rootView.findViewById(R.id.edPhoneNumber);
        edAddress = (EditText) rootView.findViewById(R.id.edAddress);
        edShippingAddress = (EditText) rootView.findViewById(R.id.edShippingAddress);
        cbAddress = (CheckBox) rootView.findViewById(R.id.cbAddress);

        spSelectCountry = (TextView) rootView.findViewById(R.id.spSelectCountry);
        spSetFavoriteRestaurant = (TextView) rootView.findViewById(R.id.spSetFavoriteRestaurant);
        spSetFavoriteRestaurant.setSelected(true);

        btnRegister = (RelativeLayoutButton) rootView.findViewById(R.id.btnRegister);
        btnRegister.imgIcon.setImageResource(R.drawable.ic_btn_signup);
        btnRegister.btnText.setText(getString(R.string.btn_signup));

        btnLogin = (RelativeLayoutButton) rootView.findViewById(R.id.btnLogin);
        btnLogin.imgIcon.setImageResource(R.drawable.ic_btn_login);
        btnLogin.btnText.setText(getString(R.string.btn_back));

        cbAddress.setEnabled(false);

        cbAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edShippingAddress.setText(edAddress.getText().toString());
                } else {
                    edShippingAddress.setText("");
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
            }
        });

        btnLogin.setOnClickListener(this);
        spSelectCountry.setOnClickListener(this);
        spSetFavoriteRestaurant.setOnClickListener(this);
        edDateOfBirth.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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

        if (v == spSetFavoriteRestaurant) {
            try {
                if (countryModel != null) {
                    if (restaurantList != null && restaurantList.size() > 0) {
                        bottomSheetFavRestaurant(restaurantList);
                    } else {
                        AppController.getRestaurantListRegister(context, countryModel.getCountry_id(), AppConstants.LANG_EN, true, new Callback() {
                            @Override
                            public void run(Object result) throws JSONException, IOException {

                                JSONObject jsonResponse = new JSONObject((String) result);
                                //Log.i("DataResponse", jsonResponse.toString());
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
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (v == btnLogin) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
        }

        if (v == btnRegister) {
            try {
                registerUserInformation();
            } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        if (v == edDateOfBirth) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            try {
                View focus = getActivity().getCurrentFocus();
                if (focus != null)
                    inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            new DatePickerDialog(context, datePicker, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private void bottomSheetFavRestaurant(final List<RestaurantModel> list) {
        try {
            final View view = getLayoutInflater(null).inflate(R.layout.bottom_sheet_layout_fav_restaurant, null);

            lvRestaurantList = (ListView) view.findViewById(R.id.lvRestaurantList);

            if (jsonArray != null && jsonArray.length() > 0) {
                List<RestaurantModel> dummyList = new ArrayList<>();
                dummyList.addAll(list);
                for (int i = 0; i < dummyList.size(); i++) {
                    if (dummyList.get(i).isSelected()) {
                        dummyList.get(i).setSelected(true);
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

    // MAKE REGISTRATION OF USER
    private void registerUserInformation() throws UnsupportedEncodingException, JSONException, ParseException {

       /* String year = (String) spExpiryYears.getSelectedItem();
        String date = (String) spExpiryMonth.getSelectedItem();*/

        if (edFirstName.getText().length() == 0) {
            AppUtils.showError(edFirstName, getString(R.string.empty_first_name));
        } else if (edLastName.getText().length() == 0) {
            AppUtils.showError(edLastName, getString(R.string.empty_last_name));
        } else if (edDateOfBirth.getText().length() == 0) {
            AppUtils.showError(edDateOfBirth, getString(R.string.empty_date_of_birth));
        } else if (AppUtils.isValidDate(edDateOfBirth.getText().toString(), AppConstants.DD_MM_YYYY_SLASH) == 1) {
            AppUtils.showError(edDateOfBirth, getString(R.string.min_val_date_of_birth));
        } else if (edEmail.getText().length() == 0) {
            AppUtils.showError(edEmail, getString(R.string.empty_email));
        } else if (!Pattern.matches(String.valueOf(Patterns.EMAIL_ADDRESS), edEmail.getText().toString())) {
            AppUtils.showError(edEmail, getString(R.string.invalid_email));
        } else if (edPassword.getText().length() == 0) {
            AppUtils.showError(edPassword, getString(R.string.empty_password));
        } else if (edPassword.getText().length() < 6) {
            AppUtils.showError(edPassword, getString(R.string.min_length_password));
        } else if (edConfirmPassword.getText().length() == 0) {
            AppUtils.showError(edConfirmPassword, getString(R.string.empty_conform_password));
        } else if (edConfirmPassword.getText().length() < 6) {
            AppUtils.showError(edConfirmPassword, getString(R.string.min_length_password));
        } else if (!edConfirmPassword.getText().toString().equals(edPassword.getText().toString())) {
            AppUtils.showError(edConfirmPassword, getString(R.string.password_is_not_match));
        } else if (edPhoneNumber.getText().length() == 0) {
            AppUtils.showError(edPhoneNumber, getString(R.string.empty_phone_number));
        } else if (edPhoneNumber.getText().length() < 8) {
            AppUtils.showError(edPhoneNumber, getString(R.string.min_length_phone_number));
        } else if (edAddress.getText().length() == 0) {
            AppUtils.showError(edAddress, getString(R.string.empty_address));
        } else if (edShippingAddress.getText().length() == 0) {
            AppUtils.showError(edShippingAddress, getString(R.string.empty_shipping_address));
        } else if (countryModel == null) {
            AppUtils.showError(spSelectCountry, getString(R.string.txt_select_country));
        } else {
            JSONObject jsonRequest = new JSONObject();

            jsonRequest.put("firstName", edFirstName.getText().toString());
            jsonRequest.put("lastName", edLastName.getText().toString());
            jsonRequest.put("email", edEmail.getText().toString());
            jsonRequest.put("dob", edDateOfBirth.getText().toString());
            jsonRequest.put("password", edPassword.getText().toString());
            jsonRequest.put("phoneNumber", edPhoneNumber.getText().toString());
            jsonRequest.put("address", edAddress.getText().toString());
            jsonRequest.put("shippingAddress", edShippingAddress.getText().toString());
            jsonRequest.put("restaurant_id", jsonArray);
            jsonRequest.put("device_id", myPrefs.getString(AppConstants.GCM_TOKEN_ID, ""));
            jsonRequest.put("is_device_android", 1);
            jsonRequest.put("country_id", countryModel.getCountry_id());
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

            AppController.RegisterCustomer(context, jsonRequest.toString(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    try {
                        JSONObject jsonResponse = new JSONObject((String) result);


                        if (jsonResponse.getInt("success") == 1) {
                            AppUtils.showToastMessage(context, getString(R.string.msg_register_success));
                            JSONObject jsonUser = jsonResponse.getJSONObject("userDetail");

                            SharedPreferences.Editor editor = myPrefs.edit();
                            editor.putString(AppConstants.USER_EMAIL_ADDRESS, edEmail.getText().toString());
                            editor.putInt(AppConstants.USER_COUNTRY, countryModel.getCountry_id());
                            editor.putString(AppConstants.USER_ADDRESS, edAddress.getText().toString());
                            editor.putString(AppConstants.USER_SHIPPING_ADDRESS, edShippingAddress.getText().toString());
                            editor.putString(AppConstants.USER_PHONE, edPhoneNumber.getText().toString());

                            editor.putString(AppConstants.USER_NAME, edFirstName.getText().toString() + " " + edLastName.getText().toString());
                            editor.putString(AppConstants.USER_FIRST_NAME, edFirstName.getText().toString());
                            editor.putString(AppConstants.USER_PROFILE_QR, jsonUser.getString("qrCode"));
                            editor.apply();


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("You are successfully registered. Please check your email to activate your account.")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                                                String version = String.valueOf(pInfo.versionCode);
                                                fetchAllSetting(version);
                                            } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                                                e.printStackTrace();
                                            } catch(Exception e)
                                            {
                                                e.printStackTrace();
                                            }
//                                            startActivity(new Intent(context, HomeActivity.class));
//                                            startActivity(new Intent(context, RegisterActivity.class));
//                                            ((Activity) context).finish();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.setTitle(getResources().getString(R.string.app_name));
                            alert.show();


                        } else if (jsonResponse.getInt("success") == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.msg_register_exist));
                        } else if (jsonResponse.getInt("success") == 3) {
                            AppUtils.showToastMessage(context, getString(R.string.msg_register_error));
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        } else if (jsonResponse.getInt("success") == 99) {
                            //AppUtils.showToastMessage(context, jsonResponse.getString("message"));

                            displayDialog(jsonResponse.getString("message"));

                        } else {
                            AppUtils.showToastMessage(context, getString(R.string.system_error));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppUtils.showToastMessage(context, getString(R.string.system_error));
                    }
                }
            });
        }
    }

    private void fetchAllSetting(String version) throws UnsupportedEncodingException, JSONException, ParseException {

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("email", AppUtils.getAppPreference(context)
                .getString(AppConstants.USER_EMAIL_ADDRESS, ""));
        jsonRequest.put("country_id", AppUtils.getAppPreference(context)
                .getInt(AppConstants.USER_COUNTRY, -1));
        jsonRequest.put("type", "1");
        jsonRequest.put("version", version);

        AppController.fetchAllSetting(context, jsonRequest.toString(), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                try {
                    JSONObject jsonResponse = new JSONObject((String) result);


                    if (jsonResponse.getString("success").equals("1")) {
                        JSONObject jsonObject = jsonResponse.getJSONObject("data");
                        String image_url = jsonObject.getString("Image");
                        ImageDialog.newInstance(context, image_url, true).show();

                    } else if (jsonResponse.getString("success").equals("0")) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    } else {
                        AppUtils.showToastMessage(context, getString(R.string.system_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppUtils.showToastMessage(context, getString(R.string.system_error));
                }
            }
        });
    }

    private void displayDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity) context).finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.show();
    }
}
