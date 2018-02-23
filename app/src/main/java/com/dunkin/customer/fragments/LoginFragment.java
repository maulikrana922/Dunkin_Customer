package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
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
import java.util.regex.Pattern;

/**
 * Created by Admin on 8/20/2015.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Context context;

    private EditText edEmail, edPassword;
    private TextView txtForgotPassword;
    private RelativeLayoutButton btnSubmit, btnSignup;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initializeViews();
        return rootView;
    }

    private void initializeViews() {
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edPassword = (EditText) rootView.findViewById(R.id.edPassword);

        btnSubmit = (RelativeLayoutButton) rootView.findViewById(R.id.btnLogin);
        btnSignup = (RelativeLayoutButton) rootView.findViewById(R.id.btnSignup);

        btnSubmit.imgIcon.setImageResource(R.drawable.ic_btn_login);
        btnSubmit.btnText.setText(getString(R.string.btn_signin));

        btnSignup.imgIcon.setImageResource(R.drawable.ic_btn_signup);
        btnSignup.btnText.setText(getString(R.string.btn_signup));

        txtForgotPassword = (TextView) rootView.findViewById(R.id.txtForgotPass);

        btnSubmit.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == txtForgotPassword) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ForgotPasswordFragment()).commit();
        }

        if (v == btnSignup) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new RegisterFragment()).commit();
        }

        if (v == btnSubmit) {
            if (edEmail.getText().length() == 0) {
                AppUtils.showError(edEmail, getString(R.string.empty_email));
            } else if (!Pattern.matches(String.valueOf(Patterns.EMAIL_ADDRESS), edEmail.getText().toString())) {
                AppUtils.showError(edEmail, getString(R.string.invalid_email));
            } else if (edPassword.getText().length() == 0) {
                AppUtils.showError(edPassword, getString(R.string.empty_password));
            } else if (edPassword.getText().length() < 6) {
                AppUtils.showError(edPassword, getString(R.string.min_length_password));
            } else {
                try {
                    AppController.loginUser(context, edEmail.getText().toString(), edPassword.getText().toString(), new Callback() {
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
                                    editor.putString(AppConstants.USER_EMAIL_ADDRESS, edEmail.getText().toString());
                                    editor.putInt(AppConstants.USER_COUNTRY, jsonUser.getInt("country_id"));
                                    editor.putString(AppConstants.USER_ADDRESS, jsonUser.getString("address"));
                                    editor.putString(AppConstants.USER_SHIPPING_ADDRESS, jsonUser.getString("shippingAddress"));
                                    editor.putString(AppConstants.USER_FIRST_NAME, jsonUser.getString("firstName"));
                                    editor.putString(AppConstants.USER_PROFILE_QR, jsonUser.getString("qrCode"));
                                    editor.putString(AppConstants.USER_PHONE, jsonUser.getString("phone_number"));
                                    editor.putString(AppConstants.USER_CASEID, jsonUser.getString(new AppBase().getHeartCase()));
                                    editor.putString(AppConstants.USER_NAME,  jsonUser.getString("firstName") + " " +  jsonUser.getString("lastName"));
                                    editor.apply();

                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();

                                    startActivity(new Intent(context, NewHomeActivity.class));
                                    ((Activity) context).finish();
                                }
                                if (jsonResponse.getInt("success") == 100) {
                                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                }
                            }
                        }
                    });
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
