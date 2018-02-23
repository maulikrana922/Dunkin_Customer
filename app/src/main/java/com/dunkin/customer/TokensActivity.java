package com.dunkin.customer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.adapters.TokenAdapter;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.TokenModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.dunkin.customer.constants.AppConstants.context;

/**
 * Created by qtm-c-android on 2/6/17.
 */

public class TokensActivity extends BaseActivity {

//    SmartTabLayout tabs;
    ViewPager viewPager;
    TabLayout tabs;
    private TokenAdapter tokenAdapter;
    private ProgressBar progressLoading;
    private String promo_id;
    private String winnerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateView(R.layout.activity_token, getString(R.string.token));

        promo_id = getIntent().getStringExtra("promo_id");

        initializeViews();
    }

    private void initializeViews() {
        progressLoading = (ProgressBar) findViewById(R.id.progressLoad);
        tabs = (TabLayout) findViewById(R.id.tabs);
//        tabs = (SmartTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        try {
            getDataFromAPI();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getDataFromAPI() throws UnsupportedEncodingException {
        AppController.fetchMyPromoTicket(context, promo_id, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                String apiResponse = (String) result;
                if (!apiResponse.equalsIgnoreCase(AppConstants.TIME_OUT)) {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    progressLoading.setVisibility(View.GONE);

                    if (jsonResponse.getInt("success") == 1) {
                        List<TokenModel> tokenModelList = AppUtils.getJsonMapper().
                                readValue(jsonResponse.getJSONArray("ticketList").toString(),
                                        new TypeReference<List<TokenModel>>() {
                                        });

                        List<TokenModel> winnerModelList = AppUtils.getJsonMapper().
                                readValue(jsonResponse.getJSONArray("winnerList").toString(),
                                        new TypeReference<List<TokenModel>>() {
                                        });

                        winnerImage = jsonResponse.getString("winnerImage");

                        tokenAdapter = new TokenAdapter(TokensActivity.this,
                                ((AppCompatActivity) context).getSupportFragmentManager(),
                                tokenModelList, winnerImage);
                        viewPager.setAdapter(tokenAdapter);
                        viewPager.setOffscreenPageLimit(tokenModelList.size());

//                        tabs.setViewPager(viewPager);
                        tabs.setupWithViewPager(viewPager);
                    } else if (jsonResponse.getInt("success") == 0) {
                        List<TokenModel> tokenModelList = AppUtils.getJsonMapper().
                                readValue(jsonResponse.getJSONArray("ticketList").toString(),
                                        new TypeReference<List<TokenModel>>() {
                                        });

                        List<TokenModel> winnerModelList = AppUtils.getJsonMapper().
                                readValue(jsonResponse.getJSONArray("winnerList").toString(),
                                        new TypeReference<List<TokenModel>>() {
                                        });

                        winnerImage = jsonResponse.getString("winnerImage");

                        tokenAdapter = new TokenAdapter(TokensActivity.this,
                                ((AppCompatActivity) context).getSupportFragmentManager(),
                                tokenModelList, winnerImage);
                        viewPager.setAdapter(tokenAdapter);
                        viewPager.setOffscreenPageLimit(tokenModelList.size());

                        tabs.setupWithViewPager(viewPager);
//                        tabs.setViewPager(viewPager);
                    } else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    } else {
                    }
                }
            }
        });
    }
}
