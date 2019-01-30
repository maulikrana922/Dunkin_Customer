package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.NewWalletNoteListAdapter;
import com.dunkin.customer.adapters.WalletAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.WalletModel;
import com.dunkin.customer.models.WalletRedeemPoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyWalletFragment extends Fragment {

    //    SmartTabLayout tabs;
//    TabLayout tabs;
    ViewPager viewPager;
    Animation animFadein;
    private Context context;
    private WalletAdapter walletAdapter;
    private TextView emptyView, tvTabHistory, tvTabPointHistory;
    private LinearLayout scrollContainer,llTabs;
    private ProgressBar progressLoading;
    private DBAdapter dbAdapter;
    private RelativeLayout mainLayout;
    private ImageView imgQR;
    private WalletRedeemPoint walletRedeemPoint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NewHomeActivity) getActivity()).setToolbarView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_wallet_new, null);
        dbAdapter = new DBAdapter(context);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        scrollContainer = (LinearLayout) rootView.findViewById(R.id.scrollContainer);
//        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);
        tvTabHistory = (TextView) rootView.findViewById(R.id.tvTabHistory);
        tvTabPointHistory = (TextView) rootView.findViewById(R.id.tvTabPointHistory);
        llTabs=(LinearLayout)rootView.findViewById(R.id.llTabs);
//        tabs = (SmartTabLayout) rootView.findViewById(R.id.tabs);
//        tabs = (TabLayout) rootView.findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true);
        tvTabHistory.setSelected(true);
        tvTabPointHistory.setSelected(false);
        
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);

        tvTabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

        tvTabPointHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.w("onPageScrolled", String.valueOf(position));
            }

            @Override
            public void onPageSelected(int position) {
                Log.w("onPageSelected", String.valueOf(position));
                if (position == 0) {
                    tvTabHistory.setSelected(true);
                    tvTabPointHistory.setSelected(false);
                } else if (position == 1) {
                    tvTabHistory.setSelected(false);
                    tvTabPointHistory.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.w("onPageScrollStateChange", String.valueOf(state));
            }
        });
        emptyView = (TextView) rootView.findViewById(R.id.emptyElement);
        imgQR = (ImageView) rootView.findViewById(R.id.imgProfileQR);
