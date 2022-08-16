package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import javax.annotation.Nullable;


import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppBase;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.widget.RelativeLayoutButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Admin on 2/19/2016.
 */
public class GiftAppinessFragment extends Fragment {

    private Context context;
    private View rootView;
    private LinearLayout scrollContainer;
    private ProgressBar progressLoad;
    private RelativeLayout mainLayout;
    Animation animFadein;
    private EditText etPoint, etPhone, etDesc;
    private RelativeLayoutButton btnShare;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_gift_appiness, container, false);

        progressLoad = (ProgressBar) rootView.findViewById(R.id.progressLoad);
        scrollContainer = (LinearLayout) rootView.findViewById(R.id.scrollContainer);
        mainLayout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        mainLayout.startAnimation(animFadein);


        progressLoad.setVisibility(View.GONE);
        scrollContainer.setVisibility(View.VISIBLE);

        etPoint = (EditText) rootView.findViewById(R.id.etPoint);
        etPhone = (EditText) rootView.findViewById(R.id.etPhone);
        etDesc = (EditText) rootView.findViewById(R.id.etDesc);

        btnShare = (RelativeLayoutButton) rootView.findViewById(R.id.btnShare);
        btnShare.imgIcon.setImageResource(R.drawable.ic_white_submit);
        btnShare.btnText.setText(getString(R.string.btn_share_point));

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePoint();
            }
        });


        return rootView;
    }

    private void sharePoint() {

        if (etPoint.getText().length() == 0) {
            AppUtils.showError(etPoint, getString(R.string.empty_point));
        } else if (etPhone.getText().length() == 0) {
            AppUtils.showError(etPhone, getString(R.string.empty_phone_number));
        } else {
            showPasswordDialog();
        }
    }

    public void showPasswordDialog() {
        final AlertDialog b;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.password_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        final EditText etPassword = (EditText) dialogView.findViewById(R.id.etPassword);
        final TextView etTitle = (TextView) dialogView.findViewById(R.id.etTitle);
        etTitle.setText(getString(R.string.password_share,AppUtils.getAppPreference(context).getString(AppConstants.USER_NAME, "")));
        dialogBuilder.setTitle(getString(R.string.app_name));

        dialogBuilder.setPositiveButton(R.string.txt_ok, null);
        dialogBuilder.setNegativeButton(R.string.al_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        b = dialogBuilder.create();
        b.show();

        b.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPassword.getText().length() == 0) {
                    AppUtils.showError(etPassword, getString(R.string.empty_password));

                } else {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

                    try {
                        AppController.loginUser(context, AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, ""), etPassword.getText().toString(), new Callback() {
                            @Override
                            public void run(Object result) throws JSONException, IOException {
                                JSONObject jsonResponse = new JSONObject((String) result);
                                if (jsonResponse != null) {
                                    if (jsonResponse.getInt("success") == 0)
                                        AppUtils.showToastMessage(context, getString(R.string.msg_login_user_not_exist));

                                    if (jsonResponse.getInt("success") == 2)
                                        AppUtils.showToastMessage(context, getString(R.string.msg_login_not_match));

                                    if (jsonResponse.getInt("success") == 3)
                                        AppUtils.showToastMessage(context, getString(R.string.msg_login_user_inactive));

                                    if (jsonResponse.getInt("success") == 1) {
                                        JSONObject jsonUser = jsonResponse.getJSONObject("user");

                                        SharedPreferences.Editor editor = AppUtils.getAppPreference(context).edit();
                                        editor.putString(AppConstants.USER_CASEID, jsonUser.getString(new AppBase().getHeartCase()));
                                        editor.apply();

                                        b.dismiss();

                                        CallShareApi();
                                    }
                                    if (jsonResponse.getInt("success") == 100) {
                                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                    }
                                    if (jsonResponse.getInt("success") == 99) {
                                        displayDialog(jsonResponse.getString("message"));
                                    }
                                    b.dismiss();
                                }
                            }
                        });
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

            }
        });
    }

    private void CallShareApi() {

        try {

            AppController.sharepoint(context, AppUtils.getAppPreference(context).getString(AppConstants.USER_EMAIL_ADDRESS, "")
                    ,String.valueOf(AppUtils.getAppPreference(context).getInt(AppConstants.USER_COUNTRY, -1))
                    ,etPoint.getText().toString(), etPhone.getText().toString()
                    ,"1",
                    etDesc.getText().toString(), new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);

                    if (jsonResponse != null) {
                        if (jsonResponse.getInt("success") == 1) {
                            etPoint.setText("");
                            etPhone.setText("");
                            etDesc.setText("");
                        }
                        else if (jsonResponse.getInt("success") == 0) {
                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));

//                            displayDialog(jsonResponse.getString("message"));

                        }
                        else if (jsonResponse.getInt("success") == 99) {
                            //AppUtils.showToastMessage(context, jsonResponse.getString("message"));

                            displayDialog(jsonResponse.getString("message"));

                        } else {
                            AppUtils.showToastMessage(context, getString(R.string.system_error));
                        }

//                        if (jsonResponse.getInt("success") == 0)
//                            AppUtils.showToastMessage(context, getString(R.string.msg_login_user_not_exist));
//
//                        if (jsonResponse.getInt("success") == 2)
//                            AppUtils.showToastMessage(context, getString(R.string.msg_login_not_match));
//
//                        if (jsonResponse.getInt("success") == 3)
//                            AppUtils.showToastMessage(context, getString(R.string.msg_login_user_inactive));
//
//                        if (jsonResponse.getInt("success") == 1) {
//
//                            CallShareApi();
//                        }
//                        if (jsonResponse.getInt("success") == 100) {
//                            AppUtils.showToastMessage(context, jsonResponse.getString("message"));
//                        }
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void displayDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, RegisterActivity.class));
                        ((Activity) context).finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.show();
    }



}
