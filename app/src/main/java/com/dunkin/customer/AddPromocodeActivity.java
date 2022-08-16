package com.dunkin.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import javax.annotation.Nullable;


import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.Utils.GPSTracker;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.dialogs.ScanAndWinDialog;
import com.dunkin.customer.dialogs.ScanAndWinPromocodeDialog;
import com.dunkin.customer.dialogs.WinStatusDialog;
import com.dunkin.customer.fragments.NewHomeFragment;
import com.dunkin.customer.fragments.NotificationFragment;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import static com.dunkin.customer.NewHomeActivity.isOfferEnable;

/**
 * Created by qtm-c-android on 29/6/17.
 */

public class AddPromocodeActivity extends Fragment implements View.OnClickListener {

    private EditText edPromoCode;
    Animation animFadein;
    GPSTracker gps;
    private double latitude, longitude;
    private String lat, log;
    private String scanImage;
    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_add_promo_code, null);
        initializeViews(view);
        gps = new GPSTracker(context);
        return view;
    }

    private void initializeViews(View view) {
//        scanImage = getIntent().getStringExtra("scanImage");
        edPromoCode = (EditText) view.findViewById(R.id.edPromoCode);
        TextView btnSubmit =  view.findViewById(R.id.btnSubmit);
        TextView btnScanQRCode =  view.findViewById(R.id.btnScanQRCode);
        ImageView txtScan = (ImageView) view.findViewById(R.id.txtScan);

        animFadein = AnimationUtils.loadAnimation(context,
                R.anim.fade_in);
        btnSubmit.setOnClickListener(this);

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams)btnSubmit.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        btnSubmit.setLayoutParams(layoutParams);
        btnSubmit.setGravity(Gravity.CENTER);

//        if (!TextUtils.isEmpty(scanImage)) {
//            AppUtils.setImage(txtScan, scanImage);
//        }

        btnScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOfferEnable.equalsIgnoreCase("1"))
                    ScanAndWinPromocodeDialog.newInstance(context, NewHomeActivity.strOfferUrl, true).show();
            }
        });
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
               AppController.redeemPromoCode(context, code,
                       AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1),
                       AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""),
                       lat, log, new Callback() {
                           @Override
                           public void run(Object result) throws JSONException, IOException {
                               JSONObject jsonResponse = new JSONObject((String) result);
                               if (jsonResponse.getInt("success") == 1) {
                                   AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                                   // Setting Dialog Message
                                   alertDialog.setMessage(jsonResponse.getString("message"));

                                   // On pressing Settings button
                                   alertDialog.setPositiveButton("View", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.cancel();
                                           getChildFragmentManager().beginTransaction().replace(R.id.content, new NotificationFragment()).commitAllowingStateLoss();
                                       }
                                   });

                                   alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.cancel();
                                       }
                                   });
                                   // Showing Alert Message
                                   alertDialog.show();
                               } else if (jsonResponse.getInt("success") == 0) {
                                   AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

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
                                   if(jsonResponse.getInt("success") == 99) {
                                       AppUtils.showToastMessage(context, getString(R.string.system_error));
                                       SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
                                       String gcm = AppUtils.getAppPreference(context).getString(AppConstants.GCM_TOKEN_ID, "");
                                       editor.clear();
                                       editor.putString(AppConstants.GCM_TOKEN_ID, gcm);
                                       editor.apply();
                                       CustomerApplication.setLocale(new Locale(AppConstants.LANG_EN));
                                       getActivity().finish();
                                       startActivity(new Intent(context, RegisterActivity.class));
                                   }else{
                                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

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