//        AppUtils.setImage(imgQR, AppUtils.getAppPreference(context).getString(AppConstants.USER_PROFILE_QR, ""));
        try {
            getDataFromAPI();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void getDataFromAPI() throws UnsupportedEncodingException {
        emptyView.setVisibility(View.GONE);
        AppController.getMyWallet(context, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
//                mainLayout.startAnimation(animFadein);
                imgQR.setVisibility(View.VISIBLE);
                llTabs.setVisibility(View.VISIBLE);
                String apiResponse = (String) result;
                try {
                    walletRedeemPoint=getDataFromAPIWalletRedeem(apiResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private WalletRedeemPoint getDataFromAPIWalletRedeem(final String apiResponse) throws UnsupportedEncodingException, JSONException {
        AppController.getWalletRedeempoint(context, AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, "")
                , String.valueOf(AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1)), new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
                        JSONObject jsonResponse = new JSONObject((String) result);
                        progressLoading.setVisibility(View.GONE);
                        if (jsonResponse.getInt("success") == 1) {
                            walletRedeemPoint = new Gson().fromJson(jsonResponse.toString(), WalletRedeemPoint.class);
                            getwalletApiResponse(apiResponse);
//                            String amount = walletRedeemPoint.wallet.total+ " " + walletRedeemPoint.wallet.currency;
//                            String points = walletRedeemPoint.wallet.usedWalletPoint;
//                            txtTotalAmount.setText(amount);
//                            txtWalletAmountPoint.setText(points);
////                            loadAnimation(flTotalAmount);
////                            loadAnimation(flRemainingPoints);
//                            progressLoading.setVisibility(View.GONE);
//                            lvWalletNoteList.setNestedScrollingEnabled(true);
//                            if (wallet.getNotes() != null && wallet.getNotes().size() > 0) {
//                                emptyElement.setVisibility(View.GONE);
//                                lvWalletNoteList.setVisibility(View.VISIBLE);
//
////            WalletNoteListAdapter walletNoteListAdapter = new WalletNoteListAdapter(getActivity(), wallet.getNotes());
////            lvWalletNoteList.setAdapter(walletNoteListAdapter);
////            lvWalletNoteList.setEmptyView(rootView.findViewById(R.id.emptyElement));
//                                NewWalletNoteListAdapter walletNoteListAdapter = new NewWalletNoteListAdapter(getActivity(), wallet.getNotes());
//                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//                                lvWalletNoteList.setLayoutManager(layoutManager);
//                                lvWalletNoteList.setAdapter(walletNoteListAdapter);
////            lvWalletNoteList.setEmptyView(rootView.findViewById(R.id.emptyElement));
//                            } else {
//                                emptyElement.setVisibility(View.VISIBLE);
//                                lvWalletNoteList.setVisibility(View.GONE);
//                            }
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        } else if (jsonResponse.getInt("success") == 99) {
                            displayDialog(jsonResponse.getString("message"));
                        } else {

                        }
                    }
                });
        return walletRedeemPoint;
    }

    private void displayDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, WalletNoteListFragment.class));
                        ((Activity) context).finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.show();
    }

    public void getwalletApiResponse(String apiResponse) throws JSONException, IOException {
        if (!apiResponse.equalsIgnoreCase(AppConstants.TIME_OUT)) {
            JSONObject jsonResponse = new JSONObject(apiResponse);
            progressLoading.setVisibility(View.GONE);
            scrollContainer.setVisibility(View.VISIBLE);

            dbAdapter.open();
            dbAdapter.addOfflineData(AppConstants.OF_WALLET, apiResponse);
            dbAdapter.close();

            if (jsonResponse.getInt("success") == 1) {
                List<WalletModel> walletModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("wallet").toString(), new TypeReference<List<WalletModel>>() {
                });

                String remainingPoints = jsonResponse.getString("points");
                walletAdapter = new WalletAdapter(context, ((AppCompatActivity) context).getSupportFragmentManager(), walletModelList, remainingPoints,walletRedeemPoint);
                viewPager.setAdapter(walletAdapter);
                viewPager.setOffscreenPageLimit(walletModelList.size());
                viewPager.setCurrentItem(1);
                AppUtils.setImage(imgQR, jsonResponse.getString(AppConstants.USER_BANNER));
//                        tabs.setupWithViewPager(viewPager);
//                        tabs.setViewPager(viewPager);
            } else if (jsonResponse.getInt("success") == 100) {
                AppUtils.showToastMessage(context, jsonResponse.getString("message"));
            } else {
                scrollContainer.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.no_data_error));
            }
        } else {
            progressLoading.setVisibility(View.GONE);
            scrollContainer.setVisibility(View.VISIBLE);

            dbAdapter.open();
            String resultDB = dbAdapter.getOfflineData(AppConstants.OF_WALLET);
            dbAdapter.close();
            JSONObject jsonResponse = new JSONObject(resultDB);

            if (jsonResponse.getInt("success") == 1) {
                List<WalletModel> walletModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("wallet").toString(), new TypeReference<List<WalletModel>>() {
                });

                String remainingPoints = jsonResponse.getString("points");

                walletAdapter = new WalletAdapter(context, ((AppCompatActivity) context).getSupportFragmentManager(), walletModelList, remainingPoints,walletRedeemPoint);
                viewPager.setAdapter(walletAdapter);
                viewPager.setCurrentItem(1);
                viewPager.setOffscreenPageLimit(walletModelList.size());
//                        tabs.setupWithViewPager(viewPager);
//                        tabs.setViewPager(viewPager);
            } else {
                scrollContainer.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.no_data_error));
            }
        }
    }
}
