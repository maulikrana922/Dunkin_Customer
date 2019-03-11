package com.dunkin.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.constants.AppConstants;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;


public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent i = new Intent(this, RegistrationIntentService.class);
        startService(i);
    }
}
