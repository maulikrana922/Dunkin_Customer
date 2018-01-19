package com.dunkin.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.GPSTracker;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by qtm-c-android on 29/6/17.
 */

public class AddPromocodeActivity extends BaseActivity implements View.OnClickListener {

    private EditText edPromoCode;
    private LinearLayout mianLayout;
    Animation animFadein;
    GPSTracker gps;
    private double latitude, longitude;
    private String lat, log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateView(R.layout.activity_add_promo_code, "PROMOCODE");
        initializeViews();
        gps = new GPSTracker(AddPromocodeActivity.this);
    }

    private void initializeViews() {
        edPromoCode = (EditText) findViewById(R.id.edPromoCode);
        RelativeLayoutButton btnSubmit = (RelativeLayoutButton) findViewById(R.id.btnSubmit);
        mianLayout = (LinearLayout) findViewById(R.id.mianLayout);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        mianLayout.startAnimation(animFadein);
        btnSubmit.btnText.setText(getString(R.string.btn_submit));
        btnSubmit.imgIcon.setImageResource(R.drawable.ic_white_submit);
        btnSubmit.setOnClickListener(AddPromocodeActivity.this);

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams)btnSubmit.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        btnSubmit.setLayoutParams(layoutParams);
        btnSubmit.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
       if (edPromoCode.getText().length() == 0) {
            AppUtils.showError(edPromoCode, "Please Enter Promo Code");
        }else {
           String code = edPromoCode.getText().toString();
           latitude = gps.getLatitude();
           longitude = gps.getLongitude();
           lat = String.valueOf(latitude);
           log = String.valueOf(longitude);
           try {
               AppController.redeemPromoCode(AddPromocodeActivity.this, code,
                       AppUtils.getAppPreference(AddPromocodeActivity.this).getInt(AppConstants.USER_COUNTRY, -1),
                       AppUtils.getAppPreference(AddPromocodeActivity.this).getString(AppConstants.USER_EMAIL_ADDRESS, ""),
                       lat, log, new Callback() {
                           @Override
                           public void run(Object result) throws JSONException, IOException {
                               JSONObject jsonResponse = new JSONObject((String) result);
                               if (jsonResponse.getInt("success") == 1) {
                                   AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddPromocodeActivity.this);

                                   // Setting Dialog Message
                                   alertDialog.setMessage(jsonResponse.getString("message"));

                                   // On pressing Settings button
                                   alertDialog.setPositiveButton("View", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.cancel();
                                           finish();
                                           Intent i = new Intent(AddPromocodeActivity.this, HomeActivity.class);
                                           i.putExtra("navigateflag", AppConstants.MENU_NOTIFICATIONS);
                                           i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                           startActivity(i);
                                       }
                                   });

                                   alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.cancel();
                                           finish();
                                       }
                                   });
                                   // Showing Alert Message
                                   alertDialog.show();
                               } else if (jsonResponse.getInt("success") == 0) {
                                   AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddPromocodeActivity.this);

                                   // Setting Dialog Message
                                   alertDialog.setMessage(jsonResponse.getString("message"));

                                   // On pressing Settings button
                                   alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.cancel();
                                       }
                                   });
                                   // Showing Alert Message
                                   alertDialog.show();
                               } else {
                                   if(jsonResponse.getInt("success") != 99) {
                                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddPromocodeActivity.this);

                                       // Setting Dialog Message
                                       alertDialog.setMessage(getString(R.string.system_error));

                                       // On pressing Settings button
                                       alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int which) {
                                               dialog.cancel();
                                           }
                                       });
                                       // Showing Alert Message
                                       alertDialog.show();
                                   }
                               }
                           }
                       });
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
    }
}

