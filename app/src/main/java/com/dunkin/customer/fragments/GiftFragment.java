package com.dunkin.customer.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.GiftPagerAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.listener.OnGiftClick;
import com.dunkin.customer.models.GiftModel;
import com.dunkin.customer.models.RestaurantModel;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GiftFragment extends Fragment implements View.OnClickListener, OnGiftClick{


    public static int restid = -1;
//    private SmartTabLayout tabs;
    private TabLayout tabs;
    private ViewPager viewPager;
    private ImageView ivBranchHint;
    private GiftPagerAdapter pagerAdapter;
    private Context context;
    private View rootView;
    private List<GiftModel> giftModelList;
    private TextView tvRemainingPoint;
    private TextView spSelectRestaurant;
    private List<RestaurantModel> restaurantList;
    private ArrayAdapter<RestaurantModel> restaurantAdapter;
    private RestaurantModel restaurantModel;
    private String points, strBranchName;
    private GiftModel gift;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gift_list_fragment_layout, null);
        tvRemainingPoint = (TextView) rootView.findViewById(R.id.tvRemainingPoint);
        spSelectRestaurant = (TextView) rootView.findViewById(R.id.spSelectRestaurent);
        ivBranchHint = (ImageView) rootView.findViewById(R.id.ivBranchHint);
        ((NewHomeActivity)getActivity()).ivDone.setOnClickListener(this);

//        tabs = (SmartTabLayout) rootView.findViewById(R.id.tabs);
        tabs = (TabLayout) rootView.findViewById(R.id.tabs);
//        tabs.setVisibility(View.VISIBLE);
//        tabs.setDistributeEvenly(true);

