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
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.dunkin.customer.dialogs.ImageFBDialog;
import com.dunkin.customer.models.CountriesModel;
import com.dunkin.customer.models.RestaurantModel;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    SharedPreferences myPrefs;
    /*----------Facebook Login---------------*/
    CallbackManager callbackManager;
    private View rootView;
    private Context context;
    private Button btnRegister, btnLogin;
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
    private String facebook_id, fb_first_name, fb_last_name, fb_email, fb_birthday;

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
//        edPhoneNumber = (EditText) rootView.findViewById(R.id.edPhoneNumber);
        edAddress = (EditText) rootView.findViewById(R.id.edAddress);
        edShippingAddress = (EditText) rootView.findViewById(R.id.edShippingAddress);
        cbAddress = (CheckBox) rootView.findViewById(R.id.cbAddress);

        spSelectCountry = (TextView) rootView.findViewById(R.id.spSelectCountry);
        spSetFavoriteRestaurant = (TextView) rootView.findViewById(R.id.spSetFavoriteRestaurant);
        spSetFavoriteRestaurant.setSelected(true);

        btnRegister = (Button) rootView.findViewById(R.id.btnRegister);
//        btnRegister.imgIcon.setImageResource(R.drawable.ic_btn_signup);
//        btnRegister.btnText.setText(getString(R.string.btn_signup));

        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
//        btnLogin.imgIcon.setImageResource(R.drawable.ic_btn_login);
//        btnLogin.btnText.setText(getString(R.string.btn_back));

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
        callbackManager = CallbackManager.Factory.create();

        getFBData();
    }

    private void getFBData() {
        if (getArguments() != null) {
            if (getArguments().containsKey(AppConstants.USER_FB_ID))
                facebook_id = getArguments().getString(AppConstants.USER_FB_ID);
            if (getArguments().containsKey(AppConstants.USER_FIRST_NAME))
                fb_first_name = getArguments().getString(AppConstants.USER_FIRST_NAME);
            if (getArguments().containsKey(AppConstants.USER_LAST_NAME))
                fb_last_name = getArguments().getString(AppConstants.USER_LAST_NAME);
            if (getArguments().containsKey(AppConstants.USER_EMAIL_ADDRESS))
                fb_email = getArguments().getString(AppConstants.USER_EMAIL_ADDRESS);
            if (getArguments().containsKey(AppConstants.USER_DOB))
                fb_birthday = getArguments().getString(AppConstants.USER_DOB);

            edFirstName.setText(fb_first_name);
            edLastName.setText(fb_last_name);
            edEmail.setText(fb_email);
            edDateOfBirth.setText(fb_birthday);
        }
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
                                //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                                if (jsonResponse.getInt("success") == 1) {
                                    restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                                    });
                                    bottomSheetFavRestaurant(restaurantList);
                                } else if (jsonResponse.getInt("success") == 100) {
                                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                } else {
                                    if (jsonResponse.getInt("success") != 99) {
                                        AppUtils.showToastMessage(context, getString(R.string.system_error));
                                    }
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
            if (facebook_id != null) {
                try {
                    registerUserWithFacebook(facebook_id);
                } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    registerUserInformation();
                } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                    e.printStackTrace();
                }
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
//        if (v == btnFBRegister) {
//            fbLogin();
//        }
    }

    private void bottomSheetFavRestaurant(final List<RestaurantModel> list) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );


            final View view =inflater.inflate(R.layout.bottom_sheet_layout_fav_restaurant, null);

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
//        } else if (edPhoneNumber.getText().length() == 0) {
//            AppUtils.showError(edPhoneNumber, getString(R.string.empty_phone_number));
//        } else if (edPhoneNumber.getText().length() < 8) {
//            AppUtils.showError(edPhoneNumber, getString(R.string.min_length_phone_number));
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
            jsonRequest.put("phoneNumber", "");
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
//                            editor.putString(AppConstants.USER_EMAIL_ADDRESS, edEmail.getText().toString());
                            editor.putInt(AppConstants.USER_COUNTRY, countryModel.getCountry_id());
                            editor.putString(AppConstants.USER_ADDRESS, edAddress.getText().toString());
                            editor.putString(AppConstants.USER_SHIPPING_ADDRESS, edShippingAddress.getText().toString());
                            editor.putString(AppConstants.USER_PHONE, "");

                            editor.putString(AppConstants.USER_NAME, edFirstName.getText().toString() + " " + edLastName.getText().toString());
                            editor.putString(AppConstants.USER_FIRST_NAME, edFirstName.getText().toString());
                            editor.putString(AppConstants.USER_PROFILE_QR, jsonUser.getString("qrCode"));
                            editor.apply();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(String.format(context.getString(R.string.msg_register_successfully), edEmail.getText().toString().trim()))
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                                                String version = String.valueOf(pInfo.versionCode);
                                                fetchAllSetting(version, edEmail.getText().toString().trim());
                                            } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
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
                            TextView messageView = (TextView) alert.findViewById(android.R.id.message);
                            messageView.setGravity(Gravity.CENTER);

                        } else if (jsonResponse.getInt("success") == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.msg_register_exist));
                        } else if (jsonResponse.getInt("success") == 3) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
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
//                        AppUtils.showToastMessage(context, getString(R.string.system_error));
                    }
                }
            });
        }
    }

    private void fetchAllSetting(String version, String email) throws UnsupportedEncodingException, JSONException, ParseException {

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("email", AppUtils.getAppPreference(context)
                .getString(AppConstants.USER_EMAIL_ADDRESS, email));
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
                        ((Activity) context).startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity) context).finish();
