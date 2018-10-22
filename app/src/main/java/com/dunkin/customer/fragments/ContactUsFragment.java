package com.dunkin.customer.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dunkin.customer.NewHomeActivity;
import com.dunkin.customer.R;
import com.dunkin.customer.RegisterActivity;
import com.dunkin.customer.Utils.AppUtils;
import com.dunkin.customer.Utils.Callback;
import com.dunkin.customer.constants.AppConstants;
import com.dunkin.customer.controllers.AppController;
import com.dunkin.customer.models.SocialLinks;
import com.dunkin.customer.widget.RelativeLayoutButton;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ContactUsFragment extends Fragment implements View.OnClickListener {

    private static List<SocialLinks> socialLinksList;
    RelativeLayoutButton btnSubmit;
    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((String) v.getTag())));
        }
    };
    private Context context;
    private EditText edName, edEmail, edNumber, edDescription;
    private LinearLayout socialLinksLayouts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);

        // ADDED COMMENT
        edName = (EditText) rootView.findViewById(R.id.edName);
        edDescription = (EditText) rootView.findViewById(R.id.edDescription);
        edEmail = (EditText) rootView.findViewById(R.id.edEmail);
        edNumber = (EditText) rootView.findViewById(R.id.edPhoneNumber);
        socialLinksLayouts = (LinearLayout) rootView.findViewById(R.id.socialLinksLayouts);
        btnSubmit = (RelativeLayoutButton) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.btnText.setText(getString(R.string.btn_submit));
        btnSubmit.imgIcon.setImageResource(R.drawable.ic_white_submit);
        socialLinksList = new ArrayList<>();

        SharedPreferences prefs = AppUtils.getAppPreference(context);

        edName.setText(prefs.getString(AppConstants.USER_FIRST_NAME, ""));
        edNumber.setText(prefs.getString(AppConstants.USER_PHONE, ""));
        edEmail.setText(prefs.getString(AppConstants.USER_EMAIL_ADDRESS, ""));

        try {
            AppController.getSocialUrls(context, new Callback() {
                @Override
                public void run(Object result) throws JSONException, IOException {
                    JSONObject jsonResponse = new JSONObject((String) result);
                    if (jsonResponse.getInt("success") == 1) {
                        socialLinksList = AppUtils.getJsonMapper().readValue(jsonResponse.getJSONArray("sociallinks").toString(), new TypeReference<List<SocialLinks>>() {
                        });
                        if (socialLinksList != null && socialLinksList.size() > 0) {
                            addLinksInLayouts();
                        }
                    }else if (jsonResponse.getInt("success") == 100) {
                        AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                    }else if(jsonResponse.getInt("success") == 99) {
                        displayDialog(jsonResponse.getString("message"));
                    }
                }
            });
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /*socialLinksList.add(new SocialLinks("FACEBOOK","https://www.facebook.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        socialLinksList.add( new SocialLinks("TWITER","https://www.twitter.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        socialLinksList.add(new SocialLinks("GOOGLE+","https://www.google.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        socialLinksList.add(new SocialLinks("INSTAGRAM","https://www.instagram.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        socialLinksList.add( new SocialLinks("SKYEP","https://www.skype.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        socialLinksList.add( new SocialLinks("LINKED IN","https://www.linkedin.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        socialLinksList.add(new SocialLinks("YOUTUBE","https://www.youtube.com/","http://122.170.114.92:929/Open_table_doc/fbicon.png"));
        */
        btnSubmit.setOnClickListener(this);

        return rootView;
    }

    public void addLinksInLayouts() {
        for (int i = 0; i < socialLinksList.size(); i++) {
            ImageView btn = new ImageView(context);
            // btn.setText(socialLinksList.get(i).getTitle());
            btn.setAdjustViewBounds(true);
            btn.setPadding(10, 10, 10, 10);

            AppUtils.setImage(btn, socialLinksList.get(i).getIcon());

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, 150, (float) 0.25);
            btn.setLayoutParams(params2);

            btn.setTag(socialLinksList.get(i).getUrl());
            btn.setOnClickListener(btnClickListener);

            if (i % 4 == 0) {
                LinearLayout li = new LinearLayout(context);
              //  li.setWeightSum(1);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                li.setLayoutParams(params1);
                li.addView(btn);
                socialLinksLayouts.addView(li);
            } else {
                int count = socialLinksLayouts.getChildCount();
                if (count > 0) {
                    LinearLayout li = (LinearLayout) socialLinksLayouts.getChildAt(count - 1);
                    li.addView(btn);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (edName.getText().length() == 0) {
                AppUtils.showError(edName, getString(R.string.empty_name));
            } else if (edEmail.getText().length() == 0) {
                AppUtils.showError(edEmail, getString(R.string.empty_email));
            } else if (!Pattern.matches(String.valueOf(Patterns.EMAIL_ADDRESS), edEmail.getText().toString())) {
                AppUtils.showError(edEmail, getString(R.string.invalid_email));
            } else if (edNumber.getText().length() == 0) {
                AppUtils.showError(edNumber, getString(R.string.empty_phone_number));
            } else if (edNumber.getText().length() < 8 || edNumber.getText().length() > 15) {
                AppUtils.showError(edNumber, getString(R.string.min_length_phone_number));
            } else if (edDescription.getText().length() == 0) {
                AppUtils.showError(edDescription, getString(R.string.empty_description));
            } else {
                AppController.postContactUs(
                        context,
                        // Email
                        edEmail.getText().toString(),
                        // Contact Number
                        edNumber.getText().toString(),
                        // Name
                        edName.getText().toString(),
                        // Description
                        edDescription.getText().toString(),
                        new Callback() {

                            @Override
                            public void run(Object result) throws JSONException, IOException {
                                JSONObject jsonResponse = new JSONObject((String) result);
                                if (jsonResponse.getInt("success") == 1) {
                                    AppUtils.showToastMessage(context, getString(R.string.txt_contact_success));
                                    ((NewHomeActivity) getActivity()).addHomeFragment();
                                } else if (jsonResponse.getInt("success") == 100) {
                                    AppUtils.showToastMessage(context, jsonResponse.getString("message"));
                                }else if (jsonResponse.getInt("success") == 99) {
                                    displayDialog(jsonResponse.getString("message"));
                                }else {
                                    AppUtils.showToastMessage(context, getString(R.string.txt_contact_failure));
                                }
                            }
                        });
            }
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
