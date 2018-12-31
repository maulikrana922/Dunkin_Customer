package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.UpdateUserProfileActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CountriesModel;
import com.dunkin.customer.models.RestaurantModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyProfileFragment extends Fragment {

    SharedPreferences myPrefs;
    MyProfileFragment fragment;
    private Context ctx;
    private TextView tvFirstName, tvLastName, tvCountry, tvEmail, tvPhoneNumber, tvAddress, tvShippingAddress, tvDateOfBirth, tvFavoriteRestaurant;
    private ImageView imgQrCode;
    private JSONObject jsonUserDetail;
    private DBAdapter dbAdapter;
    //private ProgressBar progressLoading;
    private ScrollView scrollContainer;
    private List<RestaurantModel> restaurantList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragment = this;
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_my_profile, null);
        myPrefs = AppUtils.getAppPreference(ctx);

        dbAdapter = new DBAdapter(ctx);
        //progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        imgQrCode = (ImageView) rootView.findViewById(R.id.imgQrCode);
        tvCountry = (TextView) rootView.findViewById(R.id.tvCountry);
        tvFirstName = (TextView) rootView.findViewById(R.id.tvFirstName);
        tvLastName = (TextView) rootView.findViewById(R.id.tvLastName);
        tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
        tvPhoneNumber = (TextView) rootView.findViewById(R.id.tvPhoneNumber);
        tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
        tvShippingAddress = (TextView) rootView.findViewById(R.id.tvShippingAddress);
        tvFavoriteRestaurant = (TextView) rootView.findViewById(R.id.tvFavoriteRestaurant);
        tvFavoriteRestaurant.setSelected(true);

        tvDateOfBirth = (TextView) rootView.findViewById(R.id.tvDateOfBirth);

        scrollContainer = (ScrollView) rootView.findViewById(R.id.scrollContainer);

        scrollContainer.setVisibility(View.GONE);

        try {
            getUserProfileData();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (AppUtils.checkNetwork(ctx) != 0) {
            inflater.inflate(R.menu.editprofile_btn_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_profile) {
            try {
                if (jsonUserDetail != null && AppUtils.isNotNull(jsonUserDetail.toString()) && !TextUtils.isEmpty(jsonUserDetail.toString())) {
                    Intent i = new Intent(ctx, UpdateUserProfileActivity.class);
                    i.putExtra("user", jsonUserDetail.toString());
                    fragment.startActivityForResult(i, 101);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserProfileData() throws UnsupportedEncodingException, JSONException {
        AppController.getUserProfileFragment(ctx, myPrefs.getString(AppConstants.USER_EMAIL_ADDRESS, "")
                , true, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
                        JSONObject jsonResponse = new JSONObject((String) result);
                        //progressLoading.setVisibility(View.GONE);
                        if (jsonResponse.getInt("success") == 1) {
                            scrollContainer.setVisibility(View.VISIBLE);
                            jsonUserDetail = jsonResponse.getJSONObject("userDetail");

                            dbAdapter.open();
                            dbAdapter.addOfflineData(AppConstants.OF_PROFILE, (String) result);
                            List<CountriesModel> countriesModelList = dbAdapter.getAllCountries();
                            dbAdapter.close();

                            for (CountriesModel cm : countriesModelList) {
                                if (cm.getCountry_id() == AppUtils.getAppPreference(ctx).getInt(AppConstants.USER_COUNTRY, 0)) {
                                    tvCountry.setText(cm.getName());
                                    break;
                                }
                            }

                            tvFirstName.setText(jsonUserDetail.getString("firstName"));
                            tvLastName.setText(jsonUserDetail.getString("lastName"));
                            tvEmail.setText(jsonUserDetail.getString("email"));

                            try {
                                if (jsonUserDetail.getString("dob").equalsIgnoreCase("00-00-0000")) {
                                    tvDateOfBirth.setText(R.string.str_default_date);
                                } else {
                                    String dob = AppUtils.getFormattedDate(new SimpleDateFormat(AppConstants.MM_DD_YYYY_SERVER, Locale.getDefault()).parse(jsonUserDetail.getString("dob")));
                                    tvDateOfBirth.setText(dob);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            tvAddress.setText(jsonUserDetail.getString("address"));
                            tvShippingAddress.setText(jsonUserDetail.getString("shippingAddress"));
                            tvPhoneNumber.setText(jsonUserDetail.getString("phone"));
                            if (jsonUserDetail.getString("qrCode").isEmpty()) {
                                imgQrCode.setVisibility(View.GONE);
                            } else {
                                AppUtils.setImage(imgQrCode, jsonUserDetail.getString("qrCode"));
                            }

                            if (AppUtils.isNotNull(jsonUserDetail.getString("restaurant_id"))) {
                                String[] restaurantId = jsonUserDetail.getString("restaurant_id").split(",");
                                getRestaurantList(restaurantId);
                            } else
                                tvFavoriteRestaurant.setText(ctx.getString(R.string.txt_your_favorite_restaurant));

                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(getActivity(), jsonResponse.getString("message"));
                        }else {
                            if(jsonResponse.getInt("success") == 99) {
                                displayDialog(jsonResponse.getString("message"));
                            }else{
                                AppUtils.showToastMessage(getActivity(), getString(R.string.system_error));
                            }
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
                        startActivity(new Intent(ctx, RegisterActivity.class));
                        ((Activity) ctx).finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.show();
    }

    private void getRestaurantList(final String[] restIds) {
        try {
            AppController.getRestaurantList(ctx, false, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    StringBuilder sbRestaurantName = new StringBuilder();
                    boolean firstTime = true;

                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        restaurantList = new ArrayList<>();
                        restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                        });

                        if (restIds != null && restIds.length != 0) {
                            for (int j = 0; j < restaurantList.size(); j++) {
                                for (String restId : restIds) {
                                    if (restId.equalsIgnoreCase(String.valueOf(restaurantList.get(j).getRestaurantId()))) {
                                        if (firstTime) {
                                            firstTime = false;
                                        } else {
                                            sbRestaurantName.append(", ");
                                        }
                                        sbRestaurantName.append(restaurantList.get(j).getRestaurantName());
                                    }
                                }
                            }
                            tvFavoriteRestaurant.setText(sbRestaurantName);
                        }else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(ctx, jsonResponse.getString("message"));
                        } else
                            tvFavoriteRestaurant.setText(ctx.getString(R.string.txt_your_favorite_restaurant));
                    } else if (jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }else {
                        AppUtils.showToastMessage(ctx, getString(R.string.system_error));
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 101) {
            try {
                getUserProfileData();
//                TextView tv = (TextView) getActivity().findViewById(R.id.FirstName);
//                tv.setText(getString(R.string.txt_hello, myPrefs.getString(AppConstants.USER_FIRST_NAME, "")));
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