//                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    } else {
                        if (jsonResponse.getInt("success") != 99) {
                            AppUtils.showToastMessage(context, getString(R.string.system_error));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    AppUtils.showToastMessage(context, getString(R.string.system_error));
                }
            }
        });
    }

    private void fetchAllSettingFB(String version, String email) throws UnsupportedEncodingException, JSONException, ParseException {

        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("email", AppUtils.getAppPreference(context)
                .getString(AppConstants.USER_EMAIL_ADDRESS, email));
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
                        ImageFBDialog.newInstance(context, image_url, true).show();

                    } else if (jsonResponse.getString("success").equals("0")) {
                        ((Activity) context).startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity) context).finish();
//                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    } else {
                        if (jsonResponse.getInt("success") != 99) {
                            AppUtils.showToastMessage(context, getString(R.string.system_error));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    AppUtils.showToastMessage(context, getString(R.string.system_error));
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

    public void fbLogin() {
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String fbId = object.getString("id");
                                    if (object.has("first_name"))
                                        edFirstName.setText(object.getString("first_name"));
                                    if (object.has("last_name"))
                                        edLastName.setText(object.getString("last_name"));
                                    if (object.has("email"))
                                        edEmail.setText(object.getString("email"));
                                    if (object.has("birthday"))
                                        edDateOfBirth.setText(object.getString("birthday"));
                                    facebook_id = fbId;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        //Here we put the requested fields to be returned from the JSONObject
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("email, public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // MAKE REGISTRATION OF USER WITH FACEBOOK
    private void registerUserWithFacebook(String facebook_id) throws UnsupportedEncodingException, JSONException, ParseException {

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
            jsonRequest.put("phoneNumber", "");
            jsonRequest.put("address", edAddress.getText().toString());
            jsonRequest.put("shippingAddress", edShippingAddress.getText().toString());
            jsonRequest.put("restaurant_id", jsonArray);
            jsonRequest.put("device_id", myPrefs.getString(AppConstants.GCM_TOKEN_ID, ""));
            jsonRequest.put("is_device_android", 1);
            jsonRequest.put("country_id", countryModel.getCountry_id());
            jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));
            jsonRequest.put("facebook_id", facebook_id);

            AppController.RegisterCustomerWithFB(context, jsonRequest.toString(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    try {
                        JSONObject jsonResponse = new JSONObject((String) result);

                        if (jsonResponse.getInt("success") == 1) {
                            AppUtils.showToastMessage(context, getString(R.string.msg_register_success));
                            JSONObject jsonUser = jsonResponse.getJSONObject("userDetail");

                            SharedPreferences.Editor editor = myPrefs.edit();
//                            editor.putString(AppConstants.USER_EMAIL_ADDRESS, edEmail.getText().toString());
                            editor.putInt(AppConstants.USER_COUNTRY, countryModel.getCountry_id());
                            editor.putString(AppConstants.USER_ADDRESS, edAddress.getText().toString());
                            editor.putString(AppConstants.USER_SHIPPING_ADDRESS, edShippingAddress.getText().toString());
                            editor.putString(AppConstants.USER_PHONE, "");

                            editor.putString(AppConstants.USER_NAME, edFirstName.getText().toString() + " " + edLastName.getText().toString());
                            editor.putString(AppConstants.USER_FIRST_NAME, edFirstName.getText().toString());
                            editor.putString(AppConstants.USER_PROFILE_QR, jsonUser.getString("qrCode"));
                            editor.apply();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(String.format(context.getString(R.string.msg_register_successfully), edEmail.getText().toString().trim()))
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
//                                            Intent intent = new Intent(context, RegisterActivity.class);
//                                            intent.putExtra("isFBRegister", true);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            ((Activity) context).startActivity(intent);
                                            try {
                                                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                                                String version = String.valueOf(pInfo.versionCode);
                                                fetchAllSettingFB(version, edEmail.getText().toString().trim());
                                            } catch (UnsupportedEncodingException | JSONException | ParseException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.setTitle(getResources().getString(R.string.app_name));
                            alert.show();
                            TextView messageView = (TextView) alert.findViewById(android.R.id.message);
                            messageView.setGravity(Gravity.CENTER);

                        } else if (jsonResponse.getInt("success") == 0) {
                            AppUtils.showToastMessage(context, getString(R.string.msg_register_exist));
                        } else if (jsonResponse.getInt("success") == 3) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        } else if (jsonResponse.getInt("success") == 99) {
                            displayDialog(jsonResponse.getString("message"));
                        } else {
                            AppUtils.showToastMessage(context, getString(R.string.system_error));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
