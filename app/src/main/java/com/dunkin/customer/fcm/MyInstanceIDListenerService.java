package com.dunkin.customer.fcm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dunkin.customer.RegistrationIntentService;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;
import com.google.android.gms.iid.InstanceIDListenerService;



public class MyInstanceIDListenerService{}
//extends FirebaseInstanceIdService {
//
//    private String TAG = "MyFirebaseInstanceIDService";
//    private SharedPreferences myPrefs;
//
//    @Override
//    public void onTokenRefresh() {
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        myPrefs = AppUtils.getAppPreference(this);
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//        // TODO: Implement this method to send any registration to your app's servers.
//        sendRegistrationToServer(refreshedToken);
//        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
//        Intent i = new Intent(this, RegistrationIntentService.class);
//        startService(i);
//    }
//
//    private void sendRegistrationToServer(String token) {
//        // Add custom implementation, as needed.
//        myPrefs.edit().putString(AppConstants.GCM_TOKEN_ID, token).apply();
//    }
//}
