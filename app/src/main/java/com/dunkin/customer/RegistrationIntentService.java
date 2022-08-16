package com.dunkin.customer;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.constants.URLConstant;
import com.dunkin.customer.controllers.AppController;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "GCMIntentService";
    SharedPreferences myPrefs;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
//            String token = instanceID.getToken(AppConstants.GCM_SENDER_ID,
//                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            String token = FirebaseMessaging.getInstance().getToken();
//            myPrefs.edit().putString(AppConstants.GCM_TOKEN_ID, token).apply();

//            Dunkin_Log.d("FCMToken Id : ", token);

            myPrefs = AppUtils.getAppPreference(this);
            String token  = myPrefs.getString(AppConstants.GCM_TOKEN_ID,"");

            if (AppUtils.checkNetwork(RegistrationIntentService.this) != 0) {
                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("email", AppUtils.getAppPreference(RegistrationIntentService.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""));
                    jsonRequest.put("udid", token);
                    jsonRequest.put("type", 1);
                    jsonRequest.put("lang_flag", AppUtils.getAppPreference(RegistrationIntentService.this).getString(AppConstants.USER_LANGUAGE, AppConstants.LANG_EN));

                    Dunkin_Log.d("Request for update udid", jsonRequest.toString());
//                    Toast.makeText(this, "Api = "+ URLConstant.UPDATE_UDID_URL, Toast.LENGTH_SHORT).show();

                    AppController.updateUDIDForUser(jsonRequest.toString(), new Callback() {
                        @Override
                        public void run(Object result) throws JSONException, IOException {
                            JSONObject jsonResponse = new JSONObject((String) result);
                            Dunkin_Log.d("Updated UDID", jsonResponse.getString("message"));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Dunkin_Log.d("GCMToken Id : ", "Error");
        }
    }
}
