package com.dunkin.customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dunkin.customer.fragments.LoginFragment;
import com.dunkin.customer.fragments.RegisterFragment;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;


public class RegisterActivity extends AppCompatActivity {

    LinearLayout llLoginSignUp;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        llLoginSignUp = (LinearLayout) findViewById(R.id.llLoginSignUp);

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

        (findViewById(R.id.ivFBLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbLogin();
            }
        });

        if (getIntent() != null && getIntent().hasExtra("isRegister")) {
            if (getIntent().getBooleanExtra("isRegister", false)) {
                llLoginSignUp.setVisibility(View.GONE);
                ((FrameLayout) findViewById(R.id.frame)).setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
            }
        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
    }

    private void fbLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String fbId = object.getString("id");
//                                    if (object.has("first_name"))
//                                        firstName = object.getString("first_name");
//                                    if (object.has("last_name"))
//                                        lastName = object.getString("last_name");
//                                    if (object.has("email"))
//                                        email = object.getString("email");
//                                    if (object.has("birthday"))
//                                        dateOfBirth = object.getString("birthday");
//                                    if (object.has("gender"))
//                                        gender = object.getString("gender");
//                                    if (currentLatLng != null) {
//                                        socialLoginPresenter.socialLogin(1, firstName + lastName, firstName, lastName, email, gender, dateOfBirth, currentLatLng.latitude, currentLatLng.longitude, fbId, "");
//                                    } else {
//                                        Toast.makeText(LaunchActivity.this, getString(R.string.msg_location), Toast.LENGTH_LONG).show();
//                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        //Here we put the requested fields to be returned from the JSONObject
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        logoutFromFB();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("email, public_profile"));
    }

    private void logoutFromFB() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
