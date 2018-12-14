package com.dunkin.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.dunkin.customer.DBAdaters.DBAdapter;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.Dunkin_Log;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.CountriesModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class SplashActivity extends AppCompatActivity {

    ImageView imgView;
    SharedPreferences myPrefs;
    DBAdapter db;
    boolean isFromGCM = false;
    int msgType;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE};
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        AppConstants.activity = this;
        AppConstants.context = this;

        if (getIntent().hasExtra("FromGCM")) {
            isFromGCM = getIntent().getBooleanExtra("FromGCM", false);
        } else {
            isFromGCM = false;
        }

        if (getIntent().hasExtra("MsgType")) {
            msgType = getIntent().getIntExtra("MsgType", 0);
            Dunkin_Log.e("msgtype", "" + msgType);
        }

        imgView = (ImageView) findViewById(R.id.imgView);

        myPrefs = AppUtils.getAppPreference(this);

        db = new DBAdapter(this);
        try {
            findViewById(R.id.progressLoad).setVisibility(View.GONE);
            //setImage("drawable://" + R.drawable.splash_screen);
            loadCountries();
            getGcmToken();
            getAppPermissions();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getGcmToken() {
        if (myPrefs.getString(AppConstants.GCM_TOKEN_ID, null) == null) {
            Intent intent = new Intent(SplashActivity.this, RegistrationIntentService.class);
            startService(intent);
        } else {
            Dunkin_Log.i("GCMToken Id : ", myPrefs.getString(AppConstants.GCM_TOKEN_ID, ""));
        }
    }

    private void setImage(String URL) {
        if (URL != null && !URL.isEmpty()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoader.getInstance().displayImage(URL, imgView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    try {
                        imgView.setImageBitmap(loadedImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    private void loadCountries() throws UnsupportedEncodingException {

        AppController.getCountryList(SplashActivity.this, new Callback() {
            @Override
            public void run(Object result) throws JSONException, IOException {
                JSONObject jsonResponse = new JSONObject((String) result);
                //Dunkin_Log.i("DataResponse", jsonResponse.toString());
                if (jsonResponse != null && jsonResponse.getInt("success") == 1) {
                    List<CountriesModel> countryListModel = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("countryList").toString(), new TypeReference<List<CountriesModel>>() {
                    });

                    if (countryListModel != null && countryListModel.size() > 0) {

                        db.open();
                        db.addCountries(countryListModel);
                        db.close();

                    } else {
                        AppUtils.showToastMessage(SplashActivity.this, getString(R.string.system_error));
                    }
                }
                else if (jsonResponse.getInt("success") == 100) {
                    AppUtils.showToastMessage(getApplicationContext(), jsonResponse.getString("message"));
                }
            }
        });
    }

    private void getAppPermissions() {
        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED|| ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, 101);

        } else {
            redirectToScreen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101 && AppUtils.verifyPermissions(grantResults)) {
            redirectToScreen();
        } else {
            getAppPermissions();
        }
    }

    private void redirectToScreen() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (myPrefs.getString(AppConstants.USER_EMAIL_ADDRESS, null) != null) {

                    Intent i = new Intent(SplashActivity.this, NewHomeActivity.class);
                    i.putExtra("FromGCM", isFromGCM);
                    i.putExtra("MsgType", msgType);
                    startActivity(i);
                    finish();

                } else {
                    startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
                    finish();
                }
            }
        }, 2000);

    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
