package com.dunkin.customer;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.fragments.RotatingBannerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class BackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppConstants.activity = this;
        AppConstants.context = this;
        setContentView(R.layout.activity_back);
    }

    void inflateView(int layout, String title) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        if (toolBar != null) {
            toolBar.setTitleTextColor(ContextCompat.getColor(BackActivity.this, android.R.color.white));
            setSupportActionBar(toolBar);
            if (title != null && title.length() > 0) {
                toolBar.findViewById(R.id.brandLogo).setVisibility(View.GONE);
                TextView tv = (TextView) toolBar.findViewById(R.id.toolbar_title);
                tv.setText(title);
                tv.setVisibility(View.VISIBLE);
            } else {
                toolBar.findViewById(R.id.brandLogo).setVisibility(View.VISIBLE);
                toolBar.findViewById(R.id.toolbar_title).setVisibility(View.GONE);
            }
        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(BackActivity.this, R.drawable.ic_nav_back));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FrameLayout viewContainer = (FrameLayout) findViewById(R.id.container);
        ViewGroup.inflate(BackActivity.this, layout, viewContainer);

        try {

            final FrameLayout advertisement_banner = (FrameLayout) findViewById(R.id.advertisement_banner);
            if (advertisement_banner != null) {
                advertisement_banner.setVisibility(View.GONE);

                AppController.getRotatingBanner(BackActivity.this, AppUtils.getAppPreference(BackActivity.this).getInt(AppConstants.USER_COUNTRY, -1), new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
                        JSONObject jsonResponse = new JSONObject((String) result);

                        if (jsonResponse.getInt("success") == 1) {
                            advertisement_banner.setVisibility(View.VISIBLE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.advertisement_banner, RotatingBannerFragment.newInstance()).commit();
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                        } else {
                            advertisement_banner.setVisibility(View.GONE);
                        }
                    }
                });
            }
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
