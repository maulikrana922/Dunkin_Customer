package com.dunkin.customer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppBase;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.fragments.LoginFragment;
import com.dunkin.customer.fragments.RegisterFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llLoginSignUp;
    TextView txtFBLogin;
    LoginButton facebookBtn;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        llLoginSignUp = (LinearLayout) findViewById(R.id.llLoginSignUp);
        txtFBLogin = findViewById(R.id.txtFBLogin);
        facebookBtn=findViewById(R.id.login_button);
        facebookBtn.setOnClickListener(this);
        txtFBLogin.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();

        llLoginSignUp.setVisibility(View.VISIBLE);
        ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.txtLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
            }
        });

        ((TextView) findViewById(R.id.txtSignUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new RegisterFragment()).commit();
            }
        });

        if (getIntent() != null && getIntent().hasExtra("isRegister")) {
            if (getIntent().getBooleanExtra("isRegister", false)) {
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
            }
        }
        if (getIntent() != null && getIntent().hasExtra("isFBRegister")) {
            if (getIntent().getBooleanExtra("isFBRegister", false)) {
                llLoginSignUp.setVisibility(View.VISIBLE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.GONE);
            }
        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtFBLogin:
                fbLogin();
                break;

        }
    }


    public void fbLogin() {
        LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        facebookBtn.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object, GraphResponse response) {
                                try {
                                    String facebook_id = object.getString("id");
                                    if (object.has("email"))
                                        object.getString("email");

                                    try {
                                        AppController.loginUserWithFB(RegisterActivity.this, object.getString("email"), "", facebook_id, new Callback() {
                                            @Override
                                            public void run(Object result) throws JSONException, IOException {
                                                JSONObject jsonResponse = new JSONObject((String) result);
                                                if (jsonResponse != null) {
                                                    if (jsonResponse.getInt("success") == 0)
                                                        AppUtils.showToastMessage(RegisterActivity.this, getString(R.string.msg_login_user_not_exist));

                                                    if (jsonResponse.getInt("success") == 2)
                                                        AppUtils.showToastMessage(RegisterActivity.this, getString(R.string.msg_login_not_match));

                                                    if (jsonResponse.getInt("success") == 3)
                                                        AppUtils.showToastMessage(RegisterActivity.this, getString(R.string.msg_login_user_inactive));

                                                    if (jsonResponse.getInt("success") == 1) {
                                                        JSONObject jsonUser = jsonResponse.getJSONObject("user");
                                                        SharedPreferences.Editor editor = AppUtils.getAppPreference(RegisterActivity.this).edit();
                                                        editor.putString(AppConstants.USER_EMAIL_ADDRESS, object.getString("email"));
                                                        editor.putInt(AppConstants.USER_COUNTRY, jsonUser.getInt("country_id"));
                                                        editor.putString(AppConstants.USER_ADDRESS, jsonUser.getString("address"));
                                                        editor.putString(AppConstants.USER_SHIPPING_ADDRESS, jsonUser.getString("shippingAddress"));
                                                        editor.putString(AppConstants.USER_FIRST_NAME, jsonUser.getString("firstName"));
                                                        editor.putString(AppConstants.USER_PROFILE_QR, jsonUser.getString("qrCode"));
                                                        editor.putString(AppConstants.USER_PHONE, jsonUser.getString("phone_number"));
                                                        editor.putString(AppConstants.USER_CASEID, jsonUser.getString(new AppBase().getHeartCase()));
                                                        editor.putString(AppConstants.USER_NAME, jsonUser.getString("firstName") + " " + jsonUser.getString("lastName"));
                                                        editor.apply();

                                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                        notificationManager.cancelAll();

                                                        startActivity(new Intent(RegisterActivity.this, NewHomeActivity.class));
                                                        finish();
                                                    }
                                                    if (jsonResponse.getInt("success") == 55) {
                                                        Bundle bundle = new Bundle();
                                                        if (object.has("id"))
                                                            bundle.putString(AppConstants.USER_FB_ID, object.getString("id"));
                                                        if (object.has("first_name"))
                                                            bundle.putString(AppConstants.USER_FIRST_NAME, object.getString("first_name"));
                                                        if (object.has("last_name"))
                                                            bundle.putString(AppConstants.USER_LAST_NAME, object.getString("last_name"));
                                                        if (object.has("email"))
                                                            bundle.putString(AppConstants.USER_EMAIL_ADDRESS, object.getString("email"));
                                                        if (object.has("birthday"))
                                                            bundle.putString(AppConstants.USER_DOB, object.getString("birthday"));

                                                        RegisterFragment registerFragment = new RegisterFragment();
                                                        registerFragment.setArguments(bundle);
                                                        llLoginSignUp.setVisibility(View.GONE);
                                                        ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);
                                                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, registerFragment).commit();
                                                    }
                                                    if (jsonResponse.getInt("success") == 100) {
                                                        AppUtils.showToastMessage(RegisterActivity.this, jsonResponse.getString("message"));
                                                    }
                                                }
                                            }
                                        });
                                    } catch (JSONException | UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
//                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        //Here we put the requested fields to be returned from the JSONObject
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("email, public_profile"));
        facebookBtn.performClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