//        tabs.setBackgroundColor(Color.WHITE);
//        tabs.setSelectedIndicatorColors(ContextCompat.getColor(context, android.R.color.white));
        //tabs.setTextColor(getResources().getColor(android.R.color.white));
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        rootView.findViewById(R.id.scrollContainer).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.progressLoad).setVisibility(View.GONE);
        giftModelList = new ArrayList<>();
        spSelectRestaurant.setOnClickListener(this);

        try {
            getAllGiftsData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    private void getDataFromAPI(RestaurantModel restaurantModel) throws UnsupportedEncodingException, JSONException {

        AppController.getGiftsList(context, restaurantModel, AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);
                //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                giftModelList = new ArrayList<>();

                if (jsonResponse.getInt("success") == 1) {
                    giftModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("gifts").toString(), new TypeReference<List<GiftModel>>() {
                    });
                    ivBranchHint.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                } else if (jsonResponse.getInt("success") == 0) {
                    ivBranchHint.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.GONE);
                    AppUtils.showErrorDialog(context, context.getResources().getString(R.string.msg_no_gift_available2));
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }

                String tempPoint = null;
                if (AppUtils.isNotNull(jsonResponse.getString("userpoints"))) {
                    if (jsonResponse.getString("userpoints").contains(","))
                        tempPoint = jsonResponse.getString("userpoints").replaceAll(",", "");
                    else
                        tempPoint = jsonResponse.getString("userpoints");
                }
                tvRemainingPoint.setText(getString(R.string.txt_remaining_point, AppUtils.CurrencyFormat(Double.parseDouble(tempPoint))));

                points = jsonResponse.getString("userpoints");

                List<GiftModel> giftList = new ArrayList<>();
                List<GiftModel> giftReservedList = new ArrayList<>();

                if (giftModelList != null && giftModelList.size() > 0) {
                    for (GiftModel gift : giftModelList) {
                        if (gift.getGiftType() == 1) {
                            giftList.add(gift);
                        } else {
                            giftReservedList.add(gift);
                        }
                    }
                }

                Map<String, List<GiftModel>> data = new HashMap<>();

                data.put(getString(R.string.tab_wait_for_me), giftReservedList);
                data.put(getString(R.string.tab_on_the_go), giftList);

                List<String> titles = new ArrayList<>();
                titles.add(getString(R.string.tab_wait_for_me));
                titles.add(getString(R.string.tab_on_the_go));
                tabs.setBackgroundColor(ContextCompat.getColor(context, R.color.home_welcome_text));

                pagerAdapter = new GiftPagerAdapter(getChildFragmentManager(), context, titles, data);
                viewPager.setAdapter(pagerAdapter);
                tabs.setupWithViewPager(viewPager);
            }
        });
    }

    private void getAllGiftsData() throws UnsupportedEncodingException, JSONException {

        AppController.getAllGiftsList(context, AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1), new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {

                JSONObject jsonResponse = new JSONObject((String) result);
                //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                giftModelList = new ArrayList<>();

                if (jsonResponse.getInt("success") == 1) {
                    giftModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("gifts").toString(), new TypeReference<List<GiftModel>>() {
                    });
                    ivBranchHint.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);
                } else if (jsonResponse.getInt("success") == 0) {
                    ivBranchHint.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.GONE);
                    AppUtils.showErrorDialog(context, context.getResources().getString(R.string.msg_no_gift_available2));
                }else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                }

                String tempPoint = null;
                if (AppUtils.isNotNull(jsonResponse.getString("userpoints"))) {
                    if (jsonResponse.getString("userpoints").contains(","))
                        tempPoint = jsonResponse.getString("userpoints").replaceAll(",", "");
                    else
                        tempPoint = jsonResponse.getString("userpoints");
                }
                tvRemainingPoint.setText(getString(R.string.txt_remaining_point, AppUtils.CurrencyFormat(Double.parseDouble(tempPoint))));

                points = jsonResponse.getString("userpoints");

                List<GiftModel> giftList = new ArrayList<>();
                List<GiftModel> giftReservedList = new ArrayList<>();

                if (giftModelList != null && giftModelList.size() > 0) {
                    for (GiftModel gift : giftModelList) {
                        if (gift.getGiftType() == 1) {
                            giftList.add(gift);
                        } else {
                            giftReservedList.add(gift);
                        }
                    }
                }

                Map<String, List<GiftModel>> data = new HashMap<>();

                data.put(getString(R.string.tab_wait_for_me), giftReservedList);
                data.put(getString(R.string.tab_on_the_go), giftList);

                List<String> titles = new ArrayList<>();
                titles.add(getString(R.string.tab_wait_for_me));
                titles.add(getString(R.string.tab_on_the_go));
                tabs.setBackgroundColor(ContextCompat.getColor(context, R.color.home_welcome_text));

                pagerAdapter = new GiftPagerAdapter(getChildFragmentManager(), context, titles, data);
                viewPager.setAdapter(pagerAdapter);
                tabs.setupWithViewPager(viewPager);
            }
        });
    }

    public void updateUserPoint(String points) {
        String tempPoint = null;
        if (AppUtils.isNotNull(points)) {
            if (points.contains(","))
                tempPoint = points.replaceAll(",", "");
            else
                tempPoint = points;
        }
        tvRemainingPoint.setText(getString(R.string.txt_remaining_point, AppUtils.CurrencyFormat(Double.parseDouble(tempPoint))));

        this.points = points;
        ReservedGiftFragment.giftAdapter.notifyDataSetChanged();
        NonReservedGiftFragment.giftAdapter.notifyDataSetChanged();
    }

    public String getUserPoints() {
        return points;
    }

    @Override
    public void onClick(View v) {
        if (v == spSelectRestaurant) {
            try {
                AppController.getRestaurantList(context, true, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {

                        JSONObject jsonResponse = new JSONObject((String) result);
                        //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                        if (jsonResponse.getInt("success") == 1) {

                            restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                            });

                            restaurantAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, restaurantList);

                            openDialog(getString(R.string.txt_select_restaurant));
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }else {
                            if(jsonResponse.getInt("success") != 99) {
                                AppUtils.showToastMessage(context, getString(R.string.system_error));
                            }
                        }
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else if(v.getId() == R.id.ivDone){
            if(gift != null){
                if(restid > 0){
                    addGift(gift);
                }else {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.app_name))
                            .setMessage(context.getString(R.string.select_branch))
                            .setPositiveButton(context.getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
            }else {
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.app_name))
                        .setMessage(context.getString(R.string.select_gift))
                        .setPositiveButton(context.getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        }
    }

    private void openDialog(String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        alert.setAdapter(restaurantAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restaurantModel = restaurantAdapter.getItem(which);
                restid = restaurantModel.getRestaurantId();
                strBranchName = restaurantModel.getRestaurantName();
                spSelectRestaurant.setText(restaurantModel.getRestaurantName());
//                try {
//                    getDataFromAPI(restaurantModel);
//                } catch (UnsupportedEncodingException | JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        alert.create().show();
    }

    private void addGift(final GiftModel gift){
        String tempString1, tempString2;
        if (gift.getPoints().contains(","))
            tempString1 = gift.getPoints().replaceAll(",", "");
        else
            tempString1 = gift.getPoints();

        if (points.contains(","))
            tempString2 = points.replaceAll(",", "");
        else
            tempString2 = points;

        if (Double.parseDouble(tempString1) > Double.parseDouble(tempString2)) {
            AppUtils.showToastMessage(context, context.getString(R.string.txt_gift_reserve_success_2));
        } else {
            final JSONObject jsonRequest = new JSONObject();
            try {
                jsonRequest.put("email", AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
                jsonRequest.put("country_id", AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1));
                jsonRequest.put("gift_id", gift.getGiftId());
                jsonRequest.put("gift_point", gift.getPoints());
                jsonRequest.put("gift_type", gift.getGiftType());
                jsonRequest.put("restaurant_id", GiftFragment.restid);
                jsonRequest.put("lang_flag", AppUtils.getAppPreference(context).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

                try {
                    AppController.postGiftData(context, jsonRequest.toString(), new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {

                            JSONObject jsonResponse = new JSONObject((String) result);
                            //Dunkin_Log.d("DataResponse", jsonResponse.toString());
                            if (jsonResponse.getInt("success") == 0) {
                                AppUtils.showToastMessage(context, context.getString(R.string.txt_gift_reserve_success_0));
                            }

                            if (jsonResponse.getInt("success") == 1) {
                                AppUtils.showToastMessage(context, context.getString(R.string.txt_gift_reserve_success_1));
                            }

                            if (jsonResponse.getInt("success") == 2) {
                                AppUtils.showToastMessage(context, context.getString(R.string.txt_gift_reserve_success_2));
                            }

                            if (jsonResponse.getInt("success") == 4) {
                                //AppUtils.showErrorDialog(context, jsonResponse.getString("message"));
                                AppUtils.showErrorDialog(context, context.getString(R.string.msg_gift_quantity_not_avail));
                                //((HomeActivity)context).navigate(AppConstants.MENU_GIFT_STORE, 0);
                            }
                            if (jsonResponse.getInt("success") == 100) {
                                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                            }
                            points = jsonResponse.getString("remainingPoint");
                            restid = 0;
                            GiftFragment.this.gift = null;
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
    public void onGiftConfirm(GiftModel gift) {
        this.gift = gift;
        try {
            AppController.getRestaurantList(context, true, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {

                    JSONObject jsonResponse = new JSONObject((String) result);
                    //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                    if (jsonResponse.getInt("success") == 1) {

                        restaurantList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("restaurantList").toString(), new TypeReference<List<RestaurantModel>>() {
                        });

                        restaurantAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, restaurantList);

                        openDialog(getString(R.string.txt_select_restaurant));
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else {
                        if(jsonResponse.getInt("success") != 99) {
                            AppUtils.showToastMessage(context, getString(R.string.system_error));
                        }
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
