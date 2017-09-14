package com.dunkin.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.R;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.WalletAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.WalletModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyWalletFragment extends Fragment {

    SmartTabLayout tabs;
    ViewPager viewPager;
    private Context context;
    private WalletAdapter walletAdapter;
    private TextView emptyView;
    private LinearLayout scrollContainer;
    private ProgressBar progressLoading;
    private DBAdapter dbAdapter;
    private RelativeLayout mainLayout;
    Animation animFadein;
    private ImageView imgQR;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_wallet, null);
        dbAdapter = new DBAdapter(context);

        progressLoading = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        scrollContainer = (LinearLayout) rootView.findViewById(R.id.scrollContainer);
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);

        tabs = (SmartTabLayout) rootView.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        emptyView = (TextView) rootView.findViewById(R.id.emptyElement);
        imgQR = (ImageView) rootView.findViewById(R.id.imgProfileQR);
        AppUtils.setImage(imgQR, AppUtils.getAppPreference(context).getString(AppConstants.USER_PROFILE_QR, ""));
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
                mainLayout.startAnimation(animFadein);
                imgQR.setVisibility(View.VISIBLE);
                tabs.setVisibility(View.VISIBLE);
                String apiResponse = (String) result;
                if (!apiResponse.equalsIgnoreCase(AppConstants.TIME_OUT)) {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    progressLoading.setVisibility(View.GONE);
                    scrollContainer.setVisibility(View.VISIBLE);

                    dbAdapter.open();
                    dbAdapter.addOfflineData(AppConstants.OF_WALLET, (String) result);
                    dbAdapter.close();

                    if (jsonResponse.getInt("success") == 1) {
                        List<WalletModel> walletModelList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("wallet").toString(), new TypeReference<List<WalletModel>>() {
                        });

                        String remainingPoints = jsonResponse.getString("points");

                        walletAdapter = new WalletAdapter(context, ((AppCompatActivity) context).getSupportFragmentManager(), walletModelList, remainingPoints);
                        viewPager.setAdapter(walletAdapter);
                        viewPager.setOffscreenPageLimit(walletModelList.size());

                        tabs.setViewPager(viewPager);
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else {
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

                        walletAdapter = new WalletAdapter(context, ((AppCompatActivity) context).getSupportFragmentManager(), walletModelList, remainingPoints);
                        viewPager.setAdapter(walletAdapter);
                        viewPager.setOffscreenPageLimit(walletModelList.size());

                        tabs.setViewPager(viewPager);
                    } else {
                        scrollContainer.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setText(getString(R.string.no_data_error));
                    }
                }
            }
        });
    }
}
