package com.dunkin.customer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dunkin.customer.CustomerApplication;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class LogoutService extends Service {

    private Context context;
    private int isDirectExit = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Dunkin_Log.e("Service", "Service Created");
        context = LogoutService.this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dunkin_Log.e("Service", "onStartCommand");
        getDataFromIntent(intent);
        if (AppUtils.checkNetwork(context) != 0) {
            try {
                AppController.logoutUser(context, isDirectExit, new Callback() {
                    @Override
                    public void run(Object result) throws JSONException, IOException {
                        JSONObject jsonResponse = new JSONObject((String) result);
                        if (jsonResponse.getInt("success") == 1) {
                            AppUtils.getAppPreference(context).edit().clear().apply();
                            CustomerApplication.setLocale(new Locale(AppConstants.LANG_EN));
                            Intent intent = new Intent(context, RegisterActivity.class);
                            //Clear all activities and start new task
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            stopSelf();
                        } else if (jsonResponse.getInt("success") == 100) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                        }
                    }
                });
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return START_NOT_STICKY;
    }

    private void getDataFromIntent(Intent intent) {
        if (intent.hasExtra("isDirectExit")) {
            isDirectExit = intent.getIntExtra("isDirectExit", 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Dunkin_Log.e("Service", "Service Destroyed");
    }
}
